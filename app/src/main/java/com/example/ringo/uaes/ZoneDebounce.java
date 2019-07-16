package com.example.ringo.uaes;


import android.util.Log;

public class ZoneDebounce {


//原理：查表法连续ZONEBUFFER次获得一样结果，才可以实现初级区域跳变，连续



    private int debouncedZone1=-1,debouncedZone2=-1,lastZone=-1,

    debouncedCounter1=0,debouncedCounter2=0,DEBOUNCEBUFFER;

    private int ZONEBUFFERNUMBER=5,

    DEBOUNCEBUFFERMATRIX[][]={
            {0,5,0,40,0,0,0},
            {0,0,0,20,0,0,0},
            {0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0}};



    public int DebouncedZone(int curZone)

    {



        if (curZone==-1){

            curZone=debouncedZone1;

        }

        if (curZone==lastZone){

            debouncedCounter1=debouncedCounter1+1;

        }else{

            lastZone=curZone;

            debouncedCounter1=0;

        }

        if (debouncedCounter1>=ZONEBUFFERNUMBER){

            debouncedZone1=curZone;

        }

        if (debouncedZone1==curZone){

            debouncedCounter2=debouncedCounter2+1;

        }else{

            debouncedCounter2=0;

        }
        int a =debouncedZone2,b=debouncedZone1;
        if (a==-1)a=2;
        if (b==-1)b=2;
        DEBOUNCEBUFFER=DEBOUNCEBUFFERMATRIX[a][b];

        if(debouncedCounter2>=DEBOUNCEBUFFER){

            debouncedZone2=debouncedZone1;

        }

        return debouncedZone2;

    }

}

