package com.example.ringo.uaes;

/**
 * Created by ringo on 2016/11/23.
 */


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainTabLocationFragment extends Fragment {

    private static final String TAG = "MainTabLocationFragment";
    public static ImageView location;
    private DataService.MyBinder binder;
    private Handler mHandler;
    private Handler mHandlerWriteToFolie=new Handler();
    private static int zone=-1;
    private static int motion;
    public static boolean isRecord;
    private boolean[] switches = new boolean[8];
    private static ImageView img_walk,img_pocket,img_lock,img_dynamic,img_trend;
    private static ImageView img_zone0,img_zone1,img_zone3,img_zone4,img_zone5,img_zone6;
    private static ImageView img_connect;
    private static TextView txt_unlockdis,txt_lockdis,txt_zone,txt_trend;
    private static Button btn_left,btn_right,btn_front,btn_rear,btn_unlockminus,btn_unlockplus,btn_lockminus,btn_lockplus;
    public static int uwbZone=0;
    public static int lockDis=25,unlockDis=15;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_location, container, false);

        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        // img初始化
        {
            //region
            final TextView txt_curZone = getActivity().findViewById(R.id.txt_curZone);

            //       final Button btn_rearPocket=(Button)getActivity().findViewById(R.id.btn_rearPocket);
            //     final Button btn_frontPocket=(Button)getActivity().findViewById(R.id.btn_frontPocket);
            //   final Button btn_inHand=(Button)getActivity().findViewById(R.id.btn_hand);
            final Switch switch_record = getActivity().findViewById(R.id.switch_record);
            final Intent bindIntent = new Intent(getActivity(), DataService.class);
            btn_left = getActivity().findViewById(R.id.btn_uwbleft);
            btn_right = getActivity().findViewById(R.id.btn_uwbright);
            btn_front = getActivity().findViewById(R.id.btn_uwbfront);
            btn_rear = getActivity().findViewById(R.id.btn_uwbrear);
            btn_lockminus = getActivity().findViewById(R.id.btn_lockminus);
            btn_lockplus = getActivity().findViewById(R.id.btn_lockplus);
            btn_unlockminus = getActivity().findViewById(R.id.btn_unlockminus);
            btn_unlockplus = getActivity().findViewById(R.id.btn_unlockplus);
            txt_lockdis = getActivity().findViewById(R.id.txt_lockDis);
            txt_unlockdis = getActivity().findViewById(R.id.txt_unlockDis);
            txt_zone = getActivity().findViewById(R.id.txt_zone);
            txt_trend = getActivity().findViewById(R.id.txt_trend);
            btn_lockminus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lockDis = lockDis - 1;
                    txt_lockdis.setText(" " + (float) lockDis / 10);
                }
            });
            btn_lockplus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lockDis = lockDis + 1;
                    txt_lockdis.setText(" " + (float) lockDis / 10);
                }
            });
            btn_unlockminus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    unlockDis = unlockDis - 1;
                    txt_unlockdis.setText(" " + (float) unlockDis / 10);
                }
            });
            btn_unlockplus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    unlockDis = unlockDis + 1;
                    txt_unlockdis.setText(" " + (float) unlockDis / 10);
                }
            });
            btn_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uwbZone = 0;
                    btn_left.setBackgroundColor(Color.parseColor("#FFE7BA"));
                    btn_right.setBackgroundColor(0);
                    btn_rear.setBackgroundColor(0);
                    btn_front.setBackgroundColor(0);
                }
            });
            btn_front.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uwbZone = 1;
                    btn_front.setBackgroundColor(Color.parseColor("#FFE7BA"));
                    btn_right.setBackgroundColor(0);
                    btn_rear.setBackgroundColor(0);
                    btn_left.setBackgroundColor(0);
                }
            });
            btn_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uwbZone = 2;
                    btn_right.setBackgroundColor(Color.parseColor("#FFE7BA"));
                    btn_left.setBackgroundColor(0);
                    btn_rear.setBackgroundColor(0);
                    btn_front.setBackgroundColor(0);
                }
            });
            btn_rear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uwbZone = 3;
                    btn_rear.setBackgroundColor(Color.parseColor("#FFE7BA"));
                    btn_right.setBackgroundColor(0);
                    btn_left.setBackgroundColor(0);
                    btn_front.setBackgroundColor(0);
                }
            });
            for (int i = 0; i < switches.length; ++i) {
                switches[i] = true;
            }
            Intent startIntent = new Intent(getActivity(), DataService.class);
            getActivity().startService(startIntent);

            getActivity().bindService(bindIntent, connection, getActivity().BIND_AUTO_CREATE);

            switch_record.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        isRecord = true;
                    } else {
                        isRecord = false;
                /*   try {
                        getActivity().unbindService(connection);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                    }
                }
            });


            {
                location = ((ImageView) getActivity().findViewById(R.id.zoneimage));
                img_walk = getActivity().findViewById(R.id.img_walk);
                img_pocket = getActivity().findViewById(R.id.img_pocket);
                img_connect = getActivity().findViewById(R.id.img_connectlocation);
                img_lock = getActivity().findViewById(R.id.img_lock);
                img_dynamic = getActivity().findViewById(R.id.img_dynamic);
                img_trend = getActivity().findViewById(R.id.img_trend);
                img_zone0 = getActivity().findViewById(R.id.img_zone0);

                img_zone0 = getActivity().findViewById(R.id.img_zone0);
                img_zone1 = getActivity().findViewById(R.id.img_zone1);
                img_zone3 = getActivity().findViewById(R.id.img_zone3);
                img_zone4 = getActivity().findViewById(R.id.img_zone4);
                img_zone5 = getActivity().findViewById(R.id.img_zone5);
                img_zone6 = getActivity().findViewById(R.id.img_zone6);
            }
            //endregion
        }
        mHandler=new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                {
                    //功能图标变换
                    //region
                    int walk = MainTabScanFragment.curMotion;
                    int curzone = MainTabScanFragment.curZone;
                    int curLeftRight = MainTabScanFragment.curLeftRight;
                    int pocketState = MainTabScanFragment.curPocketState;
                    int dynamic = MainTabScanFragment.dynamic;
                    // TextView txt_curzone1=getActivity().findViewById(R.id.txt_curzone1);
                    //txt_curzone1.setText("curZone: "+zone);
                    if (pocketState == 1) {
                        img_pocket.setImageResource(R.mipmap.pocket_in);
                    } else {
                        img_pocket.setImageResource(R.mipmap.pocket_out);
                    }
                    if (walk == 2) {
                        img_walk.setImageResource(R.mipmap.walk_on);
                    } else {
                        img_walk.setImageResource(R.mipmap.walk_off);
                    }
                    boolean isConnect = MainTabScanFragment.isConnect;
                    int zone_temp = MainTabScanFragment.curZoneDebounced;
                    if (isConnect) {
                        img_connect.setImageResource(R.mipmap.connect_on);
                    } else {
                        img_connect.setImageResource(R.mipmap.connect_off);
                        img_lock.setImageResource(R.mipmap.grayicon);
                    }
                    if (isConnect) {
                        //int zone =  MainTabConnectInfoFragment.readZone();
                        if (zone_temp == 1) {
                            img_lock.setImageResource(R.mipmap.greenicon);
                            if (curLeftRight == 1)
                                location.setImageResource(R.mipmap.zone1_left);
                            else {
                                location.setImageResource(R.mipmap.zone1_right);
                            }

                            //imv_Locksign.setImageResource(R.mipmap.greenicon);
                        } else if (zone_temp == 2) {
                            //    if (curLeftRight==1)
                            //     location.setImageResource(R.mipmap.zone_unknown);
                            // else  {location.setImageResource(R.mipmap.zone_unknown);}

                        } else if (zone_temp == 3) {
                            if (curLeftRight == 1)
                                location.setImageResource(R.mipmap.zone3_left);
                            else {
                                location.setImageResource(R.mipmap.zone3_right);
                            }
                            //imv_Locksign.setImageResource(R.mipmap.redicon2);
                        } else if (zone_temp == 0) {
                            location.setImageResource(R.mipmap.zone_0);
                        } else if (zone_temp == 255) {
                            location.setImageResource(R.mipmap.zone_unknown);
                        }
                        if (zone_temp == 3 && walk == 2 && MainTabScanFragment.dynamic == 0) {
                            img_lock.setImageResource(R.mipmap.redicon2);
                        }
                        if (dynamic == 1) {
                            img_dynamic.setImageResource(R.mipmap.greenicon);
                        } else {
                            img_dynamic.setImageResource(R.mipmap.redicon2);
                        }
                        if (MainTabScanFragment.trend > 10) {
                            img_trend.setImageResource(R.mipmap.redicon2);
                        } else {
                            img_trend.setImageResource(R.mipmap.greenicon);
                        }
                        txt_trend.setText(MainTabScanFragment.trend + " ");
                    }
                    txt_zone.setText("Current Zone:" + MainTabScanFragment.distance + "\n" + MainTabScanFragment.curZone + "\n" + MainTabScanFragment.curZoneDebounced);
                //endregion
                    //区域图标变换
                    {
                        //region

                        if (curzone<=6){
                           img_zone6.setImageResource(R.mipmap.greenicon);
                           if (curzone<=5){
                               img_zone5.setImageResource(R.mipmap.greenicon);
                               if (curzone<=4){
                                   img_zone4.setImageResource(R.mipmap.greenicon);
                                   switch (curzone){
                                       case (0):
                                           img_zone0.setImageResource(R.mipmap.greenicon);
                                           img_zone1.setImageResource(R.mipmap.redicon2);
                                           img_zone3.setImageResource(R.mipmap.redicon2);
                                           break;
                                       case(1):
                                           img_zone1.setImageResource(R.mipmap.greenicon);
                                           img_zone0.setImageResource(R.mipmap.redicon2);
                                           img_zone3.setImageResource(R.mipmap.redicon2);
                                           break;
                                       case(3):
                                           img_zone3.setImageResource(R.mipmap.greenicon);
                                           img_zone0.setImageResource(R.mipmap.redicon2);
                                           img_zone1.setImageResource(R.mipmap.redicon2);
                                           break;
                                   }
                               }else{
                                   img_zone4.setImageResource(R.mipmap.redicon2);
                                   img_zone0.setImageResource(R.mipmap.grayicon);
                                   img_zone1.setImageResource(R.mipmap.grayicon);
                                   img_zone3.setImageResource(R.mipmap.grayicon);
                               }
                           }else{
                               img_zone5.setImageResource(R.mipmap.redicon2);
                               img_zone4.setImageResource(R.mipmap.grayicon);
                               img_zone0.setImageResource(R.mipmap.grayicon);
                               img_zone1.setImageResource(R.mipmap.grayicon);
                               img_zone3.setImageResource(R.mipmap.grayicon);
                           }
                        }else{
                            img_zone6.setImageResource(R.mipmap.redicon2);
                            img_zone5.setImageResource(R.mipmap.grayicon);
                            img_zone4.setImageResource(R.mipmap.grayicon);
                            img_zone0.setImageResource(R.mipmap.grayicon);
                            img_zone1.setImageResource(R.mipmap.grayicon);
                            img_zone3.setImageResource(R.mipmap.grayicon);
                        }
                        //endregion
                    }
                }
                mHandler.postDelayed(this,20);
            }
        }, 20);

        super.onActivityCreated(savedInstanceState);

    }





    public static int getZone(){
        return zone;
    }


    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("Func", "onServiceConnected()");
            binder = (DataService.MyBinder) service;
            binder.setSensors(switches);
        }
    };
}



