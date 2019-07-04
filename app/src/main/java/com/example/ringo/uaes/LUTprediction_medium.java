package com.example.ringo.uaes;

public class LUTprediction_medium {
    private int PS_POINT_NUM=1,PE_NODE_NUM=6,PE_POINT_NUM=4,PS_NODE_NUM=9,OFFSET=5;

    int su8aPECaliTable[][] = {

        {0,43,0,255,0,255,0,255,0,255,0,255,0},

        {0,255,0,255,0,255,0,255,0,48,0,255,0},

        {0,255,0,255,0,255,0,255,0,255,0,35,0},

        {0,255,0,255,0,255,0,255,0,255,0,255,0},

    };


    private int Incarindex [][]=  {

            { 1, 0 }, { 2, 0 }, { 3, 0 },

            { 1, 4 }, { 2, 4 }, { 3, 4 },

            { 1, 5 }, { 2, 5 }, { 3, 5 },

    };

    int su8aPSCaliTable[][] = {

        {0,255,0,255,0,255,0,255,0,255,0,255,0,255,0,255,0,255,0},

    };


    int PS_s32CaliFunction(int[] u8aRssi)
    {

        int min;
        int max;
        int index = -1;
        int zone = -1;
        int value = 0;
        for (int point_index = 0; point_index < PS_POINT_NUM; point_index++)
        {
            for (int node_index = 0; node_index < PS_NODE_NUM; node_index++)
            {
                min = su8aPSCaliTable[point_index][ 2 * node_index];
                max = su8aPSCaliTable[point_index][ 2 * node_index + 1];
                zone = su8aPSCaliTable[point_index][ 18];
                value = u8aRssi[Incarindex[node_index][0]] - u8aRssi[Incarindex[node_index][1]];
                if ((zone != -1) && (value >= min) && (value <= max))
                {
                    if (node_index == 8)//条件无效，是否满足之前的条件
                    {
                        index = point_index;
                    }
                    continue;
                }
                else
                {
                    break;
                }

            }
            if (index != -1)
            {
                break;
            }
        }
        return index;
    }

    /*
     * name     : PE_s32CaliFunction
     * function : Locate the Key outside the car
     * parameter: array of Rssi for master and anchors
     * return   :
     *			-1: Not Found
     *			0 : In the PS Zone
     *			1 : In the UnLock Zone
     *			2 ：In the Buffer Zone
     *           3 : In the Lock Zone
     */

    int PE_s32CaliFunction(int[] u8aRssi)
    {
        int min;
        int max;
        int index = -1;
        int zone = -1;
        for (int point_index = 0; point_index < PE_POINT_NUM; point_index++)
        {
            for (int node_index = 0; node_index < PE_NODE_NUM; node_index++)
            {
                min = su8aPECaliTable[point_index][ 2 * node_index];
                max = su8aPECaliTable[point_index][2 * node_index + 1];
                zone = su8aPECaliTable[point_index][ PE_NODE_NUM*2];
                if ((zone!=-1) && (u8aRssi[node_index] >= min) && (u8aRssi[node_index] <= max))
                {
                    if (node_index == 5)//条件无效，是否满足之前的条件
                    {
                        index = point_index;
                    }
                    continue;
                }
                else
                {
                    break;
                }

            }
            if (index != -1)
            {
                break;
            }
        }
        return index;
    }

    /*
     * name     : PEPS_s32CaliFunction
     * function : Locate the Key
     * parameter: array of Rssi for master and anchors
     * return   :
     *			-1: Not Found
     *			0 : In the PS Zone
     *			1 : In the UnLock Zone
     *			2 ：In the Buffer Zone
     *           3 : In the Lock Zone
     */

    int PEPS_s32CaliFunction(int[] u8aRssi)
    {
        int index = PS_s32CaliFunction(u8aRssi);
        int zone = -1;
        if (index != -1)
        {
            zone = su8aPSCaliTable[index][ 18];
        }
        else
        {
            index = PE_s32CaliFunction(u8aRssi);
            if (index != -1)
            {
                zone = su8aPECaliTable[index][12];
            }
            else
            {

            }
        }
        return zone;
    }
}
