package ru.leti.wise.task.event.repository;


import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;
import ru.leti.wise.task.event.entity.Session;

import java.util.UUID;

@Repository
public interface SessionRepository extends R2dbcRepository<Session, UUID> {
    @Query("SELECT * FROM Session where user_id = :userId and task_id = :taskId")
    Mono<Session> findByUserIdAndTaskId(UUID userId, UUID taskId);

//    @Modifying
//    @Query("INSERT INTO Session(id, user_id, task_id, created_at) values (:#{#session.id}, :#{#session.userId}, :#{#session.taskId}, :#{#session.createdAt})")
//    Mono<Session> save(Session session);
}
