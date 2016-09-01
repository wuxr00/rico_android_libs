package r.lib.adapter;

import android.view.View;
import android.view.ViewGroup;

import r.lib.util.UnProguard;

/**
 * Created by Rico on 16/1/19.
 */
public interface RAdaptive<T> extends UnProguard{
    public void onInit(int position, View convertView, ViewGroup parent,T data);
    public void onSetViewData(int position, View convertView, ViewGroup parent,T data);

//    public void onInit(int position, View convertView, ViewGroup parent,RAdapter<T> adapter);
//    public void onSetViewData(int position, View convertView, ViewGroup parent,RAdapter<T> adapter);
}
