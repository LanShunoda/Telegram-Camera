package com.plorial.telegramcamera;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
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

        final ViewFlipper flipper = (ViewFlipper) view.findViewById(R.id.viewflipper);

        flipper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float fromPosition = 0;
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN: // Пользователь нажал на экран, т.е. начало движения
                        // fromPosition - координата по оси X начала выполнения операции
                        fromPosition = event.getX();
                        break;
                    case MotionEvent.ACTION_UP: // Пользователь отпустил экран, т.е. окончание движения
                        float toPosition = event.getX();
                        if (fromPosition > toPosition)
                        {
                            flipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.go_next_in));
                            flipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.go_next_out));
                            flipper.showNext();
                        }
                        else if (fromPosition < toPosition)
                        {
                            flipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.go_prev_in));
                            flipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.go_prev_out));
                            flipper.showPrevious();
                        }
                    default:
                        break;
                }
                return true;
            }
        });


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
