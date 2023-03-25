package com.xhw.drawerdemo;

import android.view.View;
import android.view.ViewGroup;

/*
 * User: xuhuawei
 * Date: 2022/7/12
 * Desc:
 */
public class ViewTopMarginWrapper {
    private View mTarget;
    public ViewTopMarginWrapper(View view) {
        mTarget = view;
    }

    public void setTopMargin(float height) {
        ViewGroup.MarginLayoutParams  lp= (ViewGroup.MarginLayoutParams) mTarget.getLayoutParams();
        lp.topMargin = (int) height;
        mTarget.setLayoutParams(lp);
//        mTarget.requestLayout();//必须调用，否则宽度改变但UI没有刷新
    }

    public int getTopMargin() {
        ViewGroup.MarginLayoutParams  lp= (ViewGroup.MarginLayoutParams) mTarget.getLayoutParams();
        int topMargin=lp.topMargin ;
        return topMargin;
    }
}
