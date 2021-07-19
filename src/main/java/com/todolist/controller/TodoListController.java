package com.todolist.controller;

import com.todolist.config.DataSourceManager;
import com.todolist.dao.TodoItemDAO;
import com.todolist.dao.impl.DataBaseTodoItemDAO;
import com.todolist.entity.TodoItem;
import com.todolist.template.TodoItemTemplate;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import static java.time.LocalDateTime.now;
import static javafx.scene.layout.Priority.NEVER;

/**
 * @author Tymur Berezhnoi
 */
public class TodoListController implements Initializable {

    private static final Toolkit TOOLKIT = Toolkit.getDefaultToolkit();

    private final TodoItemDAO taskDAO = new DataBaseTodoItemDAO(DataSourceManager.getDataSource());

    @FXML
    private TextField input;

    @FXML
    private VBox vBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        taskDAO.findAll().forEach(this::addToVBox);
    }

    @FXML
    private void addTask() {
        var text = input.getText().trim();

        if(text.isEmpty() || text.isBlank()) {
            input.clear();
            TOOLKIT.beep();
            return;
        }

        var todoItem = new TodoItem(text, now());
        taskDAO.save(todoItem);
        addToVBox(todoItem);

        input.clear();
    }

    private void addToVBox(TodoItem todoItem) {
        Consumer<TodoItemTemplate> onDelete = template -> {
            taskDAO.deleteById(todoItem.getId());
            vBox.getChildren().remove(template);
        };

        var todoItemTemplate = new TodoItemTemplate(todoItem, onDelete, taskDAO::update);

        VBox.setVgrow(todoItemTemplate, NEVER);
        VBox.setMargin(todoItemTemplate, new Insets(5, 5, 5, 5));
        vBox.getChildren().add(todoItemTemplate);
    }
}
