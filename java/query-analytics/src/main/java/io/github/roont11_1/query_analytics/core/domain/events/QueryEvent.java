package io.github.roont11_1.query_analytics.core.domain.events;

import java.time.Instant;
import java.util.UUID;

public record QueryEvent(
    UUID queryId,
    String term,
    String category,
    Long durationMs,
    Instant timestamp) 
{

}
