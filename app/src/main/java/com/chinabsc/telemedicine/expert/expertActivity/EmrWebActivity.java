package com.chinabsc.telemedicine.expert.expertActivity;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.utils.T;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

@ContentView(R.layout.activity_emr_web)
public class EmrWebActivity extends BaseActivity {

    public static String EO_EMR_SN_LIST = "EO_EMR_SN_LIST";
    public static String EO_EMR_ID = "EO_EMR_ID";

    private ArrayList<String> mSnList = new ArrayList<String>();
    private int mSnId = 0;

    @ViewInject(R.id.WebView)
    private WebView mWebView;

    @ViewInject(R.id.TitleLayout)
    private RelativeLayout mTitleLayout;

    @ViewInject(R.id.login_loading_layout)
    private RelativeLayout mLoginLoadingLayout;

    @Event(value = {
            R.id.BackImageView,
            R.id.PreView,
            R.id.NextView}, type = View.OnClickListener.class)
    private void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.BackImageView:
                finish();
                break;
            case R.id.PreView:
                preSn();
                break;
            case R.id.NextView:
                nextSn();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        Intent i = getIntent();

        mSnList = i.getStringArrayListExtra(EO_EMR_SN_LIST);
        mSnId = i.getIntExtra(EO_EMR_ID, 0);
        //支持JS
        mWebView.getSettings().setJavaScriptEnabled(true);
        // 设置可以支持缩放
        mWebView.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        mWebView.getSettings().setBuiltInZoomControls(true);
        //扩大比例的缩放
        mWebView.getSettings().setUseWideViewPort(true);
        //自适应屏幕
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.setWebViewClient(new WebViewClient());
        getEoWeb(mSnList.get(mSnId));
    }

    private void getEoWeb(String eo) {
        mLoginLoadingLayout.setVisibility(View.VISIBLE);
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/emr/findEmrById");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addQueryStringParameter("id", eo);
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("test", result);
                try {
                    JSONObject j = new JSONObject(result);
                    if (j.has("resultCode")) {
                        String resultCode = j.getString("resultCode");
                        if (resultCode.equals("200")) {
                            if (j.has("data")) {
                                String data = j.getString("data");
                                JSONObject k = new JSONObject(data);
                                if (k.has("content")) {
                                    String content = k.getString("content");
                                    String html = PublicUrl.getFromBase64(content);
                                    html = html.replaceAll("</head>","<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"> </head>");
                                    Log.i("test", html);
                                    mWebView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
                                }
                            }
                        } else if (resultCode.equals("401")) {
                            T.showMessage(getApplicationContext(), getString(R.string.login_timeout));
                            delToken();
                            doLogout();
                        } else {
                            T.showMessage(getApplicationContext(), getString(R.string.api_error) + resultCode);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                //Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {
                mLoginLoadingLayout.setVisibility(View.GONE);
            }
        });
    }

    private void nextSn() {
        Log.e("test", "mSnList.size() " + mSnList.size());
        Log.e("test", "mSnId " + mSnId);
        if (mSnId == mSnList.size() - 1 || mSnList.size() == 1) {
            T.showMessage(getApplicationContext(), "最后一张");
        } else {
            mSnId = mSnId + 1;
            getEoWeb(mSnList.get(mSnId));
        }
    }

    private void preSn() {
        if (mSnId == 0) {
            T.showMessage(getApplicationContext(), "第一张");
        } else {
            mSnId = mSnId - 1;
            getEoWeb(mSnList.get(mSnId));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        AudioManager audio = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                audio.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE,
                        AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                audio.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER,
                        AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_BACK:
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                    return true;
                }
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
