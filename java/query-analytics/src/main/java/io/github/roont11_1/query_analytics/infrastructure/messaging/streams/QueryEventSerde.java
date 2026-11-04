package io.github.roont11_1.query_analytics.infrastructure.messaging.streams;

import org.apache.kafka.common.serialization.Serde;
import org.springframework.kafka.support.serializer.JacksonJsonSerde;

import io.github.roont11_1.query_analytics.core.domain.events.QueryEvent;

public class QueryEventSerde 
{
    public static Serde<QueryEvent> create()
    {
        return new JacksonJsonSerde<>(QueryEvent.class);
    }
}
