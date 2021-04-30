package com.chinabsc.telemedicine.expert.expertActivity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.entity.MiddlewareMedicalImgEntity;
import com.chinabsc.telemedicine.expert.expertinterfaces.OnItemInterfaceClick;
import com.chinabsc.telemedicine.expert.myAdapter.MedicalImgReportAdapter;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.ref.WeakReference;
@ContentView(R.layout.activity_middleware_medical_img)
public class MiddlewareMedicalImgActivity extends BaseActivity implements OnItemInterfaceClick {
    private String TAG = "MiddlewareMedicalImgActivity";

    @ViewInject(R.id.myRecycle)
    private RecyclerView mRecycle;
    private String hisId;
    private String address;
    MyMiddleHandler handler=new  MyMiddleHandler(this);

    LinearLayoutManager linearLayoutManager;
    MedicalImgReportAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        linearLayoutManager = new LinearLayoutManager(this);
        getMIntent();
    }
    public void goBack(View v) {
        onBackPressed();
    }
    private void getMIntent() {
        hisId = getIntent().getStringExtra(MiddlewareEsoOrEtoActivity.TELEMEDICINE_INFO_ID);
        address = getIntent().getStringExtra("address");
        if (hisId != null) {
            getMsg();
        }
    }

    private void getMsg() {
        //http://123.56.177.185:30007/api/risExamList?hisId= + hisid 接口地址
        RequestParams params = new RequestParams(address + "/api/risExamList?hisId=" + hisId);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "result==" + result);
                MiddlewareMedicalImgEntity tmpentity = JSONObject.parseObject(result, MiddlewareMedicalImgEntity.class);

                Message msg=new Message();
                msg.what=1;
                msg.obj=tmpentity;
                handler.sendMessage(msg);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "ex==" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    public void onclick(String id, String tag) {
        Log.i(TAG,"oncitem click"+id+" tag="+tag);
        String url;
        Intent intent=new Intent(this,MedicalWareWebActivity.class);

        if (tag.equals("seeReport")){
            intent.putExtra("type","查看报告");
            //TOdo 中间件影像地址是写死的目前
            url="http://123.56.177.185:30010/reportWeb/index.aspx?"+id;
            intent.putExtra("url",url);

        }else if(tag.equals("seeMedical")){
            intent.putExtra("type","查看影像");
            url="http://123.56.177.185:30010/portal/default.aspx?"+id;
            intent.putExtra("url",url);
        }
        startActivity(intent);
    }

    static class MyMiddleHandler extends Handler {

        private final WeakReference<MiddlewareMedicalImgActivity> mActivity;

        public MyMiddleHandler(MiddlewareMedicalImgActivity mactivity) {
            Log.i("MiddleSelectActivity", "new MyMiddleHandler");
            mActivity = new WeakReference<MiddlewareMedicalImgActivity>(mactivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mActivity == null) {
                return;
            }
            switch (msg.what) {
                case 1:
                    MiddlewareMedicalImgEntity entity= (MiddlewareMedicalImgEntity) msg.obj;
                    mActivity.get().adapter = new MedicalImgReportAdapter(mActivity.get(),entity);
                    //为recyclerView设置布局管理器
                    mActivity.get().adapter.setOnItemClickListener(mActivity.get());
                    mActivity.get().mRecycle.setLayoutManager(mActivity.get().linearLayoutManager);
                   // DividerItemDecoration divider = new DividerItemDecoration(mActivity.get(), DividerItemDecoration.VERTICAL);
                   // divider.setDrawable(ContextCompat.getDrawable(mActivity.get(), R.drawable.devider_shape_2dp));
                 //   mActivity.get().mRecycle.addItemDecoration(divider);
                    mActivity.get().mRecycle.setAdapter(mActivity.get().adapter);
                    break;
                default:
                    break;
            }

        }
    }
}
