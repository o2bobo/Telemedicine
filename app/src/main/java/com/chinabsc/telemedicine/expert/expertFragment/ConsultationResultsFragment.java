package com.chinabsc.telemedicine.expert.expertFragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.expertActivity.TelemedicineInfoActivity;
import com.chinabsc.telemedicine.expert.myView.ImageDialog;
import com.chinabsc.telemedicine.expert.myView.StretchyTextView;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.utils.SPUtils;
import com.chinabsc.telemedicine.expert.utils.T;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.source.adnroid.comm.ui.chatmvp.ChatFragment;
import com.source.adnroid.comm.ui.chatutils.PhotoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/*
 * F4
 * */
public class ConsultationResultsFragment extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ArrayList mImagePathList = new ArrayList();

    public static final int RESULT_LOAD_IMAGE_1 = 1;
    public static final int RESULT_LOAD_IMAGE_2 = 2;
    public static final int RESULT_LOAD_IMAGE_3 = 3;

    public TextView mDiagnosisTitle;
    public TextView mOpinionsTitle;

    public RelativeLayout mDiagnosisRelativeLayout;
    public RelativeLayout mOpinionsRelativeLayout;

    public EditText mDiagnosisEditText;
    public EditText mOpinionsEditText;

    public ImageView mDiagnosisMicImageView;
    public ImageView mOpinionsMicImageView;

    public LinearLayout mDiagnosisLayout;
    public LinearLayout mOpinionsLayout;

    public StretchyTextView mDiagnosisTextView;
    public StretchyTextView mOpinionsTextView;

    public ImageView mUpload1ImageView;
    public ImageView mUpload2ImageView;
    public ImageView mUpload3ImageView;

    public ImageView mDel1ImageView;
    public ImageView mDel2ImageView;
    public ImageView mDel3ImageView;

    public Button mPutButton;

    public RelativeLayout mLoadingLayout;
    public ProgressBar mLoadingProgress;

    public DisplayImageOptions mOptions;
    public ImageLoader mImageLoader = ImageLoader.getInstance();

    public String mTelemedicineInfoId = "";

    private static String TAG = "test";
    // 语音听写对象
    private SpeechRecognizer mIat;
    // 语音听写UI
    private RecognizerDialog mIatDialog;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    private Toast mToast;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private int mIatItem = 1;

    //拍照相关
    private int mPhotoNum = 0;
    private String mPhotoName = "";
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 0x03;

    public ConsultationResultsFragment() {
        // Required empty public constructor
    }

    public static ConsultationResultsFragment newInstance(String param1, String param2) {
        ConsultationResultsFragment fragment = new ConsultationResultsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_consultation_results, container, false);

        init(view);
        initData();
        setViewListener();
        getDataForApi();

        return view;
    }

    private void init(View view) {
        mImageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.basic_image_download) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.drawable.basic_image_error2)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.basic_image_error2)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                .build();//构建完成

        mDiagnosisTitle = (TextView) view.findViewById(R.id.DiagnosisTitle);
        mOpinionsTitle = (TextView) view.findViewById(R.id.OpinionsTitle);

        mDiagnosisRelativeLayout = (RelativeLayout) view.findViewById(R.id.DiagnosisRelativeLayout);
        mOpinionsRelativeLayout = (RelativeLayout) view.findViewById(R.id.OpinionsRelativeLayout);

        mDiagnosisEditText = (EditText) view.findViewById(R.id.DiagnosisEditText);
        mOpinionsEditText = (EditText) view.findViewById(R.id.OpinionsEditText);

        mDiagnosisLayout = (LinearLayout) view.findViewById(R.id.DiagnosisLayout);
        mOpinionsLayout = (LinearLayout) view.findViewById(R.id.OpinionsLayout);

        mDiagnosisTextView = (StretchyTextView) view.findViewById(R.id.DiagnosisTextView);
        mDiagnosisTextView.setMaxLineCount(3);
        mOpinionsTextView = (StretchyTextView) view.findViewById(R.id.OpinionsTextView);
        mOpinionsTextView.setMaxLineCount(3);

        mDiagnosisMicImageView = (ImageView) view.findViewById(R.id.DiagnosisMicImageView);
        mOpinionsMicImageView = (ImageView) view.findViewById(R.id.OpinionsMicImageView);

        mUpload1ImageView = (ImageView) view.findViewById(R.id.Upload1ImageView);
        mUpload2ImageView = (ImageView) view.findViewById(R.id.Upload2ImageView);
        mUpload3ImageView = (ImageView) view.findViewById(R.id.Upload3ImageView);

        mDel1ImageView = (ImageView) view.findViewById(R.id.Del1ImageView);
        mDel2ImageView = (ImageView) view.findViewById(R.id.Del2ImageView);
        mDel3ImageView = (ImageView) view.findViewById(R.id.Del3ImageView);

        mPutButton = (Button) view.findViewById(R.id.PutButton);

        mLoadingLayout = (RelativeLayout) view.findViewById(R.id.LoadingLayout);
        mLoadingProgress = (ProgressBar) view.findViewById(R.id.LoadingProgress);

        SpeechUtility.createUtility(getActivity(), SpeechConstant.APPID + "=590017c6");
        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(getActivity(), mInitListener);
        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        mIatDialog = new RecognizerDialog(getActivity(), mInitListener);
        mToast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
    }

    private void initData() {
        Bundle bundle1 = getArguments();
        mTelemedicineInfoId = bundle1.getString(TelemedicineInfoActivity.TELEMEDICINE_INFO_ID);
        Log.e("F4 ID", mTelemedicineInfoId);
    }

    int ret = 0; // 函数调用返回值

    private void setViewListener() {

        mDiagnosisMicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIatItem = 1;
                startIat();
            }
        });

        mOpinionsMicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIatItem = 2;
                startIat();
            }
        });

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

        mPutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String diagnosis = mDiagnosisEditText.getText().toString();
                String opinions = mOpinionsEditText.getText().toString();
                if (mImagePathList.size() > 0) {
                    if (TextUtils.isEmpty(diagnosis)) {
                        mDiagnosisEditText.setText("见附件");
                    }
                    if (TextUtils.isEmpty(opinions)) {
                        mOpinionsEditText.setText("见附件");
                    }
                    postData();
                } else {
                    if (!TextUtils.isEmpty(diagnosis) && !TextUtils.isEmpty(opinions)) {
                        postData();
                    } else {
                        Toast.makeText(getActivity(), "请填写“诊断意见”和“处理意见”", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mLoadingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void startIat() {
        mIatResults.clear();
        // 设置参数
        setParam();
        boolean isShowDialog = true;
        if (isShowDialog) {
            // 显示听写对话框
            mIatDialog.setListener(mRecognizerDialogListener);
            mIatDialog.show();
            showTip("请开始说话…");
        } else {
            // 不显示听写对话框
            ret = mIat.startListening(mRecognizerListener);
            if (ret != ErrorCode.SUCCESS) {
                showTip("听写失败,错误码：" + ret);
            } else {
                showTip("请开始说话…");
            }
        }
    }

    private void getDataForApi() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL+ "/mobile/clinic/" + mTelemedicineInfoId + "/repo");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        String expertid = SPUtils.get(getActivity(), PublicUrl.USER_ID_KEY, "").toString();
        params.addQueryStringParameter("expertId", expertid);
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("F4 onSuccess", result);
                try {
                    JSONObject j = new JSONObject(result);
                    if (j.has("resultCode")) {
                        String resultCode = j.getString("resultCode");
                        if (resultCode.equals("200")) {
                            if (j.has("data")) {
                                String data = j.getString("data");
                                if (!data.equals("null")) {
                                    mPutButton.setVisibility(View.GONE);
                                    JSONObject k = new JSONObject(data);
                                    if (k.has("diagnosis")) {
                                        String diagnosis = k.getString("diagnosis");
                                        mDiagnosisRelativeLayout.setVisibility(View.GONE);
                                        mDiagnosisEditText.setVisibility(View.GONE);
                                        mDiagnosisTitle.setVisibility(View.GONE);
                                        mDiagnosisLayout.setVisibility(View.VISIBLE);
                                        mDiagnosisTextView.setContent(diagnosis);
                                    }
                                    if (k.has("opinions")) {
                                        String opinions = k.getString("opinions");
                                        mOpinionsRelativeLayout.setVisibility(View.GONE);
                                        mOpinionsEditText.setVisibility(View.GONE);
                                        mOpinionsTitle.setVisibility(View.GONE);
                                        mOpinionsLayout.setVisibility(View.VISIBLE);
                                        mOpinionsTextView.setContent(opinions);
                                    }
                                    if (k.has("images")) {
                                        String images = k.getString("images");
                                        JSONArray imagesArray = new JSONArray(images);
                                        mImagePathList.clear();
                                        for (int i = 0; i < imagesArray.length(); i++) {
                                            String image = imagesArray.get(i).toString();
                                            Log.e("image", image);
                                            if (!image.equals("null")) {
                                                mImagePathList.add(BaseConst.DEAULT_URL + imagesArray.get(i));
                                            }
                                        }
                                        setImageForPosted();
                                    }
                                } else {

                                }
                            }
                        } else if (resultCode.equals("401")) {
                            T.showMessage(getActivity(), getString(R.string.login_timeout));
                            delToken();
                            doLogout();
                        } else {
                            T.showMessage(getActivity(), getString(R.string.api_error) + resultCode);
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
                Log.e("F4 onError", "onError:" + ex.getMessage());
                T.showMessage(getActivity(), getString(R.string.server_error));
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

    private void showImageDialog(String url) {
        ImageDialog imageDialog;
        imageDialog = new ImageDialog(getActivity(), R.style.dialog, url);
        imageDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = imageDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
    }

    private void postData() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/clinic/" + mTelemedicineInfoId + "/repo");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addBodyParameter("clinicid", mTelemedicineInfoId);
        String expertid = SPUtils.get(getActivity(), PublicUrl.USER_ID_KEY, "").toString();
        params.addBodyParameter("expertid", expertid);
        String expertname = SPUtils.get(getActivity(), PublicUrl.USER_NAME_KEY, "").toString();
        params.addBodyParameter("expertname", expertname);
        String diagnosis = mDiagnosisEditText.getText().toString();
        params.addBodyParameter("diagnosis", diagnosis);
        String opinions = mOpinionsEditText.getText().toString();
        params.addBodyParameter("opinions", opinions);
        params.addBodyParameter("referraltag", "0");
        for (int i = 0; i < mImagePathList.size(); i++) {
            Log.e("i", mImagePathList.get(i).toString());
            params.addBodyParameter("files", new File(mImagePathList.get(i).toString()));
        }

        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("F4 onSuccess", result);
                getDataForApi();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                ex.printStackTrace();
                Log.e("F4 onError", "onError:" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                //Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {
                mLoadingLayout.setVisibility(View.GONE);
                initializeView();
                mDiagnosisEditText.setText("");
                mOpinionsEditText.setText("");
            }
        });
    }

    private boolean getPathListAvailable(int n) {
        if (mImagePathList.size() > n) {
            return false;
        } else {
            return true;
        }
    }

    private void showTakePhotoTapyDialog(int j) {
        mPhotoNum = j;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("选择图片");
        builder.setItems(new String[]{"相册", "拍照"}, new DialogInterface.OnClickListener() {
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
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

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

    private void openImageLib(int j) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                    Toast.makeText(getActivity(), "您已经拒绝过一次", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSIONS_REQUEST_CODE);
            }
        }
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, j);
    }

    // 拍照 申请相机权限
    private void takePhoto(int j) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                    Toast.makeText(getActivity(), "您已经拒绝过一次", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSIONS_REQUEST_CODE);
            }
        }
        mPhotoName = "BSC_" + getDate() + ".jpg";
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/ChinaBSC/", mPhotoName);
        Uri uri = Uri.fromFile(file);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(getActivity(), "com.chinabsc.telemedicine.expert.fileprovider", file);
        }
        PublicUrl.isFolderExists(Environment.getExternalStorageDirectory().toString() + "/ChinaBSC/");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, j);

    }

    public String getDate() {
        return Long.toString(System.currentTimeMillis());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String picturePath = "";
        Log.i("test", "resultCode:" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                Log.i("test", "picturePath:" + picturePath);
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

    private void setImage() {
        initializeView();
        for (int i = 1; i <= mImagePathList.size(); i++) {
            Log.e("mImagePathList", mImagePathList.size() + "");
            showDel(i);
            if (i < 3) {
                showNext(i + 1);
            }

            String url = mImagePathList.get(i - 1).toString();
            if (url.startsWith("http://")) {
                mImageLoader.displayImage(url, getUploadImageView(i), mOptions);
            } else {
                mImageLoader.displayImage("file:///" + url, getUploadImageView(i), mOptions);
            }

        }
    }

    private void setImageForPosted() {
        mUpload1ImageView.setVisibility(View.INVISIBLE);
        for (int i = 1; i <= mImagePathList.size(); i++) {
            String url = mImagePathList.get(i - 1).toString();
            getUploadImageView(i).setVisibility(View.VISIBLE);
            if (url.startsWith("http://")) {
                mImageLoader.displayImage(url, getUploadImageView(i), mOptions);
            } else {
                mImageLoader.displayImage("file:///" + url, getUploadImageView(i), mOptions);
            }

        }
    }

    private void initializeView() {
        for (int i = 1; i < 4; i++) {
            getUploadImageView(i).setImageBitmap(null);
            getUploadImageView(i).setVisibility(View.INVISIBLE);
            getDelImageView(i).setVisibility(View.GONE);
        }
        mUpload1ImageView.setVisibility(View.VISIBLE);
    }

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

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            showTip(error.getPlainDescription(true));
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            Log.i("test", "onEndOfSpeech");
            showTip("结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, results.getResultString());
            //printResult(results);

            if (isLast) {
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据：" + data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");


        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");


        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "1");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        // mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        // mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
    }

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results, isLast);
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            showTip(error.getPlainDescription(true));
        }

    };

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败，错误码：" + code);
            }
        }
    };

    private String mIatDiagnosisResult = "";
    private String mIatOpinionsResult = "";

    private void printResult(RecognizerResult results, boolean isLast) {
        JsonParser jsonParser = new JsonParser();
        String text = jsonParser.parseIatResult(results.getResultString());
        Log.i(TAG, "json:" + text);
        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            Log.i(TAG, "catch" + e.getMessage());
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        if (isLast) {
            if (mIatItem == 1) {
                mIatDiagnosisResult = mIatDiagnosisResult + resultBuffer.toString();
                mDiagnosisEditText.setText(mIatDiagnosisResult);
                mDiagnosisEditText.setSelection(mDiagnosisEditText.length());
            } else if (mIatItem == 2) {
                mIatOpinionsResult = mIatOpinionsResult + resultBuffer.toString();
                mOpinionsEditText.setText(mIatOpinionsResult);
                mOpinionsEditText.setSelection(mOpinionsEditText.length());
            }
        }
    }

    class JsonParser {

        String parseIatResult(String json) {
            StringBuffer ret = new StringBuffer();
            try {
                JSONTokener tokener = new JSONTokener(json);
                JSONObject joResult = new JSONObject(tokener);

                JSONArray words = joResult.getJSONArray("ws");
                for (int i = 0; i < words.length(); i++) {
                    // 转写结果词，默认使用第一个结果
                    JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                    JSONObject obj = items.getJSONObject(0);
                    ret.append(obj.getString("w"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ret.toString();
        }
    }

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
