package com.nepalese.virgocomponent.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nepalese.virgocomponent.R;
import com.nepalese.virgocomponent.common.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nepalese on 2021/2/26 15:41
 * @usage 粒子效果：星图
 */
public class VirgoParticles extends View {
    private static final String TAG = "VirgoParticles";
    private static final long MOVE_DELAY = 100;
    private static final int mParts = 6;//划分区

    private Paint paintLine;//线
    private Paint paintDot;//点

    private int mWidth, mHeight;//宽高
    private int mDotRadius;//点半径
    private int mDotNum;//初始点数
    private float mMoveSpace;//移动距离
    private float mMaxSpace;//最大吸引距离

    private int bgColor;//背景颜色
    private int dotColor;//点颜色
    private int lineColor;//线颜色

    private final List<VirgoPoint2> listDot = new ArrayList<>();//存放现有点容器
    private final List<VirgoPoint2> backupDot = new ArrayList<>();//备份未与其他点计算吸引的点的容器
    private final List<VirgoPoint2> clearDot = new ArrayList<>();//超出界内需删除的点的容器

    public VirgoParticles(Context context) {
        this(context, null);
    }

    public VirgoParticles(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoParticles(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
        startMove();
    }

    private void init(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.VirgoParticles);
        mDotNum = ta.getInt(R.styleable.VirgoParticles_vpDotNum, 72);
        mDotRadius = ta.getDimensionPixelSize(R.styleable.VirgoParticles_vpDotRadius, 5);
        mMaxSpace = ta.getFloat(R.styleable.VirgoParticles_vpMaxSpace, 120f);
        mMoveSpace = ta.getFloat(R.styleable.VirgoParticles_vpMoveSpace, 2f);
        bgColor = ta.getColor(R.styleable.VirgoParticles_vpBgColor, Color.rgb(24,31,26));
        dotColor = ta.getColor(R.styleable.VirgoParticles_vpDotColor, Color.GREEN);
        lineColor = ta.getColor(R.styleable.VirgoParticles_vpLineColor, Color.argb(80, 0, 255,0));
        ta.recycle();

        paintLine = new Paint();
        paintLine.setColor(lineColor);
        paintLine.setAntiAlias(true);
        paintLine.setStrokeWidth(2);
        paintLine.setStyle(Paint.Style.FILL);

        paintDot = new Paint();
        paintDot.setColor(dotColor);
        paintDot.setAntiAlias(true);
        paintDot.setStyle(Paint.Style.FILL);
    }

    private  void initDots(){
        listDot.clear();
        int ava = mDotNum/mParts;

        for(int i=0; i<mDotNum; i++){
            if(i<ava){
                //区块1：
                int x = CommonUtil.getRandomInt(1,mWidth/2);
                int y = CommonUtil.getRandomInt(1,mHeight/3);

                listDot.add(new VirgoPoint2(x,y));
            }else if(i<ava*2){
                //区块2：
                int x = CommonUtil.getRandomInt(mWidth/2,mWidth);
                int y = CommonUtil.getRandomInt(1,mHeight/3);

                listDot.add(new VirgoPoint2(x,y));
            }else if(i<ava*3){
                //区块3：
                int x = CommonUtil.getRandomInt(1,mWidth/2);
                int y = CommonUtil.getRandomInt(mHeight/3,mHeight*2/3);

                listDot.add(new VirgoPoint2(x,y));
            }else if(i<ava*4){
                //区块4：
                int x = CommonUtil.getRandomInt(mWidth/2,mWidth);
                int y = CommonUtil.getRandomInt(mHeight/3,mHeight*2/3);

                listDot.add(new VirgoPoint2(x,y));
            }else if(i<ava*5){
                //区块5：
                int x = CommonUtil.getRandomInt(1,mWidth/2);
                int y = CommonUtil.getRandomInt(mHeight*2/3,mHeight);

                listDot.add(new VirgoPoint2(x,y));
            }else{
                //区块6：
                int x = CommonUtil.getRandomInt(mWidth/2,mWidth);
                int y = CommonUtil.getRandomInt(mHeight*2/3,mHeight);

                listDot.add(new VirgoPoint2(x,y));
            }
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = this.getPaddingLeft() + this.getPaddingRight();
        int height = this.getPaddingTop() + this.getPaddingBottom();
        width = Math.max(width, this.getSuggestedMinimumWidth());
        height = Math.max(height, this.getSuggestedMinimumHeight());
        this.setMeasuredDimension(resolveSizeAndState(width, widthMeasureSpec, 0), resolveSizeAndState(height, heightMeasureSpec, 0));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.mWidth = this.getWidth();
        this.mHeight = this.getHeight();

        initDots();
    }

    protected void onDraw(Canvas canvas) {
        //背景
        canvas.drawColor(bgColor);

        //画点
        for(VirgoPoint2 p: listDot){
            canvas.drawCircle(p.getX(), p.getY(), mDotRadius, paintDot);
        }

        //连线
        backupDot.clear();
        backupDot.addAll(listDot);
        for(VirgoPoint2 p: listDot){
            backupDot.remove(p);
            for(VirgoPoint2 p1: backupDot){
                if(isNear(p, p1)){
                    canvas.drawLine(p.getX(), p.getY(), p1.getX(), p1.getY(), paintLine);
                }
            }
        }
    }

    //判断两个点是否在吸引范围
    private boolean isNear(VirgoPoint2 p1, VirgoPoint2 p2){
        if(Math.abs(p1.getX()-p2.getX())>mMaxSpace || Math.abs(p1.getY()-p2.getY())>mMaxSpace){
            return false;
        }
        return Math.sqrt(Math.pow((p1.getX()-p2.getX()), 2) + Math.pow((p1.getY()-p2.getY()), 2))<=mMaxSpace;
    }

    //实现36个方向
    private void moveDot(){
        //需新生成个数
        int mNewNum = 0;
        clearDot.clear();
        Log.i(TAG, "moveDot: dot num= " + listDot.size());
        for(VirgoPoint2 p: listDot){
            p.setX((float) (p.getX()+mMoveSpace*Math.cos(p.getOffset()*Math.PI/18f)));
            p.setY((float) (p.getY()-mMoveSpace*Math.sin(p.getOffset()*Math.PI/18f)));

            if(p.getX()<0 || p.getY()<0 || p.getX()>mWidth || p.getY()>mHeight){
                mNewNum++;
                clearDot.add(p);
            }
        }

        listDot.removeAll(clearDot);

        for(int i = 0; i< mNewNum; i++){
            generateDot();
        }

        invalidate();
    }

    private void generateDot(){
        int x = CommonUtil.getRandomInt(1,mWidth);
        int y = CommonUtil.getRandomInt(1,mHeight);

        listDot.add(new VirgoPoint2(x,y));
    }

    private void startMove(){
        stopMove();
        handler.post(moveTask);
    }

    private void stopMove(){
        handler.removeCallbacks(moveTask);
    }

    private final Runnable moveTask = new Runnable() {
        @Override
        public void run() {
            moveDot();
            handler.postDelayed(moveTask, MOVE_DELAY);
        }
    };

    private final Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopMove();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == GONE || visibility == INVISIBLE) {
            stopMove();
        } else {
            startMove();
        }
    }

    //////////////////////////////////////////////////////
    static class VirgoPoint2{
        private float x;
        private float y;
        private final int offset;//分36等分，每份10度

        public VirgoPoint2(int x, int y) {
            this.x = x;
            this.y = y;
            this.offset = CommonUtil.getRandomInt(1,36);
        }

        public int getOffset() {
            return offset;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }
    }
}