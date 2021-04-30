package com.chinabsc.telemedicine.expert.expertFragment;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alibaba.fastjson.JSONObject;
import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.entity.home.MainEduEntity;
import com.chinabsc.telemedicine.expert.expertActivity.EducationLiveActivity;
import com.chinabsc.telemedicine.expert.expertinterfaces.OnItemInterfaceClick;
import com.chinabsc.telemedicine.expert.myAdapter.MainEduAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.zw.libslibrary.mload.MLoadingDialog;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class EduItemFragment extends BaseLazyFragment implements OnItemInterfaceClick {
    private String TAG = "EduItemFragment";
    public static String EDU_TYPE;
    private String type;
    //View view;
    RecyclerView eduItemRecycle;
    List<MainEduEntity.DataBean> list = new ArrayList<>();
    public DisplayImageOptions mOptions;
    MainEduAdapter adapter;
    Dialog loadDialog;
    public EduItemFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_edu_item, container, false);
        }


        return rootView;
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
            loadDialog=MLoadingDialog.createLoadingDialog(getActivity(),"loading...");
            eduItemRecycle = rootView.findViewById(R.id.edu_item_recycle);// edu_item_recycle        mImageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
            eduItemRecycle.setLayoutManager(new LinearLayoutManager(getActivity()));
            adapter = new MainEduAdapter(getActivity(), list);
            adapter.setOnItemInterfaceClick(this);
            eduItemRecycle.setAdapter(adapter);
            getIntentMsg();

        } else {

        }
    }

    private void getIntentMsg() {
        Bundle idBundle = getArguments();
        if (idBundle != null) {
            type = idBundle.getString(EDU_TYPE);
            Log.i(TAG, "getIntentMsg = " + type);
        }
        if (type != null) {
            getLiveMsg(type);
        }
    }

    private void getLiveMsg(String type) {
        loadDialog.show();
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/edu/list");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        if (!type.equals("all")) {
            params.addBodyParameter("status", type);
        }
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "getLiveMsg==" + result);
                MainEduEntity eduEntity = JSONObject.parseObject(result, MainEduEntity.class);
                if (eduEntity != null) {
                    list.clear();
                    list.addAll(eduEntity.getData());
                    Log.i(TAG, "list size=" + list.size());
                    adapter.notifyDataSetChanged();
                }
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
                loadDialog.cancel();
            }
        });
    }

    @Override
    public void onclick(String id, String tag) {
        Intent intent = new Intent(getActivity(), EducationLiveActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(EducationLiveActivity.LIVE_ID, list.get(Integer.parseInt(id)).getLiveid());
        String hospital = list.get(Integer.parseInt(id)).getRcvsiteid() + " " + list.get(Integer.parseInt(id)).getCommondepartmentcode();
        String doctor = list.get(Integer.parseInt(id)).getExpertname() + " " + list.get(Integer.parseInt(id)).getJobtitlecode();
        bundle.putString(EducationLiveActivity.LIVE_TITLE, list.get(Integer.parseInt(id)).getCourseclass());
        bundle.putString(EducationLiveActivity.LIVE_HOSPITAL, hospital);
        bundle.putString(EducationLiveActivity.LIVE_DOCTOR, doctor);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
