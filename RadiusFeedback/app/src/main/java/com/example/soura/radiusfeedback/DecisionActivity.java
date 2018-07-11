package com.example.soura.radiusfeedback;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DecisionActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private DatabaseReference mUserRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decision);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserRef.keepSynced(true);

        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.hasChild(mCurrentUserId))
                {
                    String type = dataSnapshot.child(mCurrentUserId).child("usertype").getValue().toString();

                    if (type.equals("registered")) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent account = new Intent(DecisionActivity.this, CustomerActivity.class);
                                account.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(account);
                                finish();
                            }
                        }, 1500);
                    } else if (type.equals("admin")) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent account = new Intent(DecisionActivity.this, AdminActivity.class);
                                account.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(account);
                                finish();
                            }
                        }, 1500);
                    }
                }
                else
                {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent account = new Intent(DecisionActivity.this, SetupActivity.class);
                            account.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(account);
                            finish();
                        }
                    }, 2500);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}