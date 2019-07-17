package com.example.ringo.uaes;

/**
 * Created by ringo on 2016/11/23.
 */


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainTabShareFragment extends Fragment implements HandleNotify{

    private static final String TAG = "MainTabShareFragment";

    private MyAdapter mFriendListAdpter;
    private ListView lv;
    private static ImageView ivMember;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_share, container, false);
           return view;
    }

    public void onActivityCreated(Bundle savedInstanceState){


        mFriendListAdpter=new MyAdapter();
        lv = (ListView)getActivity().findViewById(R.id.friend_list_1);
        lv.setAdapter(mFriendListAdpter);
        Contents a=new Contents();
        if (!SPUtils.getBoolean("IS_OWNER"))
        {

        a.sName="Chen Xiaoxiong";
        a.sTeleNumber="151 3847 3924";
       // Contents b=new Contents();
        //b.sName="Qiao Peihu";
       // b.sTeleNumber="151 3348 9989";
        }else{
            a.sName="Qiao Peihu";
            a.sTeleNumber="136 3647 5375";
            //Contents b=new Contents();
           // b.sName="Qiao Peihu";
           // b.sTeleNumber="151 3348 9989";
        }

        mFriendListAdpter.addContent(a);
       // mFriendListAdpter.addContent(b);
        mFriendListAdpter.notifyDataSetChanged();
        lv.setOnItemClickListener(new MyOnItemClickListener());

        ivMember = getActivity().findViewById(R.id.iv_member);

        times = 0;


        super.onActivityCreated(savedInstanceState);
    }

    String flag = "";
    int times = -1;

    @Override
    public void onResume() {
        super.onResume();
        boolean isOwner = SPUtils.getBoolean("IS_OWNER");
        if (isOwner) {
            ivMember.setImageResource(R.mipmap.gold_member);
        } else {
            boolean isAuth = SPUtils.getBoolean("BLE_ENABLE_SCAN");
            if (isAuth) {
                ivMember.setImageResource(R.mipmap.silver_member);
            } else {
                ivMember.setImageResource(R.mipmap.bronze_member);
            }
        }
    }




    @Override
    public void onVehicleKeyReceived() {
        ivMember.setImageResource(R.mipmap.silver_member);

        //Add By ZYL ,onNotifyReceived tell user to handle
//        new AlertDialog.Builder(getActivity()).setTitle("Key Sharing")//设置对话框标题
//
//                .setMessage("Are you sure to share your key with Chen Xiaoxiong!!!!!")//设置显示的内容
//
//                .setPositiveButton("YES",new DialogInterface.OnClickListener() {//添加确定按钮
//
//
//
//                    @Override
//
//                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
//
//                        Log.d(TAG, "onClick: click yes **************");
//
//
//                    }
//
//                }).setNegativeButton("NO",new DialogInterface.OnClickListener() {//添加返回按钮
//
//
//
//            @Override
//
//            public void onClick(DialogInterface dialog, int which) {//响应事件
//
//                Log.d(TAG, "onClick: click no **************");
//
//
//            }
//
//        }).show();//在按键响应事件中显示此对话框



    }

    @Override
    public void onVehicleKeyCanceled() {
        ivMember.setImageResource(R.mipmap.bronze_member);
    }

    private class MyOnItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final ViewHolder holder = (ViewHolder) lv.getChildAt(position).getTag();
            String showString="Are you sure to share your key this user?";
            if(times%2 == 0){
                flag = "1";//授权
                showString="Are you sure to share your key with this user?";
            }else{
                flag = "0";//授权
                showString="Are you sure to withdraw your key from this user?";
            }

          //  Person p = (Person) parent.getItemAtPosition(position);
            new AlertDialog.Builder(getActivity()).setTitle("Key Sharing")//设置对话框标题

                    .setMessage(showString)//设置显示的内容

                    .setPositiveButton("YES",new DialogInterface.OnClickListener() {//添加确定按钮



                        @Override

                        public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                            // TODO Auto-generated method stub

                            dialog.dismiss();


                            if(holder.iShare.isSelected()){
                                holder.iShare.setSelected(false);
                            }else{
                                holder.iShare.setSelected(true);
                            }

                            // 调用后台接口，通知后台发送推送给另一部手机
                           sendPush(flag);

                            times++;

                        }

                    }).setNegativeButton("NO",new DialogInterface.OnClickListener() {//添加返回按钮



                @Override

                public void onClick(DialogInterface dialog, int which) {//响应事件

                    // TODO Auto-generated method stub

                  //  Log.i("alertdialog"," 请保存数据！");

                }

            }).show();//在按键响应事件中显示此对话框

            //Toast.makeText(getApplicationContext(), p.getName(), Toast.LENGTH_SHORT).show();

        }
    }

    private void sendPush(final String shareFlag) {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = new RequestParams("http://47.100.198.180:8668/v1/userRelation/zyltest");//url
                params.addBodyParameter("body", "钥匙授权变动");
                params.addBodyParameter("phoneNum", "123456");
                params.addBodyParameter("flag", shareFlag);
                Log.d(TAG, "sendPush: params = " + params);
                x.http().get(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d(TAG, "onSuccess: result = " + result);
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Log.d(TAG, "onError: ex = " + ex);

                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                        Log.d(TAG, "onCancelled: ");
                    }

                    @Override
                    public void onFinished() {
                        Log.d(TAG, "onFinished: ");
                    }
                });
            }
        });
    }
    /*
    private ArrayAdapter<String> adapter;

           @Override
    public void onCreate(Bundle savedInstanceState) {
              // TODO Auto-generated method stub
             super.onCreate(savedInstanceState);

              //定义一个数组
                List<String> data = new ArrayList<String>();
             for (int i = 0; i < 30; i++) {
                data.add("smyh" + i);
            }
          //将数组加到ArrayAdapter当中
            adapter = new ArrayAdapter<String>(getActivity(),
                              android.R.layout.simple_list_item_1, data);
              //绑定适配器时，必须通过ListFragment.setListAdapter()接口，而不是ListView.setAdapter()或其它方法
           setListAdapter(adapter);
       }


*/


    static class ViewHolder
    {
        public ImageView iPhoto;
        public ImageView iShare;
        public TextView tname;
        public TextView info;
    }
   public class Contents{
     public String sName;
     public String sTeleNumber;
     public String iconPath;

   }

    public class MyAdapter extends BaseAdapter
    {
        private LayoutInflater mInflater = null;
        private Map<String,String> signalstrength;
        private ArrayList<Contents> friendContentList;
        private MyAdapter()
        {
            //根据context上下文加载布局，这里的是Demo17Activity本身，即this
           super();
           mInflater = getActivity().getLayoutInflater();
          friendContentList=new ArrayList<Contents>();
          signalstrength=new HashMap<String,String>();
        }

        public void addContent(Contents ctemp){

            if(!friendContentList.contains(ctemp))
            {
                friendContentList.add(ctemp);

            }

        }
        @Override
        public int getCount() {
            //How many items are in the data set represented by this Adapter.
            //在此适配器中所代表的数据集中的条目数
            return friendContentList.size();
        }
        @Override
        public Object getItem(int position) {
            // Get the data item associated with the specified position in the data set.
            //获取数据集中与指定索引对应的数据项
            return friendContentList.get(position);
        }
        @Override
        public long getItemId(int position) {
            //Get the row id associated with the specified position in the list.
            //获取在列表中与指定索引对应的行id
            return position;
        }

        //Get a View that displays the data at the specified position in the data set.
        //获取一个在数据集中指定索引的视图来显示数据
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            //如果缓存convertView为空，则需要创建View
            if (convertView == null) {
                holder = new ViewHolder();
                //根据自定义的Item布局加载布局
                convertView = mInflater.inflate(R.layout.friend_list, null);
                holder.iPhoto = (ImageView) convertView.findViewById(R.id.photoicon);
                holder.tname = (TextView) convertView.findViewById(R.id.user_name);
                holder.info = (TextView) convertView.findViewById(R.id.tel_number);
                holder.iShare = (ImageView) convertView.findViewById(R.id.shareico);
                //将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (!SPUtils.getBoolean("IS_OWNER"))
            {
                if (position == 0) {
                    holder.iPhoto.setImageResource(R.mipmap.roy);
                } else {
                    holder.iPhoto.setImageResource(R.mipmap.kobe);
                }
             }else{
                if (position == 0) {
                    holder.iPhoto.setImageResource(R.mipmap.kobe2);
                } else {
                    holder.iPhoto.setImageResource(R.mipmap.roy);
                }
            }
            holder.tname.setText(friendContentList.get(position).sName);
            holder.info.setText(friendContentList.get(position).sTeleNumber);
            holder.iShare.setSelected(false);


            return convertView;
        }

    }

}



