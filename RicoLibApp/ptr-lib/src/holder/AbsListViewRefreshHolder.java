package holder;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;

import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by Rico on 16/4/5.
 */
public class AbsListViewRefreshHolder extends SimpleRefreshHolder implements AbsListView.OnScrollListener {

    public static final int MODE_BOTH = 2;
    public static final int MODE_LOADMORE = 3;

    private AbsListView absListView;
    private boolean isBottomRefreshing;
    private boolean alreadyAtBottom;
    private View loadMoreLayout;
    private OnTopBottomRefreshListener onTopBottomRefreshListener;
    private AbsListView.OnScrollListener onScrollListener;

    public void setOnScrollListener(AbsListView.OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    private final int REFRESH_BOTTOM = 2;


    public void setOnTopBottomRefreshListener(OnTopBottomRefreshListener onTopBottomRefreshListener) {
        this.onTopBottomRefreshListener = onTopBottomRefreshListener;
        setOnRefreshListener(onTopBottomRefreshListener);
        setMode(mode);
    }

    public AbsListView getAbsListView() {
        return absListView;
    }

    public void setMode(int mode) {
        Log.i(getClass().getName(), "mode-" + mode);
        this.mode = mode;
        if (mode == MODE_LOADMORE || mode == MODE_NONE) {
//            this.ptrFrameLayout.setEnabled(false);
            if (onTopBottomRefreshListener != null)
                onTopBottomRefreshListener.setCanDoRefresh(false);
        } else {
//            this.ptrFrameLayout.setEnabled(true);
            if (onTopBottomRefreshListener != null)
                onTopBottomRefreshListener.setCanDoRefresh(true);
        }
    }

    public void setLoadMoreLayout(View loadMoreLayout) {
        this.loadMoreLayout = loadMoreLayout;
    }

    public AbsListViewRefreshHolder(final PtrFrameLayout ptrFrameLayout, AbsListView absListView) {
        super();
        this.ptrFrameLayout = ptrFrameLayout;
        this.absListView = absListView;
        this.absListView.setOnScrollListener(this);
        handler = new Handler(ptrFrameLayout.getContext().getMainLooper()) {
            @Override
            public void dispatchMessage(Message msg) {
                switch (msg.what) {
                    case REFRESH_TOP:
                        ptrFrameLayout.autoRefresh();
//                        if (onTopBottomRefreshListener != null)
//                            onTopBottomRefreshListener.onRefreshBegin(ptrFrameLayout);
                        break;
                    case REFRESH_BOTTOM:
                        loadMoreLayout.setVisibility(View.VISIBLE);
                        isBottomRefreshing = true;
                        if (onTopBottomRefreshListener != null)
                            onTopBottomRefreshListener.onBottomRefresh();
                        break;
                    case FINISH_REFRESH:
                        ptrFrameLayout.refreshComplete();
                        if ((mode == MODE_BOTH || mode == MODE_LOADMORE) && loadMoreLayout != null) {
                            isBottomRefreshing = false;
                            if (loadMoreLayout != null)
                                loadMoreLayout.setVisibility(View.GONE);
                        }
                        break;
                }
            }
        };
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (onScrollListener != null)
            onScrollListener.onScrollStateChanged(view, scrollState);
    }

    public void bottomRefresh() {
        if (isBottomRefreshing || loadMoreLayout == null)
            return;
        handler.sendEmptyMessage(REFRESH_BOTTOM);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int position = firstVisibleItem + visibleItemCount;
        if (!alreadyAtBottom && totalItemCount > 0 && visibleItemCount < totalItemCount && position == totalItemCount && mode == MODE_BOTH) {
            alreadyAtBottom = true;
            bottomRefresh();
        } else if (position != totalItemCount && mode == MODE_BOTH) {
            alreadyAtBottom = false;
        }
        if (onScrollListener != null)
            onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
//        if (position > 0) {
//            if(onTopBottomRefreshListener != null)
//                onTopBottomRefreshListener.setCanDoRefresh(false);
//        }else if (position == 0) {
//            if(onTopBottomRefreshListener != null)
//                onTopBottomRefreshListener.setCanDoRefresh(true);
//        }
    }

    public boolean isLoadingMore(){
        return isBottomRefreshing;
    }


    public static abstract class OnTopBottomRefreshListener extends OnRefreshListener {


        abstract public void onBottomRefresh();
    }
}
