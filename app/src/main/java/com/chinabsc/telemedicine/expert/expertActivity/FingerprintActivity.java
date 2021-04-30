package com.chinabsc.telemedicine.expert.expertActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;

import com.chinabsc.telemedicine.expert.R;

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintActivity extends FragmentActivity {
    FingerprintManager manager;
    KeyguardManager mKeyManager;
    TextView mTextView;
    private final static int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);
        mTextView = (TextView) findViewById(R.id.textView);
        Intent intent = new Intent(FingerprintActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager = (FingerprintManager) this.getSystemService(Context.FINGERPRINT_SERVICE);
            mKeyManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
            if (isFinger()) {
                setFingerprintManager();
                startListening(null);
            } else {
                Intent intent = new Intent(FingerprintActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            Intent intent = new Intent(FingerprintActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }*/
    }

    public boolean isFinger() {

        //android studio 上，没有这个会报错
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            mTextView.setText("没有指纹识别权限");
            return false;
        }
        Log.i("test", "有指纹权限");

        //判断硬件是否支持指纹识别
        if (!manager.isHardwareDetected()) {
            mTextView.setText("没有指纹识别模块");
            return false;
        }
        Log.i("test", "有指纹模块");

//        //判断 是否开启锁屏密码
//        if (!mKeyManager.isKeyguardSecure()) {
//            //Toast.makeText(this, "没有开启锁屏密码", Toast.LENGTH_SHORT).show();
//            mTextView.setText("没有开启锁屏密码");
//            return false;
//        }
//        Log(TAG, "已开启锁屏密码");

        //判断是否有指纹录入
        if (!manager.hasEnrolledFingerprints()) {
            mTextView.setText("没有录入指纹");
            return false;
        }
        Log.i("test", "已录入指纹");

        return true;
    }

    CancellationSignal mCancellationSignal = new CancellationSignal();
    FingerprintManager.AuthenticationCallback mSelfCancelled;

    void setFingerprintManager() {
        //回调方法
        mSelfCancelled = new FingerprintManager.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                //但多次指纹密码验证错误后，进入此方法；并且，不能短时间内调用指纹验证
                mTextView.setText("多次识别失败，请退出应用，稍后重试");
                //showAuthenticationScreen();
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                //mTextView.setText(helpString);
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                mTextView.setText("指纹识别成功");
                Intent intent = new Intent(FingerprintActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                mTextView.setText("指纹识别失败，请重试");
            }
        };
    }

    public void startListening(FingerprintManager.CryptoObject cryptoObject) {
        //android studio 上，没有这个会报错
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            mTextView.setText("没有指纹识别权限");
            return;
        }
        manager.authenticate(cryptoObject, mCancellationSignal, 0, mSelfCancelled, null);


    }

    /**
     * 锁屏密码
     */
//    private void showAuthenticationScreen() {
//
//        Intent intent = mKeyManager.createConfirmDeviceCredentialIntent("finger", "测试指纹识别");
//        if (intent != null) {
//            startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS) {
//            // Challenge completed, proceed with using cipher
//            if (resultCode == RESULT_OK) {
//                //Toast.makeText(this, "识别成功", Toast.LENGTH_SHORT).show();
//                mTextView.setText("识别成功");
//            } else {
//                //Toast.makeText(this, "识别失败", Toast.LENGTH_SHORT).show();
//                mTextView.setText("识别失败");
//            }
//        }
//    }

}