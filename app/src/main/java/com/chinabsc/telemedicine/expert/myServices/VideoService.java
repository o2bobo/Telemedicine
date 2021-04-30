package com.chinabsc.telemedicine.expert.myServices;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import com.chinabsc.telemedicine.expert.utils.SPUtils;
import com.chinabsc.telemedicine.expert.utils.T;
import com.chinabsc.telemedicine.expert.videoActivities.IceCandidateTemp;
import com.chinabsc.telemedicine.expert.videoActivities.SessionDescriptionTemp;

import com.chinabsc.telemedicine.expert.utils.PublicUrl;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.CameraEnumerationAndroid;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class VideoService extends Service {
    String TAG = "VideoService";
    String TAG1 = "VideoStream";
    String Name = "";
    String roomId = "";
    public String AUDIO_TRACK_ID = "commyaudio";
    public String VIDEO_TRACK_ID = "commyvideo";
    public String LOCAL_MEDIA_STREAM_ID = "commyvideostream";
    Socket socket;
    PeerConnectionFactory peerConnectionFactory;//工厂
    PeerConnection peerConnection;
    PCObserver observer = new PCObserver();
    SDPObserver sdpObserver = new SDPObserver();
    RemoteObserver remoteObserver = new RemoteObserver();
    IceCandidate remoteIceCandidate;
    VideoCapturerAndroid videoCapturer;
    MediaConstraints sdpMediaConstraints = new MediaConstraints();//本地会话描述
    VideoSource videoSource;
    VideoTrack localVideoTrack, videoTrack;
    AudioSource audioSource;
    AudioTrack localAudioTrack;
    MediaStream localMediaStream;
    VideoRenderer localRenderer = null;
    VideoRenderer remoteRenderer = null;
    RendererCommon.ScalingType scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FILL;
    private MyBinder mBinder = new MyBinder();
    boolean LocalIsFront = false;
    Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate() {
        super.onCreate();
        Name = (String) SPUtils.get(getApplication(), PublicUrl.USER_NAME_KEY, "");
        if (Name == null) {
            Name = "临时用户";
        }
        Log.i(TAG, "onCreate() executed");

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        public VideoService getService() {
            return VideoService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand() executed");
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 加入房间
     */
    public void init() {
        //socket = IO.socket("http://218.247.6.82:3000");
        try {
            IO.Options opts = new IO.Options();
            opts.path = "/webSocketServer";
            //socket=IO.socket("http://192.168.0.31:3000/n2");
            Log.i(TAG, "videourl 1==>" + SPUtils.get(getApplication(), "videoUrl", ""));
            String videoUrl = (String) SPUtils.get(getApplication(), "videoUrl", "");
            Log.i(TAG, "videoUrl 2===" + videoUrl);
            if (videoUrl != null) {
                //socket = IO.socket("http://218.247.6.82:3000/n2");
                socket = IO.socket(videoUrl, opts);
            } else {
                Log.i(TAG, "连接失败");
            }
        } catch (URISyntaxException e) {
            Log.i(TAG, "soceket==" + e.toString());

        }
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.i(TAG, "connected_Name_roomId-" + Name + "_" + roomId);
                socket.emit("joinRoom", Name, roomId, new Ack() {
                    @Override
                    public void call(Object... args) {
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
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.i(TAG, "连接已经断开");
            }
        }).on("command", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.i(TAG, "command_args==" + args[0] + "***" + args[1]);
                String user = (String) args[0];
                JSONObject jsonObject = JSONObject.parseObject(args[1].toString());
                switch (jsonObject.getString("command")) {
                    case "start":
                        initPeerConnection();
                        peerConnection.createOffer(sdpObserver, sdpMediaConstraints);
                        break;
                    case "checkUser":
                        if (args[2] instanceof Ack) {
                            Ack ack = (Ack) args[2];
                            ack.call("ok");
                        } else {
                            Log.i(TAG, "args[2] is not Ack");
                        }
                        break;
                    case "otherSiteOnVideo":
                        break;
                    case "remoteLeftRoom":
                        break;
                }

                   /*  if (user.equals(Name)) {
                        Log.i(TAG, "user=" + user + "--Name=" + Name);
                        //Log.i(TAG,"lianjieqianmiaoshu"+  peerConnection.getLocalDescription().description.toString());
                        peerConnection.createOffer(sdpObserver, sdpMediaConstraints);
                    }*/
            }
        }).on("offer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (!args[0].equals(Name)) {
                    Log.i(TAG, "设置远程远程Decription" + args[1]);
                    JSONObject jsonObject = (JSONObject) JSONObject.parse((String) args[1]);
                    initPeerConnection();
                    peerConnection.setRemoteDescription(remoteObserver, new SessionDescription(SessionDescription.Type.OFFER, jsonObject.getString("sdp")));
                    //peerConnection.setRemoteDescription(remoteObserver, new SessionDescription(SessionDescription.Type.OFFER, changToDescription(jsonObject.getString("sdp"))));
                }
            }
        }).on("answer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (!args[0].equals(Name)) {
                    Log.i(TAG, "要设置RemoteDescription是" + args[1]);
                    JSONObject jsonObject = (JSONObject) JSONObject.parse((String) args[1]);
                    Log.i("sdp", "sdp" + jsonObject.getString("sdp"));
                    peerConnection.setRemoteDescription(sdpObserver, new SessionDescription(SessionDescription.Type.ANSWER, (String) jsonObject.getString("sdp")));
                }
            }
        }).on("candidate", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.i(TAG, "candidate方法已经回调");
                if (!args[1].equals(Name) && args[1] != null && !args[1].equals("") && !args[1].equals("null")) {
                    try {
                        Log.i(TAG, "发回的IceCandidate" + args[1]);
                        Gson g = new GsonBuilder().create();
                        IceCandidateTemp iceTemp = g.fromJson((String) args[1], IceCandidateTemp.class);
                        Log.i(TAG, "参数" + iceTemp.sdpMid + "--" + iceTemp.sdpMLineIndex + "--" + iceTemp.candidate);
                        remoteIceCandidate = new IceCandidate(iceTemp.sdpMid, iceTemp.sdpMLineIndex, iceTemp.candidate);
                        Log.i(TAG, " --当前IceCandidate-- " + g.toJson(remoteIceCandidate));
                        peerConnection.addIceCandidate(remoteIceCandidate);
                        Log.i(TAG, " --IceCandidate添加完成-- ");
                    } catch (Exception e) {
                        Log.i(TAG, "candidate err");
                    }
                }
            }
        }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.i(TAG, "socket—connect—error" + args[0].toString());
            }
        });
        socket.connect();
        Log.i(TAG, "监听注册完成");

    }

    /**
     * 初始化PeerConnection对象
     */
    public void initPeerConnection() {
        List<PeerConnection.IceServer> iceServers = new ArrayList<PeerConnection.IceServer>();
        PeerConnection.IceServer iceServer = new PeerConnection.IceServer("stun:59.110.15.178:3478", "", "");
        PeerConnection.IceServer iceServer1 = new PeerConnection.IceServer("turn:59.110.15.178:3478", "test3", "test3");
        iceServers.add(iceServer);
        iceServers.add(iceServer1);
        MediaConstraints pcConstraints = new MediaConstraints();
        pcConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("VoiceActivityDetection", "false"));
        peerConnection = peerConnectionFactory.createPeerConnection(iceServers, pcConstraints, observer);
        peerConnection.addStream(localMediaStream);
    }

    /**
     * 采集视频
     */
    public int getMedia() {
        Boolean PeerConnectionFactory_Flag = PeerConnectionFactory.initializeAndroidGlobals(getApplicationContext(), true, true, true);
        peerConnectionFactory = new PeerConnectionFactory();
        String Name[] = CameraEnumerationAndroid.getDeviceNames();
        if (Name.length < 1) {
            return -1;
        }
        videoCapturer = VideoCapturerAndroid.create(Name[Name.length - 1], new VideoCapturerAndroid.CameraEventsHandler() {
            @Override
            public void onCameraError(String s) {
                Log.i(TAG, "onCameraError:" + s);
            }

            @Override
            public void onCameraFreezed(String s) {
                Log.i(TAG, "onCameraFreezed:" + s);
            }

            @Override
            public void onCameraOpening(int i) {
                Log.i(TAG, "onCameraOpening:" + i);
            }

            @Override
            public void onFirstFrameAvailable() {
                Log.i(TAG, "onFirstFrameAvailable:");
            }

            @Override
            public void onCameraClosed() {
                Log.i(TAG, "onCameraClosed:");
            }
        });
        MediaConstraints mediaConstraints = new MediaConstraints();
        videoSource = peerConnectionFactory.createVideoSource(videoCapturer, mediaConstraints);
        localVideoTrack = peerConnectionFactory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
        MediaConstraints audioConstraints = new MediaConstraints();
/*        audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                "echoCancellation", "false"));
        audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                "googEchoCancellation", "false"));
        audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                "googEchoCancellation2", "false"));
        audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                "googDAEchoCancellation", "true"));*/
        audioSource = peerConnectionFactory.createAudioSource(audioConstraints);
        localAudioTrack = peerConnectionFactory.createAudioTrack(AUDIO_TRACK_ID, audioSource);
        return 1;
    }

    /**
     * 播放本地视频
     * 将本地视频添加到本地流媒体
     */
    public void playLocalMedia() {
        try {
            remoteRenderer = VideoRendererGui.createGui(0, 0, 99, 99, scalingType, true);
            localRenderer = VideoRendererGui.createGui(69, 69, 30, 30, scalingType, true);
            localVideoTrack.addRenderer(localRenderer);
            localMediaStream = peerConnectionFactory.createLocalMediaStream(LOCAL_MEDIA_STREAM_ID);
            localMediaStream.addTrack(localVideoTrack);
            localMediaStream.addTrack(localAudioTrack);

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "error*" + e.getMessage().toString());
        }
    }

    /**
     * 更新
     */
    public void updatePlay() {
        try {
            remoteRenderer = VideoRendererGui.createGui(0, 0, 99, 99, scalingType, true);
            localRenderer = VideoRendererGui.createGui(69, 69, 30, 30, scalingType, true);
            if (localVideoTrack != null) {
                localVideoTrack.addRenderer(localRenderer);
            }
            if (videoTrack != null) {
                videoTrack.addRenderer(remoteRenderer);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "重新播放失败--" + e.getMessage());
            T.showMessage(getApplicationContext(), "播放失败");
        }
    }

    /**
     * 翻转摄像头
     */
    public void changeCamere() {
        videoCapturer.switchCamera(new VideoCapturerAndroid.CameraSwitchHandler() {
            @Override
            public void onCameraSwitchDone(boolean b) {

            }

            @Override
            public void onCameraSwitchError(String s) {

            }
        });
    }

    /**
     * 切换画面
     */
    public void changeImage() {
        if (!LocalIsFront) {
            if (localVideoTrack != null && videoTrack != null) {
                localVideoTrack.removeRenderer(localRenderer);
                videoTrack.removeRenderer(remoteRenderer);
                try {
                    remoteRenderer = VideoRendererGui.createGui(0, 0, 99, 99, scalingType, true);
                    localRenderer = VideoRendererGui.createGui(69, 69, 30, 30, scalingType, true);
                    localVideoTrack.addRenderer(remoteRenderer);
                    videoTrack.addRenderer(localRenderer);
                    LocalIsFront = true;
                } catch (Exception e) {
                    Log.i(TAG, "e.printStackTrace()1==" + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                try {
                    localRenderer = VideoRendererGui.createGui(69, 69, 30, 30, scalingType, true);
                    localVideoTrack.addRenderer(localRenderer);
                } catch (Exception e) {
                    Log.i(TAG, "e.printStackTrace()2==" + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            if (localVideoTrack != null && videoTrack != null) {
                localVideoTrack.removeRenderer(remoteRenderer);
                videoTrack.removeRenderer(localRenderer);
                try {
                    remoteRenderer = VideoRendererGui.createGui(0, 0, 99, 99, scalingType, true);
                    localRenderer = VideoRendererGui.createGui(69, 69, 30, 30, scalingType, true);
                    localVideoTrack.addRenderer(localRenderer);
                    videoTrack.addRenderer(remoteRenderer);
                    LocalIsFront = false;
                } catch (Exception e) {
                    Log.i(TAG, "e.printStackTrace()3==" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

    }

    class SDPObserver implements SdpObserver {
        @Override
        public void onCreateSuccess(SessionDescription sessionDescription) {
            Log.i(TAG, "SDPObserver 创建成功 发送请求");
            Gson gson = new Gson();
            peerConnection.setLocalDescription(sdpObserver, sessionDescription);
            SessionDescriptionTemp stemp = new SessionDescriptionTemp("offer", sessionDescription.description);
            Log.i(TAG, "本地的offer待发送" + sessionDescription.description);
            Log.i("sdp", "**offer sdp" + sessionDescription.description.toString());
            socket.emit("offer", Name, gson.toJson(stemp));
        }

        @Override
        public void onSetSuccess() {
            Log.i(TAG, "SDPObserver set成功");
        }

        @Override
        public void onCreateFailure(String s) {
            Log.i(TAG, "SDPObserver 创建失败");
        }

        @Override
        public void onSetFailure(String s) {
            Log.i(TAG, "SDPObserver setFailure");
        }
    }

    class PCObserver implements PeerConnection.Observer {
        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {
            Log.i(TAG, " --onSignalingChange()-- " + signalingState);
        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
            Log.i(TAG, " --onIceConnectionChange()-- " + iceConnectionState);
        }

        @Override
        public void onIceConnectionReceivingChange(boolean b) {
            Log.i(TAG, " --onIceConnectionReceivingChange()-- " + b);
        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
            Log.i(TAG, " --onIceGatheringChange()-- " + iceGatheringState);

        }

        @Override
        public void onIceCandidate(IceCandidate iceCandidate) {

            remoteIceCandidate = iceCandidate;
            Gson g = new Gson();
            String s = g.toJson(iceCandidate);
            Log.i("iceCandidate", "转换后的 iceCandidate：" + g.toJson(iceCandidate));
          /*  if (s.contains("192.168")) {
                return;
            }*/
            // Log.i("iceCandidate","发送iceCandidate"+g.toJson(iceCandidate));
            IceCandidateTemp temp = new IceCandidateTemp(iceCandidate.sdp, iceCandidate.sdpMLineIndex, iceCandidate.sdpMid);
            socket.emit("candidate", Name, g.toJson(temp));
        }

        //绑定远程流到本地数据
        @Override
        public void onAddStream(final MediaStream mediaStream) {
            Log.i(TAG1, "获取远程流" + mediaStream.toString());
            Log.i(TAG1, "开始绑定视频");
            if (peerConnection == null) {
                Log.i(TAG1, "pc == null");
                return;
            }
            if (mediaStream.videoTracks.size() > 1 || mediaStream.audioTracks.size() > 1) {
                Log.e(TAG1, "size > 1");
                return;
            }
            Log.i(TAG1, "Size()" + mediaStream.videoTracks.size());
            if (mediaStream.videoTracks.size() == 1) {
                videoTrack = mediaStream.videoTracks.get(0);
                videoTrack.addRenderer(remoteRenderer);
                Log.i(TAG1, "绑定完毕");
            }
        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {
            Log.i(TAG, " --onRemoveStream()-- " + mediaStream);
        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {
            Log.i(TAG, " --onDataChannel()-- " + dataChannel);
        }

        @Override
        public void onRenegotiationNeeded() {
            Log.i(TAG, " --onRenegotiationNeeded()-- ");


        }
    }

    class RemoteObserver implements SdpObserver {

        @Override
        public void onCreateSuccess(SessionDescription sessionDescription) {
            Log.i(TAG, "RemoteObserver 创建成功");
            Gson gson = new Gson();
            peerConnection.setLocalDescription(remoteObserver, sessionDescription);
            SessionDescriptionTemp stemp = new SessionDescriptionTemp("answer", sessionDescription.description);
            Log.i(TAG, "本地的answer发送" + sessionDescription);
            socket.emit("answer", Name, gson.toJson(stemp));
        }

        @Override
        public void onSetSuccess() {
            Log.i(TAG, "RemoteObserver set成功");
            if (peerConnection.getLocalDescription() == null) {
                Log.i(TAG, "返回createAnswer");
                peerConnection.createAnswer(remoteObserver, sdpMediaConstraints);
            }
        }

        @Override
        public void onCreateFailure(String s) {
            Log.i(TAG, "RemoteObserver 创建失败");
        }

        @Override
        public void onSetFailure(String s) {
            Log.i(TAG, "RemoteObserver set失败");
        }
    }

    /**
     * 释放资源
     */
    public void releaseAll() {
        localMediaStream.dispose();
        audioSource.dispose();
        videoSource.dispose();
        peerConnectionFactory.dispose();
    }

    /**
     * 设置房间id
     */
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseAll();
        Log.i(TAG, "onDestroy() executed");
    }
}
