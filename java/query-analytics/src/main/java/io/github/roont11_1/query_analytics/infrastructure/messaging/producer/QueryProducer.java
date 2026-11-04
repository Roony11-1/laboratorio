package io.github.roont11_1.query_analytics.infrastructure.messaging.producer;

import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
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

    public CompletableFuture<SendResult<String, QueryEvent>> publish(QueryEvent event)
    {
        return kafkaTemplate.send(TOPIC, event.getTerm(), event);
    }
}
