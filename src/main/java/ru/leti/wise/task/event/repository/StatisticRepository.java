package ru.leti.wise.task.event.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.leti.wise.task.event.Statistic;
import ru.leti.wise.task.event.entity.StatisticEntity;
import java.util.UUID;

@Repository
public interface StatisticRepository extends R2dbcRepository<StatisticEntity, UUID> {
    Mono<StatisticEntity> findByTaskIdAndUserIdAndTypeAndScopeAndEventType(UUID taskId, UUID userId, Statistic.StatisticType type,
                                                                               Statistic.StatisticScope scope, String eventType);
    Mono<StatisticEntity> findByTaskIdAndTypeAndScopeAndEventType(UUID taskId, Statistic.StatisticType type,
                                                                  Statistic.StatisticScope scope, String eventType);
    Mono<StatisticEntity> findByUserIdAndTypeAndScopeAndEventType(UUID userId, Statistic.StatisticType type,
                                                                      Statistic.StatisticScope scope, String eventType);
    Mono<StatisticEntity> findByTypeAndScopeAndEventType(Statistic.StatisticType type,
                                                                  Statistic.StatisticScope scope, String eventType);
}
