package ru.leti.wise.task.event.mapper;

import org.mapstruct.Mapper;
import ru.leti.wise.task.event.Statistic;
import ru.leti.wise.task.event.entity.Session;



@Mapper(componentModel = "spring")
public interface SessionMapper {
    Statistic.Session toResponse(Session session);
}
