package com.obenacademy.gist.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.obenacademy.gist.Data.GistRecyclerAdapter;
import com.obenacademy.gist.Model.Gist;
import com.obenacademy.gist.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PostListActivity<gist> extends AppCompatActivity {
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private RecyclerView recyclerView;
    private GistRecyclerAdapter gistRecyclerAdapter;
    private List<Gist> gistList;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("Gist");
        mDatabaseReference.keepSynced(true);

        gistList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(gistRecyclerAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add:
                if (mUser != null && mAuth != null) {

                    startActivity(new Intent(PostListActivity.this, AddPostActivity.class));
                    finish();
                }
                break;
            case R.id.action_signout:

                if (mUser != null && mAuth != null) {
                    mAuth.signOut();

                    startActivity(new Intent(PostListActivity.this, MainActivity.class));
                    finish();
                }
        }
                return super.onOptionsItemSelected(item);
        }

    @Override
    protected void onStart() {
        super.onStart();

        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Gist gist = dataSnapshot.getValue(Gist.class);

                gistList.add(gist);

                Collections.reverse(gistList);

                gistRecyclerAdapter = new GistRecyclerAdapter(PostListActivity.this, gistList);
                recyclerView.setAdapter(gistRecyclerAdapter);
                gistRecyclerAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
