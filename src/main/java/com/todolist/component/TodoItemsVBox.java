package com.todolist.component;

import com.todolist.dao.TodoItemDAO;
import com.todolist.dao.impl.DataBaseTodoItemDAO;
import com.todolist.entity.TodoItem;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;

import java.time.format.DateTimeFormatter;

import static com.todolist.config.DataSourceManager.getDataSource;
import static de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon.PENCIL;
import static de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon.TRASH_ALT;
import static java.lang.Double.MAX_VALUE;
import static java.time.format.DateTimeFormatter.ofPattern;
import static javafx.geometry.Pos.CENTER_LEFT;
import static javafx.scene.Cursor.HAND;
import static javafx.scene.control.OverrunStyle.ELLIPSIS;
import static javafx.scene.layout.Priority.ALWAYS;
import static javafx.scene.layout.Priority.NEVER;
import static javafx.stage.StageStyle.UTILITY;
import static javafx.util.Duration.ZERO;

/**
 * @author Tymur Berezhnoi
 */
public class TodoItemsVBox extends VBox {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ListProperty<TodoItem> todoItems = new SimpleListProperty<>();
    private final TodoItemDAO taskDAO = new DataBaseTodoItemDAO(getDataSource());

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
        var description = todoItem.getDescription();
        var tooltip = buildTooltip(todoItem);
        var label = buildLabel(description, tooltip);

        var textInputDialog = new TextInputDialog(description);
        textInputDialog.initStyle(UTILITY);
        textInputDialog.setTitle("New task description");
        textInputDialog.setHeaderText("");
        textInputDialog.setGraphic(null);
        textInputDialog.setResizable(true);

        var deleteButton = new FontAwesomeIconView(TRASH_ALT);

        deleteButton.setCursor(HAND);
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

        var editButton = new FontAwesomeIconView(PENCIL);
        editButton.setVisible(false);
        editButton.setCursor(HAND);
        editButton.setSize("20");
        editButton.setOnMouseClicked(actionEvent -> {
            var text = textInputDialog.showAndWait();
            text.ifPresent(s -> {
                var textField = textInputDialog.getEditor();
                final var desc = textField.getText();
                todoItem.setDescription(desc);
                taskDAO.update(todoItem);
                label.setText(desc);
                label.getTooltip().setText(desc + "\n----------------------------\nCREATED AT: " + todoItem.getCreatedAt().format(DATE_TIME_FORMATTER));
            });
        });

        var separator = new Separator();
        separator.setOpacity(0);

        HBox.setHgrow(label, ALWAYS);
        HBox.setHgrow(separator, ALWAYS);
        HBox.setHgrow(editButton, ALWAYS);
        HBox.setHgrow(deleteButton, ALWAYS);

        var hBox = buildHBox(label, separator, editButton, deleteButton);

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
        var hBox = new HBox(nodes);
        hBox.setStyle("-fx-border-color: grey");
        hBox.setAlignment(CENTER_LEFT);
        hBox.setSpacing(15);
        hBox.setPadding(new Insets(5, 5, 5, 5));
        VBox.setVgrow(hBox, NEVER);
        VBox.setMargin(hBox, new Insets(5, 5, 5, 5));
        return hBox;
    }

    private Tooltip buildTooltip(TodoItem todoItem) {
        var tooltip = new Tooltip(todoItem.getDescription() + "\n----------------------------\nCREATED AT: " + todoItem.getCreatedAt().format(DATE_TIME_FORMATTER));
        tooltip.setShowDelay(ZERO);
        tooltip.setWrapText(true);
        tooltip.setMaxWidth(500);
        return tooltip;
    }

    private Label buildLabel(String text, Tooltip tooltip) {
        var label = new Label(text);
        label.setPrefWidth(250);
        label.setMaxWidth(MAX_VALUE);
        label.setTextOverrun(ELLIPSIS);
        label.setCursor(HAND);
        label.setTooltip(tooltip);
        return label;
    }
}
