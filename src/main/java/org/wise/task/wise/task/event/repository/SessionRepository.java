package org.wise.task.wise.task.event.repository;


import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import org.wise.task.wise.task.event.entity.Session;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface SessionRepository extends R2dbcRepository<Session, UUID> {
//    @Query("SELECT * FROM Session where userId = :userId and taskId = :taskId")
//    Mono<Session> findByUserIdAndTaskId(String userId, String taskId);
//    Flux<Session> findByUserId(String userId);
//    Flux<Session> findByTaskId(String taskId);
}
