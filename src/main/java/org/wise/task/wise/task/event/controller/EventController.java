package org.wise.task.wise.task.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.wise.task.wise.task.event.dto.CreateEventRequest;
import org.wise.task.wise.task.event.service.EventService;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping("/create")
    public Mono<Void> createEvent(@AuthenticationPrincipal Jwt userToken,
                                  @RequestBody CreateEventRequest request){
        request.setUserId(UUID.fromString(userToken.getClaim("id")));
        return eventService.createEvent(request);
    }

    @PreAuthorize("hasRole(\"ADMIN\")")
    @PostMapping("/type/create")
    public void createEventType(){

    }
}
