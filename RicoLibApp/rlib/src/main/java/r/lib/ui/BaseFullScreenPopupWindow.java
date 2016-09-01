package r.lib.ui;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * 满屏弹出窗体基类
 * Created by Rico on 15/9/14.
 */
abstract public class BaseFullScreenPopupWindow  extends PopupWindow {


    public BaseFullScreenPopupWindow(Context context) {
        super(context);//适配SDK2.3
        setBackgroundDrawable(new BitmapDrawable());//处理调用了super(context);后窗体四周出现1DP左右的padding（系统）
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        setContentView(getMainView(context));

    }

    abstract protected View getMainView(Context context);
}
