package com.chinabsc.telemedicine.expert.videoActivities;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.alibaba.fastjson.JSONObject;
import com.chinabsc.telemedicine.expert.R;
import com.github.nkzawa.emitter.Emitter;
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

public class ConnectActivity extends AppCompatActivity {
    String TAG = "zzw";
    String TAG1 = "candidate";
    String Name = "dfdsdszzzssz";
    public String AUDIO_TRACK_ID = "commyaudio";
    public String VIDEO_TRACK_ID = "commyvideo";
    public String LOCAL_MEDIA_STREAM_ID = "commyvideostream";
    Button bt;
    Socket socket;
    GLSurfaceView videoView;
    PeerConnectionFactory peerConnectionFactory;//工厂
    PeerConnection peerConnection;
    PCObserver observer = new PCObserver();
    SDPObserver sdpObserver = new SDPObserver();
    RemoteObserver remoteObserver=new RemoteObserver();
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
    RendererCommon.ScalingType scalingType;
    Boolean isCaller=false;
    Boolean isSetSuccess=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mymain);
        videoView = (GLSurfaceView) findViewById(R.id.glview_call);
        bt = (Button) super.findViewById(R.id.call);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                peerConnection.removeStream(localMediaStream);

            }
        });
        Button bt1 = (Button) super.findViewById(R.id.add);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //localMediaStream.addTrack(localVideoTrack);
                localMediaStream.addTrack(localAudioTrack);
                peerConnection.addStream(localMediaStream);
            }
        });
        initPeerConnection();
        init();
    }

    private void init() {
        try {
            socket = IO.socket("http://192.168.0.15:3000");
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    socket.emit("join", Name);
                }
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.i(TAG, "连接已经断开");
                }
            }).on("sys", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    String user = (String) args[0];
                    if (user.equals(Name)) {
                        Log.i(TAG,"user="+user+"--Name="+Name);
                        isCaller=true;
                        //Log.i(TAG,"lianjieqianmiaoshu"+  peerConnection.getLocalDescription().description.toString());
                        peerConnection.createOffer(sdpObserver, sdpMediaConstraints);
                    }
                }
            }).on("offer", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (!args[0].equals(Name)) {
                        Log.i(TAG, "设置远程远程Decription" + args[1]);
                        JSONObject jsonObject= (JSONObject) JSONObject.parse((String) args[1]);
                        //Log.i("sdp","offer sdp"+jsonObject.getString("sdp"));
                        Log.i(TAG, "changeDecription" + changToDescription(jsonObject.getString("sdp")).toString());
                        isCaller=false;
                        isSetSuccess=true;
                        //peerConnection .setRemoteDescription(sdpObserver,new SessionDescription(SessionDescription.Type.OFFER,changToDescription(jsonObject.getString("sdp"))));
                        peerConnection .setRemoteDescription(remoteObserver,new SessionDescription(SessionDescription.Type.OFFER,changToDescription(jsonObject.getString("sdp"))));
                    }
                }
            }).on("answer", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if(!args[0].equals(Name)){
                        Log.i(TAG, "要设置RemoteDescription是"+args[1]);
                        JSONObject jsonObject = (JSONObject) JSONObject.parse((String) args[1]);
                        Log.i("sdp", "sdp"+jsonObject.getString("sdp"));
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
            });
            socket.connect();
            Log.i(TAG, "监听注册完成");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void initPeerConnection() {
        Boolean PeerConnectionFactory_Flag = PeerConnectionFactory.initializeAndroidGlobals(getApplicationContext(), true, true, true);
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        peerConnectionFactory = new PeerConnectionFactory();
        List<PeerConnection.IceServer> iceServers = new ArrayList<PeerConnection.IceServer>();
        MediaConstraints pcConstraints = new MediaConstraints();
        pcConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("VoiceActivityDetection", "false"));
  /*      pcConstraints.optional.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        pcConstraints.optional.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        pcConstraints.optional.add(new MediaConstraints.KeyValuePair("iceRestart", "true"));*/
        peerConnection = peerConnectionFactory.createPeerConnection(iceServers, pcConstraints, observer);
        initialSystem();

    }

    private void initialSystem() {
        String Name[] = CameraEnumerationAndroid.getDeviceNames();
        videoCapturer = VideoCapturerAndroid.create(Name[0], new VideoCapturerAndroid.CameraEventsHandler() {
            @Override
            public void onCameraError(String s) {

            }

            @Override
            public void onCameraFreezed(String s) {

            }

            @Override
            public void onCameraOpening(int i) {

            }

            @Override
            public void onFirstFrameAvailable() {

            }

            @Override
            public void onCameraClosed() {

            }
        });
        MediaConstraints mediaConstraints = new MediaConstraints();
        videoSource = peerConnectionFactory.createVideoSource(videoCapturer, mediaConstraints);
        localVideoTrack = peerConnectionFactory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
        MediaConstraints audioConstraints = new MediaConstraints();
        audioSource = peerConnectionFactory.createAudioSource(audioConstraints);
        localAudioTrack = peerConnectionFactory.createAudioTrack(AUDIO_TRACK_ID, audioSource);
        VideoRendererGui.setView(videoView, new Runnable() {
            @Override
            public void run() {
            }
        });
        scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FILL;
        try {
            localRenderer = VideoRendererGui.createGui(0, 0, 30, 30, scalingType, true);
            remoteRenderer = VideoRendererGui.createGui(30, 0, 50, 50, scalingType, true);
            localVideoTrack.addRenderer(localRenderer);
        } catch (Exception e) {
            Log.i(TAG, "scalingType*" + scalingType);
            Log.i(TAG, "error*" + e.getMessage().toString());
            e.printStackTrace();
        }
        localMediaStream = peerConnectionFactory.createLocalMediaStream(LOCAL_MEDIA_STREAM_ID);
        localMediaStream.addTrack(localVideoTrack);
        localMediaStream.addTrack(localAudioTrack);
        peerConnection.addStream(localMediaStream);

    }

    class PCObserver implements PeerConnection.Observer {
        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {
            Log.i(TAG, " --onSignalingChange()-- " + signalingState);
        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
            Log.i(TAG1, " --onIceConnectionChange()-- " + iceConnectionState);
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
            Log.i(TAG, " --onIceCandidate()-- " + iceCandidate);
            remoteIceCandidate = iceCandidate;
            Gson g = new Gson();
            Log.i(TAG, "转换后的 iceCandidate：" + g.toJson(iceCandidate));
            IceCandidateTemp temp = new IceCandidateTemp(iceCandidate.sdp, iceCandidate.sdpMLineIndex, iceCandidate.sdpMid);
            socket.emit("candidate", Name, g.toJson(temp));
        }

        //绑定远程流到本地数据
        @Override
        public void onAddStream(final MediaStream mediaStream) {
            Log.i(TAG, "获取远程流" + mediaStream.toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "开始绑定视频");
                    if (peerConnection == null) {
                        Log.i(TAG, "pc == null");
                        return;
                    }
                    if (mediaStream.videoTracks.size() > 1 || mediaStream.audioTracks.size() > 1) {
                        Log.e(TAG, "size > 1");
                        return;
                    }
                    Log.i(TAG, "Size()" + mediaStream.videoTracks.size());
                    if (mediaStream.videoTracks.size() == 1) {
                        videoTrack = mediaStream.videoTracks.get(0);
                        videoTrack.addRenderer(remoteRenderer);
                    }
                }
            });
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
    class RemoteObserver implements  SdpObserver{

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
            Log.i(TAG, "设置createAnswer");
            if(peerConnection.getLocalDescription()==null){
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
    class SDPObserver implements SdpObserver {
        @Override
        public void onCreateSuccess(SessionDescription sessionDescription) {
            Log.i(TAG, "SDPObserver 创建成功 发送请求");
            Gson gson=new Gson();
            peerConnection.setLocalDescription(sdpObserver, sessionDescription);
            SessionDescriptionTemp stemp = new SessionDescriptionTemp("offer", sessionDescription.description);
            Log.i(TAG, "本地的offer待发送" + sessionDescription.description);
            Log.i("sdp","**offer sdp"+sessionDescription.description.toString());
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

    /**
     * 释放资源
     */
    public void releaseAll() {
        localMediaStream.dispose();
        audioSource.dispose();
        videoSource.dispose();
        peerConnectionFactory.dispose();
    }
    private String changToDescription(String s){
        try{
            StringBuffer sbuff=new StringBuffer();
            String s_temp=s.substring(0,s.indexOf("a=rtcp-fb:100 transport-cc"));
            String s_temp1=s.substring(s.indexOf("a=rtpmap:116 red/90000"),s.indexOf("a=rtpmap:97 rtx/90000"));
            String s_temp2=s.substring(s.indexOf("a=ssrc-group:FID"),s.length());
            String sarr[]=(s_temp+s_temp1+s_temp2).split("\r\n");
            for(int i=0;i<sarr.length;i++){
                if(sarr[i].contains("m=video")){
                    sarr[i]="m=video 9 RTP/SAVPF 100 116 117 96";
                }
                sbuff.append(sarr[i]+"\r\n");
            }
            return sbuff.toString();
        }catch (Exception e){

        }
        return s;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseAll();
        if (socket != null) {
            socket.disconnect();
        }

    }



}
