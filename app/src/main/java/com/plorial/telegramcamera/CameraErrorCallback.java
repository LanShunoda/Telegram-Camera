package com.plorial.telegramcamera;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by plorial on 5/9/16.
 */
public class CameraErrorCallback implements Camera.ErrorCallback {

    private static final String TAG = CameraErrorCallback.class.getSimpleName();
    private Context context;
    private CameraPreview preview;
    private ShotButtonOnTouchListener shotButtonOnTouchListener;
    private SwitcherOnTouchListener shotRecordSwitcher;
    private CameraPreviewFragment fragment;

    public CameraErrorCallback(Context context, CameraPreview preview, ShotButtonOnTouchListener shotButtonOnTouchListener, SwitcherOnTouchListener shotRecordSwitcher, CameraPreviewFragment fragment) {
        this.context = context;
        this.preview = preview;
        this.shotButtonOnTouchListener = shotButtonOnTouchListener;
        this.shotRecordSwitcher = shotRecordSwitcher;
        this.fragment = fragment;
    }

    @Override
    public void onError(int error, Camera camera) {
        if(error == Camera.CAMERA_ERROR_SERVER_DIED) {
            Toast.makeText(context,"Camera service died! Restarting camera...", Toast.LENGTH_LONG);
            try {
                camera.stopPreview();
                camera.release();
                CameraOpener opener = new CameraOpener(context,shotButtonOnTouchListener,shotRecordSwitcher,preview,fragment);
                opener.open();
                Toast.makeText(context,"Camera restarted after service died", Toast.LENGTH_LONG);
            }
            catch(Exception e){
                Log.e(TAG,"Exception restarting camera",e);
            }
        }
    }
}
