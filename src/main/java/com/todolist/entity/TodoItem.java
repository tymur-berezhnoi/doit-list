package com.todolist.entity;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author Tymur Berezhnoi
 */
public class TodoItem {

    private long id;
    private String description;
    private final LocalDateTime createdAt;

    public TodoItem(long id, String description, LocalDateTime createdAt) {
        this.id = id;
        this.description = description;
        this.createdAt = createdAt;
    }

    public TodoItem(String description, LocalDateTime createdAt) {
        this.description = description;
        this.createdAt = createdAt;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        var todoItem = (TodoItem) o;
        return description.equals(todoItem.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description);
    }

    @Override
    public String toString() {
        return "TodoTask{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
