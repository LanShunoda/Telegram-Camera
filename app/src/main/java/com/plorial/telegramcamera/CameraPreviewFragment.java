package com.plorial.telegramcamera;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ViewFlipper;

/**
 * Created by plorial on 4/28/16.
 */
public class CameraPreviewFragment extends Fragment{

    private static final String TAG = CameraPreviewFragment.class.getSimpleName();

    private Camera camera;
    private CameraPreview preview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.camera_preview_fragment, container, false);
        camera = getCameraInstance();

        preview = new CameraPreview(getActivity(), camera);
        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.camera_preview);
        frameLayout.addView(preview);

        frameLayout.setOnTouchListener(new FlipperOnTouchListener(view));

        return view;
    }

    private Camera getCameraInstance(){
        Camera c = null;
        try {
            if(checkCameraHardware(getActivity())) {
                c = Camera.open();
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
