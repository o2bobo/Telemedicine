package com.chinabsc.telemedicine.expert.expertFragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.application.MyApplication;
import com.chinabsc.telemedicine.expert.entity.StopFloatViewMessage;
import com.chinabsc.telemedicine.expert.expertActivity.TelemedicineInfoActivity;
import com.chinabsc.telemedicine.expert.myServices.RtcService;
import com.chinabsc.telemedicine.expert.utils.ActivityVisableUtil;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.webrtc.RendererCommon;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;


/**
 * A simple {@link Fragment} subclass.
 */
public class VIdeoPlayFragment extends Fragment {
    String TAG = "VideoPlay";
    View view = null;
    VideoReceiver videoReceiver;
    GLSurfaceView videoView;//用于界面
    GLSurfaceView floatVideoView;//用于初始化悬浮窗
    Button joinRoom, changeImg;
    VideoRenderer.Callbacks localRenderer = null;
    VideoRenderer.Callbacks remoteRenderer = null;
    VideoRenderer localVideoRender, remoteVideoRender;
    RendererCommon.ScalingType scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FILL;
    String userId;
    String roomId = "";
    Intent intent;
    RtcService rtcService;
    int isLocalFront;
    //用于悬浮窗
    ConstraintLayout toucherLayout;
    WindowManager.LayoutParams params;
    WindowManager windowManager;
    //回调接口
    IVideoCallBack iVideoCallBack;

    public interface IVideoCallBack {
        void backToVideo();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IVideoCallBack) {
            iVideoCallBack = (IVideoCallBack) context;
        } else {
            throw new IllegalArgumentException("activity must implements IVideoCallBack");
        }
    }

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "NetService name disconnect");

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //返回一个MsgService对象
            rtcService = ((RtcService.LocalBinder) service).getService();
            joinRoom.setText("退出房间");
            rtcService.sendStreamBroadcast();
            MyApplication.isRtcServiceRuning = true;
        }
    };

    public VIdeoPlayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        isLocalFront = 0;
        view = inflater.inflate(R.layout.fragment_video_play, container, false);
        videoView = (GLSurfaceView) view.findViewById(R.id.glview_call);
        joinRoom = (Button) view.findViewById(R.id.stopVideo);
        changeImg = (Button) view.findViewById(R.id.changeImg);
        joinRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "click==" + joinRoom.getText().toString());
                if (joinRoom.getText().toString().equals("进入房间")) {
                    getPermision();

                } else if (joinRoom.getText().toString().equals("退出房间")) {
                    getActivity().getApplicationContext().unbindService(conn);
                    joinRoom.setText("进入房间");
                } else {
                    Log.i(TAG, "click=else=" + joinRoom.getText().toString());
                }

            }
        });
        changeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.hasRemoteStream && MyApplication.isRtcServiceRuning) {
                    switch (isLocalFront) {
                        case 0:
                            Log.i(TAG, "changeImg" + isLocalFront);
                            rtcService.mLocalStream.videoTracks.get(0).removeRenderer(localVideoRender);
                            rtcService.mRemoteStream.videoTracks.get(0).removeRenderer(remoteVideoRender);
                            remoteVideoRender = new VideoRenderer(remoteRenderer);
                            localVideoRender = new VideoRenderer(localRenderer);
                            rtcService.mLocalStream.videoTracks.get(0).addRenderer(remoteVideoRender);
                            rtcService.mRemoteStream.videoTracks.get(0).addRenderer(localVideoRender);
                            isLocalFront = 1;
                            break;
                        case 1:
                            Log.i(TAG, "changeImg" + isLocalFront);
                            rtcService.mLocalStream.videoTracks.get(0).removeRenderer(remoteVideoRender);
                            rtcService.mRemoteStream.videoTracks.get(0).removeRenderer(localVideoRender);
                            remoteVideoRender = new VideoRenderer(remoteRenderer);
                            localVideoRender = new VideoRenderer(localRenderer);
                            rtcService.mLocalStream.videoTracks.get(0).addRenderer(localVideoRender);
                            rtcService.mRemoteStream.videoTracks.get(0).addRenderer(remoteVideoRender);
                            isLocalFront = 0;
                            break;
                    }
                }

            }
        });
        VideoRendererGui.setView(videoView, new Runnable() {
            @Override
            public void run() {

            }
        });
        localRenderer = VideoRendererGui.createGuiRenderer(0, 0, 99, 99, scalingType, true);
        localVideoRender = new VideoRenderer(localRenderer);
        remoteRenderer = VideoRendererGui.createGuiRenderer(60, 60, 39, 39, scalingType, true);
        remoteVideoRender = new VideoRenderer(remoteRenderer);
        userId = (String) SPUtils.get(getActivity(), PublicUrl.USER_ID_KEY, "");
        Bundle bundle1 = getArguments();
        roomId = bundle1.getString(TelemedicineInfoActivity.TELEMEDICINE_INFO_ID);
        intent = new Intent(getActivity().getApplicationContext(), RtcService.class);
        intent.putExtra("userId", userId);
        intent.putExtra("roomId", roomId);
        Log.i(TAG, "onCreateView" + roomId);
        initReceiver();
        if (MyApplication.isRtcServiceRuning) {
            if (MyApplication.hasRemoteStream) {
                rtcService.mRemoteStream.videoTracks.get(0).addRenderer(remoteVideoRender);
            }
            rtcService.mLocalStream.videoTracks.get(0).addRenderer(localVideoRender);
            joinRoom.setText("退出房间");

        }
        return view;
    }


    private void initReceiver() {
        if (videoReceiver == null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction("localStream");
            filter.addAction("remoteStream");
            filter.addAction("stopRTCService");
            filter.addAction("startjoin");
            videoReceiver = new VideoReceiver();
            getActivity().registerReceiver(videoReceiver, filter);
        }

    }

    public class VideoReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "intent.getType()=" + intent.getAction());
            switch (intent.getAction()) {
                case "localStream":
                    rtcService.mLocalStream.videoTracks.get(0).removeRenderer(localVideoRender);
                    localVideoRender = new VideoRenderer(localRenderer);
                    rtcService.mLocalStream.videoTracks.get(0).addRenderer(localVideoRender);
                    break;
                case "remoteStream":
                    PublicUrl.setSpeakerphoneOn(VIdeoPlayFragment.this.getActivity());
                    rtcService.mRemoteStream.videoTracks.get(0).removeRenderer(remoteVideoRender);
                    remoteVideoRender = new VideoRenderer(remoteRenderer);
                    rtcService.mRemoteStream.videoTracks.get(0).addRenderer(remoteVideoRender);
                    break;
                case "stopRTCService":
                    try {
                        getActivity().getApplicationContext().unbindService(conn);
                        closeFloatView();
                    } catch (Exception e) {
                        Log.i(TAG, "stopService=err=" + e.getMessage());
                    }
                    joinRoom.setText("进入房间");
                    showDialog(getActivity());

                    break;
                case "startjoin":

                    break;

            }

        }
    }

    private void getPermision() {
       /* TelephonyManager manager;
        manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);*/
        Log.i(TAG, "Check Permission" + ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA));
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "request Permission");

            this.requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                    0);
        } else {
            getActivity().getApplicationContext().bindService(intent, conn, Context.BIND_AUTO_CREATE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult 1" + requestCode);
        if (requestCode == 0) {
            Log.i(TAG, "onRequestPermissionsResult 2");
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "GRANTED ");
                getActivity().getApplicationContext().bindService(intent, conn, Context.BIND_AUTO_CREATE);
            } else {
                videohowDialog(VIdeoPlayFragment.this.getActivity());
                Log.i(TAG, "not GRANTED ");
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        if (videoView != null) {
            videoView.onResume();
        }
        if (floatVideoView != null && MyApplication.isRtcServiceRuning) {
            closeFloatView();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        if (videoView != null) {
            videoView.onPause();
        }
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView");
        if (MyApplication.isRtcServiceRuning) {
            getFloatWindowPermission();
        }

        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        try {
            getActivity().getApplicationContext().unbindService(conn);
            getActivity().unregisterReceiver(videoReceiver);
        } catch (Exception e) {
            Log.i(TAG, "onDestroy==" + e.getMessage());
        }

        closeFloatView();
        iVideoCallBack = null;

        super.onDestroy();

    }

    //显示基本的AlertDialog
    private void showDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.mipmap.basic_logo_big);
        builder.setTitle("提示");
        builder.setMessage("对方已经退出房间");
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    //显示基本的AlertDialog
    private void videohowDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.mipmap.basic_logo_big);
        builder.setTitle("提示");
        builder.setMessage("请开启相机和录音权限后使用该功能");
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }
    //获取悬浮窗权限
    private void getFloatWindowPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (Settings.canDrawOverlays(getActivity())) {
                // Toast.makeText(getActivity(), "已开启Toucher", Toast.LENGTH_SHORT).show();
                createToucher();
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                Toast.makeText(getActivity(), "需要取得权限以使用悬浮窗", Toast.LENGTH_SHORT).show();
                startActivity(intent);

            }
        } else {
            //SDK在23以下，不用管.
            createToucher();
        }
    }

    //状态栏高度.
    int statusBarHeight = -1;

    @SuppressLint("ClickableViewAccessibility")
    private void createToucher() {
        //赋值WindowManager&LayoutParam.
        params = new WindowManager.LayoutParams();
        windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        //设置type.系统提示型窗口，一般都在应用程序窗口之上.
        //Android8.0行为变更，对8.0进行适配https://developer.android.google.cn/about/versions/oreo/android-8.0-changes#o-apps
        //Build.VERSION_CODES.O_MR1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        //设置效果为背景透明.
        params.format = PixelFormat.RGBA_8888;
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        //设置窗口初始停靠位置.
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.x = 0;
        params.y = 0;

        //设置悬浮窗口长宽数据.
        params.width = 300;
        params.height = 300;

        LayoutInflater inflater = LayoutInflater.from(getActivity().getApplication());
        //获取浮动窗口视图所在布局.
        toucherLayout = (ConstraintLayout) inflater.inflate(R.layout.activity_floatview, null);
        //添加toucherlayout
        windowManager.addView(toucherLayout, params);

        Log.i(TAG, "toucherlayout-->left:" + toucherLayout.getLeft());
        Log.i(TAG, "toucherlayout-->right:" + toucherLayout.getRight());
        Log.i(TAG, "toucherlayout-->top:" + toucherLayout.getTop());
        Log.i(TAG, "toucherlayout-->bottom:" + toucherLayout.getBottom());

        //主动计算出当前View的宽高信息.
        toucherLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        //用于检测状态栏高度.
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        Log.i(TAG, "状态栏高度为:" + statusBarHeight);

        //浮动窗组件.
        floatVideoView = (GLSurfaceView) toucherLayout.findViewById(R.id.floatview_call);
        VideoRendererGui.setView(floatVideoView, new Runnable() {
            @Override
            public void run() {

            }
        });
        if (MyApplication.isRtcServiceRuning) {
            if (MyApplication.hasLocalStream) {
                localRenderer = VideoRendererGui.createGuiRenderer(0, 0, 99, 99, scalingType, true);
                localVideoRender = new VideoRenderer(localRenderer);
                rtcService.mLocalStream.videoTracks.get(0).addRenderer(localVideoRender);
            }
            if (MyApplication.hasRemoteStream) {
                rtcService.mRemoteStream.videoTracks.get(0).removeRenderer(remoteVideoRender);
                remoteRenderer = VideoRendererGui.createGuiRenderer(60, 60, 39, 39, scalingType, true);
                remoteVideoRender = new VideoRenderer(remoteRenderer);
                rtcService.mRemoteStream.videoTracks.get(0).addRenderer(remoteVideoRender);
            }
        }

        toucherLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                params.x = (int) event.getRawX() - 150;
                params.y = (int) event.getRawY() - 150 - statusBarHeight;
                windowManager.updateViewLayout(toucherLayout, params);
                return false;
            }
        });
        toucherLayout.setOnClickListener(new View.OnClickListener() {
            long[] hints = new long[2];

            @Override
            public void onClick(View v) {
                Log.i(TAG, "点击了");
                System.arraycopy(hints, 1, hints, 0, hints.length - 1);
                hints[hints.length - 1] = SystemClock.uptimeMillis();
                if (SystemClock.uptimeMillis() - hints[0] >= 700) {
                    Log.i(TAG, "要执行");
                    Toast.makeText(getActivity(), "连续点击两次回到视频", Toast.LENGTH_SHORT).show();

                } else {
                    if (ActivityVisableUtil.isForeground(getActivity())) {
                        iVideoCallBack.backToVideo();
                    }

                }
            }
        });
    }


    public void closeFloatView() {
        if (floatVideoView != null) {
            floatVideoView = null;
            try {
                windowManager.removeView(toucherLayout);
            } catch (Exception e) {
                Log.i(TAG, "recycle floatVideoView exception==>" + e.getMessage());
            }

        }
    }

}
