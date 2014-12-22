/*
 * Copyright (C) 2010 ZXing authors
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

package com.gmail.adffice.zbar.sample.decode;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.gmail.adffice.zbar.Config;
import com.gmail.adffice.zbar.Image;
import com.gmail.adffice.zbar.ImageScanner;
import com.gmail.adffice.zbar.Symbol;
import com.gmail.adffice.zbar.SymbolSet;
import com.gmail.adffice.zbar.sample.CaptureActivity;


final class DecodeHandler extends Handler {
    static final int MSG_DECODE = 1;
    static final int MSG_QUIT = 2;
    private static final String TAG = DecodeHandler.class.getSimpleName();
    private boolean running = true;
    private final CaptureActivity activity;
    private ImageScanner scanner;

    DecodeHandler(CaptureActivity activity) {
        this.activity = activity;

        /* Instance barcode scanner */
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.ENABLE, 1);

    }

    @Override
    public void handleMessage(Message message) {
        if (!running) {
            return;
        }
        switch (message.what) {
            case MSG_DECODE:
                decode((byte[]) message.obj, message.arg1, message.arg2);
                break;
            case MSG_QUIT:
                running = false;
                Looper.myLooper().quit();
                break;
            default:
                break;
        }
    }

    /**
     * Decode the data within the viewfinder rectangle, and time how long it
     * took. For efficiency, reuse the same reader objects from one decode to
     * the next.
     *
     * @param data   The YUV preview frame.
     * @param width  The width of the preview frame.
     * @param height The height of the preview frame.
     */
    private void decode(byte[] data, int width, int height) {
        long start = System.currentTimeMillis();

        byte[] rotatedData = new byte[data.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)
                rotatedData[x * height + height - y - 1] = data[x + y * width];
        }
        int tmp = width;
        width = height;
        height = tmp;

        Rect cropRect = activity.getCropRect();
        Image barcode = new Image();
        barcode.setFormat("Y800");
        barcode.setSize(width, height);
        barcode.setData(rotatedData);
        barcode.setCrop(cropRect.left, cropRect.top, cropRect.width(), cropRect.height());

        int result = scanner.scanImage(barcode.convert("Y800"));
        Handler handler = activity.getHandler();
        if (result != 0) {
            SymbolSet syms = scanner.getResults();
            // Don't log the barcode contents for security.
            long end = System.currentTimeMillis();
            Log.d(TAG, "Found barcode in " + (end - start) + " ms");
            String barcodeResult = null;
            for (Symbol sym : syms) {
                barcodeResult = sym.getData();
                Log.d(TAG, "barcode result " + barcodeResult);
            }
            if (handler != null) {
                Message message = Message.obtain(handler, CaptureActivityHandler.MSG_DECODE_SUCCEEDED, barcodeResult);
                message.sendToTarget();
            }
        } else {
            if (handler != null) {
                Message message = Message.obtain(handler, CaptureActivityHandler.MSG_DECODE_FAILED);
                message.sendToTarget();
            }
        }
    }

}
