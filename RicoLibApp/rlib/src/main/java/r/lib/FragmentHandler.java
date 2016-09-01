package r.lib;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.List;

import r.lib.listener.FragmentListener;
import r.lib.ui.BaseFragment;
import r.lib.util.LogUtil;


/**
 * Created by Rico on 2015/8/4.
 */
public class FragmentHandler {

    private Context context;
    private FragmentManager fragmentManager;
    private FragmentListener fragmentListener;
    //    private String curFragment;
    private SparseArray<String> curFragmentArray = new SparseArray<>();


    public FragmentHandler(@NonNull Context context, @NonNull FragmentManager fragmentManager, FragmentListener fragmentListener) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.fragmentListener = fragmentListener;
    }

    public void addOrShowFragment(@NonNull ViewGroup container, @NonNull String tag, Class<? extends BaseFragment> fragmentClz) {

        int layoutId = container.getId();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        String curFragmentTag = curFragmentArray.get(layoutId);
        if (!TextUtils.isEmpty(curFragmentTag)) {
            Fragment old = fragmentManager.findFragmentByTag(curFragmentTag);
            if (old != null) {
                transaction.hide(old);
                container.removeView(old.getView());
            }
        }
        Fragment saved = fragmentManager.findFragmentByTag(tag);
        LogUtil.info("saved-" + saved);
        if (saved == null) {
            try {
                BaseFragment fragment = fragmentClz.newInstance();

                if (fragmentListener != null)
                    fragment.setFragmentListener(fragmentListener);

                transaction.add(layoutId, fragment, tag);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
//            container.removeView(saved.getView());
//            ((ViewGroup)saved.getView().getParent()).removeView(saved.getView());
            container.addView(saved.getView());
            transaction.show(saved);
        }
//        try {
//            transaction.commit();
//        } catch (IllegalStateException e) {
        transaction.commitAllowingStateLoss();
//        }


        curFragmentArray.put(layoutId, tag);

    }

    public void addOrShowFragment(@NonNull ViewGroup container, @NonNull String tag, BaseFragment fragment) {

        int layoutId = container.getId();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        String curFragmentTag = curFragmentArray.get(layoutId);
        if (!TextUtils.isEmpty(curFragmentTag)) {
            Fragment old = fragmentManager.findFragmentByTag(curFragmentTag);
            if (old != null) {
                transaction.hide(old);
                container.removeView(old.getView());
            }
        }
        Fragment saved = fragmentManager.findFragmentByTag(tag);
        if (saved == null) {

            if (fragmentListener != null)
                fragment.setFragmentListener(fragmentListener);

            transaction.add(layoutId, fragment, tag);
        } else {
//            ((ViewGroup)saved.getView().getParent()).removeView(saved.getView());
            container.addView(saved.getView());
            transaction.show(saved);
        }

//        try {
//            transaction.commit();
//        } catch (IllegalStateException e) {
        transaction.commitAllowingStateLoss();
//        }

        curFragmentArray.put(layoutId, tag);

    }

    public Fragment getFragment(String tag) {
        return fragmentManager.findFragmentByTag(tag);
    }

    public void hideFragment(@NonNull ViewGroup container, @NonNull String tag) {
        Fragment saved = fragmentManager.findFragmentByTag(tag);
        if (saved != null) {
            container.removeView(saved.getView());
            fragmentManager.beginTransaction().hide(saved).commitAllowingStateLoss();
            FragmentTransaction transaction = fragmentManager.beginTransaction().hide(saved);
//            try {
//                transaction.commit();
//            } catch (IllegalStateException e) {
            transaction.commitAllowingStateLoss();
            int layoutId = container.getId();
            if (tag.equals(curFragmentArray.get(layoutId)))
                curFragmentArray.remove(layoutId);
//            }
        }


    }

    public void removeFragment(@NonNull ViewGroup container, final @NonNull String tag) {
        Fragment saved = fragmentManager.findFragmentByTag(tag);
        if (saved != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction().remove(saved);
            try {
                transaction.commit();
            } catch (IllegalStateException e) {
                transaction.commitAllowingStateLoss();
            }
            container.removeView(saved.getView());
            int layoutId = container.getId();
            if (tag.equals(curFragmentArray.get(layoutId)))
                curFragmentArray.remove(layoutId);

//            }
        }
    }


    public void destroyView() {
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null && !fragments.isEmpty()) {
            LogUtil.info("销毁fragments-" + fragments);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            for (int i = 0; i < fragments.size(); i++) {
                Fragment temp = fragments.get(i);
                if (temp != null)
                    transaction.remove(temp);
            }
//            try {
//                transaction.commit();
//            } catch (IllegalStateException e) {
//                e.printStackTrace();
            transaction.commitAllowingStateLoss();
//            }
        }

    }
}
