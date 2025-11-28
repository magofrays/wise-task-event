package org.wise.task.wise.task.event.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.wise.task.wise.task.event.entity.Event;
import reactor.core.publisher.Flux;

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
}
