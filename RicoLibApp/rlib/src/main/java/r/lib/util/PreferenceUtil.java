package r.lib.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Map;

/**
 * Created by Rico on 2015/5/14.
 */
public class PreferenceUtil {

    public static void add(String name, Context context, String key, Object value) {
        if (TextUtils.isEmpty(key) || value == null) {
            return;
        }
        SharedPreferences.Editor editor = context.getSharedPreferences(name, Context.MODE_PRIVATE).edit();
        if (value instanceof String) {
            editor.putString(key, String.valueOf(value));
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } /*else if (value instanceof Set) {
            editor.putStringSet(key, (Set<String>) value);
        }*/
        editor.commit();
    }

    public static <T> T get(String name, Context context, String key, T def) {
        if (!TextUtils.isEmpty(key)) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
            if (def instanceof String || def == null) {
                return (T) sharedPreferences.getString(key, String.valueOf(def));
            } else if (def instanceof Boolean) {
                return (T) Boolean.valueOf(sharedPreferences.getBoolean(key, (Boolean) def));
            } else if (def instanceof Float) {
                return (T) Float.valueOf(sharedPreferences.getFloat(key, (Float) def));
            } else if (def instanceof Integer) {
                return (T) Integer.valueOf(sharedPreferences.getInt(key, (Integer) def));
            } else if (def instanceof Long) {
                return (T) Long.valueOf(sharedPreferences.getLong(key, (Long) def));
            }
             /*else if (def instanceof Set) {
            editor.putStringSet(key, (Set<String>) def);
        }*/
        }
        return def;
    }

    /*

        public static void add(String name, Context context, String key, String value) {
            context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().putString(key, value).apply();
        }


        public static void add(String name, Context context, String key, boolean value) {
            context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().putBoolean(key, value).apply();
        }

        public static void add(String name, Context context, String key, float value) {
            context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().putFloat(key, value).apply();
        }

        public static void add(String name, Context context, String key, int value) {
            context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().putInt(key, value).apply();
        }


        public static void add(String name, Context context, String key, long value) {
            context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().putLong(key, value).apply();
        }

        public static void add(String name, Context context, String key, Set<String> value) {
            context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().putStringSet(key, value).apply();
        }


        public static String get(String name, Context context, String key, String def) {
            return context.getSharedPreferences(name, Context.MODE_PRIVATE).getString(key, def);
        }


        public static boolean get(String name, Context context, String key, boolean def) {
            return context.getSharedPreferences(name, Context.MODE_PRIVATE).getBoolean(key, def);
        }

        public static float get(String name, Context context, String key, float def) {
            return context.getSharedPreferences(name, Context.MODE_PRIVATE).getFloat(key, def);
        }

        public static int get(String name, Context context, String key, int def) {
            return context.getSharedPreferences(name, Context.MODE_PRIVATE).getInt(key, def);
        }

        public static long get(String name, Context context, String key, long def) {
            return context.getSharedPreferences(name, Context.MODE_PRIVATE).getLong(key, def);
        }

        public static Set<String> get(String name, Context context, String key, Set<String> def) {
            return context.getSharedPreferences(name, Context.MODE_PRIVATE).getStringSet(key, def);
        }
    */
    public static Map<String, ?> getAll(String name, Context context) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE).getAll();
    }

    public static void clear(String name, Context context) {
        context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().clear().apply();
    }

    public static void remove(String name, Context context, String key) {
        context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().remove(key).apply();
    }

}
