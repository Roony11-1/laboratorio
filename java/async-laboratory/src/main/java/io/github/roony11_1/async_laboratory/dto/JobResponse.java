package io.github.roony11_1.async_laboratory.dto;

import java.time.Instant;

import io.github.roony11_1.async_laboratory.model.Job;

public record JobResponse(
    Instant startedAt,
    Instant finishedAt,
    Long timeRunning,
    String threadName,
    Job.State state) 
{
}