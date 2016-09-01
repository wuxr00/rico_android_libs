package r.lib.network;

import r.lib.model.ResponseMessage;
import r.lib.util.UnProguard;

/**
 * Created by Rico on 16/8/30.
 */
public class ResponseResult implements UnProguard {
    public boolean success;
    public String result;
    public ResponseMessage msg;

    @Override
    public void injected() {

    }

    public ResponseResult() {
    }

    public ResponseResult setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public ResponseResult setResult(String result) {
        this.result = result;
        return this;
    }

    public ResponseResult setMsg(ResponseMessage msg) {
        this.msg = msg;
        return this;
    }

    @Override
    public String toString() {
        return "ResponseResult{" +
                "success=" + success +
                ", result='" + result + '\'' +
                ", msg=" + msg +
                '}';
    }
}