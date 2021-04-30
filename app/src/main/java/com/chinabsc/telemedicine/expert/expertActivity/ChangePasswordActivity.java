package com.chinabsc.telemedicine.expert.expertActivity;

import android.content.Intent;
import android.os.Bundle;

import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

@ContentView(R.layout.activity_change_password)
public class ChangePasswordActivity extends BaseActivity {

    private Boolean mShowPasswordMark = false;
    @ViewInject(R.id.PasswordEditText)
    private EditText mPasswordEditText;
    @ViewInject(R.id.ShowPasswordImageView)
    private ImageView mShowPasswordImageView;

    private Boolean mAgainShowPasswordMark = false;
    @ViewInject(R.id.AgainPasswordEditText)
    public EditText mAgainPasswordEditText;
    @ViewInject(R.id.AgainShowPasswordImageView)
    public ImageView mAgainShowPasswordImageView;

    @Event(value = {
            R.id.BackImageView,
            R.id.ShowPasswordView,
            R.id.AgainShowPasswordView,
            R.id.SendButton}, type = View.OnClickListener.class)
    private void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.BackImageView:
                finish();
                break;
            case R.id.ShowPasswordView:
                if (!mShowPasswordMark) {
                    mShowPasswordMark = true;
                    mPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mShowPasswordImageView.setImageResource(R.drawable.login_show_password_selected);
                    mPasswordEditText.setSelection(mPasswordEditText.length());
                } else {
                    mShowPasswordMark = false;
                    mPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mShowPasswordImageView.setImageResource(R.drawable.login_show_password);
                    mPasswordEditText.setSelection(mPasswordEditText.length());
                }
                break;
            case R.id.AgainShowPasswordView:
                if (!mAgainShowPasswordMark) {
                    mAgainShowPasswordMark = true;
                    mAgainPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mAgainShowPasswordImageView.setImageResource(R.drawable.login_show_password_selected);
                    mAgainPasswordEditText.setSelection(mAgainPasswordEditText.length());
                } else {
                    mAgainShowPasswordMark = false;
                    mAgainPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mAgainShowPasswordImageView.setImageResource(R.drawable.login_show_password);
                    mAgainPasswordEditText.setSelection(mAgainPasswordEditText.length());
                }
                break;
            case R.id.SendButton:
                String password = mPasswordEditText.getText().toString().trim();
                String againpassword = mAgainPasswordEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(againpassword) && password.equals(againpassword)) {
                    password = PublicUrl.getFinalPassword(password);
                    putNewPassword(password);
                } else {
                    Toast.makeText(getApplicationContext(), "两次填写不一致", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
    }

    private void putNewPassword(String password) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/user/changePassword");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addBodyParameter("password", password);
        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("Password onSuccess", result);
                try {
                    JSONObject j = new JSONObject(result);
                    if (j.has("resultCode")) {
                        String resultCode = j.getString("resultCode");
                        if (resultCode.equals("200")) {
                            getLogout();
                        }  else if (resultCode.equals("401")) {
                            T.showMessage(getApplicationContext(), getString(R.string.login_timeout));
                            getLogout();
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
                Log.i("onError", "onError:" + ex.getMessage());
                T.showMessage(getApplicationContext(), getString(R.string.server_error));
            }

            @Override
            public void onCancelled(CancelledException cex) {
                //Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {
                //mLoginLoadingLayout.setVisibility(View.GONE);
            }
        });
    }

    private void getLogout() {
        delToken();
        Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
