package com.todolist.template;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Tymur Berezhnoi
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface FXMLTemplate {
    String value();
}
