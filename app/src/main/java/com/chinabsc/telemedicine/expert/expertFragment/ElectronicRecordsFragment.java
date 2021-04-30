package com.chinabsc.telemedicine.expert.expertFragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.expertActivity.BizcloudActivity;
import com.chinabsc.telemedicine.expert.expertActivity.EcgActivity;
import com.chinabsc.telemedicine.expert.expertActivity.ElcActivity;
import com.chinabsc.telemedicine.expert.expertActivity.EltActivity;
import com.chinabsc.telemedicine.expert.expertActivity.EmrActivity;
import com.chinabsc.telemedicine.expert.expertActivity.EoActivity;
import com.chinabsc.telemedicine.expert.expertActivity.EsoOrEtoActivity;
import com.chinabsc.telemedicine.expert.expertActivity.TelemedicineInfoActivity;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.utils.T;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/*
 * 个人病历查询界面
 * */
public class ElectronicRecordsFragment extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RelativeLayout mEsoNumberLayout;
    public RelativeLayout mEtoNumberLayout;
    public RelativeLayout mEoNumberLayout;
    public RelativeLayout mBizcloudLayout;
    public RelativeLayout mEmrNumberLayout;
    public RelativeLayout mEcgNumberLayout;
    public RelativeLayout mEltNumberLayout;
    public RelativeLayout mElcNumberLayout;

    public TextView mEsoNumberText;
    public TextView mEtoNumberText;
    public TextView mEoNumberText;
    public TextView mBizcloudText;
    public TextView mEmrNumberText;
    public TextView mEcgNumberText;
    public TextView mEltNumberText;
    public TextView mElcNumberText;

    public String mTelemedicineInfoId = "";

    public ElectronicRecordsFragment() {
        // Required empty public constructor
    }

    public static ElectronicRecordsFragment newInstance(String param1, String param2) {
        ElectronicRecordsFragment fragment = new ElectronicRecordsFragment();
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
        View view = inflater.inflate(R.layout.fragment_electronic_records, container, false);
        initData();
        init(view);
        getDataForApi();

        return view;
    }

    private void init(View view) {
        //长期记录
        mEsoNumberLayout = (RelativeLayout) view.findViewById(R.id.EsoNumber);
        mEsoNumberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), EsoOrEtoActivity.class);
                it.putExtra(EsoOrEtoActivity.TELEMEDICINE_INFO_ID, mTelemedicineInfoId);
                it.putExtra(EsoOrEtoActivity.TYPE_ID, "eso");
                startActivity(it);
                }
        });
        //临时医嘱
        mEtoNumberLayout = (RelativeLayout) view.findViewById(R.id.EtoNumber);
        mEtoNumberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), EsoOrEtoActivity.class);
                it.putExtra(EsoOrEtoActivity.TELEMEDICINE_INFO_ID, mTelemedicineInfoId);
                it.putExtra(EsoOrEtoActivity.TYPE_ID, "eto");
                startActivity(it);
            }
        });
        //手术记录
        mEoNumberLayout = (RelativeLayout) view.findViewById(R.id.EoNumber);
        mEoNumberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), EoActivity.class);
                it.putExtra(EoActivity.TELEMEDICINE_INFO_ID, mTelemedicineInfoId);
                startActivity(it);
            }
        });
        //影像检查
        mBizcloudLayout = (RelativeLayout) view.findViewById(R.id.BizcloudNumber);
        mBizcloudLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), BizcloudActivity.class);
                it.putExtra(BizcloudActivity.TELEMEDICINE_INFO_ID, mTelemedicineInfoId);
                startActivity(it);
            }
        });
        //病历记录
        mEmrNumberLayout = (RelativeLayout) view.findViewById(R.id.EmrNumber);
        mEmrNumberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), EmrActivity.class);
                it.putExtra(BizcloudActivity.TELEMEDICINE_INFO_ID, mTelemedicineInfoId);
                startActivity(it);
            }
        });
        //心电检查
        mEcgNumberLayout = (RelativeLayout) view.findViewById(R.id.EcgNumber);
        mEcgNumberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), EcgActivity.class);
                it.putExtra(BizcloudActivity.TELEMEDICINE_INFO_ID, mTelemedicineInfoId);
                startActivity(it);
            }
        });
        //检验记录
        mEltNumberLayout = (RelativeLayout) view.findViewById(R.id.EltNumber);
        mEltNumberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), EltActivity.class);
                it.putExtra(BizcloudActivity.TELEMEDICINE_INFO_ID, mTelemedicineInfoId);
                startActivity(it);
            }
        });
        //检查记录
        mElcNumberLayout = (RelativeLayout) view.findViewById(R.id.ElcNumber);
        mElcNumberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), ElcActivity.class);
                it.putExtra(BizcloudActivity.TELEMEDICINE_INFO_ID, mTelemedicineInfoId);
                startActivity(it);
            }
        });

        mEsoNumberText = (TextView) view.findViewById(R.id.EsoNumberText);
        mEtoNumberText = (TextView) view.findViewById(R.id.EtoNumberText);
        mEoNumberText = (TextView) view.findViewById(R.id.EoNumberText);
        mBizcloudText = (TextView) view.findViewById(R.id.BizcloudNumberText);
        mEmrNumberText = (TextView) view.findViewById(R.id.EmrNumberText);
        mEcgNumberText = (TextView) view.findViewById(R.id.EcgNumberText);
        mEltNumberText = (TextView) view.findViewById(R.id.EltNumberText);
        mElcNumberText = (TextView) view.findViewById(R.id.ElcNumberText);
    }

    private void initData() {
        Bundle bundle1 = getArguments();
        mTelemedicineInfoId = bundle1.getString(TelemedicineInfoActivity.TELEMEDICINE_INFO_ID);
        Log.e("F2 ID", mTelemedicineInfoId);
    }

    private void getDataForApi() {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/clinic/emr/" + mTelemedicineInfoId);
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("F2 onSuccess", result);
                parseAttaJson(result);
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

    private void parseAttaJson(String result) {
        try {
            JSONObject j = new JSONObject(result);
            if (j.has("resultCode")) {
                String resultCode = j.getString("resultCode");
                if (resultCode.equals("200")) {
                    if (j.has("data")) {
                        String data = j.getString("data");
                        JSONObject k = new JSONObject(data);
                        if (k.has("esoNumber")) {
                            String esoNumber = k.getString("esoNumber");
                            if (esoNumber.equals("0")) {
                                mEsoNumberText.setVisibility(View.INVISIBLE);
                            } else {
                                mEsoNumberText.setVisibility(View.VISIBLE);
                            }
                            mEsoNumberText.setText(esoNumber);
                        }
                        if (k.has("etoNumber")) {
                            String etoNumber = k.getString("etoNumber");
                            if (etoNumber.equals("0")) {
                                mEtoNumberText.setVisibility(View.INVISIBLE);
                            } else {
                                mEtoNumberText.setVisibility(View.VISIBLE);
                            }
                            mEtoNumberText.setText(etoNumber);
                        }
                        if (k.has("eoNumber")) {
                            String eoNumber = k.getString("eoNumber");
                            if (eoNumber.equals("0")) {
                                mEoNumberText.setVisibility(View.INVISIBLE);
                            } else {
                                mEoNumberText.setVisibility(View.VISIBLE);
                            }
                            mEoNumberText.setText(eoNumber);
                        }
                        if (k.has("bizcloudNumber")) {
                            String bizcloudNumber = k.getString("bizcloudNumber");
                            if (bizcloudNumber.equals("0")) {
                                mBizcloudText.setVisibility(View.INVISIBLE);
                            } else {
                                mBizcloudText.setVisibility(View.VISIBLE);
                            }
                            mBizcloudText.setText(bizcloudNumber);
                        }
                        if (k.has("emrNumber")) {
                            String emrNumber = k.getString("emrNumber");
                            if (emrNumber.equals("0")) {
                                mEmrNumberText.setVisibility(View.INVISIBLE);
                            } else {
                                mEmrNumberText.setVisibility(View.VISIBLE);
                            }
                            mEmrNumberText.setText(emrNumber);
                        }
                        if (k.has("ecgNumber")) {
                            String ecgNumber = k.getString("ecgNumber");
                            if (ecgNumber.equals("0")) {
                                mEcgNumberText.setVisibility(View.INVISIBLE);
                            } else {
                                mEcgNumberText.setVisibility(View.VISIBLE);
                            }
                            mEcgNumberText.setText(ecgNumber);
                        }
                        if (k.has("eltNumber")) {
                            String eltNumber = k.getString("eltNumber");
                            if(eltNumber.equals("0")) {
                                mEltNumberText.setVisibility(View.INVISIBLE);
                            } else {
                                mEltNumberText.setVisibility(View.VISIBLE);
                            }
                            mEltNumberText.setText(eltNumber);
                        }
                        if (k.has("elcNumber")) {
                            String elcNumber = k.getString("elcNumber");
                            if(elcNumber.equals("0")) {
                                mElcNumberText.setVisibility(View.INVISIBLE);
                            } else {
                                mElcNumberText.setVisibility(View.VISIBLE);
                            }
                            mElcNumberText.setText(elcNumber);
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
