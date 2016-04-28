package com.plorial.telegramcamera;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

/**
 * Created by plorial on 4/28/16.
 */
public class FlipperOnTouchListener implements View.OnTouchListener {

    private float fromPosition = 0;
    private View view;

    private ImageView circle1;
    private ImageView circle2;
    private Drawable blackCircleDrawable;
    private Drawable whiteCircleDrawable;
    private final ViewFlipper flipper;

    public FlipperOnTouchListener(View view) {
        this.view = view;
        circle1 = (ImageView) view.findViewById(R.id.imageCircle1);
        circle2 = (ImageView) view.findViewById(R.id.imageCircle2);
        blackCircleDrawable = view.getContext().getResources().getDrawable(R.drawable.ic_fiber_manual_record_black_24dp);
        whiteCircleDrawable = view.getContext().getResources().getDrawable(R.drawable.ic_panorama_fish_eye_black_24dp);
        circle1.setImageDrawable(blackCircleDrawable);
        flipper = (ViewFlipper) view.findViewById(R.id.viewflipper);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                fromPosition = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                float toPosition = event.getX();
                if (fromPosition < toPosition && flipper.getCurrentView() ==flipper.getChildAt(0)) {
                    flipper.setInAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.go_next_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.go_next_out));
                    flipper.showNext();
                    circle2.setImageDrawable(blackCircleDrawable);
                    circle1.setImageDrawable(whiteCircleDrawable);
                }
                else if (fromPosition > toPosition && flipper.getCurrentView() ==flipper.getChildAt(1)) {
                    flipper.setInAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.go_prev_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.go_prev_out));
                    flipper.showPrevious();
                    circle2.setImageDrawable(whiteCircleDrawable);
                    circle1.setImageDrawable(blackCircleDrawable);
                }
            default:
                break;
        }
        return true;
    }
}
