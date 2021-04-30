package com.chinabsc.telemedicine.expert.expertActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.utils.MySQLiteOpenHelper;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.utils.SPUtils;
import com.chinabsc.telemedicine.expert.utils.T;
import com.source.android.chatsocket.net.HttpReuqests;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity {
    String TAG = "LoginActivity";
    public static final String FINISH_ALL = "FinishAll";

    @ViewInject(R.id.LogoImage)
    private ImageView mLogoImage;

    @ViewInject(R.id.login_loading_layout)
    private RelativeLayout mLoginLoadingLayout;

    @ViewInject(R.id.TitleTextView)
    private TextView mTitleTextView;

    @ViewInject(R.id.PhopneNumEditText)
    private EditText mPhopneNumEditText;

    @ViewInject(R.id.ClearPhoneNumImageView)
    private ImageView mClearPhoneNumImageView;

    private Boolean mShowPasswordMark = false;
    @ViewInject(R.id.PasswordEditText)
    private EditText mPasswordEditText;

    @ViewInject(R.id.ShowPasswordImageView)
    private ImageView mShowPasswordImageView;

    @ViewInject(R.id.login_btn_login)
    private Button mLoginButton;

    @Event(value = {
            R.id.ClearPhoneNumView,
            R.id.LogoImage,
            R.id.login_btn_login,
            R.id.ShowPasswordView}, type = View.OnClickListener.class)
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.ClearPhoneNumView:
                mPhopneNumEditText.setText("");
                mClearPhoneNumImageView.setVisibility(View.GONE);
                break;
            case R.id.login_btn_login:
                String username = mPhopneNumEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                if (!TextUtils.isEmpty(username)) {
                    if (!TextUtils.isEmpty(password)) {
                        String imei = getIMEI();
                        password = PublicUrl.getFinalPassword(password);
                        getLogin(username, password, imei);
                        mLoginLoadingLayout.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "请输入用户名", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.LogoImage:
                //mUrlSpinner.setVisibility(View.VISIBLE);
                showServerDialog();
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
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        checkPublishPermission();
        init();
        setViewListener();
    }

    //服务器地址数组
    String[] mUrlList;
    //服务器名称数组
    String[] mHospitalList;

    private void init() {
        createDatabase();
        try {
            if (TextUtils.isEmpty(selectUUID())) {
                createTable();
                insertUUID();
            }
        } catch (Exception e) {
            createTable();
            insertUUID();
        }

        //读取xml中服务器地址列表与名称列表
        Resources res = getResources();
        mUrlList = res.getStringArray(R.array.urls);
        mHospitalList = res.getStringArray(R.array.hospitals);

        selectServerForSharedPreferences();
    }

    //读取已选择的服务器
    private void selectServerForSharedPreferences() {
        //读取保存的服务器列表序号
        SharedPreferences sp = getSharedPreferences("spUrlList", Context.MODE_PRIVATE);
        int pos = sp.getInt("urlListPos", 0);

        //读取xml中服务器地址列表
        Resources res = getResources();
        String[] urlList = res.getStringArray(R.array.urls);

        //更改配置地址
        BaseConst.CHAT_DEAULT_BASE = urlList[pos];
        BaseConst.Socket_URL = urlList[pos] + "/BSCTelmed/";
        BaseConst.CHAT_PIC_URL = urlList[pos] + "/BSCTelmed";//图片地址
        BaseConst.DEAULT_URL = urlList[pos] + "/BSCTelmed";
        BaseConst.EDUCATION_URL = urlList[pos] + "/BSCEdu/";
        BaseConst.DICOMCLOUD_URL = urlList[pos] + "/DicomCloud";
        HttpReuqests.resetRequest();
        com.source.adnroid.comm.ui.net.HttpReuqests.resetRequest();
        mTitleTextView.setText(mHospitalList[pos]);
        switch (pos) {
            case 0:
                mLogoImage.setImageResource(R.mipmap.basic_logo_big);
                break;
            case 1:
                mLogoImage.setImageResource(R.mipmap.xy_logo);
                break;
            case 2:
                mLogoImage.setImageResource(R.mipmap.jz_logo);
                break;
            case 3:
                mLogoImage.setImageResource(R.mipmap.es_logo);
                break;
            case 4:
                mLogoImage.setImageResource(R.mipmap.basic_logo_big);
                break;
            default:
                mLogoImage.setImageResource(R.mipmap.basic_logo_big);
                break;
        }
    }

    //选择服务器
    private void showServerDialog() {
        new AlertDialog.Builder(this)
                .setTitle("请选择")
                //.setIcon(android.R.drawable.ic_dialog_info)
                .setSingleChoiceItems(mHospitalList, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //更改配置地址
                        BaseConst.CHAT_DEAULT_BASE = mUrlList[arg1];
                        BaseConst.Socket_URL = mUrlList[arg1] + "/BSCTelmed/";
                        BaseConst.CHAT_PIC_URL = mUrlList[arg1] + "/BSCTelmed";//图片地址
                        BaseConst.DEAULT_URL = mUrlList[arg1] + "/BSCTelmed";
                        Log.i(TAG, "Socket_URL==>" + BaseConst.Socket_URL);
                        HttpReuqests.resetRequest();
                        com.source.adnroid.comm.ui.net.HttpReuqests.resetRequest();
                        //保存当前服务器列表序号
                        SharedPreferences sp = getSharedPreferences("spUrlList", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("urlListPos", arg1);
                        editor.commit();
                        mTitleTextView.setText(mHospitalList[arg1]);
                        switch (arg1) {
                            case 0:
                                mLogoImage.setImageResource(R.mipmap.basic_logo_big);
                                break;
                            case 1:
                                mLogoImage.setImageResource(R.mipmap.xy_logo);
                                break;
                            case 2:
                                mLogoImage.setImageResource(R.mipmap.jz_logo);
                                break;
                            case 3:
                                mLogoImage.setImageResource(R.mipmap.es_logo);
                                break;
                            case 4:
                                mLogoImage.setImageResource(R.mipmap.basic_logo_big);
                                break;
                            default:
                                mLogoImage.setImageResource(R.mipmap.basic_logo_big);
                                break;
                        }
                        arg0.dismiss();
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    private void setViewListener() {
        mPhopneNumEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ("".equals(mPhopneNumEditText.getText().toString())) {
                    mClearPhoneNumImageView.setVisibility(View.GONE);
                    mLoginButton.setEnabled(false);
                } else {
                    mClearPhoneNumImageView.setVisibility(View.VISIBLE);
                    mLoginButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    int MY_PERMISSIONS_REQUEST_PHONE_STATE = 0;
    String mImei = "";

    /**
     * 6.0权限处理
     **/
    private final int WRITE_PERMISSION_REQ_CODE = 100;

    private void checkPublishPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE)) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            } else {
                TelephonyManager manager;
                manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                mImei = manager.getDeviceId();
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        WRITE_PERMISSION_REQ_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case WRITE_PERMISSION_REQ_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    TelephonyManager manager;
                    manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    mImei = manager.getDeviceId();
                }
                //bPermission = true;
                break;
            default:
                break;
        }
    }

    private String getIMEI() {
        if (!TextUtils.isEmpty(mImei)) {
            Log.i("test", "return mImei");
            return mImei;
        } else {
            Log.i("test", "return selectUUID");
            return selectUUID();
        }
    }

    private void getLogin(String username, String password, String imei) {
        Log.i(TAG, "socket url==>" + BaseConst.CHAT_DEAULT_BASE);
        Log.i(TAG, "PublicUrl.URL ==>" + BaseConst.DEAULT_URL);
        Log.i(TAG, "device imei==>" + imei);
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/auth/login");
        params.setConnectTimeout(30000);//设置请求超时
        //params.setSslSocketFactory(); // 设置ssl
        params.addBodyParameter("account", username);
        params.addBodyParameter("password", password);
        params.addBodyParameter("device", "21223654897");
        Log.i(TAG, "PublicUrl.URL" + BaseConst.DEAULT_URL + "/mobile/auth/login");
        Log.i(TAG, "username " + username.trim() + " " + password.trim() + " " + imei.trim());

        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "onSuccess==" + result);

                try {
                    JSONObject j = new JSONObject(result);
                    if (j.has("resultCode")) {
                        String resultCode = j.getString("resultCode");
                        String resultMsg = j.getString("resultMsg");
                        if (resultCode.equals("200")) {
                            if (j.has("data")) {
                                String data = j.getString("data");
                                JSONObject k = new JSONObject(data);
                                if (k.has("token")) {
                                    String token = k.getString("token");
                                    Log.i(TAG, "token: " + token);
                                    saveToken(token);
                                    goMainActivityAndFinishALL(0);
                                }
                            }
                        } else if (resultCode.equals("401")) {
                            T.showMessage(getApplicationContext(), getString(R.string.login_timeout));
                            delToken();
                            doLogout();
                        } else {
                            T.showMessage(getApplicationContext(), getString(R.string.api_error) + resultMsg);
                        }
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "e===>" + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "onError:" + ex.getMessage());
                T.showMessage(getApplicationContext(), getString(R.string.server_error));
                mLoginLoadingLayout.setVisibility(View.GONE);
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

    private void goMainActivityAndFinishALL(int i) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(FINISH_ALL, i);
        startActivity(intent);
        finish();
    }

    private void saveToken(String token) {
        SPUtils.put(LoginActivity.this, PublicUrl.TOKEN_KEY, token);
    }

    MySQLiteOpenHelper mhelper;

    private void createDatabase() {
        /* 创建一个MySQLiteOpenHelper，该语句执行是不会创建或打开连接的 */
        mhelper = new MySQLiteOpenHelper(LoginActivity.this, "com.chinabsc.telemedicine.expert.db", null, 1);
    }

    private void createTable() {
        /* 获取一个可写的SQLiteDatabase对象,创建或打开连接 */
        SQLiteDatabase sqliteDatabase = mhelper.getWritableDatabase();
        /* 创建表 */
        sqliteDatabase.execSQL("create table if not exists device(id INTEGER PRIMARY KEY autoincrement,DeviceId text);");
    }

    private void insertUUID() {
        /* 获取一个可写的SQLiteDatabase对象,创建或打开连接 */
        SQLiteDatabase sqliteDatabase = mhelper.getWritableDatabase();
        String uuid = UUID.randomUUID().toString();
        sqliteDatabase.execSQL("insert into device(DeviceId) values('" + uuid + "')");
    }

    private String selectUUID() {
        /* 获取一个可写的SQLiteDatabase对象,创建或打开连接 */
        SQLiteDatabase sqliteDatabase = mhelper.getReadableDatabase();
        Cursor cursor = sqliteDatabase.rawQuery("SELECT * FROM device", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String DeviceId = cursor.getString(cursor.getColumnIndex("DeviceId"));
                return DeviceId;
            }
        }
        return null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.i(TAG, "KEYCODE_BACK");
            goMainActivityAndFinishALL(1);
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mhelper != null) {
            mhelper.close();
        }
    }
}
