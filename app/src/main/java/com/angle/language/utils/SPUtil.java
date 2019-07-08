package com.angle.language.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;

/**
 * @author hejinlong
 * 语言设置的SP
 */
public class SPUtil {


    private final String SP_NAME = "language_setting";
    /**
     * 语言字段的选择名称
     */
    private final String TAG_LANGUAGE = "language_select";
    private static volatile SPUtil instance;

    private final SharedPreferences mSharedPreferences;

    private Locale systemCurrentLocal = Locale.ENGLISH;


    private SPUtil(Context context) {
        mSharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public static SPUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (SPUtil.class) {
                if (instance == null) {
                    instance = new SPUtil(context);
                }
            }
        }
        return instance;
    }

    /**
     * 获取保存的语言类型
     *
     * @param select 相应的语言类型
     */
    public void saveLanguage(int select) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putInt(TAG_LANGUAGE, select);
        edit.apply();
        edit.commit();
    }

    /**
     * 获取当前的语言类型
     *
     * @return 当前的语言类型
     */
    public int getSelectLanguage() {
        return mSharedPreferences.getInt(TAG_LANGUAGE, 0);
    }

    /**
     * 获取当前系统的语言
     *
     * @return 当前系统的语言
     */
    public Locale getSystemCurrentLocal() {
        return systemCurrentLocal;
    }

    /**
     * 设置当前系统的语言
     * @param local 设置语言
     */
    public void setSystemCurrentLocal(Locale local) {
        systemCurrentLocal = local;
    }
}
