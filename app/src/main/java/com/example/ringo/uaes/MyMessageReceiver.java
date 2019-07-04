package com.example.ringo.uaes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.app.Activity;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.sdk.android.push.MessageReceiver;
import com.alibaba.sdk.android.push.notification.CPushMessage;

import java.util.Map;

/**
 * Created by ringo on 2018/6/12.
 */


public class MyMessageReceiver extends MessageReceiver {
    // 消息接收部分的LOG_TAG
    public static final String REC_TAG = "receiver";
    public HandleNotify myhandleNotify;
    public HandleNotify notify;

    @Override
    public void onNotification(Context context, String title, String summary, Map<String, String> extraMap) {
        // TODO 处理推送通知
        Log.e("MyMessageReceiver", "Receive notification, title: " + title + ", summary: " + summary + ", extraMap: " + extraMap);

        myhandleNotify = new MainTabScanFragment();
        notify = new MainTabShareFragment();
        Activity testactivity=new Activity();

        if(extraMap.get("flag").equals("1")){
            //  授权
            myhandleNotify.onVehicleKeyReceived();
            notify.onVehicleKeyReceived();

            Intent intent  = new Intent(context, WarnActivity.class);
            intent.putExtra("AUTH_KEY", 0);
            context.startActivity(intent);

        }else{
            myhandleNotify.onVehicleKeyCanceled();
            notify.onVehicleKeyCanceled();

            Intent intent  = new Intent(context, WarnActivity.class);
            intent.putExtra("AUTH_KEY", 1);
            context.startActivity(intent);
        }










        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // 弹出通知
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel mChannel = new NotificationChannel("1", "notification channel", NotificationManager.IMPORTANCE_HIGH);
            mChannel.setDescription("notification description");
            mChannel.enableLights(true);
            mChannel.enableVibration(true);
            notificationManager.createNotificationChannel(mChannel);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
            mBuilder.setContentTitle("钥匙分享")
                    .setContentText("成功接收钥匙授权")
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ico))
                    .setSmallIcon(R.mipmap.ico)
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(Notification.DEFAULT_ALL);
            mBuilder.setAutoCancel(true);


            notificationManager.notify(1, mBuilder.build());
        }
    }
    @Override
    public void onMessage(Context context, CPushMessage cPushMessage) {
        Log.e("MyMessageReceiver", "onMessage, messageId: " + cPushMessage.getMessageId() + ", title: " + cPushMessage.getTitle() + ", content:" + cPushMessage.getContent());
        //获取NotificationManager实例
        NotificationManager notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //实例化NotificationCompat.Builde并设置相关属性
        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                //设置小图标
                .setSmallIcon(R.mipmap.ico)
                //设置通知标题
                .setContentTitle("最简单的Notification")
                //设置通知内容
                .setContentText("只有小图标、标题、内容");
        //设置通知时间，默认为系统发出通知的时间，通常不用设置
        //.setWhen(System.currentTimeMillis());
        //通过builder.build()方法生成Notification对象,并发送通知,id=1
        notifyManager.notify(1, builder.build());

    }
    @Override
    public void onNotificationOpened(Context context, String title, String summary, String extraMap) {
        Log.e("MyMessageReceiver", "onNotificationOpened, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
    }
    @Override
    protected void onNotificationClickedWithNoAction(Context context, String title, String summary, String extraMap) {
        Log.e("MyMessageReceiver", "onNotificationClickedWithNoAction, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
    }
    @Override
    protected void onNotificationReceivedInApp(Context context, String title, String summary, Map<String, String> extraMap, int openType, String openActivity, String openUrl) {
        Log.e("MyMessageReceiver", "onNotificationReceivedInApp, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap + ", openType:" + openType + ", openActivity:" + openActivity + ", openUrl:" + openUrl);
    }
    @Override
    protected void onNotificationRemoved(Context context, String messageId) {
        Log.e("MyMessageReceiver", "onNotificationRemoved");
    }




}