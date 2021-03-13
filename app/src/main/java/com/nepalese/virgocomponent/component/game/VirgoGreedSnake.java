package com.nepalese.virgocomponent.component.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nepalese.virgocomponent.common.CommonUtil;
import com.nepalese.virgocomponent.component.bean.VirgoPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nepalese on 2021/1/20 10:00
 * @usage 趣玩贪吃蛇
 */
public class VirgoGreedSnake extends View {
    private static final String TAG = "VirgoGreedSnake";
    private static final int minWigth = 100;
    public static final int DIR_LEFT = 1;
    public static final int DIR_UP = 2;
    public static final int DIR_RIGHT = 3;
    public static final int DIR_DOWN = 4;

    private final Context context;
    private gradeCallBack gradeCallBack;

    private Paint paintLine;//画背景线
    private Paint paintDot;//画食物
    private Paint paintSnake;//画蛇身

    private int mWidth, mHeight;//宽高，相等
    private int mLineNum;//等分数
    private int mAddSpeed;//增加速度
    private int mCurDirect;//1:left; 2:up; 3:right; 4:down;
    private int mCountTime;//计时
    private float mRect;//每格宽度
    private boolean mHasEat;//是否吃到食物
    private boolean mInvincible;//开启无敌模式

    private final VirgoPoint randomPoint = new VirgoPoint();//随机刷新点
    private final List<VirgoPoint> pointList = new ArrayList<>();//蛇体

    public VirgoGreedSnake(Context context) {
        this(context, null);
    }

    public VirgoGreedSnake(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoGreedSnake(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        //默认值
        mLineNum = 15;
        mAddSpeed = 0;
        mCurDirect = 1;
        mCountTime = 0;
        mHasEat = true;
        mInvincible = false;

        paintLine = new Paint();
        paintLine.setColor(Color.BLACK);
        paintLine.setAntiAlias(true);

        paintDot = new Paint();
        paintDot.setColor(Color.RED);
        paintDot.setAntiAlias(true);
        paintDot.setStyle(Paint.Style.FILL);

        paintSnake = new Paint();
        paintSnake.setColor(Color.BLUE);
        paintSnake.setAntiAlias(true);
        paintSnake.setStyle(Paint.Style.FILL);

        initSnake();
    }

    private  void initSnake(){
        //初始化蛇体
        int x = CommonUtil.getRandomInt(1,mLineNum/2);
        int y = CommonUtil.getRandomInt(1,mLineNum/2);
        VirgoPoint point1 = new VirgoPoint(x, y);
        VirgoPoint point2 = new VirgoPoint(x+1, y);
        VirgoPoint point3 = new VirgoPoint(x+2, y);
        VirgoPoint point4 = new VirgoPoint(x+3, y);
        pointList.add(point1);
        pointList.add(point2);
        pointList.add(point3);
        pointList.add(point4);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = mWidth = getRealSize(widthMeasureSpec);
        mRect = mWidth/(mLineNum*1f);
        setMeasuredDimension(mWidth, mHeight);
    }

    public int getRealSize(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.UNSPECIFIED) {
            result = minWigth;
        } else {
            result = size;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //背景；
        canvas.drawColor(Color.argb(60,102, 204, 255));

        //横线
        for(int i=0; i<=mLineNum; i++){
            canvas.drawLine(0, mRect*i, mWidth, mRect*i, paintLine);
        }
        //竖线
        for(int i=0; i<=mLineNum; i++){
            canvas.drawLine(mRect*i, 0, mRect*i, mHeight, paintLine);
        }

        generateFood(canvas);

        drawSnake(canvas);
    }

    //画随机点
    private void generateFood(Canvas canvas){
        if(mHasEat){
            mHasEat = false;
            //不能在蛇体内
            int x,y;
            while (true){
                x = CommonUtil.getRandomInt(0,mLineNum);
                y = CommonUtil.getRandomInt(0,mLineNum);
                boolean isNew = true;
                for(VirgoPoint point: pointList){
                    if(x==point.getX() && y==point.getY()){
                        //重新生成
                        isNew = false;
                        break;
                    }
                }
                if(isNew) break;
            }

            randomPoint.setX(x);
            randomPoint.setY(y);
        }

        canvas.drawRect(randomPoint.getX()*mRect, randomPoint.getY()*mRect,
                (randomPoint.getX()+1)*mRect, (randomPoint.getY()+1)*mRect, paintDot);
    }

    //画蛇体
    private void drawSnake(Canvas canvas){
        for(int i=0; i<pointList.size(); i++){
//            if(i==0){
//                //蛇头
//                canvas.drawSnakewBitmap(BitmapUtil.getBitmapFromRes(context, R.mipmap.icon_long), pointList.get(i).getX()*mRect, pointList.get(i).getY()*mRect, paintSnake);
//            }else{
//                canvas.drawRect(pointList.get(i).getX()*mRect, pointList.get(i).getY()*mRect,
//                        (pointList.get(i).getX()+1)*mRect, (pointList.get(i).getY()+1)*mRect, paintSnake);
//            }

            canvas.drawRect(pointList.get(i).getX()*mRect, pointList.get(i).getY()*mRect,
                    (pointList.get(i).getX()+1)*mRect, (pointList.get(i).getY()+1)*mRect, paintSnake);

        }
    }

    //==============================================================================================
    //仅需控制蛇头方向，后面的点都变为原先前面的点
    private final Runnable moveTask = new Runnable() {
        @Override
        public void run() {
            boolean isOver = false;
            VirgoPoint last = new VirgoPoint(pointList.get(0));//记录上一个
            VirgoPoint tail = new VirgoPoint(pointList.get(pointList.size()-1));//当吃到时增加这一个

            for(int i=0; i<pointList.size(); i++){
                if(i>0){
                    //last 与当前互换
                    VirgoPoint temp = new VirgoPoint(pointList.get(i));

                    pointList.get(i).setX(last.getX());
                    pointList.get(i).setY(last.getY());

                    last.setX(temp.getX());
                    last.setY(temp.getY());
                }else{//第一个
                    switch (mCurDirect){
                        case DIR_LEFT://left
                            pointList.get(i).setX(last.getX()-1);
                            break;
                        case DIR_UP://up
                            pointList.get(i).setY(last.getY()-1);
                            break;
                        case DIR_RIGHT://right
                            pointList.get(i).setX(last.getX()+1);
                            break;
                        case DIR_DOWN://down
                            pointList.get(i).setY(last.getY()+1);
                            break;
                    }

                    VirgoPoint temp = new VirgoPoint(pointList.get(i));
                    if(mInvincible){
                        //穿墙
                        if(temp.getX()<0){//从左边穿墙
                            pointList.get(i).setX(mLineNum-1);
                        }else if(temp.getX()>mLineNum){//从右边穿墙
                            pointList.get(i).setX(0);
                        }else if(temp.getY()<0){//从上边穿墙
                            pointList.get(i).setY(mLineNum-1);
                        }else if(temp.getY()>mLineNum){//从下边穿墙
                            pointList.get(i).setY(0);
                        }
                    }else{
                        //撞墙或碰到自己即死亡
                        if(temp.getX()<0 || temp.getX()>mLineNum || temp.getY()<0 || temp.getY()>mLineNum){
                            gameOver();
                            isOver = true;
                            break;
                        }

                        for(int j=1; j<pointList.size(); j++){
                            if(temp.getX()==pointList.get(j).getX() && temp.getY()==pointList.get(j).getY()){
                                gameOver();
                                isOver = true;
                                break;
                            }
                        }
                    }
                }
            }
            if(isOver){
                return;
            }

            //吃食
            VirgoPoint first = pointList.get(0);
            if(first.getX()==randomPoint.getX() && first.getY()==randomPoint.getY()){
                mHasEat = true;
                pointList.add(tail);
                gradeCallBack.updateGrade(pointList.size()-4);
                //每增加3个提升一点速度
                if(mAddSpeed <45){
                    mAddSpeed = (pointList.size()-4)/3;
                }
            }

            invalidate();
            handler.postDelayed(moveTask, 500- mAddSpeed *10);
        }
    };

    private void gameOver(){
        stopGame();
        gradeCallBack.onGameOver();
    }

    private void reStart() {
        mAddSpeed = 0;
        mCountTime = 0;
        pointList.clear();
        initSnake();
        startGame();
    }

    private final Runnable countTask = new Runnable() {
        @Override
        public void run() {
            ++mCountTime;
            gradeCallBack.updateTime(updateTime());
            handler.postDelayed(countTask, 1000);
        }
    };

    private void startCountTask(){
        stopCountTask();
        handler.post(countTask);
    }

    private void stopCountTask(){
        handler.removeCallbacks(countTask);
    }

    private String updateTime() {
        int min=0, sec;
        if(mCountTime >=60){
            min = mCountTime /60;
        }
        sec = mCountTime -60*min;

        return formatMS(min) + " : " + formatMS(sec);
    }

    private String formatMS(int ms){
        if(ms>9){
            return String.valueOf(ms);
        }else{
            return "0" + ms;
        }
    }

    private final Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };

    public interface gradeCallBack{
        //分数回调
        void updateGrade(int grade);

        //计时回调
        void updateTime(String time);

        //结束回调
        void onGameOver();
    }
    //===============================api=================================
    public void startGame(){
        stopGame();
        handler.post(moveTask);

        startCountTask();
    }

    public void stopGame(){
        stopCountTask();
        handler.removeCallbacks(moveTask);
    }

    public void changeDirection(int direction){
        this.mCurDirect = direction;
    }

    public void setmInvincible(boolean mInvincible) {
        this.mInvincible = mInvincible;
    }

    public void setGradeCallBack(gradeCallBack gradeCallBack) {
        this.gradeCallBack = gradeCallBack;
    }
}