package com.plorial.telegramcamera;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import com.wnafee.vector.compat.AnimatedVectorDrawable;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by plorial on 4/28/16.
 */
public class SwitcherOnTouchListener implements View.OnTouchListener {

    private static final String TAG = SwitcherOnTouchListener.class.getSimpleName();

    public static final int ANIM_SPEED = 500;
    private final AppCompatImageButton recordButton;
    private final CameraPreview preview;

    private float fromPosition = 0;
    private View view;

    private AppCompatImageView circle1;
    private AppCompatImageView circle2;

    private ViewSwitcher switcher;
    private ImageView bottomPanelBackground;
    private AppCompatImageButton switchButton;
    private AppCompatImageButton switchButtonCircle;
    private ViewFlipper flashFlipper;
    private TextView tvVideoTiming;

    private int colorPicture;
    private int colorVideo;
    public static boolean isRecording = false;

    private Animation inAnim;
    private Animation outAnim;
    private MediaRecorder recorder;
    private Camera camera;

    public SwitcherOnTouchListener(View view,CameraPreview preview) {
        this.view = view;
        this.preview = preview;
        isRecording = false;
        circle1 = (AppCompatImageView) view.findViewById(R.id.imageCircle1);
        circle2 = (AppCompatImageView) view.findViewById(R.id.imageCircle2);
        bottomPanelBackground = (ImageView) view.findViewById(R.id.bottomPanelBackground);
        circle1.setImageResource(R.drawable.circle_white);
        circle2.setImageResource(R.drawable.circle_black);
        switcher = (ViewSwitcher) view.findViewById(R.id.switcher);
        recordButton = (AppCompatImageButton) view.findViewById(R.id.recordButton);
        switchButton = (AppCompatImageButton) view.findViewById(R.id.switchButton);
        switchButtonCircle = (AppCompatImageButton) view.findViewById(R.id.switchButtonCircle);
        flashFlipper = (ViewFlipper) view.findViewById(R.id.flashFlipper);
        tvVideoTiming = (TextView) view.findViewById(R.id.tvVideoTiming);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
            }
        });

        colorPicture = view.getContext().getResources().getColor(R.color.colorPicture);
        colorVideo = view.getContext().getResources().getColor(R.color.colorVideo);

        inAnim = new AlphaAnimation(0, 1);
        inAnim.setDuration(ANIM_SPEED/2);

        outAnim = new AlphaAnimation(1, 0);
        outAnim.setDuration(ANIM_SPEED/2);

        switcher.setInAnimation(inAnim);
        switcher.setOutAnimation(outAnim);
    }

    private void startRecording() {
        AnimatedVectorDrawable starting = AnimatedVectorDrawable.getDrawable(view.getContext(), R.drawable.recording_button_starting_vector);
        AnimatedVectorDrawable stoping = AnimatedVectorDrawable.getDrawable(view.getContext(), R.drawable.recording_button_stoping_vector);
        Animation bottomPanelSlidingDown = AnimationUtils.loadAnimation(view.getContext(),R.anim.bottom_panel_sliding_down);
        Animation bottomPanelSlidingUp = AnimationUtils.loadAnimation(view.getContext(),R.anim.bottom_panel_sliding_up);

        if(isRecording) {
            recordButton.setImageDrawable(stoping);
            stoping.start();
            bottomPanelBackground.setVisibility(View.VISIBLE);
            bottomPanelBackground.startAnimation(bottomPanelSlidingUp);
            tvVideoTiming.setVisibility(View.INVISIBLE);
            makeViewsAppear();
            isRecording = false;
            if (recorder != null) {
                Log.d(TAG, "stop recording");
                recorder.stop();
                releaseMediaRecorder();
            }
        }else {
            recordButton.setImageDrawable(starting);
            starting.start();
            bottomPanelBackground.startAnimation(bottomPanelSlidingDown);
            bottomPanelBackground.setVisibility(View.INVISIBLE);
            tvVideoTiming.setVisibility(View.VISIBLE);
            makeViewsDisappear();
            isRecording = true;
            if (prepareVideoRecorder()) {
                Log.d(TAG, "start recording");
                recorder.start();
            } else {
                releaseMediaRecorder();
            }
        }
    }

    private boolean prepareVideoRecorder() {
        camera.unlock();
        recorder = new MediaRecorder();
        Log.d(TAG, "prepare recording");
        recorder.setCamera(camera);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        recorder.setProfile(CamcorderProfile.get(CameraPreviewFragment.currentCameraId, CamcorderProfile.QUALITY_HIGH));
        recorder.setOutputFile(getVideoFile().getAbsolutePath());
        recorder.setPreviewDisplay(preview.getHolder().getSurface());

        try {
            recorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    public void releaseMediaRecorder() {
        if (recorder != null) {
            Log.d(TAG, "realise recorder");
            recorder.reset();
            recorder.release();
            recorder = null;
            camera.lock();
        }
    }

    private File getVideoFile(){
        File videoFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        Date date = new Date();
        File video = new File(videoFile,"VID " + date +".mp4");
        Log.d(TAG, "video file created " + video.getAbsolutePath());
        return video;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    private void makeViewsAppear(){
        circle1.startAnimation(inAnim);
        circle2.startAnimation(inAnim);
        switchButton.startAnimation(inAnim);
        switchButtonCircle.startAnimation(inAnim);
        flashFlipper.setAnimation(inAnim);
        circle1.setVisibility(View.VISIBLE);
        circle2.setVisibility(View.VISIBLE);
        switchButton.setVisibility(View.VISIBLE);
        switchButtonCircle.setVisibility(View.VISIBLE);
        flashFlipper.setVisibility(View.VISIBLE);
    }

    private void makeViewsDisappear(){
        circle1.startAnimation(outAnim);
        circle2.startAnimation(outAnim);
        switchButton.startAnimation(outAnim);
        switchButtonCircle.startAnimation(outAnim);
        flashFlipper.startAnimation(outAnim);
        circle1.setVisibility(View.INVISIBLE);
        circle2.setVisibility(View.INVISIBLE);
        switchButton.setVisibility(View.INVISIBLE);
        switchButtonCircle.setVisibility(View.INVISIBLE);
        flashFlipper.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(!isRecording) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    fromPosition = event.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    float toPosition = event.getX();
                    if (fromPosition < toPosition && switcher.getCurrentView() == switcher.getChildAt(0)) {
                        switcher.showNext();
                        circle1.setImageResource(R.drawable.circle_black);
                        circle2.setImageResource(R.drawable.circle_white);

                        AnimatedVectorDrawable extending = AnimatedVectorDrawable.getDrawable(view.getContext(), R.drawable.record_button_extending_vector);
                        recordButton.setImageDrawable(extending);
                        extending.start();
                        bottomBackgroundAnim(colorPicture, colorVideo);
                    } else if (fromPosition > toPosition && switcher.getCurrentView() == switcher.getChildAt(1)) {
                        switcher.showPrevious();
                        circle1.setImageResource(R.drawable.circle_white);
                        circle2.setImageResource(R.drawable.circle_black);

                        AnimatedVectorDrawable constriction = AnimatedVectorDrawable.getDrawable(view.getContext(), R.drawable.record_button_constriction_vector);
                        recordButton.setImageDrawable(constriction);
                        constriction.start();

                        bottomBackgroundAnim(colorVideo, colorPicture);
                    }
                default:
                    break;
            }
        }
        return true;
    }

    private void bottomBackgroundAnim(int colorFrom, int colorTo){
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(ANIM_SPEED);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                bottomPanelBackground.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
    }
}
