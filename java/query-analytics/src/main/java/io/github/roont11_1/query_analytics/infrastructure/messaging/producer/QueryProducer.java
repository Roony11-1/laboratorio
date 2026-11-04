package io.github.roont11_1.query_analytics.infrastructure.messaging.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import io.github.roont11_1.query_analytics.core.domain.events.QueryEvent;

@Component 
public class QueryProducer 
{
    private static final String TOPIC = "query-events";
    private final KafkaTemplate<String, QueryEvent> kafkaTemplate;

    public QueryProducer(KafkaTemplate<String, QueryEvent> kafkaTemplate)
    {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(QueryEvent event)
    {
        kafkaTemplate.send(TOPIC, event.term(), event);
    }
}
