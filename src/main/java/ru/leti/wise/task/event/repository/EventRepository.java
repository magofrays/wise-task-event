package ru.leti.wise.task.event.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.leti.wise.task.event.entity.Event;

import java.util.UUID;

@Repository
public interface EventRepository extends R2dbcRepository<Event, UUID> {
//    @Query("""
//        SELECT ei.* FROM event_info ei
//        JOIN session s ON ei.session_id = s.session_id
//        WHERE s.task_id = :taskId and s.user_id = :userId
//        """)
//    Flux<Event> findByTaskIdAndUserId(@Param("taskId") String taskId, @Param("userId") String userId);
//
//    Flux<Event> findAllBySessionId(UUID sessionId);
    Flux<Event> findAllBySessionIdAndEventType
}
