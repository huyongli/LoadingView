package cn.ittiger.loading.demo;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ittiger.loading.CircleProgress;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class CircleProgressActivity extends AppCompatActivity {

    @BindView(R.id.circleProgress)
    CircleProgress mCircleProgress;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_progress);
        ButterKnife.bind(this);

    }

    public void onClick(View view) {

        updateProgress();
    }

    public void updateProgress() {

        if(mCircleProgress.getProgress() >= 100) {
            return;
        }
        mCircleProgress.setProgress(mCircleProgress.getProgress() + 1);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
               updateProgress();
            }
        }, 50);
    }
}
