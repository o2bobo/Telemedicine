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
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.expertActivity.SubscribeInfoActivity;
import com.chinabsc.telemedicine.expert.myView.StretchyTextView;
import com.chinabsc.telemedicine.expert.utils.T;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class SubscribeFormFragment extends BaseFragment {
    public static String TAG = "SubscribeFormFragment";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //医生信息
    public TextView mSndSiteNameTextView;
    //患者信息
    public TextView mPatientNameTextView;

    //病历摘要
    public StretchyTextView mCaseSummaryTextView;
    //诊断及依据
    public StretchyTextView mEvidenceTextView;
    //治疗情况简介
    public StretchyTextView mTreatmentTextView;

    public String mClinicId = "";

    public SubscribeFormFragment() {
        // Required empty public constructor
    }

    public static SubscribeFormFragment newInstance(String param1, String param2) {
        SubscribeFormFragment fragment = new SubscribeFormFragment();
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
        View view = inflater.inflate(R.layout.fragment_subscribe_form, container, false);
        init(view);
        initData();
        getDataForApi();
        return view;
    }

    private void init(View view) {
        mSndSiteNameTextView = (TextView) view.findViewById(R.id.SendSiteNameView);
        mPatientNameTextView = (TextView) view.findViewById(R.id.PatientNameView);

        mCaseSummaryTextView = (StretchyTextView) view.findViewById(R.id.CaseSummaryTextView);
        mCaseSummaryTextView.setMaxLineCount(3);

        mEvidenceTextView = (StretchyTextView) view.findViewById(R.id.EvidenceTextView);
        mEvidenceTextView.setMaxLineCount(3);

        mTreatmentTextView = (StretchyTextView) view.findViewById(R.id.TreatmentTextView);
        mTreatmentTextView.setMaxLineCount(3);

    }

    private void initData() {
        Bundle bundle1 = getArguments();
        mClinicId = bundle1.getString(SubscribeInfoActivity.CLINIC_ID);
    }

    private void getDataForApi() {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/outPatient/outClinic/detail?clinicId=" + mClinicId);
        Log.i(TAG, "getDataForApi url==" + BaseConst.DEAULT_URL + "/mobile/outPatient/outClinic/detail?clinicId=" + mClinicId);
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "getDataForApi==" + result);
                parseClinicJson(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                ex.printStackTrace();
                Log.i(TAG, "getDataForApi onError:" + ex.getMessage());
                T.showMessage(getActivity(), getString(R.string.server_error));
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
                        if (k.has("sndsitename")) {
                            String sndSiteName = k.getString("sndsitename");
                            mSndSiteNameTextView.setText(sndSiteName);
                        }
                        if (k.has("doctorname")) {
                            String doctorName = k.getString("doctorname");
                            doctorName = mSndSiteNameTextView.getText().toString() + "    " + doctorName;
                            mSndSiteNameTextView.setText(doctorName);
                        }
                        if (k.has("name")) {
                            String patientName = k.getString("name");
                            patientName = mPatientNameTextView.getText().toString() + patientName;
                            mPatientNameTextView.setText(patientName);
                        }
                        if (k.has("gender")) {
                            String patientGender = k.getString("gender");
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
