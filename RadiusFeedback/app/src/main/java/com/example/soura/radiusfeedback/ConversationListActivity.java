package com.example.soura.radiusfeedback;

import android.content.Context;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ConversationListActivity extends AppCompatActivity {

    private RecyclerView mConvList;

   private DatabaseReference mConvDatabase;
    private DatabaseReference mMessageDatabase;
    private DatabaseReference mUsersDatabase;

    Toolbar toolbar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mConvList = (RecyclerView)findViewById(R.id.conv_list);
        mAuth = FirebaseAuth.getInstance();


        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child("Admin");
        mConvDatabase.keepSynced(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child("Admin");

        mUsersDatabase.keepSynced(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);

        linearLayoutManager.setStackFromEnd(true);

        mConvList.setHasFixedSize(true);
        mConvList.setLayoutManager(linearLayoutManager);

    }

    @Override

    public void onStart()
    {
        super.onStart();

        Query conversationQuery  = mConvDatabase.orderByChild("timestamp");

        FirebaseRecyclerOptions<Conv> options = new FirebaseRecyclerOptions.Builder<Conv>()
                .setQuery(conversationQuery , Conv.class)
                .setLifecycleOwner(this)
                .build();

        final FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Conv, ConvViewHolder>(options)
        {
            @Override
            public ConvViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user_list, parent, false);

                return new ConvViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final ConvViewHolder holder, int position, final Conv model)
            {
                final String list_user_id = getRef(position).getKey();
                Query lastMessageQuery = mMessageDatabase.child(list_user_id).limitToLast(1);

                lastMessageQuery.addChildEventListener(new ChildEventListener()
                {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
                    {
                        String data = dataSnapshot.child("message").getValue().toString();
                        holder.setMessage(data);

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {}

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}

                });

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        final String userName = dataSnapshot.child("name").getValue().toString();

                        holder.setName(userName);
                        holder.mView.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                Intent chatIntent = new Intent(ConversationListActivity.this, ConversationActivity.class);
                                chatIntent.putExtra("user_id", list_user_id);
                                startActivity(chatIntent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
        };

        mConvList.setAdapter(adapter);
    }


    public static class ConvViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public ConvViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;
        }


        public void setMessage(String message)
        {
            TextView userStatusView = (TextView) mView.findViewById(R.id.status_text);
            userStatusView.setText(message);
        }

        public void setName(String name)
        {
            TextView userNameView = (TextView) mView.findViewById(R.id.name_text);
            userNameView.setText(name);
        }
    }
}
