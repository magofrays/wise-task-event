package org.wise.task.wise.task.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.wise.task.wise.task.event.dto.CreateEventRequest;
import org.wise.task.wise.task.event.dto.CreateEventTypeRequest;
import org.wise.task.wise.task.event.entity.Event;
import org.wise.task.wise.task.event.entity.EventType;
import org.wise.task.wise.task.event.entity.Session;
import org.wise.task.wise.task.event.repository.EventRepository;
import org.wise.task.wise.task.event.repository.EventTypeRepository;
import org.wise.task.wise.task.event.repository.SessionRepository;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventTypeRepository eventTypeRepository;
    private final SessionRepository sessionRepository;

    public Mono<Void> createEvent(CreateEventRequest createEventRequest) {
        var session = Session.builder()
                .id(UUID.randomUUID())
                .created_at(Timestamp.valueOf(LocalDateTime.now()))
                .task_id(createEventRequest.getTaskId())
                .user_id(createEventRequest.getUserId())
                .build();

        return sessionRepository.save(session)
                .flatMap(savedSession -> {
                    var event = Event.builder()
                            .id(UUID.randomUUID())
                            .session_id(savedSession.getId())
                            .event_entity_id(createEventRequest.getEventEntityId())
                            .event_type_id(createEventRequest.getEventTypeId())
                            .event_value(createEventRequest.getEventValue())
                            .build();
                    return eventRepository.save(event);
                })
                .then();
    }
    public Mono<Void> createEventType(CreateEventTypeRequest request) {
        var eventType = EventType.builder().event_name(request.getEventName()).build();
        return eventTypeRepository.save(eventType).then();
    }
}
