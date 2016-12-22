package cn.ittiger.loading.demo;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ittiger.loading.CountDownView;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class CountDownViewActivity extends AppCompatActivity {

    @BindView(R.id.countDownView1)
    CountDownView mCountDownView1;
    @BindView(R.id.countDownView2)
    CountDownView mCountDownView2;
    @BindView(R.id.btn_start)
    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_down_view);
        ButterKnife.bind(this);

        mCountDownView1.setCountDownViewListener(MyCountDownViewListener);
        mCountDownView2.setCountDownViewListener(MyCountDownViewListener);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCountDownView1.start();
                mCountDownView2.start();
            }
        });
    }

    CountDownView.CountDownViewListener MyCountDownViewListener = new CountDownView.CountDownViewListener() {

        @Override
        public void onStart() {

            //倒计时开始的实现
            Toast.makeText(CountDownViewActivity.this, "CountDown Start", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFinish() {

            //倒计时结束的实现
            Toast.makeText(CountDownViewActivity.this, "CountDown Finish", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSkipClick() {

            //点击倒计时View的实现
            Toast.makeText(CountDownViewActivity.this, "跳过", Toast.LENGTH_SHORT).show();
        }
    };
}
