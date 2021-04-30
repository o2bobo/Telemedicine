package com.chinabsc.telemedicine.expert.expertFragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.expertActivity.TelemedicineInfoActivity;
import com.chinabsc.telemedicine.expert.myView.StretchyTextView;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.utils.T;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/*
* F1
* */
public class ConsultationFragment extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public TextView mDateTextView;
    public TextView mSndSiteNameTextView;
    public TextView mPatientNameTextView;

    public StretchyTextView mPurposeTextView;
    public StretchyTextView mCaseSummaryTextView;
    public StretchyTextView mEvidenceTextView;
    public StretchyTextView mTreatmentTextView;

    public String mTelemedicineInfoId = "";

    public ConsultationFragment() {
        // Required empty public constructor
    }

    public static ConsultationFragment newInstance(String param1, String param2) {
        ConsultationFragment fragment = new ConsultationFragment();
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
        View view = inflater.inflate(R.layout.fragment_consultation, container, false);
        init(view);
        initData();
        getDataForApi();
        return view;
    }

    private void init(View view) {
        mDateTextView = (TextView) view.findViewById(R.id.DateTextView);
        mSndSiteNameTextView = (TextView) view.findViewById(R.id.SendSiteNameView);
        mPatientNameTextView = (TextView) view.findViewById(R.id.PatientNameView);

        mPurposeTextView = (StretchyTextView) view.findViewById(R.id.PurposeView);
        mPurposeTextView.setMaxLineCount(3);
        //mPurposeTextView.setContent("近些年来，越来越多的行业开始和互联网结合，诞生了越来越多的互联网创业公司。互联网创业公司需要面对许多的不确定因素。如果你和你的小伙伴们够幸运，你们的公司可能会在几个星期之内让用户数、商品数、订单量增长几十倍上百倍。一次促销可能会带来平时几十倍的访问流量，一次秒杀活动可能会吸引平时数百倍的访问用户。这对公司自然是极大的好事，说明产品得到认可，公司未来前景美妙。");

        mCaseSummaryTextView = (StretchyTextView) view.findViewById(R.id.CaseSummaryTextView);
        mCaseSummaryTextView.setMaxLineCount(3);
        //mCaseSummaryTextView.setContent("近些年来，越来越多的行业开始和互联网结合，诞生了越来越多的互联网创业公司。互联网创业公司需要面对许多的不确定因素。如果你和你的小伙伴们够幸运，你们的公司可能会在几个星期之内让用户数、商品数、订单量增长几十倍上百倍。一次促销可能会带来平时几十倍的访问流量，一次秒杀活动可能会吸引平时数百倍的访问用户。这对公司自然是极大的好事，说明产品得到认可，公司未来前景美妙。");

        mEvidenceTextView = (StretchyTextView) view.findViewById(R.id.EvidenceTextView);
        mEvidenceTextView.setMaxLineCount(3);
        //mEvidenceTextView.setContent("近些年来，越来越多的行业开始和互联网结合，诞生了越来越多的互联网创业公司。互联网创业公司需要面对许多的不确定因素。如果你和你的小伙伴们够幸运，你们的公司可能会在几个星期之内让用户数、商品数、订单量增长几十倍上百倍。一次促销可能会带来平时几十倍的访问流量，一次秒杀活动可能会吸引平时数百倍的访问用户。这对公司自然是极大的好事，说明产品得到认可，公司未来前景美妙。");

        mTreatmentTextView = (StretchyTextView) view.findViewById(R.id.TreatmentTextView);
        mTreatmentTextView.setMaxLineCount(3);
        //mTreatmentTextView.setContent("近些年来，越来越多的行业开始和互联网结合，诞生了越来越多的互联网创业公司。互联网创业公司需要面对许多的不确定因素。如果你和你的小伙伴们够幸运，你们的公司可能会在几个星期之内让用户数、商品数、订单量增长几十倍上百倍。一次促销可能会带来平时几十倍的访问流量，一次秒杀活动可能会吸引平时数百倍的访问用户。这对公司自然是极大的好事，说明产品得到认可，公司未来前景美妙。");

    }

    private void initData() {
        Bundle bundle1 = getArguments();
        mTelemedicineInfoId = bundle1.getString(TelemedicineInfoActivity.TELEMEDICINE_INFO_ID);
        Log.e("F1 ID", mTelemedicineInfoId);

    }

    private void getDataForApi() {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL+ "/mobile/clinic/" + mTelemedicineInfoId);
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("F1 onSuccess", result);
                parseClinicJson(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                ex.printStackTrace();
                Log.e("F1 onError", "onError:" + ex.getMessage());
                T.showMessage(getActivity(),getString(R.string.server_error));
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

    private void parseClinicJson(String result) {
        try {
            JSONObject j = new JSONObject(result);
            if (j.has("resultCode")) {
                String resultCode = j.getString("resultCode");
                if (resultCode.equals("200")) {
                    if (j.has("data")) {
                        String data = j.getString("data");
                        JSONObject k = new JSONObject(data);
                        if (k.has("dateDone")) {
                            String dateDone = k.getString("dateDone");
                            mDateTextView.setText(dateDone);
                        }
                        if (k.has("purpose")) {
                            String purpose = k.getString("purpose");
                            mPurposeTextView.setContent(purpose);
                        }
                        if (k.has("history")) {
                            String history = k.getString("history");
                            mCaseSummaryTextView.setContent(history);
                        }
                        if (k.has("treatment")) {
                            String treatment = k.getString("treatment");
                            mTreatmentTextView.setContent(treatment);
                        }
                        if (k.has("diagnosis")) {
                            String diagnosis = k.getString("diagnosis");
                            mEvidenceTextView.setContent(diagnosis);
                        }
                        if (k.has("sndSiteName")) {
                            String sndSiteName = k.getString("sndSiteName");
                            mSndSiteNameTextView.setText(sndSiteName);
                        }
                        if (k.has("doctorName")) {
                            String doctorName = k.getString("doctorName");
                            doctorName = mSndSiteNameTextView.getText().toString() + "    " + doctorName;
                            mSndSiteNameTextView.setText(doctorName);
                        }
                        if (k.has("patientName")) {
                            String patientName = k.getString("patientName");
                            patientName = mPatientNameTextView.getText().toString() + patientName;
                            mPatientNameTextView.setText(patientName);
                        }
                        if (k.has("patientGender")) {
                            String patientGender = k.getString("patientGender");
                            if (patientGender.equals("0") || patientGender.equals("男")) {
                                patientGender = mPatientNameTextView.getText().toString() + "    " + "男";
                                mPatientNameTextView.setText(patientGender);
                            } else if (patientGender.equals("1") || patientGender.equals("女")) {
                                patientGender = mPatientNameTextView.getText().toString() + "    " + "女";
                                mPatientNameTextView.setText(patientGender);
                            } else {
                                patientGender = mPatientNameTextView.getText().toString() + "    " + "未知";
                                mPatientNameTextView.setText(patientGender);
                            }
                        }
                        if (k.has("age")) {
                            String age = k.getString("age");
                            age = mPatientNameTextView.getText().toString() + "    " + age;
                            mPatientNameTextView.setText(age);
                        }
                        if (k.has("folk")) {
                            String folk = k.getString("folk");
                        }
                        if (k.has("marriage")) {
                            String marriage = k.getString("marriage");
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
