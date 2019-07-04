package com.example.ringo.uaes;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.util.Arrays;


public class PredictionTF_frontandrear {
    private static final String TAG = "PredictionTF_zone";
    //设置模型输入/输出节点的数据维度
    private static final int WINDOW = 1;
    private static final int SensorNumber=10;
    //模型中输入变量的名称
    private static final String inputName = "x_input";
    //模型中输出变量的名称
    private static final String outputName = "output";
    private static final String modePath="file:///android_asset/HARModel190310002_FrontAndRear_with10RSSI.pb";
    TensorFlowInferenceInterface inferenceInterface;


    private float[][]storage=new float[WINDOW][SensorNumber];
    static {
        //加载libtensorflow_inference.so库文件
        System.loadLibrary("tensorflow_inference");
        Log.e(TAG,"libtensorflow_inference.so库加载成功");
    }


    PredictionTF_frontandrear(AssetManager assetManager) {
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

        inferenceInterface.feed(inputName, inputdata2,1,WINDOW,SensorNumber);
        //运行tensorflow
        String[] outputNames = new String[] {outputName};
        inferenceInterface.run(outputNames);
        ///获取输出节点的输出信息
        float[] outputs = new float[2]; //用于存储模型的输出数据
        inferenceInterface.fetch(outputName, outputs);

        return outputs;
    }

    public void TFclose(){
        inferenceInterface.close();
    }
    public void Storage(Node[] Nodes){

        int Window=WINDOW;
        for (int i=0;i<Window-1;i++){
            for(int j=0;j<SensorNumber;j++){
                storage[i][j]=storage[i+1][j];
            }

        }

        for (int i=0;i<=9;i++){
            storage[WINDOW-1][i]=(float)Nodes[i].RSSI_filtered;
        }

    }
    public double getAverage(double[] arr) {
        double sum = 0;
        int number = arr.length;
        for (int i = 0; i < number; i++) {
            sum += arr[i];
        }
        return sum / number;
    }

    public double getStandardDeviation(double[] arr) {
        double sum = 0;
        int number = arr.length;
        double avgValue = getAverage(arr);//获取平均值
        for (int i = 0; i < number; i++) {
            sum += Math.pow((arr[i] - avgValue), 2);
        }

        return Math.sqrt((sum / (number - 1)));
    }
}
