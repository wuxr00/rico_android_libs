package r.lib.util;

import android.util.Base64;

import java.security.MessageDigest;

/**
 * Created by Rico on 16/1/19.
 */
public class StrUtil {


    public static String decodeFromBase64(String base64Str) {
        return new String(Base64.decode(base64Str, Base64.DEFAULT));
    }

    public static String encodeToBase64(String normalStr) {
        return Base64.encodeToString(normalStr.getBytes(), Base64.DEFAULT);
    }

    /**
     * MD5
     *
     * @param s
     * @return
     */
    public final static String MD5(String s) {
        if(s == null)
            return "";
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
