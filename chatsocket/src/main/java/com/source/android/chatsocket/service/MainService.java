package com.source.android.chatsocket.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.bsc.chat.commenbase.BaseConst;
import com.source.android.chatsocket.MainActivity;
import com.source.android.chatsocket.R;
import com.source.android.chatsocket.chatdb.ChatDBManager;
import com.source.android.chatsocket.entity.DataBean;
import com.source.android.chatsocket.entity.MsgEntity;
import com.source.android.chatsocket.entity.RoomEntity;
import com.source.android.chatsocket.entity.SocketConst;
import com.source.android.chatsocket.messages.AddMemberEvent;
import com.source.android.chatsocket.messages.ChatActivityStatusMessage;
import com.source.android.chatsocket.messages.ChatGroupPeopleChangeCallBackMessage;
import com.source.android.chatsocket.messages.ChatUpLoadFileCallBackMessage;
import com.source.android.chatsocket.messages.CommenSocketResponse;
import com.source.android.chatsocket.messages.MessageCallBack;
import com.source.android.chatsocket.messages.MessageEvent;
import com.source.android.chatsocket.messages.NetMessage;
import com.source.android.chatsocket.messages.NetReconnectMessage;
import com.source.android.chatsocket.messages.PersionEvent;
import com.source.android.chatsocket.messages.ServiceEvent;
import com.source.android.chatsocket.net.HttpReuqests;
import com.source.android.chatsocket.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;


import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainService extends Service {
    private String TAG = "MainService";
    Socket socket;
    List<String> roomIds;//??????????????????????????????ids
    String userId;
    String token;
    PowerManager.WakeLock wakeLock;//?????????
    ChatDBManager chatDBManager;//?????????????????????
    private boolean chatAcitvityStatus = false;

    public MainService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            roomIds = intent.getStringArrayListExtra("roomIds");
            userId = intent.getStringExtra(SocketConst.USER_ID_KEY);
            token = intent.getStringExtra(SocketConst.TOKEN_KEY);
            Log.d(TAG, "onStartCommand==>" + roomIds.size() + "userId==>" + userId);

        } catch (Exception e) {
            Log.e(TAG, "MainService getIntent err==>" + e.getMessage());
        }
        if (socket != null) {
            socket.connect();
            Log.i(TAG, "MainService  socket.connect state " + socket.connected());
        }else {
            Log.e(TAG, "MainService  socket is NULL");
        }
        if (socket!=null&&socket.connected() && (int) SPUtils.get(MainService.this, SocketConst.Socket_Register_STATUS, 0) == 0) {

            Log.e(TAG, "MainService  userRegister start");
            userRegister();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        chatDBManager = new ChatDBManager(this);
        keepSober();
        initSocket();
        EventBus.getDefault().register(this);
    }

    public void setNotifiction() {
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext()); //????????????Notification?????????
        Intent nfIntent = new Intent(this, MainActivity.class);

        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0)) // ??????PendingIntent
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher)) // ??????????????????????????????(?????????)
                .setContentTitle("Title") // ??????????????????????????????
                .setSmallIcon(R.mipmap.ic_launcher) // ??????????????????????????????
                .setContentText("??????????????????") // ?????????????????????
                .setWhen(System.currentTimeMillis()); // ??????????????????????????????
        Notification notification = builder.build(); // ??????????????????Notification
        notification.defaults = Notification.DEFAULT_SOUND; //????????????????????????
        startForeground(110, notification);// ??????????????????
    }

    public void initSocket() {
        try {
            IO.Options options = new IO.Options();
            options.reconnectionDelay = 2000;
            options.timeout = 10000;
            options.reconnection = true;
            options.path = "/BSCMessage";
            if (BaseConst.CHAT_SOCKET_URL != null) {
                Log.d(TAG, "SocketConst address==>" + BaseConst.CHAT_SOCKET_URL);
                socket = IO.socket(BaseConst.CHAT_SOCKET_URL, options);
            } else {
                return;
            }

            //socket = IO.socket("http://192.168.0.19:3000/message-001", options);


        } catch (URISyntaxException e) {
            Log.d(TAG, "" + e.getMessage());
            e.printStackTrace();
        }
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "Socket Connect");
                sendNetMsg(1);
                //??????????????????
                userRegister();
            }
        }).on("msg", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "msg==>" + args[0].toString());
                MsgEntity msgEntity = com.alibaba.fastjson.JSONObject.parseObject(args[0].toString(), MsgEntity.class);
                sendChatMsg(msgEntity);

            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.d(TAG, "SocketDisconnect==" + args[0].toString());
                sendNetMsg(0);
            }

        }).on(Socket.EVENT_PING, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (args != null) {
                    Log.i("Ping", "PING==>1");
                }
            }
        }).on(Socket.EVENT_PONG, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (args != null) {
                    Log.i("Ping", "PONG==>" + args[0].toString());
                }
            }
        }).on(Socket.EVENT_RECONNECTING, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (args != null) {
                    Log.i(TAG, "RECONNECTING==>" + args[0].toString());
                }
            }
        }).on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (args != null) {
                    Log.i(TAG, "RECONNECTING==>" + args[0].toString());
                }
            }
        }).on(Socket.EVENT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "SocketERROR==" + args[0].toString());
                sendNetMsg(0);
            }
        });
    }

    //??????????????????
    public void userRegister() {
        Log.d(TAG, "????????????==");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", userId);
            jsonObject.put("userToken", "zzw");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("register", jsonObject, new Ack() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "Register Callback" + args[0].toString());
                //??????????????????????????????????????????
                com.alibaba.fastjson.JSONObject jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(args[0].toString());
                Log.d(TAG, "registerResult==>" + jsonObject1.getString("id"));
                if (!jsonObject1.getString("id").equals("registerSuccess")) {
                    //???????????????????????????
                    Log.i(TAG, "register failed");
                    sendNetNeedReconnectMsg(0);
                    return;
                } else {
                    Log.i(TAG, "register success");
                    sendNetNeedReconnectMsg(1);
                    //???????????????????????????
                    getRooms();
                }

            }
        });
    }

    //??????socket ???????????? status 0?????? 1??????
    public void sendNetNeedReconnectMsg(int status) {
        Log.i(TAG, "????????????????????????==>" + status);
        EventBus.getDefault().post(new NetReconnectMessage(status));
        SPUtils.put(MainService.this, SocketConst.Socket_Register_STATUS, status);
    }

    //??????socket ???????????? status 0?????? 1??????
    public void sendNetMsg(int status) {
        Log.i(TAG, "????????????????????????==>" + status);
        EventBus.getDefault().post(new NetMessage(status));
        SPUtils.put(MainService.this, SocketConst.Socket_STATUS, status);
    }

    //??????????????????
    public void sendChatMsg(MsgEntity msgEntity) {
        if (msgEntity.getType().equals("toRoom")) {
            EventBus.getDefault().post(new ServiceEvent(msgEntity));
        } else if (msgEntity.getType().equals("toUser")) {
            EventBus.getDefault().post(new PersionEvent(msgEntity));
        }
    }

    //??????????????????
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMoonEvent(NetReconnectMessage netReconnectMessage) {
        Log.d(TAG, "???chatFragment????????????netReconnectMessage?????????" + netReconnectMessage.getNetStatus());
        if (netReconnectMessage.getNetStatus() == 2) {
            userRegister();
        }

    }

    //chatActivity????????????
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMoonEvent(ChatActivityStatusMessage chatActivityStatusMessage) {
        Log.d(TAG, "???chatFragment????????????chatActivityStatusMessage?????????" + chatActivityStatusMessage.isStatus());
        chatAcitvityStatus = chatActivityStatusMessage.isStatus();
    }

    //??????????????????????????????
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMoonEvent(ChatUpLoadFileCallBackMessage chatUpLoadFileCallBackMessage) {
        Log.d(TAG, "???UpLoadFileService??????????????????????????????=>" + chatUpLoadFileCallBackMessage.getId() + "status==>" + chatUpLoadFileCallBackMessage.getStatus() + "chatAcitvityStatus==>" + chatAcitvityStatus);
        JSONObject jsonObject = new JSONObject();
        String msg = "";
        try {
            jsonObject.put("url", chatUpLoadFileCallBackMessage.getUrl());//url
            jsonObject.put("thumbUrl", chatUpLoadFileCallBackMessage.getUrl());//thumbUrl
            msg = jsonObject.toString();
            Log.i(TAG, "chatUpLoadFileCallBackMessage===>" + msg);
        } catch (JSONException e) {
            Log.e(TAG, "ChatUpLoadFileCallBackMessage err==>" + e.getMessage());
        }
        MsgEntity msgEntity = MsgEntity.parse(userId, msg, chatUpLoadFileCallBackMessage.getRoomId(), "toRoom", "image");
        msgEntity.setId(chatUpLoadFileCallBackMessage.getId());
        MessageEvent messageEvent = new MessageEvent(msgEntity, userId);
        sendChatMessage(messageEvent);
    }

    //??????????????????
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMoonEvent(MessageEvent messageEvent) {
        Log.d(TAG, "???chatFragment????????????MessageEvent?????????" + messageEvent.getMsgEntity());
        sendChatMessage(messageEvent);
    }

    //????????????????????????????????????
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMoonEvent(AddMemberEvent addMemberEvent) {
        Log.d(TAG, "???chatFragment?????????????????????" + addMemberEvent.getId() + " " + addMemberEvent.getRoomId() + " " + addMemberEvent.getUserId());
        sendAddMemberMessage(addMemberEvent);
    }

    //?????????????????????????????????
    public void sendAddMemberMessage(AddMemberEvent addMemberEvent) {
        Log.i(TAG, "sendAddMemberMessage start");
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", addMemberEvent.getId());
            jsonObject.put("roomId", addMemberEvent.getRoomId());
            jsonObject.put("userId", addMemberEvent.getUserId());
        } catch (JSONException e) {
            Log.e(TAG, "sendAddMemberMessage err");
        }
        socket.emit("chatManager", jsonObject, new Ack() {
            @Override
            public void call(Object... args) {
                Log.i(TAG, "chatManager callBack==>" + args[0].toString());
                CommenSocketResponse commenSocketResponse = com.alibaba.fastjson.JSONObject.parseObject(args[0].toString(), CommenSocketResponse.class);
                Log.i(TAG, "chatManager result==>" + commenSocketResponse.getResultCode());
                try {
                    if (commenSocketResponse.getResultCode() == 200) {
                        addPeopleCallBack(commenSocketResponse.getType(), 1, jsonObject.getString("roomId"), jsonObject.getString("userId"));
                    } else {
                        addPeopleCallBack(commenSocketResponse.getType(), 0, jsonObject.getString("roomId"), jsonObject.getString("userId"));
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "chatManager change err==>" + e.getMessage());
                }
            }
        });
    }

    //????????????????????????????????? type 1?????? 0??????
    public void addPeopleCallBack(String type, int result, String roomId, String userId) {
        EventBus.getDefault().post(new ChatGroupPeopleChangeCallBackMessage(type, result, userId, roomId));
    }

    //??????????????????
    public void sendChatMessage(final MessageEvent messageEvent) {
        Log.i(TAG, "MessageEvent get id==>" + messageEvent.getMsgEntity().getId());
        final JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject.put("id", messageEvent.getMsgEntity().getId());//????????????
            jsonObject.put("type", messageEvent.getMsgEntity().getType());
            jsonObject.put("from", messageEvent.getMsgEntity().getFrom());
            jsonObject.put("to", messageEvent.getMsgEntity().getTo());
            jsonObject1.put("type", messageEvent.getMsgEntity().getMessage().getType());
            jsonObject1.put("msg", messageEvent.getMsgEntity().getMessage().getMsg());
            jsonObject.put("message", jsonObject1);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        socket.emit("msg", jsonObject, new Ack() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "msg ACK " + args[0].toString() + "jsonObject==>" + jsonObject.toString());
                String resullt = com.alibaba.fastjson.JSONObject.parseObject(args[0].toString()).getString("resultCode");
                try {
                    if (resullt.equals("200")) {
                        EventBus.getDefault().post(new MessageCallBack(messageEvent.getMsgEntity(), 1));
                        deleteSuccessMsg(jsonObject.getString("id"));
                    } else {
                        EventBus.getDefault().post(new MessageCallBack(messageEvent.getMsgEntity(), 2));
                        upDateMsg(messageEvent.getMsgEntity().getId(), 2);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "MessageCallBack err==>" + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MainService Destory");
        EventBus.getDefault().unregister(this);
        if (socket != null) {
            socket.disconnect();
            socket.close();
        }
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
        if (chatDBManager != null) {
            chatDBManager.closeDB();
        }
    }

    //??????cpu?????????
    public void keepSober() {
        PowerManager pm = (PowerManager) MainService.this.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "com.source.android.chatsocket.service.MainService");
        wakeLock.acquire();

    }

    public void getRooms() {
        HttpReuqests.getInstance().getRooms(token, userId, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String result = null;
                try {
                    result = response.body().string();
                    Log.i(TAG, "result==>" + result);
                    RoomEntity roomEntity = com.alibaba.fastjson.JSONObject.parseObject(result, RoomEntity.class);
                    if (roomEntity.getResultCode() != 200) return;
                    Log.i(TAG, "getRomms--------");
                    List<DataBean> list = roomEntity.getData();
                    roomIds.clear();
                    for (DataBean data : list) {
                        // Log.i(TAG,"join room==>"+data.getId());
                        roomIds.add(data.getId());
                    }
                    JSONObject roomIdObject;
                    if (roomIds != null && roomIds.size() > 0) {
                        for (String id : roomIds) {
                            roomIdObject = new JSONObject();
                            try {
                                roomIdObject.put("id", "joinRoom");
                                roomIdObject.put("roomId", id);
                            } catch (JSONException e) {
                                Log.e(TAG, "sendRoomID err==>" + e.getMessage());
                                e.printStackTrace();
                            }
                            socket.emit("chatManager", roomIdObject, new Ack() {
                                @Override
                                public void call(Object... args) {
                                    Log.d(TAG, "chatManager Callback==>" + args[0].toString());
                                }
                            });
                        }
                    }

                } catch (IOException e) {
                    Log.e(TAG, "getRooms err" + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "getRooms onFailure==>" + t.getMessage());
            }
        });
    }

    //???????????????????????????
    public int deleteSuccessMsg(String msgId) {
        return chatDBManager.deleteMessage(msgId);
    }

    //????????????????????????
    public void saveToDB(MsgEntity msgEntity, String userId, int status) {
        String message = com.alibaba.fastjson.JSONObject.toJSONString(msgEntity);
        chatDBManager.add(msgEntity.getId(), msgEntity.getTo(), userId, message, msgEntity.getMessage().getType(), new Date().getTime(), new Date().getTime(), status);
    }

    public void upDateMsg(String msgId, int status) {

        chatDBManager.updateMessage(msgId, status);
    }


}
