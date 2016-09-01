package r.lib.ui;

import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;

/**
 * 进入、退出动画效果基础activity
 * Created by Rico on 2015/8/18.
 */
public abstract class AnimBaseActivity extends BaseActivity {

    private boolean inited;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mainView = getMainView();
//        setContentView(mainView);

        if (savedInstanceState == null)//只在第一次进入执行
            getMainView().getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if (!inited) {
                        inited = true;
                        beforeStartAnimation();
                        Animation animation = getStartAnimation();
                        if (animation == null)
                            loadData();
                        else {
                            animation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    loadData();
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                            getMainView().startAnimation(animation);
                        }
                        onViewPreDraw();
                    }
                    return true;
                }
            });
    }

    @Override
    public void finish() {
        Animation animation = getFinishAnimation();
        if (animation == null)
            super.finish();
        else {
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    AnimBaseActivity.super.finish();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            getMainView().startAnimation(animation);
        }
    }


    /**
     * 获取开始动画
     *
     * @return
     */
    abstract protected Animation getStartAnimation();

    /**
     * 获取结束动画
     *
     * @return
     */
    abstract protected Animation getFinishAnimation();
//
//    /**
//     * 获取布局根视图
//     *
//     * @return
//     */
//    abstract protected View getMainView();


    /**
     * 动画/界面执行完毕后加载数据
     *
     * @return
     */
    abstract protected void loadData();

    /**
     * view绘制完时，用于子类获取视图的尺寸等属性
     */
    protected void onViewPreDraw() {

    }

    /**
     * 动画开始前执行
     */
    protected void beforeStartAnimation() {

    }

}
