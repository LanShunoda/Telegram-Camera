package com.plorial.telegramcamera;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;

/**
 * Created by plorial on 4/27/16.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private final static String TAG = CameraPreview.class.getSimpleName();
    private SurfaceHolder holder;
    private Camera camera;
    private Context context;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.camera = camera;
        this.context = context;

        holder = getHolder();
        holder.addCallback(this);

        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
            setPreviewSize();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        if (this.holder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        try {
            camera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }
        setCameraDisplayOrientation(0);
        try {
            camera.setPreviewDisplay(this.holder);
            camera.startPreview();
        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    void setPreviewSize() {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        boolean widthIsMax = display.getWidth() > display.getHeight();

        Camera.Size size = camera.getParameters().getPreviewSize();

        RectF rectDisplay = new RectF();
        RectF rectPreview = new RectF();

        rectDisplay.set(0, 0, display.getWidth(), display.getHeight());

        if (widthIsMax) {
            rectPreview.set(0, 0, size.width, size.height);
        } else {
            rectPreview.set(0, 0, size.height, size.width);
        }

        Matrix matrix = new Matrix();

        matrix.setRectToRect(rectDisplay, rectPreview, Matrix.ScaleToFit.START);
        matrix.invert(matrix);

        matrix.mapRect(rectPreview);

        getLayoutParams().height = (int) (rectPreview.bottom);
        getLayoutParams().width = (int) (rectPreview.right);
    }

    void setCameraDisplayOrientation(int cameraId) {
        int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result = 0;
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
            result = ((360 - degrees) + info.orientation);
        } else
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = ((360 - degrees) - info.orientation);
                result += 360;
            }
        result = result % 360;
        camera.setDisplayOrientation(result);
    }
}
