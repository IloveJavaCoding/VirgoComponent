package com.nepalese.virgocomponent.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.nepalese.virgocomponent.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class VirgoPickerView extends View {
    private static final String TAG = "PickerView";

    private List<String> mDataList;
    private Paint mPaint, nPaint;
    private Timer timer;
    private MyTimerTask mTask;
    private onSelectListener mSelectListener;

    private int mViewHeight;
    private int mViewWidth;
    private float mLastDownY;
    private int mCurrentSelected;//选中的位置，这个位置是mDataList的中心位置，一直不变

    private float mMaxTextSize = 80;
    private float mMinTextSize = 40;
    private float mMoveLen = 0; //滑动的距离
    private final float mMaxTextAlpha = 255;
    private final float mMinTextAlpha = 120;
    private static final float MARGIN_ALPHA = 2.8f;//text之间间距和minTextSize之比
    private static final float SPEED = 10;//自动回滚到中间的速度

    private int mColorText;
    private int nColorText;
    private boolean canScroll = true;
    private boolean isInit = false;
    private boolean loop;  //控制是否首尾相接循环显示
    //----------------------------------------------------------------------------
    public VirgoPickerView(Context context) {
        super(context);
        init();
    }

    public VirgoPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.VirgoPickerView); //<attr name="isLoop" format="boolean" />
        loop = typedArray.getBoolean(R.styleable.VirgoPickerView_vpvIsLoop, true);
        mColorText = typedArray.getColor(R.styleable.VirgoPickerView_vpvMainColor, 0x66ccff);
        nColorText = typedArray.getColor(R.styleable.VirgoPickerView_vpvSecondColor, 0x666666);
        init();
    }

    private void init() {
        timer = new Timer();
        mDataList = new ArrayList<>();
        //used to draw the selected data
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(mColorText);

        //第二个paint,to draw the others
        nPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        nPaint.setStyle(Paint.Style.FILL);
        nPaint.setTextAlign(Paint.Align.CENTER);
        nPaint.setColor(nColorText);
    }

    Handler updateHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (Math.abs(mMoveLen) < SPEED) {
                mMoveLen = 0;

                if (mTask != null) {mTask.cancel();
                    mTask = null;
                    performSelect();
                }
            } else
                // 这里mMoveLen / Math.abs(mMoveLen)是为了保有mMoveLen的正负号，以实现上滚或下滚
                mMoveLen = mMoveLen - mMoveLen / Math.abs(mMoveLen) * SPEED;
            invalidate();
        }
    };

    public void setOnSelectListener(onSelectListener listener) {
        mSelectListener = listener;
    }

    private void performSelect() {
        if (mSelectListener != null)
            mSelectListener.onSelect(mDataList.get(mCurrentSelected));
    }

    //====================overWrite=======================================
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {//layout
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewHeight = getMeasuredHeight();
        mViewWidth = getMeasuredWidth();
        // 按照View的高度计算字体大小
        mMaxTextSize = mViewHeight / 7f;
        mMinTextSize = mMaxTextSize / 2.2f;
        isInit = true;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 根据index绘制view
        if (isInit)
            drawData(canvas);
    }

    private void drawData(Canvas canvas) {
        // 先绘制选中的text再往上往下绘制其余的text
        float scale = parabola(mViewHeight / 4.0f, mMoveLen);
        float size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize;
        mPaint.setTextSize(size);
        mPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha));

        // text居中绘制，注意baseline的计算才能达到居中，y值是text中心坐标
        float x = (float) (mViewWidth / 2.0);
        float y = (float) (mViewHeight / 2.0 + mMoveLen);
        Paint.FontMetricsInt fmi = mPaint.getFontMetricsInt();
        float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
        canvas.drawText(mDataList.get(mCurrentSelected), x, baseline, mPaint);

        // 绘制上方data
        for (int i = 1; (mCurrentSelected - i) >= 0; i++) {
            drawOtherText(canvas, i, -1);
        }
        // 绘制下方data
        for (int i = 1; (mCurrentSelected + i) < mDataList.size(); i++) {
            drawOtherText(canvas, i, 1);
        }
    }

    /**
     * @param canvas
     * @param position 距离mCurrentSelected的差值
     * @param type     1表示向下绘制，-1表示向上绘制
     */
    private void drawOtherText(Canvas canvas, int position, int type) {
        float d = MARGIN_ALPHA * mMinTextSize * position + type * mMoveLen;
        float scale = parabola(mViewHeight / 4.0f, d);
        float size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize;
        nPaint.setTextSize(size);
        nPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha));
        float y = (float) (mViewHeight / 2.0 + type * d);
        Paint.FontMetricsInt fmi = nPaint.getFontMetricsInt();
        float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
        canvas.drawText(mDataList.get(mCurrentSelected + type * position), (float) (mViewWidth / 2.0), baseline, nPaint);
    }

    /**
     * 抛物线
     * @param zero 零点坐标
     * @param x    偏移量
     * @return scale
     */
    private float parabola(float zero, float x) {
        float f = (float) (1 - Math.pow(x / zero, 2));
        return f < 0 ? 0 : f;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                doDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveLen += (event.getY() - mLastDownY);
                if (mMoveLen > MARGIN_ALPHA * mMinTextSize / 2) {
                    if (!loop && mCurrentSelected == 0) {
                        mLastDownY = event.getY();
                        invalidate();
                        return true;
                    }
                    if (!loop) mCurrentSelected--;
                    // 往下滑超过离开距离
                    moveTailToHead();
                    mMoveLen = mMoveLen - MARGIN_ALPHA * mMinTextSize;
                } else if (mMoveLen < -MARGIN_ALPHA * mMinTextSize / 2) {
                    if (mCurrentSelected == mDataList.size() - 1) {
                        mLastDownY = event.getY();
                        invalidate();
                        return true;
                    }
                    if (!loop) mCurrentSelected++;
                    // 往上滑超过离开距离
                    moveHeadToTail();
                    mMoveLen = mMoveLen + MARGIN_ALPHA * mMinTextSize;
                }

                mLastDownY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                doUp(event);
                break;
        }
        return true;
    }

    private void doDown(MotionEvent event) {
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
        mLastDownY = event.getY();
    }

    private void doUp(MotionEvent event) {
        // 抬起手后mCurrentSelected的位置由当前位置move到中间选中位置
        if (Math.abs(mMoveLen) < 0.0001) {
            mMoveLen = 0;
            return;
        }
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
        mTask = new MyTimerTask(updateHandler);
        timer.schedule(mTask, 0, 10);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (canScroll)
            return super.dispatchTouchEvent(event);
        else
            return false;
    }

    //------------------setting methods------------------------------------
    public void setData(List<String> datas) {
        mDataList = datas;
        mCurrentSelected = datas.size() / 4;
        invalidate();
    }

    //选择选中的内容
    public void setSelected(String mSelectItem) {
        for (int i = 0; i < mDataList.size(); i++){
            if (mDataList.get(i).equals(mSelectItem)) {
                setSelected(i);
                return;
            }
        }
        setSelected(0);
    }

    //选择选中的item的index
    public void setSelected(int selected) {
        mCurrentSelected = selected;
        if (loop) {
            int distance = mDataList.size() / 2 - mCurrentSelected;
            if (distance < 0)
                for (int i = 0; i < -distance; i++) {
                    moveHeadToTail();
                    mCurrentSelected--;
                }
            else if (distance > 0)
                for (int i = 0; i < distance; i++) {
                    moveTailToHead();
                    mCurrentSelected++;
                }
        }
        invalidate();
    }

    private void moveHeadToTail() {
        if (loop) {
            String head = mDataList.get(0);
            mDataList.remove(0);
            mDataList.add(head);
        }
    }

    private void moveTailToHead() {
        if (loop) {
            String tail = mDataList.get(mDataList.size() - 1);
            mDataList.remove(mDataList.size() - 1);
            mDataList.add(0, tail);
        }
    }

    public void setIsLoop(boolean isLoop) {
        loop = isLoop;
    }

    public void setmColorText(int mColorText) {
        this.mColorText = mColorText;
    }

    public void setnColorText(int nColorText) {
        this.nColorText = nColorText;
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    //============================================================
    static class MyTimerTask extends TimerTask {
        Handler handler;

        public MyTimerTask(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            handler.sendMessage(handler.obtainMessage());
        }
    }

    public interface onSelectListener {
        void onSelect(String text);
    }
}
