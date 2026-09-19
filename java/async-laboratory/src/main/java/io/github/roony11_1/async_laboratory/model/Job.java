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
