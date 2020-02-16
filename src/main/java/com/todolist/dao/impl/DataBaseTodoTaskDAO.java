package com.todolist.dao.impl;

import com.todolist.dao.TodoTaskDAO;
import com.todolist.entity.TodoTask;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Tymur Berezhnoi
 */
public class DataBaseTodoTaskDAO implements TodoTaskDAO {

    private final DataSource dataSource;

    public DataBaseTodoTaskDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(TodoTask todoTask) {
        try(Connection connection = dataSource.getConnection()) {
            String insertSql = "INSERT INTO TODO_TASK (DESCRIPTION, CREATED_AT) VALUES (?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, todoTask.getDescription());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(todoTask.getCreatedAt()));
            preparedStatement.execute();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                long id = generatedKeys.getLong("ID");
                todoTask.setId(id);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Set<TodoTask> findAll() {
        try(Connection connection = dataSource.getConnection()) {
            String insertSql = "SELECT * FROM TODO_TASK;";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(insertSql);
            Set<TodoTask> tasks = new HashSet<>();

            // JDBC Mapping from DB table to Java object
            while(resultSet.next()) {
                long id = resultSet.getLong("ID");
                String description = resultSet.getString("DESCRIPTION");
                LocalDateTime createdAt = resultSet.getTimestamp("CREATED_AT").toLocalDateTime();
                tasks.add(new TodoTask(id, description, createdAt));
            }
            return tasks;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptySet();
    }

    @Override
    public void deleteById(long id) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM TODO_TASK WHERE ID = ?;");
            statement.setLong(1, id);
            statement.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(TodoTask todoTask) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("UPDATE TODO_TASK SET DESCRIPTION = ? WHERE ID = ?;");
            statement.setString(1, todoTask.getDescription());
            statement.setLong(2, todoTask.getId());
            statement.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
