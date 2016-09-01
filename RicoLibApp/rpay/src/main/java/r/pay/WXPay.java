package r.pay;

import android.content.Context;
import android.widget.Toast;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;


public class WXPay extends IPay {

    private IWXAPI api;
    private WXPayEntity wx;
    public static String appId;


    public boolean checkWX(Context context) {
        if (!api.isWXAppInstalled()) {
            Toast.makeText(context, "您未安装微信客户端,请先安装后重新充值,感谢使用。", Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        if (!api.isWXAppSupportAPI()) {
            Toast.makeText(context, "当前微信版本过低（要求5.0版以上），请先升级",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public void pay(Context context, IPayEntity entity) {
        wx = (WXPayEntity) entity;
        if (api == null) {
            api = WXAPIFactory.createWXAPI(context, wx.appId);
        }
        if (!checkWX(context)) {
            if (onPayListener != null)
                onPayListener.onPay(new RPayResult(false, ""));
            return;
        }
        appId = wx.appId;
        PayReq req = new PayReq();
        //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
        req.appId = wx.appId;
        req.partnerId = wx.partnerId;
        req.prepayId = wx.prepayId;
        req.nonceStr = wx.nonceStr;
        req.timeStamp = wx.timeStamp;
        req.packageValue = wx.packageValue;
        req.sign = wx.sign;
        req.extData = wx.extData; // optional
        api.registerApp(wx.appId);
        api.sendReq(req);
        if (onPayListener != null)
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    onPayListener.onPay(new RPayResult(true, ""));

                }
            }.start();
    }

}
