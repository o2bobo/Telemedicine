package com.chinabsc.telemedicine.expert.expertFragment;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.entity.MiddleAddressEntity;
import com.chinabsc.telemedicine.expert.entity.MiddleBingFangEntity;
import com.chinabsc.telemedicine.expert.entity.MiddleBingQuEntity;
import com.chinabsc.telemedicine.expert.entity.MiddlePatientEntity;
import com.chinabsc.telemedicine.expert.expertActivity.MiddleSelectActivity;
import com.chinabsc.telemedicine.expert.expertActivity.MiddlewareActivity;
import com.chinabsc.telemedicine.expert.expertinterfaces.OnItemInterfaceClick;
import com.chinabsc.telemedicine.expert.myAdapter.BQFViewHolder;
import com.chinabsc.telemedicine.expert.myAdapter.MiddleBingQuBingFangAdapter;
import com.chinabsc.telemedicine.expert.myAdapter.PatientRecycleAdapter;
import com.zw.libslibrary.mload.MLoadingDialog;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeMedicalRecordFragment extends BaseLazyFragment implements OnItemInterfaceClick {
   // View view;
    String address;
    View positonView;
    private TextView selectedAddressTv;

    private RecyclerView mRecycle;
    MyMiddleHandler handler = new MyMiddleHandler(this);
    private String mInfectedpatchId;
    PatientRecycleAdapter patientRecycleAdapter;//?????????????????????
    LinearLayoutManager linearLayoutManager;
    private StringBuffer selectedAddress = new StringBuffer();
    List<MiddleBingQuEntity.DataBean> BQlist = new ArrayList<>();
    List<MiddleBingFangEntity.DataBean> BFlist = new ArrayList<>();
    RecyclerView bingquRecycle;
    RecyclerView bingfangRecycle;
    PopupWindow popupWindow;
    Dialog loadDialog;
    public HomeMedicalRecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView==null){
            rootView = inflater.inflate(R.layout.fragment_home_medical_record, container, false);
        }

        return rootView;
    }
    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        Log.i("HomeMedicalRecordFragment","isVisible="+isVisible);
        if (isVisible){
            positonView=rootView.findViewById(R.id.medical_positong);
            loadDialog=MLoadingDialog.createLoadingDialog(getActivity(),"loading...");
            selectedAddressTv = rootView.findViewById(R.id.selected_address);
            selectedAddressTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (popupWindow != null) {
                        if (popupWindow.isShowing()) {
                            return;
                        }
                    }
                    getBingQu();
                }
            });
            mRecycle = rootView.findViewById(R.id.myRecycle);
            linearLayoutManager = new LinearLayoutManager(getActivity());
            getMiddleAddress();
            initPopwindow();
        }else {

        }
    }
    //?????????????????????
    private void getMiddleAddress() {
        loadDialog.show();
        Log.i("MiddlewareActivity", "BaseConst.DEAULT_URL==" + BaseConst.DEAULT_URL);
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/site/middleware");
        params.addHeader("authorization", getTokenFromLocal());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "????????????????????? result==" + result);
                MiddleAddressEntity middleAddressEntity = JSONObject.parseObject(result, MiddleAddressEntity.class);
                Log.i(TAG, "ip=" + middleAddressEntity.getData().getIp() + " port" + middleAddressEntity.getData().getPort());
                address = "http://" + middleAddressEntity.getData().getIp() + ":" + middleAddressEntity.getData().getPort();
                Log.i(TAG, "address==" + address);
                BaseConst.MIDDLE_URL = "http://" + middleAddressEntity.getData().getIp() + ":" + middleAddressEntity.getData().getPort();
                handler.sendEmptyMessage(1);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                loadDialog.cancel();
            }
        });
    }

    //??????????????????
    private void getBingQu() {
        loadDialog.show();
        Log.i(TAG, "address=====" + address + "/api/infectedpatch");
        RequestParams params = new RequestParams(address + "/api/infectedpatch");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "????????????==" + result);
                MiddleBingQuEntity middleBingQuEntity = JSONObject.parseObject(result, MiddleBingQuEntity.class);
                if (middleBingQuEntity.getCode().equals("001")) {
                    Message msg = new Message();
                    msg.what = 2;
                    msg.obj = middleBingQuEntity;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "??????????????????err= " + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                loadDialog.cancel();
            }
        });
    }

    //??????????????????
    private void getBingFang(String infectedpatchId) {
        loadDialog.show();
        RequestParams params = new RequestParams(address + "/api/ward");
        params.addBodyParameter("infectedpatch_id", infectedpatchId);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "????????????" + result);
                MiddleBingFangEntity middleBingFangEntity = JSONObject.parseObject(result, MiddleBingFangEntity.class);
                Message message = new Message();
                message.what = 3;
                message.obj = middleBingFangEntity;
                handler.sendMessage(message);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "??????????????????err= " + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                loadDialog.cancel();
            }
        });
    }

    //??????????????????
    private void getPatientMsg(String infectedpatchId, String wardId) {
        loadDialog.show();
        //infectedpatch_id=0020010098& ward_id=001002003
        RequestParams params = new RequestParams(address + "/api/current_inpatient");
        params.addBodyParameter("infectedpatch_id", infectedpatchId);
        params.addBodyParameter("ward_id", wardId);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "??????????????????" + result);
                MiddlePatientEntity middlePatientEntity = JSONObject.parseObject(result, MiddlePatientEntity.class);
                Message msg = new Message();
                msg.what = 4;
                msg.obj = middlePatientEntity;
                handler.sendMessage(msg);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "?????????????????? " + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                loadDialog.cancel();
            }
        });
    }

    @Override
    public void onclick(String id, String tag) {
        Log.i(TAG, "hisId==" + id);
        Intent intent = new Intent(getActivity(), MiddlewareActivity.class);
        intent.putExtra("hisId", id);
        intent.putExtra("address", address);
        startActivity(intent);
    }

    static class MyMiddleHandler extends Handler {

        private final WeakReference<HomeMedicalRecordFragment> mActivity;

        public MyMiddleHandler(HomeMedicalRecordFragment mactivity) {
            Log.i("MiddleSelectActivity", "new MyMiddleHandler");
            mActivity = new WeakReference<HomeMedicalRecordFragment>(mactivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i("MiddleSelectActivity", "msg==" + msg.what);
            if (mActivity == null) {
                Log.i("MiddleSelectActivity", "activity==null");
                return;
            }
            if (msg.what == 1) {
                //???????????????????????????

            } else if (msg.what == 2) {
                //??????????????????
                mActivity.get().showPopuwindow();
                mActivity.get().BQlist.clear();
                MiddleBingQuEntity middleBingQuEntity = (MiddleBingQuEntity) msg.obj;
                mActivity.get().sortBingQuList(middleBingQuEntity.getData());//??????
                mActivity.get().setBingQuCheckStatus(middleBingQuEntity.getData());
                mActivity.get().BQlist.addAll(middleBingQuEntity.getData());
                mActivity.get().bingquRecycle.getAdapter().notifyDataSetChanged();
                //  mActivity.get().selectedAddress.append(middleBingQuEntity.getData().get(0).getInfectedpatchName());

            } else if (msg.what == 3) {
                //??????????????????
                mActivity.get().BFlist.clear();
                MiddleBingFangEntity middleBingFangEntity = (MiddleBingFangEntity) msg.obj;
                mActivity.get().sortBingFangList(middleBingFangEntity.getData());//??????
                mActivity.get().BFlist.addAll(middleBingFangEntity.getData());
                mActivity.get().bingfangRecycle.getAdapter().notifyDataSetChanged();
                // mActivity.get().selectedAddress.append(middleBingFangEntity.getData().get(0).getWardName());

            } else if (msg.what == 4) {
                //????????????????????????
                MiddlePatientEntity middlePatientEntity = (MiddlePatientEntity) msg.obj;
                mActivity.get().patientRecycleAdapter = new PatientRecycleAdapter(mActivity.get().getActivity(), middlePatientEntity);
                //???recyclerView?????????????????????
                mActivity.get().patientRecycleAdapter.setOnItemClickListener(mActivity.get());
                mActivity.get().mRecycle.setLayoutManager(mActivity.get().linearLayoutManager);
   /*             DividerItemDecoration divider = new DividerItemDecoration(mActivity.get(), DividerItemDecoration.VERTICAL);
                divider.setDrawable(ContextCompat.getDrawable(mActivity.get(), R.drawable.devider_shape));
                mActivity.get().mRecycle.addItemDecoration(divider);*/
                mActivity.get().mRecycle.setAdapter(mActivity.get().patientRecycleAdapter);

            } else if (msg.what == 5) {
                mActivity.get().selectedAddressTv.setText(mActivity.get().selectedAddress);
                mActivity.get().selectedAddress.delete(0, mActivity.get().selectedAddress.length());
                if (mActivity.get().popupWindow.isShowing()) {
                    mActivity.get().popupWindow.dismiss();
                }
            }
        }
    }


    //????????????
    private void sortBingQuList(List<MiddleBingQuEntity.DataBean> list) {
        Collections.sort(list, new Comparator<MiddleBingQuEntity.DataBean>() {
            @Override
            public int compare(MiddleBingQuEntity.DataBean o1, MiddleBingQuEntity.DataBean o2) {
                Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);
                return com.compare(o1.getInfectedpatchName(), o2.getInfectedpatchName());
            }
        });

    }

    //????????????
    private void sortBingFangList(List<MiddleBingFangEntity.DataBean> list) {
        Collections.sort(list, new Comparator<MiddleBingFangEntity.DataBean>() {
            @Override
            public int compare(MiddleBingFangEntity.DataBean o1, MiddleBingFangEntity.DataBean o2) {
                Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);
                return com.compare(o1.getWardName(), o2.getWardName());
            }
        });
    }

    //popuwindow????????????????????????
    private void initPopwindow() {
        //??????PopupWindow?????????View
        View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.middle_popuwindow_layout, null);
        bingquRecycle = popupView.findViewById(R.id.bingqu_recycle);
        bingfangRecycle = popupView.findViewById(R.id.bingfang_recycle);
        bingquRecycle.setLayoutManager(new LinearLayoutManager(getActivity()));
        bingfangRecycle.setLayoutManager(new LinearLayoutManager(getActivity()));
        bingquRecycle.setAdapter(new MiddleBingQuBingFangAdapter(getActivity(), BQlist) {
            @Override
            public void bingView(final RecyclerView.ViewHolder holder, final int position) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // selectedAddress.append(BQlist.get(position).getInfectedpatchName());
                        mInfectedpatchId = BQlist.get(position).getInfectedpatchId();
                        getBingFang(mInfectedpatchId);
                        updateCheckstatus(position);

                    }
                });
                if (holder instanceof BQFViewHolder) {
                    ((BQFViewHolder) holder).tv.setText(BQlist.get(position).getInfectedpatchName());
                    if (BQlist.get(position).getCheckStatus().equals("0")) {
                        ((BQFViewHolder) holder).markImg.setVisibility(View.INVISIBLE);
                    } else {
                        ((BQFViewHolder) holder).markImg.setVisibility(View.VISIBLE);
                    }

                }
            }
        });
        bingfangRecycle.setAdapter(new MiddleBingQuBingFangAdapter(getActivity(), BFlist) {
            @Override
            public void bingView(RecyclerView.ViewHolder holder, final int position) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedAddress.append(BFlist.get(position).getWardName());
                        getPatientMsg(mInfectedpatchId, BFlist.get(position).getWardId());
                        handler.sendEmptyMessage(5);
                    }
                });
                if (holder instanceof BQFViewHolder) {
                    ((BQFViewHolder) holder).tv.setText(BFlist.get(position).getWardName());
                }
            }
        });
        popupWindow = new PopupWindow(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //??????PopupWindow???????????????
        popupWindow.setContentView(popupView);
        //??????????????????PopupWindow??????????????????????????????setBackgroundDrawable????????????????????????
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setOutsideTouchable(false);
        //??????PopupWindow??????
         popupWindow.setAnimationStyle(R.style.popwin_anim);
        //??????????????????PopupWindow???????????????????????????
        popupWindow.setClippingEnabled(true);
        //??????PopupWindow????????????
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });

    }

    private void showPopuwindow() {
        //PopupWindow???targetView????????????
        synchronized (this) {
            if (popupWindow.isShowing()) {
                return;
            }
            //popupWindow.showAsDropDown(selectedAddressTv);
            showAsDropDown(popupWindow, positonView, 0, 0);
        }
    }

    public void showAsDropDown(final PopupWindow pw, final View anchor, final int xoff, final int yoff) {
        if (Build.VERSION.SDK_INT >= 24) {
            Rect visibleFrame = new Rect();
            anchor.getGlobalVisibleRect(visibleFrame);
            int height = anchor.getResources().getDisplayMetrics().heightPixels - visibleFrame.bottom;
            pw.setHeight(height);
            pw.showAsDropDown(anchor, xoff, yoff);
        } else {
            pw.showAsDropDown(anchor, xoff, yoff);
        }
    }

    private void setBingQuCheckStatus(List<MiddleBingQuEntity.DataBean> list) {
        for (MiddleBingQuEntity.DataBean dataBean : list) {
            dataBean.setCheckStatus("0");
        }
    }

    private void updateCheckstatus(int position) {
        for (MiddleBingQuEntity.DataBean dataBean : BQlist) {
            dataBean.setCheckStatus("0");
        }
        BQlist.get(position).setCheckStatus("1");
        bingquRecycle.getAdapter().notifyDataSetChanged();
    }
}
