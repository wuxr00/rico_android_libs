package r.lib.model;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Rico on 15/10/10.
 */
public class RefreshObj {
    public int position;
    public View convertView;
    public ViewGroup parent;

    public RefreshObj(int position, View convertView, ViewGroup parent) {
        this.position = position;
        this.convertView = convertView;
        this.parent = parent;
    }


    @Override
    public String toString() {
        return "RefreshObj{" +
                "position=" + position +
                ", convertView=" + convertView +
                ", parent=" + parent +
                '}';
    }
}
