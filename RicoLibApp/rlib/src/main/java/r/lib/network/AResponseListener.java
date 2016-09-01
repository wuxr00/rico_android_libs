package r.lib.network;

/**
 * Created by Rico on 16/8/30.
 */
public abstract class AResponseListener {
    abstract public void onResponse(ResponseResult responseResult);


    public void responseFliper(ResponseResult response) {
        onResponse(response);
    }
}
