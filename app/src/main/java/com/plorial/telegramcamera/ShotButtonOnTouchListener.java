package com.plorial.telegramcamera;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.hardware.Camera;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ViewSwitcher;

import com.wnafee.vector.compat.AnimatedVectorDrawable;

/**
 * Created by plorial on 5/1/16.
 */
public class ShotButtonOnTouchListener implements View.OnTouchListener {

    public static final String TAG = ShotButtonOnTouchListener.class.getSimpleName();
    private Camera camera;
    private View view;

    public ShotButtonOnTouchListener(View view, Camera camera) {
        this.camera = camera;
        this.view = view;
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
                takePicture();
                changePanels();

        }
        return true;
    }

    private void changePanels(){
        ViewSwitcher switcher = (ViewSwitcher) view.findViewById(R.id.bottom_panel_switcher);
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
        view.findViewById(R.id.flashFlipper).setVisibility(View.INVISIBLE);
        SwitcherOnTouchListener.isRecording = true;

        bCancel.startAnimation(scaleAnimation);
        bDone.startAnimation(scaleAnimation);
    }

    private void takePicture(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                camera.takePicture(null, null, new PictureCallback());
            }
        }).start();
    }
}
