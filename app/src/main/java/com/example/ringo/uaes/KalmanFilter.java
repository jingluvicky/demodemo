package com.example.ringo.uaes;

import android.util.Log;

public class KalmanFilter {
   double A=1;
   double Q=0.03;
   double H=1;
   double R=10^2;
   double B=0;
   double U=0;
   double x=0;
   double P;
   double K;

  public double FilteredRSSI(double newRSSIValue)

  {
    double z=newRSSIValue;
    if (0==x ){
      x=z;
      P=R;
    }
    else{
      x=x+B*U;
      P=P+Q;
      K=P/(P+R);
      x=x+K*(z-x);
      P=P-K*P;
    }

    return x;
  }

}
