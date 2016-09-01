package r.lib.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import r.lib.util.LogUtil;

/**
 * Created by Rico on 16/8/31.
 */
public class RRAdapter<T> extends RecyclerView.Adapter<RRViewHolder<T>> {

    private Context context;
    private List<T> dataList;
    private boolean notifyAfterChange = true;
    private boolean autoSetData = true;
    private boolean innerHolderClz;
    private int layoutRes;
    private Object outterObj;//用于内部类
    private ArrayList<ItemField> itemFields = new ArrayList<>();
    private Constructor holderConstructor;
    private ArrayList<RRViewHolder> holderList = new ArrayList<>();

    public RRAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RRViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutRes <= 0)
            return new RRViewHolder<>(new View(context));
        View itemV = LayoutInflater.from(context).inflate(layoutRes, parent, false);
        try {
            return ((RRViewHolder) (innerHolderClz ? holderConstructor.newInstance(outterObj, itemV) : holderConstructor.newInstance(itemV)))
                    .setAdapter(this);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return new RRViewHolder<>(new View(context));
    }

    @Override
    public void onBindViewHolder(RRViewHolder<T> holder, int position) {
        T data = dataList.get(position);
        if (autoSetData) {
            long start = System.currentTimeMillis();
            int size = itemFields.size();
            try {
                for (int i = 0; i < size; i++) {
                    ItemField itemField = itemFields.get(i);
                    itemField.setter.invoke(itemField.holderField.get(holder), itemField.objField.get(data));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            LogUtil.info("耗时：", (System.currentTimeMillis() - start) + "");
        }
        holder.onSetData(position, data);
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }


    private void clear() {
        notifyAfterChange = false;
        itemFields.clear();
        context = null;
        outterObj = null;
        int size = holderList.size();
        for (int i = 0; i < size; i++) {
            holderList.get(i).finish();
        }
        holderList.clear();
    }

    public void finish(boolean clearData) {
        if (clearData) {
            clearData();
        }
        clear();
    }


    public void setAutoSetData(boolean autoSetData) {
        this.autoSetData = autoSetData;
    }

    public void setClz(Class<? extends RRViewHolder> viewHolderClz, Class<T> objClz, Object outterObj) {
        this.outterObj = outterObj;
        LogUtil.debug("RRAdapter", Arrays.toString(viewHolderClz.getDeclaredConstructors()));
        try {
            holderConstructor = viewHolderClz.getDeclaredConstructor(View.class);

        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
            try {
                holderConstructor = viewHolderClz.getDeclaredConstructor(outterObj.getClass(), View.class);
                innerHolderClz = true;
            } catch (NoSuchMethodException e1) {
                e1.printStackTrace();
            }
        }

        holderConstructor.setAccessible(true);

        Field[] holderFields = viewHolderClz.getDeclaredFields();
        long start = System.currentTimeMillis();
        for (Field holderField :
                holderFields) {
//                LogUtil.debug("RRAdapter getclzz holderMethod", Arrays.toString(holderField.getType().getDeclaredMethods()));
            RAdapterField fieldAnno = holderField.getAnnotation(RAdapterField.class);
            RMultiAdapterField multiAnno = holderField.getAnnotation(RMultiAdapterField.class);
            if (fieldAnno != null) {
                String objFieldStr = fieldAnno.objectField();
                try {
                    Field objField = TextUtils.isEmpty(objFieldStr) ? null : objClz.getDeclaredField(objFieldStr);

                    objField.setAccessible(true);
                    Method setter = holderField.getType().getMethod(fieldAnno.setterName(), fieldAnno.setterParamClz());
                    holderField.setAccessible(true);
                    ItemField itemField = new ItemField();
                    itemField.objField = objField;
                    itemField.setter = setter;
                    itemField.holderField = holderField;
                    itemFields.add(itemField);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            } else if (multiAnno != null) {
                String[] objFields = multiAnno.objectFields();
                String[] setters = multiAnno.setterNames();
                Class<?>[] clzs = multiAnno.setterParamClzs();
                int index = 0;
                for (String objField :
                        objFields) {
                    if (!TextUtils.isEmpty(objField)) {
                        try {
//                            LogUtil.debug("RRAdapter getclzz", objField + " - " + setters[index] + " - " + clzs[index]);
                            Field objF = objClz.getDeclaredField(objField);
                            objF.setAccessible(true);
                            Method setter = holderField.getType().getMethod(setters[index], clzs[index]);
                            holderField.setAccessible(true);
                            ItemField itemField = new ItemField();
                            itemField.objField = objF;
                            itemField.setter = setter;
                            itemField.holderField = holderField;
                            itemFields.add(itemField);
                        } catch (NoSuchFieldException e1) {
                            e1.printStackTrace();
                        } catch (NoSuchMethodException e1) {
                            e1.printStackTrace();
                        }
                    }
                    index++;
                }
            }

        }
        LogUtil.info("耗时：", (System.currentTimeMillis() - start) + "");
    }

    public void setClz(Class<? extends RRViewHolder> viewHolderClz, Class<T> objClz) {
        setClz(viewHolderClz, objClz, context);
    }

    public RRAdapter setLayoutRes(int layoutRes) {
        this.layoutRes = layoutRes;
        return this;
    }

    public void setNotifyAfterChange(boolean notifyAfterChange) {
        this.notifyAfterChange = notifyAfterChange;
    }

    public void setData(List<T> dataList) {
        this.dataList = dataList;
        if (notifyAfterChange)
            notifyDataSetChanged();
    }

    public void addData(T t) {
        if (this.dataList == null)
            this.dataList = new ArrayList<>();
        this.dataList.add(t);
        if (notifyAfterChange)
            notifyDataSetChanged();
    }

    public void insertData(int location, T t) {
        if (this.dataList == null)
            this.dataList = new ArrayList<>();
        this.dataList.add(location, t);
        if (notifyAfterChange)
            notifyDataSetChanged();
    }

    public void addAll(Collection<T> ts) {
        if (this.dataList == null)
            this.dataList = new ArrayList<>();
        this.dataList.addAll(ts);
        if (notifyAfterChange)
            notifyDataSetChanged();
    }

    public void clearData() {
        if (this.dataList != null) {
            dataList.clear();
            if (notifyAfterChange)
                notifyDataSetChanged();
        }
    }
}
