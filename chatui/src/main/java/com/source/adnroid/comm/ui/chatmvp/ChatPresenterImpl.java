package com.source.adnroid.comm.ui.chatmvp;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.source.adnroid.comm.ui.chatmvp.base.IBaseView;
import com.source.adnroid.comm.ui.chatmvp.message.DBMessageEvent;
import com.source.adnroid.comm.ui.chatmvp.message.UserMessageEvent;
import com.source.adnroid.comm.ui.entity.ChatFileEntity;
import com.source.adnroid.comm.ui.entity.MsgTypeEnum;
import com.source.android.chatsocket.entity.MsgEntity;
import com.source.android.chatsocket.entity.MsgViewEntity;
import com.source.android.chatsocket.messages.ChatActivityStatusMessage;
import com.source.android.chatsocket.messages.ChatUpLoadFileCallBackMessage;
import com.source.android.chatsocket.messages.ChatUpLoadFileMessage;
import com.source.android.chatsocket.messages.HistoryMessageEvent;
import com.source.android.chatsocket.messages.MessageCallBack;
import com.source.android.chatsocket.messages.MessageEvent;
import com.source.android.chatsocket.messages.NetMessage;
import com.source.android.chatsocket.messages.NetReconnectMessage;
import com.source.android.chatsocket.messages.ServiceEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.UUID;

public class ChatPresenterImpl implements IChatConstract.IChatPresenter {
    private String TAG = this.getClass().getSimpleName();
    private IChatConstract.IChatView mView;
    private String roomId;
    private String token;
    private String userId;
    private Context context;

    public ChatPresenterImpl(String roomId, String token, String userId, Context context) {
        this.roomId = roomId;
        this.token = token;
        this.userId = userId;
        this.context = context;
    }

    @Override
    public void getData(int userId) {

    }

    @Override
    public void sendTextMessage(MsgEntity msgEntity) {
        Log.i(TAG, "msgEntity.getType==>" + msgEntity.getMessage().getType());
        switch (msgEntity.getMessage().getType()) {
            case "text":
                MessageEvent messageEvent = new MessageEvent(msgEntity, msgEntity.getFrom());
                EventBus.getDefault().post(messageEvent);
                break;
            case "picture":
                sendFile("picture", msgEntity);
                break;
            case "gallery":
                sendFile("gallery", msgEntity);
                break;
            case "image":
                MessageEvent imageEvent = new MessageEvent(msgEntity, msgEntity.getFrom());
                EventBus.getDefault().post(imageEvent);
                break;
        }
    }

    @Override
    public void getHistoryMessage(int begin, int limit, int type) {
        Log.i(TAG, "loadHistoryFinish==>start");
        ChatModule chatModule = new ChatModule();
        chatModule.getHistoryMessage(begin, limit, roomId, token, type);
    }

    @Override
    public void getMessageFromNativeDB() {
        ChatModule chatModule = new ChatModule();
        chatModule.getMessageFromNativeDB(roomId, userId, context);
    }

    @Override
    public void getUserMessage() {
        ChatModule chatModule = new ChatModule();
        chatModule.getGroupMembersMessage(token, roomId);
    }

    @Override
    public void deleteDBMessageByID(String id) {
        ChatModule chatModule = new ChatModule();
        int status = chatModule.deleteSuccessMsg(id, context);
        if (status == 1) {
            mView.deleteMsgSuccess(id);
        } else {
            mView.deleteMsgSuccess("failed");
        }

    }

    @Override
    public void findDBMessageByID(String id) {
        ChatModule chatModule = new ChatModule();
        MsgViewEntity msgViewEntity = chatModule.getMsgById(id, context);
        mView.findMsgSuccess(msgViewEntity);
        deleteDBMessageByID(id);
    }


    //????????????
    public void sendFile(String type, MsgEntity msgEntity) {
        EventBus.getDefault().post(new ChatUpLoadFileMessage(type, msgEntity.getId(), msgEntity.getTo(), msgEntity.getMessage().getMsg(), token, "failer"));
    }



    @Override
    public void start() {
        mView.showText("sss...");
    }

    @Override
    public void attachView(IBaseView view) {
        Log.i(TAG, "attachView");
        this.mView = (IChatConstract.IChatView) view;
        EventBus.getDefault().register(this);
    }

    @Override
    public void detacheView() {
        Log.i(TAG, "detacheView");
        this.mView = null;
        EventBus.getDefault().unregister(this);

    }

    @Override
    public boolean isViewAttached() {
        return mView != null;
    }

    //??????????????????????????????
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMoonEvent(MessageCallBack messageCallBack) {
        Log.d(TAG, "???socket????????????messageCallBack??????=>" + messageCallBack.getMsg().toString() + "status==>" + messageCallBack.getStatus());
        mView.messageCallBack(messageCallBack);
    }

/*    //??????????????????????????????
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMoonEvent(ChatUpLoadFileCallBackMessage chatUpLoadFileCallBackMessage) {
        Log.d(TAG, "???service??????????????????????????????=>" + "type==" + chatUpLoadFileCallBackMessage.getType() + "  id==" + chatUpLoadFileCallBackMessage.getId() + "status==>" + chatUpLoadFileCallBackMessage.getStatus());
        switch (chatUpLoadFileCallBackMessage.getType()) {
            case "picture":
                mView.upLoadFileCallBack(chatUpLoadFileCallBackMessage);//????????????UI????????????UI
                ChatFileEntity pictureEntity = ConverToChatFileEntity(chatUpLoadFileCallBackMessage.getUrl(), chatUpLoadFileCallBackMessage.getUrl());
                String msg = JSON.toJSONString(pictureEntity);
                MsgEntity pictureMsgEntity = MsgEntity.parse(userId, msg, roomId, MsgTypeEnum.TO_ROOM.getType(), MsgTypeEnum.IMAGE_MSG.getType());
                pictureMsgEntity.setId(chatUpLoadFileCallBackMessage.getId());
                sendTextMessage(pictureMsgEntity);//?????????MainService??????socket??????
                break;
            case "gallery":
                mView.upLoadFileCallBack(chatUpLoadFileCallBackMessage);//????????????UI????????????UI
                ChatFileEntity galleryFileEntity = ConverToChatFileEntity(chatUpLoadFileCallBackMessage.getUrl(), chatUpLoadFileCallBackMessage.getUrl());
                String galleryMsg = JSON.toJSONString(galleryFileEntity);
                MsgEntity galleryEntity = MsgEntity.parse(userId, galleryMsg, roomId, MsgTypeEnum.TO_ROOM.getType(), MsgTypeEnum.IMAGE_MSG.getType());
                galleryEntity.setId(chatUpLoadFileCallBackMessage.getId());
                sendTextMessage(galleryEntity);//?????????MainService??????socket??????
                break;
            case "video":

                break;
        }

    }*/

    //??????????????????
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMoonEvent(HistoryMessageEvent historyMessageEvent) {
        Log.d(TAG, "??????????????????????????????=>" + "rooID==" + historyMessageEvent.getRoomId());
        if (isViewAttached()) {
            mView.loadHistoryFinish(historyMessageEvent.getList(), historyMessageEvent.getType());
        }
    }

    //???????????????????????????
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMoonEvent(DBMessageEvent dbMessageEvent) {
        Log.d(TAG, "??????????????????????????????=>" + "rooID==" + dbMessageEvent.getRoomId() + "msg====>" + dbMessageEvent.toString());
        if (isViewAttached()) {
            mView.loadLocalDBFinish(dbMessageEvent.getList());
        }
    }

    //???????????????????????????
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMoonEvent(UserMessageEvent userMessageEvent) {
        Log.d(TAG, "???????????????????????????????????????=>" + "rooID==" + userMessageEvent.getRoomID());
        if (isViewAttached()) {
            mView.loadUserMessageFinish(userMessageEvent.getUserMap());
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    //?????????socket?????????????????????
    public void onMoonEvent(ServiceEvent messageEvent) {
        Log.d(TAG, "???socket???????????????ServiceEvent??????==>" + messageEvent.getMsgEntity().toString());
        //roomId?????????????????????????????????
        mView.showRemoteMessage(messageEvent.getMsgEntity());
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMoonEvent(NetReconnectMessage netReconnectMessage) {
        Log.d(TAG, "???socket???????????????NetReconnectMessage??????=>" + netReconnectMessage.getNetStatus());
        mView.refereshNetReconnectStatus(netReconnectMessage);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMoonEvent(NetMessage netMessage) {
        Log.d(TAG, "???socket???????????????NetMessage??????=>" + netMessage.getNetStatus());
        mView.refereshNetStatus(netMessage);
    }
}
