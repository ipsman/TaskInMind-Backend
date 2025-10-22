package com.matedevs.taskinmind.controller;


import com.matedevs.taskinmind.model.Task;
import com.matedevs.taskinmind.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:3000")
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<Task> addTask(@RequestBody Task task){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Task savedTask = taskService.createTask(task, username);

        return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
    }

    @GetMapping
    public List<Task> getAllTasksForCurrentUser() {

        // 1. Az Authentication objektum megszerzése
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            // Ezt a Security filternek kellene kezelnie, de biztonsági okból:
            throw new AccessDeniedException("User not authenticated.");
        }

        // 2. Felhasználónév kinyerése a Principal objektumból (ez lehet a JWT-ből származó username)
        String username = authentication.getName();

        // 3. Ezzel a username-mel meg kell szerezni a tényleges userId-t
        // Ezt a Service-ben érdemes kezelni!

        Long userId = taskService.getUserIdByUsername(username); // <-- Ezt a metódust kell hozzáadnod a TaskService-hez!

        // Visszaadjuk a taskokat a valódi userId alapján
        return taskService.getTasksByUserId(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id){
        Optional<Task> task = taskService.getTaskById(id);
        return task.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@RequestBody Task taskDetails, @PathVariable Long id){
        Task updatedTask = taskService.updateTask(id, taskDetails);
        if(updatedTask != null){
            return new ResponseEntity<>(updatedTask, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/(id)")
    public ResponseEntity<Task> deleteTask(@PathVariable Long id){
        Optional<Task> task = taskService.getTaskById(id);
        if(task.isPresent()){
            taskService.deleteTask(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
