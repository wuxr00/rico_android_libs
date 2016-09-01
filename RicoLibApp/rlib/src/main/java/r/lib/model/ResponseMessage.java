package r.lib.model;

import android.content.Context;
import android.widget.Toast;

import r.lib.network.RequestUtil;


/**
 * 处理服务器／其他来源返回信息提示
 * Created by Rico on 15/9/2.
 */
public class ResponseMessage {

    public String message;
    public int msgRes;

    public ResponseMessage setMessage(String message) {
        this.message = message;
        return this;
    }

    public ResponseMessage setMessage(int msgRes) {
        this.msgRes = msgRes;
        return this;
    }

    public static ResponseMessage create(String message) {
        return new ResponseMessage().setMessage(message);
    }

    public static ResponseMessage create(int msgRes) {
        return new ResponseMessage().setMessage(msgRes);
    }

    public String getString(Context context) {
        return RequestUtil.strIsNull(message) ? msgRes > 0 ? context.getResources().getString(msgRes) : "" : message;
    }

    public void show(Context context) {
        Toast.makeText(context, RequestUtil.strIsNull(message) ? msgRes > 0 ? context.getResources().getString(msgRes) : "" : message, Toast.LENGTH_SHORT).show();
    }

    public void showLong(Context context) {
        Toast.makeText(context, RequestUtil.strIsNull(message) ? msgRes > 0 ? context.getResources().getString(msgRes) : "" : message, Toast.LENGTH_LONG).show();
    }

    @Override
    public String toString() {
        return "ResponseMessage{" +
                "message='" + message + '\'' +
                ", msgRes=" + msgRes +
                '}';
    }
}
