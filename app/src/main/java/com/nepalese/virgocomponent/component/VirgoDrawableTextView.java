package com.nepalese.virgocomponent.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.nepalese.virgocomponent.R;

/**
 * @author nepalese on 2020/12/15 17:15
 * @usage 可设置drawable宽高的textview控件
 */
public class VirgoDrawableTextView extends androidx.appcompat.widget.AppCompatTextView {
    private static final String TAG = "VirgoDrawableTextView";

    public VirgoDrawableTextView(Context context) {
        this(context, null);
    }

    public VirgoDrawableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoDrawableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    //设置位置
    private static final int LEFT = 1, TOP = 2, RIGHT = 3, BOTTOM = 4;

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.VirgoDrawableTextView);
            int mWidth = a.getDimensionPixelSize(R.styleable.VirgoDrawableTextView_drawableWidth, 0);
            int mHeight =a.getDimensionPixelSize(R.styleable.VirgoDrawableTextView_drawableHeight,0);
            Drawable mDrawable =a.getDrawable(R.styleable.VirgoDrawableTextView_drawableSrc);
            int mLocation = a.getInt(R.styleable.VirgoDrawableTextView_drawableLocation, LEFT);
            a.recycle();

            drawDrawable(mDrawable, mWidth, mHeight, mLocation);
        }
    }

    private void drawDrawable(Drawable mDrawable, int mWidth, int mHeight, int mLocation) {
        if (mDrawable != null) {
            if (mWidth != 0 && mHeight != 0) {
                mDrawable.setBounds(0, 0, mWidth, mHeight);
            }
            switch (mLocation) {
                case LEFT:
                    this.setCompoundDrawables(mDrawable, null,
                            null, null);
                    break;
                case TOP:
                    this.setCompoundDrawables(null, mDrawable,
                            null, null);
                    break;
                case RIGHT:
                    this.setCompoundDrawables(null, null,
                            mDrawable, null);
                    break;
                case BOTTOM:
                    this.setCompoundDrawables(null, null, null,
                            mDrawable);
                    break;
            }
        }
    }
}
