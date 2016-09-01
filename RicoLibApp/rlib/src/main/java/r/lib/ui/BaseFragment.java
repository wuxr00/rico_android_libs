package r.lib.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import r.lib.listener.FragmentListener;
import r.lib.util.LogUtil;
import r.lib.util.ThreadHelperFactory;


/**
 * Created by Rico on 2015/8/4.
 */
public abstract class BaseFragment extends Fragment {


    private ThreadHelperFactory.ThreadHelper threadHelper;
    protected FragmentListener fragmentListener;

    public ThreadHelperFactory.ThreadHelper getThreadHelper() {
        return threadHelper;
    }

    public void initThreadHelper(Context context) {
        threadHelper = ThreadHelperFactory.getInstance().createUIThreadHelper(context);
    }

    public void setFragmentListener(FragmentListener fragmentListener) {
        this.fragmentListener = fragmentListener;
    }

    protected void onShow() {

    }

    protected void onHide() {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        LogUtil.info("hidden-" + getClass().getName() + " - " + hidden);
        if (hidden) {
            onHide();
        } else {
            onShow();
        }

    }


    abstract protected View getMainView(LayoutInflater inflater, ViewGroup container);


    @Nullable
    @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return getMainView(inflater, container);
    }

    @Override
    public void onDestroy() {
        if(threadHelper != null)
            threadHelper.shutdown();
        super.onDestroy();
    }
}
