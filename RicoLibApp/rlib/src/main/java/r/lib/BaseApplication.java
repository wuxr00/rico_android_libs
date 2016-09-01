package r.lib;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import r.lib.ui.BaseActivity;
import r.lib.util.ContextUtil;
import r.lib.util.LogUtil;
import r.lib.util.PreferenceUtil;

/**
 * Created by Rico on 15/9/2.
 */
public class BaseApplication extends Application {
    public boolean debugMode;

    private List<BaseActivity> runActivities = new ArrayList<>();

    public void addActivity(BaseActivity activity) {
        runActivities.add(activity);
    }

    public void popActivity(BaseActivity activity) {
        if (runActivities.size() > 0)
            runActivities.remove(activity);
    }

    public List<BaseActivity> getRunActivities() {
        return runActivities;
    }

    public void exit() {
        for (Iterator<BaseActivity> iterator = runActivities.iterator(); iterator.hasNext(); ) {
            iterator.next().finish();
//            iterator.remove();
        }

        runActivities.clear();
     /*
        while (runActivities.size() > 0) {
            runActivities.pop().finish();
        }*/
    }

    public boolean isAppInFront() {
        int size = runActivities.size();
        for (int i = 0; i < size; i++) {
            if (runActivities.get(i).isInFront())
                return true;
        }
        return false;
    }

    public boolean isUIRunning(){
        return !runActivities.isEmpty();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ContextUtil.init(this);
        CrushHandler.getInstance().init(this);
//        init();
    }

    public void init() {
        RCallbackHanlder.init(this);

//        int inited = PreferenceUtil.get(Constants.SHARED_CONFIGURE, this, Constants.SHARED_KEY_SCREEN_WIDTH, -1);
//        if (inited <= 0) {
//
//            DisplayMetrics dm = new DisplayMetrics();
//            ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
//
//            PreferenceUtil.add(Constants.SHARED_CONFIGURE, this, Constants.SHARED_KEY_SCREEN_WIDTH, dm.widthPixels);
//            PreferenceUtil.add(Constants.SHARED_CONFIGURE, this, Constants.SHARED_KEY_SCREEN_HEIGHT, dm.heightPixels);
//
//        }
    }
}
