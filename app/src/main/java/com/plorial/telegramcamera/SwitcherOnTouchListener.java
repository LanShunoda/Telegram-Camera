package com.plorial.telegramcamera;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import com.wnafee.vector.compat.AnimatedVectorDrawable;

/**
 * Created by plorial on 4/28/16.
 */
public class SwitcherOnTouchListener implements View.OnTouchListener {

    private static final int ANIM_SPEED = 500;
    private final AppCompatImageButton recordButton;
    private float fromPosition = 0;
    private View view;

    private AppCompatImageView circle1;
    private AppCompatImageView circle2;
    private Drawable blackCircleDrawable;
    private Drawable whiteCircleDrawable;
    private final ViewSwitcher switcher;
    private final RelativeLayout relativeLayout;

    private int colorPicture;
    private int colorVideo;
    private boolean isRecording = false;

    public SwitcherOnTouchListener(View view) {
        this.view = view;
        circle1 = (AppCompatImageView) view.findViewById(R.id.imageCircle1);
        circle2 = (AppCompatImageView) view.findViewById(R.id.imageCircle2);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout);
        circle1.setImageResource(R.drawable.circle_white);
        circle2.setImageResource(R.drawable.circle_black);
        switcher = (ViewSwitcher) view.findViewById(R.id.switcher);
        recordButton = (AppCompatImageButton) view.findViewById(R.id.recordButton);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
            }
        });

        colorPicture = view.getContext().getResources().getColor(R.color.colorPicture);
        colorVideo = view.getContext().getResources().getColor(R.color.colorVideo);

        Animation inAnim = new AlphaAnimation(0, 1);
        inAnim.setDuration(ANIM_SPEED/2);
        Animation outAnim = new AlphaAnimation(1, 0);
        outAnim.setDuration(ANIM_SPEED/2);

        switcher.setInAnimation(inAnim);
        switcher.setOutAnimation(outAnim);
    }

    private void startRecording() {
        AnimatedVectorDrawable starting = AnimatedVectorDrawable.getDrawable(view.getContext(), R.drawable.recording_button_starting_vector);
        AnimatedVectorDrawable stoping = AnimatedVectorDrawable.getDrawable(view.getContext(), R.drawable.recording_button_stoping_vector);
        if(isRecording) {
            recordButton.setImageDrawable(stoping);
            stoping.start();
            isRecording = false;
        }else {
            recordButton.setImageDrawable(starting);
            starting.start();
            isRecording = true;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                fromPosition = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                float toPosition = event.getX();
                if (fromPosition < toPosition && switcher.getCurrentView() ==switcher.getChildAt(0)) {
                    switcher.showNext();
                    circle1.setImageResource(R.drawable.circle_black);
                    circle2.setImageResource(R.drawable.circle_white);

                    AnimatedVectorDrawable extending = AnimatedVectorDrawable.getDrawable(view.getContext(), R.drawable.record_button_extending_vector);
                    recordButton.setImageDrawable(extending);
                    extending.start();
                   relativeLayoutBackgroundAnim(colorPicture, colorVideo);
                }
                else if (fromPosition > toPosition && switcher.getCurrentView() ==switcher.getChildAt(1)) {
                    switcher.showPrevious();
                    circle1.setImageResource(R.drawable.circle_white);
                    circle2.setImageResource(R.drawable.circle_black);

                    AnimatedVectorDrawable constriction = AnimatedVectorDrawable.getDrawable(view.getContext(), R.drawable.record_button_constriction_vector);
                    recordButton.setImageDrawable(constriction);
                    constriction.start();

                    relativeLayoutBackgroundAnim(colorVideo, colorPicture);
                }
            default:
                break;
        }
        return true;
    }

    private void relativeLayoutBackgroundAnim(int colorFrom, int colorTo){
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(ANIM_SPEED);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                relativeLayout.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
    }
}
