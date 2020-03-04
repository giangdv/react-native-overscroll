package com.tato.lib.overscroll;

import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.ScrollView;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import me.everything.android.ui.overscroll.IOverScrollDecor;
import me.everything.android.ui.overscroll.IOverScrollUpdateListener;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * Created by eagleliu on 2017/12/13.
 * Edit by kaseru
 */
public class ReactOverScroll extends SizeMonitoringFrameLayout {
    int currentScrollY;
    ReactContext context;
    View child;
    ViewTreeObserver.OnScrollChangedListener scrollEvent = new ViewTreeObserver.OnScrollChangedListener() {
        @Override
        public void onScrollChanged() {
            int scrollY = child.getScrollY();
            int scrollX = child.getScrollX();
            if (child == null || currentScrollY == scrollY) return;
            currentScrollY = scrollY;
            ReactOverScroll.this.onScrollEvent(scrollX, scrollY, child.getWidth(), child.getHeight());
        }
    };

    public ReactOverScroll(ReactContext context) {
        super(context);
        this.context = context;
    }

    @Override
    public void addView(final View child, int index) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ReactOverScroll can host only one direct child");
        }
        super.addView(child, index);
        this.child = child;
        IOverScrollDecor decor = null;
        if (child != null) {
            if (child instanceof ScrollView) {
                decor = OverScrollDecoratorHelper.setUpOverScroll((ScrollView)child);
            } else if (child instanceof HorizontalScrollView) {
                decor = OverScrollDecoratorHelper.setUpOverScroll((HorizontalScrollView)child);
            } else if (child instanceof ListView) {
                decor = OverScrollDecoratorHelper.setUpOverScroll((ListView)child);
            } else {
                decor =  OverScrollDecoratorHelper.setUpStaticOverScroll(child, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
            }
        }
        if (decor != null) {
            decor.setOverScrollUpdateListener(new IOverScrollUpdateListener() {
                @Override
                public void onOverScrollUpdate(IOverScrollDecor decor, int state, float offset) {
                    final View view = decor.getView();
                    int scrollY = (int) offset;
                    if (scrollY > 0) {
                        scrollY = -scrollY;
                    } else if (scrollY < 0) {
                        scrollY = view.getHeight() + Math.abs(scrollY);
                    }
                    if (scrollY != 0) ReactOverScroll.this.onScrollEvent(0, scrollY, view.getWidth(), view.getHeight());
                }
            });
            child.getViewTreeObserver().addOnScrollChangedListener(this.scrollEvent);
        }
    }

    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        child.getViewTreeObserver().removeOnScrollChangedListener(this.scrollEvent);
        this.child = null;
    }

    int pxToDp(int px){
        float density = context.getResources().getDisplayMetrics().density;
        return  (int)(px / density);
    }

    void onScrollEvent(int x, int y, int width, int height){
        x = pxToDp(x);
        y = pxToDp(y);
        width = pxToDp(width);
        height = pxToDp(height);
        WritableMap nativeEvent = Arguments.createMap();
        WritableMap contentOffset = Arguments.createMap();
        contentOffset.putInt("x", x);
        contentOffset.putInt("y", y);
        nativeEvent.putMap("contentOffset", contentOffset);
        WritableMap contentSize = Arguments.createMap();
        contentSize.putInt("width", width);
        contentSize.putInt("height", height);
        nativeEvent.putMap("contentSize", contentSize);
        WritableMap data = Arguments.createMap();
        data.putMap("nativeEvent", nativeEvent);
        ReactOverScroll.this.context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("onScroll", data);
    }
}
