package r.okhttpaccess;

import com.squareup.okhttp.Request;

import cn.coreprogress.listener.ProgressListener;
import r.lib.network.NotRequestParam;

/**
 * Created by Rico on 15/10/21.
 */
public class DownloadRequest implements OkRequestEntity {


    @NotRequestParam
    private ProgressListener progressListener;
    @NotRequestParam
    private String url;

    public ProgressListener getProgressListener() {
        return progressListener;
    }

    public DownloadRequest(String url, ProgressListener progressListener) {
        this.progressListener = progressListener;
        this.url = url;
    }

    @Override
    public Request getRequest() {
        return new Request.Builder()
                .url(url)
                .build();
    }

    @Override
    public String getUrl() {
        return url;
    }
}
