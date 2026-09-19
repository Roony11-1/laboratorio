### Laboratorio de asincronía Java Spring Boot

# ¿Qué se busca?

Conocer como ejecutar tareas fuera del hilo principal en el pedidos hacia el servidor de Spring Boot.

En este servicio, se tiene el modelo base de Job.java

```java

package io.github.roony11_1.async_laboratory.model;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class Job 
{
    private UUID id = UUID.randomUUID();
    private Instant startedAt;
    private Instant finishedAt;
    private State state = State.PENDING;
    private String threadName;

    public UUID getId()
    {
        return this.id;
    }

    public void setRunning(Instant startedAt)
    {
        this.startedAt = startedAt;
        this.state = State.RUNNING;
    }

    public Instant getStartedAt()
    {
        return this.startedAt;
    }

    public void setCompleted(Instant finishedAt)
    {
        this.finishedAt = finishedAt;
        this.state = State.COMPLETED;
    }

    public void setFailed(Instant finishedAt)
    {
        this.finishedAt = finishedAt;
        this.state = State.FAILED;
    }

    public Instant getFinishedAt()
    {
        return this.finishedAt;
    }

    public State getState()
    {
        return this.state;
    }

    public void setState(State estado)
    {
        this.state = estado;
    }

    public String getThreadName()
    {
        return this.threadName;
    }

    public void setThreadName(String threadName)
    {
        this.threadName = threadName;
    }

    public Long getTimeRunning()
    {
        if (finishedAt == null)
            return null;

        return Duration.between(startedAt, finishedAt).toSeconds();
    }

    public enum State
    {
        PENDING,
        RUNNING,
        COMPLETED,
        FAILED
    }
}


```

Conceptualmente representa un "proceso" que se mando a ejecutar en el servidor, 
representa que esta pendiente, corriendo y completado o fallido, además de metadatos; marcas de tiempo y el nombre del hilo a ejecutar.

### ¿Qué se esta explorando?

Se explora la diferencia en peticiones sincronas y asíncronas en java, vemos conceptos como Thread, es la unidad de ejecución en la que se procesa
el código de una petición, como mandar su ejecucion a un hilo secundario, en que se diferencia. TaskExecutor una interfaz que permite correr funciones.
ThreadPoolTaskExecutor una implementación concreta de la interfaz anterior, permite manejar un Pool de hilos, configurandolo y usarlo.
Vimos la anotación del ecosistema @Async, permite a Spring delegar la ejecución del método a un TaskExecutor generando su proxie a tráves de un Bean válido.

Los estados que puede estar el job es para identificar en que momento va, junto al metodo HTTP 202 aceptado, significa que el servidor recibio
tu petición y la procesara, la comunicación se corta en esa respuesta y el servidor se encargara de terminar lo que se necesite. Como
encontremos el recurso alli nace el header Location, que se puede devolver un URI con el findBy... necesario para encontrar solo ese recurso.

además de configurar el polling especifico para el laboratorio, vimos que si falla en un hilo secundario no le avisara a nadie tal asunto en
una comunicación rest tradicional.

Tenemos el siguiente flujo

# JobController
Tiene métodos para crear un Job
## doJob
Conecta directamente al modelo sincrono bloqueante creas un Job, esperas su ejecución y recibes su respuesta.
## doJobThread y doJobThreadPool
Son bastante similares uno crea los Threads manualmente, mientras que el segundo usa el Pool de Threads administrado
por lo que no crea infinitos Threads.
##doJobAsync
Versión de doJobThreadPool en el modo Spring, usa su sistema de anotaciones para eliminar el boilerplate, no es necesario
inyectar el TaskExecutor, lo hace el interceptor automaticamente al llamar un metodo anotado con @Async('que ejecutor usaras (el que esta configurado en ThreadConfig)')


### Ciclo de vida de los Jobs
Se tiene basicamente el ciclo, un Job nace en pendiente, cuando inicia su ejecucion pasa a ejecutando y finalmente llega a finalizado o fallido.

Como podemos visualizarlo en JobService en el método doJob

```java
    public JobResponse doJob(Job job) throws InterruptedException
    {
        String nombreThread = Thread.currentThread().getName();

        System.out.println("Ejecutando [doJob] - Hilo: " + nombreThread);

        job.setThreadName(nombreThread);

        Thread.sleep(5000);

        var started = Instant.now();

        job.setRunning(started);

        jobRepository.save(job);

        Random random = new Random();

        for (int i = 0; i<10; i++)
        {
            Thread.sleep(1000);

            if (random.nextInt(10) <= 0)
            {
                job.setFailed(Instant.now());
                jobRepository.save(job);
                throw new RuntimeException("Error aleatorio del job");
            }
        }

        var finished = Instant.now();

        job.setCompleted(finished);

        jobRepository.save(job);

        return jobMapper.apply(job);
    }
```

lo que hace este metodo es recibir un Job (se toma como una request desde el controlador, podría representar ejecutar un pedido, una compra.
Se manda a ejecutar asincronicamente ese proceso y al cliente se le avisa que se esta procesando)

vemos en su flujo que le coloca el nombre del thread que lo esta ejecutando, espera 5 segundos e inicia.

ahoro en el bucle vemos que cada 1 segundo, pregunta oye voy a fallar? (simulando un proceso que realmente pueda fallar alguna vez)
si no continuo si no me detengo. el proceso si termina se marca como terminado si no se marca como fallido y se guarda y se devuelve una respuesta.

Este metodo se llama directamente en el controlador

```java
    @PostMapping("/sync")
    public ResponseEntity<JobResponse> doJob() throws InterruptedException
    {
        var job = jobService.createJob();

        return ResponseEntity.ok(jobService.doJob(job));
    }
```

El cliente debe esperar 15 segundos a su respuesta, a menos que falle antes. los metodos thread ya son asincronicos pero menos
comodos para usar, por lo que usaremos de ejemplo la manera de Spring de realizarlo.

Mientras que en el modelo asincronico vemos que llama a doJobAsync llama internamente a doJob, solamente es un envoltorio que se
ejecuta en otro Thread, maneja el error propio de usar un Thread distinto.

```java
    @Async("jobExecutor")
    public void doJobAsync(Job job)
    {
        try
        {
            doJob(job); 
        }
        catch (InterruptedException ex)
        {
            job.setState(Job.State.FAILED);
            jobRepository.save(job);
            Thread.currentThread().interrupt();
            throw new RuntimeException(ex);
        }
    }
```

Es llamado por el metodo doJobAsync en el controlador, virtualmente se ve igual a doJob. La diferencia radica en que este método
devuelve una respuesta inmediatamente HTTP 202 aceptado, y el .location agrega el header a la respuesta con la información para buscarla,
se menciona una URL para consultar en el metodo findById

```java
    @PostMapping("/async")
    public ResponseEntity<Void> doJobAsync()
    {
        var job = jobService.createJob();

        jobService.doJobAsync(job);

        return ResponseEntity.accepted()
            .location(URI.create("/jobs/"+job.getId()))
            .build();
    }
```