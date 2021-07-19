package com.todolist.template;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * @author Tymur Berezhnoi
 */
public interface TemplateLoader {
    default <T extends Parent> void loadFXML(T component) {
        var clazz = component.getClass();
        if (!clazz.isAnnotationPresent(FXMLTemplate.class)) {
            throw new RuntimeException("Component: " + clazz.getSimpleName() + " has no annotation: " + FXMLTemplate.class.getSimpleName());
        }

        var fxmlTemplate = clazz.getAnnotation(FXMLTemplate.class);
        if(fxmlTemplate.value().isEmpty() || fxmlTemplate.value().isBlank()) {
            throw new RuntimeException("Annotation: " + FXMLTemplate.class.getSimpleName() + " has empty value");
        }

        FXMLLoader loader = new FXMLLoader();
        loader.setRoot(component);
        loader.setControllerFactory(theClass -> component);

        var fileName = fxmlTemplate.value();
        try {
            loader.load(component.getClass().getResourceAsStream(fileName));
        } catch(Exception e) {
            throw new RuntimeException("Can't load FXML file: " + fileName);
        }
    }
}
