package com.plorial.telegramcamera;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

/**
 * Created by plorial on 4/28/16.
 */
public class SwitcherOnTouchListener implements View.OnTouchListener {

    private static final int ANIM_SPEED = 500;
    private float fromPosition = 0;
    private View view;

    private ImageView circle1;
    private ImageView circle2;
    private Drawable blackCircleDrawable;
    private Drawable whiteCircleDrawable;
    private final ViewSwitcher switcher;
    private final RelativeLayout relativeLayout;

    private int colorPicture;
    private int colorVideo;

    public SwitcherOnTouchListener(View view) {
        this.view = view;
        circle1 = (ImageView) view.findViewById(R.id.imageCircle1);
        circle2 = (ImageView) view.findViewById(R.id.imageCircle2);
        blackCircleDrawable = view.getContext().getResources().getDrawable(R.drawable.circle_black);
        whiteCircleDrawable = view.getContext().getResources().getDrawable(R.drawable.circle_white);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout);
        circle1.setImageDrawable(whiteCircleDrawable);
        switcher = (ViewSwitcher) view.findViewById(R.id.switcher);

        colorPicture = view.getContext().getResources().getColor(R.color.colorPicture);
        colorVideo = view.getContext().getResources().getColor(R.color.colorVideo);

        Animation inAnim = new AlphaAnimation(0, 1);
        inAnim.setDuration(ANIM_SPEED/2);
        Animation outAnim = new AlphaAnimation(1, 0);
        outAnim.setDuration(ANIM_SPEED/2);

        switcher.setInAnimation(inAnim);
        switcher.setOutAnimation(outAnim);
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
                    circle2.setImageDrawable(whiteCircleDrawable);
                    circle1.setImageDrawable(blackCircleDrawable);

                   relativeLayoutBackgroundAnim(colorPicture, colorVideo);
                }
                else if (fromPosition > toPosition && switcher.getCurrentView() ==switcher.getChildAt(1)) {
                    switcher.showPrevious();
                    circle2.setImageDrawable(blackCircleDrawable);
                    circle1.setImageDrawable(whiteCircleDrawable);

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
