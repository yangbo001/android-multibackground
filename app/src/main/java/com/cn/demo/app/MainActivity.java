package com.cn.demo.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.icon_disable_enable).setEnabled(false);
        findViewById(R.id.icon_disable_enable_default).setEnabled(false);
    }
}