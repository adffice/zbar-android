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

package com.gmail.adffice.zbar.sample.decode;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.gmail.adffice.zbar.SymbolSet;
import com.gmail.adffice.zbar.sample.CaptureActivity;
import com.gmail.adffice.zbar.sample.camera.CameraManager;

/**
 * This class handles all the messaging which comprises the state machine for
 * capture.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class CaptureActivityHandler extends Handler {
    public static final int MSG_RESTART_PREVIEW = 110;
    public static final int MSG_DECODE_SUCCEEDED = 111;
    public static final int MSG_DECODE_FAILED = 112;
    public static final int MSG_RETURN_SCAN_RESULT = 113;

    private static final String TAG = CaptureActivityHandler.class.getSimpleName();
    private final CaptureActivity activity;

    /**
     * 真正负责扫描任务的核心线程
     */
    private final DecodeThread decodeThread;

    private State state;

    private final CameraManager cameraManager;

    /**
     * 当前扫描的状态
     */
    private enum State {
        /**
         * 预览
         */
        PREVIEW,
        /**
         * 扫描成功
         */
        SUCCESS,
        /**
         * 结束扫描
         */
        DONE
    }

    public CaptureActivityHandler(CaptureActivity activity, CameraManager cameraManager) {
        this.activity = activity;

        // 启动扫描线程
        decodeThread = new DecodeThread(activity);
        decodeThread.start();

        state = State.SUCCESS;

        // Start ourselves capturing previews and decoding.
        this.cameraManager = cameraManager;

        // 开启相机预览界面
        cameraManager.startPreview();

        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case MSG_RESTART_PREVIEW: // 准备进行下一次扫描
                Log.d(TAG, "Got restart preview message");
                restartPreviewAndDecode();
                break;
            case MSG_DECODE_SUCCEEDED:
                Log.d(TAG, "Got decode succeeded message");
                state = State.SUCCESS;
                activity.handleDecode((String) message.obj);
                break;
            case MSG_DECODE_FAILED:
                // We're decoding as fast as possible, so when one decode fails,
                // start another.
                state = State.PREVIEW;
                cameraManager.requestPreviewFrame(decodeThread.getHandler(), DecodeThread.MSG_DECODE);
                break;
            case MSG_RETURN_SCAN_RESULT:
                Log.d(TAG, "Got return scan result message");
                activity.setResult(Activity.RESULT_OK, (Intent) message.obj);
                activity.finish();
                break;
            default:
                break;
        }
    }

    public void quitSynchronously() {
        state = State.DONE;
        cameraManager.stopPreview();
        Message quit = Message.obtain(decodeThread.getHandler(), DecodeThread.MSG_QUIT);
        quit.sendToTarget();

        try {
            // Wait at most half a second; should be enough time, and onPause()
            // will timeout quickly
            decodeThread.join(500L);
        } catch (InterruptedException e) {
            // continue
        }

        // Be absolutely sure we don't send any queued up messages
        removeMessages(MSG_DECODE_SUCCEEDED);
        removeMessages(MSG_DECODE_FAILED);
    }

    /**
     * 完成一次扫描后，只需要再调用此方法即可
     */
    private void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;

            // 向decodeThread绑定的handler（DecodeHandler)发送解码消息
            cameraManager.requestPreviewFrame(decodeThread.getHandler(), DecodeThread.MSG_DECODE);
        }
    }

}
