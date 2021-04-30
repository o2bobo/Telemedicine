package com.chinabsc.telemedicine.expert.expertActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.myView.StretchyTextView;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.utils.T;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

@ContentView(R.layout.activity_hospital)
public class HospitalActivity extends BaseActivity {

    public static String HOSPITAL_ID = "HOSPITAL_ID";
    public static String HOSPITAL_NAME = "HOSPITAL_NAME";

    @ViewInject(R.id.TitleTextView)
    public TextView mTitleTextView;

    @ViewInject(R.id.HospitalTextView)
    public StretchyTextView mHospitalTextView;

    @ViewInject(R.id.HospitalImageView)
    public ImageView mHospitalImageView;

    @ViewInject(R.id.DepartLayout)
    public LinearLayout mDepartLayout;

    @ViewInject(R.id.DepartNo)
    public TextView mDepartNo;

    @Event(value = {
            R.id.BackImageView}, type = View.OnClickListener.class)
    private void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.BackImageView:
                finish();
                break;
        }
    }

    public class DepartInfo {
        public String departid;
        public String departname;
    }
    public ArrayList<DepartInfo> mDepartInfoList = new ArrayList<DepartInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        Intent i = getIntent();
        String hid = i.getStringExtra(HOSPITAL_ID);
        String name = i.getStringExtra(HOSPITAL_NAME);
        Log.i("test", hid + "+" + name);
        mTitleTextView.setText(name);
        mHospitalTextView.setMaxLineCount(3);
        getData(hid);
    }

    private void getData(String hospitalId) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL+ "/mobile/union/getMemberById");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addBodyParameter("id", hospitalId);
        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("test", "getData:" + result);
                parseTabJson(result);
                if (mDepartInfoList.size() == 0) {
                    mDepartNo.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("onError", "onError:" + ex.getMessage());
                T.showMessage(getApplicationContext(), getString(R.string.server_error));
            }

            @Override
            public void onCancelled(CancelledException cex) {
                //Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {
                //mLoginLoadingLayout.setVisibility(View.GONE);
            }
        });
    }

    private void parseTabJson(String result) {
        try {
            JSONObject j = new JSONObject(result);
            if (j.has("resultCode")) {
                String resultCode = j.getString("resultCode");
                if (resultCode.equals("200")) {
                    if (j.has("data")) {
                        String data = j.getString("data");
                        Log.i("test", "data:" + data);
                        JSONObject k = new JSONObject(data);
                        if (k.has("site")) {
                            String site = k.getString("site");
                            Log.i("test", "site:" + site);
                            JSONObject l = new JSONObject(site);
                            if (l.has("description")) {
                                String description = l.getString("description");
                                if (!TextUtils.isEmpty(description)) {
                                    mHospitalTextView.setContent(description);
                                } else {
                                    mHospitalTextView.setContent("无");
                                }
                            }
                            if (l.has("titleimage")) {
                                String titleimage = l.getString("titleimage");
                                setTitleImage(titleimage);
                            }
                        }
                        if (k.has("departList")) {
                            String array = k.getString("departList");
                            JSONArray AllianceArray = new JSONArray(array);
                            for (int i = 0; i < AllianceArray.length(); i++) {
                                JSONObject l = AllianceArray.getJSONObject(i);
                                DepartInfo item = new DepartInfo();
                                if (l.has("departid")) {
                                    item.departid = l.getString("departid");
                                }
                                if (l.has("departname")) {
                                    item.departname = l.getString("departname");
                                }
                                mDepartInfoList.add(item);
                            }
                            setDepartInfoData();
                        }
                    }
                } else if (resultCode.equals("401")) {
                    T.showMessage(getApplicationContext(), getString(R.string.login_timeout));
                    delToken();
                    doLogout();
                } else {
                    T.showMessage(getApplicationContext(), getString(R.string.api_error) + resultCode);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setDepartInfoData() {
        View ViewArray[] = new View[mDepartInfoList.size()];
        for (int i = 0; i < mDepartInfoList.size(); i++) {
            HospitalItemView item = new HospitalItemView();
            ViewArray[i] = item.mHospitalItem;
            item.mTitle.setText(mDepartInfoList.get(i).departname);
            mDepartLayout.addView(ViewArray[i]);
        }

        int mDepartItemNum = 0;
        for (int i = 0; i < mDepartInfoList.size(); i++) {
            ViewArray[i].setTag(mDepartItemNum);
            mDepartItemNum++;
            ViewArray[i].setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int n = (Integer) v.getTag();
                    Intent it = new Intent(HospitalActivity.this, DepartActivity.class);
                    it.putExtra(DepartActivity.Depart_ID, mDepartInfoList.get(n).departid);
                    it.putExtra(DepartActivity.Depart_NAME, mDepartInfoList.get(n).departname);
                    startActivity(it);
                }

            });

        }
    }

    private class HospitalItemView {
        public View mHospitalItem;
        public TextView mTitle;

        public HospitalItemView() {
            mHospitalItem = LayoutInflater.from(HospitalActivity.this).inflate(
                    R.layout.add_text_view, null);
            viewfinder();
        }

        public void viewfinder() {
            mTitle = (TextView) mHospitalItem.findViewById(R.id.MedicalImageItemTitle);
        }
    }

    private void setTitleImage(String url) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.basic_image_download_widescreen) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.drawable.basic_image_error_widescreen)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.basic_image_error_widescreen)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                .build();
        ImageLoader.getInstance().displayImage(BaseConst.DEAULT_URL + url, mHospitalImageView, options);
    }
}
