package com.chinabsc.telemedicine.expert.expertActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;

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

@ContentView(R.layout.activity_contact_info)
public class ContactInfoActivity extends BaseActivity {

    public DisplayImageOptions mOptions;
    public ImageLoader mImageLoader = ImageLoader.getInstance();

    public static String CONTACT_INFO_ID = "CONTACT_INFO_ID";
    public static String CONTACT_PHONE_NUM = "CONTACT_PHONE_NUM";

    public String mContactInfoId = "";
    public String mContactPhone = "";

    @ViewInject(R.id.PhoneImageView)
    private ImageView mPhoneImageView;

    @ViewInject(R.id.UserHeadImageView)
    private ImageView mUserHeadImageView;

    @ViewInject(R.id.UserNameTextView)
    private TextView mUserNameTextView;

    @ViewInject(R.id.UserTitleTextView)
    private TextView mUserTitleTextView;

    @ViewInject(R.id.UserSiteName)
    private TextView mUserSiteName;

    @ViewInject(R.id.ContactInfoListView)
    private ListView mContactInfoListView;
    public ContactInfoListAdapter mContactInfoListAdapter;

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
                AlertDialog.Builder builder = new AlertDialog.Builder(ContactInfoActivity.this);
                //builder.setCancelable(false);
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

    public class DateInfo {
        public String clinicId;
        public String dateDone;
        public String sndSiteName;
        public String doctorName;
        public String patientName;
        public String patientGender;
        public String age;
        public String purpose;
    }

    public ArrayList<DateInfo> mScheduleItem = new ArrayList<DateInfo>();

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

        String userId = SPUtils.get(ContactInfoActivity.this, PublicUrl.USER_ID_KEY, "").toString();
        String token = getTokenFromLocal();
        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(userId)) {
            init();
            getData();
            getUserInfo();
            getContactSchedule(mContactInfoId);
            if (TextUtils.isEmpty(mContactPhone) || mContactPhone.equals("null")) {
                mPhoneImageView.setVisibility(View.INVISIBLE);
            }
        }

    }

    private void getUserInfo() {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/user/" + mContactInfoId);
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
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
                                    Log.i("userName", userName);
                                    mUserNameTextView.setText(userName);
                                }
                                if (k.has("siteName")) {
                                    String siteName = k.getString("siteName");
                                    mUserSiteName.setText(siteName);
                                    Log.i("siteName", siteName);
                                }
                                if (k.has("departName")) {
                                    String departName = k.getString("departName");
                                    mUserTitleTextView.setText(departName);
                                    Log.i("departName", departName);
                                }
                                if (k.has("jobTitle")) {
                                    String jobTitle = mUserTitleTextView.getText().toString();
                                    jobTitle = jobTitle + "   " + k.getString("jobTitle");
                                    mUserTitleTextView.setText(jobTitle);
                                    Log.i("jobTitle", jobTitle);
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
                T.showMessage(getApplication(), getString(R.string.server_error));
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

    private void getContactSchedule(String uId) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/clinic");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        String eId = SPUtils.get(ContactInfoActivity.this, PublicUrl.USER_ID_KEY, "").toString();
        params.addBodyParameter("expertUserId", eId);
        params.addBodyParameter("doctorUserId", uId);
        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("Schedule onSuccess", result);
                mScheduleItem = parseJson(result);
                mContactInfoListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                ex.printStackTrace();
                Log.i("onError", "onError:" + ex.getMessage());
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

    private ArrayList<DateInfo> parseJson(String result) {
        ArrayList<DateInfo> itemList = new ArrayList<DateInfo>();
        try {
            JSONObject j = new JSONObject(result);
            if (j.has("resultCode")) {
                String resultCode = j.getString("resultCode");
                if (resultCode.equals("200")) {
                    if (j.has("data")) {
                        String data = j.getString("data");
                        JSONArray array = new JSONArray(data);
                        itemList.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject k = array.getJSONObject(i);
                            DateInfo item = new DateInfo();
                            if (k.has("clinicId")) {
                                item.clinicId = k.getString("clinicId");
                            }
                            if (k.has("dateDone")) {
                                item.dateDone = k.getString("dateDone");
                            }
                            if (k.has("sndSiteName")) {
                                item.sndSiteName = k.getString("sndSiteName");
                            }
                            if (k.has("doctorName")) {
                                item.doctorName = k.getString("doctorName");
                            }
                            if (k.has("patientName")) {
                                item.patientName = k.getString("patientName");
                            }
                            if (k.has("patientGender")) {
                                item.patientGender = k.getString("patientGender");
                            }
                            if (k.has("age")) {
                                item.age = k.getString("age");
                            }
                            if (k.has("purpose")) {
                                item.purpose = k.getString("purpose");
                            }
                            itemList.add(item);
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
        return itemList;
    }

    private void init() {
        mContactInfoListAdapter = new ContactInfoListAdapter(this);
        mContactInfoListView.setAdapter(mContactInfoListAdapter);
        mContactInfoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ContactInfoActivity.this, TelemedicineInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(TelemedicineInfoActivity.TELEMEDICINE_INFO_ID, mScheduleItem.get(position).clinicId);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

    private void getData() {
        Bundle bundle = this.getIntent().getExtras();
        mContactInfoId = bundle.getString(CONTACT_INFO_ID);
        mContactPhone = bundle.getString(CONTACT_PHONE_NUM);
    }

    public class ContactInfoListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public ContactInfoListAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            //return mArticleListItem.size();
            return mScheduleItem.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.contact_info_item, null);
                viewHolder.date = (TextView) convertView.findViewById(R.id.DateTextView);
                viewHolder.patientNameView = (TextView) convertView.findViewById(R.id.PatientNameView);
                viewHolder.purposeView = (TextView) convertView.findViewById(R.id.PurposeView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.date.setText(mScheduleItem.get(position).dateDone);
            String patientGender = mScheduleItem.get(position).patientGender;
            String sex = "";
            if (patientGender.equals("0") || patientGender.equals("男")) {
                sex = "男";
            } else if (patientGender.equals("1") || patientGender.equals("女")) {
                sex = "女";
            } else {
                sex = "未知";
            }
//            viewHolder.ageView.setText(mAllItem.get(position).age);
            viewHolder.patientNameView.setText(mScheduleItem.get(position).patientName + " " + sex + " " + mScheduleItem.get(position).age + "岁");
            viewHolder.purposeView.setText(mScheduleItem.get(position).purpose);
            return convertView;
        }
    }

    private static class ViewHolder {
        TextView date;
        TextView patientNameView;
        TextView purposeView;
    }
}
