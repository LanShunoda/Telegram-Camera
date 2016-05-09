package com.plorial.telegramcamera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

/**
 * Created by plorial on 5/9/16.
 */
public class CameraOpener {

    private static final String TAG = CameraOpener.class.getSimpleName();
    private Context context;
    private Camera camera;
    private ShotButtonOnTouchListener shotButtonOnTouchListener;
    private SwitcherOnTouchListener shotRecordSwitcher;
    private CameraPreview preview;
    private CameraPreviewFragment fragment;
    private Camera.Parameters cameraParams;

    public CameraOpener(Context context, ShotButtonOnTouchListener shotButtonOnTouchListener, SwitcherOnTouchListener shotRecordSwitcher, CameraPreview preview, CameraPreviewFragment fragment) {
        this.context = context;
        this.shotButtonOnTouchListener = shotButtonOnTouchListener;
        this.shotRecordSwitcher = shotRecordSwitcher;
        this.preview = preview;
        this.fragment = fragment;
    }

    public void open(){
        camera = getCameraInstance(CameraPreviewFragment.currentCameraId);

        preview.setCamera(camera);
        camera.setErrorCallback(new CameraErrorCallback(context, preview, shotButtonOnTouchListener, shotRecordSwitcher, fragment));
        cameraParams = camera.getParameters();
        cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        camera.setParameters(cameraParams);
        fragment.setCamera(camera);
        shotButtonOnTouchListener.setCamera(camera);
        shotRecordSwitcher.setCamera(camera);
        preview.setCameraDisplayOrientation(context, CameraPreviewFragment.currentCameraId, camera);
        try {
            camera.setPreviewDisplay(preview.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    private Camera getCameraInstance(int id){
        Camera c = null;
        try {
            if(checkCameraHardware(context)) {
                c = Camera.open(id);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "Camera isn't available");
        }
        return c;
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        } else {
            return false;
        }
    }
}
