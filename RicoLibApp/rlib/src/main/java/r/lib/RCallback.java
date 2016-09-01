package r.lib;

import android.content.Context;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import r.lib.model.ResponseMessage;
import r.lib.util.ThreadHelperFactory;

/**
 * Created by Rico on 16/1/23.
 */
public class RCallback<T> {



    protected void onStart() {

    }

    protected void onComplete() {

    }

    protected void onMessage(ResponseMessage responseMessage) {

    }

    protected void onDataCallback(T t) {

    }

    protected void onSuccess() {

    }

    protected void onFailed() {

    }

    protected void onCallback(CallbackResult<T> result) {

    }

    public static class CallbackResult<T>{
        public boolean success;
        public ResponseMessage responseMessage;
        public T data;
    }

}
