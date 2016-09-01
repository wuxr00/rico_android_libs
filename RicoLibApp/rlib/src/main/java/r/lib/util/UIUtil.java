package r.lib.util;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.support.annotation.AnimRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * 界面视图工具类
 * Created by Rico on 2015/8/12.
 */
public class UIUtil {
    private UIUtil() {
    }
//

    /**
     * 界面view注入
     *
     * @param viewHolder
     * @param mainView
     */
    public static void injectView(@NonNull UnProguard viewHolder, @NonNull View mainView) {
        Class<?> clz = viewHolder.getClass();
        try {
            do {
                Field[] fields = clz.getDeclaredFields();

                for (Field field : fields) {
                    Class type = field.getType();
                    if (View.class.isAssignableFrom(type) && Modifier.FINAL != (Modifier.FINAL & field.getModifiers())) {
                        boolean accessible = field.isAccessible();
                        field.setAccessible(true);
                        int viewId = mainView.getResources().getIdentifier(field.getName(), "id", mainView.getContext().getPackageName());
                        if (viewId > 0) {
                            View view = mainView.findViewById(viewId);
                            LogUtil.info(field.getName() + " - " + viewId + " - " + mainView.findViewById(viewId) + " - " + field.getModifiers());
                            if (view != null)
                                field.set(viewHolder, view);

                        }
                        field.setAccessible(accessible);

                    }
                   /* else if (UnProguard.class.isAssignableFrom(type)) {
                        boolean accessible = field.isAccessible();
                        field.setAccessible(true);
                        field.set(viewHolder, field.getClass().newInstance());
                        injectView((UnProguard) field.get(viewHolder), mainView);
                        field.setAccessible(accessible);
                    }*/
                }

                clz = clz.getSuperclass();
            } while (clz != null);

            viewHolder.injected();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    /**
     * 界面view注入
     *
     * @param viewHolder
     * @param mainView
     */
    public static void injectView(@NonNull UnProguard viewHolder, @NonNull View mainView, View.OnClickListener onClickListener) {
        Class<?> clz = viewHolder.getClass();
        try {
            do {
                Field[] fields = clz.getDeclaredFields();

                for (Field field : fields) {
                    Class type = field.getType();
                    if (View.class.isAssignableFrom(type) && Modifier.FINAL != (Modifier.FINAL & field.getModifiers())) {
                        boolean accessible = field.isAccessible();
                        field.setAccessible(true);
                        int viewId = mainView.getResources().getIdentifier(field.getName(), "id", mainView.getContext().getPackageName());
                        if (viewId > 0) {
                            View view = mainView.findViewById(viewId);
//                            LogUtil.info(field.getName() + " - " + viewId + " - " + mainView.findViewById(viewId));
                            if (view != null)
                                field.set(viewHolder, view);

                            OnClick onClick = field.getAnnotation(OnClick.class);
                            if (onClick != null && onClickListener != null) {
                                view.setOnClickListener(onClickListener);
                            }
                        }
                        field.setAccessible(accessible);

                    } /*else if (UnProguard.class.isAssignableFrom(type)) {
                        boolean accessible = field.isAccessible();
                        field.setAccessible(true);
                        Constructor constructor = field.getClass().getDeclaredConstructor();
                        if(constructor == null)
                            constructor = field.getClass().getConstructor();
                        field.set(viewHolder, constructor.newInstance());
                        LogUtil.info("holderfield-" + field.get(viewHolder));
                        injectView((UnProguard) field.get(viewHolder), mainView, onClickListener);
                        field.setAccessible(accessible);
                    }*/
                }

                clz = clz.getSuperclass();
            } while (clz != null);

            viewHolder.injected();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    public static void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }

    /**
     * 隐藏软键盘
     *
     * @param context
     * @param focus
     */
    public static void hideSoftInput(Context context, View focus) {
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(focus.getWindowToken(), 0);
    }

    /*
    获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {

        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }

    /**
     * 加载动画文件
     *
     * @param context
     * @param id
     * @return
     * @throws Resources.NotFoundException
     */
    public static Animation loadAnimation(Context context, @AnimRes int id)
            throws Resources.NotFoundException {

        XmlResourceParser parser = null;
        try {
            parser = context.getResources().getAnimation(id);
            return createAnimationFromXml(context, parser, null, Xml.asAttributeSet(parser));
        } catch (XmlPullParserException ex) {
            Resources.NotFoundException rnf = new Resources.NotFoundException("Can't load animation resource ID #0x" +
                    Integer.toHexString(id));
            rnf.initCause(ex);
            throw rnf;
        } catch (IOException ex) {
            Resources.NotFoundException rnf = new Resources.NotFoundException("Can't load animation resource ID #0x" +
                    Integer.toHexString(id));
            rnf.initCause(ex);
            throw rnf;
        } finally {
            if (parser != null) parser.close();
        }
    }

    /* private static Animation createAnimationFromXml(Context c, XmlPullParser parser)
             throws XmlPullParserException, IOException {

         return createAnimationFromXml(c, parser, null, Xml.asAttributeSet(parser));
     }
 */
    private static Animation createAnimationFromXml(Context c, XmlPullParser parser,
                                                    AnimationSet parent, AttributeSet attrs) throws XmlPullParserException, IOException {

        Animation anim = null;

        // Make sure we are on network start tag.
        int type;
        int depth = parser.getDepth();

        while (((type = parser.next()) != XmlPullParser.END_TAG || parser.getDepth() > depth)
                && type != XmlPullParser.END_DOCUMENT) {

            if (type != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if (name.equals("set")) {
                anim = new AnimationSet(c, attrs);
                createAnimationFromXml(c, parser, (AnimationSet) anim, attrs);
            } else if (name.equals("alpha")) {
                anim = new AlphaAnimation(c, attrs);
            } else if (name.equals("scale")) {
                anim = new ScaleAnimation(c, attrs);
            } else if (name.equals("rotate")) {
                anim = new RotateAnimation(c, attrs);
            } else if (name.equals("translate")) {
                anim = new TranslateAnimation(c, attrs);
            } else {
                try {
                    anim = (Animation) Class.forName(name).getConstructor(Context.class, AttributeSet.class).newInstance(c, attrs);
                } catch (Exception te) {
                    throw new RuntimeException("Unknown animation name: " + parser.getName() + " error:" + te.getMessage());
                }
            }

            if (parent != null) {
                parent.addAnimation(anim);
            }
        }

        return anim;

    }
}
