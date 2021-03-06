package com.chinabsc.telemedicine.expert.expertActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.myView.ImageDialog;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.utils.SPUtils;
import com.chinabsc.telemedicine.expert.utils.T;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.http.RequestParams;
import org.xutils.http.body.MultipartBody;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_add_scheduling)
public class AddSchedulingActivity extends BaseActivity {
    String TAG = "AddSchedulingActivity";

    public static String SCHEDULE_ID = "SCHEDULE_ID";

    //????????????
    @ViewInject(R.id.BackImageView)
    private ImageView mBackImageView;

    //???????????????
    @ViewInject(R.id.NextTextView)
    private TextView mNextTextView;
    //???????????????
    @ViewInject(R.id.PreTextView)
    private TextView mPreTextView;

    //?????????
    @ViewInject(R.id.LoadingLayout)
    private RelativeLayout mLoadingLayout;

    //????????????
    private int mPageNum = 0;

    //????????????
    @ViewInject(R.id.ProgressLayout)
    private LinearLayout mProgressLayout;

    //?????????
    @ViewInject(R.id.SectionA)
    private RelativeLayout mSectionALayout;

    //?????????
    @ViewInject(R.id.SectionB)
    private ScrollView mSectionBLayout;

    //?????????
    @ViewInject(R.id.SectionC)
    private LinearLayout mSectionCLayout;

    //??????
    private String mHospitalId;

    //??????
    private String mDepartId;
    private String mDepartName;

    //??????
    private String mDocId;
    private String mDocName;

//    //??????
//    @ViewInject(R.id.SelectHospitalText)
//    private TextView mSelectHospitalText;
//
//    //??????
//    @ViewInject(R.id.SelectDepartText)
//    private TextView mSelectDepartText;
//
//    //??????
//    @ViewInject(R.id.SelectDocText)
//    private TextView mSelectDocText;

    //??????CheckBox
    @ViewInject(R.id.EmergencyCheckBox)
    private CheckBox mEmergencyCheckBox;
    private String mEmergency;

    //?????????
    @ViewInject(R.id.IdNumEdit)
    private EditText mIdNumEdit;

    //?????????
    @ViewInject(R.id.RecordNumEdit)
    private EditText mRecordNum;

    //?????????
    @ViewInject(R.id.HospitalizationEdit)
    private EditText mHospitalizationEdit;

    //??????Edit
    @ViewInject(R.id.NameEdit)
    private EditText mNameEdit;

    //??????CheckBox
    @ViewInject(R.id.SexManCheckBox)
    private CheckBox mSexManCheckBox;
    @ViewInject(R.id.SexWoCheckBox)
    private CheckBox mSexWoCheckBox;
    private String mSex;

    //??????????????????
    @ViewInject(R.id.EthnicSpinner)
    private Spinner mEthnicSpinner;
    private String mEthnic;

    //??????????????????
    @ViewInject(R.id.MarriageSpinner)
    private Spinner mMarriageSpinner;
    private String mMarriage;

    //????????????Edit
    @ViewInject(R.id.MedicalRecordsEdit)
    private EditText mMedicalRecordsEdit;

    //????????????Edit
    @ViewInject(R.id.DiagnosisEditText)
    private EditText mDiagnosisEditText;

    //??????????????????Edit
    @ViewInject(R.id.TreatmentEditText)
    private EditText mTreatmentEditText;

    //????????????Edit
    @ViewInject(R.id.PurposeEditText)
    private EditText mPurposeEditText;

    @ViewInject(R.id.Upload1ImageView)
    private ImageView mUpload1ImageView;
    @ViewInject(R.id.Upload2ImageView)
    private ImageView mUpload2ImageView;
    @ViewInject(R.id.Upload3ImageView)
    private ImageView mUpload3ImageView;

    @ViewInject(R.id.Del1ImageView)
    private ImageView mDel1ImageView;
    @ViewInject(R.id.Del2ImageView)
    private ImageView mDel2ImageView;
    @ViewInject(R.id.Del3ImageView)
    private ImageView mDel3ImageView;

    //???????????????????????????
    public ArrayList mImagePathList = new ArrayList();

    public static final int RESULT_LOAD_IMAGE_1 = 1;
    public static final int RESULT_LOAD_IMAGE_2 = 2;
    public static final int RESULT_LOAD_IMAGE_3 = 3;
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 0x03;

    //ImageLoader
    public DisplayImageOptions mOptions;
    public ImageLoader mImageLoader = ImageLoader.getInstance();

    @Event(value = {
            R.id.BackImageView,
            R.id.PreTextView,
            R.id.NextTextView,
            R.id.ProvinceText,
            R.id.AllianceText,
            R.id.SpecializedText,
            R.id.SelfHospitalText,
            R.id.PutButton}, type = View.OnClickListener.class)
    private void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.BackImageView:
                showFinishDialog();
                break;
            case R.id.PreTextView:
                setPrePage();
                break;
            case R.id.NextTextView:
                setNextPage();
                break;
            case R.id.PutButton:
                sendAllData();
                break;
        }
    }

    //????????????
    private String mUserId = "";
    //??????id
    private String mSchedulingId = "";

    //??????????????????
    List<String> mProvinceNameList = new ArrayList<String>();
    //??????id??????
    List<String> mProvinceCodeList = new ArrayList<String>();

    //??????????????????
    List<String> mHospitalNameList = new ArrayList<String>();
    //??????id??????
    List<String> mHospitalCodeList = new ArrayList<String>();

    //??????????????????
    List<String> mDepartNameList = new ArrayList<String>();
    //??????id??????
    List<String> mDepartCodeList = new ArrayList<String>();

    //??????????????????
    List<String> mDocNameList = new ArrayList<String>();
    //??????id??????
    List<String> mDocCodeList = new ArrayList<String>();

    //??????????????????
    List<String> mEthnicNameList = new ArrayList<String>();
    //??????id??????
    List<String> mEthnicCodeList = new ArrayList<String>();

    //??????????????????
    List<String> mMarriageNameList = new ArrayList<String>();
    //??????id??????
    List<String> mMarriageCodeList = new ArrayList<String>();

    //??????????????????
    List<String> mCareerNameList = new ArrayList<String>();
    //??????id??????
    List<String> mCareerCodeList = new ArrayList<String>();

    //????????????????????????
    List<String> mConditionNameList = new ArrayList<String>();
    //????????????id??????
    List<String> mConditionCodeList = new ArrayList<String>();

    //??????????????????
    List<String> mInsuranceNameList = new ArrayList<String>();
    //??????id??????
    List<String> mInsuranceCodeList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        Intent i = getIntent();
        mSchedulingId = i.getStringExtra(SCHEDULE_ID);

        String token = getTokenFromLocal();
        String userId = SPUtils.get(AddSchedulingActivity.this, PublicUrl.USER_ID_KEY, "").toString();
        mUserId = userId;
        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(userId)) {
            getEthnicData();
            getMarriageData();
        }
        init();
    }

    //??????????????????
    private void initPage() {
        mSectionALayout.setVisibility(View.GONE);
        mSectionBLayout.setVisibility(View.GONE);
        mSectionCLayout.setVisibility(View.GONE);
    }

    //???????????????
    private void setNextPage() {
        switch (mPageNum) {
            case 0:
                String idNum = mIdNumEdit.getText().toString();
                if (TextUtils.isEmpty(mNameEdit.getText().toString())
                        || TextUtils.isEmpty(mSex)
                        || TextUtils.isEmpty(mEthnic)
                        || TextUtils.isEmpty(mMarriage)
                        ) {
                    T.showMessage(getApplicationContext(), "?????????*??????");
                } else if (!PublicUrl.isIDCard(idNum) || TextUtils.isEmpty(idNum)) {
                    T.showMessage(getApplicationContext(), "???????????????????????????");
                } else {
                    initPage();
                    mSectionBLayout.setVisibility(View.VISIBLE);
                    mPageNum = mPageNum + 1;
                    mBackImageView.setVisibility(View.INVISIBLE);
                    mPreTextView.setVisibility(View.VISIBLE);
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(mMedicalRecordsEdit.getText().toString())
                        && !TextUtils.isEmpty(mDiagnosisEditText.getText().toString())) {
                    initPage();
                    mSectionCLayout.setVisibility(View.VISIBLE);
                    mPageNum = mPageNum + 1;
                    mBackImageView.setVisibility(View.INVISIBLE);
                    mPreTextView.setVisibility(View.VISIBLE);
                    mNextTextView.setVisibility(View.INVISIBLE);
                } else {
                    T.showMessage(getApplicationContext(), "?????????*??????");
                }
                break;
            default:
                break;
        }
        setAddProgress();
    }

    //???????????????
    private void setPrePage() {
        mPageNum = mPageNum - 1;
        mNextTextView.setVisibility(View.VISIBLE);
        switch (mPageNum) {
            case 0:
                initPage();
                mSectionALayout.setVisibility(View.VISIBLE);
                mPreTextView.setVisibility(View.GONE);
                mBackImageView.setVisibility(View.VISIBLE);
                break;
            case 1:
                initPage();
                mSectionBLayout.setVisibility(View.VISIBLE);
                break;
            case 2:
                initPage();
                mSectionCLayout.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
        setAddProgress();
    }

    //????????????
    private void setAddProgress() {
        switch (mPageNum) {
            case 0:
                //mProgressLayout.setBackground(getResources().getDrawable(R.drawable.add_type_1));
                break;
            case 1:
                //mProgressLayout.setBackground(getResources().getDrawable(R.drawable.add_type_2));
                break;
            case 2:
                //mProgressLayout.setBackground(getResources().getDrawable(R.drawable.add_type_3));
                break;
            case 3:
                //mProgressLayout.setBackground(getResources().getDrawable(R.drawable.add_type_4));
                break;
            default:
                break;
        }
    }

    //???????????????
    private void init() {
        mImageLoader.init(ImageLoaderConfiguration.createDefault(AddSchedulingActivity.this));
        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.basic_image_download) //??????????????????????????????????????????
                .showImageForEmptyUri(R.drawable.basic_image_error2)//????????????Uri??????????????????????????????????????????
                .showImageOnFail(R.drawable.basic_image_error2)  //??????????????????/??????????????????????????????????????????
                .cacheInMemory(true)//?????????????????????????????????????????????
                .cacheOnDisk(true)//????????????????????????????????????SD??????
                .considerExifParams(true)  //????????????JPEG??????EXIF???????????????????????????
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//??????????????????????????????????????????
                .bitmapConfig(Bitmap.Config.RGB_565)//???????????????????????????//
                .resetViewBeforeLoading(true)//?????????????????????????????????????????????
                .build();//????????????

        setViewListener();

        //??????CheckBox
        mEmergencyCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mEmergency = "02";
                } else {
                    mEmergency = "01";
                }
            }
        });

        //??????CheckBox
        mSexManCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mSex = "0";
                    mSexWoCheckBox.setChecked(false);
                } else {
                    mSex = "";
                }
            }
        });

        mSexWoCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mSex = "1";
                    mSexManCheckBox.setChecked(false);
                } else {
                    mSex = "";
                }
            }
        });

        //??????????????????
        ArrayAdapter<String> ethnicAdapter = new ArrayAdapter<String>(this, R.layout.spinner_text, mEthnicNameList);
        ethnicAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEthnicSpinner.setAdapter(ethnicAdapter);
        mEthnicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                mEthnic = mEthnicNameList.get(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        //??????????????????
        ArrayAdapter<String> marriageAdapter = new ArrayAdapter<String>(this, R.layout.spinner_text, mMarriageNameList);
        marriageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMarriageSpinner.setAdapter(marriageAdapter);
        mMarriageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                mMarriage = mMarriageNameList.get(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    //????????????????????????
    private void setViewListener() {
        mUpload1ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getPathListAvailable(0)) {
                    showTakePhotoTapyDialog(RESULT_LOAD_IMAGE_1);
                } else {
                    showImageDialog(mImagePathList.get(0).toString());
                }
            }
        });

        mUpload2ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getPathListAvailable(1)) {
                    showTakePhotoTapyDialog(RESULT_LOAD_IMAGE_2);
                } else {
                    showImageDialog(mImagePathList.get(1).toString());
                }
            }
        });

        mUpload3ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getPathListAvailable(2)) {
                    showTakePhotoTapyDialog(RESULT_LOAD_IMAGE_3);
                } else {
                    showImageDialog(mImagePathList.get(2).toString());
                }
            }
        });

        mDel1ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delImage(1);
            }
        });

        mDel2ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delImage(2);
            }
        });

        mDel3ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delImage(3);
            }
        });
    }

    //??????????????????
    private int mPhotoNum = 0;

    //???????????????Dialog
    private void showTakePhotoTapyDialog(int j) {
        mPhotoNum = j;
        AlertDialog.Builder builder = new AlertDialog.Builder(AddSchedulingActivity.this);
        builder.setTitle("????????????");
        builder.setItems(new String[]{"??????", "??????"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which) {
                    case 0:
                        openImageLib(mPhotoNum);
                        break;
                    case 1:
                        takePhoto(mPhotoNum);
                        break;
                    default:
                        break;
                }
            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    //????????????Dialog
    private void showImageDialog(String url) {
        ImageDialog imageDialog;
        imageDialog = new ImageDialog(AddSchedulingActivity.this, R.style.dialog, url);
        imageDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = imageDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
    }

    //????????????
    private void openImageLib(int j) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(AddSchedulingActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(AddSchedulingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(AddSchedulingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(AddSchedulingActivity.this, Manifest.permission.CAMERA)) {
                    Toast.makeText(AddSchedulingActivity.this, "????????????????????????", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSIONS_REQUEST_CODE);
            }
        }
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, j);
    }

    //??????????????????
    private String mPhotoName = "";

    //????????????
    private void takePhoto(int j) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(AddSchedulingActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(AddSchedulingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(AddSchedulingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(AddSchedulingActivity.this, Manifest.permission.CAMERA)) {
                    Toast.makeText(AddSchedulingActivity.this, "????????????????????????", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSIONS_REQUEST_CODE);
            }
        }
        mPhotoName = "BSC_" + getDate() + ".jpg";
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/ChinaBSC/", mPhotoName);
        Uri uri = Uri.fromFile(file);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(AddSchedulingActivity.this, "com.chinabsc.telemedicine.expert.fileprovider", file);
        }
        PublicUrl.isFolderExists(Environment.getExternalStorageDirectory().toString() + "/ChinaBSC/");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, j);
    }

    //???????????????
    public String getDate() {
        return Long.toString(System.currentTimeMillis());
    }

    //????????????????????????
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String picturePath = "";
        Log.i(TAG, "resultCode:" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                Log.i(TAG, "picturePath:" + picturePath);
                mImagePathList.add(picturePath);
                cursor.close();

                if (requestCode > 0 && requestCode < 4) {
                    switch (requestCode) {
                        case 1:
                            setImage();
                            break;
                        case 2:
                            setImage();
                            break;
                        case 3:
                            setImage();
                            break;
                        default:
                            break;
                    }
                }
            } else {
                File picture = new File(Environment.getExternalStorageDirectory().toString() + "/ChinaBSC/" + mPhotoName);
                mImagePathList.add(picture);
                setImage();
            }
        }
    }

    //????????????
    private void setImage() {
        initializeView();
        for (int i = 1; i <= mImagePathList.size(); i++) {
            Log.e(TAG, mImagePathList.size() + "");
            showDel(i);
            if (i < 3) {
                showNext(i + 1);
            }
            String url = mImagePathList.get(i - 1).toString();
            mImageLoader.displayImage("file:///" + url, getUploadImageView(i), mOptions);
        }
    }

    //????????????
    private void delImage(int i) {
        switch (i) {
            case 1:
                mImagePathList.remove(i - 1);
                setImage();
                break;
            case 2:
                mImagePathList.remove(i - 1);
                setImage();
                break;
            case 3:
                mImagePathList.remove(i - 1);
                setImage();
                break;
            default:
                break;
        }
    }

    //??????????????????
    private void showDel(int i) {
        switch (i) {
            case 1:
                mDel1ImageView.setVisibility(View.VISIBLE);
                break;
            case 2:
                mDel2ImageView.setVisibility(View.VISIBLE);
                break;
            case 3:
                mDel3ImageView.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    //????????????????????????????????????
    private void showNext(int i) {
        switch (i) {
            case 2:
                mUpload2ImageView.setVisibility(View.VISIBLE);
                break;
            case 3:
                mUpload3ImageView.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    //???????????????????????????
    private void initializeView() {
        for (int i = 1; i < 4; i++) {
            getUploadImageView(i).setImageBitmap(null);
            getUploadImageView(i).setVisibility(View.INVISIBLE);
            getDelImageView(i).setVisibility(View.GONE);
        }
        mUpload1ImageView.setVisibility(View.VISIBLE);
    }

    //??????????????????????????????
    private ImageView getUploadImageView(int i) {
        switch (i) {
            case 1:
                return mUpload1ImageView;
            case 2:
                return mUpload2ImageView;
            case 3:
                return mUpload3ImageView;
            default:
                break;
        }
        return null;
    }

    //????????????????????????????????????
    private ImageView getDelImageView(int i) {
        switch (i) {
            case 1:
                return mDel1ImageView;
            case 2:
                return mDel2ImageView;
            case 3:
                return mDel3ImageView;
            default:
                break;
        }
        return null;
    }

    //?????????????????????????????????????????????
    private boolean getPathListAvailable(int n) {
        if (mImagePathList.size() > n) {
            return false;
        } else {
            return true;
        }
    }

    //??????????????????
    private void delProvince() {
        mProvinceNameList.clear();
        mProvinceCodeList.clear();
    }

    //??????????????????
    private void delHospital() {
        mHospitalNameList.clear();
        mHospitalCodeList.clear();
    }

    //??????????????????
    private void delDepartment() {
        mDepartNameList.clear();
        mDepartCodeList.clear();
        mDepartId = "";
        mDepartName = "";
    }

    //??????????????????
    private void delDoc() {
        mDocNameList.clear();
        mDocCodeList.clear();
        mDocId = "";
        mDocName = "";
    }

    //??????????????????
    private void getProvince() {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/clinic/provinceList");
        //params.setSslSocketFactory(); // ??????ssl
        params.addHeader("authorization", getTokenFromLocal());
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "getProvince:" + result);
                com.alibaba.fastjson.JSONObject j = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.parse(result);
                String resultCode = j.getString("resultCode");
                String resultMsg = j.getString("resultMsg");
                if (resultCode.equals("200")) {
                    com.alibaba.fastjson.JSONArray dataArray = j.getJSONArray("data");
                    for (int a = 0; a < dataArray.size(); a++) {
                        String itemCode = dataArray.getJSONObject(a).get("itemcode").toString();
                        if (!itemCode.equals("0")) {
                            mProvinceNameList.add(dataArray.getJSONObject(a).get("itemname").toString());
                            mProvinceCodeList.add(itemCode);
                        }

                    }
                } else {
                    T.showMessage(AddSchedulingActivity.this, getString(R.string.api_error) + resultMsg);
                    delToken();
                    doLogout();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "onError:" + ex.getMessage());
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

    //??????????????????
    private void getHospital(String itemCode) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/clinic/getSiteList?regionCode=" + itemCode + "&userId=" + mUserId);
        //params.setSslSocketFactory(); // ??????ssl
        params.addHeader("authorization", getTokenFromLocal());
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                mLoadingLayout.setVisibility(View.GONE);
                Log.i(TAG, "getHospitalData:" + result);
                com.alibaba.fastjson.JSONObject j = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.parse(result);
                String resultCode = j.getString("resultCode");
                String resultMsg = j.getString("resultMsg");
                if (resultCode.equals("200")) {
                    com.alibaba.fastjson.JSONArray dataArray = j.getJSONArray("data");
                    mHospitalNameList.clear();
                    mHospitalCodeList.clear();
                    for (int a = 0; a < dataArray.size(); a++) {
                        mHospitalNameList.add(dataArray.getJSONObject(a).get("sitename").toString());
                        mHospitalCodeList.add(dataArray.getJSONObject(a).get("siteid").toString());
                    }
                } else {
                    T.showMessage(AddSchedulingActivity.this, getString(R.string.api_error) + resultMsg);
                    delToken();
                    doLogout();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "onError:" + ex.getMessage());
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

    //??????????????????
    private void getDepartment(String itemCode) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/clinic/getDepartList?siteid=" + itemCode);
        //params.setSslSocketFactory(); // ??????ssl
        params.addHeader("authorization", getTokenFromLocal());
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                mLoadingLayout.setVisibility(View.GONE);
                Log.i(TAG, "getDepartment:" + result);
                com.alibaba.fastjson.JSONObject j = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.parse(result);
                String resultCode = j.getString("resultCode");
                String resultMsg = j.getString("resultMsg");
                if (resultCode.equals("200")) {
                    com.alibaba.fastjson.JSONArray dataArray = j.getJSONArray("data");
                    mDepartNameList.clear();
                    mDepartCodeList.clear();
                    for (int a = 0; a < dataArray.size(); a++) {
                        mDepartNameList.add(dataArray.getJSONObject(a).get("departname").toString());
                        mDepartCodeList.add(dataArray.getJSONObject(a).get("departid").toString());
                    }
                } else {
                    T.showMessage(AddSchedulingActivity.this, getString(R.string.api_error) + resultMsg);
                    delToken();
                    doLogout();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "onError:" + ex.getMessage());
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

    //??????????????????
    private void getDoc(String itemCode) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/clinic/getExpertList?departId=" + itemCode);
        //params.setSslSocketFactory(); // ??????ssl
        params.addHeader("authorization", getTokenFromLocal());
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                mLoadingLayout.setVisibility(View.GONE);
                Log.i(TAG, "getDepartment:" + result);
                com.alibaba.fastjson.JSONObject j = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.parse(result);
                String resultCode = j.getString("resultCode");
                String resultMsg = j.getString("resultMsg");
                if (resultCode.equals("200")) {
                    com.alibaba.fastjson.JSONArray dataArray = j.getJSONArray("data");
                    mDocNameList.clear();
                    mDocCodeList.clear();
                    for (int a = 0; a < dataArray.size(); a++) {
                        mDocNameList.add(dataArray.getJSONObject(a).get("username").toString());
                        mDocCodeList.add(dataArray.getJSONObject(a).get("userid").toString());
                    }
                } else {
                    T.showMessage(AddSchedulingActivity.this, getString(R.string.api_error) + resultMsg);
                    delToken();
                    doLogout();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "onError:" + ex.getMessage());
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

    //??????????????????
    private void getEthnicData() {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/clinic/getNations");
        //params.setSslSocketFactory(); // ??????ssl
        params.addHeader("authorization", getTokenFromLocal());
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                com.alibaba.fastjson.JSONObject j = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.parse(result);
                String resultCode = j.getString("resultCode");
                String resultMsg = j.getString("resultMsg");
                if (resultCode.equals("200")) {
                    com.alibaba.fastjson.JSONArray dataArray = j.getJSONArray("data");
                    mEthnicNameList.clear();
                    mEthnicCodeList.clear();
                    for (int a = 0; a < dataArray.size(); a++) {
                        mEthnicNameList.add(dataArray.getJSONObject(a).get("itemname").toString());
                        mEthnicCodeList.add(dataArray.getJSONObject(a).get("itemid").toString());
                    }
                    ((ArrayAdapter) mEthnicSpinner.getAdapter()).notifyDataSetChanged();
                } else {
                    T.showMessage(AddSchedulingActivity.this, getString(R.string.api_error) + resultMsg);
                    delToken();
                    doLogout();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
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

    //??????????????????
    private void getMarriageData() {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/clinic/getMarriages");
        //params.setSslSocketFactory(); // ??????ssl
        params.addHeader("authorization", getTokenFromLocal());
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                com.alibaba.fastjson.JSONObject j = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.parse(result);
                String resultCode = j.getString("resultCode");
                String resultMsg = j.getString("resultMsg");
                if (resultCode.equals("200")) {
                    com.alibaba.fastjson.JSONArray dataArray = j.getJSONArray("data");
                    mMarriageNameList.clear();
                    mMarriageCodeList.clear();
                    for (int a = 0; a < dataArray.size(); a++) {
                        mMarriageNameList.add(dataArray.getJSONObject(a).get("itemname").toString());
                        mMarriageCodeList.add(dataArray.getJSONObject(a).get("itemid").toString());
                    }
                    ((ArrayAdapter) mMarriageSpinner.getAdapter()).notifyDataSetChanged();
                } else {
                    T.showMessage(AddSchedulingActivity.this, getString(R.string.api_error) + resultMsg);
                    delToken();
                    doLogout();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
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

    //??????????????????
    private void sendAllData() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/outPatient/outClinic/doAdd");
        //params.setSslSocketFactory(); // ??????ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.setMultipart(true);
        params.setAsJsonContent(true);
        //????????????id
        params.addBodyParameter("userId", mUserId);
        //??????id
        params.addBodyParameter("scheduleId", mSchedulingId);
        //??????
        params.addBodyParameter("clinictype", mEmergency);
        //?????????
        params.addBodyParameter("card_id", mIdNumEdit.getText().toString());
        //??????
        String birth = mIdNumEdit.getText().toString();
        String year = birth.substring(6, 10);
        String month = birth.substring(10, 12);
        String day = birth.substring(12, 14);
        birth = year + "-" + month + "-" + day;
        params.addBodyParameter("birthdate", birth);
        //?????????
        params.addBodyParameter("hisrecordid", mRecordNum.getText().toString());
        //?????????
        params.addBodyParameter("visitid", mHospitalizationEdit.getText().toString());
        //??????
        params.addBodyParameter("fullname", mNameEdit.getText().toString());
        //??????
        params.addBodyParameter("gender", mSex);
        //??????
        params.addBodyParameter("folk", mEthnic);
        //??????
        params.addBodyParameter("marriage", mMarriage);
        //????????????
        params.addBodyParameter("history", mMedicalRecordsEdit.getText().toString());
        //????????????
        params.addBodyParameter("diagnosis", mDiagnosisEditText.getText().toString());
        //????????????
        params.addBodyParameter("treatment", mTreatmentEditText.getText().toString());
        //????????????
        params.addBodyParameter("purpose", mPurposeEditText.getText().toString());
        //??????
        List<KeyValue> list = new ArrayList<>();
        for (int i = 0; i < mImagePathList.size(); i++) {
            list.add(new KeyValue("file", new File(mImagePathList.get(i).toString())));
        }
        MultipartBody body = new MultipartBody(list, "UTF-8");
        params.setRequestBody(body);

        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "sendAllData==" + result);
                com.alibaba.fastjson.JSONObject j = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.parse(result);
                String resultCode = j.getString("resultCode");
                String resultMsg = j.getString("resultMsg");
                if (resultCode.equals("200")) {
                    T.showMessage(AddSchedulingActivity.this, "????????????");
//                    Intent intent = new Intent(AddSchedulingActivity.this, RecordsActivity.class);
//                    startActivity(intent);
                    finish();
                } else {
                    T.showMessage(AddSchedulingActivity.this, getString(R.string.api_error) + resultMsg);
                    delToken();
                    doLogout();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "sendAllData onError:" + ex.getMessage());
                T.showMessage(getApplicationContext(), getString(R.string.server_error));
            }

            @Override
            public void onCancelled(CancelledException cex) {
                //Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {
                mLoadingLayout.setVisibility(View.GONE);
            }
        });
    }

    private void showFinishDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddSchedulingActivity.this);
        builder.setTitle("??????");
        builder.setMessage("??????????????????");
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
