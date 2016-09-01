package r.lib.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;

import r.lib.BaseApplication;
import r.lib.CrushHandler;
import r.lib.ui.widget.component.TitleView;
import r.lib.util.ThreadHelperFactory;


/**
 * Created by Rico on 15/9/2.
 */
abstract public class BaseActivity extends AppCompatActivity {

    private ThreadHelperFactory.ThreadHelper threadHelper;
    private View mainView;
    private TitleView titleView;
    private BaseApplication application;
    private boolean inFront;
    protected boolean donotLoadLayout;

    public View getContentView() {
        return mainView;
    }

    public ThreadHelperFactory.ThreadHelper getThreadHelper() {
        return threadHelper;
    }

    public TitleView getTitleView() {
        return titleView;
    }

    public BaseApplication getBaseApplication() {
        return application;
    }


    public boolean isInFront() {
        return inFront;
    }

    protected void initThreadHelper() {
        threadHelper = ThreadHelperFactory.getInstance().createUIThreadHelper(this);
    }

    protected void initThreadHelper(Handler.Callback callback) {
        threadHelper = ThreadHelperFactory.getInstance().createUIThreadHelper(this, callback);
    }

    protected void initTitleView(@IdRes int titleId) {
        titleView = new TitleView(this, (Toolbar) findViewById(titleId));
    }

    protected void initTitleView(Toolbar titleBar) {
        titleView = new TitleView(this, titleBar);
    }

    abstract protected View getMainView();

    protected void onViewCreated(Bundle savedInstanceState) {

    }

    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    protected void onResume() {
        inFront = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        inFront = false;
        super.onPause();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getApplication() instanceof BaseApplication)
            application = (BaseApplication) getApplication();
        if (application != null)
            application.addActivity(this);
        if (!donotLoadLayout) {
            mainView = getMainView();
            setContentView(mainView);
            initViews(savedInstanceState);
            mainView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                private boolean inited;

                @Override
                public void onGlobalLayout() {
                    if (!inited) {
                        inited = true;
                        onViewCreated(savedInstanceState);
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (application != null)
            application.popActivity(this);
        if (threadHelper != null)
            threadHelper.shutdown();
        if (titleView != null)
            titleView.destroyView();
        application = null;
    }
}
