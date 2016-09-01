package r.lib.network;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Rico on 2015/8/19.
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface RequestBody {
    String key() default "";
}
