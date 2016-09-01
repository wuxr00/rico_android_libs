package r.lib.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by Rico on 16/1/20.
 */
public class DeviceUtil {
    public static void call(Context context, String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse(new StringBuilder("tel:").append(phoneNum).toString());
        intent.setData(data);
        context.startActivity(intent);
    }

    public static void openURL(Context context, String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        context.startActivity(intent);
    }
}
