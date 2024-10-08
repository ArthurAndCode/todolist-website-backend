package com.arthur.code.todolist_website_backend.repository;

import com.arthur.code.todolist_website_backend.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.userId = :userId")
    List<Task> findAllByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    void deleteByUserId(Long userId);
}
