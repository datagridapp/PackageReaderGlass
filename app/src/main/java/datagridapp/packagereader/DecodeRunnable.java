/*
 * Copyright (C) 2014 ZXing authors
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

package datagridapp.packagereader;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author Sean Owen
 */
final class DecodeRunnable implements Runnable, Camera.PreviewCallback {

  private static final String TAG = DecodeRunnable.class.getSimpleName();

  private final CaptureActivity activity;
  private final Camera camera;
  private final int height;
  private final int width;
  private boolean running;
  private Handler handler;
  private final CountDownLatch handlerInitLatch;

  DecodeRunnable(CaptureActivity activity, Camera camera) {
    this.activity = activity;
    this.camera = camera;
    Camera.Parameters parameters = camera.getParameters();
    Camera.Size previewSize = parameters.getPreviewSize();
    height = previewSize.height;
    width = previewSize.width;
    running = true;
    handlerInitLatch = new CountDownLatch(1);
  }

  private Handler getHandler() {
    try {
      handlerInitLatch.await();
    } catch (InterruptedException ie) {
      // continue?
    }
    return handler;
  }


  @Override
  public void run() {
    Looper.prepare();
    handler = new DecodeHandler();
    handlerInitLatch.countDown();
    Looper.loop();
  }

  void startScanning() {
    getHandler().obtainMessage(R.id.decode_failed).sendToTarget();
  }

  void stop() {
    getHandler().obtainMessage(R.id.quit).sendToTarget();
  }

  @Override
  public void onPreviewFrame(byte[] data, Camera camera) {
    if (running) {
      getHandler().obtainMessage(R.id.decode, data).sendToTarget();
    }
  }

  private final class DecodeHandler extends Handler {

    private final Map<DecodeHintType,Object> hints;

    DecodeHandler() {
      hints = new EnumMap<DecodeHintType,Object>(DecodeHintType.class);
      hints.put(DecodeHintType.POSSIBLE_FORMATS,
          Arrays.asList(BarcodeFormat.AZTEC, BarcodeFormat.QR_CODE, BarcodeFormat.DATA_MATRIX));
    }

    @Override
    public void handleMessage(Message message) {
      if (!running) {
        return;
      }
      switch (message.what) {
        case R.id.decode:
          decode((byte[]) message.obj);
          break;
        case R.id.decode_succeeded:
          final Result result = (Result) message.obj;
          activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
              activity.setResult(result);
            }
          });
          break;
        case R.id.decode_failed:
          camera.setOneShotPreviewCallback(DecodeRunnable.this);
          break;
        case R.id.quit:
          running = false;
          Looper.myLooper().quit();
          break;
      }
    }

    private void decode(byte[] data) {
      Result rawResult = null;
      PlanarYUVLuminanceSource source =
          new PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false);
      BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
      try {
        rawResult = new MultiFormatReader().decode(bitmap, hints);
      } catch (ReaderException re) {
        // continue
      }

      Handler handler = getHandler();
      Message message;
      if (rawResult == null) {
        message = handler.obtainMessage(R.id.decode_failed);
      } else {
        Log.i(TAG, "Decode succeeded: " + rawResult.getText());
        message = handler.obtainMessage(R.id.decode_succeeded, rawResult);
      }
      message.sendToTarget();
    }

  }

}
