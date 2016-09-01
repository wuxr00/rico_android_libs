package r.lib;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import java.util.List;

import r.lib.util.LogUtil;


/**
 * 应用状态类，表明应用在线/离线
 * Created by Rico on 2015/7/28.
 */
public class NetworkStateMachine {

    private static NetworkStateMachine instance;

    private boolean offline;
    private boolean isWifi;
    private boolean isMobileNet;
    private NetStateReceiver netStateReceiver;

    private NetworkStateMachine() {

    }

    public boolean isWifi() {
        return isWifi;
    }

    public boolean isMobileNet() {
        return isMobileNet;
    }

    public boolean isOffline() {
        return offline ;
    }


    public synchronized static NetworkStateMachine getInstance() {
        if (instance == null) {
            instance = new NetworkStateMachine();
        }
        return instance;
    }

    public void init(Context context) {
        offline = !isNetworkAvailable(context);
        LogUtil.info("init-" + offline);
    }

    /*public void init(Context context) {

    }*/

    public void startNetStateListener(Context context) {
        if (netStateReceiver == null) {
//            offline = isNetworkAvailable(context);
            netStateReceiver = new NetStateReceiver();
            context.registerReceiver(netStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    public void stopNetStateListener(Context context) {
        if (netStateReceiver != null) {
            context.unregisterReceiver(netStateReceiver);
            netStateReceiver = null;
        }
    }


    /**
     * 判断wifi、数据网络是否可用
     *
     * @param context
     * @return
     */
    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);//wifi网络
        NetworkInfo mMobileNetworkInfo = mConnectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);//手机数据网络
        if (mWiFiNetworkInfo == null || !(isWifi = mWiFiNetworkInfo.isAvailable())) {
            if (mMobileNetworkInfo == null || !mMobileNetworkInfo.isAvailable() || "DISCONNECTED".equals(mMobileNetworkInfo.getState().name())) {
                return false;
            }else
                isMobileNet = true;
        }
        LogUtil.info("网络类型－" + isWifi + " - " + isMobileNet);
        return true;
    }

    private class NetStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, Intent intent) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean flag = false;
                    ActivityManager am = (ActivityManager) context
                            .getSystemService(Context.ACTIVITY_SERVICE);
                    List<ActivityManager.RunningAppProcessInfo> runnings = am
                            .getRunningAppProcesses();
                    if (runnings != null) {// 判断程序是否在运行、后台，若没有运行了，注销广播监听
                        for (int i = 0; i < runnings.size(); i++) {
                            ActivityManager.RunningAppProcessInfo info = runnings.get(i);
                            if (context.getPackageName().equals(info.processName)) {
                                flag = true;
                            }
                        }
                    }
                    if (!flag) {
                        new Handler(context.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                context.unregisterReceiver(NetStateReceiver.this);
                            }
                        });
                        return;
                    }
                    if (offline && isNetworkAvailable(context)) {
                        LogUtil.info("网络状态 - 初始离线 -> 在线");
                        offline = false;

                    } else if (!offline && !isNetworkAvailable(context)) {
                        LogUtil.info("网络状态 - 初始在线 -> 离线");
                        offline = true;

                    }
                }
            }

            ).start();

        }
    }

}
