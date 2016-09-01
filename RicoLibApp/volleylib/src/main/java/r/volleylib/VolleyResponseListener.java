package r.volleylib;

import com.android.volley.Response;

/**
 * Created by Rico on 16/8/31.
 */
public class VolleyResponseListener<T> {
    public Response.Listener<T> listener;
    public Response.ErrorListener errorListener;

    public VolleyResponseListener setListener(Response.Listener<T> listener) {
        this.listener = listener;
        return this;
    }

    public VolleyResponseListener setErrorListener(Response.ErrorListener errorListener) {
        this.errorListener = errorListener;
        return this;
    }
}
