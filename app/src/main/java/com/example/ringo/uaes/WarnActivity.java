package com.example.ringo.uaes;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class WarnActivity extends AppCompatActivity {
    private static final String TAG = "WarnActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_warn);

        int authKey = getIntent().getIntExtra("AUTH_KEY", 2);
        if (authKey == 0) {
            new AlertDialog.Builder(this).setTitle("Key Sharing")//设置对话框标题

                    .setMessage("You have received a new key from Zhang Yalin. Please Confirm!!")//设置显示的内容

                    .setPositiveButton("YES",new DialogInterface.OnClickListener() {//添加确定按钮



                        @Override

                        public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                            Log.d(TAG, "onClick: click yes **************");
                            dialog.dismiss();
                            finish();
                        }

                    }).setNegativeButton("NO",new DialogInterface.OnClickListener() {//添加返回按钮



                @Override

                public void onClick(DialogInterface dialog, int which) {//响应事件

                    Log.d(TAG, "onClick: click no **************");
                    dialog.dismiss();
                    finish();

                }

            }).show();//在按键响应事件中显示此对话框
        } else if (authKey == 1) {
            new AlertDialog.Builder(this).setTitle("Key Canceled!")//设置对话框标题
                    .setMessage("Your digital key has been withdrawed by Zhang Yalin!")//设置显示的内容
                    .setPositiveButton("OK",new DialogInterface.OnClickListener() {//添加确定按钮

                        @Override
                        public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                            dialog.dismiss();
                            finish();
                        }
            }).show();//在按键响应事件中显示此对话框
        } else {
            finish();
        }

    }

}
