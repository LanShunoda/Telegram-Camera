package com.plorial.telegramcamera;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import com.wnafee.vector.compat.AnimatedVectorDrawable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;


/**
 * Created by plorial on 5/1/16.
 */
public class ShotButtonOnTouchListener implements View.OnTouchListener, Camera.PictureCallback {

    public static final String TAG = ShotButtonOnTouchListener.class.getSimpleName();
    private Camera camera;
    private View view;
    private  ViewSwitcher switcher;
    private  ImageButton bCrop;
    private String photoFilePath;
    private MediaPlayer player;
    private Handler handler;

    public ShotButtonOnTouchListener(View view) {
        this.view = view;
        handler = new Handler();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                AnimatedVectorDrawable pressed = AnimatedVectorDrawable.getDrawable(v.getContext(), R.drawable.shot_button_pressing_vector);
                ((AppCompatImageButton)v).setImageDrawable(pressed);
                pressed.start();
                break;
            case MotionEvent.ACTION_UP:
                AnimatedVectorDrawable realised = AnimatedVectorDrawable.getDrawable(v.getContext(), R.drawable.shot_button_realising_vector);
                ((AppCompatImageButton)v).setImageDrawable(realised);
                realised.start();
                changePanels();
                takePicture();
                break;
        }
        return true;
    }

    private void changePanels(){

        switcher = (ViewSwitcher) view.findViewById(R.id.bottom_panel_switcher);
        Button bCancel = (Button) view.findViewById(R.id.buttonCancel);
        Button bDone = (Button) view.findViewById(R.id.buttonDone);
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.1f,1f,0.1f,1f,Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setDuration(SwitcherOnTouchListener.ANIM_SPEED/2);
        Animation inAnim = new AlphaAnimation(0, 1);
        inAnim.setDuration(SwitcherOnTouchListener.ANIM_SPEED/2);

        Animation outAnim = new AlphaAnimation(1, 0);
        outAnim.setDuration(SwitcherOnTouchListener.ANIM_SPEED/2);
        switcher.setInAnimation(inAnim);
        switcher.setOutAnimation(outAnim);
        switcher.showNext();
        ViewFlipper flashFlipper = (ViewFlipper) view.findViewById(R.id.flashFlipper);
        if(flashFlipper.getVisibility() != View.GONE) {
            flashFlipper.setVisibility(View.INVISIBLE);
        }
        bCrop = (ImageButton) view.findViewById(R.id.crop);
        bCancel.startAnimation(scaleAnimation);
        bDone.startAnimation(scaleAnimation);

        bCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCropFragment();
            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(photoFilePath);
                if(file != null) {
                    file.delete();
                }
                beginAgainCameraPreview();
            }
        });

        bDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginAgainCameraPreview();
            }

        });
    }

    private void startCropFragment() {
        CropFragment fragment = new CropFragment();
        Bundle bundle = new Bundle();
        bundle.putCharSequence(MainActivity.PHOTO_FILE_PATH, photoFilePath);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = ((Activity)view.getContext()).getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void beginAgainCameraPreview(){
        switcher.showNext();
        SwitcherOnTouchListener.isRecording.set(false);
        ViewFlipper flashFlipper = (ViewFlipper) view.findViewById(R.id.flashFlipper);
        if(flashFlipper.getVisibility() != View.GONE) {
            flashFlipper.setVisibility(View.VISIBLE);
        }
        if(player!=null) {
            if(player.isPlaying())
                player.stop();
            player.reset();
            player.release();
            player=null;
        }
        camera.startPreview();
    }

    @Override
    public void onPictureTaken(final byte[] data, Camera camera) {
        final File photoFile = createFile();
        photoFilePath = photoFile.getAbsolutePath();
        new Thread(new Runnable() {
            @Override
            public void run() {
                writePhoto(data, photoFile);
            }
        }).start();
    }

    private void takePicture(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                camera.takePicture(new Camera.ShutterCallback() {
                    @Override
                    public void onShutter() {
                        player = MediaPlayer.create(view.getContext(), R.raw.camera_shot_sound);
                        player.start();
                    }
                }, null, getPictureCallback());
            }
        },250);
    }

    private Camera.PictureCallback getPictureCallback(){
        return this;
    }

    private static void writePhoto(byte[] data, File file){
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File createFile(){
        File pictureFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File photo = new File(pictureFile,"IMG_" + timeStamp + ".jpg");
        return photo;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
}
