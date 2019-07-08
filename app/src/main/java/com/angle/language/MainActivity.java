package com.angle.language;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.angle.language.base.BaseActivity;

/**
 * 演示Android多语言环境的切换
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public static void reStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void changeLanguage(View view) {
        SettingActivity.enter(this);
    }
}
