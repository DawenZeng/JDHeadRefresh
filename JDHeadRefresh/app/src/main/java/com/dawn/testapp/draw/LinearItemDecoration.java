package com.dawn.testapp.draw;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * 适用于单列列表，item多列时不适用======================================================================
 * <p/>
 * 作者：Renj
 * <p/>
 * 创建时间：20017-04-10    22:06
 * <p/>
 * 描述：给 RecyclerView 绘制分割线<br/><br/>
 * 提示：在 support 包的版本是 25或以上 时，系统提供了一个默认绘制分割线的实现 DividerItemDecoration，但是该实现只针对 LinearLayoutManager 。<br/><br/>
 * <b>说明：</b><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * <b>该类主要兼容 LinearLayoutManager 和 GridLayoutManager 类型的水平和垂直方向的分割线，<br/>
 * 而对于 StaggeredGridLayoutManager，因为有多列时 item 的位置发生变化(按顺序是最后一列的和其他列交换了位置)导致绘制结果不对，需要改进。</b><br/>
 * <p/>
 * 修订历史：
 * <p/>
 * ======================================================================
 */
public class LinearItemDecoration extends RecyclerView.ItemDecoration {
    private static final int DEFAULT_DIVIDER_HEIGHT = 1;
    private static final int DEFAULT_DIVIDER_COLOR = 0xFFDDDDDD;
    private Paint mPaint;
    private int mHorizontalDividerHeight = DEFAULT_DIVIDER_HEIGHT; // 水平方向上的宽度
    private int mVerticalDividerHeight = DEFAULT_DIVIDER_HEIGHT;   // 垂直方向上的宽度
    private boolean mIsDrawLastLow = false; // 最后一行是否绘制分割线
    private boolean mIsDrawLoatCol = false; // 最后一列是否绘制分割线

    public LinearItemDecoration() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(DEFAULT_DIVIDER_COLOR);
    }

    /**
     * 指定分割线高度，单位 px ；默认 1px
     *
     * @param dividerHeight 水平方向和垂直方向上的宽度相同
     * @return
     */
    public LinearItemDecoration dividerHeight(int dividerHeight) {
        this.mHorizontalDividerHeight = dividerHeight;
        this.mVerticalDividerHeight = dividerHeight;
        return this;
    }

    /**
     * 指定分割线高度，单位 px ；默认 1px
     *
     * @param horizontalDividerHeight 水平方向上的宽度
     * @param verticalDividerHeight   垂直方向的宽度
     * @return
     */
    public LinearItemDecoration dividerHeight(int horizontalDividerHeight, int verticalDividerHeight) {
        this.mHorizontalDividerHeight = horizontalDividerHeight;
        this.mVerticalDividerHeight = verticalDividerHeight;
        return this;
    }

    /**
     * 设置最后一行和最后一列是否需要绘制分割线
     *
     * @param isDrawLastLow 是否绘制最后一行  默认 false
     * @param isDrawLoatCol 是否绘制最后一列  默认 false
     * @return
     */
    public LinearItemDecoration isDrawLastLowAndCol(boolean isDrawLastLow, boolean isDrawLoatCol) {
        this.mIsDrawLastLow = isDrawLastLow;
        this.mIsDrawLoatCol = isDrawLoatCol;
        return this;
    }

    /**
     * 指定分割线显色，默认 0xFFDDDDDD
     *
     * @param color
     * @return
     */
    public LinearItemDecoration dividerColor(int color) {
        mPaint.setColor(color);
        return this;
    }

    // 设置偏移量
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int viewLayoutPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        int childCount = parent.getAdapter().getItemCount();
        boolean lastRaw = isLastRaw(layoutManager, childCount, viewLayoutPosition);
        boolean lastCol = isLastCol(layoutManager, childCount, viewLayoutPosition);
        if (lastRaw) {
            if (lastCol) {
                // 最后一行最后一列，判断是否绘制右边底部
                if (mIsDrawLastLow && mIsDrawLoatCol) // 绘制右边和底部
                    outRect.set(0, 0, mVerticalDividerHeight, mHorizontalDividerHeight);
                else if (mIsDrawLastLow) // 绘制底部
                    outRect.set(0, 0, 0, mHorizontalDividerHeight);
                else if (mIsDrawLoatCol) // 绘制右边
                    outRect.set(0, 0, mVerticalDividerHeight, 0);
                else
                    outRect.set(0, 0, 0, 0);
            } else {
                // 最后一行但是不是最后一列，判断是否绘制底部
                if (mIsDrawLastLow) // 绘制底部
                    outRect.set(0, 0, mVerticalDividerHeight, mHorizontalDividerHeight);
                else // 不绘制底部
                    outRect.set(0, 0, mVerticalDividerHeight, 0);
            }
        } else if (lastCol) {
            if (lastRaw) {
                // 最后一列最后一行，判断是否绘制右边底部
                if (mIsDrawLastLow && mIsDrawLoatCol) // 绘制右边和底部
                    outRect.set(0, 0, mVerticalDividerHeight, mHorizontalDividerHeight);
                else if (mIsDrawLastLow) // 绘制底部
                    outRect.set(0, 0, 0, mHorizontalDividerHeight);
                else if (mIsDrawLoatCol) // 绘制右边
                    outRect.set(0, 0, mVerticalDividerHeight, 0);
                else
                    outRect.set(0, 0, 0, 0);
            } else {
                // 最后一列但是不是最后一行，判断是否绘制右边
                if (mIsDrawLoatCol) // 绘制右边
                    outRect.set(0, 0, mVerticalDividerHeight, mHorizontalDividerHeight);
                else // 不绘制右边
                    outRect.set(0, 0, 0, mHorizontalDividerHeight);
            }
        } else {
            // 不是最后一行也不是最后一列，绘制右边和底部
            outRect.set(0, 0, mVerticalDividerHeight, mHorizontalDividerHeight);
        }
    }

    /**
     * 判断是否最后一列
     *
     * @param layoutManager
     * @param childCount
     * @param itemPosition
     * @return
     */
    private boolean isLastCol(RecyclerView.LayoutManager layoutManager, int childCount, int itemPosition) {
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            int spanCount = gridLayoutManager.getSpanCount();
            int orientation = gridLayoutManager.getOrientation();
            if (GridLayoutManager.VERTICAL == orientation) {
                return (itemPosition + 1) % spanCount == 0;
            } else {
                int yu = childCount % spanCount;
                if (yu == 0) return itemPosition >= (childCount - spanCount);
                else return itemPosition >= (childCount - yu);
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            int spanCount = staggeredGridLayoutManager.getSpanCount();
            int orientation = staggeredGridLayoutManager.getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                return (itemPosition + 1) % spanCount == 0;
            } else {
                int yu = childCount % spanCount;
                if (yu == 0) return itemPosition >= (childCount - spanCount);
                else return itemPosition >= (childCount - yu);
            }
        } else {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int orientation = linearLayoutManager.getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                return true;
            } else {
                return (itemPosition + 1) == childCount;
            }
        }
    }

    /**
     * 判断是否最后一行
     *
     * @param layoutManager
     * @param childCount
     * @param itemPosition
     * @return
     */
    private boolean isLastRaw(RecyclerView.LayoutManager layoutManager, int childCount, int itemPosition) {
        // 注意：GridLayoutManager extends LinearLayoutManager 使用 instanceof 一定要先判断 GridLayoutManager
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            int orientation = gridLayoutManager.getOrientation();
            int spanCount = gridLayoutManager.getSpanCount();
            if (GridLayoutManager.VERTICAL == orientation) {
                int yu = childCount % spanCount;
                if (yu == 0) return itemPosition >= (childCount - spanCount);
                else return itemPosition >= (childCount - yu);
            } else {
                return (itemPosition + 1) % spanCount == 0;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            int orientation = staggeredGridLayoutManager.getOrientation();
            int spanCount = staggeredGridLayoutManager.getSpanCount();
            if (GridLayoutManager.VERTICAL == orientation) {
                int yu = childCount % spanCount;
                if (yu == 0) return itemPosition >= (childCount - spanCount);
                else return itemPosition >= (childCount - yu);
            } else {
                return (itemPosition + 1) % spanCount == 0;
            }
        } else {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int orientation = linearLayoutManager.getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                return (itemPosition + 1) == childCount;
            } else {
                return true;
            }
        }
    }

    // 在 item 绘制之前调用(就是绘制在 item 的底层) [和 onDrawOver() 方法二选一即可]
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int childCountTotal = parent.getAdapter().getItemCount();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = parent.getChildAt(i);
            int viewLayoutPosition = ((RecyclerView.LayoutParams) childAt.getLayoutParams()).getViewLayoutPosition();
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) childAt.getLayoutParams();

            /**
             * 在此处如果不在对最后一行或者最后一列进行判断的话，
             * 当在页面中给整个 RecyclerView 控件设置 padding 值时，依然会将最后一行的底部和最后一列的右边绘制出来
             */
            boolean lastRaw = isLastRaw(layoutManager, childCountTotal, viewLayoutPosition);
            boolean lastCol = isLastCol(layoutManager, childCountTotal, viewLayoutPosition);

            if (!lastRaw) {
                // 画水平方向的线
                int left = childAt.getLeft() - layoutParams.leftMargin;
                int right = childAt.getRight() + layoutParams.rightMargin;
                int top = childAt.getBottom() + layoutParams.bottomMargin;
                int bottom = top + mHorizontalDividerHeight;
                c.drawRect(left, top, right, bottom, mPaint);
            }

            if (!lastCol) {
                // 画竖直方向的线
                int left1 = childAt.getRight() + layoutParams.rightMargin;
                int right1 = left1 + mVerticalDividerHeight;
                int top1 = childAt.getTop() - layoutParams.topMargin;
                int bottom1 = childAt.getBottom() + layoutParams.bottomMargin;
                c.drawRect(left1, top1, right1, bottom1, mPaint);
            }

            if (!lastRaw && !lastCol) {
                // 画水平方向和竖直方向的线的交叉点的背景
                int left2 = childAt.getRight() + layoutParams.rightMargin;
                int right2 = left2 + mVerticalDividerHeight;
                int top2 = childAt.getBottom() + layoutParams.bottomMargin;
                int bottom2 = top2 + mHorizontalDividerHeight;
                c.drawRect(left2, top2, right2, bottom2, mPaint);
            }
        }
    }

    // 在 item 绘制之后调用(就是绘制在 item 的上层) [和 onDraw() 方法二选一即可]
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }
}
