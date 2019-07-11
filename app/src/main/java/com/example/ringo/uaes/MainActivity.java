package com.example.ringo.uaes;

import android.Manifest;

import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;

import android.view.View;
import android.view.View.OnClickListener;

import android.widget.ImageView;
import android.widget.Toast;

import android.content.pm.PackageManager;

import android.widget.TextView;

import android.util.Log;

//import com.example.ringo.uaes.MainTabScanFragment.startConnectCB;
import com.example.ringo.uaes.MainTabControlFragment.sendCMD;
//import com.example.ringo.uaes.MainTabConnectInfoFragment.setStartSendCounter;

public class MainActivity extends AppCompatActivity implements OnClickListener,sendCMD {

    public String Str_Version="2019.01.22";
    public String Str_TabControl="Remote Control";
    public String Str_TabMonitor="Vehicle Info";
    public String Str_TabShare="Account Management";
    public String Str_TabBle="Device Scan";
    private ImageView[] mainTabs;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private int index;
    private int currentTabIndex;
    private Fragment[] fragments;
    private MainTabControlFragment fragment1;
    private MainTabShareFragment fragment2;
    private MainTabScanFragment fragment3;
    private MainTabLocationFragment fragment4;
    private MainTabLocationoutputFragment fragment5;
    private static TextView versionNumber;
    private static final String MODEL_FILE = "file:///android_asset/HARModel.pb"; //模型存放路径


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {

            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_PRIVILEGED,
            Manifest.permission.RECEIVE_BOOT_COMPLETED
    };
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    public void sendControlCMD(int cmd) {
            fragment3.setCMDCounter(cmd);
    }//1 Lock 2 UnLock 3 Panic



    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_main);
        initView();
    }


    @Override
    protected void onStart() {
        super.onStart();
        verifyStoragePermissions(this);
        requestPermission(this,0);
        requestPermission(this,1);
        requestPermission(this,2);
        requestPermission(this,3);

    }



    public static void verifyStoragePermissions(Activity activity) {

        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.e("Permisson Got",permissions.toString());
    }

    private void initView() {
        currentTabIndex = 0;
        //getActionBar().setTitle(Str_TabControl);
        mainTabs = new ImageView[5];


        mainTabs[0] = (ImageView) findViewById(R.id.btn_tabcontrol);
        mainTabs[1] = (ImageView) findViewById(R.id.btn_tabshare);
        mainTabs[2] = (ImageView) findViewById(R.id.btn_tabble);
        mainTabs[3] = (ImageView) findViewById(R.id.btn_location);
        mainTabs[4]=(ImageView)findViewById(R.id.btn_locationoutput );

        //userButtons[0].setOnClickListener(this);
       // userButtons[1].setOnClickListener(this);
       // userButtons[2].setOnClickListener(this);

        mainTabs[0].setOnClickListener(this);
        mainTabs[1].setOnClickListener(this);
        mainTabs[2].setOnClickListener(this);
        mainTabs[3].setOnClickListener(this);
        mainTabs[4].setOnClickListener(this);
        mainTabs[0].setImageResource(R.mipmap.control_b);


        fragment1 = new MainTabControlFragment();
        fragment2 = new MainTabShareFragment();
        fragment3 = new MainTabScanFragment();
        fragment4 = new MainTabLocationFragment();
        fragment5=new MainTabLocationoutputFragment();

        fragments = new Fragment[]{fragment1, fragment2, fragment3, fragment4,fragment5};
        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragments[0])
                .add(R.id.fragment_container, fragments[1])
                .add(R.id.fragment_container, fragments[2])
                .add(R.id.fragment_container, fragments[3])
                .add(R.id.fragment_container,fragments[4])
                .hide(fragments[1]).hide(fragments[2]).hide(fragments[3]).hide(fragments[4])
                .show(fragments[0]).commit();
        Log.v("Start","Enabled");

        //versionNumber=(TextView) findViewById(R.id.txt_version);
       // versionNumber.setText("Update date: "+Str_Version);

    }



private void refreshTabIcon(){


    mainTabs[0].setImageResource(R.mipmap.control);
    mainTabs[1].setImageResource(R.mipmap.share);
    mainTabs[2].setImageResource(R.mipmap.beacon);
    mainTabs[3].setImageResource(R.mipmap.setting_gray);
    mainTabs[4].setImageResource(R.mipmap.location_gray);

}


    /**
     * Requests permission.
     *
     * @param activity
     * @param requestCode request code, e.g. if you need request CAMERA permission,parameters is PermissionUtils.CODE_CAMERA
     */
    public static void requestPermission(final Activity activity, final int requestCode) {
        if (activity == null) {
            return;
        }

       // Log.i(TAG, "requestPermission requestCode:" + requestCode);
        if (requestCode < 0 || requestCode >= PERMISSIONS_STORAGE.length) {
          //  Log.w(TAG, "requestPermission illegal requestCode:" + requestCode);
            return;
        }

        final String requestPermission = PERMISSIONS_STORAGE[requestCode];

//如果是6.0以下的手机，ActivityCompat.checkSelfPermission()会始终等于PERMISSION_GRANTED，
// 但是，如果用户关闭了你申请的权限，ActivityCompat.checkSelfPermission(),会导致程序崩溃(java.lang.RuntimeException: Unknown exception code: 1 msg null)，
// 你可以使用try{}catch(){},处理异常，也可以在这个地方，低于23就什么都不做，
// 个人建议try{}catch(){}单独处理，提示用户开启权限。
// if (Build.VERSION.SDK_INT < 23) {
// return;
// }

        int checkSelfPermission;
        try {
            checkSelfPermission = ActivityCompat.checkSelfPermission(activity, requestPermission);
        } catch (RuntimeException e) {
            Toast.makeText(activity, "please open this permission", Toast.LENGTH_SHORT)
                    .show();
            //Log.e(TAG, "RuntimeException:" + e.getMessage());
            return;
        }

        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
           // Log.i(TAG, "ActivityCompat.checkSelfPermission != PackageManager.PERMISSION_GRANTED");


            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermission)) {
               // Log.i(TAG, "requestPermission shouldShowRequestPermissionRationale");
              //  shouldShowRationale(activity, requestCode, requestPermission);

            } else {
              //  Log.d(TAG, "requestCameraPermission else");
                ActivityCompat.requestPermissions(activity, new String[]{requestPermission}, requestCode);
            }

        } else {
          //  Log.d(TAG, "ActivityCompat.checkSelfPermission ==== PackageManager.PERMISSION_GRANTED");
            //Toast.makeText(activity, "opened:" + requestPermissions[requestCode], Toast.LENGTH_SHORT).show();
          //  permissionGrant.onPermissionGranted(requestCode);
        }
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_tabcontrol:
                index = 0;
                refreshTabIcon();
                mainTabs[0].setImageResource(R.mipmap.control_b);
                Log.v("ZYL debug", "Tab Control Clicked");
                //getActionBar().setTitle(Str_TabControl);
                break;

            case R.id.btn_tabshare:
                index = 1;
                refreshTabIcon();
                mainTabs[1].setImageResource(R.mipmap.share_b);
                Log.v("ZYL debug", "Tab Control Clicked");
                // getActionBar().setTitle(Str_TabShare);
                break;
            case R.id.btn_tabble:
                index = 2;
                refreshTabIcon();
                mainTabs[2].setImageResource(R.mipmap.ibeacon_b);
                //  getActionBar().setTitle(Str_TabBle);
                break;
            case R.id.btn_location:
                index = 3;
                refreshTabIcon();
                mainTabs[3].setImageResource(R.mipmap.setting);
                Log.v("ZYL debug", "Tab Control Clicked");
                //getActionBar().setTitle(Str_TabControl);
                break;
            case R.id.btn_locationoutput:
                index = 4;
                refreshTabIcon();
                mainTabs[4].setImageResource(R.mipmap.location);
                Log.v("ZYL debug", "Tab Control Clicked");
                //getActionBar().setTitle(Str_TabControl);
                break;

        }



        if (currentTabIndex != index) {
           // mainTabs[index].setBackgroundColor(0xFFFFFFFF);
            //mainTabs[currentTabIndex].setBackgroundColor(0xff1699cc);
            FragmentTransaction trx = getFragmentManager().beginTransaction();
            //index==3

            trx.hide(fragments[currentTabIndex]);
            trx.show(fragments[index]).commit();
            currentTabIndex = index;


        }
    }



}




