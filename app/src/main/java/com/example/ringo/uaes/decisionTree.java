package com.example.ringo.uaes;

public class decisionTree {
    public static int getPredict(Node[] Nodes){
        float []arr=new float[6];
        arr[0]=(float)Nodes[1].RSSI_filtered;
        arr[1]=(float)Nodes[2].RSSI_filtered;
        arr[2]=(float)Nodes[3].RSSI_filtered;
        arr[3]=(float)Nodes[4].RSSI_filtered;
        arr[4]=(float)Nodes[5].RSSI_filtered;
        arr[5]=(float)Nodes[6].RSSI_filtered;
        int minIdx = bubbleSortIdx(arr);
        if (minIdx==0){
            int[]feature_vector=new int[3];
            feature_vector[0]=(int)Nodes[1].RSSI_filtered;
            feature_vector[1]=(int)Nodes[2].RSSI_filtered;
            feature_vector[2]=(int)Nodes[6].RSSI_filtered;
            return decision_tree1(feature_vector);
        }
        if (minIdx==1){
            int[]feature_vector=new int[3];
            feature_vector[0]=(int)Nodes[2].RSSI_filtered;
            feature_vector[1]=(int)Nodes[3].RSSI_filtered;
            feature_vector[2]=(int)Nodes[1].RSSI_filtered;
            return decision_tree2(feature_vector);
        }
        if (minIdx==2){
            int[]feature_vector=new int[3];
            feature_vector[0]=(int)Nodes[3].RSSI_filtered;
            feature_vector[1]=(int)Nodes[4].RSSI_filtered;
            feature_vector[2]=(int)Nodes[2].RSSI_filtered;
            return decision_tree3(feature_vector);
        }
        if (minIdx==3){
            int[]feature_vector=new int[3];
            feature_vector[0]=(int)Nodes[4].RSSI_filtered;
            feature_vector[1]=(int)Nodes[5].RSSI_filtered;
            feature_vector[2]=(int)Nodes[3].RSSI_filtered;
            return decision_tree4(feature_vector);
        }
        if (minIdx==4){
            int[]feature_vector=new int[3];
            feature_vector[0]=(int)Nodes[5].RSSI_filtered;
            feature_vector[1]=(int)Nodes[6].RSSI_filtered;
            feature_vector[2]=(int)Nodes[4].RSSI_filtered;
            return decision_tree5(feature_vector);
        }
        if (minIdx==5){
            int[]feature_vector=new int[3];
            feature_vector[0]=(int)Nodes[6].RSSI_filtered;
            feature_vector[1]=(int)Nodes[1].RSSI_filtered;
            feature_vector[2]=(int)Nodes[5].RSSI_filtered;
            return decision_tree6(feature_vector);
        }
        return 0;
    }
    public static int bubbleSortIdx(float[]arr) {
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
        return tempNumber;
    }

    static int decision_tree1(int[] feature_vector)
    {
        if (feature_vector[0] <= 64.5) {
            if (feature_vector[1] <= 77.5) {
                if (feature_vector[0] <= 61.5) {
                    if (feature_vector[1] <= 71.5) {
                        return 0;
                    }
                    else {
                        return 0;
                    }
                }
                else {
                    if (feature_vector[2] <= 83.5) {
                        return 1;
                    }
                    else {
                        return 2;
                    }
                }
            }
            else {
                if (feature_vector[0] <= 61.5) {
                    if (feature_vector[2] <= 65.5) {
                        return 0;
                    }
                    else {
                        return 0;
                    }
                }
                else {
                    if (feature_vector[2] <= 77.5) {
                        return 0;
                    }
                    else {
                        return 0;
                    }
                }
            }
        }
        else {
            if (feature_vector[0] <= 66.5) {
                if (feature_vector[1] <= 78.5) {
                    if (feature_vector[2] <= 89.5) {
                        return 2;
                    }
                    else {
                        return 0;
                    }
                }
                else {
                    if (feature_vector[0] <= 65.5) {
                        return 0;
                    }
                    else {
                        return 2;
                    }
                }
            }
            else {
                if (feature_vector[0] <= 75.5) {
                    if (feature_vector[1] <= 91.5) {
                        return 2;
                    }
                    else {
                        return 1;
                    }
                }
                else {
                    if (feature_vector[1] <= 77.5) {
                        return 1;
                    }
                    else {
                        return 2;
                    }
                }
            }
        }
    }


    static int decision_tree2(int[] feature_vector)
    {
        if (feature_vector[0] <= 67.5) {
            if (feature_vector[0] <= 62.5) {
                if (feature_vector[0] <= 58.5) {
                    if (feature_vector[1] <= 77.5) {
                        return 0;
                    }
                    else {
                        return 0;
                    }
                }
                else {
                    if (feature_vector[1] <= 70.5) {
                        return 0;
                    }
                    else {
                        return 0;
                    }
                }
            }
            else {
                if (feature_vector[2] <= 68.5) {
                    if (feature_vector[0] <= 65.5) {
                        return 1;
                    }
                    else {
                        return 1;
                    }
                }
                else {
                    if (feature_vector[1] <= 69.5) {
                        return 0;
                    }
                    else {
                        return 2;
                    }
                }
            }
        }
        else {
            if (feature_vector[2] <= 74.5) {
                if (feature_vector[1] <= 81.5) {
                    if (feature_vector[0] <= 71.5) {
                        return 2;
                    }
                    else {
                        return 2;
                    }
                }
                else {
                    if (feature_vector[1] <= 83.5) {
                        return 2;
                    }
                    else {
                        return 2;
                    }
                }
            }
            else {
                if (feature_vector[1] <= 79.5) {
                    if (feature_vector[2] <= 79.5) {
                        return 2;
                    }
                    else {
                        return 0;
                    }
                }
                else {
                    if (feature_vector[1] <= 87.5) {
                        return 2;
                    }
                    else {
                        return 2;
                    }
                }
            }
        }
    }


    static int decision_tree3(int[] feature_vector)
    {
        if (feature_vector[0] <= 66.5) {
            if (feature_vector[2] <= 74.5) {
                if (feature_vector[1] <= 70.5) {
                    if (feature_vector[0] <= 63.5) {
                        return 0;
                    }
                    else {
                        return 0;
                    }
                }
                else {
                    if (feature_vector[1] <= 71.5) {
                        return 1;
                    }
                    else {
                        return 0;
                    }
                }
            }
            else {
                if (feature_vector[0] <= 58.5) {
                    if (feature_vector[1] <= 60.0) {
                        return 0;
                    }
                    else {
                        return 0;
                    }
                }
                else {
                    if (feature_vector[1] <= 63.5) {
                        return 2;
                    }
                    else {
                        return 0;
                    }
                }
            }
        }
        else {
            if (feature_vector[2] <= 73.5) {
                if (feature_vector[2] <= 70.5) {
                    if (feature_vector[1] <= 71.5) {
                        return 1;
                    }
                    else {
                        return 0;
                    }
                }
                else {
                    if (feature_vector[1] <= 83.0) {
                        return 0;
                    }
                    else {
                        return 2;
                    }
                }
            }
            else {
                if (feature_vector[1] <= 70.5) {
                    if (feature_vector[2] <= 85.5) {
                        return 1;
                    }
                    else {
                        return 2;
                    }
                }
                else {
                    if (feature_vector[0] <= 72.5) {
                        return 2;
                    }
                    else {
                        return 2;
                    }
                }
            }
        }
    }


    static int decision_tree4(int[] feature_vector)
    {
        if (feature_vector[0] <= 63.5) {
            if (feature_vector[0] <= 59.5) {
                if (feature_vector[2] <= 74.5) {
                    if (feature_vector[2] <= 72.5) {
                        return 0;
                    }
                    else {
                        return 1;
                    }
                }
                else {
                    if (feature_vector[2] <= 90.5) {
                        return 0;
                    }
                    else {
                        return 2;
                    }
                }
            }
            else {
                if (feature_vector[2] <= 69.5) {
                    if (feature_vector[1] <= 90.5) {
                        return 1;
                    }
                    else {
                        return 1;
                    }
                }
                else {
                    if (feature_vector[2] <= 91.5) {
                        return 0;
                    }
                    else {
                        return 1;
                    }
                }
            }
        }
        else {
            if (feature_vector[1] <= 73.5) {
                if (feature_vector[2] <= 84.5) {
                    if (feature_vector[0] <= 64.5) {
                        return 2;
                    }
                    else {
                        return 0;
                    }
                }
                else {
                    if (feature_vector[1] <= 68.5) {
                        return 0;
                    }
                    else {
                        return 2;
                    }
                }
            }
            else {
                if (feature_vector[0] <= 66.5) {
                    if (feature_vector[1] <= 85.5) {
                        return 2;
                    }
                    else {
                        return 0;
                    }
                }
                else {
                    if (feature_vector[0] <= 71.5) {
                        return 2;
                    }
                    else {
                        return 2;
                    }
                }
            }
        }
    }


    static int decision_tree5(int[] feature_vector)
    {
        if (feature_vector[0] <= 65.5) {
            if (feature_vector[0] <= 61.5) {
                if (feature_vector[0] <= 56.5) {
                    return 0;
                }
                else {
                    if (feature_vector[2] <= 61.5) {
                        return 0;
                    }
                    else {
                        return 0;
                    }
                }
            }
            else {
                if (feature_vector[1] <= 70.5) {
                    if (feature_vector[2] <= 68.5) {
                        return 1;
                    }
                    else {
                        return 1;
                    }
                }
                else {
                    if (feature_vector[2] <= 66.5) {
                        return 0;
                    }
                    else {
                        return 2;
                    }
                }
            }
        }
        else {
            if (feature_vector[0] <= 67.5) {
                if (feature_vector[1] <= 70.5) {
                    if (feature_vector[2] <= 71.5) {
                        return 0;
                    }
                    else {
                        return 1;
                    }
                }
                else {
                    if (feature_vector[2] <= 68.5) {
                        return 2;
                    }
                    else {
                        return 2;
                    }
                }
            }
            else {
                if (feature_vector[1] <= 88.5) {
                    if (feature_vector[1] <= 70.5) {
                        return 2;
                    }
                    else {
                        return 2;
                    }
                }
                else {
                    if (feature_vector[2] <= 78.5) {
                        return 2;
                    }
                    else {
                        return 2;
                    }
                }
            }
        }
    }
    static int decision_tree6(int[] feature_vector)
    {
        if (feature_vector[0] <= 63.5) {
            if (feature_vector[0] <= 56.5) {
                if (feature_vector[2] <= 68.5) {
                    if (feature_vector[1] <= 67.5) {
                        return 0;
                    }
                    else {
                        return 1;
                    }
                }
                else {
                    if (feature_vector[2] <= 75.5) {
                        return 0;
                    }
                    else {
                        return 0;
                    }
                }
            }
            else {
                if (feature_vector[1] <= 72.5) {
                    if (feature_vector[1] <= 62.5) {
                        return 0;
                    }
                    else {
                        return 1;
                    }
                }
                else {
                    if (feature_vector[0] <= 61.5) {
                        return 0;
                    }
                    else {
                        return 2;
                    }
                }
            }
        }
        else {
            if (feature_vector[0] <= 66.5) {
                if (feature_vector[1] <= 68.5) {
                    if (feature_vector[2] <= 83.5) {
                        return 2;
                    }
                    else {
                        return 0;
                    }
                }
                else {
                    if (feature_vector[1] <= 81.5) {
                        return 2;
                    }
                    else {
                        return 2;
                    }
                }
            }
            else {
                if (feature_vector[2] <= 80.5) {
                    if (feature_vector[0] <= 77.5) {
                        return 2;
                    }
                    else {
                        return 1;
                    }
                }
                else {
                    if (feature_vector[1] <= 71.5) {
                        return 2;
                    }
                    else {
                        return 2;
                    }
                }
            }
        }
    }
}
