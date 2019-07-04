package com.example.ringo.uaes;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.util.Arrays;


public class PredictionTF_zone0412 {
    private static final String TAG = "PredictionTF_zone";
    //设置模型输入/输出节点的数据维度
    private static final int WINDOW = 1;
    private static final int SensorNumber=7;
    //模型中输入变量的名称
    private static final String inputName = "x_input";
    //模型中输出变量的名称
    private static final String outputName = "output";
    private static final String modePath="file:///android_asset/190412001_nnUWB__normalized.pb";
    TensorFlowInferenceInterface inferenceInterface;


    private float[][]storage=new float[WINDOW][SensorNumber];
    static {
        //加载libtensorflow_inference.so库文件
        System.loadLibrary("tensorflow_inference");
        Log.e(TAG,"libtensorflow_inference.so库加载成功");
    }


    PredictionTF_zone0412(AssetManager assetManager) {
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
    public void Storage(Node[] Nodes){
        // main,min,std,mean,diff16,sum12
        //float[]rssiMax={87,95,96,98,96,95,95};
        //float[]rssiMin={47,44,53,46,54,48,49};
        //for(int j=0;j<7;j++){
        //    Nodes[j].RSSI_filtered=(Nodes[j].RSSI_filtered-rssiMin[j])/(rssiMax[j]-rssiMin[j]);
        //}
        int Window=WINDOW;
        storage[WINDOW-1][0]=(float)Nodes[0].RSSI_filtered;
        //find maximum value of anchor
        float []arr=new float[6];
        for (int i=0;i<6;i++){
            arr[i]=(float)Nodes[i+1].RSSI_filtered;
        }
        //float[]arr={91,88,59,84,76,78};
        float [] arr_sort=bubbleSort01(arr);
        storage[WINDOW-1][1]=(float)arr_sort[0];
        storage[WINDOW-1][2]=(float)arr_sort[1];
        storage[WINDOW-1][3]=getStandardDeviation(arr);
        storage[WINDOW-1][4]=getAverage(arr);
        storage[WINDOW-1][5]=arr_sort[5]-arr_sort[0];
        storage[WINDOW-1][6]=arr_sort[0]+arr_sort[1];
        // normalization
        float[] paraMax={88,86,89,(float)18.0532,(float)89.6667,46,174};
        float[] paraMin={51,46,55,(float)1.25831,72,4,101};
        for (int i=0;i<7;i++){
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
