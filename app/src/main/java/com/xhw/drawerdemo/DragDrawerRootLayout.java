package com.xhw.drawerdemo;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/*
 * User: xuhuawei
 * Date: 2023/4/1
 * Desc:
 */
public class DragDrawerRootLayout extends RelativeLayout implements Runnable{
    private View topView;
    private View containerView;
    private View middleView;
    private View bottomView;

    private int hidenDistance;
    private int maxScrollDistance = 0;
    private int orgPointX;
    private int orgPointY;

    private int miniTouchSlop;

    private static final String TAG_TOP = "drawerTop";
    private static final String TAG_CONTAINER = "drawerContainer";
    private static final String TAG_MIDDLE = "drawerMiddle";
    private static final String TAG_BOTTOM = "drawerBottom";

    private int initMargin=0;

    public DragDrawerRootLayout(Context context) {
        super(context);
        init();
    }

    public DragDrawerRootLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragDrawerRootLayout(Context context, AttributeSet attrs, int defStyleAttr) {
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
            if (BuildConfig.DEBUG) {
                throw new RuntimeException("没有设置 drawerTop、drawerMiddle、drawerBottom对应的tag");
            }
        }

        post(this);
    }

    @Override
    public void run() {
        int topMeasuredHeight = topView.getMeasuredHeight();
        int hideHeight = middleView.getMeasuredHeight();
        hidenDistance = hideHeight - topMeasuredHeight;

        maxScrollDistance = hideHeight;

        ViewGroup.MarginLayoutParams layoutParams =
                (ViewGroup.MarginLayoutParams) containerView.getLayoutParams();
        layoutParams.topMargin = -hidenDistance;
        containerView.setLayoutParams(layoutParams);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        int curPointX = (int) ev.getX();
        int curPointY = (int) ev.getY();

        if (action == MotionEvent.ACTION_DOWN) {
            orgPointX = curPointX;
            orgPointY = curPointY;
            initMargin = getTopMargin();
        } else if (action == MotionEvent.ACTION_UP) {
            int distanceX = Math.abs(curPointX - orgPointX);
            int distanceY = curPointY - orgPointY;
            int absDistanceY = Math.abs(distanceY);
            if (absDistanceY > distanceX && absDistanceY > miniTouchSlop) {
                if (distanceY > 0) {
                    // 向下滑动展示
                    if (absDistanceY > maxScrollDistance / 2) {
                        showAnim();
                    } else {
                        hideAnim();
                    }
                } else {
                    // 向上滑动隐藏
                    if (absDistanceY > maxScrollDistance / 2) {
                        hideAnim();
                    } else {
                        showAnim();
                    }
                }
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            int distanceY = curPointY - orgPointY;
            int distanceX = Math.abs(curPointX - orgPointX);
            int absDistanceY = Math.abs(distanceY);
            // 判断上下滑动条件
            if (absDistanceY > distanceX && absDistanceY > miniTouchSlop && absDistanceY < maxScrollDistance) {
                setTopMargin(initMargin+distanceY);
            }
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }


    public void setTopMargin(float height) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) containerView.getLayoutParams();
        lp.topMargin = (int) height;
        containerView.setLayoutParams(lp);
    }

    public int getTopMargin() {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) containerView.getLayoutParams();
        return lp.topMargin;
    }

    private void hideAnim() {
        ViewTopMarginWrapper viewHeightWrapper = new ViewTopMarginWrapper(containerView);
        PropertyValuesHolder holder1 = PropertyValuesHolder.ofFloat("topMargin", getTopMargin(),
                -hidenDistance);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(viewHeightWrapper, holder1);
        animator.start();
    }

    private void showAnim() {
        ViewTopMarginWrapper viewHeightWrapper = new ViewTopMarginWrapper(containerView);
        PropertyValuesHolder holder1 = PropertyValuesHolder.ofFloat("topMargin", getTopMargin(),
                topView.getMeasuredHeight());
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(viewHeightWrapper, holder1);
        animator.start();
    }


}
