/*
package com.source.adnroid.comm.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;

import com.source.adnroid.comm.ui.R;
import com.source.adnroid.comm.ui.entity.ChatFileEntity;
import com.source.adnroid.comm.ui.entity.ChatHistoryInside;
import com.source.adnroid.comm.ui.entity.ChatHistoryMessage;
import com.source.adnroid.comm.ui.entity.ChatUserGroupDetailsMessage;
import com.source.adnroid.comm.ui.entity.CommenResponse;
import com.source.adnroid.comm.ui.entity.Const;
import com.source.adnroid.comm.ui.entity.MsgTypeEnum;
import com.source.adnroid.comm.ui.interfaces.OnItemClickListener;
import com.source.adnroid.comm.ui.net.HttpReuqests;
import com.source.android.chatsocket.entity.MsgEntity;
import com.source.android.chatsocket.entity.MsgViewEntity;
import com.source.android.chatsocket.messages.ChatUpLoadFileCallBackMessage;
import com.source.android.chatsocket.messages.MessageCallBack;
import com.source.android.chatsocket.messages.MessageEvent;
import com.source.android.chatsocket.messages.NetMessage;
import com.source.android.chatsocket.messages.NetReconnectMessage;
import com.source.android.chatsocket.messages.ServiceEvent;
import com.source.android.chatsocket.utils.SPUtils;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


*/
/**
 * Created by zzw on 2018/4/2.
 *//*


public class ChatFragment extends ChatBaseFragment implements OnLoadMoreListener, OnRefreshListener, OnItemClickListener {
    private String TAG = "ChatFragment";
    private Map<String, ChatUserGroupDetailsMessage> userMap = new HashMap<String, ChatUserGroupDetailsMessage>();
    ChatMsgHandler msgHandler = new ChatMsgHandler();
    private int begin = 0;
    private int limit = 10;

    @Override//???????????????????????? ?????????????????????????????????????????????????????????
    public void initHistoryData() {
        getGroupMembersMessage();
        //?????????????????? ??????Handler

    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    //?????????socket?????????????????????
    public void onMoonEvent(ServiceEvent messageEvent) {
        Log.d(TAG, "???socket???????????????ServiceEvent??????==>" + messageEvent.getMsgEntity().toString());
        //roomId?????????????????????????????????
        messageEvent.getMsgEntity().setId(UUID.randomUUID().toString());
        MsgViewEntity msgViewEntity = ConverToMsgViewEntity(messageEvent.getMsgEntity(), "1");
        if (messageEvent.getMsgEntity().getTo().equals(roomId)) {
        */
/*    if(messageEvent.getMsgEntity().getMessage().getType().equals(MsgTypeEnum.REMOVE_FROM_ROOM.getType())){//??????
                JSONObject jsonObject= JSON.parseObject(msgViewEntity.getMessage().getMsg());
                String userId=jsonObject.getString(Const.USER_ID);
                String removeName= userMap.get(userId).getMemberName();
                Map<String,String> tempMap=new HashMap<String,String>();
                tempMap.put("removeName",removeName);
                msgViewEntity.setMap(tempMap);
                updateData(msgViewEntity);
                getGroupMembersMessage();
            }else if(messageEvent.getMsgEntity().getMessage().getType().equals(MsgTypeEnum.INVITE_JOIN_ROOM.getType())){//??????
                getGroupMembersMessage();
            }*//*

            if (messageEvent.getMsgEntity().getMessage().getType().equals(MsgTypeEnum.INVITE_JOIN_ROOM.getType())) {//??????
                getGroupMembersMessage();
            }
            updateData(msgViewEntity);

        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMoonEvent(NetReconnectMessage netReconnectMessage) {
        Log.d(TAG, "???socket???????????????NetMessage??????=>" + netReconnectMessage.getNetStatus());
        Message message = new Message();
        message.what = 5;
        message.arg1 = netReconnectMessage.getNetStatus();
        msgHandler.sendMessage(message);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMoonEvent(NetMessage netMessage) {
        Log.d(TAG, "???socket???????????????NetMessage??????=>" + netMessage.getNetStatus());
        Message message = new Message();
        message.what = 4;
        message.arg1 = netMessage.getNetStatus();
        msgHandler.sendMessage(message);
    }

    //??????????????????????????????
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMoonEvent(MessageCallBack messageCallBack) {
        Log.d(TAG, "???socket????????????messageCallBack??????=>" + messageCallBack.getMsgId() + "status==>" + messageCallBack.getStatus());
        Message message = new Message();
        message.what = 6;
        message.obj = messageCallBack;
        msgHandler.sendMessage(message);
    }

    //??????????????????????????????
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMoonEvent(ChatUpLoadFileCallBackMessage chatUpLoadFileCallBackMessage) {
        Log.d(TAG, "???service??????????????????????????????=>" + chatUpLoadFileCallBackMessage.getId() + "status==>" + chatUpLoadFileCallBackMessage.getStatus());
        updateImageClick(chatUpLoadFileCallBackMessage.getId(), chatUpLoadFileCallBackMessage.getUrl(), chatUpLoadFileCallBackMessage.getStatus());
    }

    //?????????????????????????????????
    private MsgViewEntity ConverToMsgViewEntity(MsgEntity msgEntity, String msgStatus) {
        MsgViewEntity msgViewEntity = new MsgViewEntity();
        msgViewEntity.setId(msgEntity.getId());
        msgViewEntity.setMsgStatus(msgStatus);
        msgViewEntity.setFrom(msgEntity.getFrom());
        msgViewEntity.setTo(msgEntity.getTo());
        msgViewEntity.setType(msgEntity.getType());
        msgViewEntity.setMessage(msgEntity.getMessage());
        if (userMap.containsKey(msgEntity.getFrom()) || userMap.containsKey(msgEntity.getTo())) {
            msgViewEntity.setUserName(userMap.get(msgEntity.getFrom()).getMemberName());
            msgViewEntity.setUserPhoto(userMap.get(msgEntity.getFrom()).getPhoto());
        }
        return msgViewEntity;
    }

    //???????????????????????????
    private ChatFileEntity ConverToChatFileEntity(String url, String thumUrl) {
        ChatFileEntity chatFileEntity = new ChatFileEntity();
        chatFileEntity.setThumbUrl(thumUrl);
        chatFileEntity.setUrl(url);
        return chatFileEntity;
    }

    public void updateData(MsgViewEntity msgEntity) {
        list.add(msgEntity);
        msgHandler.sendEmptyMessage(0);
    }

    //????????????
    public void sendData() {
        if (!TextUtils.isEmpty(chatEdit.getText())) {
            String myMsg = chatEdit.getText().toString();
            MsgEntity msgEntity = MsgEntity.parse(userId, myMsg, roomId, MsgTypeEnum.TO_ROOM.getType(), MsgTypeEnum.TEXT_MSG.getType());
            Log.i(TAG, "send msgEntity==>" + msgEntity.toString());

            MsgViewEntity msgViewEntity = ConverToMsgViewEntity(msgEntity, "0");
            list.add(msgViewEntity);
            Message msg = new Message();
            msg.what = 1;
            msg.obj = msgEntity;
            msgHandler.sendMessage(msg);
        } else {
            Toast.makeText(getActivity(), "?????????????????????", Toast.LENGTH_SHORT).show();
        }
        Log.i(TAG, "MsgTypeEnum=>" + MsgTypeEnum.TO_ROOM.getType());

    }

    //????????????
    public void sendData(MsgEntity tMsgEntity) {
        if (tMsgEntity != null) {
            MsgEntity msgEntity = MsgEntity.parse(userId, tMsgEntity.getMessage().getMsg(), roomId, tMsgEntity.getType(), tMsgEntity.getMessage().getType());
            Log.i(TAG, "send msgEntity==>" + msgEntity.toString());
            MsgViewEntity msgViewEntity = ConverToMsgViewEntity(msgEntity, "0");
            list.add(msgViewEntity);
            Message msg = new Message();
            msg.what = 1;
            msg.obj = msgEntity;
            msgHandler.sendMessage(msg);
        } else {
            Toast.makeText(getActivity(), "?????????????????????", Toast.LENGTH_SHORT).show();
        }
        Log.i(TAG, "MsgTypeEnum=>" + MsgTypeEnum.TO_ROOM.getType());

    }

    @Override
    public void initListener() {

        //???swipeToLoadLayout?????????????????????????????????
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        chatMessageAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onKeyListener() {
        sendData();
    }

    @Override
    public void sendMsgClick() {
        sendData();
    }

    //??????????????????(??????/??????)
    @Override
    public String sendImageClick(String type, String url) {
        String msg = JSONObject.toJSONString(ConverToChatFileEntity(url, url));
        MsgEntity msgEntity = MsgEntity.parse(userId, msg, roomId, MsgTypeEnum.TO_ROOM.getType(), type);
        Log.i(TAG, "send msgFIleEntity==>" + msgEntity.toString());
        msgEntity.getMessage().setType(MsgTypeEnum.IMAGE_MSG.getType());//UI????????????????????? ???????????????????????????????????????????????????
        MsgViewEntity msgViewEntity = ConverToMsgViewEntity(msgEntity, "0");
        list.add(msgViewEntity);
        scrollToLastMsg();
        return msgEntity.getId();
    }

    //?????????????????????????????????????????????
    @Override
    public void updateImageClick(String id, String url, String status) {
        MsgViewEntity msgEntity = null;
        for (int i = list.size(); i > 0; i--) {
            if (id == list.get(i - 1).getId()) {
                msgEntity = list.get(i - 1);
            }
        }
        if (status.equals("1")) {
            ChatFileEntity chatFileEntity = ConverToChatFileEntity(url, url);
            String fileMsg = JSONObject.toJSONString(chatFileEntity);
            if (!TextUtils.isEmpty(fileMsg)) {
                msgEntity.getMessage().setMsg(fileMsg);
            }
            if (msgEntity != null) {
                Message msg = new Message();
                msg.what = 1;
                msg.obj = msgEntity;
                msgHandler.sendMessage(msg);
            }
        } else if (status.equals("0")) {
            updateMsgStatue(id, "2");
        }

    }

    @Override
    public void reconnectClick() {
        Log.i(TAG, "chat_net_reconnect click");
        EventBus.getDefault().post(new NetReconnectMessage(2));
    }

    @Override
    public void onLoadMore() {
        Log.i(TAG, "onLoadMore");
*/
/*        for (int i = 0; i < 20; i++) {
            list.add("???????????????????????????");
        }
        chatMessageAdapter.notifyDataSetChanged();*//*

        //??????????????????????????????
        swipeToLoadLayout.setLoadingMore(false);
    }

    //??????????????????????????????
    @Override
    public void onRefresh() {
        begin += limit;
        getHistoryMessage(begin, limit, 2);
        //????????????????????????
        swipeToLoadLayout.setRefreshing(false);
    }

    @Override
    public void onClick(String type, String position) {
        //type??????????????????
        if (type.equals(MsgTypeEnum.PATIENT_MSG.getType())) {//????????????
            Bundle bundle = new Bundle();
            bundle.putString(Const.TELEMEDICINE_INFO_ID, position);
            Intent intent = new Intent(Const.PATIENT_URL);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (type.equals("imageClick")) {
            Bundle bundle = new Bundle();
            bundle.putString("Expert_ID", position);
            Intent intent = new Intent(Const.Expert_URL);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (type.equals("longClick")) {
            Log.i(TAG, "longClick====>" + position);
            showResendOrDlelteDialog(position);
        }
    }

    @Override
    public void onLongClick(int position) {

    }

    //?????????????????? type  ????????????????????????????????????????????? ???????????????????????????
    public void getHistoryMessage(int begin, int limit, final int type) {
        Log.i(TAG, "begin==>" + begin + " limit=>" + limit);
        HttpReuqests.getInstance().getSnsDiscussListByPage(token, begin, limit, roomId, new Callback<ChatHistoryMessage>() {
            @Override
            public void onResponse(Call<ChatHistoryMessage> call, Response<ChatHistoryMessage> response) {
                Log.i(TAG, "getHistory success=+>" + response.body().getData());
                ChatHistoryMessage chatHistoryMessage = response.body();
                if (chatHistoryMessage.getData() == null) {
                    return;
                }
                Log.i(TAG, "chatHistoryMessage ==>" + chatHistoryMessage.toString());
                List<ChatHistoryInside> chatHistoryInsideList = chatHistoryMessage.getData().getData();
                Log.i(TAG, "chatHistoryInsideList size==>" + chatHistoryInsideList.size());
                List<MsgViewEntity> tempMsgList = new ArrayList<MsgViewEntity>();
                for (ChatHistoryInside item : chatHistoryInsideList) {
                    String msgItem = item.getMessage();
                    try {
                        MsgEntity msgEntity = JSONObject.parseObject(msgItem, MsgEntity.class);
                        msgEntity.setId(UUID.randomUUID().toString());
                        tempMsgList.add(ConverToMsgViewEntity(msgEntity, "1"));
                    } catch (Exception e) {
                        Log.i(TAG, "msgEntity cast failed==>" + e.getMessage());
                        e.printStackTrace();
                    }
                }
                Collections.reverse(tempMsgList);
                list.addAll(0, tempMsgList);
                Message message = new Message();
                message.what = 2;
                message.obj = list;
                message.arg1 = type;
                msgHandler.sendMessage(message);
            }

            @Override
            public void onFailure(Call<ChatHistoryMessage> call, Throwable t) {
                Log.i(TAG, "getHistory failed");
            }
        });
    }

    //??????????????????????????????
    public void getMessageFromNativeDB() {
        Log.i(TAG, "getMessageFromNativeDB");
        List<String> tempList = chatDBManager.queryMsgByGroupId(userId, roomId);
        List<MsgViewEntity> tempMsgList = new ArrayList<MsgViewEntity>();
        for (String item : tempList) {
            Log.i(TAG, "item==>" + item);
            try {
                MsgEntity msgEntity = JSONObject.parseObject(item, MsgEntity.class);
                if (msgEntity.getMessage().getType().equals(MsgTypeEnum.GALLERY.getType()) || msgEntity.getMessage().getType().equals(MsgTypeEnum.PICTURE.getType())) {
                    msgEntity.getMessage().setType(MsgTypeEnum.IMAGE_MSG.getType());
                }
                tempMsgList.add(ConverToMsgViewEntity(msgEntity, "2"));
            } catch (Exception e) {
                Log.i(TAG, "msgEntity cast failed==>" + e.getMessage());
            }
        }
        list.addAll(tempMsgList);
        Message message = new Message();
        message.what = 7;
        msgHandler.sendMessage(message);
    }

    //?????????????????????????????????????????????
    private void getGroupMembersMessage() {
        HttpReuqests.getInstance().getSnsMemberByGroupId(token, roomId, new Callback<CommenResponse<List<ChatUserGroupDetailsMessage>>>() {
            @Override
            public void onResponse(Call<CommenResponse<List<ChatUserGroupDetailsMessage>>> call, Response<CommenResponse<List<ChatUserGroupDetailsMessage>>> response) {
                CommenResponse<List<ChatUserGroupDetailsMessage>> commenResponse = response.body();
                Log.i(TAG, "getGroupMembersMessage==>" + commenResponse.getResultCode());
                for (ChatUserGroupDetailsMessage item : commenResponse.getData()) {
                    // Log.i(TAG, "userId==>" + item.getUserId());
                    userMap.put(item.getUserId(), item);
                }
                msgHandler.sendEmptyMessage(3);


            }

            @Override
            public void onFailure(Call<CommenResponse<List<ChatUserGroupDetailsMessage>>> call, Throwable t) {
                Log.i(TAG, "getGroupMembersMessage  onFailure");
            }
        });
    }

    class ChatMsgHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            chatMessageAdapter.notifyDataSetChanged();
            if (msg.what == 0) {
                //??????????????????
                if (isShowLastPosition()) {
                    scrollToLastMsg();
                } else {
                    Log.d(TAG, "?????????????????????");
                    msgNotRead.setText("???????????????????????????");
                }
            } else if (msg.what == 1) {//??????????????????
                MsgEntity msgEntity = (MsgEntity) msg.obj;
                EventBus.getDefault().post(new MessageEvent(msgEntity,userId));
                chatEdit.setText("");
                scrollToLastMsg();
            } else if (msg.what == 2) {//????????????????????????
                chatMessageAdapter.notifyDataSetChanged();
                if (msg.arg1 == 1) {
                    scrollToLastMsg();
                }
            } else if (msg.what == 3) {//????????????????????????
                getHistoryMessage(0, limit, 1);
                getMessageFromNativeDB();//??????????????????????????????
            } else if (msg.what == 4) {//socket????????????
                changeView(msg.arg1);
            } else if (msg.what == 5) {//socket ??????????????????
                changeNetReconnectStatus(msg.arg1);
            } else if (msg.what == 6) {//??????????????????
                MessageCallBack messageCallBack = (MessageCallBack) msg.obj;
                updateMsgStatue(messageCallBack.getMsgId(), String.valueOf(messageCallBack.getStatus()));
            } else if (msg.what == 7) {//????????????????????????????????????
                scrollToLastMsg();
            }
        }
    }

    //??????????????????
    public void updateMsgStatue(String msgId, String status) {
       */
/* deleteSuccessMsg(msgId);?????????MainService??????*//*

        try {
            for (int i = list.size(); i > 0; i--) {
                if (list.get(i - 1).getId().equals(msgId)) {
                    Log.i(TAG, "upstatus==>" + i);
                    list.get(i - 1).setMsgStatus(status);
                    chatMessageAdapter.notifyItemChanged(i - 1);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "message update err==>" + e.getMessage());
        }
    }

    //?????????????????????????????????
    private void scrollToLastMsg() {
        recyclerView.scrollToPosition(chatMessageAdapter.getItemCount() - 1);
    }

    //??????????????????????????????
    public boolean isShowLastPosition() {
        Log.d(TAG, "lastPosition=>" + linearLayoutManager.findLastVisibleItemPosition() + "ItemCount" + chatMessageAdapter.getItemCount());
        int lastPosition = linearLayoutManager.findLastVisibleItemPosition();
        if (lastPosition == chatMessageAdapter.getItemCount() - 2) {
            return true;
        }
        return false;
    }

    //??????ID??????????????????
    public MsgEntity getMsgById(String id) {
        MsgEntity msgEntity = null;
        String chatMsg = chatDBManager.queryMsgById(id);
        Log.i(TAG, "chatMsg From DB==>" + chatMsg);
        if (chatMsg != null) {
            msgEntity = JSONObject.parseObject(chatMsg, MsgEntity.class);
            Log.i(TAG, "msgEntity toString==>" + msgEntity);
        }
        return msgEntity;
    }

    //recycle??????????????????
    public void removeItem(String id) {
        for (int i = list.size(); i > 0; i--) {
            if (id == list.get(i - 1).getId()) {
                chatMessageAdapter.notifyItemRemoved(i - 1);
                list.remove(i - 1);
                chatMessageAdapter.notifyDataSetChanged();
            }
        }
    }

    //??????or???????????? dialog
    public void showResendOrDlelteDialog(final String position) {
        final String items[] = {"??????", "??????"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("?????????");
        // builder.setMessage("???????????????????"); //????????????
        builder.setIcon(R.mipmap.ic_launcher);
        // ???????????????????????????????????????????????????????????????builder.setMessage()?????????????????????????????????
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Log.i(TAG, "selecet==>" + items[which] + which);
                if (which == 0) {
                    MsgEntity msgEntity = getMsgById(position);
                    if (msgEntity != null) {
                        removeItem(position);
                        if (msgEntity.getMessage().getType().equals(MsgTypeEnum.TEXT_MSG.getType())) {//????????????
                            sendData(msgEntity);
                        } else if (msgEntity.getMessage().getType().equals(MsgTypeEnum.GALLERY.getType())) {//????????????
                            ChatFileEntity chatFileEntity = JSONObject.parseObject(msgEntity.getMessage().getMsg(), ChatFileEntity.class);
                            String picId = sendImageClick(MsgTypeEnum.GALLERY.getType(), chatFileEntity.getUrl());
                            sendFile(MsgTypeEnum.GALLERY.getType(), picId, chatFileEntity.getUrl());
                        } else if (msgEntity.getMessage().getType().equals(MsgTypeEnum.PICTURE.getType())) {//????????????
                            ChatFileEntity chatFileEntity = JSONObject.parseObject(msgEntity.getMessage().getMsg(), ChatFileEntity.class);
                            String galleryid = sendImageClick(MsgTypeEnum.PICTURE.getType(), chatFileEntity.getUrl());
                            sendFile(MsgTypeEnum.PICTURE.getType(), galleryid, chatFileEntity.getUrl());
                        }
                    }
                } else {
                    Log.i(TAG, "????????????==>" + position);
                    deleteSuccessMsg(position);
                    removeItem(position);
                }


            }
        });
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Log.i(TAG, "selecet==>??????");
            }
        });
        builder.create().show();
    }
    //???????????????????????????
    public int deleteSuccessMsg(String msgId) {
        return chatDBManager.deleteMessage(msgId);
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume netstatus==>" + SPUtils.get(getActivity(), Const.Socket_STATUS, 0).toString() + "register status==>" + SPUtils.get(getActivity(), Const.Socket_Register_STATUS, 0).toString());
        changeView((int) SPUtils.get(getActivity(), Const.Socket_STATUS, 0));
        changeNetReconnectStatus(((int) SPUtils.get(getActivity(), Const.Socket_Register_STATUS, 0)));
    }

}

*/
