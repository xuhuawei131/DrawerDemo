package com.xhw.drawerdemo;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;


/*
 * User: xuhuawei
 * Date: 2023/3/25
 * Desc:
 */
public class DrawerRootLayout extends RelativeLayout {
    private static final String TAG_TOP = "drawerTop";
    private static final String TAG_CONTAINER = "drawerContainer";
    private static final String TAG_MIDDLE = "drawerMiddle";
    private static final String TAG_BOTTOM = "drawerBottom";

    private View topView;
    private View containerView;
    private View middleView;
    private View bottomView;
    private int hidenDistance;

    private int orgPointX;
    private int orgPointY;

    private int miniTouchSlop;
    private boolean isShow = false;

    public DrawerRootLayout(Context context) {
        super(context);
        init();
    }

    public DrawerRootLayout(Context context,  AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawerRootLayout(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        miniTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        setClickable(true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        topView = findViewWithTag(TAG_TOP);
        containerView = findViewWithTag(TAG_CONTAINER);
        middleView = findViewWithTag(TAG_MIDDLE);
        bottomView = findViewWithTag(TAG_BOTTOM);

        if (topView == null || middleView == null || bottomView == null) {
//            if (BuildConfig.DEBUG) {
                throw new RuntimeException("没有设置 drawerTop、drawerMiddle、drawerBottom对应的tag");
//            }
        }

        post(new Runnable() {
            @Override
            public void run() {
                int topMeasuredHeight = topView.getMeasuredHeight();
                int hideHeight = middleView.getMeasuredHeight();
                hidenDistance = hideHeight - topMeasuredHeight;

                MarginLayoutParams layoutParams =
                        (MarginLayoutParams) middleView.getLayoutParams();
                layoutParams.topMargin = -hidenDistance;
                middleView.setLayoutParams(layoutParams);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        Log.e("xhw", "dispatchTouchEvent action=" + action);
        if (action == MotionEvent.ACTION_DOWN) {
            orgPointX = (int) ev.getX();
            orgPointY = (int) ev.getY();
        } else if (action == MotionEvent.ACTION_UP) {
            int curPointX = (int) ev.getX();
            int curPointY = (int) ev.getY();

            int distanceX = Math.abs(curPointX - orgPointX);
            int distanceY = curPointY - orgPointY;
            int absDistanceY = Math.abs(distanceY);
            if (absDistanceY > distanceX && absDistanceY > miniTouchSlop) {
                if (distanceY > 0) {
                    showAnim();
                } else {
                    hideAnim();
                }
            }
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    private void hideAnim() {
        if (isShow) {
            ViewTopMarginWrapper viewHeightWrapper = new ViewTopMarginWrapper(containerView);
            PropertyValuesHolder holder1 = PropertyValuesHolder.ofFloat("topMargin", topView.getMeasuredHeight(),
                    -hidenDistance);
            ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(viewHeightWrapper, holder1);
            animator.start();
            isShow = false;
        }
    }

    private void showAnim() {
        if (!isShow) {
            isShow = true;
            ViewTopMarginWrapper viewHeightWrapper = new ViewTopMarginWrapper(containerView);
            PropertyValuesHolder holder1 = PropertyValuesHolder.ofFloat("topMargin", -hidenDistance,
                    topView.getMeasuredHeight());
            ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(viewHeightWrapper, holder1);
            animator.start();
        }
    }

}
