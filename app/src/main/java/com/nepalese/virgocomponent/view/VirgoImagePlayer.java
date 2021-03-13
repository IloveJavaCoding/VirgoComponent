package com.nepalese.virgocomponent.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.nepalese.virgocomponent.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author nepalese on 2021/1/12 15:11
 * @usage 可设置动画效果的滚动图片播放器
 */
//todo 增加圆角效果
public class VirgoImagePlayer extends RelativeLayout {
    private static final String TAG = "VirgoImagePlayer";

    private Context context;
    private ViewPager viewPager;
    private VirgoPagerAdapter adapter;
    private LinearLayout layoutDot; // 点点承载容器
    private Timer timer;// 控制自动跳转
    private TimerTask timerTask;

    private List<VirgoRoundImageView> imageViewList; // 图库集
    private List<VirgoRoundImageView> dotList; // 点点集

    private int dotSize; // 点点的大小
    private int dotMargin; // 点点的间距
    private int duration; // 滑动间隔
    private boolean addDot;// 是否添加点点
    private boolean isDown = false; // 监听滑动图片

    private Animation inAnim; // 进入动画
    private Animation outAnim; // 出去动画
    private int dotFocus; // 点点资源文件id
    private int dotUnFocus;// 点点资源文件id

    public VirgoImagePlayer(Context context) {
        this(context, null);
    }

    public VirgoImagePlayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoImagePlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initStyle(attrs);
        init();
    }

    private void init() {
        initUI();
        initData();
        setListener();
    }

    private void initStyle(AttributeSet attrs) {
        //自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VirgoImagePlayer);
        dotSize = typedArray.getDimensionPixelSize(R.styleable.VirgoImagePlayer_vipDotSize, 15);
        dotMargin = typedArray.getDimensionPixelSize(R.styleable.VirgoImagePlayer_vipDotMargin, 7);
        duration = typedArray.getInt(R.styleable.VirgoImagePlayer_vipDuration, 10000);
        addDot = typedArray.getBoolean(R.styleable.VirgoImagePlayer_vipAddDot, true);
        dotFocus = typedArray.getResourceId(R.styleable.VirgoImagePlayer_vipDotFocus, R.drawable.shape_dot_focus);
        dotUnFocus = typedArray.getResourceId(R.styleable.VirgoImagePlayer_vipDotUnFocus, R.drawable.shape_dot_unfocus);
        typedArray.recycle();
    }

    private void initUI(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootview = inflater.inflate(R.layout.layout_virgo_image_player, this, true);
        viewPager = rootview.findViewById(R.id.viewpager);
        layoutDot = rootview.findViewById(R.id.layoutDots);
    }

    private void initData() {
        imageViewList = new ArrayList<>();
        dotList = new ArrayList<>();
        adapter = new VirgoPagerAdapter(imageViewList);
        viewPager.setAdapter(adapter);

        // 默认进出场动画
        inAnim = AnimationUtils.loadAnimation(context, R.anim.anim_scale_center_in);
        outAnim = AnimationUtils.loadAnimation(context, R.anim.anim_scale_center_out);

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (isDown || viewPager == null || imageViewList.size() <= 1) {
                    return;
                }

                int count = imageViewList.size();
                int nextItem = (viewPager.getCurrentItem() + 1) % count;
                imageViewList.get(viewPager.getCurrentItem()).startAnimation(outAnim);// 出场动画
                handler.sendEmptyMessageDelayed(nextItem, outAnim.getDuration());// 上一张完全出场后进入下一张
            }
        };
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListener() {
        viewPager.setOnTouchListener(new OnTouchListener() {
            float oldX = 0f, newX = 0f;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        oldX = motionEvent.getX();
                        isDown = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        isDown = false;
                        newX = motionEvent.getX();
                        if(newX-oldX>10){
                            //向右滑
                            Log.e(TAG, "onTouch: 向右滑");
                            if(viewPager.getCurrentItem()==0){
                                handler.sendEmptyMessage(imageViewList.size()-1);
                            }
                        }else if(newX-oldX<-10){
                            //向左滑
                            Log.e(TAG, "onTouch: 向左滑");
                            if(viewPager.getCurrentItem()==(imageViewList.size()-1)){
                                handler.sendEmptyMessage(0);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                }
                return false;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {//选中后当前页 从0开始
                //监听图片变化控制点点位置
                if(imageViewList.size()<=1){
                    //仅一张图时不显示
                    return;
                }

                if(addDot){
                    for(int i=0; i<imageViewList.size(); i++){
                        dotList.get(i).setImageResource(getDotResId(i,position));
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //0 SCROLL_STATE_IDLE: pager处于空闲状态
                //1 SCROLL_STATE_DRAGGING： pager处于正在拖拽中
                //2 SCROLL_STATE_SETTLING： pager正在自动沉降，相当于松手后，pager恢复到一个完整pager的过程
            }
        });
    }

    private void startPlay(){
        timer.scheduleAtFixedRate(timerTask, 1000, duration);
    }

    private final Handler handler = new Handler(Looper.myLooper()) {
        public void handleMessage(Message msg) {
            viewPager.setCurrentItem(msg.what, false);// false: 取消原本平移动画
            imageViewList.get(msg.what).startAnimation(inAnim);// 进场动画
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(timer!=null){
            timer.cancel();
            timer = null;
            timerTask.cancel();
            timerTask = null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static class VirgoPagerAdapter extends PagerAdapter {
        List<VirgoRoundImageView> list;

        public VirgoPagerAdapter(List<VirgoRoundImageView> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list==null?0:list.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view==object;
        }

        @NonNull
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            VirgoRoundImageView VirgoRoundImageView = list.get(position);
            container.addView(VirgoRoundImageView);
            return VirgoRoundImageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
            container.removeView(list.get(position));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void play(){
        startPlay();
    }

    public void setImages(List<String> url){
        if(url==null || url.size()<=0){
            return;
        }

        layoutDot.removeAllViews();
        imageViewList.clear();
        dotList.clear();

        if(url.size()==1){
            //仅一张图
            VirgoRoundImageView imageView = new VirgoRoundImageView(context);
            imageView.setScaleType(VirgoRoundImageView.ScaleType.CENTER_CROP);
            Glide.with(context).load(url.get(0)).into(imageView);
            imageViewList.add(imageView);
        }else{
            //有多张图
            //单个点的布局
            LinearLayout.LayoutParams paramsDot = new LinearLayout.LayoutParams(dotSize, dotSize);
            paramsDot.setMargins(dotMargin, dotMargin, dotMargin, dotMargin);
            for(int i=0; i<url.size(); i++){
                VirgoRoundImageView img = new VirgoRoundImageView(context);
                img.setScaleType(VirgoRoundImageView.ScaleType.CENTER_CROP);
                Glide.with(context).load(url.get(i)).into(img);
                imageViewList.add(img);

                if(addDot){
                    VirgoRoundImageView dot = new VirgoRoundImageView(context);
                    dot.setImageResource(getDotResId(i,0));
                    dotList.add(dot);
                    layoutDot.addView(dot, paramsDot);
                }
            }
        }
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

    private int getDotResId(int position, int aim){
        int resid;
        if(position==aim){
            resid = dotFocus;
        } else {
            resid = dotUnFocus;
        }
        return resid;
    }

    public void setDuration(int duration) {
        if(duration<1000){
            duration = 1000;
        }
        this.duration = duration;
    }

    //设置点点位置， 默认左下角
    public void setDotLayout(int gravity){
        layoutDot.setGravity(gravity);
    }

    //是否显示点点
    public void setAddDot(boolean addDot) {
        this.addDot = addDot;
        layoutDot.setVisibility(INVISIBLE);
    }

    //设置自定义进场场动画
    public void setInAnim(Animation inAnim){
        this.inAnim = inAnim;
    }

    public void setOutAnim(Animation outAnim) {
        this.outAnim = outAnim;
    }
}