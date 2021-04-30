package com.chinabsc.telemedicine.expert.expertActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;

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

@ContentView(R.layout.activity_business_contact)
public class BusinessContactActivity extends BaseActivity {

    public DisplayImageOptions mOptions;
    public ImageLoader mImageLoader = ImageLoader.getInstance();

    @ViewInject(R.id.ContactListView)
    private ListView mContactListView;
    public ContactListAdapter mContactListAdapter;

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

    public class DateInfo {
        public String userId;
        public String head;
        public String userName;
        public String mobile;
        public String siteName;
    }

    public ArrayList<DateInfo> mContactItem = new ArrayList<DateInfo>();

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

        String userId = SPUtils.get(BusinessContactActivity.this, PublicUrl.USER_ID_KEY, "").toString();
        String token = getTokenFromLocal();
        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(userId)) {
            setViewListener();
            getDataForApi(userId);
        }
    }

    private void getDataForApi(String userId) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/user/contacts");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addQueryStringParameter("userId", userId);
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("Business onSuccess", result);
                parseJson(result);
                mContactItem = parseJson(result);
                mContactListAdapter.notifyDataSetChanged();
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
                            if (k.has("userId")) {
                                item.userId = k.getString("userId");
                            }
                            if (k.has("userName")) {
                                item.userName = k.getString("userName");
                            }
                            if (k.has("mobile")) {
                                item.mobile = k.getString("mobile");
                            }
                            if (k.has("siteName")) {
                                item.siteName = k.getString("siteName");
                            }
                            if (k.has("photo")) {
                                item.head = BaseConst.DEAULT_URL + k.getString("photo");
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

    private void setViewListener() {
        mContactListAdapter = new ContactListAdapter(this);
        mContactListView.setAdapter(mContactListAdapter);
        mContactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(BusinessContactActivity.this, ContactInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(ContactInfoActivity.CONTACT_INFO_ID, mContactItem.get(position).userId);
                bundle.putString(ContactInfoActivity.CONTACT_PHONE_NUM, mContactItem.get(position).mobile);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    public class ContactListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public ContactListAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            //return mArticleListItem.size();
            return mContactItem.size();
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
                convertView = mInflater.inflate(R.layout.business_contact_item, null);
                viewHolder.head = (ImageView) convertView.findViewById(R.id.UserHeadImageView);
                viewHolder.name = (TextView) convertView.findViewById(R.id.NameTextView);
                viewHolder.phone = (TextView) convertView.findViewById(R.id.PhoneTextView);
                viewHolder.hospital = (TextView) convertView.findViewById(R.id.HospitalTextView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ImageLoader.getInstance().displayImage(mContactItem.get(position).head, viewHolder.head, mOptions);
            viewHolder.name.setText(mContactItem.get(position).userName);
            String mobile = mContactItem.get(position).mobile;
            if (TextUtils.isEmpty(mobile) || mobile.equals("null")) {
                viewHolder.phone.setText("无");
            } else {
                viewHolder.phone.setText(mobile);
            }
            viewHolder.hospital.setText(mContactItem.get(position).siteName);
            return convertView;
        }
    }

    private static class ViewHolder {
        ImageView head;
        TextView name;
        TextView phone;
        TextView hospital;
    }
}
