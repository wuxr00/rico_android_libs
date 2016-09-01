package r.lib.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.AnimRes;
import android.support.annotation.ArrayRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.StringRes;
import android.view.animation.Animation;

/**
 * Created by Rico on 15/9/25.
 */
public class ContextUtil {

    private  static Context context;

    private  ContextUtil() {
    }

    public static void init(Context mContext){
        context = mContext;
    }

    public static Context getContext(){
        return context;
    }

    public static String getString(@StringRes int resId) {
        return context.getResources().getString(resId);
    }

    public static String[] getStringArray(@ArrayRes int resId) {
        return context.getResources().getStringArray(resId);
    }

    public static Drawable getDrawable(@DrawableRes int resId) {
        return context.getResources().getDrawable(resId);
    }


    public static int getInt(@IntegerRes int resId) {
        return context.getResources().getInteger(resId);
    }
}
