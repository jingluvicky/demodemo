package com.example.ringo.uaes;

import android.util.Log;

public class Prediction_dynamicRSSI {

    private static final int WINDOW = 20;
    private static final int BUFFERWINDOW=20,TRENDWINDOW=10;
    private static final int SensorNumber=3;
    private float[][]storage=new float[WINDOW][SensorNumber];
    private int[] buffer=new int[20];
    private int outputs=0;
    private float lastValue=0,curValue;
    private float[]arr_sort;
    private int [][]trendBuffer=new int [TRENDWINDOW][2];

    public int getTrend(Node[]Nodes){
        for (int i=0;i<TRENDWINDOW-1;i++){
            for(int j=0;j<2;j++)
                trendBuffer[i][j]=trendBuffer[i+1][j];
        }
        curValue=getSum(arr_sort);
        Log.d("curvalue",(curValue-lastValue)+" "+curValue);
        float temp=curValue-lastValue;
        //if (curValue-lastValue>2 && curValue-lastValue<10)
            trendBuffer[TRENDWINDOW-1][0]=(int)curValue;//curValue-lastValue;
        //else trendBuffer[TRENDWINDOW-1]=0;
        trendBuffer[TRENDWINDOW-1][1]=trendBuffer[TRENDWINDOW-1][0]-trendBuffer[0][0];
        lastValue=curValue;
        int sum=0;
        for (int i=0;i<TRENDWINDOW-1;i++){
            if (trendBuffer[i][1]<50)
            sum=sum+trendBuffer[i][1];
        }
        Log.d("sum",sum+" ");
        return (int)sum/TRENDWINDOW;//sum;
        //if (sum>TRENDWINDOW/3)
        //return 1;
        //else return 0;
    }
    public int getPredict() {

        if (getMax(storage)>7 && storage[0][0]-storage[WINDOW-1][0]<-10)
           outputs=1;
        else outputs=0;
        buffer[BUFFERWINDOW-1]=outputs;
        Log.d("dynamic1",getMax(storage)+" "+(storage[0][0]-storage[WINDOW-1][0])+"  "+outputs);
        for (int i=0;i<BUFFERWINDOW-1;i++){
            buffer[i]=buffer[i+1];
        }
        int sum=0;
        for (int i=0;i<BUFFERWINDOW;i++){
            sum=sum+buffer[i];
        }
        if (sum>0)
            return 1;
        else return 0;
    }


    public void Storage(Node[] Nodes)
    {
            for (int i=0;i<WINDOW-1;i++){
                for(int j=0;j<SensorNumber;j++)
                    storage[i][j]=storage[i+1][j];
            }
            //find minimal value of anchor
            float []arr=new float[9];
            for (int i=0;i<9;i++){
                arr[i]=(float)(Nodes[i+1].RSSI_max);
            }
            //float []arr={10,59,43,59,66,33};
             arr_sort=bubbleSort01(arr);

            storage[WINDOW-1][0]=(float)arr_sort[0];
            storage[WINDOW-1][1]=(float)storage[WINDOW-1][0]-storage[WINDOW-2][0];

            for (int i=0;i<SensorNumber;i++){
                storage[WINDOW-1][i]=storage[WINDOW-1][i];

            }
        }


    //sort the array, from minimal to maximal
    public static float[] bubbleSort01(float[]arr) {
        //minimal value find

        float []nodeRSSI=arr;
        //float[]nodeRSSI={92,38,49,37,59,73};
        float temp; // 记录临时中间值
        int tempNumber=0;
        int size = nodeRSSI.length; // 数组大小
        for (int i = 0; i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {
                if (nodeRSSI[i] > nodeRSSI[j]) { //交换两数的位置
                    tempNumber=i;
                    temp = nodeRSSI[i];
                    nodeRSSI[i] = nodeRSSI[j];
                    nodeRSSI[j] = temp;
                }
            }
        }
        return nodeRSSI;
    }

    // find the maximal value
    // return [0] value
    // return [1] value index
    public static int getMax(float[][] storage) {
        float []arr=new float[WINDOW];
        for (int i = 0;i<WINDOW;i++){
            arr[i]=storage[i][1];
        }

        if(arr==null||arr.length==0){
            return 0;//如果数组为空 或者是长度为0 就返回null
        }
        int maxIndex=0;//假设第一个元素为最小值 那么下标设为0
        int[] arrnew=new int[2];//设置一个 长度为2的数组 用作记录 规定第一个元素存储最小值 第二个元素存储下标
        for(int i =0;i<arr.length-1;i++){
            if(arr[maxIndex]<arr[i+1]){
                maxIndex=i+1;
            }
        }
        arrnew[0]=(int)arr[maxIndex];
        arrnew[1]=maxIndex;

        return arrnew[0];
    }
    public static float getSum(float[]arr_sort){

        float sum=0;
        for(int i =0;i<3;i++){
            sum=sum+arr_sort[i];
            }
        return sum;
    }
}
