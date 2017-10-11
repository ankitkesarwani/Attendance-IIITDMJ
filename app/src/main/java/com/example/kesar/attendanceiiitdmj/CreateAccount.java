package com.example.kesar.attendanceiiitdmj;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.data.DataBufferObserverSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

import java.util.HashMap;

public class CreateAccount extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputLayout mName;
    private TextInputLayout mContactno;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Spinner mAccountFor;

    private Button mCreateAccountBtn;
    private String userRole;
    private String[] userRoleString = new String[] { "Student", "Faculty"};

    private DatabaseReference mDatabase;

    private DatabaseReference mUserDatabaseForStudents;
    private DatabaseReference mUserDatabaseForFaculty;

    private ProgressDialog mRegProgress;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mToolbar = (Toolbar) findViewById(R.id.create_account_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRegProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mUserDatabaseForStudents = FirebaseDatabase.getInstance().getReference().child("Students");
        mUserDatabaseForFaculty = FirebaseDatabase.getInstance().getReference().child("Faculty");

        mName = (TextInputLayout) findViewById(R.id.create_account_name);
        mContactno = (TextInputLayout) findViewById(R.id.create_account_contactno);
        mEmail = (TextInputLayout) findViewById(R.id.create_account_email_id);
        mPassword = (TextInputLayout) findViewById(R.id.create_account_password);

        mAccountFor = (Spinner) findViewById(R.id.create_account_for);

        mCreateAccountBtn = (Button) findViewById(R.id.create_account_btn);

        mAccountFor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                userRole = (String) mAccountFor.getSelectedItem();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<String> adapter_role = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, userRoleString);
        adapter_role.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAccountFor.setAdapter(adapter_role);

        mCreateAccountBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = mName.getEditText().getText().toString();
                String contactno = mContactno.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                if(!TextUtils.isEmpty(name) || !TextUtils.isEmpty(contactno) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {

                    mRegProgress.setTitle("Registering User...");
                    mRegProgress.setMessage("Please wait while we create your account !");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();

                    registerUser(name, contactno, email, password);

                } else {

                    Toast.makeText(CreateAccount.this, "Please enter all the required field", Toast.LENGTH_LONG).show();

                }

            }
        });

    }

    private void registerUser(final String name, final String contactNo, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {

                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();

                    if(userRole.equals("Student")) {

                        mDatabase = FirebaseDatabase.getInstance().getReference().child("Students").child(uid);

                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                        HashMap<String, String> userMap = new HashMap<>();
                        userMap.put("device_token", deviceToken);
                        userMap.put("name", name);
                        userMap.put("contact_number", contactNo);
                        userMap.put("image", "default");
                        userMap.put("thumb_image", "default");

                        mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()) {

                                    mRegProgress.dismiss();

                                    Intent mainIntent = new Intent(CreateAccount.this, MainActivity.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                    finish();

                                }

                            }
                        });

                    } else if(userRole.equals("Faculty")) {

                        mDatabase = FirebaseDatabase.getInstance().getReference().child("Faculty").child(uid);

                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                        HashMap<String, String> userMap = new HashMap<>();
                        userMap.put("device_token", deviceToken);
                        userMap.put("name", name);
                        userMap.put("designation", "No designation Added");
                        userMap.put("address", "No Address Added");
                        userMap.put("webpage", "No Webpage Added");
                        userMap.put("contact_number", contactNo);
                        userMap.put("image", "default");
                        userMap.put("thumb_image", "default");
                        userMap.put("facebook_link", "https://www.facebook.com/");
                        userMap.put("twitter_link", "https://twitter.com/");
                        userMap.put("linkedin_link", "https://www.linkedin.com/in/");
                        userMap.put("github_link", "https://github.com/");

                        mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()) {

                                    mRegProgress.dismiss();

                                    Intent mainIntent = new Intent(CreateAccount.this, MainActivity.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                    finish();

                                }

                            }
                        });

                    }

                } else {

                    mRegProgress.hide();
                    Toast.makeText(CreateAccount.this, "Error in Registering. Please try Again...", Toast.LENGTH_LONG).show();

                }

            }
        });

    }
}
