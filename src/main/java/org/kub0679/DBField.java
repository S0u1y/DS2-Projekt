package org.kub0679;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DBField {
    enum Strategy{
        Id,
        Normal
    }
    Strategy strategy() default Strategy.Normal;
}
