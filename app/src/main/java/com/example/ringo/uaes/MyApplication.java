package com.example.ringo.uaes;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;

import org.xutils.x;

public class MyApplication extends Application {
    private static final String TAG = "Init";
    @Override
    public void onCreate() {
        super.onCreate();
        SPUtils.initSharedPreferences(this);

        //是车主
        SPUtils.save("IS_OWNER", true);
        SPUtils.save("BLE_ENABLE_SCAN",true);

        if (!SPUtils.getBoolean("IS_OWNER")) {
            initCloudChannel(this);
        }

        x.Ext.init(this);
        x.Ext.setDebug(true);

    }
    /**
     * 初始化云推送通道
     * @param applicationContext
     */
    private void initCloudChannel(Context applicationContext) {
        PushServiceFactory.init(applicationContext);
        CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(applicationContext, callback);
        pushService.bindAccount("123456", callback);
    }

    private CommonCallback callback = new CommonCallback() {
        @Override
        public void onSuccess(String response) {
            Log.d(TAG, "init cloudchannel success");
        }
        @Override
        public void onFailed(String errorCode, String errorMessage) {
            Log.d(TAG, "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
        }
    };
}