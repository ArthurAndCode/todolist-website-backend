package com.arthur.code.todolist_website_backend.model;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "text", nullable = false, updatable = true, length = 500)
    private String text;

    @Column(name = "user_id")
    private Long userId;
}

