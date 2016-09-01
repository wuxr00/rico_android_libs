package r.ricolibapp;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.DefaultPtrHeader;
import r.lib.adapter.RAdapterField;
import r.lib.adapter.RMultiAdapterField;
import r.lib.adapter.RRAdapter;
import r.lib.adapter.RRViewHolder;
import r.lib.ui.BaseActivity;
import r.lib.util.LogUtil;
import r.lib.util.UIUtil;
import r.lib.util.UnProguard;

/**
 * Created by Rico on 16/8/29.
 */
public class TestRecycleViewActivity extends BaseActivity implements UnProguard {

    private List<TestObj1> datas = new ArrayList<>();
    private MyAdapter adapter;
    private RRAdapter<TestObj1> testObj1RRAdapter;
    private PtrFrameLayout ptrFL;
    private RecyclerView recyclerV;

    @Override
    protected View getMainView() {
        return getLayoutInflater().inflate(R.layout.view_refresh_recycleview, null);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        super.initViews(savedInstanceState);
        LogUtil.allowLog(this, true);
        LogUtil.info("打印");
        UIUtil.injectView(this, getContentView());
        ptrFL.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
//                testObj1RRAdapter.clearData();
                new Thread() {
                    @Override
                    public void run() {
//                        testObj1RRAdapter.setNotifyAfterChange(false);
                        char[] arr = new StringBuilder(UUID.randomUUID().toString())
                                .append(UUID.randomUUID().toString())
                                .toString().toCharArray();
                        for (char c :
                                arr) {
//                            datas.add(Character.toString(c));
                            TestObj1 data = new TestObj1();
                            data.content = Character.toString(c);
                            data.color = c % 2 == 0 ? Color.RED : Color.BLUE;
                            data.select = c % 3 == 0;
                            datas.add(data);
//                            testObj1RRAdapter.addData(data);
                        }
//                        testObj1RRAdapter.setNotifyAfterChange(true);
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ptrFL.refreshComplete();
//                                testObj1RRAdapter.notifyDataSetChanged();
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                }.start();
            }
        });

        DefaultPtrHeader defaultPtrHeader = new DefaultPtrHeader(this);
        ptrFL.setHeaderView(defaultPtrHeader);
        ptrFL.addPtrUIHandler(defaultPtrHeader);
        ptrFL.setLoadingMinTime(500);


//        testObj1RRAdapter = new RRAdapter<>(this);
//        testObj1RRAdapter.setClz(TestViewHolder.class, TestObj1.class, this);
//        testObj1RRAdapter.setLayoutRes(R.layout.listitem_test);
//        testObj1RRAdapter.setData(new ArrayList<TestObj1>());
//        recyclerV.setAdapter(testObj1RRAdapter);

        final Paint linePaint = new Paint();
        int[] ATTRS = new int[]{
                android.R.attr.listDivider
        };
        final TypedArray a = obtainStyledAttributes(ATTRS);
        final Drawable mDivider = a.getDrawable(0);
        a.recycle();
        linePaint.setColor(Color.GREEN);

        adapter = new MyAdapter();
        recyclerV.setAdapter(adapter);
        recyclerV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerV.addItemDecoration(new RecyclerView.ItemDecoration() {

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                final int left = parent.getPaddingLeft();
                final int right = parent.getWidth() - parent.getPaddingRight();

                final int childCount = parent.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    final View child = parent.getChildAt(i);
                    android.support.v7.widget.RecyclerView v = new android.support.v7.widget.RecyclerView(parent.getContext());
                    final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                            .getLayoutParams();
                    final int top = child.getBottom() + params.bottomMargin;
                    final int bottom = top + 10;
//                    mDivider.setBounds(left, top, right, bottom);
//                    mDivider.draw(c);
                    c.drawRect(left, top, right, bottom, linePaint);
                }
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0, 0, 0, 20);
            }

            @Override
            public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDrawOver(c, parent, state);
            }
        });
    }

    @Override
    public void injected() {

    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            LogUtil.info("创建viewholder: " + viewType);
            return new MyViewHolder(getLayoutInflater().inflate(R.layout.listitem_test, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            long start = System.currentTimeMillis();
            TestObj1 data = datas.get(position);
            holder.test1TV.setText(data.content);
            holder.testTV.setText(data.content);
            holder.testTV.setChecked(data.select);
            holder.testTV.setTextColor(data.color);
            LogUtil.info("耗时：", (System.currentTimeMillis() - start) + "");
//            holder.testTV.setText(datas.get(position));
//            LogUtil.info("onbind", holder.hashCode() + " - " + position);
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder implements UnProguard {

        private CheckBox testTV;
        private TextView test1TV;

        public MyViewHolder(View itemView) {
            super(itemView);
            UIUtil.injectView(this, itemView);
        }


        @Override
        public void injected() {

        }
    }

    private class TestViewHolder extends RRViewHolder<TestObj1> {

        @RMultiAdapterField(objectFields = {"color", "content", "select"}
                , setterNames = {"setTextColor", "setText", "setChecked"}
                , setterParamClzs = {int.class, CharSequence.class, boolean.class})
        private CheckBox testTV;
        @RAdapterField(objectField = "content", setterName = "setText", setterParamClz = CharSequence.class)
        private TextView test1TV;

        public TestViewHolder(View itemView) {
            super(itemView);
        }
    }
}
