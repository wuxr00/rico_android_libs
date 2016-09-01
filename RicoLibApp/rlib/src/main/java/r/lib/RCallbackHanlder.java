package r.lib;

import android.content.Context;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import r.lib.model.ResponseMessage;
import r.lib.util.LogUtil;
import r.lib.util.ThreadHelperFactory;

/**
 * Created by Rico on 16/1/23.
 */
public class RCallbackHanlder {
    private static RCallbackHanlder instance;
    private final HashMap<Object, CallbackHolder> dataMap = new HashMap<>();
    private ThreadHelperFactory.ThreadHelper uiThreadhelper;
    private ExecutorService pool;
    private boolean finished;

    public static final int MODE_UI = 1;
    public static final int MODE_BACKGROUND = 2;


    public static void register(Object key, int mode, RCallback rCallback) {
        CallbackHolder holder = new CallbackHolder();
        holder.mode = mode;
        holder.rCallback = rCallback;
        instance.dataMap.put(key, holder);
        LogUtil.info("register callback-" + key);
    }

    public static void unRegister(Object key) {
        if (instance != null)
            instance.dataMap.remove(key);
    }

    public static boolean isFinished() {
        return instance == null;
    }

    public synchronized static void finish() {
        if (!isFinished() && instance != null) {
            instance.finished = true;
            instance.dataMap.clear();
            instance.uiThreadhelper.shutdown();
            instance.pool.shutdown();
            instance = null;
            LogUtil.info("RCallbackHanlder finished");
        }
    }

    public synchronized static void init(Context context) {
        LogUtil.info("RCallbackHanlder init start");
        instance = new RCallbackHanlder();
        instance.pool = Executors.newCachedThreadPool();
        instance.uiThreadhelper = ThreadHelperFactory.getInstance().createUIThreadHelper(context);
        instance.finished = false;
        LogUtil.info("init callbackhandler" + instance.pool.isShutdown());
    }

    public static void start(Object key) {
        final CallbackHolder holder = instance.dataMap.get(key);
        LogUtil.info("callback-" + key + " - " + holder);
        if (holder != null)
            if (holder.mode == MODE_UI)
                instance.uiThreadhelper.execute(new Runnable() {
                    @Override
                    public void run() {
                        holder.rCallback.onStart();
                    }
                });
            else if (holder.mode == MODE_BACKGROUND)
                instance.pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        holder.rCallback.onStart();
                    }
                });
            else
                holder.rCallback.onStart();
    }

    public static void complete(Object key) {
        final CallbackHolder holder = instance.dataMap.get(key);
        LogUtil.info("callback-" + key + " - " + holder);
        if (holder != null)
            if (holder.mode == MODE_UI)
                instance.uiThreadhelper.execute(new Runnable() {
                    @Override
                    public void run() {
                        holder.rCallback.onComplete();
                    }
                });
            else if (holder.mode == MODE_BACKGROUND)
                instance.pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        holder.rCallback.onComplete();
                    }
                });
            else
                holder.rCallback.onComplete();
    }

    public static <T> void complete(Object key, final boolean success, final T t, final ResponseMessage message) {
        final CallbackHolder holder = instance.dataMap.get(key);
        LogUtil.info("callback-" + key + " - " + holder);
        if (holder != null)
            if (holder.mode == MODE_UI)
                instance.uiThreadhelper.execute(new Runnable() {
                    @Override
                    public void run() {
                        holder.rCallback.onComplete();
                        holder.rCallback.onDataCallback(t);
                        holder.rCallback.onMessage(message);
                        if (success)
                            holder.rCallback.onSuccess();
                        else
                            holder.rCallback.onFailed();
                    }
                });
            else if (holder.mode == MODE_BACKGROUND)
                instance.pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        holder.rCallback.onComplete();
                        holder.rCallback.onDataCallback(t);
                        holder.rCallback.onMessage(message);
                        if (success)
                            holder.rCallback.onSuccess();
                        else
                            holder.rCallback.onFailed();
                    }
                });
            else {
                holder.rCallback.onComplete();
                holder.rCallback.onDataCallback(t);
                holder.rCallback.onMessage(message);
                if (success)
                    holder.rCallback.onSuccess();
                else
                    holder.rCallback.onFailed();
            }
    }

    public static <T> void completeCallback(Object key, final boolean success, final T t, final ResponseMessage message) {
        final CallbackHolder holder = instance.dataMap.get(key);
        LogUtil.info("callback-" + key + " - " + holder);
        if (holder != null)
            if (holder.mode == MODE_UI)
                instance.uiThreadhelper.execute(new Runnable() {
                    @Override
                    public void run() {
                        holder.rCallback.onComplete();
                        RCallback.CallbackResult<T> result = new RCallback.CallbackResult<T>();
                        result.data = t;
                        result.responseMessage = message;
                        result.success = success;
                        holder.rCallback.onCallback(result);
                    }
                });
            else if (holder.mode == MODE_BACKGROUND)
                instance.pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        holder.rCallback.onComplete();
                        RCallback.CallbackResult<T> result = new RCallback.CallbackResult<T>();
                        result.data = t;
                        result.responseMessage = message;
                        result.success = success;
                        holder.rCallback.onCallback(result);
                    }
                });
            else {
                holder.rCallback.onComplete();
                RCallback.CallbackResult<T> result = new RCallback.CallbackResult<T>();
                result.data = t;
                result.responseMessage = message;
                result.success = success;
                holder.rCallback.onCallback(result);
            }
    }


    public static void sendMsg(Object key, final ResponseMessage responseMessage) {
        final CallbackHolder holder = instance.dataMap.get(key);
        LogUtil.info("callback-" + key + " - " + holder);
        if (holder != null)
            if (holder.mode == MODE_UI)
                instance.uiThreadhelper.execute(new Runnable() {
                    @Override
                    public void run() {
                        holder.rCallback.onMessage(responseMessage);
                    }
                });
            else if (holder.mode == MODE_BACKGROUND)
                instance.pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        holder.rCallback.onMessage(responseMessage);
                    }
                });
            else
                holder.rCallback.onMessage(responseMessage);
        LogUtil.info("after call - " + instance.dataMap);
    }

    public static <T> void sendData(Object key, final T t) {
        final CallbackHolder holder = instance.dataMap.get(key);
        LogUtil.info("callback-" + key + " - " + holder);
        if (holder != null)
            if (holder.mode == MODE_UI)
                instance.uiThreadhelper.execute(new Runnable() {
                    @Override
                    public void run() {
                        holder.rCallback.onDataCallback(t);
                    }
                });
            else if (holder.mode == MODE_BACKGROUND)
                instance.pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        holder.rCallback.onDataCallback(t);
                    }
                });
            else
                holder.rCallback.onDataCallback(t);
    }

    public static void success(Object key) {
        final CallbackHolder holder = instance.dataMap.get(key);
        LogUtil.info("callback-" + key + " - " + holder);
        if (holder != null)
            if (holder.mode == MODE_UI)
                instance.uiThreadhelper.execute(new Runnable() {
                    @Override
                    public void run() {
                        holder.rCallback.onSuccess();
                    }
                });
            else if (holder.mode == MODE_BACKGROUND)
                instance.pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        holder.rCallback.onSuccess();
                    }
                });
            else
                holder.rCallback.onSuccess();

    }

    public static void failed(Object key) {
        final CallbackHolder holder = instance.dataMap.get(key);
        LogUtil.info("callback-" + key + " - " + holder);
        if (holder != null)
            if (holder.mode == MODE_UI)
                instance.uiThreadhelper.execute(new Runnable() {
                    @Override
                    public void run() {
                        holder.rCallback.onFailed();
                    }
                });
            else if (holder.mode == MODE_BACKGROUND)
                instance.pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        holder.rCallback.onFailed();
                    }
                });
            else
                holder.rCallback.onFailed();

    }

    private static class CallbackHolder {
        public RCallback rCallback;
        public int mode;
    }
}
