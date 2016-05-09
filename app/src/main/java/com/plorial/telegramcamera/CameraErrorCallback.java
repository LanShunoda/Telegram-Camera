package com.plorial.telegramcamera;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;

import java.io.IOException;

/**
 * Created by plorial on 5/9/16.
 */
public class CameraErrorCallback implements Camera.ErrorCallback {

    private static final String TAG = CameraErrorCallback.class.getSimpleName();
    private Context context;
    private CameraPreview preview;

    public CameraErrorCallback(Context context, CameraPreview preview) {
        this.context = context;
        this.preview = preview;
    }

    @Override
    public void onError(int error, Camera camera) {
        if(error == Camera.CAMERA_ERROR_SERVER_DIED) {
            try {
                camera.stopPreview();
                camera.release();
                camera = Camera.open(CameraPreviewFragment.currentCameraId);
                preview.setCameraDisplayOrientation(context, CameraPreviewFragment.currentCameraId, camera);
                try {
                    camera.setPreviewDisplay(preview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                camera.startPreview();
            }
            catch(Exception e){
                Log.e(TAG,"Exception restarting camera",e);
            }
        }
    }
}
