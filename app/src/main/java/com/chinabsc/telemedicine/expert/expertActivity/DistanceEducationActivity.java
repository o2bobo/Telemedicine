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
import android.widget.AdapterView;

import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;

import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chinabsc.telemedicine.expert.utils.PublicUrl;
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

@ContentView(R.layout.activity_distance_education)
public class DistanceEducationActivity extends BaseActivity {
    public DisplayImageOptions mOptions;
    public ImageLoader mImageLoader = ImageLoader.getInstance();

    @ViewInject(R.id.PlayingTextView)
    private TextView mPlayingTextView;
    @ViewInject(R.id.WaitingTextView)
    private TextView mWaitingTextView;
    @ViewInject(R.id.CompletedTextView)
    private TextView mCompletedTextView;
    @ViewInject(R.id.AllTextView)
    private TextView mAllTextView;


    @ViewInject(R.id.PlayingSelectedView)
    private View mPlayingSelectedView;
    @ViewInject(R.id.WaitingSelectedView)
    private View mWaitingSelectedView;
    @ViewInject(R.id.CompletedSelectedView)
    private View mCompletedSelectedView;
    @ViewInject(R.id.AllSelectedView)
    private View mAllSelectedView;

    @ViewInject(R.id.PlayingListView)
    private ListView mPlayingListView;
    public PlayingListAdapter mPlayingListAdapter;

    @ViewInject(R.id.WaitingListView)
    private ListView mWaitingListView;
    public WaitingListAdapter mWaitingListAdapter;

    @ViewInject(R.id.CompletedListView)
    private ListView mCompletedListView;
    public CompletedListAdapter mCompletedListAdapter;

    @ViewInject(R.id.AllListView)
    public ListView mAllListView;
    public AllListAdapter mAllListAdapter;

    @Event(value = {
            R.id.BackImageView,
            R.id.PlayingTextView,
            R.id.WaitingTextView,
            R.id.CompletedTextView,
            R.id.AllTextView}, type = View.OnClickListener.class)
    private void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.BackImageView:
                finish();
                break;
            case R.id.PlayingTextView:
                initializeScheduleTextView();
                mPlayingSelectedView.setVisibility(View.VISIBLE);

                initializeScheduleListView();
                mPlayingListView.setVisibility(View.VISIBLE);
                break;
            case R.id.WaitingTextView:
                initializeScheduleTextView();
                mWaitingSelectedView.setVisibility(View.VISIBLE);

                initializeScheduleListView();
                mWaitingListView.setVisibility(View.VISIBLE);
                break;
            case R.id.CompletedTextView:
                initializeScheduleTextView();
                mCompletedSelectedView.setVisibility(View.VISIBLE);

                initializeScheduleListView();
                mCompletedListView.setVisibility(View.VISIBLE);
                break;
            case R.id.AllTextView:
                initializeScheduleTextView();
                mAllSelectedView.setVisibility(View.VISIBLE);

                initializeScheduleListView();
                mAllListView.setVisibility(View.VISIBLE);
                break;
        }
    }

    public class DateInfo {
        public String courseclass;
        public String starttime;
        public String liveid;
        public String rcvsiteid;
        public String commondepartmentcode;
        public String expertname;
        public String jobtitlecode;
        public String statue;
    }

    public ArrayList<DateInfo> mPlayingItem = new ArrayList<DateInfo>();
    public ArrayList<DateInfo> mWattingItem = new ArrayList<DateInfo>();
    public ArrayList<DateInfo> mCompletedItem = new ArrayList<DateInfo>();
    public ArrayList<DateInfo> mAllItem = new ArrayList<DateInfo>();

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


        String token = getTokenFromLocal();
        if (!TextUtils.isEmpty(token)) {
            init();
            startAllRequest();
        }
    }

    private void init() {
        mPlayingListAdapter = new PlayingListAdapter(this);
        mPlayingListView.setAdapter(mPlayingListAdapter);
        mPlayingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(DistanceEducationActivity.this, EducationLiveActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(EducationLiveActivity.LIVE_ID, mPlayingItem.get(position).liveid);
                String hospital = mPlayingItem.get(position).rcvsiteid + " " + mPlayingItem.get(position).commondepartmentcode;
                String doctor = mPlayingItem.get(position).expertname + " " + mPlayingItem.get(position).jobtitlecode;
                bundle.putString(EducationLiveActivity.LIVE_TITLE, mPlayingItem.get(position).courseclass);
                bundle.putString(EducationLiveActivity.LIVE_HOSPITAL, hospital);
                bundle.putString(EducationLiveActivity.LIVE_DOCTOR, doctor);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        mWaitingListAdapter = new WaitingListAdapter(this);
        mWaitingListView.setAdapter(mWaitingListAdapter);

        mCompletedListAdapter = new CompletedListAdapter(this);
        mCompletedListView.setAdapter(mCompletedListAdapter);

        mAllListAdapter = new AllListAdapter(this);
        mAllListView.setAdapter(mAllListAdapter);
        mAllListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String status = mAllItem.get(position).statue;
                if (status.equals("playing")) {

                    Intent intent = new Intent(DistanceEducationActivity.this, EducationLiveActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(EducationLiveActivity.LIVE_ID, mAllItem.get(position).liveid);
                    String hospital = mAllItem.get(position).rcvsiteid + " " + mAllItem.get(position).commondepartmentcode;
                    String doctor = mAllItem.get(position).expertname + " " + mAllItem.get(position).jobtitlecode;
                    bundle.putString(EducationLiveActivity.LIVE_TITLE, mAllItem.get(position).courseclass);
                    bundle.putString(EducationLiveActivity.LIVE_HOSPITAL, hospital);
                    bundle.putString(EducationLiveActivity.LIVE_DOCTOR, doctor);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }

    private void startAllRequest() {
        getPlayingLive();
        getWaitingLive();
        getCompletedLive();
        getAllLive();
    }

    private void getPlayingLive() {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/edu/list");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addBodyParameter("status", "playing");
        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("getPlayingLive", result);
                mPlayingItem = parseJson(result);
                mPlayingListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                ex.printStackTrace();
                Log.i("getPlayingLive onError", "onError:" + ex.getMessage());
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

    private void getWaitingLive() {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL+ "/mobile/edu/list");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addBodyParameter("status", "preparing");
        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("getWaitingLive", result);
                mWattingItem = parseJson(result);
                mWaitingListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                ex.printStackTrace();
                Log.i("getWaitingLive onError", "onError:" + ex.getMessage());
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

    private void getCompletedLive() {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL+ "/mobile/edu/list");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addBodyParameter("status", "stopped");
        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("getCompletedLive", result);
                mCompletedItem = parseJson(result);
                mCompletedListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                ex.printStackTrace();
                Log.i("getCompleted onError", "onError:" + ex.getMessage());
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

    private void getAllLive() {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/edu/list");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("getAllLive", result);
                mAllItem = parseJson(result);
                mAllListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                ex.printStackTrace();
                Log.i("getAllLive onError", "onError:" + ex.getMessage());
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
                            if (k.has("warename")) {
                                item.courseclass = k.getString("warename");
                            }
                            if (k.has("starttime")) {
                                item.starttime = k.getString("starttime");
                            }
                            if (k.has("liveid")) {
                                item.liveid = k.getString("liveid");
                            }
                            if (k.has("rcvsiteid")) {
                                item.rcvsiteid = k.getString("rcvsiteid");
                            }
                            if (k.has("commondepartmentcode")) {
                                item.commondepartmentcode = k.getString("commondepartmentcode");
                            }
                            if (k.has("expertname")) {
                                item.expertname = k.getString("expertname");
                            }
                            if (k.has("jobtitlecode")) {
                                item.jobtitlecode = k.getString("jobtitlecode");
                            }
                            if (k.has("statue")) {
                                item.statue = k.getString("statue");
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

    //获取视频详情
    private void getEducationLiveInfo(String liveId,String doctorId){
        RequestParams params=new RequestParams("http://192.168.1.109:8080/mobile/edu/checksignUp");
        params.addBodyParameter("liveId",liveId);
        params.addBodyParameter("doctorId",doctorId);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG,"getEducationLiveInfo result"+result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });


    }

    private void initializeScheduleTextView() {
        mPlayingSelectedView.setVisibility(View.INVISIBLE);
        mWaitingSelectedView.setVisibility(View.INVISIBLE);
        mCompletedSelectedView.setVisibility(View.INVISIBLE);
        mAllSelectedView.setVisibility(View.INVISIBLE);
    }

    private void initializeScheduleListView() {
        mPlayingListView.setVisibility(View.GONE);
        mWaitingListView.setVisibility(View.GONE);
        mCompletedListView.setVisibility(View.GONE);
        mAllListView.setVisibility(View.GONE);
    }

    public class PlayingListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public PlayingListAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            //return mArticleListItem.size();
            return mPlayingItem.size();
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
                convertView = mInflater.inflate(R.layout.education_schedule_item, null);
                viewHolder.pic = (ImageView) convertView.findViewById(R.id.pic);
                viewHolder.courseclass = (TextView) convertView.findViewById(R.id.courseclass);
                viewHolder.starttime = (TextView) convertView.findViewById(R.id.starttime);
                viewHolder.statue = (TextView) convertView.findViewById(R.id.statue);
                viewHolder.rcvsiteid = (TextView) convertView.findViewById(R.id.rcvsiteid);
                viewHolder.expertname = (TextView) convertView.findViewById(R.id.expertname);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.courseclass.setText(mPlayingItem.get(position).courseclass);
            String time = PublicUrl.stampToDate(mPlayingItem.get(position).starttime);
            viewHolder.starttime.setText(time);
            String status = mPlayingItem.get(position).statue;
            if (status.equals("preparing")) {
                status = "等待中";
                viewHolder.statue.setBackgroundResource(R.drawable.education_status_preparing);
                ImageLoader.getInstance().displayImage("drawable://" + R.mipmap.education_preparing, viewHolder.pic, mOptions);
            } else if (status.equals("stopped")) {
                status = "已结束";
                viewHolder.statue.setBackgroundResource(R.drawable.education_status_completed);
                ImageLoader.getInstance().displayImage("drawable://" + R.mipmap.education_completed, viewHolder.pic, mOptions);
            } else {
                status = "正在直播";
                viewHolder.statue.setBackgroundResource(R.drawable.education_status_playing);
                ImageLoader.getInstance().displayImage("drawable://" + R.mipmap.education_playing, viewHolder.pic, mOptions);
            }
            viewHolder.statue.setText(status);
            viewHolder.rcvsiteid.setText(mPlayingItem.get(position).rcvsiteid + " " + mPlayingItem.get(position).commondepartmentcode);
            viewHolder.expertname.setText(mPlayingItem.get(position).expertname + " " + mPlayingItem.get(position).jobtitlecode);
            return convertView;
        }
    }

    public class WaitingListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public WaitingListAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            //return mArticleListItem.size();
            return mWattingItem.size();
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
                convertView = mInflater.inflate(R.layout.education_schedule_item, null);
                viewHolder.pic = (ImageView) convertView.findViewById(R.id.pic);
                viewHolder.courseclass = (TextView) convertView.findViewById(R.id.courseclass);
                viewHolder.starttime = (TextView) convertView.findViewById(R.id.starttime);
                viewHolder.statue = (TextView) convertView.findViewById(R.id.statue);
                viewHolder.rcvsiteid = (TextView) convertView.findViewById(R.id.rcvsiteid);
                viewHolder.expertname = (TextView) convertView.findViewById(R.id.expertname);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.courseclass.setText(mWattingItem.get(position).courseclass);
            String time = PublicUrl.stampToDate(mWattingItem.get(position).starttime);
            viewHolder.starttime.setText(time);
            String status = mWattingItem.get(position).statue;
            if (status.equals("preparing")) {
                status = "等待中";
                viewHolder.statue.setBackgroundResource(R.drawable.education_status_preparing);
                ImageLoader.getInstance().displayImage("drawable://" + R.mipmap.education_preparing, viewHolder.pic, mOptions);
            } else if (status.equals("stopped")) {
                status = "已结束";
                viewHolder.statue.setBackgroundResource(R.drawable.education_status_completed);
                ImageLoader.getInstance().displayImage("drawable://" + R.mipmap.education_completed, viewHolder.pic, mOptions);
            } else {
                status = "正在直播";
                viewHolder.statue.setBackgroundResource(R.drawable.education_status_playing);
                ImageLoader.getInstance().displayImage("drawable://" + R.mipmap.education_playing, viewHolder.pic, mOptions);
            }
            viewHolder.statue.setText(status);
            viewHolder.rcvsiteid.setText(mWattingItem.get(position).rcvsiteid + " " + mWattingItem.get(position).commondepartmentcode);
            viewHolder.expertname.setText(mWattingItem.get(position).expertname + " " + mWattingItem.get(position).jobtitlecode);
            return convertView;
        }
    }

    public class CompletedListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public CompletedListAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            //return mArticleListItem.size();
            return mCompletedItem.size();
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
                convertView = mInflater.inflate(R.layout.education_schedule_item, null);
                viewHolder.pic = (ImageView) convertView.findViewById(R.id.pic);
                viewHolder.courseclass = (TextView) convertView.findViewById(R.id.courseclass);
                viewHolder.starttime = (TextView) convertView.findViewById(R.id.starttime);
                viewHolder.statue = (TextView) convertView.findViewById(R.id.statue);
                viewHolder.rcvsiteid = (TextView) convertView.findViewById(R.id.rcvsiteid);
                viewHolder.expertname = (TextView) convertView.findViewById(R.id.expertname);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.courseclass.setText(mCompletedItem.get(position).courseclass);
            String time = PublicUrl.stampToDate(mCompletedItem.get(position).starttime);
            viewHolder.starttime.setText(time);
            String status = mCompletedItem.get(position).statue;
            if (status.equals("preparing")) {
                status = "等待中";
                viewHolder.statue.setBackgroundResource(R.drawable.education_status_preparing);
                ImageLoader.getInstance().displayImage("drawable://" + R.mipmap.education_preparing, viewHolder.pic, mOptions);
            } else if (status.equals("stopped")) {
                status = "已结束";
                viewHolder.statue.setBackgroundResource(R.drawable.education_status_completed);
                ImageLoader.getInstance().displayImage("drawable://" + R.mipmap.education_completed, viewHolder.pic, mOptions);
            } else {
                status = "正在直播";
                viewHolder.statue.setBackgroundResource(R.drawable.education_status_playing);
                ImageLoader.getInstance().displayImage("drawable://" + R.mipmap.education_playing, viewHolder.pic, mOptions);
            }
            viewHolder.statue.setText(status);
            viewHolder.rcvsiteid.setText(mCompletedItem.get(position).rcvsiteid + " " + mCompletedItem.get(position).commondepartmentcode);
            viewHolder.expertname.setText(mCompletedItem.get(position).expertname + " " + mCompletedItem.get(position).jobtitlecode);
            return convertView;
        }
    }

    public class AllListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public AllListAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            //return mArticleListItem.size();
            return mAllItem.size();
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
                convertView = mInflater.inflate(R.layout.education_schedule_item, null);
                viewHolder.pic = (ImageView) convertView.findViewById(R.id.pic);
                viewHolder.courseclass = (TextView) convertView.findViewById(R.id.courseclass);
                viewHolder.starttime = (TextView) convertView.findViewById(R.id.starttime);
                viewHolder.statue = (TextView) convertView.findViewById(R.id.statue);
                viewHolder.rcvsiteid = (TextView) convertView.findViewById(R.id.rcvsiteid);
                viewHolder.expertname = (TextView) convertView.findViewById(R.id.expertname);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.courseclass.setText(mAllItem.get(position).courseclass);
            String time = PublicUrl.stampToDate(mAllItem.get(position).starttime);
            viewHolder.starttime.setText(time);
            String status = mAllItem.get(position).statue;
            if (status.equals("preparing")) {
                status = "等待中";
                viewHolder.statue.setBackgroundResource(R.drawable.education_status_preparing);
                ImageLoader.getInstance().displayImage("drawable://" + R.mipmap.education_preparing, viewHolder.pic, mOptions);
            } else if (status.equals("stopped")) {
                status = "已结束";
                viewHolder.statue.setBackgroundResource(R.drawable.education_status_completed);
                ImageLoader.getInstance().displayImage("drawable://" + R.mipmap.education_completed, viewHolder.pic, mOptions);
            } else {
                status = "正在直播";
                viewHolder.statue.setBackgroundResource(R.drawable.education_status_playing);
                ImageLoader.getInstance().displayImage("drawable://" + R.mipmap.education_playing, viewHolder.pic, mOptions);
            }
            viewHolder.statue.setText(status);
            viewHolder.rcvsiteid.setText(mAllItem.get(position).rcvsiteid + " " + mAllItem.get(position).commondepartmentcode);
            viewHolder.expertname.setText(mAllItem.get(position).expertname + " " + mAllItem.get(position).jobtitlecode);
            return convertView;
        }
    }

    private static class ViewHolder {
        ImageView pic;
        TextView courseclass;
        TextView starttime;
        TextView rcvsiteid;
        TextView expertname;
        TextView statue;
    }
}

