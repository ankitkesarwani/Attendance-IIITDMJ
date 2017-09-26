package com.example.kesar.attendanceiiitdmj;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    private Button mCreateAccountBtn;
    private Button mSigninBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mCreateAccountBtn = (Button) findViewById(R.id.start_create_account_btn);
        mSigninBtn = (Button) findViewById(R.id.start_sign_in_btn);

        mCreateAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent createAccountIntent = new Intent(StartActivity.this, CreateAccount.class);
                startActivity(createAccountIntent);

            }
        });

        mSigninBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent signinIntent = new Intent(StartActivity.this, Signin.class);
                startActivity(signinIntent);

            }
        });

    }
}
