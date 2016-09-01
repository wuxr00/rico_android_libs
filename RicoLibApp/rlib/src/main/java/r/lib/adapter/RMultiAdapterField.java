package r.lib.adapter;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Rico on 16/1/19.
 */

@Target(FIELD)
@Retention(RUNTIME)
public @interface RMultiAdapterField {
    //对应数据对象字段名
    public String[] objectFields();
    public String[] setterNames();
    public Class<?>[] setterParamClzs();
}
