package com.source.adnroid.comm.ui.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.v7.app.AlertDialog;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.source.adnroid.comm.ui.R;

import com.source.adnroid.comm.ui.adapter.UserSharedAdapter;
import com.source.adnroid.comm.ui.chatview.ToastUtil;

import com.source.adnroid.comm.ui.entity.ChatTypeEntity;
import com.source.adnroid.comm.ui.entity.ChatTypeItem;
import com.source.adnroid.comm.ui.entity.ChatTypeItemEntity;
import com.source.adnroid.comm.ui.entity.Const;
import com.source.adnroid.comm.ui.entity.MessagePatientEntity;
import com.source.adnroid.comm.ui.entity.MsgTypeEnum;
import com.source.adnroid.comm.ui.entity.PatientEntity;
import com.source.adnroid.comm.ui.entity.RoomEntity;
import com.source.adnroid.comm.ui.entity.UrlEntity;
import com.source.adnroid.comm.ui.interfaces.OnUserListItemClickListener;
import com.source.adnroid.comm.ui.net.HttpReuqests;

import com.source.android.chatsocket.entity.MsgEntity;
import com.source.android.chatsocket.messages.MessageCallBack;
import com.source.android.chatsocket.messages.MessageEvent;
import com.source.android.chatsocket.messages.NetMessage;
import com.source.android.chatsocket.messages.NetReconnectMessage;
import com.source.android.chatsocket.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserSharedListActivity extends UserShareBaseActivity implements OnUserListItemClickListener, OnLoadMoreListener, OnRefreshListener {
    private String TAG = "UserSharedListActivity";
    ImageView addButton;
    MsgHandler msgHandler = new MsgHandler(this);
    //UserListAdapter userListAdapter;
    UserSharedAdapter userSharedAdapter;
    private String UserMessage = "";//????????????
    private String UserSex = "";//????????????
    private String UserPatientMessage = "";//????????????
    private String TelemedicineId = "";//??????ID
    private String url = "";
    private String type;//"????????????"
    private boolean Flag = false;//????????????????????????????????????????????????????????????????????????
    String mRoomName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        EventBus.getDefault().register(this);
        addButton = findViewById(R.id.chat_create_newgroup);
        addButton.setVisibility(View.INVISIBLE);
        initIntentMsg();
        initBaseView();
        setTitleName("??????????????????");
        initAdapterAndListener();
        getSPMessage();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        initChatType();
        initData();
        getSPMessage();
    }

    private void initIntentMsg() {

        type = getIntent().getType();
        Log.i(TAG, "type==>" + type);
        switch (type) {

            case "patientshared":
                TelemedicineId = getIntent().getStringExtra(Const.TELEMEDICINE_INFO_ID);
                getPatientMsg();
                break;
            case "url":
                url = getIntent().getStringExtra(Const.SHARED_URL);
                if (url==null|| TextUtils.isEmpty(url)){
                    return;
                }
                Flag = true;
                break;
        }
        Log.i(TAG, "type==>" + type + "TelemedicineId=>" + TelemedicineId + "Userid=>" + userId + "token=>" + token);

    }

    @Override
    public void initAdapterAndListener() {
        userSharedAdapter = new UserSharedAdapter(list);
        //???recyclerView???????????????
        recyclerView.setAdapter(userSharedAdapter);
        swipeToLoadLayout.setOnRefreshListener(this);
        userSharedAdapter.setOnItemClickListener(this);
    }


    @Override
    public void OnTabItemClick() {
        if (selectedType == 0) {
            initData();
        } else {
            Log.i(TAG, "mTabItem code=>" + mTabItem.get(selectedType).getCode());
            upDateChat(mTabItem.get(selectedType).getCode());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    //????????????????????????
    private void getPatientMsg() {
        HttpReuqests.getInstance().getTelemedicineInfo(token, TelemedicineId, new Callback<PatientEntity>() {
            @Override
            public void onResponse(Call<PatientEntity> call, Response<PatientEntity> response) {
                PatientEntity patientEntity = response.body();
                Log.d(TAG, "getPatientMsg success==>" + patientEntity.getData().getDoctorName());
                UserMessage = patientEntity.getData().getPatientName();
                UserSex = patientEntity.getData().getPatientGender();
                UserPatientMessage = patientEntity.getData().getDiagnosis().toString();
                Flag = true;
            }

            @Override
            public void onFailure(Call<PatientEntity> call, Throwable t) {
                Log.d(TAG, "getPatientMsg failed==>");
            }
        });
    }

    //??????????????????
    @Override
    public void onClick(String roomId, String roomName) {
        if (Flag) {
            Log.i(TAG, "UserSharedListActivity onClick roomId==>" + roomId + "roomName=>" + roomName);
            mRoomName = roomName;
            this.roomId = roomId;
            switch (type) {
                case "patientshared":
                    if (TelemedicineId==null|| TextUtils.isEmpty(TelemedicineId)){
                        return;
                    }
                    MessagePatientEntity messagePatientEntity = new MessagePatientEntity();
                    messagePatientEntity.setPatientId(TelemedicineId);//??????????????? PatientId
                    messagePatientEntity.setUserMessage(UserMessage);//???????????????????????????
                    messagePatientEntity.setUserSex(UserSex);
                    messagePatientEntity.setUserPatientMessage(UserPatientMessage);//???????????????????????????;
                    String msg = JSON.toJSONString(messagePatientEntity);
                    Log.i(TAG, "msg=>" + msg);
                    MsgEntity msgEntity = MsgEntity.parse(userId, msg, roomId, MsgTypeEnum.TO_ROOM.getType(), MsgTypeEnum.PATIENT_MSG.getType());
                    Log.i(TAG, "msgEntity==>" + msgEntity);
                    EventBus.getDefault().post(new MessageEvent(msgEntity, userId));
                    break;
                case "url":
                    UrlEntity urlEntity = new UrlEntity();
                    urlEntity.setUrl(url);
                    String urlMsg = JSON.toJSONString(urlEntity);
                    Log.i(TAG, "urlMsg=>" + urlMsg);
                    MsgEntity urlMsgEntity = MsgEntity.parse(userId, urlMsg, roomId, MsgTypeEnum.TO_ROOM.getType(), MsgTypeEnum.Url.getType());
                    Log.i(TAG, "msgEntity==>" + urlEntity);
                    EventBus.getDefault().post(new MessageEvent(urlMsgEntity, userId));
                    break;

            }


        }

    }

    @Override
    public void onLongClick(int position) {

    }


    //???????????????????????????
    private void initChatType() {
        ChatTypeItem item = new ChatTypeItem();
        item.setName("??????");
        item.setCode("");
        mTabItem.add(item);
        HttpReuqests.getInstance().getSnsGroupTypeByUserId(token, userId, new Callback<ChatTypeEntity>() {
            @Override
            public void onResponse(Call<ChatTypeEntity> call, Response<ChatTypeEntity> response) {
                ChatTypeEntity chatTypeEntity = response.body();
                Log.i(TAG, "getTypeListInfo success===>" + chatTypeEntity.toString());
                List<ChatTypeItemEntity> list = new ArrayList<>();
                list.addAll(response.body().getData());
                for (ChatTypeItemEntity dataBean : list) {
                    if (dataBean != null) {
                        ChatTypeItem item = new ChatTypeItem();
                        item.setName(dataBean.getItemname());
                        item.setCode(dataBean.getItemid());
                        mTabItem.add(item);
                    }
                }
                mTabSize = mTabItem.size();
                msgHandler.sendEmptyMessage(2);

            }

            @Override
            public void onFailure(Call<ChatTypeEntity> call, Throwable t) {
                Log.i(TAG, "getTypeListInfo onFailure");
            }
        });


    }

    //?????????????????????
    private void initData() {
        HttpReuqests.getInstance().getSnsGroupListByType(token, "", userId, new Callback<RoomEntity>() {
            @Override
            public void onResponse(Call<RoomEntity> call, Response<RoomEntity> response) {
                RoomEntity roomEntity = response.body();
                Log.d(TAG, "getSnsGroupList success");
                Message msg = new Message();
                msg.what = 1;
                msg.obj = roomEntity;
                msgHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<RoomEntity> call, Throwable t) {
                Log.d(TAG, "getSnsGroupList onFailure" + t.getMessage());
            }
        });
    }

    //?????????????????????
    private void upDateChat(String grouptype) {
        Log.i(TAG, "upDateChat==");
        HttpReuqests.getInstance().getSnsGroupListByType(token, grouptype, userId, new Callback<RoomEntity>() {
            @Override
            public void onResponse(Call<RoomEntity> call, Response<RoomEntity> response) {
                Log.d(TAG, "getSnsGroupList success");
                RoomEntity roomEntity = response.body();
                Message msg = new Message();
                msg.what = 3;
                msg.obj = roomEntity;
                msgHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<RoomEntity> call, Throwable t) {
                Log.d(TAG, "getSnsGroupList onFailure" + t.getMessage());
            }
        });
    }

    @Override
    public void onLoadMore() {
        swipeToLoadLayout.setLoadingMore(false);
    }

    @Override
    public void onRefresh() {
        swipeToLoadLayout.setRefreshing(false);
    }

    static class MsgHandler extends Handler {
        WeakReference<UserSharedListActivity> mActivityReference;

        MsgHandler(UserSharedListActivity activity) {
            mActivityReference = new WeakReference<UserSharedListActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mActivityReference == null) {
                return;
            }
            if (msg.what == 1) {//?????????????????????
                mActivityReference.get().list.clear();
                try {
                    mActivityReference.get().list.addAll(((RoomEntity) msg.obj).getData());
                    mActivityReference.get().userSharedAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.e(mActivityReference.get().TAG, mActivityReference.get().TAG + "==>" + e.getMessage());
                }
            } else if (msg.what == 2) {//???????????????????????????
                mActivityReference.get().list.clear();
                mActivityReference.get().setTabData();
            } else if (msg.what == 3) {//???????????????????????????
                mActivityReference.get().list.clear();
                mActivityReference.get().userSharedAdapter.notifyDataSetChanged();
                try {
                    mActivityReference.get().list.addAll(((RoomEntity) msg.obj).getData());
                    mActivityReference.get().userSharedAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.e(mActivityReference.get().TAG, mActivityReference.get().TAG + "==>" + e.getMessage());
                }
            } else if (msg.what == 4) {
                Log.i(mActivityReference.get().TAG, "4 change status==>" + msg.arg1);
                mActivityReference.get().changeNetStatus(msg.arg1);
            } else if (msg.what == 5) {
                Log.i(mActivityReference.get().TAG, "5 change reconnect status==>" + msg.arg1);
                mActivityReference.get().changeNetReconnectStatus(msg.arg1);
            } else if (msg.what == 6) {
                // showDialog(getParent(),"????????????");
                Toast.makeText(mActivityReference.get(), "????????????", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mActivityReference.get(), ChatActivity.class);
                Log.i(mActivityReference.get().TAG, "Intent roomId=>" + mActivityReference.get().roomId +
                        "userId=>" + mActivityReference.get().userId + "token=>" + mActivityReference.get().token + "roomName" + mActivityReference.get().mRoomName
                );
                intent.putExtra(Const.ROOM_ID, mActivityReference.get().roomId);
                intent.putExtra(Const.USER_ID, mActivityReference.get().userId);
                intent.putExtra(Const.TOKEN_KEY, mActivityReference.get().token);
                intent.putExtra(Const.ROOM_NAME, mActivityReference.get().mRoomName);
                mActivityReference.get().startActivity(intent);
                mActivityReference.get().finish();

            } else if (msg.what == 7) {
                // showDialog(UserSharedListActivity.this,"??????????????????????????????");
                Toast.makeText(mActivityReference.get(), "????????????", Toast.LENGTH_SHORT).show();
                mActivityReference.get().finish();
            }
        }
    }

    //????????????
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMoonEvent(MessageCallBack messageCallBack) {
        Log.d(TAG, "????????????" + messageCallBack.getStatus() + "netStatus==>" + SPUtils.get(UserSharedListActivity.this, Const.Socket_STATUS, 0) + "userRegisterStatus==>" + SPUtils.get(UserSharedListActivity.this, Const.Socket_Register_STATUS, 0));
        if (messageCallBack.getStatus() == 0) {
            return;
        }
        if ((int) SPUtils.get(UserSharedListActivity.this, Const.Socket_STATUS, 0) == 1 && (int) SPUtils.get(UserSharedListActivity.this, Const.Socket_Register_STATUS, 0) == 1) {
            Message message = new Message();
            message.what = 6;
            msgHandler.sendMessage(message);
        } else {
            msgHandler.sendEmptyMessage(7);
        }
    }

    //?????????socket ??????????????????
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMoonEvent(NetReconnectMessage netReconnectMessage) {
        Log.d(TAG, "???socket???????????????netReconnectMessage??????=>" + netReconnectMessage.getNetStatus());
        Message msg = new Message();
        msg.what = 5;
        msg.arg1 = netReconnectMessage.getNetStatus();
        msgHandler.sendMessage(msg);
    }

    //?????????socket??????
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMoonEvent(NetMessage netMessage) {
        Log.d(TAG, "???socket???????????????NetMessage??????=>" + netMessage.getNetStatus());
        Message msg = new Message();
        msg.what = 4;
        msg.arg1 = netMessage.getNetStatus();
        msgHandler.sendMessage(msg);
    }

    //???????????????AlertDialog
    private void showDialog(Context context, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("????????????");
        builder.setMessage(msg);
        builder.setPositiveButton("??????",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    public void createNewGroup(View v) {

    }
}
