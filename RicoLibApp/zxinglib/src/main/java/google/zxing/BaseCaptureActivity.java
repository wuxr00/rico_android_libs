package google.zxing;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import google.zxing.camera.CameraManager;
import google.zxing.decode.CaptureActivityHandler;
import google.zxing.decode.InactivityTimer;
import google.zxing.decode.RGBLuminanceSource;
import google.zxing.view.ViewfinderView;


public abstract class BaseCaptureActivity extends Activity implements Callback, Captureable {

    public static final int REQUEST_CODE_QRCODE = 200;

    public static final String BUNDLE_RESULT = "codeResult";
    protected String TAG = "二维码扫描";
    protected String mCoderNumber;
    protected CaptureActivityHandler handler;
    protected ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    @SuppressWarnings("unused")
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    protected SurfaceView surfaceView;
    protected SurfaceHolder surfaceHolder;

//    private Camera camera;
    // private boolean hasStartPreview = false;
//    private Parameters parameter;


    /**
     * Called when the activity is first created.
     */
    public void init(SurfaceView surfaceView, ViewfinderView viewfinderView) {
//        setContentView(R.layout.zxing_activity_scan);
        CameraManager.init(getApplication());
        this.viewfinderView = viewfinderView;
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        this.surfaceView = surfaceView;

    }

    @Override
    public CameraManager getCameraManager() {
        return CameraManager.get();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        if (surfaceView != null) {
            surfaceHolder = surfaceView.getHolder();
            if (hasSurface) {
                initCamera(surfaceHolder);
            } else {
                surfaceHolder.addCallback(this);
                surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            }
            decodeFormats = null;
            characterSet = null;

            playBeep = true;
            AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
            if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
                playBeep = false;
            }
            initBeepSound();
            vibrate = true;


//            viewfinderView.startAnim();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();

    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        viewfinderView.destroyView();
        // if (camera != null) {
        // CameraManager.stopPreview();
        // }
        // 反注册
        super.onDestroy();

    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();

    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
        CameraManager.stopPreview();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        Log.e(TAG, "drawViewfinder");
        viewfinderView.drawViewfinder();

    }

    //完成调用这个
    public void handleDecode(Result obj, Bitmap barcode) {
        inactivityTimer.onActivity();
//		viewfinderView.drawResultBitmap(barcode);
        playBeepSoundAndVibrate();
        //	txtResult.setVisibility(View.VISIBLE);
        //	txtResult.setText(obj.getBarcodeFormat().toString() + ":"
        //			+ obj.getText());

//		if(obj.getText()!=null) {
//			scanningImage(barcode);

        this.mCoderNumber = obj.getText();
//		Toast.makeText(this, "获取到数据:" + obj.getText() + "。", Toast.LENGTH_LONG).show();
//        setResult(RESULT_OK, new Intent().putExtra(BUNDLE_RESULT, obj.getText()));
        codeDecoded(obj.getText());
//        finish();
    }

    abstract protected void codeDecoded(String data);

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    private boolean checkReadInfo(String info) {


        if (info.contains("|")) {
            String[] arrInfo = info.split("\\|");
            if (arrInfo.length == 3) {
                //	Constant.branchName = arrInfo[0];
                try {
                    //Constant.branchId =
                    Integer.parseInt(arrInfo[1]);
                    //Constant.tableCode =
                    Integer.parseInt(arrInfo[2]);
                    return true;
                } catch (NumberFormatException NFE) {
                    return false;
                }
            }
        }
        return false;
    }

    private boolean scanningImage(Bitmap bitmap) {

        Map<DecodeHintType, String> hints = new HashMap<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");

        // 获得待解析的图片
        RGBLuminanceSource source = new RGBLuminanceSource(bitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        Result result;
        try {
            result = reader.decode(bitmap1);
            // ".","|"为正则表达式的特殊字符
            String branchInfo = result.toString();
            return checkReadInfo(branchInfo);
            // 得到解析后的文字

        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    // 字符序列转换为16进制字符串
    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("0x");
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            System.out.println(buffer);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            BaseCaptureActivity.this.finish();
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {

        } else if (keyCode == KeyEvent.KEYCODE_MENU) {

        }
        return false;
    }


}