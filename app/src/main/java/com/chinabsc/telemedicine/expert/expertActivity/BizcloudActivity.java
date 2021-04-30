package com.chinabsc.telemedicine.expert.expertActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.entity.BizItem;
import com.chinabsc.telemedicine.expert.myAdapter.BizAdapter;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.utils.T;

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

@ContentView(R.layout.activity_bizcloud)
public class BizcloudActivity extends BaseActivity {

    public static String TELEMEDICINE_INFO_ID = "TELEMEDICINE_INFO_ID";
    public String mTelemedicineInfoId = "";

    @ViewInject(R.id.BizSwipeToLoadLayout)
    private SwipeToLoadLayout mBizSwipeToLoadLayout;
    @ViewInject(R.id.swipe_target)
    private RecyclerView mBizRecyclerView;
    private LinearLayoutManager mBizLayoutManager;
    private boolean isLoading;
    private int mBizTotal = 0;
    private int mBizStart = 0;
    private BizAdapter mBizAdapter;
    private Handler mBizHandler = new Handler();

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

    public ArrayList<BizItem> mBizItem = new ArrayList<BizItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            mTelemedicineInfoId = bundle.getString(TELEMEDICINE_INFO_ID);
            Log.i("Bizcloud Bundle", "Telemedicine" + mTelemedicineInfoId);
        } else {
            Log.i("Bizcloud Bundle", "bundle == null");
            finish();
        }

        init();

        mBizTotal = 0;
        mBizStart = 0;
        mBizItem.clear();

        getBiz(mTelemedicineInfoId);
    }

    private void init() {
        mBizLayoutManager = new LinearLayoutManager(this);
        mBizRecyclerView.setLayoutManager(mBizLayoutManager);
        mBizAdapter = new BizAdapter(this, mBizItem);
        mBizRecyclerView.setAdapter(mBizAdapter);

        mBizRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int Biztate) {
                super.onScrollStateChanged(recyclerView, Biztate);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = mBizLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == mBizAdapter.getItemCount()) {
                    if (mBizTotal > mBizItem.size()) {
                        if (!isLoading) {
                            isLoading = true;
                            mBizHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getBiz(mTelemedicineInfoId);
                                    Log.d("test", "load more completed");
                                    isLoading = false;
                                }
                            }, 300);
                        }
                    }
                }
            }
        });

        mBizAdapter.setOnItemClickListener(new BizAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }

            @Override
            public void onItemChildClick(int position, String tag) {
                if (tag.equals("HistoryView")) {
                    if (!TextUtils.isEmpty(mBizItem.get(position).history) && mBizItem.get(position).history.length() > 150)
                        showMessageDialog("病历摘要", mBizItem.get(position).history);
                } else if (tag.equals("DiagnosisView")) {
                    if (!TextUtils.isEmpty(mBizItem.get(position).diagnosis) && mBizItem.get(position).diagnosis.length() > 150)
                        showMessageDialog("临床诊断", mBizItem.get(position).diagnosis);
                } else if (tag.equals("DescriptionView")) {
                    if (!TextUtils.isEmpty(mBizItem.get(position).description) && mBizItem.get(position).description.length() > 150)
                        showMessageDialog("影像表现", mBizItem.get(position).description);
                } else if (tag.equals("OpinionsView")) {
                    if (!TextUtils.isEmpty(mBizItem.get(position).opinions) && mBizItem.get(position).opinions.length() > 150)
                        showMessageDialog("影像诊断", mBizItem.get(position).opinions);
                } else if (tag.equals("InstanceClickText")) {
                    Intent it = new Intent(BizcloudActivity.this, WebActivity.class);
                    it.putExtra(WebActivity.URL_ID, mBizItem.get(position).viewerUrl);
                    it.putExtra(WebActivity.TITLE_VISIBILITY, 1);
                    startActivity(it);
                }
            }
        });
    }

    private void showMessageDialog(String title, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BizcloudActivity.this);
        builder.setTitle(title);
        builder.setMessage(text);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void getBiz(String tId) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/clinic/emr/" + tId + "/bizcloud");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addQueryStringParameter("begin", mBizStart + "");
        params.addQueryStringParameter("limit", "10");
        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("getBizcloud url", BaseConst.DEAULT_URL + "/mobile/clinic/emr/" + mTelemedicineInfoId + "/bizcloud");
                Log.i("getBizcloud onSuccess", result);
                parseJson(result);
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
            }
        });
    }

    private void parseJson(String result) {
        ArrayList<BizItem> itemList = new ArrayList<BizItem>();
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
                            mBizTotal = total;
                        }
                        if (k.has("data")) {
                            String array = k.getString("data");
                            JSONArray newsArray = new JSONArray(array);
                            for (int i = 0; i < newsArray.length(); i++) {
                                JSONObject l = newsArray.getJSONObject(i);
                                BizItem item = new BizItem();
                                item.total = total;
                                if (l.has("studyTime")) {
                                    String studyTime = l.getString("studyTime");
                                    if (!studyTime.equals("null")) {
                                        item.studyTime = studyTime;
                                    } else {
                                        item.studyTime = "";
                                    }
                                }
                                if (l.has("modality")) {
                                    String modality = l.getString("modality");
                                    if (!modality.equals("null")) {
                                        item.modality = modality;
                                    } else {
                                        item.modality = "";
                                    }
                                }
                                if (l.has("bodyPart")) {
                                    String bodyPart = l.getString("bodyPart");
                                    if (!bodyPart.equals("null")) {
                                        item.bodyPart = bodyPart;
                                    } else {
                                        item.bodyPart = "";
                                    }
                                }
                                if (l.has("instanceNum")) {
                                    String instanceNum = l.getString("instanceNum");
                                    if (!instanceNum.equals("null")) {
                                        item.instanceNum = instanceNum;
                                    } else {
                                        item.instanceNum = "";
                                    }
                                }
                                if (l.has("viewerUrl")) {
                                    String viewerUrl = l.getString("viewerUrl");
                                    if (!viewerUrl.equals("null")) {
                                        item.viewerUrl = viewerUrl;
                                    } else {
                                        item.viewerUrl = "";
                                    }
                                }
                                if (l.has("history")) {
                                    String history = l.getString("history");
                                    if (!history.equals("null")) {
                                        item.history = history;
                                    } else {
                                        item.history = "";
                                    }
                                }
                                if (l.has("diagnosis")) {
                                    String diagnosis = l.getString("diagnosis");
                                    if (!diagnosis.equals("null")) {
                                        item.diagnosis = diagnosis;
                                    } else {
                                        item.diagnosis = "";
                                    }
                                }
                                if (l.has("description")) {
                                    String description = l.getString("description");
                                    if (!description.equals("null")) {
                                        item.description = description;
                                    } else {
                                        item.description = "";
                                    }
                                }
                                if (l.has("opinions")) {
                                    String opinions = l.getString("opinions");
                                    if (!opinions.equals("null")) {
                                        item.opinions = opinions;
                                    } else {
                                        item.opinions = "";
                                    }
                                }
                                if (l.has("expertName")) {
                                    String expertName = l.getString("expertName");
                                    if (!expertName.equals("null")) {
                                        item.expertName = expertName;
                                    } else {
                                        item.expertName = "";
                                    }
                                }
                                if (l.has("status")) {
                                    String status = l.getString("status");
                                    if (!status.equals("null")) {
                                        item.status = status;
                                    } else {
                                        item.status = "";
                                    }
                                }
                                mBizItem.add(item);
                            }
                            mBizStart = mBizStart + newsArray.length();
                            mBizAdapter.notifyDataSetChanged();
                            mBizSwipeToLoadLayout.setRefreshing(false);
                            mBizAdapter.notifyItemRemoved(mBizAdapter.getItemCount());
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
