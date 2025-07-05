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

    // Új esemény létrehozása (POST /api/events)
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        // A 'createdAt' mezőt az @PrePersist annotáció automatikusan beállítja
        Event createdEvent = eventService.createEvent(event);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED); // 201 Created státusz
    }

    // Összes esemény lekérdezése (GET /api/events)
    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
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