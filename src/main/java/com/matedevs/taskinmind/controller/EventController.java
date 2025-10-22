package com.matedevs.taskinmind.controller;

import com.matedevs.taskinmind.config.CustomUserDetails;
import com.matedevs.taskinmind.model.Event;
import com.matedevs.taskinmind.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:3000")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }


    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("A felhasználó nincs hitelesítve.");
        }

        // 1. Check if authenticated
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            return customUserDetails.getId();
        }
        else{
            throw new SecurityException("Ervenytelen token!");
        }
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        Long userId = getAuthenticatedUserId();

        event.setUserId(userId);
        try {
            Event savedEvent = eventService.createEvent(event);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    @GetMapping(params = {"Year", "Month"})
    public ResponseEntity<List<Event>> getEventsByMonth(@RequestParam Integer Year,
                                                        @RequestParam Integer Month) {
        List<Event> events;
        try {
            Long userId = getAuthenticatedUserId();
            System.out.println("DEBUG: EventService.getEventsByMonthAndUser called with year=" + Year + ", month=" + Month + ", userId=" + userId);
            logger.info("DEBUG: EventService.getEventsByMonthAndUser called with year=" + Year + ", month=" + Month + ", userId=" + userId);
            List<Event> event = eventService.getEventsByMonth(Year, Month, userId);
            return ResponseEntity.ok(event);
        } catch (SecurityException e) {
            System.err.println("Security error getting events by month: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (Exception e) {
            System.err.println("Error getting events by month: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Optional<Event> event = eventService.getEventById(id);
        return event.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event eventDetails) {
        Optional<Event> optionalEvent = eventService.getEventById(id);

        if (optionalEvent.isPresent()) {
            Event existingEvent = optionalEvent.get();
            existingEvent.setTitle(eventDetails.getTitle());
            existingEvent.setDescription(eventDetails.getDescription());
            existingEvent.setStartDate(eventDetails.getStartDate());
            existingEvent.setEndDate(eventDetails.getEndDate());
            existingEvent.setLocation(eventDetails.getLocation());
            existingEvent.setColor(eventDetails.getColor());
            existingEvent.setRepeatEvent(eventDetails.getRepeatEvent());
            existingEvent.setReminderTime(eventDetails.getReminderTime());

            Event updatedEvent = eventService.createEvent(existingEvent);
            return new ResponseEntity<>(updatedEvent, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        Optional<Event> event = eventService.getEventById(id);
        if (event.isPresent()) {
            eventService.deleteEvent(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}