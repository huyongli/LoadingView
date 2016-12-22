package cn.ittiger.loading;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * 自定义广告倒计时View
 *
 * @author laohu
 * @site http://ittiger.cn
 */
public class CountDownView extends View {
    private static final String DEFAULT_TEXT = "跳过广告";
    private static final int DEFAULT_TEXT_SIZE = 12;
    private static final int DEFAULT_TEXT_COLOR = Color.WHITE;
    private static final int DEFAULT_PROGRESS_COLOR = 0xFF6ADBFE;
    private static final int DEFAULT_PROGRESS_WIDTH = 3;
    private static final int DEFAULT_BACKGROUND_COLOR = 0x50555555;
    private static final int DEFAULT_DURATION = 5000;
    private static final int DEFAULT_COUNT_DOWN_STEP = 25;//倒计时更新的时间间隔
    private static final int DEFAULT_WIDTH = 50;

    /**
     * 倒计时文字内容，默认为：跳过广告
     */
    private String mText;
    /**
     * 倒计时文字字体大小，默认为：12
     */
    private int mTextSize;
    /**
     * 倒计时文字颜色，默认为：white
     */
    private int mTextColor;
    /**
     * 倒计时进度条颜色，默认为：0xFF6ADBFE
     */
    private int mProgressColor;
    /**
     * 倒计时进度条宽度，默认为：3dp
     */
    private int mProgressWidth;
    /**
     * 倒计时视图背景色，默认为：0x50555555
     */
    private int mBackgroundColor;
    /**
     * 倒计时的总时间，单位毫秒，默认为：5000
     */
    private long mDuration;
    /**
     * 文本是否自动换行，默认为：true
     */
    private boolean mWordWrap;

    /**
     * 外部进度条画笔
     */
    private Paint mProgressPaint;
    /**
     * 整体圆形画笔
     */
    private Paint mCirclePaint;
    /**
     * 文字画笔
     */
    private TextPaint mTextPaint;
    /**
     * 处理绘制时文字换行的工具类
     */
    private StaticLayout mStaticLayout;
    /**
     * 当前进度，度数，一圈360度
     */
    private float mCurProgress = 0;
    /**
     * 倒计时计时器
     */
    private CountDownTimer mCountDownTimer;
    /**
     * 倒计时开始和结束时的监听
     */
    private CountDownViewListener mCountDownViewListener;
    private int mWidth = 0;

    public CountDownView(Context context) {

        this(context, null);
    }

    public CountDownView(Context context, AttributeSet attrs) {

        this(context, attrs, 0);
    }

    public CountDownView(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CountDownView, defStyleAttr, 0);
        mText = typedArray.getString(R.styleable.CountDownView_text);
        if(TextUtils.isEmpty(mText)) {
            mText = DEFAULT_TEXT;
        }
        float scale = getResources().getDisplayMetrics().density;
        int textSize = (int) (DEFAULT_TEXT_SIZE * scale + 0.5f);
        int progressWidth = (int) (DEFAULT_PROGRESS_WIDTH * scale + 0.5f);
        mWidth = (int) (DEFAULT_WIDTH * scale + 0.5f);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.CountDownView_textSize, textSize);
        mTextColor = typedArray.getColor(R.styleable.CountDownView_textColor, DEFAULT_TEXT_COLOR);
        mProgressColor = typedArray.getColor(R.styleable.CountDownView_progressColor, DEFAULT_PROGRESS_COLOR);
        mProgressWidth = typedArray.getDimensionPixelSize(R.styleable.CountDownView_progressWidth, progressWidth);
        mBackgroundColor = typedArray.getColor(R.styleable.CountDownView_backgroundColor, DEFAULT_BACKGROUND_COLOR);
        mDuration = typedArray.getInt(R.styleable.CountDownView_countDownTime, DEFAULT_DURATION);
        mWordWrap = typedArray.getBoolean(R.styleable.CountDownView_wordWrap, true);
        typedArray.recycle();

        initPaint();
    }

    private void initPaint() {

        mCirclePaint = new Paint();
        //抗锯齿，对边界锯齿进行模糊处理
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setFilterBitmap(true);
        //防抖动，对图像抖动进行模糊平滑处理，使之看起来更柔和
        mCirclePaint.setDither(true);
        mCirclePaint.setColor(mBackgroundColor);
        //实心填充绘制
        mCirclePaint.setStyle(Paint.Style.FILL);

        mProgressPaint = new Paint();
        //抗锯齿，对边界锯齿进行模糊处理
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setFilterBitmap(true);
        //防抖动，对图像抖动进行模糊平滑处理，使之看起来更柔和
        mProgressPaint.setDither(true);
        mProgressPaint.setStrokeWidth(mProgressWidth);
        //空心绘制
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setColor(mProgressColor);

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.density = getResources().getDisplayMetrics().density;
        //抗锯齿，对边界锯齿进行模糊处理
        mTextPaint.setAntiAlias(true);
        mTextPaint.setFilterBitmap(true);
        //防抖动，对图像抖动进行模糊平滑处理，使之看起来更柔和
        mTextPaint.setDither(true);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        int length = mText.length();
        if(mWordWrap) {
            //自动换行时对文本的一半进行测量
            length = (mText.length() + 1) / 2;
        }
        int textWidth = (int) mTextPaint.measureText(mText.substring(0, length));
        //使用StaticLayout对文本进行自动换行
        mStaticLayout = new StaticLayout(mText, mTextPaint, textWidth, Layout.Alignment.ALIGN_NORMAL, 1F, 0, false);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mCountDownViewListener != null) {
                    mCountDownViewListener.onSkipClick();
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if(widthSpecMode != MeasureSpec.EXACTLY) {
            widthSize = mWidth;//给wrap_content设顶一个默认宽度
        }
        if(heightSpecMode != MeasureSpec.EXACTLY) {
            heightSize = mWidth;//给wrap_content设顶一个默认高度
        }
        int max = Math.max(widthSize, heightSize);
        setMeasuredDimension(max, max);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        int width = getMeasuredWidth();
        float radius = width / 2.0f;

        //画整体圆形视图
        canvas.drawCircle(radius, radius, radius, mCirclePaint);

        //画进度条，其实就是画一个封闭的圆弧
        float arcRadius = mProgressWidth / 2.0f;
        //画圆弧时,坐标系原点不在圆弧边缘，所以坐标系原点要平移ProgressWidth / 2.0f
        RectF rectF = new RectF(arcRadius, arcRadius, width - arcRadius, width - arcRadius);
        canvas.drawArc(rectF, -90, mCurProgress, false, mProgressPaint);

        //变换绘制原点
        canvas.translate(radius, radius - mStaticLayout.getHeight() / 2.0f);
        //画文字
        mStaticLayout.draw(canvas);
    }

    public void start() {

        if(mCountDownViewListener != null) {
            mCountDownViewListener.onStart();
        }
        if(mDuration <= DEFAULT_COUNT_DOWN_STEP) {
            throw new IllegalArgumentException("duration att unit must be millions seconds");
        }
        if(mCountDownTimer == null) {
            mCountDownTimer = new CountDownTimer(mDuration, DEFAULT_COUNT_DOWN_STEP) {
                @Override
                public void onTick(long millisUntilFinished) {

                    mCurProgress = (mDuration - millisUntilFinished) * 1.0f / mDuration * 360;
                    invalidate();
                }

                @Override
                public void onFinish() {

                    mCurProgress = 360;
                    invalidate();
                    if(mCountDownViewListener != null) {
                        mCountDownViewListener.onFinish();
                    }
                }
            };
        } else {
            mCountDownTimer.cancel();
            mCurProgress = 0;
        }
        mCountDownTimer.start();
    }

    public void stop() {

        if(mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    public void setCountDownViewListener(CountDownViewListener countDownViewListener) {

        mCountDownViewListener = countDownViewListener;
    }

    public interface CountDownViewListener {

        /**
         * 倒计时刚开始的回调
         */
        void onStart();

        /**
         * 倒计时结束时的回调
         */
        void onFinish();

        /**
         * 点击跳过事件的回调
         * 不需要对视图手动设置onClickListener监听，设置之后，此方法失效
         */
        void onSkipClick();
    }
}
