package com.anioncode.memory.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.anioncode.memory.R;

public class ShowPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_page);
        Intent intent = getIntent();
        String test = intent.getStringExtra("ShowPageActivity");
        TextView textView=findViewById(R.id.textView);
        textView.setText(test);
    }
}
