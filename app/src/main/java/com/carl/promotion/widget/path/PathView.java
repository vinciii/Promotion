package com.carl.promotion.widget.path;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.text.MeasuredText;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.carl.promotion.R;

public class PathView extends View {

    private ValueAnimator valueAnimator;

    public PathView(Context context) {
        this(context, null);
    }

    public PathView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private Paint paint;
    private Paint paintText;
    private Path path;

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        path = new Path();
        paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        start();
    }

    private void start() {
        valueAnimator = ValueAnimator.ofFloat(0, 270);
        valueAnimator.setDuration(1100);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                radius = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;

    }


    private int width;
    private int height;

    private float radius;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawArc(25, 25, width-25, height-25, -225, radius, false, paint);
        }



        paintText.setTextSize(40);
        paintText.setTextAlign(Paint.Align.CENTER);

        canvas.drawText((int) radius + "", width/2, height/2, paintText);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(getResources().getDrawable(R.mipmap.ic_launcher_round));
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        start();
        return super.onTouchEvent(event);

    }
}
