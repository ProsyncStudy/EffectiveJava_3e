package study.prosync.ch6;

import java.lang.annotation.*;

/**
* Indicates that the annotated method is a test method.
* Use only on parameterless static methods.
*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Item39ExceptionTest {
    Class<? extends Throwable> value();
}
