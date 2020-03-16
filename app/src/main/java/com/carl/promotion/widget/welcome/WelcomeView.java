package com.carl.promotion.widget.welcome;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;

import com.carl.promotion.R;


/**
 *
 */
public class WelcomeView extends View {

    private Paint mPaint;

    private Paint mStrokePaint;

    private ValueAnimator mValueAnimator;


    //背景色
    private int mBackgroudColor = Color.WHITE;
    //旋转圆颜色
    private int[] mCircleColors;

    //旋转中心坐标
    private float mCenterX;
    private float mCenterY;

    //扩散元最大半径
    private float mDistance;

    //6小球半径
    private float mCircleRadius = 18;
    //旋转大圆半径
    private float mRotateRadius = 90;

    //当前旋转角度
    private float mCurrentRotateAngle = 0F;

    //当前大圆形半径
    private float mCurrentRotateRadius = mRotateRadius;

    //扩散圆半径
    private float mCurrentStrokeRadius = 0f;

    private int mRotateDuration1 = 0;
    private int mRotateDuration2 = 0;
    private int mRotateDuration3 = 0;

    //当前状态类
    private WelcomeState mState;


    public WelcomeView(Context context) {
        this(context, null);
    }

    public WelcomeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WelcomeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }


    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setColor(mBackgroudColor);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WelcomeView, defStyleAttr, 0);
        mRotateDuration1 = typedArray.getInt(R.styleable.WelcomeView_rotate_duration, 1000);
        mRotateDuration2 = typedArray.getInt(R.styleable.WelcomeView_wave_duration, 1000);
        mRotateDuration3 = typedArray.getInt(R.styleable.WelcomeView_expand_duration, 1000);


        mCircleColors = context.getResources().getIntArray(R.array.welcome_circlr_colors);
        typedArray.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w * 1f / 2;
        mCenterY = h * 1f / 2;

        mDistance = (float) (Math.hypot(w, h) / 2); //保证能完整的显示所有屏幕内容
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mState == null) {
            mState = new RotateState();
        }
        mState.drawState(canvas);
    }


    //旋转
    private class RotateState extends WelcomeState {
        private RotateState() {
            mValueAnimator = ValueAnimator.ofFloat(0, (float) (Math.PI * 2));
            mValueAnimator.setDuration(mRotateDuration1);
            mValueAnimator.setInterpolator(new LinearInterpolator());
            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCurrentRotateAngle = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });

            mValueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mState = new ExplodeState();
                }
            });
            mValueAnimator.start();
        }

        @Override
        void drawState(Canvas canvas) {
            //绘制背景
            drawBackground(canvas);
            //绘制6个小球
            drawCircles(canvas);
        }
    }

    private void drawCircles(Canvas canvas) {
        float rotateAngel = (float) (Math.PI * 2 / mCircleColors.length);
        for (int i = 0; i < mCircleColors.length; i++) {
            float angel = i * rotateAngel + mCurrentRotateAngle;
            float cx = (float) Math.cos(angel) * mCurrentRotateRadius + mCenterX;//  x = r*cos A +cenX
            float cy = (float) Math.sin(angel) * mCurrentRotateRadius + mCenterY;// y = r * sin A + cenY
            mPaint.setColor(mCircleColors[i]);
            canvas.drawCircle(cx, cy, mCircleRadius, mPaint);
        }
    }

    private void drawBackground(Canvas c) {
        if (mCurrentStrokeRadius > 0) {
            //绘制空心圆
            float strokeWidth = mDistance - mCurrentStrokeRadius;
            //真实半径
            float radius = strokeWidth / 2 + mCurrentStrokeRadius;

            mStrokePaint.setStrokeWidth(strokeWidth);

            c.drawCircle(mCenterX, mCenterY, radius, mStrokePaint);
        } else {
            c.drawColor(mBackgroudColor);

        }
    }

    private abstract class WelcomeState {
        abstract void drawState(Canvas canvas);
    }


    //扩散聚合
    private class ExplodeState extends WelcomeState {
        public ExplodeState() {
            //从 0  到大圆半径的平移,
            mValueAnimator = ValueAnimator.ofFloat(0, mRotateRadius);
            mValueAnimator.setDuration(mRotateDuration2);
            //设置一给超出回弹的插值器
            mValueAnimator.setInterpolator(new OvershootInterpolator(10f));
            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCurrentRotateRadius = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });

            mValueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mCircleRadius = 0;  //让当前画布的圆消失
                    mState = new WaveState();
                }
            });
            mValueAnimator.reverse();
        }

        @Override
        void drawState(Canvas canvas) {
            drawBackground(canvas);
            drawCircles(canvas);
        }


    }


    //水波纹
    private class WaveState extends WelcomeState {

        public WaveState() {
            mValueAnimator = ValueAnimator.ofFloat(mCircleRadius, mDistance);
            mValueAnimator.setDuration(mRotateDuration3);
            mValueAnimator.setInterpolator(new LinearInterpolator());
            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCurrentStrokeRadius = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            mValueAnimator.start();
        }

        @Override
        void drawState(Canvas canvas) {
            drawBackground(canvas);
            drawCircles(canvas);
        }
    }


}
