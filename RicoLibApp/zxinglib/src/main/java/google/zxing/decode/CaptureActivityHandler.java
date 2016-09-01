/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package google.zxing.decode;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.Vector;

import google.zxing.Captureable;
import google.zxing.camera.CameraManager;
import google.zxing.view.ViewfinderResultPointCallback;


/**
 * This class handles all the messaging which comprises the state machine for
 * capture.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class CaptureActivityHandler extends Handler {

	public static final int AUTO_FOCUS = 201;
	public static final int RESTART_PREVIEW = 202;
	public static final int DECODE_SUCCEEDED = 203;
	public static final int DECODE_FAILED = 204;
	public static final int DECODE = 205;
	public static final int RETURN_SCAN_RESULT = 206;
	public static final int LAUNCH_PRODUCT_QUERY = 207;
	public static final int QUIT = 208;

	private static final String TAG = CaptureActivityHandler.class
			.getSimpleName();

	private final Captureable activity;
	private final DecodeThread decodeThread;
	private State state;

	private enum State {
		PREVIEW, SUCCESS, DONE
	}

	public CaptureActivityHandler(Captureable activity,
			Vector<BarcodeFormat> decodeFormats, String characterSet) {
		this.activity = activity;
		decodeThread = new DecodeThread(activity, decodeFormats, characterSet,
				 new ViewfinderResultPointCallback(activity.getViewfinderView()));
		decodeThread.start();
		state = State.SUCCESS;

		// Start ourselves capturing previews and decoding.
		CameraManager.get().startPreview();
		restartPreviewAndDecode();
	}

	@Override
	public void handleMessage(Message message) {
		if (message.what == AUTO_FOCUS) {
			// Log.d(TAG, "Got auto-focus message");
			// When one auto focus pass finishes, start another. This is the
			// closest thing to
			// continuous AF. It does seem to hunt a bit, but I'm not sure what
			// else to do.
			if (state == State.PREVIEW) {
				CameraManager.get().requestAutoFocus(this,AUTO_FOCUS);
			}
		} else if (message.what == RESTART_PREVIEW) {
			Log.d(TAG, "Got restart preview message");
			restartPreviewAndDecode();
		} else if (message.what == DECODE_SUCCEEDED) {
			Log.d(TAG, "Got decode succeeded message");
			state = State.SUCCESS;
			Bundle bundle = message.getData();
			Bitmap barcode = bundle == null ? null : (Bitmap) bundle
					.getParcelable(DecodeThread.BARCODE_BITMAP);
			activity.handleDecode((Result) message.obj, barcode);
		} else if (message.what == DECODE_FAILED) {
			// We're decoding as fast as possible, so when one decode fails,
			// start another.
			state = State.PREVIEW;
			CameraManager.get().requestPreviewFrame(decodeThread.getHandler(),
					DECODE);
		} else if (message.what == RETURN_SCAN_RESULT) {
			Log.d(TAG, "Got return scan result message");
			activity.getActivity().setResult(Activity.RESULT_OK, (Intent) message.obj);
			activity.getActivity().finish();
		} else if (message.what == LAUNCH_PRODUCT_QUERY) {
			Log.d(TAG, "Got product query message");
			String url = (String) message.obj;
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			activity.getActivity().startActivity(intent);
		}
	}

	public void quitSynchronously() {
		state = State.DONE;
		CameraManager.get();
		CameraManager.stopPreview();
		Message quit = Message.obtain(decodeThread.getHandler(), QUIT);
		quit.sendToTarget();
		try {
			decodeThread.join();
		} catch (InterruptedException e) {
			// continue
		}

		// Be absolutely sure we don't send any queued up messages
		removeMessages(DECODE_SUCCEEDED);
		removeMessages(DECODE_FAILED);
	}

	public void restartPreviewAndDecode() {
		if (state == State.SUCCESS) {
			state = State.PREVIEW;
			CameraManager.get().requestPreviewFrame(decodeThread.getHandler(),
					DECODE);
			CameraManager.get().requestAutoFocus(this, AUTO_FOCUS);
			activity.drawViewfinder();
		}
	}

}
