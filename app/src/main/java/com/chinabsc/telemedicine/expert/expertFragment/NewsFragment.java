package com.chinabsc.telemedicine.expert.expertFragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.entity.NewsInfo;
import com.chinabsc.telemedicine.expert.expertActivity.LoginActivity;
import com.chinabsc.telemedicine.expert.expertActivity.NewsActivity;
import com.chinabsc.telemedicine.expert.expertActivity.WebActivity;
import com.chinabsc.telemedicine.expert.myAdapter.NewsListAdapter;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.utils.SPUtils;
import com.chinabsc.telemedicine.expert.utils.T;
import com.zw.libslibrary.mload.MLoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

public class NewsFragment extends BaseLazyFragment {

    private SwipeToLoadLayout mNewsSwipeToLoadLayout;
    private RecyclerView mNewsRecyclerView;
    private LinearLayoutManager mNewsLayoutManager;
    private NewsListAdapter mNewsListAdapter;
    private Handler mNewsHandler = new Handler();
    public ArrayList<NewsInfo> mNewsItem = new ArrayList<NewsInfo>();
    private boolean isLoading;
    public static String TAB_ID_KEY = "TAB_ID_KEY";
    private String mTabId = "";
    private int mNewsTotal = 0;
    private int mNewsStart = 0;
   // View view;
   Dialog loadDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_news, container, false);
        }
        return rootView;
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);

        if (isVisible) {
            loadDialog=MLoadingDialog.createLoadingDialog(getActivity(),"loading...");
            init(rootView);
            Bundle idBundle = getArguments();
            if (idBundle != null) {
                Log.e(TAG, "idBundle = " + idBundle);
                mTabId = idBundle.getString(TAB_ID_KEY);
            }
            getNewsListData();
        } else {

        }
        Log.i(TAG, "NewsFragment isVisible = " + isVisible+" mTabId="+mTabId);
    }

    private void init(View view) {
        mNewsSwipeToLoadLayout = (SwipeToLoadLayout) view.findViewById(R.id.NewsSwipeToLoadLayout);
        mNewsRecyclerView = (RecyclerView) view.findViewById(R.id.swipe_target);

        mNewsLayoutManager = new LinearLayoutManager(getActivity());
        mNewsRecyclerView.setLayoutManager(mNewsLayoutManager);
        mNewsListAdapter = new NewsListAdapter(getActivity(), mNewsItem);
        mNewsRecyclerView.setAdapter(mNewsListAdapter);

        mNewsListAdapter.setOnItemClickListener(new NewsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent it = new Intent(getActivity(), WebActivity.class);
                it.putExtra(WebActivity.TITLE_TEXT, "新闻资讯");
                it.putExtra(WebActivity.URL_ID, BaseConst.DEAULT_URL + "/mobile/news/findNewsByid?artid=" + mNewsItem.get(position).artid);
                startActivity(it);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        mNewsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.i("test", "StateChanged = " + newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.i("test", "onScrolled");

                int lastVisibleItemPosition = mNewsLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == mNewsListAdapter.getItemCount()) {
                    Log.i("test", "loading executed");
                    if (mNewsTotal > mNewsItem.size()) {
                        if (!isLoading) {
                            isLoading = true;
                            mNewsHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getNewsListData();
                                    Log.i("test", "load more completed");
                                    isLoading = false;
                                }
                            }, 300);
                        }
                    }
                }
            }
        });
    }

    private void getNewsListData() {
        loadDialog.show();
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/news/findNewListByid");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        //params.setConnectTimeout(2 * 1000);
        Log.i("test", mNewsStart + "");
        params.addQueryStringParameter("colid", mTabId);
        params.addQueryStringParameter("begin", mNewsStart + "");
        params.addQueryStringParameter("limit", "10");
        Callback.Cancelable cancelable;
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("test", result);
                try {
                    JSONObject j = new JSONObject(result);
                    if (j.has("resultCode")) {
                        String resultCode = j.getString("resultCode");
                        if (resultCode.equals("200")) {
                            if (j.has("data")) {
                                String data = j.getString("data");
                                JSONObject k = new JSONObject(data);
                                int total = 0;
                                if (k.has("total")) {
                                    total = k.getInt("total");
                                    mNewsTotal = total;
                                }
                                if (k.has("data")) {
                                    String array = k.getString("data");
                                    JSONArray newsArray = new JSONArray(array);
                                    for (int i = 0; i < newsArray.length(); i++) {
                                        JSONObject l = newsArray.getJSONObject(i);
                                        NewsInfo item = new NewsInfo();
                                        item.total = total;
                                        if (l.has("artid")) {
                                            item.artid = l.getString("artid");
                                            Log.i("item.arid", item.artid);
                                        }
                                        if (l.has("coverimage")) {
                                            item.coverimage = BaseConst.DEAULT_URL + l.getString("coverimage");
                                        }
                                        if (l.has("title")) {
                                            item.title = l.getString("title");
                                        }
                                        if (l.has("articalprofile")) {
                                            item.articalprofile = l.getString("articalprofile");
                                        }
                                        mNewsItem.add(item);
                                    }
                                    mNewsStart = mNewsStart + newsArray.length();
                                    mNewsListAdapter.notifyDataSetChanged();
                                    mNewsSwipeToLoadLayout.setRefreshing(false);
                                    mNewsListAdapter.notifyItemRemoved(mNewsListAdapter.getItemCount());
                                }
                            }
                        } else if (resultCode.equals("401")) {
                            T.showMessage(getActivity(), getString(R.string.login_timeout));
                            delToken();
                            doLogout();
                        } else {
                            T.showMessage(getActivity(), getString(R.string.api_error) + resultCode);
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
                T.showMessage(getActivity(), getString(R.string.server_error));
            }

            @Override
            public void onCancelled(CancelledException cex) {
                //Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {
                //mLoginLoadingLayout.setVisibility(View.GONE);
                loadDialog.cancel();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public String getTokenFromLocal() {
        String token = SPUtils.get(getActivity(), PublicUrl.TOKEN_KEY, "").toString();
        Log.i("Fragment getToken", token);
        if (TextUtils.isEmpty(token)) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            getActivity().startActivity(intent);
            return null;
        } else {
            return token;
        }
    }
}
