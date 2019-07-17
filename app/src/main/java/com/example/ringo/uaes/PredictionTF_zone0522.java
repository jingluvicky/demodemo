package com.example.ringo.uaes;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.util.Arrays;


public class PredictionTF_zone0522{
    private static final String TAG = "PredictionTF_zone";
    //设置模型输入/输出节点的数据维度
    private static final int WINDOW = 1;
    private static final int SensorNumber=8;
    //模型中输入变量的名称
    private static final String inputName = "x_input";
    //模型中输出变量的名称
    private static final String outputName = "output";
    private static final String modePath="file:///android_asset/190711_top_nio_distance_fullyconnect.pb";
    TensorFlowInferenceInterface inferenceInterface;


    private float[][]storage=new float[WINDOW][SensorNumber];
    static {
        //加载libtensorflow_inference.so库文件
        System.loadLibrary("tensorflow_inference");
        Log.e(TAG,"libtensorflow_inference.so库加载成功");
    }


    PredictionTF_zone0522(AssetManager assetManager) {
        //初始化TensorFlowInferenceInterface对象
        inferenceInterface = new TensorFlowInferenceInterface(assetManager,modePath);
        Log.e(TAG,"TensoFlow模型文件加载成功");

    }

    /**
     *  利用训练好的TensoFlow模型预测结果
     *
     * @return 返回预测结果，int数组
     */

    public float[] getPredict() {

        float []inputdata2=new float[WINDOW*SensorNumber];

        for (int j=0;j<WINDOW;j++) {
            for (int i = 0; i < SensorNumber; i++) {
                if (storage==null){
                    inputdata2[j * SensorNumber + i] = 0;
                }else
                    inputdata2[j * SensorNumber + i] = storage[j][i];
            }
        }
        //将数据feed给tensorflow的输入节点

        inferenceInterface.feed(inputName, inputdata2,1,SensorNumber);
        //运行tensorflow
        String[] outputNames = new String[] {outputName};
        inferenceInterface.run(outputNames);
        ///获取输出节点的输出信息
        float[] outputs=new float[1]; //用于存储模型的输出数据
        inferenceInterface.fetch(outputName, outputs);

        return outputs;
    }

    public void TFclose(){
        inferenceInterface.close();
    }
    public void Storage(Node[] Nodes,int curPocketstate){
        for (int i=0;i<WINDOW-1;i++){
            for(int j=0;j<SensorNumber;j++)
                storage[i][j]=storage[i+1][j];


        }
        // main,min,std,mean,diff16,sum12
        float[]rssiMax={86,86,84,86,86,86,85};
        float[]rssiMin={45,33,39,38,35,37,36};
        float[]arrNode=new float[7];
        for(int j=0;j<7;j++){
            arrNode[j]=(float)(Nodes[j].RSSI_filtered-curPocketstate*3-rssiMin[j])/(rssiMax[j]-rssiMin[j]);
        }

        int Window=WINDOW;
        storage[WINDOW-1][0]=arrNode[0];
        //find maximum value of anchor
        float []arr=new float[6];
        for (int i=0;i<6;i++){
            arr[i]=(float)arrNode[i+1];
        }

        //float[]arr={91,88,59,84,76,78};
        float [] arr_sort=bubbleSort01(arr);
        storage[WINDOW-1][1]=(float)arr_sort[0];
        storage[WINDOW-1][2]=(float)arr_sort[1];
        storage[WINDOW-1][3]=getStandardDeviation(arr);
        storage[WINDOW-1][4]=getAverage(arr);
        storage[WINDOW-1][5]=arr_sort[5]-arr_sort[0];
        storage[WINDOW-1][6]=arr_sort[0]+arr_sort[1];
        storage[WINDOW-1][7]=arr_sort[0]+arr_sort[1]+arr_sort[2];
        // normalization

        float[] paraMax={1,(float)0.679,(float)0.849,(float)0.352,(float)0.822,(float)0.957,(float)1.417,(float)2.25};
        float[] paraMin={0,0,(float)0.146,(float)0.050,(float)0.503,(float)0.152,(float)0.204,(float)0.826};
        for (int i=0;i<SensorNumber;i++){
            storage[WINDOW-1][i]=(storage[WINDOW-1][i]-paraMin[i])/(paraMax[i]-paraMin[i]);
        }

    }
    // sort the anchor RSSI
    public static float[] bubbleSort01(float[]arr) {
        float []nodeRSSI=arr;
        //float[]nodeRSSI={92,38,49,37,59,73};
        float temp; // 记录临时中间值
        int tempNumber=0;
        int size = nodeRSSI.length; // 数组大小
        for (int i = 0; i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {
                if (nodeRSSI[i] > nodeRSSI[j]) { // 交换两数的位置
                    tempNumber=i;
                    temp = nodeRSSI[i];
                    nodeRSSI[i] = nodeRSSI[j];
                    nodeRSSI[j] = temp;
                }
            }
        }
        return nodeRSSI;
    }
    public static float[] onehot(int number){
        float[]temp=new float[6];
        for (int i=0;i<6;i++) temp[i]=0;
        temp[number]=1;
        return temp;
    }

    public static int getMinIndex(Node[]Nodes) {
        float []arr=new float[6];
        arr[0]=(float)Nodes[1].RSSI_filtered;
        arr[1]=(float)Nodes[2].RSSI_filtered;
        arr[2]=(float)Nodes[3].RSSI_filtered;
        arr[3]=(float)Nodes[4].RSSI_filtered;
        arr[4]=(float)Nodes[5].RSSI_filtered;
        arr[5]=(float)Nodes[6].RSSI_filtered;
        // float[]arr={92,38,49,37,59,73};
        if(arr==null||arr.length==0){
            return 0;//如果数组为空 或者是长度为0 就返回null
        }
        int minIndex=0;//假设第一个元素为最小值 那么下标设为0
        int[] arrnew=new int[2];//设置一个 长度为2的数组 用作记录 规定第一个元素存储最小值 第二个元素存储下标
        for(int i =0;i<arr.length-1;i++){
            if(arr[minIndex]>arr[i+1]){
                minIndex=i+1;
            }
        }
        arrnew[0]=(int)arr[minIndex];
        arrnew[1]=minIndex;
        return arrnew[0];
    }

    public float getAverage(float[] arr) {
        float sum = 0;
        int number = arr.length;
        for (int i = 0; i < number; i++) {
            sum += arr[i];
        }
        return sum / number;
    }

    public float getStandardDeviation(float[] arr) {
        float sum = 0;
        int number = arr.length;
        float avgValue = getAverage(arr);//获取平均值
        for (int i = 0; i < number; i++) {
            sum += Math.pow((arr[i] - avgValue), 2);
        }

        return (float) Math.sqrt((sum / (number)));
    }


}
