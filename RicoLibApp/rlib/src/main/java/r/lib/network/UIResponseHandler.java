package r.lib.network;

import android.os.Handler;
import android.os.Message;

import r.lib.util.ContextUtil;
import r.lib.util.ThreadHelperFactory;

/**
 * Created by Rico on 15/10/20.
 */
public class UIResponseHandler {

    private static ThreadHelperFactory.ThreadHelper threadHelper;

    private static Handler.Callback handlerCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.obj != null) {
                UIResponseListener listener = (UIResponseListener) msg.obj;
                listener.doCallback();
            }
            return false;
        }
    };


    public static void submit(UIResponseListener responseListener) {
        if (threadHelper == null)
            threadHelper = ThreadHelperFactory.getInstance().createUIThreadHelper(ContextUtil.getContext(), handlerCallback);
        Message msg = new Message();
        msg.obj = responseListener;
        threadHelper.sendMessage(msg);
    }

    public static void finish() {
        if (threadHelper != null)
            threadHelper.shutdown();
        threadHelper = null;
    }
}
