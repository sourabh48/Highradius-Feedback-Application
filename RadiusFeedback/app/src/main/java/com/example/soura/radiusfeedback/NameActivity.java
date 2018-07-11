package com.example.soura.radiusfeedback;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NameActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputLayout mStatus;

    private FirebaseAuth mAuth;


    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrentUser;

    private ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        String uid = mCurrentUser.getUid();
        mStatusDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        mAuth = FirebaseAuth.getInstance();

        mToolbar=(Toolbar)findViewById(R.id.status_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgress=new ProgressDialog(this);
        mProgress.setTitle("Saving Changes");
        mProgress.setMessage("Please Wait..");

        String status=getIntent().getStringExtra("status");

        mStatus=(TextInputLayout)findViewById(R.id.textInputLayout);

        mStatus.getEditText().setText(status);
    }

    public void Save(View view)
    {

        String status=mStatus.getEditText().getText().toString();
        mProgress.show();
        mStatusDatabase.child("name").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    mProgress.dismiss();
                    Intent account = new Intent(NameActivity.this,UpdateActivity.class);
                    account.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(account);
                    finish();
                }
                else
                {
                    Toast.makeText(NameActivity.this,"Error in Saving", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
