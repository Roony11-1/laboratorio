package io.github.roony11_1.async_laboratory.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.roony11_1.async_laboratory.dto.JobResponse;
import io.github.roony11_1.async_laboratory.service.JobService;

@RestController
@RequestMapping("/jobs")
public class JobController 
{
    private final JobService jobService;
    private final TaskExecutor jobExecutor;

    public JobController(JobService jobService, TaskExecutor jobExecutor)
    {
        this.jobService = jobService;
        this.jobExecutor = jobExecutor;
    }

    @GetMapping("/all")
    public ResponseEntity<List<JobResponse>> findAll()
    {
        var jobs = jobService.findAll();

        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> findById(@PathVariable UUID id)
    {
        return ResponseEntity.ok(jobService.findById(id));
    }

    @PostMapping("/sync")
    public ResponseEntity<JobResponse> doJob() throws InterruptedException
    {
        var job = jobService.createJob();

        return ResponseEntity.ok(jobService.doJob(job));
    }

    @PostMapping("/thread")
    public ResponseEntity<Void> doJobThread()
    {
        var job = jobService.createJob();

        new Thread(() -> 
        {
            try
            {
                jobService.doJob(job);
            }
            catch (InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
        }).start();

        return ResponseEntity.accepted()
            .location(URI.create("/jobs/"+job.getId()))
            .build();
    }

    @PostMapping("/thread-pool")
    public ResponseEntity<Void> doJobThreadPool()
    {
        var job = jobService.createJob();

        jobExecutor.execute(() -> 
        {
            try
            {
                jobService.doJob(job);
            }
            catch (InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
        });

        return ResponseEntity.accepted()
            .location(URI.create("/jobs/"+job.getId()))
            .build();
    }

    @PostMapping("/async")
    public ResponseEntity<Void> doJobAsync()
    {
        var job = jobService.createJob();

        jobService.doJobAsync(job);

        return ResponseEntity.accepted()
            .location(URI.create("/jobs/"+job.getId()))
            .build();
    }
}
