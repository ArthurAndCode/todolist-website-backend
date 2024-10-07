package com.arthur.code.todolist_website_backend.controller;

import com.arthur.code.todolist_website_backend.model.Task;
import com.arthur.code.todolist_website_backend.service.taskService.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/task")
@CrossOrigin(origins = "http://localhost:5173")
public class TaskController {
    private final TaskService taskService;
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Task>> getAllTasks(@PathVariable Long userId) {
        List<Task> tasks = taskService.findAllTasks(userId);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<Task> addNewTask(@RequestBody Task task) {
        Task addedTask = taskService.addTask(task);
        return new ResponseEntity<>(addedTask, HttpStatus.CREATED);
    }
    @DeleteMapping("/{taskId}")
    public void addNewTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
    }

    @PutMapping("/{taskId}")
    public void updateTask(@PathVariable Long taskId, @RequestBody Task updatedTask) {
        taskService.updateTask(taskId, updatedTask);
    }
}
