package r.lib.network;

import android.text.TextUtils;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Rico on 2015/8/19.
 */
public class RequestUtil {
    protected static RequestUtil util;

    protected String serverUrl;

    protected RequestUtil() {

    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getServerUrl() {
        return serverUrl;
    }


    public Map<String, String> getHeader(RequestEntity requestEntity) {
        Map<String, String> result = new HashMap<>();
        if (requestEntity != null) {
            Field[] fields = requestEntity.getClass().getDeclaredFields();
            try {

                for (Field field :
                        fields) {
                    if (field.getName().startsWith("this$"))
                        continue;

                    int annonCount = field.getAnnotations().length;
                    Annotation headerAnnon = annonCount == 0 ? null : field.getAnnotation(RequestHeader.class);
                    if (headerAnnon == null)
                        continue;

                    String key = null;
                    if (headerAnnon != null) {
                        key = ((RequestHeader) headerAnnon).key();
                    }
                    key = TextUtils.isEmpty(key) ? field.getName() : key;

                    result.put(key, parseValue(field.get(requestEntity), field.getType()));

//                    LogUtil.info("请求-" + key + "-" + parseValue(field.get(requestEntity), field.getType()));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public Map<String, String> genMapParams(RequestEntity requestEntity) {
        Map<String, String> result = new HashMap<>();
        if (requestEntity != null) {
            parseMapParams(requestEntity, requestEntity.getClass(), result);
        }
        return result;
    }

    protected void parseMapParams(RequestEntity requestEntity, Class<?> clz, Map<String, String> result) {
        Field[] fields = clz.getDeclaredFields();
        try {

            for (Field field :
                    fields) {
                KeyVal keyVal = parseReuqestField(field, requestEntity);
                if (keyVal.valid)
                    result.put(keyVal.key, keyVal.value);
//                LogUtil.info("请求-" + key + "-" + parseValue(field.get(requestEntity), field.getType()));

            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Class<?> superClz = clz.getSuperclass();
        if (superClz != null)
            parseMapParams(requestEntity, superClz, result);
    }


    public CharSequence genGetterParams(RequestEntity requestEntity) {
        StringBuilder result = new StringBuilder();
        if (requestEntity != null) {
            parseGetterParams(requestEntity, requestEntity.getClass(), result, true);
        }
        return result;
    }

    protected void parseGetterParams(RequestEntity requestEntity, Class<?> clz, StringBuilder result, boolean first) {
        Field[] fields = clz.getDeclaredFields();
        try {
            for (Field field :
                    fields) {
                KeyVal keyVal = parseReuqestField(field, requestEntity);
                if (keyVal.valid)
                    if (first) {
                        first = false;
                        result.append(getGetter(keyVal.key, keyVal.value, "?"));
                    } else {
                        result.append(getGetter(keyVal.key, keyVal.value, "&"));
                    }
//                LogUtil.info("请求-"+key+"-"+parseValue(field.get(requestEntity), field.getType()));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Class<?> superClz = clz.getSuperclass();
        if (superClz != null)
            parseGetterParams(requestEntity, superClz, result, first);
    }


    public CharSequence getGetter(String key, Object value, String prefix) {
        return new StringBuilder(prefix).append(key).append("=").append(value);
    }

    protected synchronized KeyVal parseReuqestField(Field field, Object fieldObj) throws IllegalAccessException {
        KeyVal keyVal = new KeyVal();

        if (field.getName().startsWith("this$")) {
            keyVal.valid = false;
            return keyVal;
        }

//                    Field field = fields[i];
        field.setAccessible(true);
        int annonCount = field.getAnnotations().length;
        Annotation headerAnnon = annonCount == 0 ? null : field.getAnnotation(RequestHeader.class);
        Annotation passAnnon = annonCount == 0 ? null : field.getAnnotation(NotRequestParam.class);
        Annotation bodyAnnon = annonCount == 0 ? null : field.getAnnotation(RequestBody.class);
        if (headerAnnon != null || (annonCount == 0 && Modifier.isTransient(field.getModifiers())) || passAnnon != null || bodyAnnon != null)//是请求头或者没有注解并被transient修饰或者被标注不是请求参数或是添加请求体则跳过该字段
        {
            keyVal.valid = false;
            return keyVal;
        }

        RequestParam paramAnnon = annonCount == 0 ? null : field.getAnnotation(RequestParam.class);
        String key = null;
        if (paramAnnon != null)

        {
            key = paramAnnon.key();
        }
        keyVal.key = TextUtils.isEmpty(key) ? field.getName() : key;
        keyVal.value = parseValue(field.get(fieldObj), field.getType());
        return keyVal;
    }

    public String getUrl(CharSequence uri) {
//        if (TextUtils.isEmpty(this.serverUrl))
//            this.serverUrl = Constants.SERVER;
        return new StringBuilder(this.serverUrl).append(uri).toString();
    }

    public static boolean strIsNull(String string) {
        return TextUtils.isEmpty(string) || "null".equals(string) || "NULL".equals(string);
    }

    protected String parseValue(Object value, Class<?> type) {
//        LogUtil.info("解析值－"+value);
        String result = null;
        if (Character.TYPE.isAssignableFrom(type))
            result = value == null ? "" : Character.toString((Character) value);
        else if (Integer.TYPE.isAssignableFrom(type))
            result = value == null ? "0" : Integer.toString((Integer) value);
        else if (Boolean.TYPE.isAssignableFrom(type))
            result = value == null ? "false" : Boolean.toString((Boolean) value);
        else if (String.class.isAssignableFrom(type))
            result = value == null ? "" : value.toString();
        else if (Long.TYPE.isAssignableFrom(type))
            result = value == null ? "0" : Long.toString((Long) value);
        else if (Double.TYPE.isAssignableFrom(type))
            result = value == null ? "0.0" : Double.toString((Double) value);
        else if (Float.TYPE.isAssignableFrom(type))
            result = value == null ? "0.0" : Float.toString((Float) value);
        else if (Short.TYPE.isAssignableFrom(type))
            result = value == null ? "0" : Short.toString((Short) value);
        else
            result = value == null ? null : value.toString();
        return result;
    }

    protected class KeyVal {
        public boolean valid;
        public String key;
        public String value;
    }
}
