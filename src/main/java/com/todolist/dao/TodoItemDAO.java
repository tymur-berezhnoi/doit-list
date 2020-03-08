package com.todolist.dao;

import com.todolist.entity.TodoItem;

import java.util.Set;

/**
 * @author Tymur Berezhnoi
 */
public interface TodoItemDAO {

    void save(TodoItem todoItem);

    Set<TodoItem> findAll();

    void deleteById(long id);

    void update(TodoItem todoItem);
}
