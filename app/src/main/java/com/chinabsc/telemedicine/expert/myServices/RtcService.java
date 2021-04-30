package com.chinabsc.telemedicine.expert.myServices;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.application.MyApplication;
import com.chinabsc.telemedicine.expert.expertActivity.TelemedicineInfoActivity;
import com.chinabsc.telemedicine.expert.utils.IceCandidateTemp;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.utils.WebRtcClient;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.net.URISyntaxException;

public class RtcService extends Service implements WebRtcClient.RtcListener {
    public WebRtcClient webRtcClient;
    public MediaStream mLocalStream, mRemoteStream;
    String TAG = "VideoPlay";
    Socket socket;
    String userId;
    String roomId;
    NotificationManager notificationManager;//用于关闭通知
    /**
     * id不可设置为0,否则不能设置为前台service
     */
    private static final int NOTIFICATION_DOWNLOAD_PROGRESS_ID = 0x0001;
    private boolean isRemove = false;//是否需要移除
    Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onStatusChanged(PeerConnection.IceConnectionState iceConnectionState) {
        if (iceConnectionState == PeerConnection.IceConnectionState.DISCONNECTED) {
            Intent intent = new Intent();
            intent.setAction("disconnect");
            sendBroadcast(intent);
        }
        if (iceConnectionState == PeerConnection.IceConnectionState.FAILED) {
            Intent intent = new Intent();
            intent.setAction("close_video");
            sendBroadcast(intent);
        }

    }

    @Override
    public void onLocalStream(MediaStream localStream) {
        mLocalStream = localStream;
        MyApplication.hasLocalStream=true;
    }

    @Override
    public void onAddRemoteStream(MediaStream remoteStream) {
        mRemoteStream = remoteStream;
        Intent mIntent = new Intent("remoteStream");
        sendBroadcast(mIntent);
        MyApplication.hasRemoteStream = true;
    }

    public void sendStreamBroadcast() {
        Intent mIntent = new Intent("localStream");
        sendBroadcast(mIntent);

    }

    @Override
    public void onRemoveRemoteStream(MediaStream remoteStream) {
        remoteStream.videoTracks.get(0).dispose();
        MyApplication.hasRemoteStream = false;
    }

    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {
        Log.i(TAG, " --onIceCandidate()-- " + iceCandidate);
        // remoteIceCandidate = iceCandidate;
        Gson g = new Gson();
        Log.i(TAG, "转换后的 iceCandidate：" + g.toJson(iceCandidate));
        IceCandidateTemp temp = new IceCandidateTemp(iceCandidate.sdp, iceCandidate.sdpMLineIndex, iceCandidate.sdpMid);
//        Intent mIntent = new Intent("candidate");
//        mIntent.putExtra("candidate", g.toJson(temp));
//        //发送广播
//        sendBroadcast(mIntent);
        socket.emit("candidate", userId, g.toJson(temp));
    }

    @Override
    public void onRemoteObserverCreateSucess(String name, String Description) {
        Log.i(TAG, "发送answer广播");
        /*Intent mIntent = new Intent("answer");
        mIntent.putExtra("answer",Description);
        sendBroadcast(mIntent);*/
        socket.emit("answer", userId, Description);
    }

    @Override
    public void onLocalObserverCreateSucess(String name, String Description) {
       /* Intent mIntent = new Intent("offer");
        mIntent.putExtra("offer",Description);
        sendBroadcast(mIntent);*/
        socket.emit("offer", userId, Description);
    }

    public class LocalBinder extends Binder {
        public RtcService getService() {
            return RtcService.this;
        }
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "RtcService---onCreate");

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "RtcService---onStartCommand" + intent.getStringExtra("userId") + "-" + intent.getStringExtra("roomId"));
        userId = intent.getStringExtra("userId");
        roomId = intent.getStringExtra("roomId");
        webRtcClient = new WebRtcClient(RtcService.this);
        if (Build.VERSION.SDK_INT < 26) {
            createNotification();
        } else if (Build.VERSION.SDK_INT >= 26) {
            init8Nofication();
        }
        getNodeServerUrl();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind" + intent.getStringExtra("userId") + "-" + intent.getStringExtra("roomId"));
        userId = intent.getStringExtra("userId");
        roomId = intent.getStringExtra("roomId");
        webRtcClient = new WebRtcClient(RtcService.this);
        if (Build.VERSION.SDK_INT < 26) {
            createNotification();
        } else if (Build.VERSION.SDK_INT >= 26) {
            init8Nofication();
        }
        getNodeServerUrl();
        return new LocalBinder();
    }

    private void getNodeServerUrl() {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/frontend/cfginfo/getcfg");
        params.addBodyParameter("config", "teleconsultationNodeServer");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "result:" + result);
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject.getString("state").equals("success")) {
                    String videoUrl = jsonObject.getJSONObject("data").getString("url");
                    if (videoUrl.startsWith("http")) {

                    } else if (videoUrl.startsWith("/")) {
                        videoUrl= BaseConst.CHAT_DEAULT_BASE + videoUrl;
                    }
                    initSocket(videoUrl);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "onError:" + ex.getMessage() + "==" + isOnCallback);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i(TAG, "onCancelled:");
            }

            @Override
            public void onFinished() {
                Log.i(TAG, "onFinished:");
            }
        });
    }

    private void initSocket(String url) {
        IO.Options opts = new IO.Options();
        opts.path = "/webSocketServer";
       /* opts.forceNew=true;
        opts.reconnection = false;*/
        try {
            Log.i(TAG, "videourl===>" + url);
            socket = IO.socket(url, opts);
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.i(TAG, "connected");
                    socket.emit("joinRoom", userId, roomId, new Ack() {
                        @Override
                        public void call(Object... args) {
                            Log.i(TAG, "joinRoom:" + args[0] + "");
                            JSONObject jsonObject = JSONObject.parseObject(args[0].toString());
                            switch (jsonObject.getString("state")) {
                                case "error"://*信息错误*//*
                                    Log.i(TAG, "系统错误....");
                                    mHandler.post(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "系统错误！", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    break;
                                case "ok"://*加入成功*//*
                                    Log.i(TAG, "加入成功");
                                    mHandler.post(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "加入成功！", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    break;
                                case "alreadyInRoom"://*申请者冲突*//*
                                    Log.i(TAG, "alreadyInRoom");
                                    mHandler.post(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "已经在房间！", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    break;
                                case "roomFull"://*专家冲突*//*
                                    Log.i(TAG, "专家冲突");
                                    mHandler.post(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "专家冲突！", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    break;
                            }
                        }
                    });
                }
            });
            socket.on("command", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.i(TAG, "command:" + args[0] + "=" + args[1]);
                    JSONObject jsonObject = JSON.parseObject(args[1].toString());
                    switch (jsonObject.getString("command")) {
                        case "start":
                            webRtcClient.peerConnection.createOffer(webRtcClient.sdpObserver, webRtcClient.offer_sdpMediaConstraints);
                            break;
                        case "checkUser":
                            ((Ack) args[2]).call("ok");
                            break;
                        case "otherSiteOnVideo":
                            //T.showMessage(getActivity(),"此帐号在别处正在会诊");
                            break;
                        case "remoteLeftRoom":
                            //T.showMessage(getActivity(),"对方离开");
                            break;
                        case "userLeave":
                            Intent intent = new Intent("stopRTCService");
                            sendBroadcast(intent);
                            break;
                    }
                }
            });
            socket.on("offer", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.i(TAG, "offer:" + args[0] + "=" + args[1]);
                    JSONObject jsonObject = JSON.parseObject(args[1].toString());
                    if (args[0].toString() != userId) {
                        webRtcClient.peerConnection.setRemoteDescription(webRtcClient.remoteObserver, new SessionDescription(SessionDescription.Type.OFFER, jsonObject.getString("sdp")));
                    }
                }
            });
            socket.on("answer", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.i(TAG, "answer:" + args[0] + "=" + args[1]);
                    JSONObject jsonObject = JSON.parseObject(args[1].toString());
                    webRtcClient.peerConnection.setRemoteDescription(webRtcClient.remoteObserver, new SessionDescription(SessionDescription.Type.ANSWER, jsonObject.getString("sdp")));

                }
            });
            socket.on("candidate", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.i(TAG, "candidate:" + args.length + args[0] + "===" + args[1]);
                    JSONObject jsonObject = JSON.parseObject(args[1].toString());
                    if (args[0] != userId) {
                        webRtcClient.peerConnection.addIceCandidate(new IceCandidate(jsonObject.getString("sdpMid"), jsonObject.getIntValue("sdpMLineIndex"), jsonObject.getString("candidate")));
                    }
                }
            });

            socket.on("disconnect", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    webRtcClient.releaseAll();
                }
            });
            socket.on(Socket.EVENT_ERROR,new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e(TAG,"error=="+args[0].toString());
                }
            });
        } catch (URISyntaxException e) {
            Log.i(TAG, e.getMessage() + "error");
            e.printStackTrace();
        }
        socket.connect();
        Log.i(TAG, "socket connected");
    }

    /**
     * Notification
     */
    public void createNotification() {
        //使用兼容版本
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        //设置状态栏的通知图标
        builder.setSmallIcon(R.mipmap.basic_logo);
        //设置通知栏横条的图标
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.basic_back));
        //禁止用户点击删除按钮删除
        builder.setAutoCancel(false);
        //禁止滑动删除
        builder.setOngoing(true);
        //右上角的时间显示
        builder.setShowWhen(true);
        //设置通知栏的标题内容
        builder.setContentTitle("正在视频中");
        Intent intent = new Intent(this, TelemedicineInfoActivity.class);
        intent.putExtra("type", "service");
        //创建任务栈Builder
        //TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        //stackBuilder.addParentStack(VideoPlayActivity.class);
        //stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, TelemedicineInfoActivity.class), 0);
        // PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        //创建通知
        Notification notification = builder.build();
        //设置为前台服务
        startForeground(NOTIFICATION_DOWNLOAD_PROGRESS_ID, notification);
    }

    /**
     * Notification android8.0 通知
     */
    private void init8Nofication() {
        if (Build.VERSION.SDK_INT >= 26) {
            Intent intent = new Intent(this, TelemedicineInfoActivity.class);
            intent.putExtra("type", "service");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            int notificationId = 0x1234;
            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            //ChannelId为"1",ChannelName为"Channel1"
            NotificationChannel channel = new NotificationChannel("1", "Channel1", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true); //是否在桌面icon右上角展示小红点
            channel.setLightColor(Color.GREEN); //小红点颜色
            channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
            notificationManager.createNotificationChannel(channel);
            Notification.Builder builder = new Notification.Builder(this, "1"); //与channelId对应
            //icon title text必须包含，不然影响桌面图标小红点的展示
            builder.setSmallIcon(android.R.drawable.stat_notify_chat)
                    .setContentTitle("视频中")
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setContentText("点击返回")
                    .setContentIntent(pendingIntent)
                    .setNumber(1); //久按桌面图标时允许的此条通知的数量
            notificationManager.notify(notificationId, builder.build());
        }
    }


    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.i(TAG, "onRebind");
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "RtcService------onDestroy");
        webRtcClient.releaseAll();
        if (socket != null) {
            socket.off();
            socket.disconnect();
            socket.close();
        }
        if (Build.VERSION.SDK_INT < 26) {
            stopForeground(true);
        } else if (Build.VERSION.SDK_INT >= 26) {
            notificationManager.cancel(0x1234);
        }
        MyApplication.isRtcServiceRuning = false;
        MyApplication.hasRemoteStream = false;
        super.onDestroy();
    }
}
