package com.todolist.controller;

import com.todolist.config.DataSourceManager;
import com.todolist.dao.TodoItemDAO;
import com.todolist.dao.impl.DataBaseTodoItemDAO;
import com.todolist.entity.TodoItem;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.awt.Toolkit;
import java.net.URL;
import java.util.ResourceBundle;

import static java.time.LocalDateTime.now;
import static javafx.collections.FXCollections.observableArrayList;

/**
 * @author Tymur Berezhnoi
 */
public class TodoListController implements Initializable {

    private ObservableList<TodoItem> todoItems = observableArrayList();

    @FXML
    public TextField input;

    private static final Toolkit TOOLKIT = Toolkit.getDefaultToolkit();

    private final TodoItemDAO taskDAO = new DataBaseTodoItemDAO(DataSourceManager.getDataSource());

    public ObservableList<TodoItem> getTodoItems() {
        return todoItems;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        var tasks = taskDAO.findAll();
        todoItems.addAll(tasks);
    }

    @FXML
    public void addTask() {
        var text = input.getText().trim();

        if(text.isEmpty() || text.isBlank()) {
            input.clear();
            TOOLKIT.beep();
            return;
        }

        var todoItem = new TodoItem(text, now());
        taskDAO.save(todoItem);
        todoItems.add(todoItem);
        input.clear();

    }
}
