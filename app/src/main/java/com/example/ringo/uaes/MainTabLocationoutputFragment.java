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

public class MainTabLocationoutputFragment extends Fragment {


    private boolean[] switches = new boolean[8];
    private DataService.MyBinder binder;
    private Handler mHandler;
    private static final String TAG = "MainTabLocationoutputFragment";
    public static int X,Y;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        X = 500; Y =1000;
        View view = inflater.inflate(R.layout.fragment_locationoutput, container, false);

        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {

        final TextView txt_curZone=getActivity().findViewById(R.id.txt_curZone);

        //       final Button btn_rearPocket=(Button)getActivity().findViewById(R.id.btn_rearPocket);
        //     final Button btn_frontPocket=(Button)getActivity().findViewById(R.id.btn_frontPocket);
        //   final Button btn_inHand=(Button)getActivity().findViewById(R.id.btn_hand);
        final Switch switch_record=getActivity().findViewById(R.id.switch_record);
        final Intent bindIntent = new Intent(getActivity(), DataService.class);




        mHandler=new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                {
                    X=MainTabScanFragment.curX;
                    Y=MainTabScanFragment.curY;
                    View circleview=getActivity().findViewById(R.id.circle);
                    circleview.invalidate();
                }
                mHandler.postDelayed(this,20);
            }
        }, 20);
        super.onActivityCreated(savedInstanceState);

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



