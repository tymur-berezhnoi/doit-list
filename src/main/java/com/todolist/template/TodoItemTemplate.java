package com.todolist.template;

import com.todolist.entity.TodoItem;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Objects.requireNonNull;
import static javafx.stage.StageStyle.UTILITY;
import static javafx.util.Duration.ZERO;

/**
 * @author Tymur Berezhnoi
 */
@FXMLTemplate("/ui/fxml/template/TodoItemTemplate.fxml")
public class TodoItemTemplate extends HBox implements Initializable, TemplateLoader {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    private Label label;

    @FXML
    private FontAwesomeIconView pencil;

    @FXML
    private FontAwesomeIconView deleteButton;

    private final TodoItem todoItem;

    private final Consumer<TodoItemTemplate> onDelete;
    private final Consumer<TodoItem> onUpdate;

    public TodoItemTemplate(TodoItem todoItem, Consumer<TodoItemTemplate> onDelete, Consumer<TodoItem> onUpdate) {
        this.todoItem = requireNonNull(todoItem);
        this.onDelete = requireNonNull(onDelete);
        this.onUpdate = requireNonNull(onUpdate);

        loadFXML(this);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        label.setText(todoItem.getDescription());
        label.setTooltip(buildTooltip(todoItem));

        deleteButton.setOnMouseEntered(mouseEvent -> deleteButton.setOpacity(1));
        deleteButton.setOnMouseExited(mouseEvent -> deleteButton.setOpacity(0.75));

        deleteButton.setOnMouseClicked(actionEvent -> onDelete.accept(this));

        setOnMouseEntered(mouseEvent -> {
            pencil.setVisible(true);
            deleteButton.setVisible(true);
        });

        setOnMouseExited(mouseEvent -> {
            pencil.setVisible(false);
            deleteButton.setVisible(false);
        });

        pencil.setOnMouseClicked(mouseEvent -> onPencilClicked());
    }

    private void onPencilClicked() {
        var textInputDialog = new TextInputDialog(todoItem.getDescription());
        textInputDialog.initStyle(UTILITY);
        textInputDialog.setTitle("New task description");
        textInputDialog.setHeaderText("");
        textInputDialog.setGraphic(null);
        textInputDialog.setResizable(true);

        var text = textInputDialog.showAndWait();
        text.ifPresent(s -> {
            var textField = textInputDialog.getEditor();
            var desc = textField.getText();
            todoItem.setDescription(desc);
            onUpdate.accept(todoItem);
            label.setText(desc);
            label.getTooltip().setText(desc + "\n----------------------------\nCREATED AT: " + todoItem.getCreatedAt().format(DATE_TIME_FORMATTER));
        });
    }

    private Tooltip buildTooltip(TodoItem todoItem) {
        var tooltip = new Tooltip(todoItem.getDescription() + "\n----------------------------\nCREATED AT: " + todoItem.getCreatedAt().format(DATE_TIME_FORMATTER));
        tooltip.setShowDelay(ZERO);
        tooltip.setWrapText(true);
        tooltip.setMaxWidth(500);
        return tooltip;
    }
}
