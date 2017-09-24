package com.example.kesar.attendanceiiitdmj;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.main_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("IIITDM JABALPUR");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
