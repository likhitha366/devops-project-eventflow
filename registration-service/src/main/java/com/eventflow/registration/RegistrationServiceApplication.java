package com.eventflow.registration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@SpringBootApplication
public class RegistrationServiceApplication { public static void main(String[] args) { SpringApplication.run(RegistrationServiceApplication.class, args); } }
record Registration(String id, String userId, String eventId, String status, LocalDateTime registeredAt) {}
record NewRegistration(@NotBlank String userId, @NotBlank String eventId) {}
@RestController @RequestMapping("/registrations") @CrossOrigin
class RegistrationController {
 private final Map<String, Registration> registrations = new LinkedHashMap<>();
 @GetMapping List<Registration> all() { return new ArrayList<>(registrations.values()); }
 @GetMapping("/user/{userId}") List<Registration> byUser(@PathVariable String userId) { return registrations.values().stream().filter(r -> r.userId().equals(userId)).toList(); }
 @PostMapping @ResponseStatus(HttpStatus.CREATED) Registration register(@Valid @RequestBody NewRegistration input) { Registration registration = new Registration("reg-" + UUID.randomUUID().toString().substring(0, 8), input.userId(), input.eventId(), "CONFIRMED", LocalDateTime.now()); registrations.put(registration.id(), registration); return registration; }
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) void cancel(@PathVariable String id) { registrations.remove(id); }
}
