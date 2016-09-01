package r.lib.ui.widget.component;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import r.lib.R;


/**
 * Created by Rico on 2015/8/4.
 */
public class TitleView {

    protected AppCompatActivity context;
    protected ActionBar actionBar;
    protected Toolbar toolbar;
    protected ActionBarDrawerToggle drawerToggle;
    protected DrawerLayout drawerLayout;
    protected List<View> customViews = new ArrayList<>();
    protected OnTitleViewHiddenChangedListener onTitleViewHiddenChangedListener;

    protected boolean isDrawerToggleShown;

    public void setOnTitleViewHiddenChangedListener(OnTitleViewHiddenChangedListener onTitleViewHiddenChangedListener) {
        this.onTitleViewHiddenChangedListener = onTitleViewHiddenChangedListener;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public TitleView(@NonNull AppCompatActivity context, Toolbar toolbar) {
        this.context = context;
        this.toolbar = toolbar;
        this.toolbar.setTitle("");
        context.setSupportActionBar(toolbar);
        this.actionBar = context.getSupportActionBar();

    }

    public TitleView setContentInsetsRelative(int leftPadding, int rightPadding) {
        int rightSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightPadding, context.getResources().getDisplayMetrics());
        int leftSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftPadding, context.getResources().getDisplayMetrics());
        toolbar.setContentInsetsRelative(leftSize, rightSize);
        return this;
    }


    public void showDrawerToggle(DrawerLayout drawerLayout) {
        if (!isDrawerToggleShown) {
            isDrawerToggleShown = true;
            this.drawerLayout = drawerLayout;
            drawerToggle = new ActionBarDrawerToggle(context, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
            drawerToggle.syncState();
            drawerLayout.setDrawerListener(drawerToggle);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    public void hideDrawerToggle() {
        isDrawerToggleShown = false;
        toolbar.setNavigationIcon(null);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void clearCustomViews() {
        if (!customViews.isEmpty())
            for (Iterator<View> it = customViews.iterator(); it.hasNext(); ) {
                toolbar.removeView(it.next());
                it.remove();
            }

        customViews = null;
    }

    public void addView(View view) {
        customViews.add(view);
        toolbar.addView(view);
    }

    public void addView(View view, Toolbar.LayoutParams lp) {
        customViews.add(view);
        toolbar.addView(view, lp);
    }

    public void removeView(View view) {
        customViews.remove(view);
        toolbar.removeView(view);
    }

    public void setTitleText(CharSequence text) {
        toolbar.setTitle(text);
    }

    public void setTitleText(@StringRes int text) {
        toolbar.setTitle(text);
    }

/*
    public void setTitle(CharSequence title) {
        textView.setText(title);
    }

    public void setTitle(@StringRes int title) {
        textView.setText(title);
    }*/

    public void showNavigationView(@DrawableRes int drawableRes, View.OnClickListener onClickListener) {

        toolbar.setNavigationIcon(drawableRes);
        toolbar.setNavigationOnClickListener(onClickListener);
    }

    public void showNavigationView(Drawable drawable, View.OnClickListener onClickListener) {

        toolbar.setNavigationIcon(drawable);
        toolbar.setNavigationOnClickListener(onClickListener);
    }

    public void hideNavigationView() {
        if (isDrawerToggleShown)
            isDrawerToggleShown = false;
        toolbar.setNavigationIcon(null);
        toolbar.setNavigationOnClickListener(null);
    }


    public void hideTitleView() {
        if (actionBar.isShowing()) {
            actionBar.hide();
            if (onTitleViewHiddenChangedListener != null)
                onTitleViewHiddenChangedListener.onHidden();
        }
    }

    public void showTitleView() {
        if (!actionBar.isShowing()) {
            actionBar.show();
            if (onTitleViewHiddenChangedListener != null)
                onTitleViewHiddenChangedListener.onShown();
        }
    }

    public void destroyView() {
        clearCustomViews();
        context = null;
        actionBar = null;
        hideNavigationView();
    }

    public interface OnTitleViewHiddenChangedListener {
        public void onHidden();

        public void onShown();
    }
}
