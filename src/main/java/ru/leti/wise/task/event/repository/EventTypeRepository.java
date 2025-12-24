package ru.leti.wise.task.event.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import ru.leti.wise.task.event.entity.EventType;

public interface EventTypeRepository extends R2dbcRepository<EventType, Long> {
}
