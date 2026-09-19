package io.github.roony11_1.async_laboratory.service;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import io.github.roony11_1.async_laboratory.dto.JobResponse;
import io.github.roony11_1.async_laboratory.model.Job;
import io.github.roony11_1.async_laboratory.repository.IJobRepository;

@Service 
public class JobService 
{
    private final IJobRepository jobRepository;
    private final Function<Job, JobResponse> jobMapper = job -> 
        new JobResponse(job.getStartedAt(), job.getFinishedAt(), job.getTimeRunning(), job.getThreadName(), job.getState());

    public JobService(IJobRepository jobRepository)
    {
        this.jobRepository = jobRepository;
    }

    public List<JobResponse> findAll()
    {
        return jobRepository.findAll().stream()
            .map(jobMapper::apply)
            .toList();
    }

    public JobResponse findById(UUID id)
    {
        return jobRepository.findById(id)
            .map(jobMapper::apply)
            .orElseThrow(() -> new IllegalStateException("No se encontro el Job con la id asociada"));
    }

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

    public Job createJob()
    {
        var job = new Job();

        jobRepository.save(job);

        return job;
    }
}
