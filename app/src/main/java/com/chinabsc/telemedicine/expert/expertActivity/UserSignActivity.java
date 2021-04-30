package com.chinabsc.telemedicine.expert.expertActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinabsc.telemedicine.expert.R;

import org.xutils.http.RequestParams;

public class UserSignActivity extends BaseActivity {

    private ImageView mBackImageView;
    /**  */
    private TextView mTitle;
    private ImageView mMySign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign);
        initView();
    }

    private void initView() {
        mBackImageView = (ImageView) findViewById(R.id.BackImageView);
        mTitle = (TextView) findViewById(R.id.title);
        mMySign = (ImageView) findViewById(R.id.mySign);
        mBackImageView.setOnClickListener(new MonClickListner());
        mMySign.setOnClickListener(new MonClickListner());
    }

    class MonClickListner implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.BackImageView:
                    onBackPressed();
                    break;
                case R.id.mySign:
                    break;
            }
        }
    }
    private void getSign(){

    }
}
