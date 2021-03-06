package com.plorial.telegramcamera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import java.util.ArrayList;

/**
 * Created by plorial on 5/5/16.
 */
public class DegreesView extends View implements View.OnTouchListener {

    private static final String TAG = DegreesView.class.getSimpleName();

    private Paint paint;
    private Paint paintBlue;
    private Paint centerPaint;
    private float centerX; // центр вьюхи, ил центр полукруга
    private float radius; // радиус полукруга, или амплитуда
    private float startY;
    private float stopY;
    private float pivotPoint;
    private DegreeChangedCallback degreeChangedCallback;

    private static final float DEGREES_DELTA = (float) (Math.PI/18); // одной полоске соответствует 10 градусов
    private static final float ROTATE_SPEED = 0.0175f;

    private ArrayList<Float> points;
    private float fromPosition;
    private Paint paintPivot;

    public DegreesView(Context context) {
        super(context);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paintBlue = new Paint();
        paintBlue.setColor(Color.BLUE);
        centerPaint = new Paint();
        centerPaint.setColor(Color.BLUE);
        centerPaint.setStrokeWidth(4f);
        paintPivot = new Paint();
        paintPivot.setColor(Color.BLUE);
        paintPivot.setStrokeWidth(2f);
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Drawable d = context.getResources().getDrawable(R.drawable.rotate);

        centerX = display.getWidth()/2;
        radius = centerX - d.getMinimumWidth()*2;

        startY = 0.4f;
        stopY = d.getMinimumHeight()/1.5f;
        createPoints();
        this.setOnTouchListener(this);
    }

    private void createPoints() {
        points = new ArrayList<>();
        pivotPoint = 0.00001f;
        for(float i = (float) -Math.PI; i < Math.PI; i = i + DEGREES_DELTA) {
            points.add(i);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(centerX, 0.0f, centerX, stopY * 1.5f , centerPaint); //center line
        for (float degree: points) {
            if(degree > -Math.PI/2 && degree < Math.PI/2) {// печатаем полукруг
                if(isBetweenZeroAndPivot(degree, pivotPoint)){
                    degree = getDegree(degree);
                    canvas.drawLine(centerX + degree, startY, centerX + degree, stopY, paintBlue);
                }else{
                    degree = getDegree(degree);
                    canvas.drawLine(centerX + degree, startY, centerX + degree, stopY, paint);
                }
            }
        }
        if(pivotPoint > -Math.PI/2 && pivotPoint < Math.PI/2) {
            canvas.drawLine(centerX + getDegree(pivotPoint), 0.2f, centerX + getDegree(pivotPoint), stopY * 1.25f, paintPivot);
        }
        if(degreeChangedCallback != null){
            degreeChangedCallback.onDegreeChanged((float) Math.toDegrees(pivotPoint) * (-1));
        }
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

    private boolean isBetweenZeroAndPivot(float degree, float pivot){
        if(degree > 0 && degree < pivot){
            return true;
        } else if (degree < 0 && degree > pivot){
            return true;
        }else
            return false;
    }

    public void changeDegree(float degree){
        float radians = (float) Math.toRadians(degree);
        float p = pivotPoint + radians;
        if(p < -Math.PI){
            p = (float) (Math.PI - getDelta(p));
        }else if(p > Math.PI){
            p = (float) (-Math.PI + getDelta(p));
        }
        pivotPoint = p;
        invalidate();
    }

    private float getDelta(float p){
        return (float) (Math.abs(p) - Math.PI);
    }

    public void resetDegree(){
        createPoints();
        invalidate();
    }

    private float getDegree(float point){
        return (float) (radius * Math.sin(point));
    }

    public void setDegreeChangedCallback(DegreeChangedCallback degreeChangedCallback) {
        this.degreeChangedCallback = degreeChangedCallback;
    }

    public interface DegreeChangedCallback{
        void onDegreeChanged(float degree);
    }
}
