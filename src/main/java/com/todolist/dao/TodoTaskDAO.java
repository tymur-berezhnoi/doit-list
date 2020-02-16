package com.todolist.dao;

import com.todolist.entity.TodoTask;

import java.util.Set;

/**
 * @author Tymur Berezhnoi
 */
public interface TodoTaskDAO {

    void save(TodoTask todoTask);

    Set<TodoTask> findAll();

    void deleteById(long id);

    void update(TodoTask todoTask);
}
