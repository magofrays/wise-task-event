package ru.leti.wise.task.event.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import ru.leti.wise.task.event.entity.Statistic;
import ru.leti.wise.task.event.entity.StatisticScope;
import ru.leti.wise.task.event.entity.StatisticType;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StatisticRepository extends R2dbcRepository<Statistic, UUID> {
    Optional<Statistic> findByTaskIdAndUserIdAndTypeAndScopeAndEventType(UUID taskId, UUID userId, StatisticType type,
                                                                         StatisticScope scope, String eventType);
    Optional<Statistic> findByTaskIdAndTypeAndScopeAndEventType(UUID taskId, StatisticType type,
                                                                StatisticScope scope, String eventType);
    Optional<Statistic> findByUserIdAndTypeAndScopeAndEventType(UUID userId, StatisticType type,
                                                                StatisticScope scope, String eventType);
}
