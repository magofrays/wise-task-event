package ru.leti.wise.task.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.leti.wise.task.event.dto.CreateEventRequest;
import ru.leti.wise.task.event.dto.CreateEventTypeRequest;
import ru.leti.wise.task.event.entity.Event;
import ru.leti.wise.task.event.entity.EventType;
import ru.leti.wise.task.event.entity.Session;
import ru.leti.wise.task.event.repository.EventRepository;
import ru.leti.wise.task.event.repository.EventTypeRepository;
import ru.leti.wise.task.event.repository.SessionRepository;

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
        return findSession(createEventRequest.getUserId(), createEventRequest.getTaskId())
                .flatMap(session -> {
                    var event = Event.builder()
                            .id(UUID.randomUUID())
                            .session_id(session.getId())
                            .event_entity_id(createEventRequest.getEventEntityId())
                            .event_type_id(createEventRequest.getEventTypeId())
                            .event_value(createEventRequest.getEventValue())
                            .build();
                    return eventRepository.save(event).then();
                });
    }

    private Mono<Session> findSession(UUID userId, UUID taskId) {
        return sessionRepository.findByUserIdAndTaskId(userId, taskId)
                .switchIfEmpty(
                        sessionRepository.save(Session.builder()
                                .id(UUID.randomUUID())
                                .created_at(Timestamp.valueOf(LocalDateTime.now()))
                                .task_id(taskId)
                                .user_id(userId)
                                .build())
                );
    }

    public Mono<Void> createEventType(CreateEventTypeRequest request) {
        var eventType = EventType.builder()
                .event_name(request.getEventName())
                .build();
        return eventTypeRepository.save(eventType).then();
    }
}
