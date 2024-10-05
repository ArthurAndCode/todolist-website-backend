package com.arthur.code.todolist_website_backend.service;

import com.arthur.code.todolist_website_backend.data.TaskRepository;
import com.arthur.code.todolist_website_backend.model.Task;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskService {

    @Autowired
    private final TaskRepository taskRepository;

    public List<Task> findAllTasks(Long userId) {
        return taskRepository.findAllByUserId(userId);
    }

    public Task addTask(Task task) {
        taskRepository.save(task);
        return task;
    }

    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    public void deleteAllTasksByUserId(Long userId) {
        taskRepository.deleteByUserId(userId);
    }

    public void updateTask(Long taskId, Task updatedTask) {
        Task task = findTaskByIdOrThrow(taskId);
        updateTaskDetails(task, updatedTask);
    }

    private Task findTaskByIdOrThrow(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalStateException("Task with id " + taskId + " does not exist"));
    }

    private void updateTaskDetails(Task task, Task updatedTask) {
        task.setText(updatedTask.getText());
        taskRepository.save(task);
    }
}
