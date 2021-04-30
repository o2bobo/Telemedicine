package com.chinabsc.telemedicine.expert.expertActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;

import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.utils.T;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_user_info)
public class UserInfoActivity extends BaseActivity {

    public DisplayImageOptions mOptions;
    public ImageLoader mImageLoader = ImageLoader.getInstance();


    @ViewInject(R.id.UserHeadImageView)
    private ImageView mUserHeadImageView;
    @ViewInject(R.id.UserNameTextView)
    private TextView mUserNameTextView;
    @ViewInject(R.id.JobTitleTextView)
    private TextView mJobTitleTextView;
    @ViewInject(R.id.SiteNameTextView)
    private TextView mSiteNameTextView;
    @ViewInject(R.id.DepartNameTextView)
    private TextView mDepartNameTextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        mImageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.basic_image_download) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.drawable.basic_image_error)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.basic_image_error)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                .build();//构建完成

        getUserInfo();
    }

    private void getUserInfo() {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/user");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        //params.setConnectTimeout(2 * 1000);
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("getUserInfo onSuccess", result);
                try {
                    JSONObject j = new JSONObject(result);
                    if (j.has("resultCode")) {
                        String resultCode = j.getString("resultCode");
                        if (resultCode.equals("200")) {
                            if (j.has("data")) {
                                String data = j.getString("data");
                                JSONObject k = new JSONObject(data);
                                if (k.has("userId")) {
                                    String userId = k.getString("userId");
                                    Log.i("test", "userId: " + userId);
                                }
                                if (k.has("userName")) {
                                    String userName = k.getString("userName");
                                    mUserNameTextView.setText(userName);
                                }
                                if (k.has("jobTitle")) {
                                    String jobTitle = k.getString("jobTitle");
                                    mJobTitleTextView.setText(jobTitle);
                                }
                                if (k.has("siteName")) {
                                    String siteName = k.getString("siteName");
                                    mSiteNameTextView.setText(siteName);
                                }
                                if (k.has("departName")) {
                                    String siteName = k.getString("departName");
                                    mDepartNameTextView.setText(siteName);
                                }
                                if (k.has("photo")) {
                                    String photo = BaseConst.DEAULT_URL+ k.getString("photo");
                                    ImageLoader.getInstance().displayImage(photo, mUserHeadImageView, mOptions);
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

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                //ex.printStackTrace();
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
}
