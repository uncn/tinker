package com.sunzn.tinker.sample;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.sunzn.tinker.library.Tinker;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Tinker tinker = new Tinker(this);
//        tinker.setTinkerColor(Color.parseColor("#3F51B5"));
//        tinker.setTinkerResource(R.mipmap.ic_launcher);
//        tinker.setTinkerResource(R.color.colorPrimaryDark);
        tinker.setTinkerColor(Color.RED);
//        tinker.setTinkerAlpha(0.5F);
//        Tinker.setBarLightMode(this);
        Tinker.setBarDarkMode(this);
    }

}
