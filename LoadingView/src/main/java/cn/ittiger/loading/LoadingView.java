package cn.ittiger.loading;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * 加载动画
 *
 * @author: laohu on 2016/7/3
 * @site: http://ittiger.cn
 */
public class LoadingView extends View {
    /**
     * 球由大变小动画的默认持续时间
     */
    private static final int DURATION = 800;
    /**
     * 球在动画变化过程中的默认最大半径
     */
    private static final int MAX_RADIUS = 25;
    /**
     * 球在动画变化过程中的默认最小半径
     */
    private static final int MIN_RADIUS = 10;
    /**
     * 球1的默认背景色
     */
    private static final int BALL_COLOR_2 = Color.parseColor("#ff33b5e5");
    /**
     * 球2的默认背景色
     */
    private static final int BALL_COLOR_1 = Color.RED;
    /**
     * 球的动画变化器
     */
    private BallAnimator mBallAnimator;

    private int mBall1Color;
    private int mBall2Color;
    private int mDuration;
    private int mMaxRadius;
    private int mMinRadius;

    public LoadingView(Context context) {

        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {

        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingView);
        mBall1Color = typedArray.getColor(R.styleable.LoadingView_ball1Color, BALL_COLOR_1);
        mBall2Color = typedArray.getColor(R.styleable.LoadingView_ball2Color, BALL_COLOR_2);
        mMaxRadius = typedArray.getDimensionPixelSize(R.styleable.LoadingView_maxRadius, MAX_RADIUS);
        mMinRadius = typedArray.getDimensionPixelSize(R.styleable.LoadingView_minRadius, MIN_RADIUS);
        mDuration = typedArray.getInt(R.styleable.LoadingView_duration, DURATION);
        typedArray.recycle();

        mBallAnimator = new BallAnimator(mDuration, mMinRadius, mMaxRadius);

        Ball ball1 = new Ball(mBall1Color, mMinRadius);
        ball1.setPosition(mMaxRadius, mMaxRadius);

        Ball ball2 = new Ball(mBall2Color, mMinRadius);
        ball2.setPosition(mMinRadius * 4 + mMaxRadius, mMaxRadius);

        mBallAnimator.addBall(ball1);
        mBallAnimator.addBall(ball2);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        for(Ball ball : mBallAnimator.getBalls()) {
            ball.draw(canvas);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if(widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mMinRadius * 2 + mMinRadius * 2 + mMaxRadius * 2, mMaxRadius * 2);
        } else if(widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mMinRadius * 2 + mMinRadius * 2 + mMaxRadius * 2, heightSpecSize);
        } else if(heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, mMaxRadius * 2);
        }
    }

    public void start() {

        mBallAnimator.start();
    }

    public void stop() {

        mBallAnimator.stop();
    }

    public class Ball {

        private Paint mPaint;
        private float mInitRadius;
        private float mRadius;
        private Point mPosition;
        private int mColor;

        public Ball(int color, float radius) {

            mColor = color;
            mInitRadius = mRadius = radius;

            mPaint = new Paint();
            mPaint.setColor(mColor);
            mPaint.setAntiAlias(true);
            mPosition = new Point();
        }

        public void setRadius(float radius) {

            this.mRadius = radius;
        }

        public void setPosition(int x, int y) {

            this.mPosition.set(x, y);
        }

        public void draw(Canvas canvas) {

            canvas.drawCircle(mPosition.x, mPosition.y, mRadius, mPaint);
        }

        public void reset() {

            this.mRadius = mInitRadius;
        }
    }

    public class BallAnimator {
        private int mDuration;
        private float mMinRadius;
        private float mMaxRadius;
        private List<Ball> mBalls;
        private List<ValueAnimator> mBallAnimators;

        public BallAnimator(int duration, float minRadius, float maxRadius) {

            mDuration = duration;
            mMinRadius = minRadius;
            mMaxRadius = maxRadius;
            mBalls = new ArrayList<>();
            mBallAnimators = new ArrayList<>();
        }

        public void start() {

            mBallAnimators.clear();
            for(int i = 0; i < mBalls.size(); i++) {
                mBalls.get(i).reset();
                createBallAnimator(mBalls.get(i), mDuration * i);
            }
        }

        public void stop() {

            for(ValueAnimator valueAnimator : mBallAnimators) {
                valueAnimator.cancel();
            }
            for(Ball ball : mBalls) {
                ball.reset();
            }
        }

        private void createBallAnimator(Ball ball, int startDelay) {

            ValueAnimator valueAnimator = new ValueAnimator();
            valueAnimator.setFloatValues(mMinRadius, mMaxRadius);
            valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            valueAnimator.setDuration(mDuration);
            valueAnimator.addUpdateListener(new BallUpdateListener(ball));
            valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            valueAnimator.setStartDelay(startDelay);
            mBallAnimators.add(valueAnimator);
            valueAnimator.start();
        }

        public void addBall(Ball ball) {

            this.mBalls.add(ball);
        }

        public List<Ball> getBalls() {

            return mBalls;
        }
    }

    private class BallUpdateListener implements ValueAnimator.AnimatorUpdateListener {

        private Ball ball;

        public BallUpdateListener(Ball ball) {

            this.ball = ball;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {

            float value = (float) animation.getAnimatedValue();
            ball.setRadius(value);
            invalidate();
        }
    }
}
