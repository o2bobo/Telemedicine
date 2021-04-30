package com.chinabsc.telemedicine.expert.expertActivity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.myAdapter.PhotoPagerAdapter;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

@ContentView(R.layout.activity_photo_pager)
public class PhotoPagerActivity extends Activity {

    public static final String IMAGE_NUM = "IMAGE_NUM";
    public static final String IMAGE_LIST = "IMAGE_LIST";

    @ViewInject(R.id.viewpager)
    private ViewPager viewPager;

    PhotoPagerAdapter myPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        ArrayList<String> imageUrlList = new ArrayList<String>();
        int startNum = 0;
        Bundle bundle = this.getIntent().getExtras();

        if (bundle != null) {
            imageUrlList = bundle.getStringArrayList(IMAGE_LIST);
            startNum = bundle.getInt(IMAGE_NUM);
        } else {
            Log.i("bundle", "bundle == null");
            finish();
        }

        myPagerAdapter = new PhotoPagerAdapter(this, imageUrlList);
        viewPager.setAdapter(myPagerAdapter);
        viewPager.setCurrentItem(startNum);
    }

}
