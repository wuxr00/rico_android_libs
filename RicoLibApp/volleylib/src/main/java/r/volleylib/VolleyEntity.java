package r.volleylib;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;

import r.lib.network.RequestEntity;

/**
 * Created by Rico on 16/8/30.
 */
abstract public class VolleyEntity<T> implements RequestEntity {

    public transient int timeout = 30000;
    public transient Object tag;
    public transient int retry = DefaultRetryPolicy.DEFAULT_MAX_RETRIES;

    public VolleyEntity setTag(Object tag) {
        this.tag = tag;
        return this;
    }

    public VolleyEntity setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public VolleyEntity setRetry(int retry) {
        this.retry = retry;
        return this;
    }



    public Request getRequest(VolleyResponseListener<T> responseListener) {
        return newRequest(responseListener).setTag(tag).setRetryPolicy(new DefaultRetryPolicy(timeout, retry, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }



    abstract public  Request<T> newRequest(VolleyResponseListener<T> responseListener);
}
