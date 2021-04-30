package com.chinabsc.telemedicine.expert.expertFragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.entity.DataBean;
import com.chinabsc.telemedicine.expert.entity.RoomEntity;
import com.chinabsc.telemedicine.expert.expertActivity.AllianceActivity;
import com.chinabsc.telemedicine.expert.expertActivity.BusinessContactActivity;
import com.chinabsc.telemedicine.expert.expertActivity.UnionActivity;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.utils.SPUtils;
import com.source.adnroid.comm.ui.activity.UserListActivity;
import com.source.adnroid.comm.ui.chatutils.ChatSerciceUtils;
import com.source.android.chatsocket.service.MainService;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeCooperationFragment extends BaseLazyFragment {
    //View view;
    LinearLayout mAllianceLayout;
    LinearLayout mUnionLayout;
    LinearLayout mChatGroup;
    LinearLayout mConnact;

    public HomeCooperationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView==null){
            rootView = inflater.inflate(R.layout.fragment_home_cooperation, container, false);
        }

        // Inflate the layout for this fragment
        mAllianceLayout = rootView.findViewById(R.id.AllianceLayout);
        mAllianceLayout.setOnClickListener(new HomeCooperationOnClickListener());
        mUnionLayout = rootView.findViewById(R.id.UnionLayout);
        mUnionLayout.setOnClickListener(new HomeCooperationOnClickListener());
        mChatGroup = rootView.findViewById(R.id.chat_group);
        mChatGroup.setOnClickListener(new HomeCooperationOnClickListener());
        mConnact = rootView.findViewById(R.id.connact);
        mConnact.setOnClickListener(new HomeCooperationOnClickListener());
        return rootView;
    }

    class HomeCooperationOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            Intent intent;
            switch (v.getId()) {

                case R.id.AllianceLayout:
                    intent = new Intent(getActivity(), AllianceActivity.class);
                    startActivity(intent);
                    break;
                case R.id.UnionLayout:
                    intent = new Intent(getActivity(), UnionActivity.class);
                    startActivity(intent);
                    break;

                case R.id.connact:
                    intent = new Intent(getActivity(), BusinessContactActivity.class);
                    startActivity(intent);
                    break;

                case R.id.chat_group:
                    Log.i("MainService", " status==>" + ChatSerciceUtils.isServiceRunning(getActivity(), "com.source.android.chatsocket.service.MainService"));
                    //如果服务没有启动那么启动服务
                    if (!ChatSerciceUtils.isServiceRunning(getActivity(), "com.source.android.chatsocket.service.MainService")) {
                        initChatSocket();//尝试启动服务
                    }
                    Intent intent1 = new Intent();
                    intent1.setClass(getActivity(), UserListActivity.class);
                    intent1.putExtra("userId", SPUtils.get(getActivity(), PublicUrl.USER_ID_KEY, "").toString());
                    intent1.putExtra(PublicUrl.TOKEN_KEY, getTokenFromLocal());
                    startActivity(intent1);
                    break;
            }
        }
    }

    private void initChatSocket() {
        // TODO:加入超时判断重连机制
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/snsgroup/getSnsGroupListByType");
        //params.addHeader("authorization", getTokenFromLocal());
        Log.i("MainActivity", "getTokenFromLocal==>" + getTokenFromLocal() + "userId==>" + SPUtils.get(getActivity(), PublicUrl.USER_ID_KEY, "").toString());
        params.addHeader("Authorization", getTokenFromLocal());
        params.addBodyParameter("userId", SPUtils.get(getActivity(), PublicUrl.USER_ID_KEY, "").toString());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("MainActivity", "result==>" + result.toString());
                List<String> roomIds = new ArrayList<String>();
                try {
                    RoomEntity roomEntity = com.alibaba.fastjson.JSONObject.parseObject(result, RoomEntity.class);
                    List<DataBean> list = roomEntity.getData();
                    for (DataBean data : list) {
                        roomIds.add(data.getId());
                    }
                } catch (Exception e) {
                    Log.e("MainActivity", "" + e.getMessage());
                }
                Intent intent = new Intent();
                intent.putStringArrayListExtra("roomIds", (ArrayList<String>) roomIds);
                intent.putExtra(PublicUrl.TOKEN_KEY, SPUtils.get(getActivity(), PublicUrl.TOKEN_KEY, "").toString());
                intent.putExtra(PublicUrl.USER_ID_KEY, SPUtils.get(getActivity(), PublicUrl.USER_ID_KEY, "").toString());
                intent.setClass(getActivity(), MainService.class);
                getActivity().startService(intent);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "initChatSocket onError==>" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e(TAG, "onCancelled");
            }

            @Override
            public void onFinished() {

            }
        });

    }
}
