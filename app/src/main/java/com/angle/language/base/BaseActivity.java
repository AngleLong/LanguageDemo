package com.angle.language.base;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.angle.language.utils.LocalManageUtil;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalManageUtil.setLocal(newBase));
    }
}
