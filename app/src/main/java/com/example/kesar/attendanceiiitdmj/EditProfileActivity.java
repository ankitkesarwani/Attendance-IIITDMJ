package com.example.kesar.attendanceiiitdmj;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class EditProfileActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private TextInputLayout mName;
    private TextInputLayout mDesignation;
    private TextInputLayout mContactno;
    private TextInputLayout mAddress;
    private TextInputLayout mWebpage;
    private TextInputLayout mFacebook;
    private TextInputLayout mTwitter;
    private TextInputLayout mLinkedin;
    private TextInputLayout mGithub;

    private Button mChangeImage;
    private Button mSaveChanges;

    private DatabaseReference mFacultyDatabase;
    private FirebaseUser mCurrentUser;

    private CircleImageView mDisplayImage;

    private static final int GALLERY_PICK = 1;

    private StorageReference mImageStorage;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mToolbar = (Toolbar) findViewById(R.id.edit_profile_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String name_value = getIntent().getStringExtra("name");
        String designation_value = getIntent().getStringExtra("designation");
        String contactno_value = getIntent().getStringExtra("contactno");
        String address_value = getIntent().getStringExtra("address");
        String webpage_value = getIntent().getStringExtra("webpage");

        mDisplayImage = (CircleImageView) findViewById(R.id.edit_profile_image);
        mName = (TextInputLayout) findViewById(R.id.edit_profile_name);
        mDesignation = (TextInputLayout) findViewById(R.id.edit_profile_designation);
        mContactno = (TextInputLayout) findViewById(R.id.edit_profile_contact);
        mAddress = (TextInputLayout) findViewById(R.id.edit_profile_address);
        mWebpage = (TextInputLayout) findViewById(R.id.edit_profile_webpage);
        mFacebook = (TextInputLayout) findViewById(R.id.edit_profile_facebook);
        mTwitter = (TextInputLayout) findViewById(R.id.edit_profile_twitter);
        mLinkedin = (TextInputLayout) findViewById(R.id.edit_profile_linkedin);
        mGithub = (TextInputLayout) findViewById(R.id.edit_profile_github);

        mChangeImage = (Button) findViewById(R.id.edit_profile_change_image);
        mSaveChanges = (Button) findViewById(R.id.edit_profile_save_changes);

        mName.getEditText().setText(name_value);
        mDesignation.getEditText().setText(designation_value);
        mContactno.getEditText().setText(contactno_value);
        mAddress.getEditText().setText(address_value);
        mWebpage.getEditText().setText(webpage_value);
        mFacebook.getEditText().setText("https://www.facebook.com/");
        mTwitter.getEditText().setText("https://twitter.com/");
        mLinkedin.getEditText().setText("https://www.linkedin.com/in/");
        mGithub.getEditText().setText("https://github.com/");

        mImageStorage = FirebaseStorage.getInstance().getReference();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid();

        mFacultyDatabase = FirebaseDatabase.getInstance().getReference().child("Faculty").child(current_uid);
        mFacultyDatabase.keepSynced(true);

        mFacultyDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String image = dataSnapshot.child("image").getValue().toString();

                if (!image.equals("default")) {

                    Picasso.with(EditProfileActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.avatar).into(mDisplayImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                            Picasso.with(EditProfileActivity.this).load(image).placeholder(R.drawable.avatar).into(mDisplayImage);

                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

            }
        });

        mSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProgressDialog = new ProgressDialog(EditProfileActivity.this);
                mProgressDialog.setTitle("Saving Changes");
                mProgressDialog.setMessage("Please wait while we updating changes");
                mProgressDialog.show();

                String name = mName.getEditText().getText().toString();
                String designation = mDesignation.getEditText().getText().toString();
                String contactno = mContactno.getEditText().getText().toString();
                String address = mAddress.getEditText().getText().toString();
                String webpage = mWebpage.getEditText().getText().toString();
                String facebook = mFacebook.getEditText().getText().toString();
                String twitter = mTwitter.getEditText().getText().toString();
                String linkedin = mLinkedin.getEditText().getText().toString();
                String github = mGithub.getEditText().getText().toString();

                mFacultyDatabase.child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()) {



                        } else {

                            Toast.makeText(getApplicationContext(), "There was some error in saving status.", Toast.LENGTH_LONG).show();

                        }
                    }
                });
                mFacultyDatabase.child("designation").setValue(designation).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()) {



                        } else {

                            Toast.makeText(getApplicationContext(), "There was some error in saving status.", Toast.LENGTH_LONG).show();

                        }
                    }
                });
                mFacultyDatabase.child("contact_number").setValue(contactno).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()) {



                        } else {

                            Toast.makeText(getApplicationContext(), "There was some error in saving status.", Toast.LENGTH_LONG).show();

                        }
                    }
                });
                mFacultyDatabase.child("address").setValue(address).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()) {



                        } else {

                            Toast.makeText(getApplicationContext(), "There was some error in saving status.", Toast.LENGTH_LONG).show();

                        }
                    }
                });
                mFacultyDatabase.child("webpage").setValue(webpage).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()) {



                        } else {

                            Toast.makeText(getApplicationContext(), "There was some error in saving status.", Toast.LENGTH_LONG).show();

                        }
                    }
                });
                mFacultyDatabase.child("facebook_link").setValue(facebook).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()) {



                        } else {

                            Toast.makeText(getApplicationContext(), "There was some error in saving status.", Toast.LENGTH_LONG).show();

                        }
                    }
                });
                mFacultyDatabase.child("twitter_link").setValue(twitter).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()) {



                        } else {

                            Toast.makeText(getApplicationContext(), "There was some error in saving status.", Toast.LENGTH_LONG).show();

                        }
                    }
                });
                mFacultyDatabase.child("linkedin_link").setValue(linkedin).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()) {



                        } else {

                            Toast.makeText(getApplicationContext(), "There was some error in saving status.", Toast.LENGTH_LONG).show();

                        }
                    }
                });
                mFacultyDatabase.child("github_link").setValue(github).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()) {

                            mProgressDialog.dismiss();

                        } else {

                            Toast.makeText(getApplicationContext(), "There was some error in saving status.", Toast.LENGTH_LONG).show();

                        }
                    }
                });

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK) {

                mProgressDialog = new ProgressDialog(EditProfileActivity.this);
                mProgressDialog.setTitle("Uploading Image...");
                mProgressDialog.setMessage("Please wait while we upload and process the image.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                Uri resultUri = result.getUri();

                File thumb_filepath = new File(resultUri.getPath());

                String current_user_id = mCurrentUser.getUid();

                Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(75)
                        .compressToBitmap(thumb_filepath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                StorageReference filePath = mImageStorage.child("profile_images").child(current_user_id + ".jpg");

                final StorageReference thumb_filePath = mImageStorage.child("profile_images").child("thumbs").child(current_user_id + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()) {

                            final String download_url = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_filePath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();

                                    if(thumb_task.isSuccessful()) {

                                        Map updateHashmap = new HashMap<>();
                                        updateHashmap.put("image", download_url);
                                        updateHashmap.put("thumb_image", thumb_downloadUrl);

                                        mFacultyDatabase.updateChildren(updateHashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()) {

                                                    mProgressDialog.dismiss();
                                                    Toast.makeText(EditProfileActivity.this, "Uploading Success", Toast.LENGTH_LONG).show();

                                                }

                                            }
                                        });

                                    } else {

                                        Toast.makeText(EditProfileActivity.this, "Error in Uploading Thumbnail", Toast.LENGTH_LONG).show();
                                        mProgressDialog.dismiss();

                                    }

                                }
                            });
                        } else {

                            Toast.makeText(EditProfileActivity.this, "Error in Uploading", Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();

                        }

                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }

        }

    }


}
