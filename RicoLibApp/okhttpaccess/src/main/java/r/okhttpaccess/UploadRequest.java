package r.okhttpaccess;

import com.squareup.okhttp.Request;

import java.io.File;

import cn.coreprogress.helper.ProgressHelper;
import cn.coreprogress.listener.ProgressListener;
import r.lib.network.MultipartParam;
import r.lib.network.NotRequestParam;

/**
 * 上传
 * Created by Rico on 15/9/27.
 */
abstract public class UploadRequest implements OkRequestEntity {

    @NotRequestParam
    private ProgressListener progressListener;
    @MultipartParam
    private File upFile;

    public UploadRequest setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }

    public UploadRequest setUpFile(File upFile) {
        this.upFile = upFile;
        return this;
    }

    public String getFilePath(){
        return upFile.getAbsolutePath();
    }



    @Override
    public Request getRequest() {
        return new Request.Builder().url(getUrl()).post(ProgressHelper.addProgressRequestListener(OKRequestUtil.getUtil().getMultipartOkRequestBody(this), progressListener)).build();
    }

}
