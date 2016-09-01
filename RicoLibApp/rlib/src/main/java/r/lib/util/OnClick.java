package r.lib.util;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Rico on 15/9/19.
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface OnClick {
    int listenerIndex() default 0;
}
