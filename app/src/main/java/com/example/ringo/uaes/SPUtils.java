package com.example.ringo.uaes;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ringo on 2018/8/10.
 * SharedPreferences工具类
 */

public class  SPUtils {
    private static SharedPreferences sp;

    public static void initSharedPreferences(MyApplication application){
        sp = application.getSharedPreferences("com.example.ringo.uaes", Context.MODE_PRIVATE);

    }

    public static void save(String key, String value){
        sp.edit().putString(key, value).commit();
    }

    public static void save(String key, boolean value){
        sp.edit().putBoolean(key, value).commit();
    }

    public static void save(String key, int value){
        sp.edit().putInt(key, value).commit();
    }

    public static String getString(String key){
        return sp.getString(key, "");
    }

    public static int getInt(String key){
        return sp.getInt(key, -1);
    }

    public static boolean getBoolean(String key){
        return sp.getBoolean(key, false);
    }
}
