package com.chinabsc.telemedicine.expert.expertActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.myView.StretchyTextView;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.utils.SPUtils;
import com.chinabsc.telemedicine.expert.utils.T;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
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

@ContentView(R.layout.activity_expert)
public class ExpertActivity extends BaseActivity {
    private String TAG = "ExpertActivity";
    public DisplayImageOptions mOptions;
    public ImageLoader mImageLoader = ImageLoader.getInstance();

    public static String Expert_ID = "Expert_ID";
    public static String Expert_NAME = "Expert_NAME";

    @ViewInject(R.id.TitleTextView)
    public TextView mTitleTextView;

    @ViewInject(R.id.UserHeadImageView)
    private ImageView mUserHeadImageView;

    @ViewInject(R.id.UserSiteName)
    private TextView mUserSiteName;

    @ViewInject(R.id.UserDepartmentName)
    private TextView mUserDepartmentName;

    @ViewInject(R.id.UserTitleTextView)
    private TextView mUserTitleTextView;

    @ViewInject(R.id.SkillTextView)
    private StretchyTextView mSkillTextView;

    @ViewInject(R.id.DescriptionTextView)
    private StretchyTextView mDescriptionTextView;

    public String mContactPhone = "";

    @Event(value = {
            R.id.BackImageView,
            R.id.PhoneImageView}, type = View.OnClickListener.class)
    private void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.BackImageView:
                finish();
                break;
            case R.id.PhoneImageView:
                AlertDialog.Builder builder = new AlertDialog.Builder(ExpertActivity.this);
                builder.setCancelable(false);
                builder.setTitle(mContactPhone);
                builder.setItems(new String[]{"拨号", "短信"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mContactPhone));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                break;
                            case 1:
                                Uri uri = Uri.parse("smsto:" + mContactPhone);
                                Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                                it.putExtra("sms_body", "");
                                startActivity(it);
                                break;
                            default:
                                break;
                        }
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        mImageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.basic_image_user) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.drawable.basic_image_user)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.basic_image_user)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                .build();//构建完成

        Intent i = getIntent();
        String eid = i.getStringExtra(Expert_ID);
        String name = i.getStringExtra(Expert_NAME);
        Log.i(TAG, "eid + name==" + eid + "+" + name);
        //mTitleTextView.setText(name);

        String userId = SPUtils.get(ExpertActivity.this, PublicUrl.USER_ID_KEY, "").toString();
        String token = getTokenFromLocal();
        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(userId)) {
            getUserInfo(eid);
        }

    }

    private void getUserInfo(String eid) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/union/getExpertById");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addBodyParameter("id", eid);
        Log.i(TAG, "getData url==" + BaseConst.DEAULT_URL + "/mobile/union/getExpertById");
        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "getData==" + result);
                parseTabJson(result);
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
                        JSONObject k = new JSONObject(data);
                        if (k.has("expert")) {
                            String expert = k.getString("expert");
                            JSONObject l = new JSONObject(expert);
                            if (l.has("photo")) {
                                String photo = BaseConst.DEAULT_URL + l.getString("photo");
                                ImageLoader.getInstance().displayImage(photo, mUserHeadImageView, mOptions);
                            }
                            if (l.has("jobtitle")) {
                                String jobtitle = l.getString("jobtitle");
                                mUserTitleTextView.setText(jobtitle);
                            }
                            if (l.has("telephone")) {
                                mContactPhone = l.getString("telephone");
                            }
                            if (l.has("description")) {
                                String description = l.getString("description");
                                mDescriptionTextView.setMaxLineCount(3);
                                mDescriptionTextView.setContent(description);
                            }
                            if (l.has("skill")) {
                                String skill = l.getString("skill");
                                mSkillTextView.setMaxLineCount(3);
                                mSkillTextView.setContent(skill);
                            }
                            if (l.has("realname")) {
                                String realname = l.getString("realname");
                                mTitleTextView.setText(realname);
                            }
                        }
                        if (k.has("site")) {
                            String site = k.getString("site");
                            mUserSiteName.setText(site);
                        }
                        if (k.has("department")) {
                            String department = k.getString("department");
                            mUserDepartmentName.setText(department);
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

}
