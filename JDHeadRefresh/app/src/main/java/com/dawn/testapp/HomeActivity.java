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

import com.dawn.refreshloadmorelayout.RefreshLoadMoreLayout;
import com.dawn.testapp.draw.LinearItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements RefreshLoadMoreLayout.CallBack {

    /**
     * 改变titlebar中icon颜色时的距离
     */
    private static int DISTANCE_WHEN_TO_SELECTED = 40;
    private RefreshLoadMoreLayout refreshloadmore;
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
        setContentView(R.layout.activity_home);


        refreshloadmore = findViewById(R.id.refreshLoadMore);
        recyclerView = findViewById(R.id.recyclerView);
        scanningLayout = findViewById(R.id.scanning_layout);
        advisoryLayout = findViewById(R.id.advisory_layout);
        homeTitleBarLayout = findViewById(R.id.home_title_bar_layout);
        homeTitleBarBgView = findViewById(R.id.home_title_bar_bg_view);

        refreshloadmore.init(new RefreshLoadMoreLayout.Config(this).showLastRefreshTime(
                HomeActivity.class, "MM-dd HH:mm").autoLoadMore().multiTask());
//        //一进入界面就加载
//        refreshloadmore.startAutoRefresh();
        for (int i = 1; i <= 20; i++) {
            datas.add("item" + i);
        }
        setRecyclerView();
    }

    private void setRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);//默认为 LinearLayoutManager.VERTICAL
        //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);//垂直列表
        //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);//水平列表
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MyRecyclerViewAdapter(datas);
        recyclerView.setAdapter(adapter);
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
    }

    @Override
    public void onRefresh() {
        adapter.updata(datas);
        homeTitleBarBgView.setVisibility(View.GONE);
        refreshloadmore.stopRefresh(2000);//延迟15秒结束下拉刷新
    }

    @Override
    public void onLoadMore() {
        addList.clear();
        mHandler.postDelayed(new Runnable() {//延迟加载
            @Override
            public void run() {
                for (int i = 1; i <= 5; i++) {
                    addList.add("bbbbbbb" + i);
                }
                adapter.addDatas(addList);
                refreshloadmore.stopLoadMore();
            }
        }, 1000);
    }
}
