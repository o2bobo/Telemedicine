package com.chinabsc.telemedicine.expert.myView;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinabsc.telemedicine.expert.R;

public class BaseMenuDialog extends Dialog {
    public Context mContext;
    public LinearLayout containerViewGroup;
    public View mContentView;
    public TextView titleView;
    Window window = null;

    //构造器
    public BaseMenuDialog(Context context) {
        super(context, R.style.doc_menu_dialog_style);//样式
        mContext = context;
        containerViewGroup = (LinearLayout) getLayoutInflater().inflate(R.layout.base_menu_dialog, null);
        titleView = (TextView) containerViewGroup.findViewById(R.id.dictdialog_title_tv);
    }
    public View findViewById(int id) {
        return mContentView.findViewById(id);
    }

    /**
     * 设置窗口显示
     */
    public void windowDeploy() {
        window = getWindow(); // 得到对话框
        window.setWindowAnimations(R.style.doc_menu_dialog_animation); // 设置窗口弹出动画效果
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.x = 0; // x小于0左移，大于0右移
        windowAttributes.y = 0; // y小于0上移，大于0下移
        windowAttributes.height = 2 * mContext.getResources().getDisplayMetrics().heightPixels / 3;
        windowAttributes.width = LinearLayout.LayoutParams.FILL_PARENT;
        windowAttributes.alpha = 0.6f; //设置透明度
        windowAttributes.gravity = Gravity.BOTTOM; // 设置重力，对齐方式
        window.setAttributes(windowAttributes);
    }
    //显示到layout里面
    @Override
    public void show() {
        if (mContentView != null) {
            containerViewGroup.addView(mContentView);
        }
        setContentView(containerViewGroup);
        setCanceledOnTouchOutside(true);
        windowDeploy();
        super.show();
    }
    //选中的title设置为title
    @Override
    public void setTitle(CharSequence title) {
        if (titleView != null)
            titleView.setText(title);
    }
}
