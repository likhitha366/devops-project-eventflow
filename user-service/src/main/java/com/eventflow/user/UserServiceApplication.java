package com.eventflow.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@SpringBootApplication
public class UserServiceApplication { public static void main(String[] args) { SpringApplication.run(UserServiceApplication.class, args); } }

record User(String id, String name, String email) {}
record NewUser(@NotBlank String name, @Email String email) {}

@RestController @RequestMapping("/users") @CrossOrigin
class UserController {
  private final Map<String, User> users = new LinkedHashMap<>();
  UserController() {
    users.put("usr-demo-001", new User("usr-demo-001", "Aarav Sharma", "aarav.sharma@college.edu"));
    users.put("usr-demo-002", new User("usr-demo-002", "Priya Nair", "priya.nair@college.edu"));
    users.put("usr-demo-003", new User("usr-demo-003", "Rohan Patel", "rohan.patel@college.edu"));
  }
  @GetMapping List<User> all() { return new ArrayList<>(users.values()); }
  @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED)
  User register(@Valid @RequestBody NewUser request) { String id = "usr-" + UUID.randomUUID().toString().substring(0, 8); User user = new User(id, request.name(), request.email()); users.put(id, user); return user; }
  @GetMapping("/{id}") User get(@PathVariable String id) { User user = users.get(id); if (user == null) throw new NoSuchElementException("User not found"); return user; }
}
