package com.plorial.telegramcamera;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.camera_preview_fragment, container, false);
        currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        camera = getCameraInstance(currentCameraId);

        preview = new CameraPreview(getActivity(), camera);

        frameLayout = (FrameLayout) view.findViewById(R.id.camera_preview);
        frameLayout.addView(preview);

        frameLayout.setOnTouchListener(new SwitcherOnTouchListener(view));
        final Animation animationRotate = AnimationUtils.loadAnimation(
                getActivity(), R.anim.rotate);

        final AppCompatImageButton switchButton = (AppCompatImageButton) view.findViewById(R.id.switchButton);

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchButton.startAnimation(animationRotate);
                switchCamera();
                AppCompatImageButton switchCircle = (AppCompatImageButton) view.findViewById(R.id.switchButtonCircle);
                VectorDrawableCompat drawable = (VectorDrawableCompat) switchCircle.getDrawable();
                if (drawable instanceof Animatable){
                    Log.d(TAG,"anim");
                    ((Animatable) drawable).start();
                }
            }
        });

        return view;
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
