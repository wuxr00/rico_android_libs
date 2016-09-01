package r.volleylib;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Objects;

import javax.xml.transform.ErrorListener;

import r.lib.network.ResponseResult;

/**
 * Created by Rico on 16/8/30.
 */
public class VolleyUtil {

    private static RequestQueue mQueue;
    private static ArrayList<Object> tagList = new ArrayList<>();


    public static void init(Context context) {
        if (mQueue != null) {
            int size = tagList.size();
            for (int i = 0; i < size; i++) {
                mQueue.cancelAll(tagList.get(i));
            }
            tagList.clear();
        } else
            mQueue = Volley.newRequestQueue(context);
    }

    private static final Object cancelLock = new Object();

    public static void cancel(Object tag) {
        synchronized (cancelLock) {
            mQueue.cancelAll(tag);
            tagList.remove(tag);
        }
    }

    public static void remove(Object tag) {
        synchronized (cancelLock) {
            tagList.remove(tag);
        }
    }

    public static <T> void access(final VolleyEntity<T> entity, final VResponseListener<T> responseListener) {
        if (tagList.contains(entity.tag))
            return;
        tagList.add(entity.tag);
        Request request = entity.getRequest(
                new VolleyResponseListener<T>().setListener(new Response.Listener<T>() {
                    @Override
                    public void onResponse(T response) {
                        remove(entity.tag);
                        if (responseListener != null)
                            responseListener.onResponse(response);
                    }
                }).setErrorListener(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        remove(entity.tag);
                        if (responseListener != null)
                            responseListener.onErrorResponse(error);
                    }
                })

        );
//        request.setRetryPolicy(new DefaultRetryPolicy(entity.timeout, entity.retry, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        request.setTag(entity.tag);
    }

}
