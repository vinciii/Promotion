package com.carl.promotion.widget.split;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;


import com.carl.promotion.R;

import java.util.ArrayList;

public class SplitView extends View {

    private Paint mpaint;
    private Bitmap bitmap;
    private float d = 3;//粒子直径
    private ValueAnimator valueAnimator;
    private ArrayList<Ball> list = new ArrayList<>();

    public SplitView(Context context) {
        this(context, null);
    }

    public SplitView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SplitView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
        valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setRepeatCount(-1);
        valueAnimator.setDuration(200);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                updateBall();
                invalidate();
            }
        });
    }

    private void updateBall() {
        for (Ball ball : list) {
            ball.x += ball.vX;
            ball.y += ball.vY;
            ball.vX += ball.aX;
            ball.vY += ball.aY;
        }
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        mpaint = new Paint();
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
        Ball ball;
        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                ball = new Ball();
                ball.color = bitmap.getPixel(i, j);
                ball.x = i * d + d / 2;
                ball.y = j * d + d / 2;
                ball.r = d / 2;
                ball.vX = (float) (Math.pow(-1, Math.ceil(Math.random() * 1000)) * 20 * Math.random());
                ball.vY = rangInt(-15, 35);
                ball.aX = 0;
                ball.aY = 0.98f;
                list.add(ball);
            }
        }
    }




    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(500, 500);
        for (Ball ball : list) {
            mpaint.setColor(ball.color);
            canvas.drawCircle(ball.x, ball.y, ball.r, mpaint);
        }
    }

    private int rangInt(int i, int i1) {
        int max = Math.max(i, i1);
        int min = Math.min(i, i1);
        return (int) (min + Math.ceil(Math.random() * (max - min)));
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            valueAnimator.start();
        }
        return super.onTouchEvent(event);
    }

    public class Ball {
        public int color;// 图片像素颜色值
        public float x;//例子圆心坐标
        public float y;//粒子圆心y
        public float r;//例子半径
        public float vX;//运动水平速度
        public float vY;//运动Y速度
        public float aX;//水平加速度
        public float aY;//垂直加速度
    }
}

