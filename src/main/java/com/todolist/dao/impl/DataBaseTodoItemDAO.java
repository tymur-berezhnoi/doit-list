package com.todolist.dao.impl;

import com.todolist.dao.TodoItemDAO;
import com.todolist.entity.TodoItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.emptySet;

/**
 * @author Tymur Berezhnoi
 */
public class DataBaseTodoItemDAO implements TodoItemDAO {

    private final DataSource dataSource;

    public DataBaseTodoItemDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(TodoItem todoItem) {
        try(Connection connection = dataSource.getConnection()) {
            String insertSql = "INSERT INTO TODO_ITEM (DESCRIPTION, CREATED_AT) VALUES (?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, todoItem.getDescription());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(todoItem.getCreatedAt()));
            preparedStatement.execute();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                long id = generatedKeys.getLong("ID");
                todoItem.setId(id);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Set<TodoItem> findAll() {
        try(Connection connection = dataSource.getConnection()) {
            String insertSql = "SELECT * FROM TODO_ITEM;";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(insertSql);
            Set<TodoItem> tasks = new HashSet<>();

            while(resultSet.next()) {
                long id = resultSet.getLong("ID");
                String description = resultSet.getString("DESCRIPTION");
                LocalDateTime createdAt = resultSet.getTimestamp("CREATED_AT").toLocalDateTime();
                tasks.add(new TodoItem(id, description, createdAt));
            }
            return tasks;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return emptySet();
    }

    @Override
    public void deleteById(long id) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM TODO_ITEM WHERE ID = ?;");
            statement.setLong(1, id);
            statement.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(TodoItem todoItem) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("UPDATE TODO_ITEM SET DESCRIPTION = ? WHERE ID = ?;");
            statement.setString(1, todoItem.getDescription());
            statement.setLong(2, todoItem.getId());
            statement.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
