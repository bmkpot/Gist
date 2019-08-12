package com.obenacademy.gist.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.obenacademy.gist.R;

import java.util.HashMap;
import java.util.Map;


public class AddPostActivity extends AppCompatActivity {
    private ImageButton mPostImage;
    private EditText mPostTitle;
    private EditText mPostDesc;
    private Button mSubmitButton;
    private StorageReference mStorage;
    private DatabaseReference mPostDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ProgressDialog mProgress;
    private Uri mImageUri;
    private static final int GALLERY_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_add_post);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance().getReference();

        mPostDatabase = FirebaseDatabase.getInstance().getReference().child("Gist");

        mPostImage = (ImageButton) findViewById ( R.id.profilePic );
        mPostTitle = (EditText) findViewById ( R.id.postTitleBt );
        mPostDesc = (EditText) findViewById ( R.id.descriptionBt );
        mSubmitButton = (Button) findViewById ( R.id.submitPost );

        mPostImage.setOnClickListener( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent ( Intent.ACTION_GET_CONTENT );
                galleryIntent.setType ("image/*");
                galleryIntent.setAction (Intent.ACTION_GET_CONTENT);
                startActivityForResult (galleryIntent, GALLERY_CODE);

            }
        } );


        mSubmitButton.setOnClickListener( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                //Posting to our database
                startPosting();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {

            mImageUri = data.getData();
            mPostImage.setImageURI(mImageUri);
        }
    }

    private void startPosting() {

        mProgress.setMessage("Posting message...");
        mProgress.show ();

        final String titleVal = mPostTitle.getText().toString().trim();
        final String descVal = mPostDesc.getText().toString().trim();

        if (!TextUtils.isEmpty(titleVal) && !TextUtils.isEmpty(descVal) && mImageUri != null) {

            final StorageReference filePath = mStorage.child ("Gist_images")
                    .child((mImageUri.getLastPathSegment ()));


            filePath.putFile(mImageUri).addOnCompleteListener (new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {

                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloadUrl = uri.toString();


                                DatabaseReference newPost = mPostDatabase.push();


                                Map<String, String> dataToSave = new HashMap<>();
                                dataToSave.put("title", titleVal);
                                dataToSave.put("desc", descVal);
                                dataToSave.put("image", downloadUrl);
                                dataToSave.put("timestamp",String.valueOf(java.lang.System.currentTimeMillis()));
                                dataToSave.put("userid", mUser.getUid () );

                                newPost.setValue (dataToSave);
                                mProgress.dismiss();

                                startActivity (new Intent(AddPostActivity.this, PostListActivity.class));
                                finish();

                            }
                        } );

                    }
                }
            } );
        }
    }
}
