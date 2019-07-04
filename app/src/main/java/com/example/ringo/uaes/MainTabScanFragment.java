package com.example.ringo.uaes;

/**
 * Created by ringo on 2017/6/25.
 */

import android.app.Fragment;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.util.Log;
import java.util.ArrayList;

import java.util.List;
import java.util.UUID;

import static android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_LATENCY;


//This is the Ble tab, used to scan and find BLE devices during developing period
public class MainTabScanFragment extends Fragment  implements HandleNotify{
    private static final String TAG = "MainTabScanFragment";
    private static int OFFSET=0,MODELOFFSET=-7,POCKETOFFSET=-5;
    //Bluetooth
    private BluetoothManager mbluetoothManager;
    private BluetoothAdapter mbluetoothAdapter;
    private BluetoothLeScanner mbluetoothlescanner;
    private BluetoothDevice mdevice;
    private BluetoothGatt mbluetoothgatt;
    private BluetoothGattService mbluetoothgattservice;
    private BluetoothGattCharacteristic mbluetoothgattcharacteristic;
    private BluetoothGattCharacteristic mBluetoothGattCharacNotify;
    //Bluetooth UUID

    private final  String TARGET_DEVICE = "SMART PEPS DEMO";
    private final String service_uuid = "0000fff0-0000-1000-8000-00805f9b34fb";
    private final String charac_uuid =  "0000fff4-0000-1000-8000-00805f9b34fb";
    private final String WRITE_UUID =  "0000fff3-0000-1000-8000-00805f9b34fb";
    private final static UUID config_uuid =  UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    // Thread
    Handler periodShow=new Handler();
    Handler handlerWrite=new Handler();
    Thread writeThread,zoneRecognizeThread, motionRecognizeThread;

    // prediction
    PredictionTF_zone0522 preTF_zone;
    PredictionTF_zone0618_medium preTF_zone_medium;
    PredictionTF_zone0618_medium_pocket preTF_zone_medium_pocket;
    Prediction_dynamicRSSI preDynamic;
    PredictionTF_frontandrear preTF_front;
    PredictionTF_motion preTF_motion;
    LUTprediction_top luTpredictionTop =new LUTprediction_top(); //Lookup table
    ZoneDebounce zoneDebounce=new ZoneDebounce();

    // UI components
    private Switch switch_connect;
    private TextView txt_RSSI;
    ImageView img_scan,img_connect;
    TextView txt_curMotion,txt_curZone;


    // Variables
    public volatile boolean exit = false;
    public static volatile boolean toConnect=true, isScan=false,isConnect=false;
    public static byte[] dataReceived=new byte[15];
    public static Node[] Nodes = new Node[13];
    public KalmanFilter_A_max[] Kalman = new KalmanFilter_A_max[13];
    public KalmanFilter_distance distanceFilter=new KalmanFilter_distance();
    public KalmanFilter Kalman_main = new KalmanFilter();
    public static int curMotion=255,curZone=5,curZoneDebounced,curLeftRight=255,curPocketState,dynamic=0,trend=0;
    public static float[] curMotionOutput;
    public int CMDCounter,CMDValue,
            DECISIONTYPE,MOTIONEABLE,
            ZONEBUFFERNUMBER,
            DEBOUNCEDBUFFER1to3,DEBOUNCEDBUFFER3to1,DEBOUNCEDBUFFER1toi,
            CARCONFIGTYPE;

    public static float distanceValue,light;
    private int MainRSSI;
    public static float X,Y,Z,distance;
    //Sensor values
    public static float[] linearAccValue,gravityValue,gyroValue,acceleroValue;
    // display anchor values
    Runnable runnable=new Runnable() {
        @Override
        public void run() {


            if(isConnect) {
                txt_RSSI.setText("M :  " + Nodes[0].RSSI_filtered + "\n" +
                        "A1:  " + (int) Nodes[1].RSSI_filtered + "\n" +
                        "A2:  " + (int) (Nodes[2].RSSI_filtered)+ "\n" +
                        "A3:  " + (int) (Nodes[3].RSSI_filtered)+ "\n" +
                        "A4:  " + (int) Nodes[4].RSSI_filtered + "\n" +
                        "A5:  " + (int) Nodes[5].RSSI_filtered + "\n" +
                        "A6:  " + (int) Nodes[6].RSSI_filtered + "\n" +
                        "A7:  " + (int) Nodes[7].RSSI_filtered + "\n" +
                        "A8:  " + (int) Nodes[8].RSSI_filtered + "\n" +
                        "A9:  " + (int) Nodes[9].RSSI_filtered + "\n"+
                        "A10:  " + (int) Nodes[10].RSSI_filtered + "\n" +
                        "A11:  " + (int) Nodes[11].RSSI_filtered + "\n"
                );

                // display motion outputs
                if (curMotionOutput!=null) {
                    if (curMotion==2){
                        txt_curMotion.setText("Current motion:  walking"   + " \n" + curMotionOutput[0] + " " + curMotionOutput[1] + " " + curMotionOutput[2]);
                    }else{
                        txt_curMotion.setText("Current motion: not walking" + " \n" + curMotionOutput[0] + " " + curMotionOutput[1] + " " + curMotionOutput[2]);

                    }
                }
                // display zone outputs
                txt_curZone.setText("Current Zone:" + distance+"\n"+curZone);


            }
            if (isScan) {
                img_scan.setImageResource(R.mipmap.greenicon);
            } else {
                img_scan.setImageResource(R.mipmap.redicon2);
            }

            if (isConnect) {
                img_connect.setImageResource(R.mipmap.greenicon);
            } else {
                img_connect.setImageResource(R.mipmap.redicon2);
            }
            // update the UI every 100 ms
            periodShow.postDelayed(this,100);
        }
    };
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_ble, container, false);
        // begin to display values
        periodShow.postDelayed(runnable,300);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState){

        super.onActivityCreated(savedInstanceState);
        // init anchor features
        initNode();
        // init bluetooth service
        InitBluetoothService();
        mbluetoothAdapter.getBluetoothLeAdvertiser().startAdvertising(createAdvSettings(false, 0),createAdvertiseData(), mAdvertiseCallback);

        // init layout components
        preDynamic=new Prediction_dynamicRSSI();
        preTF_front=new PredictionTF_frontandrear(this.getContext().getAssets());
        preTF_zone=new PredictionTF_zone0522(this.getContext().getAssets());
        preTF_zone_medium=new PredictionTF_zone0618_medium(this.getContext().getAssets());
//        preTF_zone_medium_pocket=new PredictionTF_zone0618_medium_pocket(this.getContext().getAssets());
        preTF_motion=new PredictionTF_motion(this.getContext().getAssets());
        txt_RSSI=getActivity().findViewById(R.id.txt_RSSI);
        txt_curMotion = getActivity().findViewById(R.id.txt_curMotion);
        txt_curZone = getActivity().findViewById(R.id.txt_curZone);
        img_scan=getActivity().findViewById(R.id.img_scan);
        img_connect=getActivity().findViewById(R.id.img_connect);
        switch_connect=getActivity().findViewById(R.id.Switch_connect);
        DECISIONTYPE=getResources().getInteger(R.integer.DECISIONTYPE);
        ZONEBUFFERNUMBER=getResources().getInteger(R.integer.ZONEBUFFER);
        DEBOUNCEDBUFFER1to3=getResources().getInteger(R.integer.DEBOUNCEDBUFFER1to3);
        DEBOUNCEDBUFFER3to1=getResources().getInteger(R.integer.DEBOUNCEDBUFFER3to1);
        DEBOUNCEDBUFFER1toi=getResources().getInteger(R.integer.DEBOUNCEDBUFFER1toi);
        CARCONFIGTYPE=getResources().getInteger(R.integer.CARCONFIGTYPE);
        MOTIONEABLE=getResources().getInteger(R.integer.MOTIONENABLE);
        //
        switch_connect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            // @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //if has the authority
                if (SPUtils.getBoolean("BLE_ENABLE_SCAN")) {
                    if (isChecked) {
                        InitBluetoothService();
                        exit = false;
                        mbluetoothlescanner = mbluetoothAdapter.getBluetoothLeScanner();
                        // scan settings and start scan
                        Log.d(TAG, "onCreate: start scanner");
                        List<ScanFilter> filters = new ArrayList<>();
                        ScanSettings settings = new ScanSettings.Builder().setScanMode(SCAN_MODE_LOW_LATENCY).build();
                        mbluetoothlescanner.startScan(filters, settings, scanCallback);
                        isScan = true;
                        toConnect = true;
                        motionRecognizeThread = new Thread(runnableMotionRecognize);
                        zoneRecognizeThread = new Thread(runnableZoneRecognize);
                        motionRecognizeThread.start();
                        zoneRecognizeThread.start();
                    } else {
                        exit = true;
                        toConnect = false;
                        if (mbluetoothgatt != null) {
                            mbluetoothgatt.disconnect();
                            mbluetoothgatt.close();
                            isConnect = false;
                            curMotion = 255;
                            curZone = 255;
                            curLeftRight = 255;
                        }


                        Log.d(TAG, "Disconnect");
                       // mbluetoothAdapter.getBluetoothLeAdvertiser().stopAdvertising(mAdvertiseCallback);
                        Log.d(TAG, "Stop advertising");
                        //if (writeThread.getState()==Thread.State.RUNNABLE)
                        mbluetoothlescanner.stopScan(scanCallback);
                        isScan = false;
                        handlerWrite.removeCallbacks(runnableWriteCharac);
                    }
                } else {
                    // has no authority
                    Toast.makeText(getActivity(), "You are not authorized!", Toast.LENGTH_SHORT).show();
                    switch_connect.setChecked(false);
                }
            }
        });
    }


    private void InitBluetoothService(){
        mbluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mbluetoothAdapter = mbluetoothManager.getAdapter();
        if (!mbluetoothAdapter.isEnabled())
        {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),0);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d(TAG, "onCreate: Request permission");

        }
    }

    private ScanCallback scanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            Log.d(TAG, "onScanResult: "+device.getName() +":"+ device.getAddress());
            // connect to master
            if(device.getName()!= null && device.getName().equals(TARGET_DEVICE))
            {

                Log.d(TAG, "onScanResult: device scan success,ready to connect");
                mdevice = device;
                mbluetoothgatt = mdevice.connectGatt(getContext(), false, new BluetoothGattCallback() {

                    @Override
                    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                        super.onServicesDiscovered(gatt, status);
                        Log.d(TAG, "onServicesDiscovered: 发现服务");
                        mbluetoothgattservice = mbluetoothgatt.getService(UUID.fromString(service_uuid));
                        Log.d(TAG, "onServicesDiscovered: " + mbluetoothgattservice.getUuid().toString());
                        mbluetoothgattcharacteristic = mbluetoothgattservice.getCharacteristic(UUID.fromString(WRITE_UUID));
                        mBluetoothGattCharacNotify=mbluetoothgattservice.getCharacteristic(UUID.fromString(charac_uuid));
                        if(mBluetoothGattCharacNotify != null)
                        {
                            setCharacteristicNotification(mBluetoothGattCharacNotify, true);
                            //                    boolean isEnableNotification =  mbluetoothgatt.setCharacteristicNotification(mbluetoothgattcharacteristic, true);
                            //                   if(isEnableNotification)
                            {
                                Log.d(TAG, "onEnableNotification: 使能成功"+mBluetoothGattCharacNotify.getUuid().toString());
                            }
                        }else
                        {
                            Log.d(TAG, "onServicesDiscovered: 发现服务失败");
                        }
                        if(mbluetoothgattcharacteristic!=null) {
                            //write data to master
                            writeThread = new Thread(runnableWriteCharac);
                            writeThread.start();
                            //handlerWrite.postDelayed(runnableWriteCharac,50);

                        }

                    }

                    @Override
                    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                        super.onCharacteristicChanged(gatt, characteristic);

                        Log.d(TAG, "onCharacteristicChanged: "+ characteristic.getStringValue(0));
                        //record data from master's notify
                        dataReceived=characteristic.getValue();
                        Log.d("datanotify",dataReceived[0]+" ");
                        // interpret data
                        readNotify();
                    }

                    @Override
                    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                        super.onConnectionStateChange(gatt, status, newState);
                        if(newState == BluetoothGatt.STATE_CONNECTED)
                        {
                            isConnect=true;
                            //start advertising
                            mbluetoothAdapter.getBluetoothLeAdvertiser().startAdvertising(createAdvSettings(false, 0),createAdvertiseData(), mAdvertiseCallback);
                            // begin to recognize the zone and motion
//                            mbluetoothgatt = gatt;
                            Log.d(TAG, "onConnectionStateChange: Connect");
                            // stop scan
                            mbluetoothlescanner.stopScan(scanCallback);
                            isScan=false;
                        }
                        gatt.discoverServices();
                        if(newState == BluetoothGatt.STATE_DISCONNECTED)
                        {
                            isConnect=false;
                            mbluetoothgatt.close();
                            // inte node for next connection
                            initNode();

                            // if the user still wants to connect, start scanning again
                            if (switch_connect.isChecked())
                            {
                                List<ScanFilter>filters=new ArrayList<>();
                                ScanSettings settings=new ScanSettings.Builder().setScanMode(SCAN_MODE_LOW_LATENCY).build();
                                mbluetoothlescanner.startScan(filters,settings,scanCallback);
                                isScan=true;
                            }
                            Log.d(TAG, "onConnectionStateChange: Disconnect");
                        }

                    }
                    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                        super.onReadRemoteRssi(gatt, rssi, status);
                        MainRSSI=rssi;
                    }

                });

            }
        }


    };

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mbluetoothAdapter == null || mbluetoothgatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        boolean isEnableNotification =  mbluetoothgatt.setCharacteristicNotification(characteristic, enabled);
        if(isEnableNotification) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(config_uuid);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mbluetoothgatt.writeDescriptor(descriptor);


        }
    }

    public AdvertiseSettings createAdvSettings(boolean connectAble, int timeoutMillis) {
        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();
        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
        builder.setConnectable(connectAble);
        builder.setTimeout(timeoutMillis);
        builder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
        AdvertiseSettings mAdvertiseSettings = builder.build();
        if (mAdvertiseSettings == null) {
            Toast.makeText(getActivity(), "mAdvertiseSettings == null", Toast.LENGTH_LONG).show();
            Log.e(TAG, "mAdvertiseSettings == null");
        }
        return mAdvertiseSettings;
    }

    public AdvertiseData createAdvertiseData() {
        AdvertiseData.Builder mDataBuilder = new AdvertiseData.Builder();
        mDataBuilder.setIncludeDeviceName(true); //广播名称也需要字节长度
        mDataBuilder.setIncludeTxPowerLevel(true);

        mDataBuilder.addServiceData(ParcelUuid.fromString("0000fff0-0000-1000-8000-00805f9b34fb"),new byte[]{1,2});

        AdvertiseData mAdvertiseData = mDataBuilder.build();
        if (mAdvertiseData == null) {
            //	Toast.makeText(Main4Activity.this, "mAdvertiseSettings == null", Toast.LENGTH_LONG).show();
            Log.e(TAG, "mAdvertiseSettings == null");
        }
        return mAdvertiseData;
    }

    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            if (settingsInEffect != null) {
                Log.d(TAG, "onStartSuccess TxPowerLv=" + settingsInEffect.getTxPowerLevel() + " mode=" + settingsInEffect.getMode()
                        + " timeout=" + settingsInEffect.getTimeout());
            } else {
                Log.e(TAG, "onStartSuccess, settingInEffect is null");
            }
            Log.e(TAG, "onStartSuccess settingsInEffect" + settingsInEffect);

        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            Log.e(TAG, "onStartFailure errorCode" + errorCode);

            if (errorCode == ADVERTISE_FAILED_DATA_TOO_LARGE) {
                //	Toast.makeText(Main4Activity.this, "R.string.advertise_failed_data_too_large", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Failed to start advertising as the advertise data to be broadcasted is larger than 31 bytes.");
            } else if (errorCode == ADVERTISE_FAILED_TOO_MANY_ADVERTISERS) {
                //Toast.makeText(Main4Activity.this, "R.string.advertise_failed_too_many_advertises", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Failed to start advertising because no advertising instance is available.");
            } else if (errorCode == ADVERTISE_FAILED_ALREADY_STARTED) {
                //	Toast.makeText(Main4Activity.this, "R.string.advertise_failed_already_started", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Failed to start advertising as the advertising is already started");
            } else if (errorCode == ADVERTISE_FAILED_INTERNAL_ERROR) {
                //Toast.makeText(Main4Activity.this, "R.string.advertise_failed_internal_error", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Operation failed due to an internal error");
            } else if (errorCode == ADVERTISE_FAILED_FEATURE_UNSUPPORTED) {
                //	Toast.makeText(Main4Activity.this, "R.string.advertise_failed_feature_unsupported", Toast.LENGTH_LONG).show();
                Log.e(TAG, "This feature is not supported on this platform");
            }
        }
    };

    private Runnable runnableZoneRecognize=new Runnable() {
        public void run() {
            while(toConnect) {
                initNode();
                // 1. perform the tensorflow model
                // 2. perform Lookup table

                switch(DECISIONTYPE){
                    case 1:
                        float[] outputs=new float[1];
                        switch (CARCONFIGTYPE){
                            case 1:
                                preTF_zone.Storage(Nodes);
                                 outputs = preTF_zone.getPredict();
                                if (distanceFilter==null) distanceFilter=new KalmanFilter_distance();

                                break;
                            case 2:
                                if(curPocketState==1){
                                    preTF_zone_medium_pocket.Storage(Nodes);
                                    outputs=preTF_zone_medium_pocket.getPredict();
                                }else{
                                    preTF_zone_medium.Storage(Nodes);
                                    outputs=preTF_zone_medium.getPredict();
                                }

                                break;


                            default:
                                outputs[0]=0;
                                break;

                        }
                        outputs[0]=(float)distanceFilter.FilteredRSSI(outputs[0],true);

                        distance=outputs[0];
                        if (distance<(float)MainTabLocationFragment.unlockDis/10)
                            curZone=1;
                        else if (distance>(float)MainTabLocationFragment.lockDis/10)
                            curZone=3;
                        else curZone=2;
                        if (Nodes[0].RSSI_filtered<60)curZone=1;
                        int temp= luTpredictionTop.PEPS_s32CaliFunction(Nodes);
                        if (temp==0)
                            curZone=0;
                        break;
                    case 2:
                        switch (CARCONFIGTYPE){
                            case(1):

                        }
                        curZone= luTpredictionTop.PEPS_s32CaliFunction(Nodes);
                        break;
                }

                curZoneDebounced=zoneDebounce.DebouncedZone(curZone);
                preDynamic.Storage(Nodes);

                dynamic=preDynamic.getPredict();
                trend=preDynamic.getTrend();

                Log.d("dynamic",dynamic+" ");

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private boolean AwakeCheck(int curZone,Node[] nodes){

        if (curZone==5)
        {
            if (nodes[0].RSSI_filtered<=70);
            return true;
        }else if (curZone==3 ||curZone==4){
            return false;
        }
        return true;
    }

    private Runnable runnableMotionRecognize=new Runnable() {
        public void run() {
            int counter_motion=0;

            while(toConnect ) {
                if(curZone>=2 || curZone==-1){// perform motion detect if the phone is outside the unlock zone
                    // init sensor values

                    // motion detect
                    preTF_motion.Storage(gyroValue, linearAccValue, acceleroValue);

                    // predict the motion state every 100ms
                    if (counter_motion == 5) {
                        curMotion = preTF_motion.getPredict();
                    }
                    if (counter_motion > 5) {
                        counter_motion = 0;
                    } else {
                        counter_motion++;
                    }
                }else {
                    preTF_motion.clearAll();
                    curMotion=0;
                }
                if (gyroValue == null) gyroValue = new float[3];
                if (gravityValue == null) gravityValue = new float[3];
                if (linearAccValue == null) linearAccValue = new float[3];
                //pocket detect
                if (PocketDetector.inPocket(gravityValue, distanceValue, light))
                {
                    curPocketState = 1;
                    Log.d("pocket","in pocket");
                    OFFSET=MODELOFFSET+POCKETOFFSET;
                }
                else {
                    curPocketState = 0;
                   OFFSET=MODELOFFSET;
                }
                if (MOTIONEABLE==0){
                    curMotion=2;
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Runnable runnableWriteCharac= new Runnable() {
        @Override
        public void run() {
            while(!exit){
                if (mbluetoothgatt.readRemoteRssi()) {
                    if (Nodes[0]==null) Nodes[0]=new Node();
                    Nodes[0].RSSI = Math.abs(MainRSSI)+OFFSET;
                    Nodes[0].RSSI_filtered = Kalman_main.FilteredRSSI(Nodes[0].RSSI);
                }

                // create new byte array
                byte[] atemp = new byte[]{0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0,
                        0,0,0
                };

                Integer[] I=new Integer[18];
                int tempCMD;
                if(CMDCounter>0){
                    tempCMD=(CMDValue);
                    CMDCounter--;}
                else{
                    CMDValue=0;
                    tempCMD=0;
                }

                // Command
                I[0]=tempCMD;
                // current Zone
                I[1] = curZone;//curZone_filtered;
                I[2] =curLeftRight;
                I[3] = 0;

                if (curMotion==2) I[4] = 1;else I[4]=0;
                for(int i=1;i<=12;i++){
                    if (Kalman[i]==null) Kalman[i]=new KalmanFilter_A_max();
                }

                I[5] =(int)Nodes[0].RSSI_filtered;
                I[6]=(int)Nodes[1].RSSI_filtered;
                I[7] =(int)Nodes[2].RSSI_filtered;
                I[8]=(int)Nodes[3].RSSI_filtered;
                I[9]=(int)Nodes[4].RSSI_filtered;
                I[10]=(int)Nodes[5].RSSI_filtered;
                I[11]=(int)Nodes[6].RSSI_filtered;
                I[12]=(int)Nodes[7].RSSI_filtered;
                I[13]=(int)Nodes[8].RSSI_filtered;
                I[14]=(int)Nodes[9].RSSI_filtered;
                I[15]=(int)Nodes[10].RSSI_filtered;
                I[16]=(int)Nodes[11].RSSI_filtered;
                I[17]=(int)MainTabLocationFragment.uwbZone;
                for (int i=0;i<=17;i++){
                    atemp[i]=I[i].byteValue();// convert Integer to byte
                }
                // write the characteristic
                mbluetoothgattcharacteristic.setValue(atemp);
                mbluetoothgatt.writeCharacteristic(mbluetoothgattcharacteristic);
                try{
                    Thread.sleep(50);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    };

    private void readNotify(){
     /*   if (mbluetoothgatt.readRemoteRssi()) {
            if (Nodes[0]==null) Nodes[0]=new Node();
            Nodes[0].RSSI = Math.abs(MainRSSI);
            Nodes[0].RSSI_filtered = Kalman_main.FilteredRSSI(Nodes[0].RSSI);
        }*/
        //Byte 2-11 current assist BLE RSSI
        for (int i=1;i<=11;i++) {
            if (Nodes[i] == null) Nodes[i] = new Node();
            Nodes[i].RSSI = -dataReceived[i]+OFFSET;

        }

        // filter the RSSI
        for(int i=1;i<=11;i++){
            if (Kalman[i]==null) Kalman[i]=new KalmanFilter_A_max();

            double[]temp= Kalman[i].FilteredRSSI((double) Nodes[i].RSSI, Nodes[i].Validaty);
            Nodes[i].RSSI_max=(int)temp[1];
            Nodes[i].RSSI_filtered=temp[0];
        }


        //Byte 12 Validity assist RSSI 1-8
       /* int validity_1=dataReceived[11];
        for (int i=7;i>=0;i--){
            if (validity_1>=Math.pow(2,i)){
                Nodes[i+1].Validaty=true;
                validity_1=validity_1-(int)Math.pow(2,i);

            }else{
                Nodes[i+1].Validaty=true;///////////////////////////****************************************************************

            }
        }
        //Byte 13 Validity Assist RSSI 9-10
        int validity_2=dataReceived[12];
        for (int i=1;i>=0;i--){
            if (validity_2>=Math.pow(2,i)){
                Nodes[i+9].Validaty=true;
                validity_2=validity_2-(int)Math.pow(2,i);
            }else{Nodes[i+9].Validaty=true;}/////////////*********************************************************************************888
        }
*/
        if(Math.abs(Nodes[2].RSSI_filtered+Nodes[3].RSSI_filtered-Nodes[5].RSSI_filtered-Nodes[6].RSSI_filtered)>5) {
            if (Nodes[2].RSSI_filtered + Nodes[3].RSSI_filtered > Nodes[5].RSSI_filtered + Nodes[6].RSSI_filtered) {
                curLeftRight = 0;
            } else {
                curLeftRight = 1;
            }
        }

    }

    public void setCMDCounter(int value) {

        CMDCounter = 6; //Set the send times
        CMDValue = value; //Specify the control value
    }

    @Override
    public void onVehicleKeyReceived() {
        // 已经授权，需要打开扫描开关
        SPUtils.save("BLE_ENABLE_SCAN", true);

        //Add By ZYL ,onNotifyReceived tell user to handle

    }

    @Override
    public void onVehicleKeyCanceled() {
        // 取消授权，关闭扫描开关
        SPUtils.save("BLE_ENABLE_SCAN", false);

        //Add By ZYL ,onNotifyReceived tell user to handle
    }




    @Override
    public void onResume() {
        super.onResume();
        boolean isOwner = SPUtils.getBoolean("IS_OWNER");
        if (isOwner) {
            // 需要打开扫描开关
            //   SPUtils.save("BLE_ENABLE_SCAN", true);
        } else {
            // 需要关闭扫描开关
            //  SPUtils.save("BLE_ENABLE_SCAN", false);
        }
    }

    private void initNode(){

        for(int i=0;i<13;i++){
            if (Nodes[i]==null){
            Nodes[i]=new Node();
            Nodes[i].RSSI=100;
            Nodes[i].RSSI_filtered=100;
            }
        }

    }

}
//End of Fragment Class
