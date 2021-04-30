package com.chinabsc.telemedicine.expert.myView;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class DocMenuViewPager extends ViewPager {
    public DocMenuViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DocMenuViewPager(Context context) {
        super(context);
    }
    //判断menu在x,y的位置
    public void scrollTo(int x,int y){
        if(getAdapter()==null||x>getWidth()*(getAdapter().getCount()-2)){
            return;
        }
        super.scrollTo(x,y);
    }

}
