package r.lib.network;

/**
 * Created by Rico on 15/10/19.
 */
abstract public class UIResponseListener extends AResponseListener {

    protected ResponseResult responseResult;


    public void doCallback() {
        onResponse(responseResult);
    }


    @Override
    public void responseFliper(ResponseResult response) {
        this.responseResult = response;
        UIResponseHandler.submit(this);

    }
}
