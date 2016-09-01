package r.lib.util;

import android.content.Context;
import android.util.Log;

/**
 * Created by Rico on 2015/6/17.
 */
public class LogUtil {
    private static String TAG = "appLog";
    private static boolean logable;

    public static void allowLog(Context context, boolean allowLog) {
        TAG = context.getPackageName();
        logable = allowLog;
    }

    public static void info(String tag, String info) {
        if (logable) {
            Log.i(tag, info);
        }
    }

    public static void debug(String tag, String debug) {
        if (logable) {
            Log.d(tag, debug);
        }
    }

    public static void err(String tag, String err) {
        if (logable) {
            Log.e(tag, err);
        }
    }

    public static void info(String info) {
        if (logable) {
            Log.i(TAG, info);
        }
    }

    public static void debug(String debug) {
        if (logable) {
            Log.d(TAG, debug);
        }
    }

    public static void err(String err) {
        if (logable) {
            Log.e(TAG, err);
        }
    }
}
