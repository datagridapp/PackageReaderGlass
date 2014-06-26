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

package com.datagridapp.packagereader;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.glass.widget.CardScrollView;
import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.google.zxing.client.result.ResultParser;
import com.google.zxing.client.result.TextParsedResult;
import com.google.zxing.client.result.URIParsedResult;

import java.io.IOException;

/**
 * @author Sean Owen
 */
public final class CaptureActivity extends Activity implements SurfaceHolder.Callback {

    private static final String TAG = CaptureActivity.class.getSimpleName();

    private boolean hasSurface;
    private SurfaceHolder holderWithCallback;
    private Camera camera;
    private DecodeRunnable decodeRunnable;
    private Result result;
    private CardScrollView mScrollView;
    private CardsAdapter mCardsAdapter;


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.capture);

        mCardsAdapter = new CardsAdapter(this);
        mScrollView = (CardScrollView) findViewById(R.id.status_view);
        mScrollView.setAdapter(mCardsAdapter);
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder?");
        }
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            holderWithCallback = surfaceHolder;
        }
    }

    @Override
    public synchronized void onPause() {
        result = null;
        if (decodeRunnable != null) {
            decodeRunnable.stop();
            decodeRunnable = null;
        }
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        if (holderWithCallback != null) {
            holderWithCallback.removeCallback(this);
            holderWithCallback = null;
        }
        super.onPause();
    }

    @Override
    public synchronized void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "Surface created");
        holderWithCallback = null;
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public synchronized void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // do nothing
    }

    @Override
    public synchronized void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "Surface destroyed");
        holderWithCallback = null;
        hasSurface = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (result != null) {
            switch (keyCode) {
//                case KeyEvent.KEYCODE_DPAD_CENTER:
//                    handleResult(result);
//                    return true;
                case KeyEvent.KEYCODE_BACK:
                    reset();
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initCamera(SurfaceHolder holder) {
        if (camera != null) {
            throw new IllegalStateException("Camera not null on initialization");
        }
        camera = Camera.open();
        if (camera == null) {
            throw new IllegalStateException("Camera is null");
        }

        CameraConfigurationManager.configure(camera);

        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            Log.e(TAG, "Cannot start prevew", e);
        }

        decodeRunnable = new DecodeRunnable(this, camera);
        new Thread(decodeRunnable).start();
        reset();
    }

    void setResult(Result result) {
        String[] textArr = result.getText().split(";");
        mCardsAdapter.setCards(textArr);

        mScrollView.setVisibility(View.VISIBLE);
        mScrollView.activate();

//        statusView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, textArr));
//        statusView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Math.max(14, 72 - text.length() / 2));
        this.result = result;
    }

    private void handleResult(Result result) {
        ParsedResult parsed = ResultParser.parseResult(result);
        Intent intent;
        if (parsed.getType() == ParsedResultType.URI) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(((URIParsedResult) parsed).getURI()));
        } else {
            intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra("query", ((TextParsedResult) parsed).getText());
        }
        startActivity(intent);
    }

    private void reset() {
        mScrollView.setVisibility(View.GONE);
        mScrollView.deactivate();
        result = null;
        decodeRunnable.startScanning();
    }

}
