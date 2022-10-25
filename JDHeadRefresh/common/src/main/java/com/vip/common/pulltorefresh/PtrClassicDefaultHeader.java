package com.vip.common.pulltorefresh;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.vip.common.R;
import com.vip.common.pulltorefresh.indicator.PtrIndicator;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PtrClassicDefaultHeader extends FrameLayout implements PtrUIHandler {

    private final static String KEY_SharedPreferences = "cube_ptr_classic_last_update";
    private int mRotateAniTime = 150;
    private RotateAnimation mFlipAnimation;
    private RotateAnimation mReverseFlipAnimation;
    private TextView mTitleTextView;
    private View mRotateView;
    private View mProgressBar;
    private long mLastUpdateTime = -1;
    private TextView mLastUpdateTextView;
    private String mLastUpdateTimeKey = "";
    private boolean mShouldShowLastUpdate;
    //显示时间的格式（yyyy-MM-dd HH:mm,yyyy-MM-dd HH:mm:ss）
    private String mDateFormat = "MM-dd HH:mm";//为空时，方法一显示“刚刚，多少天、小时、分钟之前”的格式
    private SimpleDateFormat sDataFormat = new SimpleDateFormat(mDateFormat);
    protected RefreshDistanceListener listener;//监听下拉刷新控件到屏幕顶部的距离

    //及时更新时间,每秒更新距上次刷新时间
//    private LastUpdateTimeUpdater mLastUpdateTimeUpdater = new LastUpdateTimeUpdater();

    public interface RefreshDistanceListener {//监听下拉刷新控件到屏幕顶部的距离的接口

        void onPositionChange(int currentPosY);
    }

    public PtrClassicDefaultHeader(Context context) {
        super(context);
        initViews(null);
    }

    public PtrClassicDefaultHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(attrs);
    }

    public PtrClassicDefaultHeader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews(attrs);
    }

    protected void initViews(AttributeSet attrs) {
        TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.PtrClassicHeader, 0, 0);
        if (arr != null) {
            mRotateAniTime = arr.getInt(R.styleable.PtrClassicHeader_ptr_rotate_ani_time, mRotateAniTime);
        }
        buildAnimation();
        View header = LayoutInflater.from(getContext()).inflate(R.layout.cube_ptr_classic_default_header, this);

        mRotateView = header.findViewById(R.id.ptr_classic_header_rotate_view);

        mTitleTextView = (TextView) header.findViewById(R.id.ptr_classic_header_rotate_view_header_title);
        mLastUpdateTextView = (TextView) header.findViewById(R.id.ptr_classic_header_rotate_view_header_last_update);
        mProgressBar = header.findViewById(R.id.ptr_classic_header_rotate_view_progressbar);

        resetView();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        if (mLastUpdateTimeUpdater != null) {
//            mLastUpdateTimeUpdater.stop();
//        }
    }

    public void setRotateAniTime(int time) {
        if (time == mRotateAniTime || time == 0) {
            return;
        }
        mRotateAniTime = time;
        buildAnimation();
    }

    /**
     * Specify the last update time by this key string
     *
     * @param key
     */
    public void setLastUpdateTimeKey(String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        mLastUpdateTimeKey = key;
    }

    /**
     * Using an object to specify the last update time.
     *
     * @param currActivityClass
     */
    public void setLastUpdateTimeRelateObject(Class currActivityClass) {
        setLastUpdateTimeKey(currActivityClass.getSimpleName());
    }

    private void buildAnimation() {
        mFlipAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mFlipAnimation.setInterpolator(new LinearInterpolator());
        mFlipAnimation.setDuration(mRotateAniTime);
        mFlipAnimation.setFillAfter(true);

        mReverseFlipAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setDuration(mRotateAniTime);
        mReverseFlipAnimation.setFillAfter(true);
    }

    private void resetView() {
        hideRotateView();
        mProgressBar.setVisibility(INVISIBLE);
    }

    private void hideRotateView() {
        mRotateView.clearAnimation();
        mRotateView.setVisibility(INVISIBLE);
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {
        resetView();
        mShouldShowLastUpdate = true;
        tryUpdateLastUpdateTime();
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {

        mShouldShowLastUpdate = true;
        tryUpdateLastUpdateTime();
//        mLastUpdateTimeUpdater.start();

        mProgressBar.setVisibility(INVISIBLE);

        mRotateView.setVisibility(VISIBLE);
        mTitleTextView.setVisibility(VISIBLE);
        if (frame.isPullToRefresh()) {
            mTitleTextView.setText(getResources().getString(R.string.cube_ptr_pull_down_to_refresh));
        } else {
            mTitleTextView.setText(getResources().getString(R.string.cube_ptr_pull_down));
        }
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        mShouldShowLastUpdate = false;
        hideRotateView();
        mProgressBar.setVisibility(VISIBLE);
        mTitleTextView.setVisibility(VISIBLE);
        mTitleTextView.setText(R.string.cube_ptr_refreshing);

//        tryUpdateLastUpdateTime();
//        mLastUpdateTimeUpdater.stop();
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {

        hideRotateView();
        mProgressBar.setVisibility(INVISIBLE);

        mTitleTextView.setVisibility(VISIBLE);
        mTitleTextView.setText(getResources().getString(R.string.cube_ptr_refresh_complete));

        // update last update time
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(KEY_SharedPreferences, 0);
        if (!TextUtils.isEmpty(mLastUpdateTimeKey)) {
//            mLastUpdateTime = new Date().getTime();
            mLastUpdateTime = System.currentTimeMillis();
            sharedPreferences.edit().putLong(mLastUpdateTimeKey, mLastUpdateTime).commit();
        }
    }

    private void tryUpdateLastUpdateTime() {
        if (TextUtils.isEmpty(mLastUpdateTimeKey) || !mShouldShowLastUpdate) {
            mLastUpdateTextView.setVisibility(GONE);
        } else {
            String time = getLastUpdateTime();
            if (TextUtils.isEmpty(time)) {
                mLastUpdateTextView.setVisibility(GONE);
            } else {
                mLastUpdateTextView.setVisibility(VISIBLE);
                mLastUpdateTextView.setText(time);
            }
        }
    }

    private final long MINUTE_PER = 60 * 1000;
    private final long HOUR_PER = 60 * MINUTE_PER;
    private final long DAY_PER = 24 * HOUR_PER;

    private String getLastUpdateTime() {
        mLastUpdateTime = getContext().getSharedPreferences(KEY_SharedPreferences, 0).getLong(mLastUpdateTimeKey, -1);
//        if (mLastUpdateTime == -1 && !TextUtils.isEmpty(mLastUpdateTimeKey)) {
//            mLastUpdateTime = getContext().getSharedPreferences(KEY_SharedPreferences, 0).getLong(mLastUpdateTimeKey, -1);
//        }
        if (mLastUpdateTime == -1) {
            return "";
        }

        //获取时间：方法一
        Date lastUpdateDate = new Date(mLastUpdateTime);
        long nowTime = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        sb.append(getContext().getString(R.string.rll_header_last_time));
        if (mDateFormat == null || mDateFormat.equals("")) {
            long disTime = nowTime - mLastUpdateTime;
            long days = disTime / DAY_PER;
            long hours = disTime / HOUR_PER;
            long minutes = disTime / MINUTE_PER;
            if (0 == days) {
                if (0 == hours) {
                    if (0 == minutes) {
                        sb.append(getContext().getString(R.string.rll_header_time_justnow));
                        return sb.toString();
                    } else {
                        sb.append(minutes);
                        sb.append(getContext().getString(R.string.rll_header_time_minutes));
                        return sb.toString();
                    }
                } else {
                    sb.append(hours);
                    sb.append(getContext().getString(R.string.rll_header_time_hours));
                    return sb.toString();
                }
            } else {
                sb.append(days);
                sb.append(getContext().getString(R.string.rll_header_time_days));
                return sb.toString();
            }
        } else {
            sb.append(sDataFormat.format(lastUpdateDate));
            return sb.toString();
        }


//        //获取时间：方法二
//        long diffTime = new Date().getTime() - mLastUpdateTime;
//        int seconds = (int) (diffTime / 1000);
//        if (diffTime < 0) {
//            return null;
//        }
//        if (seconds <= 0) {
//            return null;
//        }
//        StringBuilder sb = new StringBuilder();
//        sb.append(getContext().getString(R.string.rll_header_last_time));
//         //放开LastUpdateTimeUpdater相关代码的注释，每隔一秒就更新时间；下拉不放，可以看到多少秒以前刷新了，秒数实时增加
//        if (seconds < 60) {
//            sb.append(seconds + "秒之前");
//        } else {
//            int minutes = (seconds / 60);
//            if (minutes > 60) {
//                int hours = minutes / 60;
//                if (hours > 24) {
//                    Date date = new Date(mLastUpdateTime);
//                    sb.append(sDataFormat.format(date));
//                } else {
//                    sb.append(hours + "小时之前");
//                }
//
//            } else {
//                sb.append(minutes + "分钟之前");
//            }
//        }
//        return sb.toString();
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {

        final int mOffsetToRefresh = frame.getOffsetToRefresh();
        final int currentPos = ptrIndicator.getCurrentPosY();
        final int lastPos = ptrIndicator.getLastPosY();

        //监听下拉刷新控件到屏幕顶部的距离
        if (listener != null) {
            listener.onPositionChange(currentPos);
        }
        if (currentPos < mOffsetToRefresh && lastPos >= mOffsetToRefresh) {
            if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                crossRotateLineFromBottomUnderTouch(frame);
                if (mRotateView != null) {
                    mRotateView.clearAnimation();
                    mRotateView.startAnimation(mReverseFlipAnimation);
                }
            }
        } else if (currentPos > mOffsetToRefresh && lastPos <= mOffsetToRefresh) {
            if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                crossRotateLineFromTopUnderTouch(frame);
                if (mRotateView != null) {
                    mRotateView.clearAnimation();
                    mRotateView.startAnimation(mFlipAnimation);
                }
            }
        }
    }

    private void crossRotateLineFromTopUnderTouch(PtrFrameLayout frame) {
        if (!frame.isPullToRefresh()) {
            mTitleTextView.setVisibility(VISIBLE);
            mTitleTextView.setText(R.string.cube_ptr_release_to_refresh);
        }
    }

    private void crossRotateLineFromBottomUnderTouch(PtrFrameLayout frame) {
        mTitleTextView.setVisibility(VISIBLE);
        if (frame.isPullToRefresh()) {
            mTitleTextView.setText(getResources().getString(R.string.cube_ptr_pull_down_to_refresh));
        } else {
            mTitleTextView.setText(getResources().getString(R.string.cube_ptr_pull_down));
        }
    }

//    private class LastUpdateTimeUpdater implements Runnable {
//
//        private boolean mRunning = false;
//
//        private void start() {
//            if (TextUtils.isEmpty(mLastUpdateTimeKey)) {
//                return;
//            }
//            mRunning = true;
//            run();//每秒刷新
//        }
//
//        private void stop() {
//            mRunning = false;
//            removeCallbacks(this);
//        }
//
//        @Override
//        public void run() {
//            tryUpdateLastUpdateTime();
//            if (mRunning) {
//                postDelayed(this, 1000);
//            }
//        }
//    }
}
