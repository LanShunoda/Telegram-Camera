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

import java.util.ArrayList;

/**
 * Created by plorial on 5/5/16. Тот день когда мне пригодился матан !!!
 */
public class DegreesView extends View implements View.OnTouchListener {

    private Paint paint;
    private float centerX; // центр вьюхи, ил центр полукруга
    private float radius; // радиус полукруга, или амплитуда для гармонического закона
    private float startY;
    private float stopY;
    private static final float OMEGA = 0.07f; //  ω - частота колебаний, 0,07 - потому что я агент 007, а если чесно то величина подобранная империческим путем
    private static final float DEGREES_DELTA = (float) (Math.PI/9); // одной полоске соответствует 10 градусов (90/9)
    private static final float ROTATE_SPEED = 0.02f;

    private ArrayList<Float> points;
    private float fromPosition;

    public DegreesView(Context context) {
        super(context);
        paint = new Paint();
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Drawable d = context.getResources().getDrawable(R.drawable.rotate);

        centerX = display.getWidth()/2;
        radius = centerX - d.getMinimumWidth()*2;

        startY = 0.0f;
        stopY = d.getMinimumHeight()/2;
        createPoints();
        this.setOnTouchListener(this);
    }

    private void createPoints() {
        points = new ArrayList<>();
        for(float i = (float) -Math.PI; i < Math.PI ; i = i + DEGREES_DELTA) {   // проходимся по половине круга от -90 до 90 градусов
            points.add(i);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.WHITE);
        canvas.drawLine(centerX, startY, centerX, stopY * 1.5f , paint); //center line
        for (float degree: points) {
            degree = (float) (radius * Math.sin(2 * Math.PI * OMEGA * degree)); // изменяем растояние между линиями по гармоническому закону
            canvas.drawLine(centerX + degree, startY, centerX + degree, stopY, paint);
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
                   if(fromPosition - toPosition < 0){
                       float p = points.get(i) - ROTATE_SPEED;
                       points.set(i, p < -Math.PI ? (float)(Math.PI) : p);
                   }else {
                       float p = points.get(i) + ROTATE_SPEED;
                       points.set(i, p > Math.PI ? (float)(-Math.PI) : p);
                   }
                }
                this.invalidate();
                break;
        }
        return true;
    }
}
