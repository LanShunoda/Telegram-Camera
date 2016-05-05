package com.plorial.telegramcamera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;

/**
 * Created by plorial on 5/5/16. Тот день когда мне пригодился матан !!!
 */
public class DegreesView extends View implements View.OnTouchListener {

    private static final String TAG = DegreesView.class.getSimpleName();

    private Paint paint;
    private Paint paintBlue;
    private float centerX; // центр вьюхи, ил центр полукруга
    private float radius; // радиус полукруга, или амплитуда
    private float startY;
    private float stopY;
    private float pivotPoint;

    private static final float DEGREES_DELTA = (float) (Math.PI/18); // одной полоске соответствует 10 градусов (90/9)
    private static final float ROTATE_SPEED = 0.01f;

    private ArrayList<Float> points;
    private float fromPosition;

    public DegreesView(Context context) {
        super(context);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paintBlue = new Paint();
        paintBlue.setColor(Color.BLUE);
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Drawable d = context.getResources().getDrawable(R.drawable.rotate);

        centerX = display.getWidth()/2;
        radius = centerX - d.getMinimumWidth()*2;

        startY = 0.2f;
        stopY = d.getMinimumHeight()/2;
        createPoints();
        this.setOnTouchListener(this);
    }

    private void createPoints() {
        points = new ArrayList<>();
        pivotPoint = 0.01f;
        for(float i = (float) -Math.PI; i < Math.PI; i = i + DEGREES_DELTA) {
            points.add(i);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(centerX, 0.0f, centerX, stopY * 1.5f , paintBlue); //center line
        for (float degree: points) {
            if(degree > -Math.PI/2 && degree < Math.PI/2) {// печатаем полукруг
                degree = getDegree(degree);
                canvas.drawLine(centerX + degree, startY, centerX + degree, stopY, paint);
            }
        }
        if(pivotPoint > -Math.PI/2 && pivotPoint < Math.PI/2) {
            canvas.drawLine(centerX + getDegree(pivotPoint), 0.1f, centerX + getDegree(pivotPoint), stopY * 1.25f, paintBlue);
        }
        Log.d(TAG, "degree " + Math.toDegrees(pivotPoint));
    }

    //Создаем еффект кручения колеса, как в старом советском радио

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                fromPosition = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float toPosition = event.getX();
                for (int i = 0; i < points.size(); i++) {
                   points.set(i, getPointChanged(fromPosition, toPosition, points.get(i)));
                }
                pivotPoint = getPointChanged(fromPosition, toPosition, pivotPoint);
                this.invalidate();
                break;
        }
        return true;
    }

    private float getPointChanged(float fromPosition, float toPosition, float point){
        if(fromPosition - toPosition > 0){
            float p = point - ROTATE_SPEED;
            return p < -Math.PI ? (float)(Math.PI) : p;
        }else {
            float p = point + ROTATE_SPEED;
            return p > Math.PI ? (float)(-Math.PI) : p;
        }
    }

    private float getDegree(float point){
        return (float) (radius * Math.sin(point));
    }
}
