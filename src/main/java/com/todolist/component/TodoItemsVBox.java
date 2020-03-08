package com.todolist.component;

import com.todolist.config.DataSourceManager;
import com.todolist.dao.TodoItemDAO;
import com.todolist.dao.impl.DataBaseTodoItemDAO;
import com.todolist.entity.TodoItem;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * @author Tymur Berezhnoi
 */
public class TodoItemsVBox extends VBox {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ListProperty<TodoItem> todoItems = new SimpleListProperty<>();
    private final TodoItemDAO taskDAO = new DataBaseTodoItemDAO(DataSourceManager.getDataSource());

    public TodoItemsVBox() {
        todoItems.addListener((ListChangeListener<TodoItem>) c -> rebuildView());
    }


    public ListProperty<TodoItem> todoItemsProperty() {
        return todoItems ;
    }

    public ObservableList<TodoItem> getTodoItems() {
        return todoItemsProperty().get() ;
    }

    private void rebuildView() {
        getChildren().clear();
        todoItems.stream()
                .map(this::generateViewRow)
                .forEach(getChildren()::add);
    }

    private HBox generateViewRow(TodoItem todoItem) {
        String description = todoItem.getDescription();
        Tooltip tooltip = buildTooltip(todoItem);
        Label label = buildLabel(description, tooltip);

        TextInputDialog textInputDialog = new TextInputDialog(description);
        textInputDialog.initStyle(StageStyle.UTILITY);
        textInputDialog.setTitle("New task description");
        textInputDialog.setHeaderText("");
        textInputDialog.setGraphic(null);
        textInputDialog.setResizable(true);

        FontAwesomeIconView deleteButton = new FontAwesomeIconView(FontAwesomeIcon.TRASH_ALT);

        deleteButton.setCursor(Cursor.HAND);
        deleteButton.setSize("20");
        deleteButton.setFill(Paint.valueOf("#f15b5b"));
        deleteButton.setOpacity(0.75);
        deleteButton.setOnMouseEntered(mouseEvent -> deleteButton.setOpacity(1));
        deleteButton.setOnMouseExited(mouseEvent -> deleteButton.setOpacity(0.75));

        deleteButton.setVisible(false);
        deleteButton.setOnMouseClicked(actionEvent -> {
            taskDAO.deleteById(todoItem.getId());
            todoItems.remove(todoItem);
            this.getChildren().remove(deleteButton.getParent());
        });

        FontAwesomeIconView editButton = new FontAwesomeIconView(FontAwesomeIcon.PENCIL);
        editButton.setVisible(false);
        editButton.setCursor(Cursor.HAND);
        editButton.setSize("20");
        editButton.setOnMouseClicked(actionEvent -> {
            final Optional<String> text = textInputDialog.showAndWait();
            text.ifPresent(s -> {
                TextField textField = textInputDialog.getEditor();
                final String desc = textField.getText();
                todoItem.setDescription(desc);
                taskDAO.update(todoItem);
                label.setText(desc);
                label.getTooltip().setText(desc + "\n----------------------------\nCREATED AT: " + todoItem.getCreatedAt().format(DATE_TIME_FORMATTER));
            });
        });

        Separator separator = new Separator();
        separator.setOpacity(0);

        HBox.setHgrow(label, Priority.ALWAYS);
        HBox.setHgrow(separator, Priority.ALWAYS);
        HBox.setHgrow(editButton, Priority.ALWAYS);
        HBox.setHgrow(deleteButton, Priority.ALWAYS);

        HBox hBox = buildHBox(label, separator, editButton, deleteButton);

        hBox.setOnMouseEntered(mouseEvent -> {
            deleteButton.setVisible(true);
            editButton.setVisible(true);
        });
        hBox.setOnMouseExited(mouseEvent -> {
            deleteButton.setVisible(false);
            editButton.setVisible(false);
        });

        return hBox;
    }

    private HBox buildHBox(Node... nodes) {
        HBox hBox = new HBox(nodes);
        hBox.setStyle("-fx-border-color: grey");
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setSpacing(15);
        hBox.setPadding(new Insets(5, 5, 5, 5));
        VBox.setVgrow(hBox, Priority.NEVER);
        VBox.setMargin(hBox, new Insets(5, 5, 5, 5));
        return hBox;
    }

    private Tooltip buildTooltip(TodoItem todoItem) {
        Tooltip tooltip = new Tooltip(todoItem.getDescription() + "\n----------------------------\nCREATED AT: " + todoItem.getCreatedAt().format(DATE_TIME_FORMATTER));
        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setWrapText(true);
        tooltip.setMaxWidth(500);
        return tooltip;
    }

    private Label buildLabel(String text, Tooltip tooltip) {
        Label label = new Label(text);
        label.setPrefWidth(250);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setTextOverrun(OverrunStyle.ELLIPSIS);
        label.setCursor(Cursor.HAND);
        label.setTooltip(tooltip);
        return label;
    }
}
