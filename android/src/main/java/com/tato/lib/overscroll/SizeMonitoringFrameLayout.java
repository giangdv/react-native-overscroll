package com.tato.lib.overscroll;

import javax.annotation.Nullable;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class SizeMonitoringFrameLayout extends FrameLayout {

    public static interface OnSizeChangedListener {
        void onSizeChanged(int width, int height, int oldWidth, int oldHeight);
    }

    private @Nullable OnSizeChangedListener mOnSizeChangedListener;

    public SizeMonitoringFrameLayout(Context context) {
        super(context);
    }

    public SizeMonitoringFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SizeMonitoringFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnSizeChangedListener(OnSizeChangedListener onSizeChangedListener) {
        mOnSizeChangedListener = onSizeChangedListener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mOnSizeChangedListener != null) {
            mOnSizeChangedListener.onSizeChanged(w, h, oldw, oldh);
        }
    }

}