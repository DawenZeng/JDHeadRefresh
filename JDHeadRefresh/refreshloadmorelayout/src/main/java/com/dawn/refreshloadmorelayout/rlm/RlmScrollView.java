package com.dawn.refreshloadmorelayout.rlm;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.qbw.log.XLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 当刷新和加载里面除ListView或GridView还有其他布局，可以上拉自动加载
 * *
 * ScrollView并没有实现滚动监听，所以我们必须自行实现对ScrollView的监听，
 * 我们很自然的想到在onTouchEvent()方法中实现对滚动Y轴进行监听
 * ScrollView的滚动Y值进行监听
 */
public class RlmScrollView extends ScrollView {

    private OnScrollListener onScrollListener;
    /**
     * 主要是用在用户手指离开RlmScrollView，RlmScrollView还在继续滑动，我们用来保存Y的距离，然后做比较
     */
    private int lastScrollY;

    private List<OnScrollListener> mOnScrollListeners = new ArrayList<>();

    public RlmScrollView(Context context) {
        super(context);
    }

    public RlmScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RlmScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (XLog.isEnabled()) XLog.v("l=%d, t=%d, oldl=%d, oldt=%d", l, t, oldl, oldt);
        notifiOnScrollListener(l, t, oldl, oldt);
    }

    public boolean addOnScrollListener(OnScrollListener onScrollListener) {
        return mOnScrollListeners.add(onScrollListener);
    }

    public boolean removeOnScrollListener(OnScrollListener onScrollListener) {
        return mOnScrollListeners.remove(onScrollListener);
    }

    private void notifiOnScrollListener(int l, int t, int oldl, int oldt) {
        for (OnScrollListener scrollListener : mOnScrollListeners) {
            scrollListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }

    /**
     * 还回0 --滚动条滚动到顶部
     * 解决ScrollView中镶嵌EditViewiew和GridView、ListView等刷新时EditViewiew不展示的问题
     */
    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        return 0;
    }

    public interface OnScrollListener {
        void onScrollChanged(ScrollView scrollView, int l, int t, int oldl, int oldt);

        /**
         * 回调方法， 返回MyScrollView滑动的Y方向距离
         */
        void onScroll(int scrollY);
    }

    /**
     * 设置滚动接口；设置监听顶部悬浮
     *
     * @param onScrollListener
     */
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    /**
     * 用于用户手指离开MyScrollView的时候获取MyScrollView滚动的Y距离，然后回调给onScroll方法中
     */
    private Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            int scrollY = RlmScrollView.this.getScrollY();

            //此时的距离和记录下的距离不相等，在隔5毫秒给handler发送消息
            if (lastScrollY != scrollY) {
                lastScrollY = scrollY;
                handler.sendMessageDelayed(handler.obtainMessage(), 5);
            }
            if (onScrollListener != null) {
                onScrollListener.onScroll(scrollY);
            }

        }

        ;

    };

    /**
     * 重写onTouchEvent， 当用户的手在MyScrollView上面的时候，
     * 直接将MyScrollView滑动的Y方向距离回调给onScroll方法中，当用户抬起手的时候，
     * MyScrollView可能还在滑动，所以当用户抬起手我们隔5毫秒给handler发送消息，在handler处理
     * MyScrollView滑动的距离
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (onScrollListener != null) {
            onScrollListener.onScroll(lastScrollY = this.getScrollY());
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                handler.sendMessageDelayed(handler.obtainMessage(), 20);
                break;
        }
        return super.onTouchEvent(ev);
    }

    private float xLast, yLast, xDistance, yDistance;

    //解决左滑大于上下滑动时，阻止上下滑动
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();
                xDistance = Math.abs(curX - xLast);
                yDistance = Math.abs(curY - yLast);
                if (xDistance > yDistance) {//解决左滑大于上下滑动时，阻止上下滑动
                    return false;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }

//    /**
//     * 滚动的回调接口
//     */
//    public interface OnScrollListener{
//        /**
//         * 回调方法， 返回MyScrollView滑动的Y方向距离
//         */
//        public void onScroll(int scrollY);
//    }
}
