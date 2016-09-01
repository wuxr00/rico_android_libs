package r.lib.adapter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import r.lib.util.UnProguard;

/**
 * Created by Rico on 16/8/31.
 */
public class ItemField implements UnProguard {

    public Method setter;
    public Field holderField;
    public Field objField;

    @Override
    public void injected() {

    }

    @Override
    public String toString() {
        return "ItemField{" +
                "setter=" + setter +
                ", holderField=" + holderField +
                ", objField=" + objField +
                '}';
    }
}