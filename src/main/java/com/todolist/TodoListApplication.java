package com.todolist;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Tymur Berezhnoi
 */
public class TodoListApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        var view = FXMLLoader.load(getClass().getResource("/ui/fxml/TodoList.fxml"));

        primaryStage.setScene(new Scene((Parent) view));
        primaryStage.setTitle("DO-IT LIST");
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(500);
        primaryStage.centerOnScreen();
        primaryStage.sizeToScene();
        primaryStage.show();
    }
}
