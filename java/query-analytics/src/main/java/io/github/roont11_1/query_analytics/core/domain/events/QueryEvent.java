package io.github.roont11_1.query_analytics.core.domain.events;

import java.time.Instant;
import java.util.UUID;

public class QueryEvent
{
    private UUID queryId;
    private String term;
    private String category;
    private long durationMs;
    private Instant timestamp;

    public QueryEvent(
        UUID queryId,
        String term,
        String category,
        Long durationMs,
        Instant timestamp)
    {
        this.queryId = queryId;
        this.term = term;
        this.category = category;
        this.durationMs = durationMs;
        this.timestamp = timestamp;
    }

    public UUID getQueryId()
    {
        return queryId;
    }

    public void setQueryId(UUID queryId)
    {
        this.queryId = queryId;
    }

    public String getTerm()
    {
        return term;
    }

    public void setTerm(String term)
    {
        this.term = term;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public long getDurationMs()
    {
        return durationMs;
    }

    public void setDurationMs(long durationMs)
    {
        this.durationMs = durationMs;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp)
    {
        this.timestamp = timestamp;
    }
}