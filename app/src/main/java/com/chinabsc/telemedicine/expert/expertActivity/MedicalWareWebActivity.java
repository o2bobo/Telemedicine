package com.chinabsc.telemedicine.expert.expertActivity;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.chinabsc.telemedicine.expert.R;

public class MedicalWareWebActivity extends BaseActivity {
    private String TAG="MedicalWareWebActivity";
    WebView medicalRecordWV;
    TextView TitleTextViewTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elec_medical_record);
        TitleTextViewTv=findViewById(R.id.TitleTextView);

        initWebView();
        getIntentMsg();

    }

    private void getIntentMsg() {
        String url=getIntent().getStringExtra("url");
        Log.i(TAG,"url=="+url+"=====");
        medicalRecordWV.loadUrl(url);
        String type=getIntent().getStringExtra("type");
        TitleTextViewTv.setText(type);
    }

    public void goBack(View v) {
        onBackPressed();
    }

    private void initWebView() {
        medicalRecordWV = findViewById(R.id.medicalRecord);
        //支持JS
        medicalRecordWV.getSettings().setJavaScriptEnabled(true);
        // 设置可以支持缩放
        medicalRecordWV.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        medicalRecordWV.getSettings().setBuiltInZoomControls(true);
        //扩大比例的缩放
        medicalRecordWV.getSettings().setUseWideViewPort(true);
        //自适应屏幕
        medicalRecordWV.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        medicalRecordWV.getSettings().setLoadWithOverviewMode(true);
        //如果不设置WebViewClient，请求会跳转系统浏览器
        medicalRecordWV.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //该方法在Build.VERSION_CODES.LOLLIPOP以前有效，从Build.VERSION_CODES.LOLLIPOP起，建议使用shouldOverrideUrlLoading(WebView, WebResourceRequest)} instead
                //返回false，意味着请求过程里，不管有多少次的跳转请求（即新的请求地址），均交给webView自己处理，这也是此方法的默认处理
                //返回true，说明你自己想根据url，做新的跳转，比如在判断url符合条件的情况下，我想让webView加载http://ask.csdn.net/questions/178242

             /*   if (url.toString().contains(".")) {
                    view.loadUrl("http://ask.csdn.net/questions/178242");
                    return true;
                }

                return false;*/
             view.loadUrl(url);
             return true;
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                //返回false，意味着请求过程里，不管有多少次的跳转请求（即新的请求地址），均交给webView自己处理，这也是此方法的默认处理
                //返回true，说明你自己想根据url，做新的跳转，比如在判断url符合条件的情况下，我想让webView加载http://ask.csdn.net/questions/178242
               /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (request.getUrl().toString().contains(".")) {
                        view.loadUrl("http://ask.csdn.net/questions/178242");
                        return true;
                    }
                }

                return false;*/
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });


    }
}
