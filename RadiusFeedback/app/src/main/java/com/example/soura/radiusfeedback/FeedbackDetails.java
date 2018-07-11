package com.example.soura.radiusfeedback;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class FeedbackDetails extends AppCompatActivity
{

    private DatabaseReference detailsdata;

    String taskid,department,Subject,Details,Suggessions,Image,Rating;
    TextView departmenttext,Subjecttext,Detailstext,Suggessionstext,Ratingtext;

    ImageView feedimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_details);

        departmenttext =(TextView)findViewById(R.id.departmentresult);
        Subjecttext =(TextView)findViewById(R.id.subjectresult);
        Detailstext =(TextView)findViewById(R.id.detailsresult);
        Suggessionstext =(TextView)findViewById(R.id.suggessionresult);
        Ratingtext =(TextView)findViewById(R.id.ratingresult);

        feedimage = (ImageView)findViewById(R.id.feedbackimage);

        taskid = getIntent().getStringExtra("taskid");
        department = getIntent().getStringExtra("department");

        detailsdata = FirebaseDatabase.getInstance().getReference().child("Feedback").child(department).child(taskid);

        detailsdata.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Subject = dataSnapshot.child("subject").getValue().toString();
                Details = dataSnapshot.child("details").getValue().toString();
                Suggessions = dataSnapshot.child("suggession").getValue().toString();
                Rating = dataSnapshot.child("rating").getValue().toString();
                Image = dataSnapshot.child("image").getValue().toString();

                departmenttext.setText(department);
                Subjecttext.setText(Subject);
                Suggessionstext.setText(Suggessions);
                Detailstext.setText(Details);
                Ratingtext.setText(Rating);

                Picasso.get().load(Image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_add_black_24dp).into(feedimage, new Callback() {

                    @Override
                    public void onSuccess()
                    {}
                    @Override
                    public void onError(Exception e)
                    {
                        Picasso.get().load(Image).placeholder(R.drawable.ic_add_black_24dp).into(feedimage);
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
