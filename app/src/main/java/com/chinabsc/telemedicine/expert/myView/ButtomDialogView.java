package com.chinabsc.telemedicine.expert.myView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;

import com.chinabsc.telemedicine.expert.R;

public class ButtomDialogView extends Dialog {


    private boolean iscancelable;//控制点击dialog外部是否dismiss
    private boolean isBackCancelable;//控制返回键是否dismiss
    private View view;
    private Context context;
    //这里的view其实可以替换直接传layout过来的 因为各种原因没传(lan)
    public ButtomDialogView(Context context, View view, boolean isCancelable,boolean isBackCancelable) {
        super(context);
        this.isBackCancelable=isBackCancelable;
        this.context = context;
        this.view = view;
        this.iscancelable = isCancelable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(view);//这行一定要写在前面
        setCancelable(iscancelable);//点击外部不可dismiss
        setCanceledOnTouchOutside(isBackCancelable);
        Window window = this.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
    }
    public void setOnClickListener(int IdRes,View.OnClickListener listener){
        View v=view.findViewById(IdRes);
        v.setOnClickListener(listener);
    }
  /*  public void setDateChangeListener(int IdRes, DatePicker.OnDateChangedListener listener){
        View v=view.findViewById(IdRes);
        v.setOnD(listener);
    }*/
}
