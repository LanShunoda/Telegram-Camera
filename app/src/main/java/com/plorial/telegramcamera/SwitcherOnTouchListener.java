package com.plorial.telegramcamera;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
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

/**
 * Created by plorial on 4/28/16.
 */
public class SwitcherOnTouchListener implements View.OnTouchListener {

    public static final int ANIM_SPEED = 500;
    private final AppCompatImageButton recordButton;
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


    public SwitcherOnTouchListener(View view) {
        this.view = view;
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
        }else {
            recordButton.setImageDrawable(starting);
            starting.start();
            bottomPanelBackground.startAnimation(bottomPanelSlidingDown);
            bottomPanelBackground.setVisibility(View.INVISIBLE);
            tvVideoTiming.setVisibility(View.VISIBLE);
            makeViewsDisappear();
            isRecording = true;
        }
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
