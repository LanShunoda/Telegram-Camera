package com.plorial.telegramcamera;

import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.wnafee.vector.compat.AnimatedVectorDrawable;

/**
 * Created by plorial on 5/1/16.
 */
public class ShotButtonOnTouchListener implements View.OnTouchListener {

    public static final String TAG = ShotButtonOnTouchListener.class.getSimpleName();
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

        }
        return true;
    }
}
