package r.okhttpaccess;

import com.squareup.okhttp.Request;

import r.lib.network.RequestEntity;

/**
 * Created by Rico on 15/9/2.
 */
public interface OkRequestEntity extends RequestEntity
{
    public Request getRequest();


}
