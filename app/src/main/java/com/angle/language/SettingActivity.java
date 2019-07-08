package com.angle.language;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.angle.language.base.BaseActivity;
import com.angle.language.utils.LocalManageUtil;

public class SettingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }

    public void changeLanguage(View view) {
        LocalManageUtil.saveSelectLanguage(this, 3);
        MainActivity.reStart(this);
    }

    public static void enter(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }
}
