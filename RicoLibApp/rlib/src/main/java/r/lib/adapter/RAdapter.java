package r.lib.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import r.lib.R;
import r.lib.util.ContextUtil;
import r.lib.util.LogUtil;
import r.lib.util.UIUtil;
import r.lib.util.UnProguard;

/**
 * Created by Rico on 16/1/19.
 */
public class RAdapter<T> extends BaseAdapter {


    private Context context;
    private List<T> dataList;
    private boolean notifyAfterChange = true;
    private boolean autoSetData = true;
    private boolean innerHolderClz;
    private int layoutRes;
    private Object outterObj;//用于内部类
//    private Class<? extends RAdaptive> viewHolderClz;

    private ArrayList<ItemField> itemFields = new ArrayList<>();
    private Constructor holderConstructor;


    public RAdapter(Context context) {
        this.context = context;
    }

    public void setAutoSetData(boolean autoSetData) {
        this.autoSetData = autoSetData;
    }

    public void setClz(Class<? extends RAdaptive> viewHolderClz, Class<T> objClz, Object outterObj) {
        this.outterObj = outterObj;
        try {
            holderConstructor = viewHolderClz.getDeclaredConstructor();

        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
            try {
                holderConstructor = viewHolderClz.getDeclaredConstructor(outterObj.getClass());
                innerHolderClz = true;
            } catch (NoSuchMethodException e1) {
                e1.printStackTrace();
            }
        }

        holderConstructor.setAccessible(true);

        Field[] holderFields = viewHolderClz.getDeclaredFields();

        try {
            for (Field holderField :
                    holderFields) {
                RAdapterField fieldAnno = holderField.getAnnotation(RAdapterField.class);
                if (fieldAnno == null) {
                    continue;
                }
                String objFieldStr = fieldAnno.objectField();
                Field objField = null;
                objField = TextUtils.isEmpty(objFieldStr) ? null : objClz.getDeclaredField(objFieldStr);

                if (objField != null) {
                    objField.setAccessible(true);
                    Method setter = holderField.getType().getMethod(fieldAnno.setterName(), fieldAnno.setterParamClz());
                    if (setter != null) {
                        holderField.setAccessible(true);
                        ItemField itemField = new ItemField();
                        itemField.objField = objField;
                        itemField.setter = setter;
                        itemField.holderField = holderField;
                        itemFields.add(itemField);
                    }
                }

            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void setClz(Class<? extends RAdaptive> viewHolderClz, Class<T> objClz) {
        setClz(viewHolderClz, objClz, context);
    }

    public RAdapter setLayoutRes(int layoutRes) {
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

    public void clear() {
        if (this.dataList != null) {
            dataList.clear();
            if (notifyAfterChange)
                notifyDataSetChanged();
        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void finish() {
        clear();
        clearData();
    }

    private void clearData() {
        notifyAfterChange = false;
        itemFields.clear();
        context = null;
        outterObj = null;
    }

    public void finish(boolean clearData) {
        if (clearData)
            clear();
        clearData();
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public T getItem(int position) {
        return this.dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (layoutRes > 0) {
            long start = System.currentTimeMillis();
            T data = getItem(position);
            RAdaptive holder = null;
            if (convertView == null || convertView.getTag(R.id.tag_holder) == null) {
                convertView = LayoutInflater.from(context).inflate(layoutRes, parent, false);
                try {
                    if (innerHolderClz)
                        holder = (RAdaptive) holderConstructor.newInstance(outterObj);
                    else
                        holder = (RAdaptive) holderConstructor.newInstance();
                    UIUtil.injectView(holder, convertView);
                    holder.onInit(position, convertView, parent, data);
                    convertView.setTag(R.id.tag_holder, holder);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else
                holder = (RAdaptive) convertView.getTag(R.id.tag_holder);
            if (holder != null) {
                LogUtil.info("Radapter getVie-" + " - " + position + " - " + holder.hashCode());
                if (autoSetData) {
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
                }

                holder.onSetViewData(position, convertView, parent, data);
            }
            LogUtil.info("time west-" + (System.currentTimeMillis() - start));
            return convertView;
        } else
            return new View(context);
    }


//
//    private Context context;
//    private List<T> dataList;
//    private boolean notifyAfterChange = true;
//    private boolean autoSetData = true;
//    private int layoutRes;
////    private Class<? extends RAdaptive> viewHolderClz;
//
//    private ArrayList<ItemField> itemFields = new ArrayList<>();
//    private Constructor holderConstructor;
//
//
//    public RAdapter(Context context) {
//        this.context = context;
//    }
//
//    public void setAutoSetData(boolean autoSetData) {
//        this.autoSetData = autoSetData;
//    }
//
//    public void setClz(Class<? extends RAdaptive> viewHolderClz, Class<T> objClz) {
//        try {
//            holderConstructor = viewHolderClz.getDeclaredConstructor();
//            holderConstructor.setAccessible(true);
//
//            Field[] holderFields = viewHolderClz.getDeclaredFields();
//
//            for (Field holderField :
//                    holderFields) {
//                RAdapterField fieldAnno = holderField.getAnnotation(RAdapterField.class);
//                if (fieldAnno != null) {
//                    String objFieldStr = fieldAnno.objectField();
//                    Field objField = TextUtils.isEmpty(objFieldStr) ? null : objClz.getDeclaredField(objFieldStr);
//                    if (objField != null) {
//                        objField.setAccessible(true);
//                        Method setter = holderField.getType().getMethod(fieldAnno.setterName(), fieldAnno.setterParamClz());
//                        if (setter != null) {
//                            holderField.setAccessible(true);
//                            ItemField itemField = new ItemField();
//                            itemField.objField = objField;
//                            itemField.setter = setter;
//                            itemField.holderField = holderField;
//                            itemFields.add(itemField);
//                        }
//                    }
//                }
//
//            }
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public RAdapter setLayoutRes(int layoutRes) {
//        this.layoutRes = layoutRes;
//        return this;
//    }
//
//    public void setNotifyAfterChange(boolean notifyAfterChange) {
//        this.notifyAfterChange = notifyAfterChange;
//    }
//
//    public void setData(List<T> dataList) {
//        this.dataList = dataList;
//        if (notifyAfterChange)
//            notifyDataSetChanged();
//    }
//
//    public void addData(T t) {
//        if (this.dataList == null)
//            this.dataList = new ArrayList<>();
//        this.dataList.add(t);
//        if (notifyAfterChange)
//            notifyDataSetChanged();
//    }
//
//    public void insertData(int location, T t) {
//        if (this.dataList == null)
//            this.dataList = new ArrayList<>();
//        this.dataList.add(location, t);
//        if (notifyAfterChange)
//            notifyDataSetChanged();
//    }
//
//    public void addAll(Collection<T> ts) {
//        if (this.dataList == null)
//            this.dataList = new ArrayList<>();
//        this.dataList.addAll(ts);
//        if (notifyAfterChange)
//            notifyDataSetChanged();
//    }
//
//    public void clear() {
//        if (this.dataList != null) {
//            dataList.clear();
//            if (notifyAfterChange)
//                notifyDataSetChanged();
//        }
//    }
//
//    @Override
//    public void notifyDataSetChanged() {
//        super.notifyDataSetChanged();
//    }
//
//    public void finish() {
//        notifyAfterChange = false;
//        clear();
//        itemFields.clear();
//        context = null;
//    }
//
//
//
//    @Override
//    public int getCount() {
//        return dataList == null ? 0 : dataList.size();
//    }
//
//    @Override
//    public T getItem(int position) {
//        return this.dataList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        if (layoutRes > 0) {
//            long start = System.currentTimeMillis();
//            T data = getItem(position);
//            RAdaptive holder = null;
//            if (convertView == null || convertView.getTag(R.id.tag_holder) == null) {
//                convertView = LayoutInflater.from(context).inflate(layoutRes, parent, false);
//                try {
//                    holder = (RAdaptive) holderConstructor.newInstance();
//                    UIUtil.injectView(holder, convertView);
//                    holder.onInit(position, convertView, parent, data);
//                    convertView.setTag(R.id.tag_holder, holder);
//                } catch (InstantiationException e) {
//                    e.printStackTrace();
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                }
//            } else
//                holder = (RAdaptive) convertView.getTag(R.id.tag_holder);
//            if (holder != null) {
//                if (autoSetData) {
//                    int size = itemFields.size();
//                    try {
//                        for (int i = 0; i < size; i++) {
//                            ItemField itemField = itemFields.get(i);
//                            itemField.setter.invoke(itemField.holderField.get(holder), itemField.objField.get(data));
//                        }
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    } catch (InvocationTargetException e) {
//                        e.printStackTrace();
//                    }
//                }
//                holder.onSetViewData(position, convertView, parent, data);
//            }
//            return convertView;
//        } else
//            return new View(context);
//    }
//
//
//    private static class ItemField implements UnProguard {
//
//        public Method setter;
//        public Field holderField;
//        public Field objField;
//
//        @Override
//        public void injected() {
//
//        }
//    }
}
