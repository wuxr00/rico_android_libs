package r.lib.ui.widget.component;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Rico on 15/11/25.
 */
public class ListViewItem {

    public int position;
    public View convertView;
    public ViewGroup parent;

    public ListViewItem(int position, View convertView, ViewGroup parent) {
        this.position = position;
        this.convertView = convertView;
        this.parent = parent;
    }


}
