package com.example.ringo.uaes;

public class KalmanFilter_A_max {
    double A=1;
    double Q=0.005;
    //double Q=0.005;
    double H=1;
    double R=15^2;
    double B=-0.01;
    double U=0;
    double x=0;
    double P;
    double K;
    double z;
    double rssiOld=70;
    int[] rssiBuffer=new int[40];
    int count =0;
    KalmanFilter_A_max(){
        for (int i =0;i<40;i++){
            rssiBuffer[i]=100;
        }
    }

    public double[] FilteredRSSI(double newRSSIValue,boolean valid)

    {
        if ((Math.abs(newRSSIValue-rssiOld))>30){
            newRSSIValue=rssiOld;
        }
        if (count==2){
            for (int i=39;i>0;i--){
                rssiBuffer[i]=rssiBuffer[i-1];
            }


            rssiBuffer[0]=(int)newRSSIValue;
            count=0;
        }
        int maxRssi=100;
        for (int j=0;j<40;j++){
            if (maxRssi>rssiBuffer[j]){
                maxRssi=rssiBuffer[j];
            }
        }

        if (0==x ){
            x=maxRssi;
            P=R;
        }else if ((Math.abs(newRSSIValue-rssiOld))>20)
            z=x;
        else{
            z=maxRssi;
        }

        if(0!=x)

        {
            x = x + B * U;
            P = P + Q;
            K = P / (P + R);
            x = x + K * (z - x);
            P = P - K * P;

        }
        count=count+1;
        rssiOld=newRSSIValue;
        double[]temp={x,maxRssi};
        return temp;
    }

}

