package com.chinabsc.telemedicine.expert.videoActivities;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.Random;

public class RtcService extends Service {
    private final IBinder mBinder = new LocalBinder();
    private final Random mGenerator = new Random();
    public class LocalBinder extends Binder {
        RtcService getService() {
            return RtcService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public int getRandomNumber() {
        return mGenerator.nextInt(100);
    }
}
