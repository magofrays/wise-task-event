package ru.leti.wise.task.event.repository;


import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.leti.wise.task.event.entity.Session;

import java.util.UUID;

@Repository
public interface SessionRepository extends R2dbcRepository<Session, UUID> {
    @Query("SELECT * FROM Session where userId = :userId and taskId = :taskId")
    Mono<Session> findByUserIdAndTaskId(UUID userId, UUID taskId);
//    Flux<Session> findByUserId(String userId);
//    Flux<Session> findByTaskId(String taskId);
}
