package com.plorial.telegramcamera;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ViewFlipper;

import com.wnafee.vector.compat.AnimatedVectorDrawable;

import java.io.IOException;

/**
 * Created by plorial on 4/28/16.
 */
public class CameraPreviewFragment extends Fragment{

    private static final String TAG = CameraPreviewFragment.class.getSimpleName();

    private Camera camera;
    private CameraPreview preview;
    private int currentCameraId;
    private FrameLayout frameLayout;
    private boolean isSwitchCircleFilled = false;
    private ViewFlipper flashFlipper;
    Camera.Parameters cameraParams;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.camera_preview_fragment, container, false);
        currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        camera = getCameraInstance(currentCameraId);
        cameraParams = camera.getParameters();

        preview = new CameraPreview(getActivity(), camera);

        frameLayout = (FrameLayout) view.findViewById(R.id.camera_preview);
        frameLayout.addView(preview);

        frameLayout.setOnTouchListener(new SwitcherOnTouchListener(view));
        final Animation animationRotate = AnimationUtils.loadAnimation(
                getActivity(), R.anim.rotate);
        AppCompatImageButton shotButton = (AppCompatImageButton) view.findViewById(R.id.shotButton);
        shotButton.setOnTouchListener(new ShotButtonOnTouchListener());
        final AppCompatImageButton switchButton = (AppCompatImageButton) view.findViewById(R.id.switchButton);

        flashFlipper = (ViewFlipper) view.findViewById(R.id.flashFlipper);
        cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        flashFlipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.flash_in));
        flashFlipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.flash_out));
        flashFlipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFlash();
            }
        });

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchButton.startAnimation(animationRotate);
                AppCompatImageButton switchCircle = (AppCompatImageButton) view.findViewById(R.id.switchButtonCircle);
                if(!isSwitchCircleFilled) {
                    AnimatedVectorDrawable drawableFilling = AnimatedVectorDrawable.getDrawable(getActivity(), R.drawable.switch_circle_filling_vector);
                    switchCircle.setImageDrawable(drawableFilling);
                    drawableFilling.start();
                    isSwitchCircleFilled = true;
                }else{
                    AnimatedVectorDrawable drawableHollowing = AnimatedVectorDrawable.getDrawable(getActivity(), R.drawable.switch_circle_hollowing_vector);
                    switchCircle.setImageDrawable(drawableHollowing);
                    drawableHollowing.start();
                    isSwitchCircleFilled = false;
                }
                switchCamera();
            }
        });

        return view;
    }

    private void changeFlash() {
        switch (cameraParams.getFlashMode()){
            case Camera.Parameters.FLASH_MODE_AUTO:
                cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                break;
            case Camera.Parameters.FLASH_MODE_TORCH:
                cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                break;
            case Camera.Parameters.FLASH_MODE_OFF:
                cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                break;
        }
        flashFlipper.showNext();
    }

    private void switchCamera(){
        camera.stopPreview();
        camera.release();
        if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
        else {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        camera = Camera.open(currentCameraId);

        CameraPreview.setCameraDisplayOrientation(getActivity(), currentCameraId, camera);
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
            if(checkCameraHardware(getActivity())) {
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

    @Override
    public void onPause() {
        super.onPause();
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
}
