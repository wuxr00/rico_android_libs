package google.zxing;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;

import com.google.zxing.Result;

import google.zxing.camera.CameraManager;
import google.zxing.view.ViewfinderView;

/**
 * Created by Rico on 16/1/20.
 */
public interface Captureable {
    public Activity getActivity();
    public ViewfinderView getViewfinderView();
    public void handleDecode(Result obj, Bitmap barcode);
    public Handler getHandler();
    public void drawViewfinder();

    public CameraManager getCameraManager();
}
