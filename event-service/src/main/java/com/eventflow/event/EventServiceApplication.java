package com.eventflow.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@SpringBootApplication
public class EventServiceApplication { public static void main(String[] args) { SpringApplication.run(EventServiceApplication.class, args); } }
record Event(String id, String title, String date, String venue, int seats, String description) {}
record NewEvent(@NotBlank String title, @NotBlank String date, @NotBlank String venue, @Min(1) int seats, String description) {}
@RestController @RequestMapping("/events") @CrossOrigin
class EventController {
 private final Map<String, Event> events = new LinkedHashMap<>();
 EventController() { put(new Event("evt-techfest", "TechFest 2026", "10 November 2026", "Main Auditorium", 100, "A day of student innovation, demos and talks.")); put(new Event("evt-cloud", "Cloud Native Workshop", "18 November 2026", "Lab Block B", 40, "Build and ship your first containerized service.")); put(new Event("evt-hack", "Campus Hackathon", "26 November 2026", "Innovation Hub", 80, "A 12-hour challenge for practical problem solvers.")); }
 private void put(Event event) { events.put(event.id(), event); }
 @GetMapping List<Event> all() { return new ArrayList<>(events.values()); }
 @GetMapping("/{id}") Event one(@PathVariable String id) { Event event = events.get(id); if(event == null) throw new NoSuchElementException("Event not found"); return event; }
 @PostMapping @ResponseStatus(HttpStatus.CREATED) Event create(@Valid @RequestBody NewEvent input) { Event event = new Event("evt-" + UUID.randomUUID().toString().substring(0, 8), input.title(), input.date(), input.venue(), input.seats(), input.description()); put(event); return event; }
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) void delete(@PathVariable String id) { events.remove(id); }
}
