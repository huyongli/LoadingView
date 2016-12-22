package cn.ittiger.loading.demo;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ittiger.loading.demo.view.CommonRecyclerView;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CommonRecyclerView.OnItemClickListener {

    @BindView(R.id.recyclerView)
    CommonRecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(5));
        mRecyclerView.setOnItemClickListener(this);
        mRecyclerView.setAdapter(new StringListAdapter(this, initData()));
    }

    List<String> initData() {

        List<String> list = new ArrayList<>();
        list.add("CountDownView(广告倒计时View)");
        list.add("LoadingView(加载等待View)");
        list.add("CircleProgress(圆环型进度百分比)");
        return list;
    }

    @Override
    public void onItemClick(int position, View itemView) {

        switch (position) {
            case 0:
                start(CountDownViewActivity.class);
                break;
            case 1:
                start(LoadingViewActivity.class);
                break;
            case 2:
                start(CircleProgressActivity.class);
                break;
        }
    }

    void start(Class claxx) {

        startActivity(new Intent(this, claxx));
    }
}
