package r.lib.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import r.lib.util.UIUtil;
import r.lib.util.UnProguard;

/**
 * Created by Rico on 16/8/31.
 */
public class RRViewHolder<T> extends RecyclerView.ViewHolder implements UnProguard {
    protected RRAdapter adapter;

    public RRViewHolder<T> setAdapter(RRAdapter adapter) {
        this.adapter = adapter;
        return this;
    }

    public void finish() {
        adapter = null;
    }


    public RRViewHolder(View itemView) {
        super(itemView);
        UIUtil.injectView(this, itemView);
    }

    @Override
    public void injected() {

    }

    public void onSetData(int position, T data) {

    }
}
