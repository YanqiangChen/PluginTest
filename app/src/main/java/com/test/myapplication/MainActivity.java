package com.test.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.test.mylibrary.annotation.Author;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnTest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnTest=findViewById(R.id.btn_test);
        btnTest.setOnClickListener(this);
    }

    @Author(name="Chiba", time=2005)
    @Override
    public void onClick(View view) {

    }
}
