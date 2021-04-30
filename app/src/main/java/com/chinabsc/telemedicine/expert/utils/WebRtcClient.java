package com.chinabsc.telemedicine.expert.utils;

import android.util.Log;

import com.chinabsc.telemedicine.expert.application.MyApplication;
import com.google.gson.Gson;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.CameraEnumerationAndroid;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzw on 2017/6/19.
 */

public class WebRtcClient {
    String TAG = "VideoPlay";
    public String AUDIO_TRACK_ID = "commyaudio";
    public String VIDEO_TRACK_ID = "commyvideo";
    public String LOCAL_MEDIA_STREAM_ID = "commyvideostream";
    //Socket socket;
    //IceCandidate remoteIceCandidate;
    PeerConnectionFactory peerConnectionFactory;//工厂
    public PeerConnection peerConnection;
    public PCObserver observer = new PCObserver();
    public SDPObserver sdpObserver = new SDPObserver();
    public RemoteObserver remoteObserver = new RemoteObserver();
    VideoCapturerAndroid videoCapturer;
    public MediaConstraints offer_sdpMediaConstraints = new MediaConstraints();//本地会话描述
    public MediaConstraints answer_sdpMediaConstraints = new MediaConstraints();//本地会话描述
    VideoSource videoSource;
    VideoTrack localVideoTrack, videoTrack;
    AudioSource audioSource;
    AudioTrack localAudioTrack;
    MediaStream localMediaStream;
    RtcListener rtcListener;
    String name = "androidname1";

    public interface RtcListener {
        void onStatusChanged(PeerConnection.IceConnectionState iceConnectionState);

        void onLocalStream(MediaStream localStream);

        void onAddRemoteStream(MediaStream remoteStream);

        void onRemoveRemoteStream(MediaStream remoteStream);

        void onIceCandidate(IceCandidate iceCandidate);

        void onRemoteObserverCreateSucess(String name, String Description);

        void onLocalObserverCreateSucess(String name, String Description);
    }

    public WebRtcClient(RtcListener listener) {

        rtcListener = listener;
        initialSystem();
        initPeerConnection();
    }

    private void initPeerConnection() {
        offer_sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        offer_sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        offer_sdpMediaConstraints.optional.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        offer_sdpMediaConstraints.optional.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
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

    private void initialSystem() {
        Boolean PeerConnectionFactory_Flag = PeerConnectionFactory.initializeAndroidGlobals(rtcListener, true, true, true);
        Log.i(TAG, "PeerConnectionFactory_Flag==" + PeerConnectionFactory_Flag);
        peerConnectionFactory = new PeerConnectionFactory();
        String Name[] = CameraEnumerationAndroid.getDeviceNames();
        videoCapturer = VideoCapturerAndroid.create(Name[1], new VideoCapturerAndroid.CameraEventsHandler() {
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
        mediaConstraints.optional.add(new MediaConstraints.KeyValuePair("noiseSuppression", "true"));
        videoSource = peerConnectionFactory.createVideoSource(videoCapturer, mediaConstraints);

        localVideoTrack = peerConnectionFactory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
        MediaConstraints audioConstraints = new MediaConstraints();
        audioConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("VoiceActivityDetection", "false"));
        audioConstraints.optional.add(new MediaConstraints.KeyValuePair("noiseSuppression", "true"));
        audioSource = peerConnectionFactory.createAudioSource(audioConstraints);
        localAudioTrack = peerConnectionFactory.createAudioTrack(AUDIO_TRACK_ID, audioSource);
        localMediaStream = peerConnectionFactory.createLocalMediaStream(LOCAL_MEDIA_STREAM_ID);
        localMediaStream.addTrack(localVideoTrack);
        localMediaStream.addTrack(localAudioTrack);
      
        rtcListener.onLocalStream(localMediaStream);
    }

    class PCObserver implements PeerConnection.Observer {
        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {
            Log.i(TAG, " --onSignalingChange()-- " + signalingState);
        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
            Log.i(TAG, " --onIceConnectionChange()-- " + iceConnectionState);
            rtcListener.onStatusChanged(iceConnectionState);
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
            rtcListener.onIceCandidate(iceCandidate);
        }

        //绑定远程流到本地数据
        @Override
        public void onAddStream(final MediaStream mediaStream) {
            Log.i(TAG, "获取远程流" + mediaStream.toString());
            rtcListener.onAddRemoteStream(mediaStream);

        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {
            Log.i(TAG, " --onRemoveStream()-- " + mediaStream);
            //mediaStream.videoTracks.get(0).dispose();
            rtcListener.onRemoveRemoteStream(mediaStream);
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
            rtcListener.onRemoteObserverCreateSucess(name, gson.toJson(stemp));
            //socket.emit("answer", name, gson.toJson(stemp));
        }

        @Override
        public void onSetSuccess() {
            Log.i(TAG, "RemoteObserver set成功======");
            if (peerConnection.getLocalDescription() == null) {
                Log.i(TAG, "设置createAnswer");
                peerConnection.createAnswer(remoteObserver, answer_sdpMediaConstraints);
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

            Gson gson = new Gson();
            peerConnection.setLocalDescription(sdpObserver, sessionDescription);
            SessionDescriptionTemp stemp = new SessionDescriptionTemp("offer", sessionDescription.description);
            Log.i(TAG, "本地的offer待发送");
            Log.i("sdp", "**offer sdp" + sessionDescription.description.toString());
            rtcListener.onLocalObserverCreateSucess(name, gson.toJson(stemp));
            //socket.emit("offer", name, gson.toJson(stemp));
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
        Log.i(TAG, "destoryed");
        if (peerConnection != null) {
            peerConnection.dispose();
        }
        Log.i(TAG, "destoryed1");
        if (videoSource != null) {
            videoSource.dispose();
        }
        Log.i(TAG, "destoryed2");
        if (peerConnectionFactory != null) {
            peerConnectionFactory.dispose();
        }
        Log.i(TAG, "destoryed3");

    }
}
