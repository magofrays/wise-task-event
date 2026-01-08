package ru.leti.wise.task.event.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.leti.wise.task.event.dto.CreateEventRequest;
import ru.leti.wise.task.event.dto.CreateEventTypeRequest;
import ru.leti.wise.task.event.entity.Event;
import ru.leti.wise.task.event.entity.EventType;
import ru.leti.wise.task.event.entity.Session;
import ru.leti.wise.task.event.exception.BusinessException;
import ru.leti.wise.task.event.exception.ErrorCode;
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

    public Mono<Void> createEvent(@NotNull CreateEventRequest createEventRequest) {
        return eventTypeRepository.findByEventName(createEventRequest.getEventType())
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.NOT_FOUND, "Event type does not exist!")))
                .flatMap(eventType -> findSession(createEventRequest.getUserId(), createEventRequest.getTaskId())
                        .flatMap(session -> {
                            var event = Event.builder()
//                                    .id(UUID.randomUUID())
                                    .sessionId(session.getId())
                                    .eventEntityId(createEventRequest.getEventEntityId())
                                    .eventTypeId(eventType.getId())
                                    .eventValue(createEventRequest.getEventValue())
                                    .build();
                            return eventRepository.save(event).then();
                        }));
    }

    private Mono<Session> findSession(UUID userId, UUID taskId) {
        return sessionRepository.findByUserIdAndTaskId(userId, taskId)
                .switchIfEmpty(
                        sessionRepository.save(Session.builder()
                                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                                .taskId(taskId)
                                .userId(userId)
                                .build())
                );
    }
    public Mono<Void> createEventType(@NotNull CreateEventTypeRequest request) {
        var eventType = EventType.builder()
                .eventName(request.getEventName())
                .build();
        return eventTypeRepository.save(eventType).then();
    }
}
