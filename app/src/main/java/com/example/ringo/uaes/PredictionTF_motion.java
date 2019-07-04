package com.example.ringo.uaes;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;


public class PredictionTF_motion {
    private static final String TAG = "PredictionTF";
    //设置模型输入/输出节点的数据维度

    private static final int WINDOW = 128;
    private static final int SENSORNUMBER=3;

    //模型中输入变量的名称
    private static final String inputName = "x_input";
    //模型中输出变量的名称
    private static final String outputName = "output";
    private static final String modePath="file:///android_asset/HARModel_motion190226005.pb";
    TensorFlowInferenceInterface inferenceInterface;


    private float[][]storage=new float[WINDOW][SENSORNUMBER];
    static {
        //加载libtensorflow_inference.so库文件
        System.loadLibrary("tensorflow_inference");
        Log.e(TAG,"libtensorflow_inference.so库加载成功");
    }


    PredictionTF_motion(AssetManager assetManager) {
        //初始化TensorFlowInferenceInterface对象
        inferenceInterface = new TensorFlowInferenceInterface(assetManager,modePath);
        Log.e(TAG,"TensoFlow模型文件加载成功");

    }

    /**
     *  利用训练好的TensoFlow模型预测结果
     *
     * @return 返回预测结果，int数组
     */
    public int getPredict() {
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
        float[] outputs = new float[3];
        if (storage[50][0]!=0) {
            //将数据feed给tensorflow的输入节点
            inferenceInterface.feed(inputName, inputdata2, 1, WINDOW, SENSORNUMBER);
            //运行tensorflow
            String[] outputNames = new String[]{outputName};
            inferenceInterface.run(outputNames);
            ///获取输出节点的输出信息
           //用于存储模型的输出数据
            inferenceInterface.fetch(outputName, outputs);
        }else {
            outputs[0]=0;outputs[1]=0;outputs[2]=0;
        }

        float tempMax = -10;
        int tempMaxNumber = 0;
        for (int i = 0; i < 3; i++) {
            if (outputs[i] > tempMax) {
                tempMax = outputs[i];
                tempMaxNumber = i;
            }
        }
        tempMaxNumber = tempMaxNumber + 1;

        if (tempMaxNumber == 2 && outputs[1] > 2) {
            return tempMaxNumber;
        } else if (tempMaxNumber != 2) {
            return tempMaxNumber;
        }
        return 0;
    }

    public void Storage(float[]gyro,float[] linearacc,float[]gravity){
        int Window=WINDOW;
        for (int i=0;i<Window-1;i++){
            for(int j=0;j<SENSORNUMBER;j++)
           storage[i][j]=storage[i+1][j];


        }
        storage[WINDOW-1][0]=Math.abs(gyro[0])+Math.abs(gyro[1])+Math.abs(gyro[2]);
        storage[WINDOW-1][1]=Math.abs(linearacc[0])+Math.abs(linearacc[1])+Math.abs(linearacc[2]);
        storage[WINDOW-1][2]=Math.abs(gravity[0])+Math.abs(gravity[1])+Math.abs(gravity[2]);

    }
    public void clearAll(){
        storage=new float[WINDOW][SENSORNUMBER];
        for (int i=0;i<WINDOW;i++){
            for(int j=0;j<SENSORNUMBER;j++)
                storage[i][j]=0;


        }
    }

}
