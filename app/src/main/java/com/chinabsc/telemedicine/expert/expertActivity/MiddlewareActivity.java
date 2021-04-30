package com.chinabsc.telemedicine.expert.expertActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.entity.MiddleAddressEntity;
import com.umeng.message.common.Const;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.x;

@ContentView(R.layout.activity_middleware)
public class MiddlewareActivity extends BaseActivity {
    String middleAddress;
    String hisID;

    @Event(value = {
            R.id.EmrNumber,
            R.id.EltNumber,
            R.id.EsoNumber,
            R.id.EtoNumber,
            R.id.BackImageView,
            R.id.BizcloudNumber,
            R.id.EoNumber,
            R.id.AddConsultationButton}, type = View.OnClickListener.class)
    private void onClick(View v) {
        Intent it;
        switch (v.getId()) {
            //病历记录
            case R.id.EmrNumber:
                it = new Intent(MiddlewareActivity.this, MiddlewareEmrActivity.class);
                it.putExtra(MiddlewareEsoOrEtoActivity.TELEMEDICINE_INFO_ID, hisID);
                it.putExtra("address", middleAddress);
                startActivity(it);
                break;
            //影像检查
            case R.id.BizcloudNumber:
                it = new Intent(MiddlewareActivity.this, MiddlewareMedicalImgActivity.class);
                it.putExtra(MiddlewareEsoOrEtoActivity.TELEMEDICINE_INFO_ID, hisID);
                it.putExtra("address", middleAddress);
                startActivity(it);
                break;
            //检验记录
            case R.id.EltNumber:
                it = new Intent(MiddlewareActivity.this, MiddlewareEltActivity.class);
                it.putExtra(MiddlewareEltActivity.TELEMEDICINE_INFO_ID, hisID);
                it.putExtra("address", middleAddress);
                startActivity(it);
                break;
            //长期医嘱
            case R.id.EsoNumber:
                it = new Intent(MiddlewareActivity.this, MiddlewareEsoOrEtoActivity.class);
                it.putExtra(MiddlewareEsoOrEtoActivity.TELEMEDICINE_INFO_ID, hisID);
                it.putExtra(MiddlewareEsoOrEtoActivity.TYPE_ID, "standing_order");
                it.putExtra("address", middleAddress);
                startActivity(it);
                break;
            //临时医嘱
            case R.id.EtoNumber:
                it = new Intent(MiddlewareActivity.this, MiddlewareEsoOrEtoActivity.class);
                it.putExtra(MiddlewareEsoOrEtoActivity.TELEMEDICINE_INFO_ID, hisID);
                it.putExtra(MiddlewareEsoOrEtoActivity.TYPE_ID, "temporary_order");
                it.putExtra("address", middleAddress);
                startActivity(it);
                break;
            //手术记录
            case R.id.EoNumber:
                it = new Intent(MiddlewareActivity.this, MiddlewareEoActivity.class);
                it.putExtra(MiddlewareEsoOrEtoActivity.TELEMEDICINE_INFO_ID, hisID);
                it.putExtra("address", middleAddress);
                startActivity(it);
                break;
            //申请会诊
            case R.id.AddConsultationButton:
                it = new Intent(MiddlewareActivity.this, AddConsultationActivity.class);
                it.putExtra(AddConsultationActivity.HIS_ID, hisID);
                startActivity(it);
                break;
            //返回
            case R.id.BackImageView:
                onBackPressed();
                break;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        getIntentMsg();
        //通过接口，获得子页面的服务器ip，暂时定死
        //用户id暂时定死
        //加入标记是否获取请求intent成功
    }

    private void getIntentMsg() {
        middleAddress = getIntent().getStringExtra("address");
        hisID = getIntent().getStringExtra("hisId");
        Log.i("zzw", "middleAddress " + middleAddress + " " + hisID);
    }


}