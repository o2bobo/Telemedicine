package com.chinabsc.telemedicine.expert.expertActivity;


import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.TextUtils;

import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.utils.SPUtils;
import com.source.adnroid.comm.ui.activity.UserSharedListActivity;
import com.source.adnroid.comm.ui.entity.Const;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;


@ContentView(R.layout.activity_web)
public class WebActivity extends BaseActivity {

    public static String URL_ID = "URL_ID";
    public static String TITLE_VISIBILITY = "TITLE_VISIBILITY";
    public static String TITLE_TEXT = "TITLE_TEXT";

    @ViewInject(R.id.WebView)
    private WebView mWebView;

    @ViewInject(R.id.TitleLayout)
    private RelativeLayout mTitleLayout;

    @ViewInject(R.id.TitleText)
    private TextView mTitleText;
    @ViewInject(R.id.share_patient_image)
    private Button sharePatientImage;

    int titlemark;

    @Event(value = {
            R.id.BackImageView, R.id.share_patient_image}, type = View.OnClickListener.class)
    private void onClick(View v) {

        switch (v.getId()) {
            case R.id.BackImageView:
                finish();
                break;
            case R.id.share_patient_image:
                Log.i("WebActivity", "try to Shared");
                Intent intent = new Intent();
                intent.setClass(WebActivity.this, UserSharedListActivity.class);
                intent.setType("url");
                intent.putExtra(Const.SHARED_URL, getIntent().getStringExtra(URL_ID));
                intent.putExtra("userId", SPUtils.get(WebActivity.this, PublicUrl.USER_ID_KEY, "").toString());
                intent.putExtra(PublicUrl.TOKEN_KEY, SPUtils.get(WebActivity.this, PublicUrl.TOKEN_KEY, "").toString());
                startActivity(intent);
                break;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        Intent i = getIntent();
        String url = i.getStringExtra(URL_ID);
        titlemark = i.getIntExtra(TITLE_VISIBILITY, 0);
        String title = i.getStringExtra(TITLE_TEXT);

        if (!TextUtils.isEmpty(url)) {
            Log.i("test", url);
            mTitleText.setText(title);
            mWebView.getSettings().setJavaScriptEnabled(true);
            //mWebView.setWebViewClient(new WebViewClient());
            mWebView.setWebViewClient(new MWebViewClient());
            mWebView.loadUrl(url);

        } else {
            Log.i("url", "null");
        }

        if (titlemark == 1) {
            mTitleLayout.setVisibility(View.GONE);
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

    class MWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (titlemark == 1) {
                sharePatientImage.setVisibility(View.GONE);
            }
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            if (titlemark == 1) {
                sharePatientImage.setVisibility(View.VISIBLE);
            }
            super.onPageFinished(view, url);
        }
    }


}
