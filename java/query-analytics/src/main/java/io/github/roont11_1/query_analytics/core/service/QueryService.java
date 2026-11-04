package io.github.roont11_1.query_analytics.core.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import io.github.roont11_1.query_analytics.api.dto.QueryRequest;
import io.github.roont11_1.query_analytics.core.domain.events.QueryEvent;
import io.github.roont11_1.query_analytics.infrastructure.messaging.producer.QueryProducer;

@Service
public class QueryService 
{
    private final QueryProducer queryProducer;

    public QueryService(QueryProducer queryProducer)
    {
        this.queryProducer = queryProducer;
    }

    public void query(QueryRequest request)
    {
        var start = System.nanoTime();

        // Aquí irá la lógica de procesamiento de la consulta.

        var durationMs = (System.nanoTime() - start) / 1_000_000;

        var event = new QueryEvent(
            UUID.randomUUID(),
            request.term(),
            request.category(),
            durationMs,
            Instant.now()
        );

        queryProducer.publish(event);
    }
}
