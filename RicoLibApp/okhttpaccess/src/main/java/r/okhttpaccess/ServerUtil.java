package r.okhttpaccess;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.util.concurrent.TimeUnit;

import cn.coreprogress.helper.ProgressHelper;

/**
 * 与服务器交互
 * Created by Rico on 2015/7/28.
 */
public class ServerUtil {

    private static final OkHttpClient okClient = new OkHttpClient();

    static {

        okClient.setConnectTimeout(30, TimeUnit.SECONDS);
    }

    private static OkHttpClient transmissionClient;

    private static void initTransmissionClient() {
        transmissionClient = new OkHttpClient();
        transmissionClient.setConnectTimeout(1000, TimeUnit.MINUTES);
        transmissionClient.setReadTimeout(1000, TimeUnit.MINUTES);
        transmissionClient.setWriteTimeout(1000, TimeUnit.MINUTES);
    }

    private ServerUtil() {
    }


    public static Call access(Request request, Callback callback) {
        Call call = okClient.newCall(request);
        call.enqueue(callback);
        return call;
    }


    public synchronized static Call upload(Request request, Callback callback) {
        if (transmissionClient == null)
            initTransmissionClient();
        Call call = transmissionClient.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call download(DownloadRequest request, Callback callback) {
        if (transmissionClient == null)
            initTransmissionClient();
        Call call = ProgressHelper.addProgressResponseListener(transmissionClient, request.getProgressListener()).newCall(request.getRequest());
        call.enqueue(callback);
        return call;
    }

}
