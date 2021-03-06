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
import com.chinabsc.telemedicine.expert.entity.DocMenuData;
import com.chinabsc.telemedicine.expert.myView.DocAllianceDialog;
import com.chinabsc.telemedicine.expert.myView.DocProvinceDialog;
import com.chinabsc.telemedicine.expert.myView.DocSelfDialog;
import com.chinabsc.telemedicine.expert.myView.DocSpecializedDialog;
import com.chinabsc.telemedicine.expert.myView.ImageDialog;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.utils.SPUtils;
import com.chinabsc.telemedicine.expert.utils.T;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
import java.util.UUID;

@ContentView(R.layout.activity_add_consultation)
public class AddConsultationActivity extends BaseActivity {
    String TAG = "AddConsultationActivity";

    public static String HIS_ID = "HIS_ID";
    public static String HOSPITAL_NAME = "HOSPITAL_NAME";
    public static String DEPART_ID = "DEPART_ID";
    public static String DEPART_NAME = "DEPART_NAME";
    public static String DOC_ID = "DOC_ID";
    public static String DOC_NAME = "DOC_NAME";

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
    private RelativeLayout mSectionBLayout;

    //?????????
    @ViewInject(R.id.SectionC)
    private ScrollView mSectionCLayout;

    //?????????
    @ViewInject(R.id.SectionD)
    private LinearLayout mSectionDLayout;

    //??????
    private String mProvince;

    //??????
    private String mHospitalId;

    //??????his_id
    //???????????????????????????????????????
    private String mHisId;
    private String mHisSiteId;

    //??????
    private String mDepartId;
    private String mDepartName;

    //??????
    private String mDocId;
    private String mDocName;

    //????????????
    @ViewInject(R.id.MassageLayout)
    private RelativeLayout mMassageLayout;

    //??????
    @ViewInject(R.id.SelectHospitalText)
    private TextView mSelectHospitalText;

    //??????
    @ViewInject(R.id.SelectDepartText)
    private TextView mSelectDepartText;

    //??????
    @ViewInject(R.id.SelectDocText)
    private TextView mSelectDocText;

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
            case R.id.ProvinceText:
                final DocProvinceDialog dialog = new DocProvinceDialog(AddConsultationActivity.this);
                dialog.setonItemClickListener(new DocProvinceDialog.MenuItemClickListener() {
                    @Override
                    public void onMenuItemClick(DocMenuData provinceData, DocMenuData hospitalData, DocMenuData departData, DocMenuData docData) {
                        if (provinceData != null & hospitalData != null & departData != null & docData != null) {
                            mMassageLayout.setVisibility(View.VISIBLE);
                            mSelectHospitalText.setText("?????????" + hospitalData.name);
                            mSelectDepartText.setText("?????????" + departData.name);
                            mSelectDocText.setText("?????????" + docData.name);
                        }
                        mHospitalId = hospitalData.id;
                        mDepartId = departData.id;
                        mDepartName = departData.name;
                        mDocId = docData.id;
                        mDocName = docData.name;
                    }
                });
                dialog.show();
                break;
            case R.id.AllianceText:
                final DocAllianceDialog dialog2 = new DocAllianceDialog(AddConsultationActivity.this);
                dialog2.setonItemClickListener(new DocAllianceDialog.MenuItemClickListener() {
                    @Override
                    public void onMenuItemClick(DocMenuData provinceData, DocMenuData hospitalData, DocMenuData departData, DocMenuData docData) {
                        if (provinceData != null & hospitalData != null & departData != null & docData != null) {
                            mMassageLayout.setVisibility(View.VISIBLE);
                            mSelectHospitalText.setText("?????????" + hospitalData.name);
                            mSelectDepartText.setText("?????????" + departData.name);
                            mSelectDocText.setText("?????????" + docData.name);
                        }
                        mHospitalId = hospitalData.id;
                        mDepartId = departData.id;
                        mDepartName = departData.name;
                        mDocId = docData.id;
                        mDocName = docData.name;
                    }
                });
                dialog2.show();
                break;
            case R.id.SpecializedText:
                final DocSpecializedDialog dialog3 = new DocSpecializedDialog(AddConsultationActivity.this);
                dialog3.setonItemClickListener(new DocSpecializedDialog.MenuItemClickListener() {
                    @Override
                    public void onMenuItemClick(DocMenuData provinceData, DocMenuData hospitalData, DocMenuData departData, DocMenuData docData) {
                        if (provinceData != null & hospitalData != null & departData != null & docData != null) {
                            mMassageLayout.setVisibility(View.VISIBLE);
                            mSelectHospitalText.setText("?????????" + hospitalData.name);
                            mSelectDepartText.setText("?????????" + departData.name);
                            mSelectDocText.setText("?????????" + docData.name);
                        }
                        mHospitalId = hospitalData.id;
                        mDepartId = departData.id;
                        mDepartName = departData.name;
                        mDocId = docData.id;
                        mDocName = docData.name;
                    }
                });
                dialog3.show();
                break;
            case R.id.SelfHospitalText:
                final DocSelfDialog dialog4 = new DocSelfDialog(AddConsultationActivity.this);
                dialog4.setonItemClickListener(new DocSelfDialog.MenuItemClickListener() {
                    @Override
                    public void onMenuItemClick(DocMenuData provinceData, DocMenuData hospitalData, DocMenuData departData, DocMenuData docData) {
                        if (provinceData != null & hospitalData != null & departData != null & docData != null) {
                            mMassageLayout.setVisibility(View.VISIBLE);
                            mSelectHospitalText.setText("?????????" + hospitalData.name);
                            mSelectDepartText.setText("?????????" + departData.name);
                            mSelectDocText.setText("?????????" + docData.name);
                        }
                        mHospitalId = hospitalData.id;
                        mDepartId = departData.id;
                        mDepartName = departData.name;
                        mDocId = docData.id;
                        mDocName = docData.name;
                    }
                });
                dialog4.show();
                break;
            case R.id.PutButton:
                sendAllData();
                break;
        }
    }

    private String mUserId = "";

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
        String hisid = i.getStringExtra(HIS_ID);
        //String hisid = "724198";
        mHisId = hisid;
        Log.i(TAG, "mHisId==" + mHisId);
        //hisid???????????????????????????????????????????????????hisid??????????????????
        if (!TextUtils.isEmpty(hisid)) {
            getInfoByHisID(hisid);
        }
        String hospitalName = i.getStringExtra(HOSPITAL_NAME);
        mDepartId = i.getStringExtra(DEPART_ID);
        mDepartName = i.getStringExtra(DEPART_NAME);
        mDocId = i.getStringExtra(DOC_ID);
        mDocName = i.getStringExtra(DOC_NAME);

        String token = getTokenFromLocal();
        String userId = SPUtils.get(AddConsultationActivity.this, PublicUrl.USER_ID_KEY, "").toString();
        mUserId = userId;
        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(userId)) {
            if (!TextUtils.isEmpty(hospitalName)) {
                mMassageLayout.setVisibility(View.VISIBLE);
                mSelectHospitalText.setText("?????????" + hospitalName);
                mSelectDepartText.setText("?????????" + mDepartName);
                mSelectDocText.setText("?????????" + mDocName);
            }
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
        mSectionDLayout.setVisibility(View.GONE);
    }

    //???????????????
    private void setNextPage() {
        switch (mPageNum) {
            case 0:
                if (!TextUtils.isEmpty(mDocName)) {
                    initPage();
                    mSectionBLayout.setVisibility(View.VISIBLE);
                    mPageNum = mPageNum + 1;
                    mBackImageView.setVisibility(View.INVISIBLE);
                    mPreTextView.setVisibility(View.VISIBLE);
                } else {
                    T.showMessage(getApplicationContext(), "???????????????");
                }
                break;
            case 1:
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
                    mSectionCLayout.setVisibility(View.VISIBLE);
                    mPageNum = mPageNum + 1;
                    mBackImageView.setVisibility(View.INVISIBLE);
                    mPreTextView.setVisibility(View.VISIBLE);
                }
                break;
            case 2:
                if (!TextUtils.isEmpty(mMedicalRecordsEdit.getText().toString())
                        && !TextUtils.isEmpty(mDiagnosisEditText.getText().toString())) {
                    initPage();
                    mSectionDLayout.setVisibility(View.VISIBLE);
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
            case 3:
                initPage();
                mSectionDLayout.setVisibility(View.VISIBLE);
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
                mProgressLayout.setBackground(getResources().getDrawable(R.drawable.add_type_1));
                break;
            case 1:
                mProgressLayout.setBackground(getResources().getDrawable(R.drawable.add_type_2));
                break;
            case 2:
                mProgressLayout.setBackground(getResources().getDrawable(R.drawable.add_type_3));
                break;
            case 3:
                mProgressLayout.setBackground(getResources().getDrawable(R.drawable.add_type_4));
                break;
            default:
                break;
        }
    }

    //???????????????
    private void init() {
        mImageLoader.init(ImageLoaderConfiguration.createDefault(AddConsultationActivity.this));
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
        AlertDialog.Builder builder = new AlertDialog.Builder(AddConsultationActivity.this);
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
        imageDialog = new ImageDialog(AddConsultationActivity.this, R.style.dialog, url);
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
            if (ContextCompat.checkSelfPermission(AddConsultationActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(AddConsultationActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(AddConsultationActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(AddConsultationActivity.this, Manifest.permission.CAMERA)) {
                    Toast.makeText(AddConsultationActivity.this, "????????????????????????", Toast.LENGTH_SHORT).show();
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
            if (ContextCompat.checkSelfPermission(AddConsultationActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(AddConsultationActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(AddConsultationActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(AddConsultationActivity.this, Manifest.permission.CAMERA)) {
                    Toast.makeText(AddConsultationActivity.this, "????????????????????????", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSIONS_REQUEST_CODE);
            }
        }
        mPhotoName = "BSC_" + getDate() + ".jpg";
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/ChinaBSC/", mPhotoName);
        Uri uri = Uri.fromFile(file);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(AddConsultationActivity.this, "com.chinabsc.telemedicine.expert.fileprovider", file);
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
                    T.showMessage(AddConsultationActivity.this, getString(R.string.api_error) + resultMsg);
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
                    T.showMessage(AddConsultationActivity.this, getString(R.string.api_error) + resultMsg);
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
                    T.showMessage(AddConsultationActivity.this, getString(R.string.api_error) + resultMsg);
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
                    T.showMessage(AddConsultationActivity.this, getString(R.string.api_error) + resultMsg);
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

    //??????hisid??????????????????
    private void getInfoByHisID(String hisid) {
        RequestParams params = new RequestParams(BaseConst.MIDDLE_URL + "/api/patient?filterField=his_id&filterValue=" + hisid);
        Log.i(TAG, "his??????????????????url==" + BaseConst.MIDDLE_URL + "/api/patient?filterField=his_id&filterValue=" + hisid);
        //params.setSslSocketFactory(); // ??????ssl
        params.addHeader("authorization", getTokenFromLocal());
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "his??????????????????==" + result);
                // TODO: 2018-12-22 ?????????????????????????????????????????????????????? Alt+Insert GsonFormat
                try {
                    JSONObject j = new JSONObject(result);
                    if (j.has("code")) {
                        String resultCode = j.getString("code");
                        if (resultCode.equals("001")) {
                            if (j.has("data")) {
                                String data = j.getString("data");
                                JSONArray array = new JSONArray(data);
                                JSONObject k = array.getJSONObject(0);
                                //??????id
                                if (k.has("siteId")) {
                                    mHisSiteId = k.getString("siteId");
                                }
                                //????????????
                                if (k.has("cardId")) {
                                    String cardId = k.getString("cardId");
                                    if (PublicUrl.isIDCard(cardId)) {
                                        mIdNumEdit.setText(cardId);
                                    }
                                }
                                //????????????
                                if (k.has("name")) {
                                    String name = k.getString("name");
                                    mNameEdit.setText(name);
                                }
                                //??????
                                if (k.has("gender")) {
                                    String gender = k.getString("gender");
                                    if (gender.indexOf("???") != -1) {
                                        mSex = "???";
                                        mSexManCheckBox.setChecked(true);
                                    } else if (gender.indexOf("???") != -1) {
                                        mSex = "???";
                                        mSexWoCheckBox.setChecked(true);
                                    } else {

                                    }
                                }
                                //??????
                                if (k.has("folk")) {
                                    String folk = k.getString("folk");
                                    if (mEthnicNameList.contains(mEthnic)) {
                                        //????????????????????????????????????????????????
                                        //???????????????????????????
                                        mEthnicNameList.add(0, folk);
                                        //????????????
                                        mEthnicSpinner.setSelection(0);
                                        //??????????????????
                                        mEthnicSpinner.setClickable(false);
                                        mEthnic = folk;
                                    }
                                }
                                //??????
                                if (k.has("marriage")) {
                                    String marriage = k.getString("marriage");
                                    if (mMarriageNameList.contains(marriage)) {
                                        //????????????????????????????????????????????????
                                        //?????????????????????????????????
                                        mMarriageNameList.add(0, marriage);
                                        //????????????
                                        mEthnicSpinner.setSelection(0);
                                        //??????????????????
                                        mMarriageSpinner.setClickable(false);
                                        mMarriage = marriage;
                                    }
                                }
                                //????????????
                                mMedicalRecordsEdit.setText("???");
                                //????????????
                                mDiagnosisEditText.setText("???");
                                //????????????
                                mTreatmentEditText.setText("???");
                                //????????????
                                mPurposeEditText.setText("???");
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
                    T.showMessage(AddConsultationActivity.this, getString(R.string.api_error) + resultMsg);
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
                    T.showMessage(AddConsultationActivity.this, getString(R.string.api_error) + resultMsg);
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
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/clinic/doAdd");
        //params.setSslSocketFactory(); // ??????ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.setMultipart(true);
        params.setAsJsonContent(true);
        //????????????id
        params.addBodyParameter("userId", mUserId);
        //??????
        params.addBodyParameter("siteid", mHospitalId);
        //??????
        params.addBodyParameter("departid", mDepartId);
        params.addBodyParameter("departname", mDepartName);
        //??????
        params.addBodyParameter("userid", mDocId);
        params.addBodyParameter("realname", mDocName);
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
            list.add(new KeyValue("files", new File(mImagePathList.get(i).toString())));
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
                    if (TextUtils.isEmpty(mHisId)) {
                        T.showMessage(AddConsultationActivity.this, "????????????");
                        Intent intent = new Intent(AddConsultationActivity.this, RecordsActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        //??????hisid????????????????????????????????????????????????????????????
                        getListTest();
                    }
                } else {
                    T.showMessage(AddConsultationActivity.this, getString(R.string.api_error) + resultMsg);
                    delToken();
                    doLogout();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "sendAllData onError:" + ex.getMessage());
                T.showMessage(getApplicationContext(), getString(R.string.server_error));
                mLoadingLayout.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                //Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {
                if (TextUtils.isEmpty(mHisId)) {
                    mLoadingLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getListTest() {
        RequestParams params = new RequestParams(BaseConst.MIDDLE_URL + "/api/lis_test?hisId=" + mHisId);
        Log.i(TAG, "getListTest url==" + BaseConst.MIDDLE_URL + "/api/lis_test?hisId=" + mHisId);
        //params.setSslSocketFactory(); // ??????ssl
        params.addHeader("authorization", getTokenFromLocal());
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "getListTest== " + result);
                com.alibaba.fastjson.JSONObject j = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.parse(result);
                String resultCode = j.getString("code");
                if (resultCode.equals("001")) {
                    com.alibaba.fastjson.JSONArray dataArray = j.getJSONArray("data");
                    result = dataArray.toString();
                    Log.i(TAG, "getListTest== " + result);
                    if (result.equals("[]")) {
                        //???????????????????????????????????????????????????
                        getMedicalRecord();
                    } else {
                        result = result.trim();
                        //???hisId?????????siteId + hisId
                        String seitId = ",\"siteId\":\"" + mHisSiteId + "\",\"hisId\"";
                        result = result.replace(",\"hisId\"", seitId);
                        Log.i(TAG, "getListTest== " + result);
                        sendListTest(result);
                    }
                } else {
                    Log.i(TAG, "getListTest onError: != 001");
                    T.showMessage(getApplicationContext(), getString(R.string.server_error));
                    mLoadingLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "getListTest onError:" + ex.getMessage());
                T.showMessage(getApplicationContext(), getString(R.string.server_error));
                mLoadingLayout.setVisibility(View.GONE);
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

    private void sendListTest(String result) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/listest/add");
        Log.i(TAG, "sendListTest url==" + BaseConst.DEAULT_URL + "/listest/add");
        //params.setSslSocketFactory(); // ??????ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addHeader("Content-Type", "application/json");
        params.setBodyContent(result);
        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "sendListTest== " + result);
                getMedicalRecord();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "sendListTest onError:" + ex.getMessage());
                T.showMessage(getApplicationContext(), getString(R.string.server_error));
                mLoadingLayout.setVisibility(View.GONE);
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

    private void getMedicalRecord() {
        RequestParams params = new RequestParams(BaseConst.MIDDLE_URL + "/api/medical_record?hisId=" + mHisId);
        Log.i(TAG, "getMedicalRecord url==" + BaseConst.MIDDLE_URL + "/api/medical_record?hisId=" + mHisId);
        //params.setSslSocketFactory(); // ??????ssl
        params.addHeader("authorization", getTokenFromLocal());
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "getMedicalRecord== " + result);
                com.alibaba.fastjson.JSONObject j = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.parse(result);
                String resultCode = j.getString("code");
                if (resultCode.equals("001")) {
                    com.alibaba.fastjson.JSONArray dataArray = j.getJSONArray("data");
                    result = dataArray.toString();
                    sendMedicalRecord(result);
                } else {
                    Log.i(TAG, "getMedicalRecord onError: != 001");
                    T.showMessage(getApplicationContext(), getString(R.string.server_error));
                    mLoadingLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "getMedicalRecord onError:" + ex.getMessage());
                T.showMessage(getApplicationContext(), getString(R.string.server_error));
                mLoadingLayout.setVisibility(View.GONE);
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

    private void sendMedicalRecord(String result) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/medicalrecord/add");
        Log.i(TAG, "sendMedicalRecord url==" + BaseConst.DEAULT_URL + "/medicalrecord/add");
        //params.setSslSocketFactory(); // ??????ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addHeader("Content-Type", "application/json");
        params.setBodyContent(result);
        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "sendMedicalRecord== " + result);
                getOperation();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "sendMedicalRecord onError:" + ex.getMessage());
                T.showMessage(getApplicationContext(), getString(R.string.server_error));
                mLoadingLayout.setVisibility(View.GONE);
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

    private void getOperation() {
        RequestParams params = new RequestParams(BaseConst.MIDDLE_URL + "/api/operation?hisId=" + mHisId);
        Log.i(TAG, "getOperation url==" + BaseConst.MIDDLE_URL + "/api/operation?hisId=" + mHisId);
        //params.setSslSocketFactory(); // ??????ssl
        params.addHeader("authorization", getTokenFromLocal());
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "getOperation== " + result);
                com.alibaba.fastjson.JSONObject j = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.parse(result);
                String resultCode = j.getString("code");
                if (resultCode.equals("001")) {
                    com.alibaba.fastjson.JSONArray dataArray = j.getJSONArray("data");
                    result = dataArray.toString();
                    Log.i(TAG, "getOperation== " + result);
                    if (result.equals("[]")) {
                        //???????????????????????????????????????????????????
                        getPatient();
                    } else {
                        sendOperation(result);
                    }
                } else {
                    Log.i(TAG, "getOperation onError: != 001");
                    T.showMessage(getApplicationContext(), getString(R.string.server_error));
                    mLoadingLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "getOperation onError:" + ex.getMessage());
                T.showMessage(getApplicationContext(), getString(R.string.server_error));
                mLoadingLayout.setVisibility(View.GONE);
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

    private void sendOperation(String result) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/emr/operation/add");
        Log.i(TAG, "sendOperation url==" + BaseConst.DEAULT_URL + "/emr/operation/add");
        //params.setSslSocketFactory(); // ??????ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addHeader("Content-Type", "application/json");
        params.setBodyContent(result);
        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "sendOperation== " + result);
                getPatient();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "sendOperation onError:" + ex.getMessage());
                T.showMessage(getApplicationContext(), getString(R.string.server_error));
                mLoadingLayout.setVisibility(View.GONE);
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

    private void getPatient() {
        RequestParams params = new RequestParams(BaseConst.MIDDLE_URL + "/api/patient?filterField=his_id&filterValue=" + mHisId);
        Log.i(TAG, "getPatient url==" + BaseConst.MIDDLE_URL + "/api/patient?filterField=his_id&filterValue=" + mHisId);
        //params.setSslSocketFactory(); // ??????ssl
        params.addHeader("authorization", getTokenFromLocal());
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "getPatient== " + result);
                com.alibaba.fastjson.JSONObject j = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.parse(result);
                String resultCode = j.getString("code");
                if (resultCode.equals("001")) {
                    com.alibaba.fastjson.JSONArray dataArray = j.getJSONArray("data");
                    result = dataArray.getJSONObject(0).toString();
                    Log.i(TAG, "getPatient== " + result);
                    if (result.equals("{}")) {
                        //???????????????????????????????????????????????????
                        getStandingOrder();
                    } else {
                        sendPatient(result);
                    }
                } else {
                    Log.i(TAG, "getPatient onError: != 001");
                    T.showMessage(getApplicationContext(), getString(R.string.server_error));
                    mLoadingLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "getPatient onError:" + ex.getMessage());
                T.showMessage(getApplicationContext(), getString(R.string.server_error));
                mLoadingLayout.setVisibility(View.GONE);
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

    private void sendPatient(String result) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/emr/patient/add");
        Log.i(TAG, "sendPatient url==" + BaseConst.DEAULT_URL + "/emr/patient/add");
        //params.setSslSocketFactory(); // ??????ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addHeader("Content-Type", "application/json");
        params.setBodyContent(result);
        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "sendPatient== " + result);
                getStandingOrder();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "sendPatient onError:" + ex.getMessage());
                T.showMessage(getApplicationContext(), getString(R.string.server_error));
                mLoadingLayout.setVisibility(View.GONE);
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

    private void getStandingOrder() {
        RequestParams params = new RequestParams(BaseConst.MIDDLE_URL + "/api/standing_order?hisId=" + mHisId);
        Log.i(TAG, "getStandingOrder url==" + BaseConst.MIDDLE_URL + "/api/standing_order?hisId=" + mHisId);
        //params.setSslSocketFactory(); // ??????ssl
        params.addHeader("authorization", getTokenFromLocal());
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "getStandingOrder== " + result);
                com.alibaba.fastjson.JSONObject j = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.parse(result);
                String resultCode = j.getString("code");
                if (resultCode.equals("001")) {
                    com.alibaba.fastjson.JSONArray dataArray = j.getJSONArray("data");
                    result = dataArray.toString();
                    Log.i(TAG, "getStandingOrder== " + result);
                    if (result.equals("[]")) {
                        //???????????????????????????????????????????????????
                        getTemporaryOrder();
                    } else {
                        sendStandingOrder(result);
                    }
                } else {
                    Log.i(TAG, "getStandingOrder onError: != 001");
                    T.showMessage(getApplicationContext(), getString(R.string.server_error));
                    mLoadingLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "getStandingOrder onError:" + ex.getMessage());
                T.showMessage(getApplicationContext(), getString(R.string.server_error));
                mLoadingLayout.setVisibility(View.GONE);
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


    private void sendStandingOrder(String result) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/emr/standingorder/add");
        Log.i(TAG, "sendStandingOrder url==" + BaseConst.DEAULT_URL + "/emr/standingorder/add");
        //params.setSslSocketFactory(); // ??????ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addHeader("Content-Type", "application/json");
        params.setBodyContent(result);
        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "sendStandingOrder== " + result);
                getTemporaryOrder();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "sendStandingOrder onError:" + ex.getMessage());
                T.showMessage(getApplicationContext(), getString(R.string.server_error));
                mLoadingLayout.setVisibility(View.GONE);
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

    private void getTemporaryOrder() {
        RequestParams params = new RequestParams(BaseConst.MIDDLE_URL + "/api/temporary_order?hisId=" + mHisId);
        Log.i(TAG, "getTemporaryOrder url==" + BaseConst.MIDDLE_URL + "/api/temporary_order?hisId=" + mHisId);
        //params.setSslSocketFactory(); // ??????ssl
        params.addHeader("authorization", getTokenFromLocal());
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "getTemporaryOrder== " + result);
                com.alibaba.fastjson.JSONObject j = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.parse(result);
                String resultCode = j.getString("code");
                if (resultCode.equals("001")) {
                    com.alibaba.fastjson.JSONArray dataArray = j.getJSONArray("data");
                    result = dataArray.toString();
                    Log.i(TAG, "getTemporaryOrder== " + result);
                    if (result.equals("[]")) {
                        //???????????????????????????????????????????????????
                        getRisExam();
                    } else {
                        sendTemporaryOrder(result);
                    }
                } else {
                    Log.i(TAG, "getTemporaryOrder onError: != 001");
                    T.showMessage(getApplicationContext(), getString(R.string.server_error));
                    mLoadingLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "getTemporaryOrder onError:" + ex.getMessage());
                T.showMessage(getApplicationContext(), getString(R.string.server_error));
                mLoadingLayout.setVisibility(View.GONE);
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

    private void sendTemporaryOrder(String result) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/emr/temporaryorder/add");
        Log.i(TAG, "sendTemporaryOrder url==" + BaseConst.DEAULT_URL + "/emr/temporaryorder/add");
        //params.setSslSocketFactory(); // ??????ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addHeader("Content-Type", "application/json");
        params.setBodyContent(result);
        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "sendTemporaryOrder== " + result);
                getRisExam();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "sendTemporaryOrder onError:" + ex.getMessage());
                T.showMessage(getApplicationContext(), getString(R.string.server_error));
                mLoadingLayout.setVisibility(View.GONE);
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

    private void getRisExam() {
        String uuid = UUID.randomUUID().toString();
        RequestParams params = new RequestParams(BaseConst.MIDDLE_URL + "/api/ris_exam?hisId=" + mHisId + "&clinicid=" + uuid);
        Log.i(TAG, "getRisExam url==" + BaseConst.MIDDLE_URL + "/api/ris_exam?hisId=" + mHisId + "&clinicid=" + uuid);
        //params.setSslSocketFactory(); // ??????ssl
        params.addHeader("authorization", getTokenFromLocal());
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "getRisExam== " + result);
                com.alibaba.fastjson.JSONObject j = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.parse(result);
                String resultCode = j.getString("code");
                if (resultCode.equals("001")) {
                    com.alibaba.fastjson.JSONArray dataArray = j.getJSONArray("data");
                    result = dataArray.toString();
                    Log.i(TAG, "getRisExam== " + result);
                    if (result.equals("[]")) {
                        //???????????????????????????????????????
                        mLoadingLayout.setVisibility(View.GONE);
                        T.showMessage(AddConsultationActivity.this, "????????????");
                        Intent intent = new Intent(AddConsultationActivity.this, RecordsActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        sendRisExam(result);
                    }
                } else {
                    Log.i(TAG, "getRisExam onError: != 001");
                    T.showMessage(getApplicationContext(), getString(R.string.server_error));
                    mLoadingLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "getTemporaryOrder onError:" + ex.getMessage());
                T.showMessage(getApplicationContext(), getString(R.string.server_error));
                mLoadingLayout.setVisibility(View.GONE);
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

    private void sendRisExam(String result) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/risexam/add");
        Log.i(TAG, "sendRisExam url==" + BaseConst.DEAULT_URL + "/risexam/add");
        //params.setSslSocketFactory(); // ??????ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addHeader("Content-Type", "application/json");
        params.setBodyContent(result);
        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "sendRisExam== " + result);
                mLoadingLayout.setVisibility(View.GONE);
                T.showMessage(AddConsultationActivity.this, "????????????");
                Intent intent = new Intent(AddConsultationActivity.this, RecordsActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "sendRisExam onError:" + ex.getMessage());
                T.showMessage(getApplicationContext(), getString(R.string.server_error));
                mLoadingLayout.setVisibility(View.GONE);
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

    private void showFinishDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddConsultationActivity.this);
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
