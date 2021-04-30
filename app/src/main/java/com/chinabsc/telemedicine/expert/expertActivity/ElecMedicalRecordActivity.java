package com.chinabsc.telemedicine.expert.expertActivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;

public class ElecMedicalRecordActivity extends BaseActivity {
  WebView  medicalRecordWV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elec_medical_record);
        initWebView();
        getIntentMsg();

    }
    public void goBack(View v){
        onBackPressed();
    }
    private void initWebView(){
        medicalRecordWV=findViewById(R.id.medicalRecord);
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
    }
    private void getIntentMsg(){
       String content= getIntent().getStringExtra("medicalContent");
       initHtml(content);
    }
    private void initHtml(String content){
        String html = PublicUrl.getFromBase64(content);
        html = html.replaceAll("</head>","<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"> </head>");
        Log.i("test", html);
        medicalRecordWV.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);

    }
}
