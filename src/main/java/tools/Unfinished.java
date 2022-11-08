package tools;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes a method that still needs to be extended to implement
 * some additional features.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface Unfinished {
    // List parameter of strings to denote what is unfinished
    String[] value() default {};
}
