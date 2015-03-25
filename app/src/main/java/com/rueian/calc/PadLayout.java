package com.rueian.calc;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * source: https://github.com/android/platform_packages_apps_calculator/blob/master/src/com/android/calculator2/CalculatorPadLayout.java
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * A layout that places children in an evenly distributed grid based on the specified
 *  {@link android.R.attr#columnCount} and {@link android.R.attr#rowCount} attributes.
 *  透過自訂的 columnCount, rowCount 兩個屬性來為子元素平均分配 grid 空間的 Layout
 */
public class PadLayout extends ViewGroup {
    private int mRowCount;
    private int mColumnCount;

    public PadLayout(Context context) {
        this(context, null);
    }

    public PadLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PadLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // 透過 TypedArray 取得在 XML 上面定義的 columnCount, rowCount 屬性
        final TypedArray a = context.obtainStyledAttributes(attrs,
                new int[] { android.R.attr.rowCount, android.R.attr.columnCount }, defStyle, 0);
        mRowCount = a.getInt(0, 1);
        mColumnCount = a.getInt(1, 1);

        a.recycle();
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        // 取得 layout 的 padding 參數，稍後用於計算 column 寬度 與 row 高度
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();

        // http://developer.android.com/reference/android/view/View.html
        // LAYOUT_DIRECTION_RTL -> Horizontal layout direction of this view is from Right to Left.
        final boolean isRTL = getLayoutDirection() == LAYOUT_DIRECTION_RTL;

        // 計算 column 寬度 與 row 高度
        final int columnWidth =
                Math.round((float) (right - left - paddingLeft - paddingRight)) / mColumnCount;
        final int rowHeight =
                Math.round((float) (bottom - top - paddingTop - paddingBottom)) / mRowCount;

        // 開始為每個子元素設定位置
        int rowIndex = 0, columnIndex = 0;
        for (int childIndex = 0; childIndex < getChildCount(); ++childIndex) {
            final View childView = getChildAt(childIndex);
            if (childView.getVisibility() == View.GONE) {
                continue;
            }

            final MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();

            final int childTop = paddingTop + lp.topMargin + rowIndex * rowHeight;
            final int childBottom = childTop - lp.topMargin - lp.bottomMargin + rowHeight;
            final int childLeft = paddingLeft + lp.leftMargin +
                    (isRTL ? (mColumnCount - 1) - columnIndex : columnIndex) * columnWidth;
            final int childRight = childLeft - lp.leftMargin - lp.rightMargin + columnWidth;

            final int childWidth = childRight - childLeft;
            final int childHeight = childBottom - childTop;

            // MeasureSpec.EXACTLY 設定子元素該有的寬與高
            // 詳細過程範例參考 http://blog.csdn.net/luoshengyang/article/details/8372924
            if (childWidth != childView.getMeasuredWidth() ||
                    childHeight != childView.getMeasuredHeight()) {
                childView.measure(
                        MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY));
            }

            // 將計算好的參數設定到子元素上
            childView.layout(childLeft, childTop, childRight, childBottom);

            rowIndex = (rowIndex + (columnIndex + 1) / mColumnCount) % mRowCount;
            columnIndex = (columnIndex + 1) % mColumnCount;
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof MarginLayoutParams;
    }
}
