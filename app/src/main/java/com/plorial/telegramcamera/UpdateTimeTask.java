package com.plorial.telegramcamera;

import android.os.Handler;
import android.widget.TextView;

import java.util.TimerTask;

/**
 * Created by plorial on 5/4/16.
 */
public class UpdateTimeTask extends TimerTask {

    private int seconds = 0;
    private int minutes = 0;
    private Handler uiHandler;
    private TextView tvVideoTiming;

    public UpdateTimeTask(Handler uiHandler, TextView tvVideoTiming) {
        this.uiHandler = uiHandler;
        this.tvVideoTiming = tvVideoTiming;
    }

    @Override
    public void run() {
        final String result = (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds);
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                tvVideoTiming.setText(result);
            }
        });
        seconds++;
        if(seconds == 60){
            minutes++;
            seconds=0;
        }
    }
}
