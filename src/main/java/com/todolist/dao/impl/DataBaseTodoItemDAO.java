package com.todolist.dao.impl;

import com.todolist.dao.TodoItemDAO;
import com.todolist.entity.TodoItem;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
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
        try(var connection = dataSource.getConnection()) {
            var insertSql = "INSERT INTO TODO_ITEM (DESCRIPTION, CREATED_AT) VALUES (?, ?);";
            var preparedStatement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, todoItem.getDescription());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(todoItem.getCreatedAt()));
            preparedStatement.execute();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                var id = generatedKeys.getLong("ID");
                todoItem.setId(id);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Set<TodoItem> findAll() {
        try(var connection = dataSource.getConnection()) {
            var insertSql = "SELECT * FROM TODO_ITEM;";
            var statement = connection.createStatement();
            var resultSet = statement.executeQuery(insertSql);
            var tasks = new HashSet<TodoItem>();

            while(resultSet.next()) {
                var id = resultSet.getLong("ID");
                var description = resultSet.getString("DESCRIPTION");
                var createdAt = resultSet.getTimestamp("CREATED_AT").toLocalDateTime();
                var status = TodoItem.Status.valueOf(resultSet.getString("STATUS"));
                tasks.add(new TodoItem(id, description, createdAt, status));
            }
            return tasks;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return emptySet();
    }

    @Override
    public void deleteById(long id) {
        try (var connection = dataSource.getConnection()) {
            var statement = connection.prepareStatement("DELETE FROM TODO_ITEM WHERE ID = ?;");
            statement.setLong(1, id);
            statement.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(TodoItem todoItem) {
        try (var connection = dataSource.getConnection()) {
            var statement = connection.prepareStatement("UPDATE TODO_ITEM SET DESCRIPTION = ? WHERE ID = ?;");
            statement.setString(1, todoItem.getDescription());
            statement.setLong(2, todoItem.getId());
            statement.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
