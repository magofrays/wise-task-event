package ru.leti.wise.task.event.repository;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.leti.wise.task.event.dto.TaskUserCount;
import ru.leti.wise.task.event.entity.Event;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public interface EventRepository extends R2dbcRepository<Event, UUID> {
    @Query("""
            SELECT s.task_id as task_id, s.user_id as user_id, COUNT(*) AS count
            FROM event e
            JOIN session s ON e.session_id = s.id
            JOIN event_type et ON et.id = e.event_type_id
            WHERE et.event_name = :eventType
              AND (:time IS NULL OR e.created_at > :time)
            GROUP BY s.task_id, s.user_id
            """)
    Flux<TaskUserCount> getEventCountGroupedByTaskIdAndUserId(@Param("eventType") String eventType,
                                                           @Param("time") Instant time);

    @Query("""
    SELECT s.user_id as user_id, COUNT(*) AS count
    FROM event e
    JOIN session s ON s.id = e.session_id
    JOIN event_type et ON et.id = e.event_type_id
    WHERE et.event_name = :eventType
        AND (:time IS NULL OR e.created_at > :time)
        AND s.task_id = :taskId
    GROUP BY s.user_id
    """)
    Flux<TaskUserCount> getEventCountGroupedByUserId(@Param("eventType") String eventType,
                                                     @Param("time") Instant time,
                                                     @Param("taskId") UUID taskId);

    @Query("""
    SELECT s.task_id as task_id, COUNT(*) AS count
    FROM event e
    JOIN session s ON s.id = e.session_id
    JOIN event_type et ON et.id = e.event_type_id
    WHERE et.event_name = :eventType
        AND (:time IS NULL OR e.created_at > :time)
        AND s.user_id = :userId
    GROUP BY s.task_id
    """)
    Flux<TaskUserCount> getEventCountGroupedByTaskId(@Param("eventType") String eventType,
                                                           @Param("time") Instant time,
                                                           @Param("userId") UUID userId);

    @Query("""
    SELECT s.task_id as task_id, s.user_id as user_id, COUNT(*) AS count
    FROM event e
    JOIN session s ON s.id = e.session_id
    JOIN event_type et ON et.id = e.event_type_id
    WHERE et.event_name = :eventType
        AND (:time IS NULL OR e.time > :time)
        AND s.user_id = :userId
        AND s.task_id = :taskId
    """)
    Flux<TaskUserCount> getEventCountByUserIdAndTaskId(@Param("eventType") String eventType,
                                                             @Param("time") Instant time,
                                                             @Param("taskId") UUID taskId,
                                                             @Param("userId") UUID userId);

    default Flux<TaskUserCount> getEventCount(String eventType, Instant time,
                                                                     UUID taskId, UUID userId){
        if(userId != null &&  taskId != null){
            return getEventCountByUserIdAndTaskId(eventType, time, taskId, userId);
        }
        if(userId != null){
            return getEventCountGroupedByTaskId(eventType, time, userId);
        }
        if(taskId != null){
            return getEventCountGroupedByUserId(eventType, time, taskId);
        }
        return getEventCountGroupedByTaskIdAndUserId(eventType, time);
    }
}
