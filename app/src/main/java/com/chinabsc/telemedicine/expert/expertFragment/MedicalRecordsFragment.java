package com.chinabsc.telemedicine.expert.expertFragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinabsc.telemedicine.expert.expertActivity.EducationLiveActivity;
import com.chinabsc.telemedicine.expert.expertActivity.PhotoPagerActivity;
import com.chinabsc.telemedicine.expert.expertActivity.TelemedicineInfoActivity;
import com.chinabsc.telemedicine.expert.expertActivity.WebActivity;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.utils.T;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

/*
 * F2
 * */
public class MedicalRecordsFragment extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ArrayList<DcmInfo> mDcmList = new ArrayList<DcmInfo>();
    public ArrayList<DateInfo> mEgcList = new ArrayList<DateInfo>();
    public ArrayList<DateInfo> mImageAttachmentList = new ArrayList<DateInfo>();
    public ArrayList<DateInfo> mVideotList = new ArrayList<DateInfo>();

    public LinearLayout mMedicalImageLayout;
    public LinearLayout mEgcLayout;
    public LinearLayout mImageAttachmentLayout;
    public LinearLayout mVideoAttachmentLayout;

    public TextView mMedicalImageNo;
    public TextView mEgcNo;
    public TextView mImageAttachmentNo;
    public TextView mVideoAttachmentNo;

    public String mTelemedicineInfoId = "";

    public MedicalRecordsFragment() {
        // Required empty public constructor
    }

    public static MedicalRecordsFragment newInstance(String param1, String param2) {
        MedicalRecordsFragment fragment = new MedicalRecordsFragment();
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
        View view = inflater.inflate(R.layout.fragment_medical_records, container, false);
        init(view);
        initData();
        getDataForApi();

        return view;
    }

    private void init(View view) {
        mMedicalImageLayout = (LinearLayout) view.findViewById(R.id.MedicalImageLayout);
        mEgcLayout = (LinearLayout) view.findViewById(R.id.EgcLayout);
        mImageAttachmentLayout = (LinearLayout) view.findViewById(R.id.ImageAttachmentLayout);
        mVideoAttachmentLayout = (LinearLayout) view.findViewById(R.id.VideoAttachmentLayout);

        mMedicalImageNo = (TextView) view.findViewById(R.id.MedicalImageNo);
        mEgcNo = (TextView) view.findViewById(R.id.EgcNo);
        mImageAttachmentNo = (TextView) view.findViewById(R.id.ImageAttachmentNo);
        mVideoAttachmentNo = (TextView) view.findViewById(R.id.VideoAttachmentNo);
    }

    private void initData() {
        Bundle bundle1 = getArguments();
        mTelemedicineInfoId = bundle1.getString(TelemedicineInfoActivity.TELEMEDICINE_INFO_ID);
        Log.e("F3 ID", mTelemedicineInfoId);
    }

    private void getDataForApi() {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/clinic/" + mTelemedicineInfoId + "/atta");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("F2 onSuccess", result);
                parseAttaJson(result);
                if (mDcmList.size() == 0) {
                    mMedicalImageNo.setVisibility(View.VISIBLE);
                }
                if (mEgcList.size() == 0) {
                    mEgcNo.setVisibility(View.VISIBLE);
                }
                if (mImageAttachmentList.size() == 0) {
                    mImageAttachmentNo.setVisibility(View.VISIBLE);
                }
                if (mVideotList.size() == 0) {
                    mVideoAttachmentNo.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                ex.printStackTrace();
                Log.e("F2 onError", "onError:" + ex.getMessage());
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

    public class DateInfo {
        public String originalFileName;
        public String relativePath;
    }

    public class DcmInfo {
        public String createDateTime;
        public String modsInStudy;
        public String numSeries;
        public String numInstances;
        public String studyPk;
        public String viewerUrl;
    }

    private void parseAttaJson(String result) {
        try {
            JSONObject j = new JSONObject(result);
            if (j.has("resultCode")) {
                String resultCode = j.getString("resultCode");
                if (resultCode.equals("200")) {
                    if (j.has("data")) {
                        String data = j.getString("data");
                        JSONObject k = new JSONObject(data);
                        if (k.has("dcmLink")) {
                            String dcmLink = k.getString("dcmLink");
                            JSONArray dcmArray = new JSONArray(dcmLink);
                            Log.e("dcmLink length", dcmArray.length() + "");
                            mDcmList.clear();
                            for (int i = 0; i < dcmArray.length(); i++) {
                                JSONObject e = dcmArray.getJSONObject(i);
                                DcmInfo item = new DcmInfo();
                                if (e.has("createDateTime")) {
                                    Log.e("dcm createDateTime", e.getString("createDateTime"));
                                    item.createDateTime = e.getString("createDateTime");
                                }
                                if (e.has("modsInStudy")) {
                                    Log.e("dcm modsInStudy", e.getString("modsInStudy"));
                                    item.modsInStudy = e.getString("modsInStudy");
                                }
                                if (e.has("numSeries")) {
                                    Log.e("dcm numSeries", e.getString("numSeries"));
                                    item.numSeries = e.getString("numSeries");
                                }
                                if (e.has("numInstances")) {
                                    Log.e("dcm numInstances", e.getString("numInstances"));
                                    item.numInstances = e.getString("numInstances");
                                }
                                if (e.has("studyPk")) {
                                    Log.e("dcm studyPk", e.getString("studyPk"));
                                    item.studyPk = e.getString("studyPk");
                                }
                                if (e.has("viewerUrl")) {
                                    Log.e("dcm viewerUrl", e.getString("viewerUrl"));
                                    item.viewerUrl = e.getString("viewerUrl");
                                }
                                mDcmList.add(item);
                            }
                            setMedicalImageData();
                        }
                        if (k.has("ecgs")) {
                            String ecgs = k.getString("ecgs");
                            Log.e("ecgs", ecgs);
                            JSONArray ecgsArray = new JSONArray(ecgs);
                            mEgcList.clear();
                            for (int i = 0; i < ecgsArray.length(); i++) {
                                JSONObject e = ecgsArray.getJSONObject(i);
                                DateInfo item = new DateInfo();
                                if (e.has("originalFileName")) {
                                    Log.e("ecgs originalFileName", e.getString("originalFileName"));
                                    item.originalFileName = e.getString("originalFileName");
                                }
                                if (e.has("relativePath")) {
                                    Log.e("ecgs relativePath", e.getString("relativePath"));
                                    item.relativePath = e.getString("relativePath");
                                }
                                mEgcList.add(item);
                            }
                            setEgcItemData();
                        }
                        if (k.has("images")) {
                            String images = k.getString("images");
                            Log.e("images", images);
                            JSONArray ecgsArray = new JSONArray(images);
                            mImageAttachmentList.clear();
                            for (int i = 0; i < ecgsArray.length(); i++) {
                                JSONObject e = ecgsArray.getJSONObject(i);
                                DateInfo item = new DateInfo();
                                if (e.has("originalFileName")) {
                                    item.originalFileName = e.getString("originalFileName");
                                }
                                if (e.has("relativePath")) {
                                    String s = e.getString("relativePath").replaceAll("\\\\", "/");
                                    item.relativePath = s;
                                }
                                mImageAttachmentList.add(item);
                            }
                            setImageAttachmentItemData();
                        }
                        if (k.has("videos")) {
                            String videos = k.getString("videos");
                            Log.e("videos", videos);
                            JSONArray videosArray = new JSONArray(videos);
                            mVideotList.clear();
                            for (int i = 0; i < videosArray.length(); i++) {
                                JSONObject e = videosArray.getJSONObject(i);
                                DateInfo item = new DateInfo();
                                if (e.has("originalFileName")) {
                                    item.originalFileName = e.getString("originalFileName");
                                }
                                if (e.has("relativePath")) {
                                    item.relativePath = e.getString("relativePath");
                                }
                                mVideotList.add(item);
                            }
                            setVideoAttachmentItemData();
                        }
                        if (k.has("others")) {
                            String others = k.getString("others");
                            Log.e("others", others);
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

    private void setMedicalImageData() {
        View ViewArray[] = new View[mDcmList.size()];
        for (int i = 0; i < mDcmList.size(); i++) {
            MedicalImageItemView item = new MedicalImageItemView();
            ViewArray[i] = item.mMedicalImageItemView;
            item.mMedicalImageItemDate.setText(mDcmList.get(i).createDateTime);
            item.mModsInStudyTextView.setText(mDcmList.get(i).modsInStudy);
            item.mNumSeriesTextView.setText("序列：" + mDcmList.get(i).numSeries);
            item.mNumInstancesTextView.setText("影像：" + mDcmList.get(i).numInstances);
            mMedicalImageLayout.addView(ViewArray[i]);
        }

        int mMedicalImageItemNum = 0;
        for (int i = 0; i < mDcmList.size(); i++) {
            ViewArray[i].setTag(mMedicalImageItemNum);
            mMedicalImageItemNum++;
            ViewArray[i].setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int n = (Integer) v.getTag();
                    //Toast.makeText(getActivity(), mDcmList.get(n).viewerUrl, Toast.LENGTH_SHORT).show();
                    Log.e("test", mDcmList.get(n).viewerUrl);
                    Intent it = new Intent(getActivity(), WebActivity.class);
                    String url = mDcmList.get(n).viewerUrl;
                    if (url.startsWith("/")) {
                        url = BaseConst.CHAT_DEAULT_BASE + url;
                    }
                    it.putExtra(WebActivity.URL_ID, url);
                    it.putExtra(WebActivity.TITLE_VISIBILITY, 1);
                    startActivity(it);
                }

            });

        }
    }

    private class MedicalImageItemView {
        public View mMedicalImageItemView;
        public TextView mMedicalImageItemDate;
        public TextView mModsInStudyTextView;
        public TextView mNumSeriesTextView;
        public TextView mNumInstancesTextView;

        public MedicalImageItemView() {
            mMedicalImageItemView = LayoutInflater.from(getActivity()).inflate(
                    R.layout.add_medical_image_view, null);
            viewfinder();
        }

        public void viewfinder() {
            mMedicalImageItemDate = (TextView) mMedicalImageItemView.findViewById(R.id.DateTextView);
            mModsInStudyTextView = (TextView) mMedicalImageItemView.findViewById(R.id.ModsInStudyTextView);
            mNumSeriesTextView = (TextView) mMedicalImageItemView.findViewById(R.id.NumSeriesTextView);
            mNumInstancesTextView = (TextView) mMedicalImageItemView.findViewById(R.id.NumInstancesTextView);
        }
    }

    private ArrayList<String> mEgcUrlList = new ArrayList<String>();

    private void setEgcItemData() {
        View ViewArray[] = new View[mEgcList.size()];
        for (int i = 0; i < mEgcList.size(); i++) {
            EgcItemView item = new EgcItemView();
            ViewArray[i] = item.mEgcItemView;
            item.mEgcItemTitle.setText(mEgcList.get(i).originalFileName);
            mEgcLayout.addView(ViewArray[i]);
            mEgcUrlList.add(BaseConst.DEAULT_URL + mEgcList.get(i).relativePath);
        }

        int mEgcItemNum = 0;
        for (int i = 0; i < mEgcList.size(); i++) {
            ViewArray[i].setTag(mEgcItemNum);
            mEgcItemNum++;
            ViewArray[i].setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int n = (Integer) v.getTag();
                    Intent intent = new Intent(getActivity(), PhotoPagerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt(PhotoPagerActivity.IMAGE_NUM, n);
                    bundle.putStringArrayList(PhotoPagerActivity.IMAGE_LIST, mEgcUrlList);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            });

        }
    }

    private class EgcItemView {
        public View mEgcItemView;
        public TextView mEgcItemTitle;

        public EgcItemView() {
            mEgcItemView = LayoutInflater.from(getActivity()).inflate(
                    R.layout.add_text_view, null);
            viewfinder();
        }

        public void viewfinder() {
            mEgcItemTitle = (TextView) mEgcItemView.findViewById(R.id.MedicalImageItemTitle);
        }
    }

    private ArrayList<String> mImageUrlList = new ArrayList<String>();

    private void setImageAttachmentItemData() {
        mImageUrlList.clear();
        View ViewArray[] = new View[mImageAttachmentList.size()];
        for (int i = 0; i < mImageAttachmentList.size(); i++) {
            ImageAttachmentItemView item = new ImageAttachmentItemView();
            ViewArray[i] = item.mImageAttachmentItemView;
            item.mImageAttachmentItemTitle.setText(mImageAttachmentList.get(i).originalFileName);
            mImageAttachmentLayout.addView(ViewArray[i]);
            mImageUrlList.add(BaseConst.DEAULT_URL + mImageAttachmentList.get(i).relativePath);
        }

        int mImageAttachmentItemNum = 0;
        for (int i = 0; i < mImageUrlList.size(); i++) {
            Log.e("F2 Image", mImageUrlList.get(i));
            ViewArray[i].setTag(mImageAttachmentItemNum);
            mImageAttachmentItemNum++;
            ViewArray[i].setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int n = (Integer) v.getTag();
                    Intent intent = new Intent(getActivity(), PhotoPagerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt(PhotoPagerActivity.IMAGE_NUM, n);
                    bundle.putStringArrayList(PhotoPagerActivity.IMAGE_LIST, mImageUrlList);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            });

        }
    }

    private class ImageAttachmentItemView {
        public View mImageAttachmentItemView;
        public TextView mImageAttachmentItemTitle;

        public ImageAttachmentItemView() {
            mImageAttachmentItemView = LayoutInflater.from(getActivity()).inflate(
                    R.layout.add_text_view, null);
            viewfinder();
        }

        public void viewfinder() {
            mImageAttachmentItemTitle = (TextView) mImageAttachmentItemView.findViewById(R.id.MedicalImageItemTitle);
        }
    }

    private void setVideoAttachmentItemData() {
        View ViewArray[] = new View[mVideotList.size()];
        for (int i = 0; i < mVideotList.size(); i++) {
            VideoAttachmentItemView item = new VideoAttachmentItemView();
            ViewArray[i] = item.mVideoAttachmentItemView;
            item.mVideoAttachmentItemTitle.setText(mVideotList.get(i).originalFileName);
            mVideoAttachmentLayout.addView(ViewArray[i]);
        }

        int mVideoAttachmentItemNum = 0;
        for (int i = 0; i < mVideotList.size(); i++) {
            ViewArray[i].setTag(mVideoAttachmentItemNum);
            mVideoAttachmentItemNum++;
            ViewArray[i].setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int n = (Integer) v.getTag();
                    Intent intent = new Intent(getActivity(), EducationLiveActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(EducationLiveActivity.LIVE_ID, "");
                    bundle.putString(EducationLiveActivity.VIDEO_URL, BaseConst.DEAULT_URL + mVideotList.get(n).relativePath);
                    bundle.putString(EducationLiveActivity.LIVE_TITLE, mVideotList.get(n).originalFileName);
                    bundle.putString(EducationLiveActivity.LIVE_HOSPITAL, "");
                    bundle.putString(EducationLiveActivity.LIVE_DOCTOR, "");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            });

        }
    }

    private class VideoAttachmentItemView {
        public View mVideoAttachmentItemView;
        public TextView mVideoAttachmentItemTitle;

        public VideoAttachmentItemView() {
            mVideoAttachmentItemView = LayoutInflater.from(getActivity()).inflate(
                    R.layout.add_text_view, null);
            viewfinder();
        }

        public void viewfinder() {
            mVideoAttachmentItemTitle = (TextView) mVideoAttachmentItemView.findViewById(R.id.MedicalImageItemTitle);
        }
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
