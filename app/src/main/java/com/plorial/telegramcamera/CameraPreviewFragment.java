package com.plorial.telegramcamera;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageButton;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
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
    public static int currentCameraId;
    private FrameLayout frameLayout;
    private boolean isSwitchCircleFilled = false;
    private ViewFlipper flashFlipper;
    private Camera.Parameters cameraParams;
    private ShotButtonOnTouchListener shotButtonOnTouchListener;
    private  SwitcherOnTouchListener shotRecordSwitcher;
    private BroadcastReceiver receiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.camera_preview_fragment, container, false);
        preview = new CameraPreview(getActivity());
        frameLayout = (FrameLayout) view.findViewById(R.id.camera_preview);
        frameLayout.addView(preview);
        shotRecordSwitcher = new SwitcherOnTouchListener(view, preview);
        frameLayout.setOnTouchListener(shotRecordSwitcher);
        final Animation animationRotate = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        AppCompatImageButton shotButton = (AppCompatImageButton) view.findViewById(R.id.shotButton);
        final AppCompatImageButton switchButton = (AppCompatImageButton) view.findViewById(R.id.switchButton);
        TextView tvVideoTiming = (TextView) view.findViewById(R.id.tvVideoTiming);
        tvVideoTiming.setVisibility(View.INVISIBLE);
        shotButtonOnTouchListener = new ShotButtonOnTouchListener(view);
        shotButton.setOnTouchListener(shotButtonOnTouchListener);
        flashFlipper = (ViewFlipper) view.findViewById(R.id.flashFlipper);
        if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            flashFlipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.flash_in));
            flashFlipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.flash_out));
            flashFlipper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeFlash();
                }
            });
        }else {
            flashFlipper.setVisibility(View.GONE);
        }
        final AppCompatImageButton switchCircle = (AppCompatImageButton) view.findViewById(R.id.switchButtonCircle);
        if (getAvailableCamera() == -1) {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
            switchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!SwitcherOnTouchListener.isRecording.get()) {
                        switchButton.startAnimation(animationRotate);
                        if (!isSwitchCircleFilled) {
                            AnimatedVectorDrawable drawableFilling = AnimatedVectorDrawable.getDrawable(getActivity(), R.drawable.switch_circle_filling_vector);
                            switchCircle.setImageDrawable(drawableFilling);
                            drawableFilling.start();
                            isSwitchCircleFilled = true;
                        } else {
                            AnimatedVectorDrawable drawableHollowing = AnimatedVectorDrawable.getDrawable(getActivity(), R.drawable.switch_circle_hollowing_vector);
                            switchCircle.setImageDrawable(drawableHollowing);
                            drawableHollowing.start();
                            isSwitchCircleFilled = false;
                        }
                        switchCamera();
                    }
                }
            });
        } else {
            currentCameraId = getAvailableCamera();
            switchButton.setVisibility(View.GONE);
            switchCircle.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        camera = getCameraInstance(currentCameraId);
        preview.setCamera(camera);
        cameraParams = camera.getParameters();
        cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        camera.setParameters(cameraParams);
        shotButtonOnTouchListener.setCamera(camera);
        shotRecordSwitcher.setCamera(camera);

    }

    private void changeFlash() {
        switch (cameraParams.getFlashMode()){
            case Camera.Parameters.FLASH_MODE_AUTO:
                cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(cameraParams);
                break;
            case Camera.Parameters.FLASH_MODE_TORCH:
                cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(cameraParams);
                break;
            case Camera.Parameters.FLASH_MODE_OFF:
                cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                camera.setParameters(cameraParams);
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
        shotButtonOnTouchListener.setCamera(camera);
        shotRecordSwitcher.setCamera(camera);
        preview.setCameraDisplayOrientation(getActivity(), currentCameraId, camera);
        try {
            camera.setPreviewDisplay(preview.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    private int getAvailableCamera(){
        int numCameras = Camera.getNumberOfCameras();
        if (numCameras == 1) {
            if(getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)){
                return Camera.CameraInfo.CAMERA_FACING_FRONT;
            }else {
                return Camera.CameraInfo.CAMERA_FACING_BACK;
            }
        } else {
            return -1;
        }
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
        if (SwitcherOnTouchListener.isRecording.get()){
            shotRecordSwitcher.stopRecording();
        }
        if (camera != null) {
            Log.d(TAG, "camera release");
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        shotRecordSwitcher.releaseMediaRecorder();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        receiver = new ScreenReceiver();
        getActivity().registerReceiver(receiver, filter);
    }

    class ScreenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
           if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                preview.setCameraDisplayOrientation(getActivity(), currentCameraId, camera);
                try {
                    camera.setPreviewDisplay(preview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                camera.startPreview();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null){
            getActivity().unregisterReceiver(receiver);
            receiver = null;
        }
    }
}
