package com.nepalese.virgocomponent.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.nepalese.virgocomponent.R;

/**
 * @author nepalese on 2020/12/4 15:21
 * @usage
 */
public class VirgoCircleProcessBar extends View {
    private static final String TAG = "CircleProcessBar";

    private Context context;

    private Paint backPaint;//背景
    private Paint frontPaint;//前景
    private Paint textPaint;//文字
    private int backColor, frontColor, textColor;
    private float textSize;//字体大小
    private float textHeight;//文字高度，用来调整位置

    private float strokeWidth;//画笔粗细，控制进度条粗细

    private float radius;//半径，控制控件大小（w, h自适应）
    private RectF rect;

    private int progress;//初始值
    private int max;//最大值

    private int width;
    private int height;

    private boolean addText;//是否需要中间文字

    public VirgoCircleProcessBar(Context context) {
        this(context, null);
    }

    public VirgoCircleProcessBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoCircleProcessBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    //完成相关参数初始化
    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        //解析自定义属性
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CircleProcessBar);
        backColor = ta.getColor(R.styleable.CircleProcessBar_cbBackColor, 0xffffffff);
        frontColor = ta.getColor(R.styleable.CircleProcessBar_cbFrontColor, 0xff66ccff);
        textColor = ta.getColor(R.styleable.CircleProcessBar_cbTextColor, 0xffff0000);
        textSize = ta.getDimension(R.styleable.CircleProcessBar_cbTextSize, 25f);

        strokeWidth = ta.getDimension(R.styleable.CircleProcessBar_cbStrokeWidth, 50f);
        radius = ta.getDimension(R.styleable.CircleProcessBar_cbRadius, 200f);

        progress = ta.getInt(R.styleable.CircleProcessBar_cbProcess, 0);
        max = ta.getInt(R.styleable.CircleProcessBar_cbMax, 100);
        addText = ta.getBoolean(R.styleable.CircleProcessBar_cbAddText, true);
        ta.recycle();
        // </end>

        // 初始化paint
        backPaint = new Paint();
        backPaint.setColor(backColor);
        backPaint.setAntiAlias(true);
        backPaint.setStyle(Paint.Style.STROKE);
        backPaint.setStrokeWidth(strokeWidth);

        frontPaint = new Paint();
        frontPaint.setColor(frontColor);
        frontPaint.setAntiAlias(true);
        frontPaint.setStyle(Paint.Style.STROKE);
        frontPaint.setStrokeWidth(strokeWidth);

        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);

        Paint.FontMetrics fm = textPaint.getFontMetrics();
        textHeight = (fm.descent - fm.ascent) / 3 * 2;
    }


    //重写测量大小的onMeasure方法和绘制View的核心方法onDraw()
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getRealSize(widthMeasureSpec);
        height = getRealSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        initRect();//根据半径找到外切正方形
        float angle = progress / (float) max * 360;//获取角度比例

        //先在底部画一个圆（空心圆）
        canvas.drawCircle(width / 2f, height / 2f, radius, backPaint);
        //画弧度
        canvas.drawArc(rect, -90, angle, false, frontPaint);
        //文字
        if(addText){
            canvas.drawText(progress + "%", width / 2f, (height+textHeight) / 2f, textPaint);
        }
    }

    private void initRect() {
        if (rect == null) {
            rect = new RectF();
            int viewSize = (int) (radius * 2);
            int left = (width - viewSize) / 2;
            int top = (height - viewSize) / 2;
            int right = left + viewSize;
            int bottom = top + viewSize;
            rect.set(left, top, right, bottom);
        }
    }

    public int getRealSize(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.UNSPECIFIED) {
            //自己计算
            result = (int) (radius * 2 + strokeWidth);
        } else {
            result = size;
        }
        return result;
    }

    //外部调用接口：初始化
    public void setAddText(boolean addText) {
        this.addText = addText;
    }

    public void setBackColor(int backColor) {
        this.backColor = backColor;
    }

    public void setFrontColor(int frontColor) {
        this.frontColor = frontColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setMax(int max) {
        this.max = max;
    }

    //初始化后修改进度
    public void refreshProcess(int process){
        this.progress = process;
        invalidate();
    }
}