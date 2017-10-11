package com.example.kesar.attendanceiiitdmj;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends AppCompatActivity {

    private TextView mName;
    private TextView mDesignation;
    private TextView mEmail;
    private TextView mContact;
    private TextView mAddress;
    private TextView mWebpage;

    private CircleImageView mDisplayImage;

    private ImageButton mProfileEdit;
    private ImageButton mFacebook;
    private ImageButton mTwitter;
    private ImageButton mLinkedin;
    private ImageButton mGithub;

    private DatabaseReference mFacultyDatabase;
    private ProgressDialog mProgressDialog;

    private FirebaseUser mCurrentUser;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mToolbar = (Toolbar) findViewById(R.id.profile_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid();
        final String email = mCurrentUser.getEmail();

        mFacultyDatabase = FirebaseDatabase.getInstance().getReference().child("Faculty").child(current_uid);

        mName = (TextView) findViewById(R.id.profile_name);
        mDesignation = (TextView) findViewById(R.id.profile_designation);
        mEmail = (TextView) findViewById(R.id.profile_email);
        mContact = (TextView) findViewById(R.id.profile_contact);
        mAddress = (TextView) findViewById(R.id.profile_address);
        mWebpage = (TextView) findViewById(R.id.profile_webpage);

        mProfileEdit = (ImageButton) findViewById(R.id.profile_edit_button);

        mDisplayImage = (CircleImageView) findViewById(R.id.profile_image);

        mFacultyDatabase.keepSynced(true);

        mFacultyDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String designation = dataSnapshot.child("designation").getValue().toString();
                String contact = dataSnapshot.child("contact_number").getValue().toString();
                String address = dataSnapshot.child("address").getValue().toString();
                String webpage = dataSnapshot.child("webpage").getValue().toString();

                final String image = dataSnapshot.child("image").getValue().toString();

                mName.setText(name);
                mDesignation.setText(designation);
                mEmail.setText(email);
                mContact.setText(contact);
                mAddress.setText(address);
                mWebpage.setText(webpage);

                if(!image.equals("default")) {

                    Picasso.with(ProfileActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.avatar).into(mDisplayImage, new Callback() {
                        @Override
                        public void onSuccess() {



                        }

                        @Override
                        public void onError() {

                            Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.avatar).into(mDisplayImage);

                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProfileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = mName.getText().toString();
                String designation = mDesignation.getText().toString();
                String contactno = mContact.getText().toString();
                String address = mAddress.getText().toString();
                String webpage = mWebpage.getText().toString();

                Intent editIntent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                editIntent.putExtra("name", name);
                editIntent.putExtra("designation", designation);
                editIntent.putExtra("contactno", contactno);
                editIntent.putExtra("address", address);
                editIntent.putExtra("webpage", webpage);
                startActivity(editIntent);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.profile_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.profile_edit) {

            String name = mName.getText().toString();
            String designation = mDesignation.getText().toString();
            String contactno = mContact.getText().toString();
            String address = mAddress.getText().toString();
            String webpage = mWebpage.getText().toString();

            Intent editProfile = new Intent(ProfileActivity.this, EditProfileActivity.class);
            editProfile.putExtra("name", name);
            editProfile.putExtra("designation", designation);
            editProfile.putExtra("contactno", contactno);
            editProfile.putExtra("address", address);
            editProfile.putExtra("webpage", webpage);
            startActivity(editProfile);

        }
        return true;
    }

}
