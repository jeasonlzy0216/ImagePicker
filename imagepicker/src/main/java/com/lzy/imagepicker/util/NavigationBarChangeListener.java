package com.lzy.imagepicker.util;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by z-chu on 2017/9/4
 * 用于监听导航栏的显示和隐藏，主要用于适配华为EMUI系统上虚拟导航栏可随时收起和展开的情况
 */
public class NavigationBarChangeListener implements ViewTreeObserver.OnGlobalLayoutListener {

    private static final int ORIENTATION_VERTICAL = 1;           //监听竖屏模式导航栏的显示和隐藏
    public static final int ORIENTATION_HORIZONTAL = 2;         //监听横屏模式导航栏的显示和隐藏

    private Rect rect;
    private View rootView;
    private boolean isShowNavigationBar = false;
    private int orientation;
    private OnSoftInputStateChangeListener listener;

    private NavigationBarChangeListener(View rootView, int orientation) {
        this.rootView = rootView;
        this.orientation = orientation;
        rect = new Rect();
    }

    @Override
    public void onGlobalLayout() {
        rect.setEmpty();
        rootView.getWindowVisibleDisplayFrame(rect);
        int heightDiff = 0;
        if (orientation == ORIENTATION_VERTICAL) {
            heightDiff = rootView.getHeight() - (rect.bottom - rect.top);
        } else if (orientation == ORIENTATION_HORIZONTAL) {
            heightDiff = rootView.getWidth() - (rect.right - rect.left);
        }
        int navigationBarHeight = Utils.hasVirtualNavigationBar(rootView.getContext()) ? Utils.getNavigationBarHeight(rootView.getContext()) : 0;
        if (heightDiff >= navigationBarHeight && heightDiff < navigationBarHeight * 2) {
            if (!isShowNavigationBar && listener != null) {
                listener.onNavigationBarShow(orientation, heightDiff);
            }
            isShowNavigationBar = true;
        } else {
            if (isShowNavigationBar && listener != null) {
                listener.onNavigationBarHide(orientation);
            }
            isShowNavigationBar = false;
        }
    }

    public void setListener(OnSoftInputStateChangeListener listener) {
        this.listener = listener;
    }

    public interface OnSoftInputStateChangeListener {
        void onNavigationBarShow(int orientation, int height);

        void onNavigationBarHide(int orientation);
    }

    public static NavigationBarChangeListener with(View rootView) {
        return with(rootView, ORIENTATION_VERTICAL);
    }

    public static NavigationBarChangeListener with(Activity activity) {
        return with(activity.findViewById(android.R.id.content), ORIENTATION_VERTICAL);
    }

    public static NavigationBarChangeListener with(View rootView, int orientation) {
        NavigationBarChangeListener changeListener = new NavigationBarChangeListener(rootView, orientation);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(changeListener);
        return changeListener;
    }

    public static NavigationBarChangeListener with(Activity activity, int orientation) {
        return with(activity.findViewById(android.R.id.content), orientation);
    }
}
