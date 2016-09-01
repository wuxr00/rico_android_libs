package r.volleylib;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import r.lib.network.AResponseListener;
import r.lib.network.ResponseResult;
import r.lib.util.LogUtil;

/**
 * Created by Rico on 16/8/30.
 */
abstract public class VResponseListener<T> extends AResponseListener implements Response.Listener<T>, Response.ErrorListener {

    @Override
    public void onErrorResponse(VolleyError error) {
        LogUtil.err("volley访问错误", error.networkResponse.statusCode + "");
        error.printStackTrace();
    }
}
