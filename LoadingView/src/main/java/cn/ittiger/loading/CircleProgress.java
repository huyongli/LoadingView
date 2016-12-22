package cn.ittiger.loading;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author laohu
 * @site http://ittiger.cn
 */
public class CircleProgress extends View {
    private static final int DEFAULT_TEXT_SIZE = 12;
    private static final int DEFAULT_TEXT_COLOR = Color.BLACK;
    private static final int DEFAULT_CIRCLE_COLOR = Color.WHITE;
    private static final int DEFAULT_LOOP_COLOR = 0x50555555;
    private static final int DEFAULT_LOOP_PROGRESS_COLOR = 0xFF6ADBFE;
    private static final int DEFAULT_LOOP_WIDTH = 5;
    private static final int DEFAULT_WIDTH = 50;
    private static final int MAX_PERCENT = 100;

    /**
     * 圆环的颜色
     */
    private int mLoopColor;
    /**
     * 圆环进度的颜色
     */
    private int mLoopProgressColor;
    /**
     * 圆环宽度
     */
    private float mLoopWidth;
    /**
     * 中间圆的背景色
     */
    private int mCircleColor;
    /**
     * 中间圆里的百分比文字颜色
     */
    private int mPercentageTextColor;
    /**
     * 中间圆里的百分比文字字体大小
     */
    private int mPercentageTextSize;
    private float mDensity;

    /**
     * 中间圆的画笔
     */
    private Paint mCirclePaint;
    /**
     * 默认圆环画笔
     */
    private Paint mLoopPaint;
    /**
     * 圆环进度画笔
     */
    private Paint mLoopProgressPaint;
    /**
     * 中间进度文字画笔
     */
    private TextPaint mTextPaint;

    private int mProgress = 0;

    public CircleProgress(Context context) {

        this(context, null);
    }

    public CircleProgress(Context context, AttributeSet attrs) {

        this(context, attrs, 0);
    }

    public CircleProgress(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        mDensity = context.getResources().getDisplayMetrics().density;
        int textSize = (int) (DEFAULT_TEXT_SIZE * mDensity + 0.5f);
        int loopWidth = (int) (DEFAULT_LOOP_WIDTH * mDensity + 0.5f);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgress);
        mCircleColor = typedArray.getColor(R.styleable.CircleProgress_circleColor, DEFAULT_CIRCLE_COLOR);
        mLoopColor = typedArray.getColor(R.styleable.CircleProgress_loopColor, DEFAULT_LOOP_COLOR);
        mLoopProgressColor = typedArray.getColor(R.styleable.CircleProgress_loopProgressColor, DEFAULT_LOOP_PROGRESS_COLOR);
        mLoopWidth = typedArray.getDimensionPixelSize(R.styleable.CircleProgress_loopWidth, loopWidth);
        mPercentageTextColor = typedArray.getColor(R.styleable.CircleProgress_percentageTextColor, DEFAULT_TEXT_COLOR);
        mPercentageTextSize = typedArray.getDimensionPixelSize(R.styleable.CircleProgress_percentageTextSize, textSize);
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
        mCirclePaint.setColor(mCircleColor);
        //实心填充绘制
        mCirclePaint.setStyle(Paint.Style.FILL);

        mLoopPaint = new Paint();
        //抗锯齿，对边界锯齿进行模糊处理
        mLoopPaint.setAntiAlias(true);
        mLoopPaint.setFilterBitmap(true);
        //防抖动，对图像抖动进行模糊平滑处理，使之看起来更柔和
        mLoopPaint.setDither(true);
        mLoopPaint.setStrokeWidth(mLoopWidth);
        //空心绘制
        mLoopPaint.setStyle(Paint.Style.STROKE);
        mLoopPaint.setColor(mLoopColor);

        mLoopProgressPaint = new Paint();
        //抗锯齿，对边界锯齿进行模糊处理
        mLoopProgressPaint.setAntiAlias(true);
        mLoopProgressPaint.setFilterBitmap(true);
        //防抖动，对图像抖动进行模糊平滑处理，使之看起来更柔和
        mLoopProgressPaint.setDither(true);
        mLoopProgressPaint.setStrokeWidth(mLoopWidth);
        //空心绘制
        mLoopProgressPaint.setStyle(Paint.Style.STROKE);
        mLoopProgressPaint.setColor(mLoopProgressColor);

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.density = getResources().getDisplayMetrics().density;
        //抗锯齿，对边界锯齿进行模糊处理
        mTextPaint.setAntiAlias(true);
        mTextPaint.setFilterBitmap(true);
        //防抖动，对图像抖动进行模糊平滑处理，使之看起来更柔和
        mTextPaint.setDither(true);
        mTextPaint.setColor(mPercentageTextColor);
        mTextPaint.setTextSize(mPercentageTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = (int) (DEFAULT_WIDTH * mDensity + 0.5f);
        if(widthSpecMode != MeasureSpec.EXACTLY) {
            widthSize = width;//给wrap_content设顶一个默认宽度
        }
        if(heightSpecMode != MeasureSpec.EXACTLY) {
            heightSize = width;//给wrap_content设顶一个默认高度
        }
        int max = Math.max(widthSize, heightSize);
        setMeasuredDimension(max, max);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        int width = getMeasuredWidth();
        float radius = width / 2.0f;

        //画中间的圆
        canvas.drawCircle(radius, radius, radius, mCirclePaint);

        //画圆弧时,坐标系原点不在圆弧边缘，所以坐标系原点要平移ProgressWidth / 2.0f
        float arcRadius = mLoopWidth / 2.0f;
        RectF rectF = new RectF(arcRadius, arcRadius, width - arcRadius, width - arcRadius);

        //画默认圆环(未显示进度条之前)
        canvas.drawArc(rectF, -90, 360, false, mLoopPaint);

        //画进度圆环
        canvas.drawArc(rectF, -90, 360.0f / MAX_PERCENT * mProgress, false, mLoopProgressPaint);

        String text = mProgress + "%";
        //画进度文字
        int textWidth = (int) mTextPaint.measureText(text);
        StaticLayout staticLayout = new StaticLayout(text, mTextPaint, textWidth, Layout.Alignment.ALIGN_NORMAL, 1F, 0, false);
        //变换文字绘制原点
        canvas.translate(radius, radius - staticLayout.getHeight() / 2.0f);
        staticLayout.draw(canvas);
    }

    public void setProgress(int progress) {

        if(progress < 0 || progress > 100) {
            return;
        }
        this.mProgress = progress;
        invalidate();
    }

    public int getProgress() {

        return mProgress;
    }

}
