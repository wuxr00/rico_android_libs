package r.lib.listener;


import r.lib.listener.BackKeyListener;
import r.lib.ui.widget.component.TitleView;

/**
 * Created by Rico on 2015/5/29.
 */
public interface FragmentListener {

    public void showProgress();

    public void dismissProgress();


    public void finish();

    public void onHiddenStateChanged(boolean hidden);

    //    public void goNext(BaseOAFragment fragment);
    public void setBackPressedListener(BackKeyListener backPressedListener);

    public void removeBackPressedListener();

    public TitleView getTitleView();

}
