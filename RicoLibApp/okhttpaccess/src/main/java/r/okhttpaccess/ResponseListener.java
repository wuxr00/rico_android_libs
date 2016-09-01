package r.okhttpaccess;

import android.text.TextUtils;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import r.lib.model.ResponseMessage;
import r.lib.network.AResponseListener;
import r.lib.network.ResponseResult;
import r.lib.util.LogUtil;

/**
 * Created by Rico on 2015/6/17.
 */
abstract public class ResponseListener extends AResponseListener implements Callback {



    @Override
    public void onFailure(Request request, IOException e) {
        LogUtil.info("ResponseListener网络访问失败－ " + e.getMessage() + "\n" + e.getLocalizedMessage());
        String err = e.getMessage();
        if (TextUtils.isEmpty(err)
                || err.contains("connect")
                || err.contains("Unable to resolve host")
                || err.contains("unexpected end of stream")) {
            responseFliper(new ResponseResult().setSuccess(false).setMsg(new ResponseMessage().setMessage(R.string.err_servererr)));
        } else if (err.contains("timeout")) {
            responseFliper(new ResponseResult().setSuccess(false).setMsg(new ResponseMessage().setMessage(R.string.err_timeout)));
        } else
            responseFliper(new ResponseResult().setSuccess(false).setMsg(new ResponseMessage().setMessage(err)));

    }

    @Override
    public void onResponse(Response response) throws IOException {
        LogUtil.info("ResponseListener网络访问返回－ " + response.isSuccessful() + " -code- " + response.code()
                + " -message- " + response.message());
        String body = response.body().string();

        if (response.isSuccessful()) {
            responseFliper(new ResponseResult().setSuccess(true).setResult(body));

        } else {
            String err = response.message();
            if (TextUtils.isEmpty(err) || response.code() == 404 || response.code() == 405) {
                responseFliper(new ResponseResult().setSuccess(false).setMsg(new ResponseMessage().setMessage(R.string.err_servererr)));
            } else if (response.code() == 500)
                responseFliper(new ResponseResult().setSuccess(false).setMsg(new ResponseMessage().setMessage(body)));
            else
                responseFliper(new ResponseResult().setSuccess(false).setMsg(new ResponseMessage().setMessage(err)));

        }
    }





}
