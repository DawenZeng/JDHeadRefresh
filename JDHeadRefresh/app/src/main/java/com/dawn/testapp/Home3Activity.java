package com.dawn.testapp;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.dawn.testapp.draw.LinearItemDecoration;
import com.vip.common.pulltorefresh.PtrClassicDefaultHeader;
import com.vip.common.pulltorefresh.PtrClassicFrameLayout;
import com.vip.common.pulltorefresh.PtrFrameLayout;
import com.vip.common.pulltorefresh.PtrHandler;

import java.util.ArrayList;
import java.util.List;

public class Home3Activity extends AppCompatActivity implements PtrClassicDefaultHeader.RefreshDistanceListener {

    /**
     * 改变titlebar中icon颜色时的距离
     */
    private static int DISTANCE_WHEN_TO_SELECTED = 40;
    private PtrClassicFrameLayout mPtrFrame;
    private RecyclerView recyclerView;
    private LinearLayout scanningLayout;
    private LinearLayout advisoryLayout;
    private FrameLayout homeTitleBarLayout;
    private View homeTitleBarBgView;
    private View rootView = null;
    private int distanceY;
    private List<String> datas = new ArrayList<>();
    private List<String> addList = new ArrayList<>();
    private MyRecyclerViewAdapter adapter;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home3);
        initBase();
    }

    /**
     * 初始化下拉刷新及滚动距离title发生的改变
     */
    private void initBase() {
        recyclerView = findViewById(R.id.recyclerView);
        scanningLayout = findViewById(R.id.scanning_layout);
        advisoryLayout = findViewById(R.id.advisory_layout);
        homeTitleBarLayout = findViewById(R.id.home_title_bar_layout);
        homeTitleBarBgView = findViewById(R.id.home_title_bar_bg_view);

        for (int i = 1; i <= 20; i++) {
            datas.add("item" + i);
        }
        initPtrFrame();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);//默认为 LinearLayoutManager.VERTICAL
        recyclerView.setLayoutManager(layoutManager);
        // 增加分割线
        recyclerView.addItemDecoration(new LinearItemDecoration().dividerHeight((int) getResources().getDimension(R.dimen.line_height))
                .dividerColor(getResources().getColor(R.color.black_font)));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                distanceY += dy;
                if (distanceY > ScreenUtil.dip2px(getApplicationContext(), 20)) {
                    homeTitleBarBgView.setBackgroundColor(getResources().getColor(R.color.white));
                    if (Build.VERSION.SDK_INT > 10) {
                        homeTitleBarBgView.setAlpha(distanceY * 1.0f / ScreenUtil.dip2px(getApplicationContext(), 100));
                    } else {
                        DISTANCE_WHEN_TO_SELECTED = 20;
                    }
                } else {
                    homeTitleBarBgView.setBackgroundColor(0);
                }

                if (distanceY > ScreenUtil.dip2px(getApplicationContext(), DISTANCE_WHEN_TO_SELECTED) && !scanningLayout.isSelected()) {
                    scanningLayout.setSelected(true);
                    advisoryLayout.setSelected(true);
                } else if (distanceY <= ScreenUtil.dip2px(getApplicationContext(), DISTANCE_WHEN_TO_SELECTED) && scanningLayout.isSelected()) {
                    scanningLayout.setSelected(false);
                    advisoryLayout.setSelected(false);
                }
            }
        });
        adapter = new MyRecyclerViewAdapter(datas);
        recyclerView.setAdapter(adapter);
    }


    /**
     * 初始化下拉刷新
     */
    private void initPtrFrame() {
        mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.rotate_header_list_view_frame);
        mPtrFrame.setLastUpdateTimeRelateObject(Home3Activity.class);
        mPtrFrame.setOnRefreshDistanceListener(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                updateData();
            }
        });

        // 是否进入页面就开始显示刷新动作
        /*mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrame.autoRefresh();
            }
        }, 100);*/
    }

    /**
     * 下拉后刷新数据
     */
    private void updateData() {
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrame.refreshComplete();
            }
        }, 1000);
    }

    @Override
    public void onPositionChange(int currentPosY) {
        if (currentPosY > 0) {
            if (homeTitleBarLayout.getVisibility() == View.VISIBLE) {
                homeTitleBarLayout.setVisibility(View.GONE);
            }
        } else {
            if (homeTitleBarLayout.getVisibility() == View.GONE) {
                homeTitleBarLayout.setVisibility(View.VISIBLE);
                distanceY = 0;
            }
        }
    }
}
