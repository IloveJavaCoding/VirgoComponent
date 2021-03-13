package com.nepalese.virgocomponent.component.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.nepalese.virgocomponent.component.bean.VirgoPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nepalese on 2021/2/24 15:32
 * @usage 简易五子棋
 */
public class VirgoFiveInARow extends View {
    private static final String TAG = "VirgoFiveInARow";
    private static final int minWigth = 100;
    private static final int maxStep = 225;//15*15

    public static final int WIN_WHITE = 1;//白子赢
    public static final int WIN_BLACK = 2;//黑子赢
    public static final int WIN_DRAW = 3;//平局

    private final Context context;
    private resultCallback callback;//结果回调

    private Paint paintLine;//画背景线
    private Paint paintWhite;//白子
    private Paint paintBlack;//黑子

    private int mWidth, mHeight;//宽高，相等
    private int mLineNum;//等分数
    private int mSteps;//已下个数
    private float mRect;//每格宽度
    private float mPadding;//边围宽度
    private float mSignalR;//标点半径
    private boolean isWhiteGo = true;//白子先行
    private boolean isFreeze = false;//游戏结束？
    private int bgColor;//背景颜色

    private final List<VirgoPoint> listWhite = new ArrayList<>();
    private final List<VirgoPoint> listBlack = new ArrayList<>();

    public VirgoFiveInARow(Context context) {
        this(context, null);
    }

    public VirgoFiveInARow(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoFiveInARow(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        Init();
    }

    private void Init() {
        mSteps = 0;
        mLineNum = 14;
        mPadding = 60;
        mSignalR = 5;

        bgColor = Color.argb(80,212,205,155);

        paintLine = new Paint();
        paintLine.setColor(Color.BLACK);
        paintLine.setAntiAlias(true);

        paintWhite = new Paint();
        paintWhite.setColor(Color.WHITE);
        paintWhite.setAntiAlias(true);
        paintWhite.setStyle(Paint.Style.FILL);

        paintBlack = new Paint();
        paintBlack.setColor(Color.BLACK);
        paintBlack.setAntiAlias(true);
        paintBlack.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = mWidth = getRealSize(widthMeasureSpec);
        mRect = (mWidth-mPadding*2)/(mLineNum*1f);
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
        //背景
        canvas.drawColor(bgColor);

        //横线
        for(int i=0; i<=mLineNum; i++){
            canvas.drawLine(mPadding, mRect*i + mPadding, mWidth-mPadding, mRect*i + mPadding, paintLine);
        }
        //竖线
        for(int i=0; i<=mLineNum; i++){
            canvas.drawLine(mRect*i + mPadding, mPadding, mRect*i + mPadding, mHeight-mPadding, paintLine);
        }

        //画标点
        canvas.drawCircle(mPadding+3*mRect, mPadding+3*mRect, mSignalR, paintBlack);
        canvas.drawCircle(mPadding+3*mRect, mPadding+11*mRect, mSignalR, paintBlack);
        canvas.drawCircle(mPadding+11*mRect, mPadding+3*mRect, mSignalR, paintBlack);
        canvas.drawCircle(mPadding+11*mRect, mPadding+11*mRect, mSignalR, paintBlack);

        //画白棋
        for(VirgoPoint p: listWhite){
            canvas.drawCircle(p.getX()*mRect+mPadding, p.getY()*mRect+mPadding, mRect/3f, paintWhite);
        }

        //画黑棋
        for(VirgoPoint p: listBlack){
            canvas.drawCircle(p.getX()*mRect+mPadding, p.getY()*mRect+mPadding, mRect/3f, paintBlack);
        }

        //判断输赢
        //white win
        if(listWhite.size()>=5){
            for(VirgoPoint p: listWhite){
               if(judgeLeft(p, 1) || judgeLeftTop(p, 1) || judgeTop(p, 1)
                       || judgeRightTop(p, 1) || judgeRight(p, 1) || judgeRightBottom(p, 1)
                       || judgeBottom(p, 1) || judgeLeftBottom(p, 1)){
                   win(WIN_WHITE);
                   return;
               }
            }
        }

        //black win
        if(listBlack.size()>=5){
            for(VirgoPoint p: listBlack){
                if(judgeLeft(p, 2) || judgeLeftTop(p, 2) || judgeTop(p, 2)
                        || judgeRightTop(p, 2) || judgeRight(p, 2) || judgeRightBottom(p, 2)
                        || judgeBottom(p, 2) || judgeLeftBottom(p, 2)){
                    win(WIN_BLACK);
                    return;
                }
            }
        }
    }

    private void win(int a){
        isFreeze = true;
        callback.gameOver(a);
    }

    //判断p左边是否连子
    private boolean judgeLeft(VirgoPoint point, int a){
        if(point.getX()>3){
            if(a==1){//白子
                return hasPointWhite(new VirgoPoint(point.getX()-1, point.getY()))
                        && hasPointWhite(new VirgoPoint(point.getX()-2, point.getY()))
                        && hasPointWhite(new VirgoPoint(point.getX()-3, point.getY()))
                        && hasPointWhite(new VirgoPoint(point.getX()-4, point.getY()));
            }else{//黑子
                return hasPointBlack(new VirgoPoint(point.getX()-1, point.getY()))
                        && hasPointBlack(new VirgoPoint(point.getX()-2, point.getY()))
                        && hasPointBlack(new VirgoPoint(point.getX()-3, point.getY()))
                        && hasPointBlack(new VirgoPoint(point.getX()-4, point.getY()));
            }
        }
        return false;
    }

    //判断p左上是否连子
    private boolean judgeLeftTop(VirgoPoint point, int a){
        if(point.getX()>3 && point.getY()>3){
            if(a==1){//白子
                return hasPointWhite(new VirgoPoint(point.getX()-1, point.getY()-1))
                        && hasPointWhite(new VirgoPoint(point.getX()-2, point.getY()-2))
                        && hasPointWhite(new VirgoPoint(point.getX()-3, point.getY()-3))
                        && hasPointWhite(new VirgoPoint(point.getX()-4, point.getY()-4));
            }else{//黑子
                return hasPointBlack(new VirgoPoint(point.getX()-1, point.getY()-1))
                        && hasPointBlack(new VirgoPoint(point.getX()-2, point.getY()-2))
                        && hasPointBlack(new VirgoPoint(point.getX()-3, point.getY()-3))
                        && hasPointBlack(new VirgoPoint(point.getX()-4, point.getY()-4));
            }
        }
        return false;
    }

    //判断p上边是否连子
    private boolean judgeTop(VirgoPoint point, int a){
        if(point.getY()>3){
            if(a==1){//白子
                return hasPointWhite(new VirgoPoint(point.getX(), point.getY()-1))
                        && hasPointWhite(new VirgoPoint(point.getX(), point.getY()-2))
                        && hasPointWhite(new VirgoPoint(point.getX(), point.getY()-3))
                        && hasPointWhite(new VirgoPoint(point.getX(), point.getY()-4));
            }else{//黑子
                return hasPointBlack(new VirgoPoint(point.getX(), point.getY()-1))
                        && hasPointBlack(new VirgoPoint(point.getX(), point.getY()-2))
                        && hasPointBlack(new VirgoPoint(point.getX(), point.getY()-3))
                        && hasPointBlack(new VirgoPoint(point.getX(), point.getY()-4));
            }
        }
        return false;
    }

    //判断p右上是否连子
    private boolean judgeRightTop(VirgoPoint point, int a){
        if(point.getX()<mLineNum-3 && point.getY()>3){
            if(a==1){//白子
                return hasPointWhite(new VirgoPoint(point.getX()+1, point.getY()-1))
                        && hasPointWhite(new VirgoPoint(point.getX()+2, point.getY()-2))
                        && hasPointWhite(new VirgoPoint(point.getX()+3, point.getY()-3))
                        && hasPointWhite(new VirgoPoint(point.getX()+4, point.getY()-4));
            }else{//黑子
                return hasPointBlack(new VirgoPoint(point.getX()+1, point.getY()-1))
                        && hasPointBlack(new VirgoPoint(point.getX()+2, point.getY()-2))
                        && hasPointBlack(new VirgoPoint(point.getX()+3, point.getY()-3))
                        && hasPointBlack(new VirgoPoint(point.getX()+4, point.getY()-4));
            }
        }
        return false;
    }

    //判断p右边是否连子
    private boolean judgeRight(VirgoPoint point, int a){
        if(point.getX()<mLineNum-3){
            if(a==1){//白子
                return hasPointWhite(new VirgoPoint(point.getX()+1, point.getY()))
                        && hasPointWhite(new VirgoPoint(point.getX()+2, point.getY()))
                        && hasPointWhite(new VirgoPoint(point.getX()+3, point.getY()))
                        && hasPointWhite(new VirgoPoint(point.getX()+4, point.getY()));
            }else{//黑子
                return hasPointBlack(new VirgoPoint(point.getX()+1, point.getY()))
                        && hasPointBlack(new VirgoPoint(point.getX()+2, point.getY()))
                        && hasPointBlack(new VirgoPoint(point.getX()+3, point.getY()))
                        && hasPointBlack(new VirgoPoint(point.getX()+4, point.getY()));
            }
        }
        return false;
    }

    //判断p右下是否连子
    private boolean judgeRightBottom(VirgoPoint point, int a){
        if(point.getX()<mLineNum-3 && point.getY()<mLineNum-3){
            if(a==1){//白子
                return hasPointWhite(new VirgoPoint(point.getX()+1, point.getY()+1))
                        && hasPointWhite(new VirgoPoint(point.getX()+2, point.getY()+2))
                        && hasPointWhite(new VirgoPoint(point.getX()+3, point.getY()+3))
                        && hasPointWhite(new VirgoPoint(point.getX()+4, point.getY()+4));
            }else{//黑子
                return hasPointBlack(new VirgoPoint(point.getX()+1, point.getY()+1))
                        && hasPointBlack(new VirgoPoint(point.getX()+2, point.getY()+2))
                        && hasPointBlack(new VirgoPoint(point.getX()+3, point.getY()+3))
                        && hasPointBlack(new VirgoPoint(point.getX()+4, point.getY()+4));
            }
        }
        return false;
    }

    //判断p下边是否连子
    private boolean judgeBottom(VirgoPoint point, int a){
        if(point.getY()<mLineNum-3){
            if(a==1){//白子
                return hasPointWhite(new VirgoPoint(point.getX(), point.getY()+1))
                        && hasPointWhite(new VirgoPoint(point.getX(), point.getY()+2))
                        && hasPointWhite(new VirgoPoint(point.getX(), point.getY()+3))
                        && hasPointWhite(new VirgoPoint(point.getX(), point.getY()+4));
            }else{//黑子
                return hasPointBlack(new VirgoPoint(point.getX(), point.getY()+1))
                        && hasPointBlack(new VirgoPoint(point.getX(), point.getY()+2))
                        && hasPointBlack(new VirgoPoint(point.getX(), point.getY()+3))
                        && hasPointBlack(new VirgoPoint(point.getX(), point.getY()+4));
            }
        }
        return false;
    }

    //判断p左下是否连子
    private boolean judgeLeftBottom(VirgoPoint point, int a){
        if(point.getX()>3 && point.getY()<mLineNum-3){
            if(a==1){//白子
                return hasPointWhite(new VirgoPoint(point.getX()-1, point.getY()+1))
                        && hasPointWhite(new VirgoPoint(point.getX()-2, point.getY()+2))
                        && hasPointWhite(new VirgoPoint(point.getX()-3, point.getY()+3))
                        && hasPointWhite(new VirgoPoint(point.getX()-4, point.getY()+4));
            }else{//黑子
                return hasPointBlack(new VirgoPoint(point.getX()-1, point.getY()+1))
                        && hasPointBlack(new VirgoPoint(point.getX()-2, point.getY()+2))
                        && hasPointBlack(new VirgoPoint(point.getX()-3, point.getY()+3))
                        && hasPointBlack(new VirgoPoint(point.getX()-4, point.getY()+4));
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if(!isFreeze){
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    //生成新子
                    generatePoint(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    //落子无悔
                    invalidate();
                    //判断是否已无路可走
                    if(mSteps>=maxStep){
                        win(WIN_DRAW);
                    }
                    break;
            }
        }

        return true;
    }

    private void generatePoint(float x, float y) {
        if(x<mPadding || y<mPadding || x>mRect*mLineNum+mPadding || y>mRect*mLineNum+mPadding){
            return;
        }

        int pX = Math.round((x-mPadding)/mRect);
        int pY = Math.round((y-mPadding)/mRect);

        VirgoPoint p = new VirgoPoint(pX, pY);
        //判断是否已被占领
        if(hasPointWhite(p)||hasPointBlack(p)){
            return;
        }

        mSteps++;

        if(isWhiteGo){
            isWhiteGo = false;
            listWhite.add(p);
        }else{
            isWhiteGo = true;
            listBlack.add(p);
        }

        //下一步
        callback.nextPlay(isWhiteGo);
    }

    //判断白子是否含有该棋子
    private boolean hasPointWhite(VirgoPoint p){
        for(VirgoPoint p1: listWhite){
            if(p1.getX()==p.getX() && p1.getY()==p.getY()){
                return true;
            }
        }

        return false;
    }

    //判断黑子是否含有该棋子
    private boolean hasPointBlack(VirgoPoint p){
        for(VirgoPoint p1: listBlack){
            if(p1.getX()==p.getX() && p1.getY()==p.getY()){
                return true;
            }
        }

        return false;
    }

    public interface resultCallback{
        void gameOver(int a);

        void nextPlay(boolean isWhite);
    }

    /////////////////////////////////////////////api////////////////////////////////////////////////
    //悔棋
    public void lastStep(){
        if(!isFreeze){//未结束
            if(isWhiteGo){//撤黑棋
                listBlack.remove(listBlack.size()-1);
                isWhiteGo = false;//黑棋接着走
            }else{//撤白棋
                listWhite.remove(listWhite.size()-1);
                isWhiteGo = true;//白棋接着走
            }
            invalidate();

            callback.nextPlay(isWhiteGo);
        }
    }

    public void setCallback(resultCallback callback) {
        this.callback = callback;
    }

    public void restartGame() {
        listWhite.clear();
        listBlack.clear();
        mSteps=0;

        isFreeze = false;
        isWhiteGo = true;
        invalidate();
        callback.nextPlay(isWhiteGo);
    }
}