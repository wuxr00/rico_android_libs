package r.okhttpaccess;

import android.text.TextUtils;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import r.lib.network.MultipartParam;
import r.lib.network.NotRequestParam;
import r.lib.network.RequestBody;
import r.lib.network.RequestEntity;
import r.lib.network.RequestHeader;
import r.lib.network.RequestParam;
import r.lib.network.RequestUtil;
import r.lib.util.LogUtil;

/**
 * Created by Rico on 15/12/3.
 */
public class OKRequestUtil extends RequestUtil {

    public static OKRequestUtil getUtil() {
        if (util == null)
            util = new OKRequestUtil();
        return (OKRequestUtil) util;
    }

    @Override
    public String getUrl(CharSequence uri) {
        return new StringBuilder(this.serverUrl).append(uri).toString();
    }

    /**
     * 用于OkHttp三方库
     *
     * @param requestEntity
     * @return
     */
    public com.squareup.okhttp.RequestBody getMultipartOkRequestBody(RequestEntity requestEntity, String fileType) {
        MultipartBuilder requestBodyBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);
        if (requestEntity != null) {
            parseMultipartOkRequestBody(requestEntity, requestEntity.getClass(), requestBodyBuilder, fileType);
        }
        return requestBodyBuilder.build();
    }

    public com.squareup.okhttp.RequestBody getMultipartOkRequestBody(RequestEntity requestEntity) {
        MultipartBuilder requestBodyBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);
        if (requestEntity != null) {
            parseMultipartOkRequestBody(requestEntity, requestEntity.getClass(), requestBodyBuilder, "application/octet-stream");
        }
        return requestBodyBuilder.build();
    }

    private void parseMultipartOkRequestBody(RequestEntity requestEntity, Class<?> clz, MultipartBuilder requestBodyBuilder, String fileType) {
        Field[] fields = clz.getDeclaredFields();
        try {
            for (Field field :
                    fields) {

                KeyVal keyVal = parseReuqestField(field, requestEntity);
                if (keyVal.valid) {
                    MultipartParam multipartParam = field.getAnnotations().length == 0 ? null : field.getAnnotation(MultipartParam.class);
                    if (field.getType().isAssignableFrom(File.class) && multipartParam != null) {
                        Object fieldVal = field.get(requestEntity);
                        if (fieldVal != null) {

                            String multiKey = null;
                            multiKey = multipartParam.key();
                            multiKey = TextUtils.isEmpty(multiKey) ? field.getName() : multiKey;
                            File file = (File) fieldVal;
                            requestBodyBuilder.addFormDataPart(multiKey, file.getName(), com.squareup.okhttp.RequestBody.create(null, file));

                            requestBodyBuilder.addPart(Headers.of("Content-Disposition", "form-data; name=\"another\";filename=\"" + fileType + "\""), com.squareup.okhttp.RequestBody.create(MediaType.parse("image/jpg"), file));//application/octet-stream//"+multiKey+"

                            LogUtil.info("请求-" + multiKey + "-" + file.getName());
                        }
                    } else {

                        requestBodyBuilder.addFormDataPart(keyVal.key, keyVal.value);
                        LogUtil.info("请求-" + keyVal.key + "-" + keyVal.value);

                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Class<?> superClz = clz.getSuperclass();
        if (superClz != null)
            parseMultipartOkRequestBody(requestEntity, superClz, requestBodyBuilder, fileType);
    }

    public com.squareup.okhttp.RequestBody getOkRequestBody(RequestEntity requestEntity) {
        FormEncodingBuilder requestBodyBuilder = new FormEncodingBuilder();
        if (requestEntity != null) {
            parseOkRequestBody(requestEntity, requestEntity.getClass(), requestBodyBuilder);
        }
        return requestBodyBuilder.build();
    }

    private void parseOkRequestBody(RequestEntity requestEntity, Class<?> clz, FormEncodingBuilder requestBodyBuilder) {
        Field[] fields = clz.getDeclaredFields();
        try {
            for (Field field :
                    fields) {

                KeyVal keyVal = parseReuqestField(field, requestEntity);
                if (keyVal.valid)
                    requestBodyBuilder.add(keyVal.key, keyVal.value);

//                LogUtil.info("请求-" + key + "-" + parseValue(field.get(requestEntity), field.getType()));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Class<?> superClz = clz.getSuperclass();
        if (superClz != null)
            parseOkRequestBody(requestEntity, superClz, requestBodyBuilder);
    }

}
