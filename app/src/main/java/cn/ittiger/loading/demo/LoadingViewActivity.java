package cn.ittiger.loading.demo;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ittiger.loading.LoadingView;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class LoadingViewActivity extends AppCompatActivity {

    @BindView(R.id.loadingView)
    LoadingView mLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_view);
        ButterKnife.bind(this);

    }

    public void onClick(View view) {

        int id = view.getId();
        if(id == R.id.btnStart) {
            mLoadingView.start();
        }
        if(id == R.id.btnStop) {
            mLoadingView.stop();
        }
    }
}
