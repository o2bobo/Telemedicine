package com.chinabsc.telemedicine.expert.myView;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.entity.DocMenuData;
import com.chinabsc.telemedicine.expert.expertActivity.LoginActivity;
import com.chinabsc.telemedicine.expert.expertActivity.MainActivity;
import com.chinabsc.telemedicine.expert.myAdapter.DocMenuDialogAdapter;
import com.chinabsc.telemedicine.expert.myAdapter.DocMenuPagerAdapter;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.utils.SPUtils;
import com.chinabsc.telemedicine.expert.utils.T;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class DocSelfDialog extends BaseMenuDialog {
    private String mUserId = "";
    private int mWidth;    //宽度
    private DocMenuViewPager mViewPager; //滑动viewPager
    private LinearLayout mRootView;    //需要显示的layout
    private View view1, view2;
    private ListView mListView1, mListView2;
    private DocMenuDialogAdapter mListView1Adapter, mListView2Adapter;
    private List<DocMenuData> mMenuList1, mMenuList2;
    private DocMenuData mMenuDataNull, mMenuData1, mMenuData2;
    private List<View> views = new ArrayList<View>(); //数据集合
    private MenuItemClickListener menuItemClickListener;   //接口，点击监听

    public DocSelfDialog(Context context) {
        super(context);

        String userId = SPUtils.get(context, PublicUrl.USER_ID_KEY, "").toString();
        mUserId = userId;
        mWidth = mContext.getResources().getDisplayMetrics().widthPixels;//获取屏幕参数
        mContentView = LayoutInflater.from(context).inflate(R.layout.doc_menu_dialog, null);
        //初始化控件及对控件操作
        initViews();
        setTitle("请选择");//设置title
        getUserInfo();
    }

    private void initViews() {
        mRootView = (LinearLayout) findViewById(R.id.rootview);
        mViewPager = (DocMenuViewPager) findViewById(R.id.viewpager);
        mViewPager.setOffscreenPageLimit(2);//显示2页

        //为view加载layout,由于三个级的菜单都是只有一个listView，这里就只xie一个了
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view1 = inflater.inflate(R.layout.doc_menu_pager_number, null);
        view2 = inflater.inflate(R.layout.doc_menu_pager_number, null);

        //获取id
        mListView1 = (ListView) view1.findViewById(R.id.listview);
        mListView2 = (ListView) view2.findViewById(R.id.listview);

        //初始化数据列表
        mMenuList1 = new ArrayList<DocMenuData>();
        mMenuList2 = new ArrayList<DocMenuData>();

        mMenuDataNull = new DocMenuData();
        mMenuDataNull.id = "";
        mMenuDataNull.name = "";

        //关联adapter
        mListView1Adapter = new DocMenuDialogAdapter(mContext, mMenuList1);
        mListView1Adapter.setSelectedBackgroundResource(R.drawable.doc_menu_select_white);//选中时的背景
        mListView1Adapter.setHasDivider(false);
        mListView1Adapter.setNormalBackgroundResource(R.color.f5f5f5);//未选中
        mListView1.setAdapter(mListView1Adapter);


        views.add(view1);
        views.add(view2);//当前是第三级菜单，所以前面已经存在第一，第二菜单了

        //关联
        mViewPager.setAdapter(new DocMenuPagerAdapter(views));
        //触屏监听
        mRootView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mViewPager.dispatchTouchEvent(event);
            }
        });

        //view1的listView的点击事件
        //点击事件
        mListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mListView1Adapter != null)
                    mListView1Adapter.setSelectedPos(position);
                if (mListView2Adapter != null)
                    mListView2Adapter.setSelectedPos(-1);

                mMenuData1 = (DocMenuData) parent.getItemAtPosition(position);
                getDoc(mMenuData1.id);
                if (mListView2Adapter == null) {
                    mListView2Adapter = new DocMenuDialogAdapter(mContext, mMenuList2);
                    mListView2Adapter.setNormalBackgroundResource(R.color.ffffff);
                    mListView2.setAdapter(mListView2Adapter);
                }
            }
        });


        //view2的listView点击
        mListView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMenuData2 = (DocMenuData) parent.getItemAtPosition(position);
                setDictItemClickListener(mMenuDataNull, mMenuDataNull, mMenuData1, mMenuData2);//选中点击的子菜单，去设置titleName
            }
        });
    }

    //获取当前用户所在医院
    private void getUserInfo() {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/user");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        //params.setConnectTimeout(2 * 1000);
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject j = new JSONObject(result);
                    if (j.has("resultCode")) {
                        String resultCode = j.getString("resultCode");
                        if (resultCode.equals("200")) {
                            if (j.has("data")) {
                                String data = j.getString("data");
                                JSONObject k = new JSONObject(data);
                                if (k.has("siteId")) {
                                    String siteId = k.getString("siteId");
                                    getDepartment(siteId);
                                    Log.i("siteId", siteId);
                                }
                                if (k.has("siteName")) {
                                    String siteName = k.getString("siteName");
                                    mMenuDataNull.name = siteName;
                                }
                                if (k.has("siteId")) {
                                    String siteId = k.getString("siteId");
                                    mMenuDataNull.id = siteId;
                                }
                            }
                        } else {
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

    //获取科室列表
    private void getDepartment(String itemCode) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/clinic/getDepartList?siteid=" + itemCode);
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("test", "getDepartment:" + result);
                com.alibaba.fastjson.JSONObject j = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.parse(result);
                String resultCode = j.getString("resultCode");
                String resultMsg = j.getString("resultMsg");
                if (resultCode.equals("200")) {
                    com.alibaba.fastjson.JSONArray dataArray = j.getJSONArray("data");
                    mMenuList1.clear();
                    for (int a = 0; a < dataArray.size(); a++) {
                        String departid = dataArray.getJSONObject(a).get("departid").toString();
                        String departname = dataArray.getJSONObject(a).get("departname").toString();
                        DocMenuData m = new DocMenuData(departid, departname, 0);
                        mMenuList1.add(m);
                    }
                    mListView1Adapter.notifyDataSetChanged();
                } else {

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
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

    //获取医生列表
    private void getDoc(String itemCode) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/clinic/getExpertList?departId=" + itemCode + "&userId=" + mUserId);
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("test", "getDepartment:" + result);
                com.alibaba.fastjson.JSONObject j = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.parse(result);
                String resultCode = j.getString("resultCode");
                String resultMsg = j.getString("resultMsg");
                if (resultCode.equals("200")) {
                    com.alibaba.fastjson.JSONArray dataArray = j.getJSONArray("data");
                    mMenuList2.clear();
                    for (int a = 0; a < dataArray.size(); a++) {
                        String userid = dataArray.getJSONObject(a).get("userid").toString();
                        String username = dataArray.getJSONObject(a).get("username").toString();
                        DocMenuData m = new DocMenuData(userid, username, 0);
                        mMenuList2.add(m);
                    }
                    mListView2Adapter.notifyDataSetChanged();
                } else {

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
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

    public String getTokenFromLocal() {
        String token = SPUtils.get(mContext, PublicUrl.TOKEN_KEY, "").toString();
        if (TextUtils.isEmpty(token)) {
            Intent intent = new Intent(mContext, LoginActivity.class);
            mContext.startActivity(intent);
            return null;
        } else {
            Log.i("test", "token: " + token);
            return token;
        }
    }

    private void setDictItemClickListener(DocMenuData menuData1, DocMenuData menuData2, DocMenuData menuData3, DocMenuData menuData4) {
        if (menuItemClickListener != null) {
            menuItemClickListener.onMenuItemClick(menuData1, menuData2, menuData3, menuData4);
        }
        dismiss();
    }

    public final void setonItemClickListener(MenuItemClickListener listener) {
        menuItemClickListener = listener;
    }

    public interface MenuItemClickListener {
        public void onMenuItemClick(DocMenuData menuData1, DocMenuData menuData2, DocMenuData menuData3, DocMenuData menuData4);
    }
}
