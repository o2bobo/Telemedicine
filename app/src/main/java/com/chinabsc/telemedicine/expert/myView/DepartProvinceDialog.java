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
import com.chinabsc.telemedicine.expert.myAdapter.DocMenuDialogAdapter;
import com.chinabsc.telemedicine.expert.myAdapter.DocMenuPagerAdapter;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.utils.SPUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class DepartProvinceDialog extends BaseMenuDialog {
    private String mUserId = "";
    private int mWidth;    //宽度
    private DocMenuViewPager mViewPager; //滑动viewPager
    private LinearLayout mRootView;    //需要显示的layout
    private View view1, view2, view3, view4;    //三个菜单级view
    private ListView mListView1, mListView2, mListView3, mListView4;  //每个菜单列表都是一个listView
    private DocMenuDialogAdapter mListView1Adapter, mListView2Adapter, mListView3Adapter, mListView4Adapter; //列表显示数据必须要的adapter
    private List<DocMenuData> mMenuList1, mMenuList2, mMenuList3, mMenuList4;
    private DocMenuData mMenuData1, mMenuData2, mMenuData3, mMenuData4;
    private List<View> views = new ArrayList<View>(); //数据集合
    private MenuItemClickListener menuItemClickListener;   //接口，点击监听

    public DepartProvinceDialog(Context context) {
        super(context);

        String userId = SPUtils.get(context, PublicUrl.USER_ID_KEY, "").toString();
        mUserId = userId;
        mWidth = mContext.getResources().getDisplayMetrics().widthPixels;//获取屏幕参数
        mContentView = LayoutInflater.from(context).inflate(R.layout.doc_menu_dialog, null);
        //初始化控件及对控件操作
        initViews();
        setTitle("请选择");//设置title
        getProvince();
    }

    private void initViews() {
        mRootView = (LinearLayout) findViewById(R.id.rootview);
        mViewPager = (DocMenuViewPager) findViewById(R.id.viewpager);
        mViewPager.setOffscreenPageLimit(2);//显示2页

        //为view加载layout,由于三个级的菜单都是只有一个listView，这里就只xie一个了
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view1 = inflater.inflate(R.layout.doc_menu_pager_number, null);
        view2 = inflater.inflate(R.layout.doc_menu_pager_number, null);
        view3 = inflater.inflate(R.layout.doc_menu_pager_number, null);
        view4 = inflater.inflate(R.layout.doc_menu_pager_number, null);

        //获取id
        mListView1 = (ListView) view1.findViewById(R.id.listview);
        mListView2 = (ListView) view2.findViewById(R.id.listview);
        mListView3 = (ListView) view3.findViewById(R.id.listview);
        mListView4 = (ListView) view4.findViewById(R.id.listview);

        //初始化数据列表
        mMenuList1 = new ArrayList<DocMenuData>();
        mMenuList2 = new ArrayList<DocMenuData>();
        mMenuList3 = new ArrayList<DocMenuData>();
        mMenuList4 = new ArrayList<DocMenuData>();

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

                if (views.contains(view3)) {
                    views.remove(view3);
                    mViewPager.getAdapter().notifyDataSetChanged();//立即更新adapter数据
                }
                mMenuData1 = (DocMenuData) parent.getItemAtPosition(position);
                getHospital(mMenuData1.id);
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
                if (mListView2Adapter != null) {
                    mListView2Adapter.setSelectedPos(position);
                    mListView2Adapter.setSelectedBackgroundResource(R.drawable.doc_menu_select_gray);
                }
                if (mListView3Adapter != null) {
                    mListView3Adapter.setSelectedPos(-1);
                }

                if (views.contains(view3)) {
                    views.remove(view3);
                }

                mMenuData2 = (DocMenuData) parent.getItemAtPosition(position);
                getDepartment(mMenuData2.id);
                if (mListView3Adapter == null) {
                    mListView3Adapter = new DocMenuDialogAdapter(mContext, mMenuList3);
                    mListView3Adapter.setHasDivider(false);
                    mListView3.setAdapter(mListView3Adapter);
                }

                //放入第三级菜单列表
                views.add(view3);
                mViewPager.getAdapter().notifyDataSetChanged();

                if (mViewPager.getCurrentItem() == 0) {
                    mViewPager.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mViewPager.setCurrentItem(1);//选一个
                        }
                    }, 300);
                }
            }
        });

        mListView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (mListView3Adapter != null) {
//                    mListView3Adapter.setSelectedPos(position);
//                    mListView3Adapter.setSelectedBackgroundResource(R.drawable.doc_menu_select_gray);
//                }
//
//                if (views.contains(view4)) {
//                    views.remove(view4);
//                }
//
//                mMenuData3 = (DocMenuData) parent.getItemAtPosition(position);
//                getDoc(mMenuData3.id);
//                if (mListView4Adapter == null) {
//                    mListView4Adapter = new DocMenuDialogAdapter(mContext, mMenuList4);
//                    mListView4Adapter.setHasDivider(false);
//                    mListView4.setAdapter(mListView4Adapter);
//                }
//
//                //放入第四级菜单列表
//                views.add(view4);
//                mViewPager.getAdapter().notifyDataSetChanged();
//
//                if (mViewPager.getCurrentItem() == 1) {
//                    mViewPager.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            mViewPager.setCurrentItem(2);//选一个
//                        }
//                    }, 300);
//                }
                mMenuData3 = (DocMenuData) parent.getItemAtPosition(position);
                setDictItemClickListener(mMenuData1, mMenuData2, mMenuData3);//选中点击的子菜单，去设置titleName
            }
        });

        //四级菜单
        mListView4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMenuData4 = (DocMenuData) parent.getItemAtPosition(position);
                setDictItemClickListener(mMenuData1, mMenuData2, mMenuData3);//选中点击的子菜单，去设置titleName
            }
        });

    }

    //获取省市列表
    private void getProvince() {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/clinic/provinceList");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("test", "getProvince:" + result);
                com.alibaba.fastjson.JSONObject j = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.parse(result);
                String resultCode = j.getString("resultCode");
                String resultMsg = j.getString("resultMsg");
                if (resultCode.equals("200")) {
                    com.alibaba.fastjson.JSONArray dataArray = j.getJSONArray("data");
                    for (int a = 0; a < dataArray.size(); a++) {
                        String itemCode = dataArray.getJSONObject(a).get("itemcode").toString();
                        if (!itemCode.equals("0")) {
                            String itemName = dataArray.getJSONObject(a).get("itemname").toString();
                            DocMenuData m = new DocMenuData(itemCode, itemName, 0);
                            mMenuList1.add(m);
                        }
                    }
                    mListView1Adapter.notifyDataSetChanged();
                } else {

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("test", "onError:" + ex.getMessage());
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

    //获取医院列表
    private void getHospital(String itemCode) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/clinic/getSiteList?regionCode=" + itemCode + "&userId=" + mUserId);
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("test", "getHospitalData:" + result);
                com.alibaba.fastjson.JSONObject j = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.parse(result);
                String resultCode = j.getString("resultCode");
                String resultMsg = j.getString("resultMsg");
                if (resultCode.equals("200")) {
                    com.alibaba.fastjson.JSONArray dataArray = j.getJSONArray("data");
                    mMenuList2.clear();
                    for (int a = 0; a < dataArray.size(); a++) {
                        String siteid = dataArray.getJSONObject(a).get("siteid").toString();
                        String sitename = dataArray.getJSONObject(a).get("sitename").toString();
                        DocMenuData m = new DocMenuData(siteid, sitename, 0);
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
                    mMenuList3.clear();
                    for (int a = 0; a < dataArray.size(); a++) {
                        String departid = dataArray.getJSONObject(a).get("departid").toString();
                        String departname = dataArray.getJSONObject(a).get("departname").toString();
                        DocMenuData m = new DocMenuData(departid, departname, 0);
                        mMenuList3.add(m);
                    }
                    mListView3Adapter.notifyDataSetChanged();
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
                    mMenuList4.clear();
                    for (int a = 0; a < dataArray.size(); a++) {
                        String userid = dataArray.getJSONObject(a).get("userid").toString();
                        String username = dataArray.getJSONObject(a).get("username").toString();
                        DocMenuData m = new DocMenuData(userid, username, 0);
                        mMenuList4.add(m);
                    }
                    mListView4Adapter.notifyDataSetChanged();
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

    private void setDictItemClickListener(DocMenuData menuData1, DocMenuData menuData2, DocMenuData menuData3) {
        if (menuItemClickListener != null) {
            menuItemClickListener.onMenuItemClick(menuData1, menuData2, menuData3);
        }
        dismiss();
    }

    public final void setonItemClickListener(MenuItemClickListener listener) {
        menuItemClickListener = listener;
    }

    public interface MenuItemClickListener {
        public void onMenuItemClick(DocMenuData menuData1, DocMenuData menuData2, DocMenuData menuData3);
    }
}
