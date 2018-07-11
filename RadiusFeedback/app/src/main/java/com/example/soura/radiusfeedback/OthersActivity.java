package com.example.soura.radiusfeedback;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class OthersActivity extends AppCompatActivity {

    Toolbar toolbar;
    private RecyclerView mFriendsList;
    private DatabaseReference mFeedbackDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employees);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFriendsList = (RecyclerView) findViewById(R.id.feedbacklist);
        mAuth = FirebaseAuth.getInstance();

        mFeedbackDatabase = FirebaseDatabase.getInstance().getReference().child("Feedback").child("Others");
        mFeedbackDatabase.keepSynced(true);

        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query = mFeedbackDatabase.orderByChild("timestamp");

        FirebaseRecyclerOptions<ProjectData> options = new FirebaseRecyclerOptions.Builder<ProjectData>()
                .setQuery(query, ProjectData.class)
                .setLifecycleOwner(this)
                .build();


        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<ProjectData,TaskViewHolder>(options) {
            @Override
            public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_view, parent, false);

                return new TaskViewHolder(view);
            }

            protected void onBindViewHolder(TaskViewHolder taskViewHolder, int position, ProjectData model) {
                taskViewHolder.setSubject(model.getSubject());

                final String taskid = getRef(position).getKey();

                taskViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent = new Intent(OthersActivity.this, FeedbackDetails.class);
                        profileIntent.putExtra("taskid", taskid);
                        profileIntent.putExtra("department", "Others");
                        startActivity(profileIntent);
                    }
                });

            }
        };
        mFriendsList.setAdapter(adapter);
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public TaskViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setSubject(String name) {

            TextView userNameView = (TextView) mView.findViewById(R.id.single_user_name);
            userNameView.setText(name);
        }
    }

}
