package com.roto.industrial.base;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.roto.industrial.utils.IndustrialLog;

/**
 * Created by hardy on 16/9/21.
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    public void startActivity(Intent intent) {
        try {
            super.startActivity(intent);
        } catch (Exception e) {
            IndustrialLog.e(e.getMessage());
        }
    }
}
