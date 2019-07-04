package com.example.ringo.uaes;

/**
 * Created by ringo on 2016/11/23.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.bluetooth.BluetoothManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.ringo.uaes.MainTabControlFragment.sendCMD;


public class MainTabControlFragment extends Fragment implements OnClickListener{

  private ImageView[] userButtons;
  private sendCMD mysendCMD;
  private String str_unlock="Are you sure to lock the car?";
  private String str_lock="Are you sure to unlock the car?";
  private String str_trunk="Are you sure to unlock the trunk?";
  private String str_panic="Panic?";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_control, container, false);


        return view;
    }
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mysendCMD= (MainTabControlFragment.sendCMD) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implementOnArticleSelectedListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        userButtons=new ImageView[4];
        userButtons[0]=(ImageView) getActivity().findViewById(R.id.btn_Lock);
        userButtons[1]=(ImageView)  getActivity().findViewById(R.id.btn_UnLock);
        userButtons[2]=(ImageView)  getActivity().findViewById(R.id.btn_Panic);
        userButtons[3]=(ImageView)getActivity().findViewById(R.id.btn_trunkunlock);
        userButtons[0].setOnClickListener(this);
        userButtons[1].setOnClickListener(this);
        userButtons[2].setOnClickListener(this);
        userButtons[3].setOnClickListener(this);
        super.onActivityCreated(savedInstanceState);

    }


    public void onClick(View view){
        if (SPUtils.getBoolean("BLE_ENABLE_SCAN")){

        switch(view.getId()){

            case R.id.btn_Lock:
                new AlertDialog.Builder(getActivity()).setTitle("")//设置对话框标题

                        .setMessage("Are you sure to lock the car?")//设置显示的内容

                        .setPositiveButton("YES",new DialogInterface.OnClickListener() {//添加确定按钮
                            @Override
                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                // TODO Auto-generated method stub
                                Toast.makeText(getActivity(), "Lock Cmd", Toast.LENGTH_SHORT).show();
                                mysendCMD.sendControlCMD(1);
                            }
                        }).setNegativeButton("NO",new DialogInterface.OnClickListener() {//添加返回按钮
             @Override
                    public void onClick(DialogInterface dialog, int which) {//响应事件
                    }

                }).show();//在按键响应事件中显示此对话框

                break;
            case R.id.btn_UnLock:
                new AlertDialog.Builder(getActivity()).setTitle("")//设置对话框标题

                        .setMessage("Are you sure to unlock the car?")//设置显示的内容

                        .setPositiveButton("YES",new DialogInterface.OnClickListener() {//添加确定按钮
                            @Override
                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                // TODO Auto-generated method stub
                                Toast.makeText(getActivity(), "Unlock Cmd", Toast.LENGTH_SHORT).show();
                                mysendCMD.sendControlCMD(2);
                            }
                        }).setNegativeButton("NO",new DialogInterface.OnClickListener() {//添加返回按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//响应事件
                    }

                }).show();//在按键响应事件中显示此对话框

                break;

            case R.id.btn_Panic:
                new AlertDialog.Builder(getActivity()).setTitle("")//设置对话框标题

                        .setMessage("Panic?")//设置显示的内容

                        .setPositiveButton("YES",new DialogInterface.OnClickListener() {//添加确定按钮
                            @Override
                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                // TODO Auto-generated method stub
                                Toast.makeText(getActivity(), "Panic Cmd", Toast.LENGTH_SHORT).show();
                                mysendCMD.sendControlCMD(3);
                            }
                        }).setNegativeButton("NO",new DialogInterface.OnClickListener() {//添加返回按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//响应事件
                    }

                }).show();//在按键响应事件中显示此对话框

                break;
            case R.id.btn_trunkunlock:
                new AlertDialog.Builder(getActivity()).setTitle("")//设置对话框标题

                        .setMessage("Are you sure to unlock the trunk?")//设置显示的内容

                        .setPositiveButton("YES",new DialogInterface.OnClickListener() {//添加确定按钮
                            @Override
                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                // TODO Auto-generated method stub
                                Toast.makeText(getActivity(), "Trunk unlock Cmd", Toast.LENGTH_SHORT).show();
                                mysendCMD.sendControlCMD(4);
                            }
                        }).setNegativeButton("NO",new DialogInterface.OnClickListener() {//添加返回按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//响应事件
                    }

                }).show();//在按键响应事件中显示此对话框

                break;

        }}
        else{
            new AlertDialog.Builder(getActivity()).setTitle("")//设置对话框标题

                    .setMessage("You are not authorized")//设置显示的内容

                    .show();//在按键响应事件中显示此对话框
        }

    }

    public interface sendCMD{

        public void sendControlCMD(int cmd);//1 Lock 2 UnLock 3 Panic

    }



}
