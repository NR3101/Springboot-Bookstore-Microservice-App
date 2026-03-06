package com.neeraj.orderservice.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neeraj.orderservice.domain.models.OrderCreatedEvent;
import com.neeraj.orderservice.domain.models.OrderEventType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class OrderEventService {

    private final OrderEventRepository orderEventRepository;
    private final OrderEventPublisher orderEventPublisher;
    private final ObjectMapper objectMapper;

    void save(OrderCreatedEvent event) {
        OrderEventEntity orderEvent = OrderEventEntity.builder()
                .eventId(event.eventId())
                .eventType(OrderEventType.ORDER_CREATED)
                .orderNumber(event.orderNumber())
                .createdAt(event.createdAt())
                .payload(toJsonPayload(event))
                .build();

        orderEventRepository.save(orderEvent);
    }

    public void publishOrderEvents() {
        Sort sort = Sort.by(Sort.Direction.ASC, "createdAt");
        List<OrderEventEntity> eventsToPublish = orderEventRepository.findAll(sort);

        log.info("Found {} order events to publish", eventsToPublish.size());
        for (OrderEventEntity event : eventsToPublish) {
            this.publishEvent(event);
            orderEventRepository.delete(event);
        }
    }

    private void publishEvent(OrderEventEntity event) {
        OrderEventType eventType = event.getEventType();

        switch (eventType) {
            case ORDER_CREATED -> {
                log.info(
                        "Publishing ORDER_CREATED event with id: {} for order number: {}",
                        event.getEventId(),
                        event.getOrderNumber());
                OrderCreatedEvent orderCreatedEvent = fromJsonPayload(event.getPayload(), OrderCreatedEvent.class);
                orderEventPublisher.publish(orderCreatedEvent);
            }
            default -> log.warn("Unknown event type: {} for event id: {}", eventType, event.getEventId());
        }
    }

    private String toJsonPayload(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T fromJsonPayload(String jsonPayload, Class<T> eventType) {
        try {
            return objectMapper.readValue(jsonPayload, eventType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
