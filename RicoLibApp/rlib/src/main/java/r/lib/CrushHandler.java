package r.lib;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Properties;

import r.lib.util.ContextUtil;
import r.lib.util.LogUtil;

/**
 * 全局异常监听
 * Created by admin on 2015/2/3.
 */
public class CrushHandler implements Thread.UncaughtExceptionHandler {
    /**
     * Debug Log Tag
     */
    public static final String TAG = "CrashHandler";
    /**
     * 是否开启日志输出, 在Debug状态下开启, 在Release状态下关闭以提升程序性能
     */
    public static final boolean DEBUG = true;

    private static CrushHandler instance;
    private Thread.UncaughtExceptionHandler handler;
    private Context context;

    /**
     * 使用Properties来保存设备的信息和错误堆栈信息
     */
    private Properties mDeviceCrashInfo = new Properties();
    private static final String VERSION_NAME = "versionName";
    private static final String VERSION_CODE = "versionCode";
    private static final String STACK_TRACE = "STACK_TRACE";
    /**
     * 错误报告文件的扩展名
     */
    private static final String CRASH_REPORTER_EXTENSION = ".txt";

    private CrushHandler() {
    }

    public static CrushHandler getInstance() {
        if (instance == null) {
            instance = new CrushHandler();
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        handler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
//        Toast.makeText(context,throwable.getMessage() + " \n " + throwable.getStackTrace(),Toast.LENGTH_LONG).show();
        throwable.printStackTrace();
        LogUtil.err("报错－" + throwable.getMessage());
        if (!handleException(throwable) && handler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            handler.uncaughtException(thread, throwable);
        } else {
            // Sleep一会后结束程序
            // 来让线程停止一会是为了显示Toast信息给用户，然后Kill程序
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                Log.e(TAG, "Error : ", e);
//            }
//            ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).killBackgroundProcesses(context.getPackageName());

//            System.exit(0);

        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return true;
        }
        final String msg = ex.getLocalizedMessage();
        // 使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                // 保存错误报告文件
                String crashFileName = saveCrashInfoToFile(ex);
//                ((EApplication)context.getApplicationContext()).exit();
//                if(context instanceof Activity)
//                    ((Activity)context).finish();
                // 发送错误报告到服务器
//        sendCrashReportsToServer(mContext);
                // Toast 显示需要出现在一个线程的消息队列中
//                Looper.prepare();
//                Toast.makeText(context, "程序出错,", Toast.LENGTH_LONG).show();
//                Looper.loop();
            }
        }.start();

        return true;
    }


    /**
     * 收集程序崩溃的设备信息
     *
     * @param
     */
    public StringBuilder collectCrashDeviceInfo() {
        StringBuilder errMsg = new StringBuilder();
        try {
            // Class for retrieving various kinds of information related to the
            // application packages that are currently installed on the device.
            // You can find this class through getPackageManager().
            PackageManager pm = context.getPackageManager();
            // getPackageInfo(String packageName, int flags)
            // Retrieve overall information about an application package that is installed on the system.
            // public static final int GET_ACTIVITIES
            // Since: API Level 1 PackageInfo flag: return information about activities in the package in activities.
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                // public String versionName The version name of this package,
                // as specified by the <manifest> tag's versionName attribute.
                errMsg.append(VERSION_NAME).append(" = ").append(pi.versionName == null ? "not set" : pi.versionName);
//                mDeviceCrashInfo.put(VERSION_NAME, pi.versionName == null ? "not set" : pi.versionName);
                // public int versionCode The version number of this package,
                // as specified by the <manifest> tag's versionCode attribute.
                errMsg.append("\n").append(VERSION_CODE).append(" = ").append(pi.versionCode);
//
//                mDeviceCrashInfo.put(VERSION_CODE, pi.versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Error while collect package info", e);
        }
        // 使用反射来收集设备信息.在Build类中包含各种设备信息,
        // 例如: 系统版本号,设备生产商 等帮助调试程序的有用信息
        // 返回 Field 对象的一个数组，这些对象反映此 Class 对象所表示的类或接口所声明的所有字段
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                // setAccessible(boolean flag)
                // 将此对象的 accessible 标志设置为指示的布尔值。
                // 通过设置Accessible属性为true,才能对私有变量进行访问，不然会得到一个IllegalAccessException的异常
                field.setAccessible(true);
                errMsg.append("\n").append(field.getName()).append(" = ").append(field.get(null));
//                mDeviceCrashInfo.put(field.getName(), field.get(null));
//                if (DEBUG) {
//                    Log.d(TAG, field.getName() + " : " + field.get(null));
//                }
            } catch (Exception e) {
                Log.e(TAG, "Error while collect crash info", e);
            }
        }
        return errMsg;
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return
     */
    private String saveCrashInfoToFile(Throwable ex) {
        // 收集设备信息
        StringBuilder errMsg = collectCrashDeviceInfo();
        Log.i(getClass().getSimpleName(), "错误信息： " + errMsg);
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        // printStackTrace(PrintWriter s)
        // 将此 throwable 及其追踪输出到指定的 PrintWriter
        ex.printStackTrace(printWriter);

        // getCause() 返回此 throwable 的 cause；如果 cause 不存在或未知，则返回 null。
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }

        // toString() 以字符串的形式返回该缓冲区的当前值。
        String result = info.toString();
        printWriter.close();
        errMsg.append("\n").append(STACK_TRACE).append(" = ").append(result);
//        mDeviceCrashInfo.put(STACK_TRACE, result);

        try {
            String timestamp = DateFormat.format("yyMMdd_HHmmss_sss", System.currentTimeMillis()).toString();
            String fileName = "errlog-" + timestamp + CRASH_REPORTER_EXTENSION;
            File f = new File(Environment.getExternalStorageDirectory(), ContextUtil.getContext().getPackageName() + File.separator + "crashinfo");
            if (!f.exists()) {
                f.mkdirs();
            }
            f = new File(f, fileName);
            if (!f.exists()) {
                f.createNewFile();
            }
            // 保存文件
            FileOutputStream out = new FileOutputStream(f);
            out.write(errMsg.toString().getBytes());
            out.flush();
            out.close();
            errMsg = new StringBuilder();
//            FileOutputStream trace = context.openFileOutput(fileName, Context.MODE_PRIVATE);
//            mDeviceCrashInfo.store(trace, "");
//            trace.flush();
//            trace.close();
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing report file...", e);
        }
        return null;
    }
}
