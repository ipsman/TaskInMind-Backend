package com.matedevs.taskinmind.controller;

import com.matedevs.taskinmind.model.Event;
import com.matedevs.taskinmind.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController // Jelöli, hogy ez egy REST Controller
@RequestMapping("/api/events") // Az összes végpont "/api/events" prefixet kap
@CrossOrigin(origins = "http://localhost:3000") // CORS engedélyezése a Next.js frontendről
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        // A @RequestBody automatikusan átalakítja a bejövő JSON-t az Event objektumoddá

        try {
            Event savedEvent = eventService.createEvent(event);
            // Siker esetén add vissza a mentett eseményt egy 201 Created státusszal
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);
        } catch (Exception e) {
            // Általános hiba esetén adj vissza egy 500-as hibát
            // Egy @ControllerAdvice lenne a legtisztább megoldás a globális hibakezelésre
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Összes esemény lekérdezése (GET /api/events)
    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{year}&{month}")
    public ResponseEntity<List<Event>> getEventsByMonth(@RequestParam(required = false) Integer Year,
                                                        @RequestParam(required = false) Integer Month) {
        List<Event> events;

        if (Year != null && Month != null) {
            events = eventService.getEventsByMonth(Year, Month);
        }
        else {
            events = eventService.getAllEvents();
        }

        return ResponseEntity.ok(events);
    }

    // Esemény lekérdezése ID alapján (GET /api/events/{id})
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Optional<Event> event = eventService.getEventById(id);
        return event.map(value -> new ResponseEntity<>(value, HttpStatus.OK)) // 200 OK, ha megtalálta
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // 404 Not Found, ha nem
    }

    // Esemény frissítése ID alapján (PUT /api/events/{id})
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event eventDetails) {
        Optional<Event> optionalEvent = eventService.getEventById(id);

        if (optionalEvent.isPresent()) {
            Event existingEvent = optionalEvent.get();
            existingEvent.setTitle(eventDetails.getTitle());
            existingEvent.setDescription(eventDetails.getDescription());
            existingEvent.setStartDate(eventDetails.getStartDate()); // Frissítjük a kezdő időpontot
            existingEvent.setEndDate(eventDetails.getEndDate());     // Frissítjük a befejező időpontot
            // A 'createdAt' nem frissül, mert 'updatable = false'

            Event updatedEvent = eventService.createEvent(existingEvent);
            return new ResponseEntity<>(updatedEvent, HttpStatus.OK); // 200 OK
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }

    // Esemény törlése ID alapján (DELETE /api/events/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        Optional<Event> event = eventService.getEventById(id);
        if (event.isPresent()) {
            eventService.deleteEvent(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content (sikeres törlés, nincs választest)
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found, ha nem létezik az esemény
    }
}