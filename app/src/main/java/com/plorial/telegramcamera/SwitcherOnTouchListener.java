package com.plorial.telegramcamera;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import com.wnafee.vector.compat.AnimatedVectorDrawable;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private ImageView videoThumb;

    private int colorPicture;
    private int colorVideo;
    public static AtomicBoolean isRecording;

    private Animation inAnim;
    private Animation outAnim;
    private MediaRecorder recorder;
    private Camera camera;
    private File currentVideoFile;

    private Timer timer;
    private final Handler uiHandler = new Handler();

    public SwitcherOnTouchListener(View view,CameraPreview preview) {
        this.view = view;
        this.preview = preview;
        isRecording = new AtomicBoolean(false);
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
        videoThumb = (ImageView) view.findViewById(R.id.thumbImage);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
            }
        });
        videoThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVideoPlayFragment();
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

    private void startVideoPlayFragment() {
        VideoPlayerFragment fragment = new VideoPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putCharSequence(MainActivity.VIDEO_FILE_PATH, currentVideoFile.getAbsolutePath());
        fragment.setArguments(bundle);
        FragmentTransaction transaction = ((Activity)view.getContext()).getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void startRecording() {
        AnimatedVectorDrawable starting = AnimatedVectorDrawable.getDrawable(view.getContext(), R.drawable.recording_button_starting_vector);
        AnimatedVectorDrawable stoping = AnimatedVectorDrawable.getDrawable(view.getContext(), R.drawable.recording_button_stoping_vector);
        Animation bottomPanelSlidingDown = AnimationUtils.loadAnimation(view.getContext(),R.anim.bottom_panel_sliding_down);
        Animation bottomPanelSlidingUp = AnimationUtils.loadAnimation(view.getContext(),R.anim.bottom_panel_sliding_up);

        if(isRecording.get()) {
            recordButton.setImageDrawable(stoping);
            stoping.start();
            bottomPanelBackground.setVisibility(View.VISIBLE);
            bottomPanelBackground.startAnimation(bottomPanelSlidingUp);
            tvVideoTiming.setVisibility(View.INVISIBLE);
            makeViewsAppear();
            if (recorder != null) {
                Log.d(TAG, "stop recording");
                try {
                    recorder.stop();
                }catch (RuntimeException e){
                    e.printStackTrace();       // video stoped too fast, android doesn't properly create file
                    currentVideoFile.delete(); // http://stackoverflow.com/questions/10147563/android-mediarecorder-stop-failed
                } finally {
                    releaseMediaRecorder();
                    timer.cancel();
                    isRecording.set(false);
                }
            }
            showVideoThumb();
        }else {
            recordButton.setImageDrawable(starting);
            starting.start();
            bottomPanelBackground.startAnimation(bottomPanelSlidingDown);
            bottomPanelBackground.setVisibility(View.INVISIBLE);
            tvVideoTiming.setVisibility(View.VISIBLE);
            makeViewsDisappear();
            isRecording.set(true);
            if (prepareVideoRecorder()) {
                Log.d(TAG, "start recording");
                recorder.start();
            } else {
                releaseMediaRecorder();
            }
            timer = new Timer();
            timer.schedule(new UpdateTimeTask(uiHandler,tvVideoTiming), 0, 1000);
        }
    }

    private void showVideoThumb() {
        if(currentVideoFile != null && currentVideoFile.exists()) {
            Log.d(TAG,"show thumb");
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(currentVideoFile.getAbsolutePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
            videoThumb.setImageBitmap(thumb);
            videoThumb.setVisibility(View.VISIBLE);
            videoThumb.startAnimation(AnimationUtils.loadAnimation(view.getContext(),R.anim.video_thumb_in));
            uiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    videoThumb.startAnimation(AnimationUtils.loadAnimation(view.getContext(),R.anim.video_thumb_out));
                    videoThumb.setVisibility(View.INVISIBLE);
                }
            },5000);
        }
    }

    private boolean prepareVideoRecorder() {
        camera.unlock();
        recorder = new MediaRecorder();
        Log.d(TAG, "prepare recording");
        recorder.setCamera(camera);
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        recorder.setProfile(CamcorderProfile.get(CameraPreviewFragment.currentCameraId, CamcorderProfile.QUALITY_HIGH));
        currentVideoFile = getVideoFile();
        recorder.setOutputFile(currentVideoFile.getAbsolutePath());
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File video = new File(videoFile,"VID_" + timeStamp + ".mp4");
        Log.d(TAG, "video file created " + video.getAbsolutePath());
        return video;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    private void makeViewsAppear(){
        circle1.startAnimation(inAnim);
        circle2.startAnimation(inAnim);
        if(switchButton.getVisibility() != View.GONE) {
            switchButton.startAnimation(inAnim);
            switchButtonCircle.startAnimation(inAnim);
            switchButton.setVisibility(View.VISIBLE);
            switchButtonCircle.setVisibility(View.VISIBLE);
        }
        if(flashFlipper.getVisibility() != View.GONE) {
            flashFlipper.setAnimation(inAnim);
            flashFlipper.setVisibility(View.VISIBLE);
        }
        circle1.setVisibility(View.VISIBLE);
        circle2.setVisibility(View.VISIBLE);
    }

    private void makeViewsDisappear(){
        circle1.startAnimation(outAnim);
        circle2.startAnimation(outAnim);
        if(switchButton.getVisibility() != View.GONE) {
            switchButton.startAnimation(outAnim);
            switchButtonCircle.startAnimation(outAnim);
            switchButton.setVisibility(View.INVISIBLE);
            switchButtonCircle.setVisibility(View.INVISIBLE);
        }
        if(flashFlipper.getVisibility() != View.GONE) {
            flashFlipper.startAnimation(outAnim);
            flashFlipper.setVisibility(View.INVISIBLE);
        }
        circle1.setVisibility(View.INVISIBLE);
        circle2.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(!isRecording.get()) {
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
