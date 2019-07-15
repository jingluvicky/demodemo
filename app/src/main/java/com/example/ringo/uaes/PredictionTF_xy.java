package com.example.ringo.uaes;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;


public class PredictionTF_xy {
    private static final String TAG = "PredictionTF";
    //设置模型输入/输出节点的数据维度

    private static final int WINDOW = 10;
    private static final int SENSORNUMBER=7;

    //模型中输入变量的名称
    private static final String inputName = "lstm_1_input";
    //模型中输出变量的名称
    private static final String outputName = "output_0";
    private static final String modePath="file:///android_asset/LSTM_0712_xy.pb";
    TensorFlowInferenceInterface inferenceInterface;


    private float[][]storage=new float[WINDOW][SENSORNUMBER];
    static {
        //加载libtensorflow_inference.so库文件
        System.loadLibrary("tensorflow_inference");
        Log.e(TAG,"libtensorflow_inference.so库加载成功");
    }


    PredictionTF_xy(AssetManager assetManager) {
        //初始化TensorFlowInferenceInterface对象
        inferenceInterface = new TensorFlowInferenceInterface(assetManager,modePath);
        Log.e(TAG,"TensoFlow模型文件加载成功");

    }

    /**
     *  利用训练好的TensoFlow模型预测结果
     *
     * @return 返回预测结果，int数组
     */
    public float []getPredict() {
        //  float[] inputdata = bitmapToFloatArray(bitmap,128,1);//需要将图片缩放带28*28
        //float[][] inputdata1=new float [128][1];
        //inputdata1[1]

        float[]inputdata2=new float[WINDOW*SENSORNUMBER];
       /* for (int i=0;i<9;i++){
            for (int j=0;j<128;j++){
                inputdata2[i*128+j]=storage[j][i];
            }
        }*/

        for (int j=0;j<WINDOW;j++){
            for (int i=0;i<SENSORNUMBER;i++){
                inputdata2[j*SENSORNUMBER+i]=(storage[j][i]);
            }
        }
        float[] outputs = new float[2];

        //将数据feed给tensorflow的输入节点
        inferenceInterface.feed(inputName, inputdata2, 1, WINDOW, SENSORNUMBER);
        //运行tensorflow
        String[] outputNames = new String[]{outputName};
        inferenceInterface.run(outputNames);
        ///获取输出节点的输出信息
        //用于存储模型的输出数据
        inferenceInterface.fetch(outputName, outputs);
        Log.d("trend11",outputs[0]+" ");

        return outputs;
    }

    public void Storage(Node[]nodes){

        for (int i=0;i<WINDOW-1;i++){
            for(int j=0;j<SENSORNUMBER;j++)
                storage[i][j]=storage[i+1][j];
        }
        for(int i=0;i<7;i++){
            storage[WINDOW-1][i]=(int)nodes[i].RSSI_filtered/100;
        }


    }
    public void clearAll(){
        storage=new float[WINDOW][SENSORNUMBER];
        for (int i=0;i<WINDOW;i++){
            for(int j=0;j<SENSORNUMBER;j++)
                storage[i][j]=0;
        }
    }

}
