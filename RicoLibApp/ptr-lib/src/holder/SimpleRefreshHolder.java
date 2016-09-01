package holder;

import android.os.Handler;
import android.os.Message;
import android.view.View;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.DefaultPtrHeader;

/**
 * Created by Rico on 16/4/7.
 */
public class SimpleRefreshHolder {


    public static final int MODE_PULLDOWN = 1;
    public static final int MODE_NONE = 4;


    protected PtrFrameLayout ptrFrameLayout;
    protected int mode;
    protected OnRefreshListener onRefreshListener;

    protected final int REFRESH_TOP = 1;
    protected final int FINISH_REFRESH = 3;


    protected Handler handler;

    public PtrFrameLayout getPtrFrameLayout() {
        return ptrFrameLayout;
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
        this.ptrFrameLayout.setPtrHandler(onRefreshListener);
        setMode(mode);
    }

    public void setDefaultHeader() {
        DefaultPtrHeader defaultPtrHeader = new DefaultPtrHeader(ptrFrameLayout.getContext());
        ptrFrameLayout.setHeaderView(defaultPtrHeader);
        ptrFrameLayout.addPtrUIHandler(defaultPtrHeader);
        ptrFrameLayout.setLoadingMinTime(1000);
    }


    public void setMode(int mode) {
        this.mode = mode;
        if (mode == MODE_NONE) {
            this.ptrFrameLayout.setEnabled(false);
            if (onRefreshListener != null)
                onRefreshListener.setCanDoRefresh(false);
        } else {
            this.ptrFrameLayout.setEnabled(true);
            if (onRefreshListener != null)
                onRefreshListener.setCanDoRefresh(true);
        }
    }

    protected SimpleRefreshHolder() {
    }

    public SimpleRefreshHolder(final PtrFrameLayout ptrFrameLayout) {
        this.ptrFrameLayout = ptrFrameLayout;
        handler = new Handler(ptrFrameLayout.getContext().getMainLooper()) {
            @Override
            public void dispatchMessage(Message msg) {
                switch (msg.what) {
                    case REFRESH_TOP:
                        ptrFrameLayout.autoRefresh();
//                        if (onRefreshListener != null)
//                            onRefreshListener.onRefreshBegin(ptrFrameLayout);
                        break;
                    case FINISH_REFRESH:
                        ptrFrameLayout.refreshComplete();
                        break;
                }
            }
        };
    }

    public boolean isRefreshing(){
        return ptrFrameLayout.isRefreshing();
    }

    public void topRefresh() {
        if (ptrFrameLayout.isRefreshing())
            return;
        handler.sendEmptyMessage(REFRESH_TOP);
    }

    public void complete() {
        handler.sendEmptyMessage(FINISH_REFRESH);
    }

    public static abstract class OnRefreshListener implements PtrHandler {

        protected boolean canDoRefresh;

        protected void setCanDoRefresh(boolean candoRefresh) {
            this.canDoRefresh = candoRefresh;
        }

        @Override
        public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
            return canDoRefresh ? PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header) : false;
        }

//        abstract public void onBottomRefresh();
    }
}
