package com.example.soura.radiusfeedback;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class HelpdeskFragment extends Fragment {
    View view;
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private ImageButton mChatAddBtn;
    private ImageButton mChatSendBtn;
    private EditText mChatMessageView;
    private RecyclerView mMessagesList;
    private SwipeRefreshLayout mRefreshLayout;
    private final List<Conversation> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private ChatAdapter mAdapter;
    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;
    private static final int GALLERY_PICK = 1;

    // Storage Firebase
    private StorageReference mImageStorage;

    //New Solution
    private int itemPos = 0;
    private String mLastKey = "";
    private String mPrevKey = "";



    public HelpdeskFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater1, ViewGroup container,Bundle savedInstanceState) {
        view = inflater1.inflate(R.layout.fragment_helpdesk, container, false);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        mChatAddBtn = (ImageButton)view.findViewById(R.id.chat_add_btn);
        mChatSendBtn = (ImageButton)view.findViewById(R.id.chat_send_btn);
        mChatMessageView = (EditText)view.findViewById(R.id.chat_message_view);

        mAdapter = new ChatAdapter(messagesList);
        mMessagesList = (RecyclerView)view.findViewById(R.id.messages_list);
        mRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.message_swipe_layout);

        mLinearLayout = new LinearLayoutManager(getContext());

        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(new LinearLayoutManager(getContext()));
        mMessagesList.setAdapter(mAdapter);

        //------- IMAGE STORAGE ---------
        mImageStorage = FirebaseStorage.getInstance().getReference();
        loadMessages();

        mRootRef.child("Chat").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(!dataSnapshot.hasChild("Admin")){
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + mCurrentUserId + "/Admin"  , chatAddMap);
                    chatUserMap.put("Chat/Admin/" + mCurrentUserId, chatAddMap);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError != null)
                            {
                                Log.d("CHAT_LOG", databaseError.getMessage().toString());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        mChatSendBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                sendMessage();
            }
        });

        mChatAddBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                mCurrentPage++;
                itemPos = 0;
                loadMoreMessages();
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK)
        {
            Uri imageUri = data.getData();
            final String current_user_ref = "messages/" + mCurrentUserId + "/Admin" ;
            final String chat_user_ref = "messages/Admin/" + mCurrentUserId;

            DatabaseReference user_message_push = mRootRef.child("messages").child(mCurrentUserId).child("Admin").push();

            final String push_id = user_message_push.getKey();
            final StorageReference filepath = mImageStorage.child("message_images").child( push_id + ".jpg");

            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final Uri fileUrl = uri;

                            final String downloadFileUrl = fileUrl.toString();

                            Map messageMap = new HashMap();
                            messageMap.put("message", downloadFileUrl);
                            messageMap.put("seen", false);
                            messageMap.put("type", "image");
                            messageMap.put("time", ServerValue.TIMESTAMP);
                            messageMap.put("from", mCurrentUserId);

                            Map messageUserMap = new HashMap();
                            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                            mChatMessageView.setText("");
                            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener()
                            {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if(databaseError != null){
                                        Log.d("CHAT_LOG", databaseError.getMessage().toString());
                                    }
                                }
                            });
                        }
                    });
                }

            });
        }
    }

    private void loadMoreMessages()
    {
        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child("Admin");
        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Conversation message = dataSnapshot.getValue(Conversation.class);
                String messageKey = dataSnapshot.getKey();

                if(!mPrevKey.equals(messageKey))
                {
                    messagesList.add(itemPos++, message);
                }
                else
                {
                    mPrevKey = mLastKey;
                }

                if(itemPos == 1)
                {
                    mLastKey = messageKey;
                }

                mAdapter.notifyDataSetChanged();
                mRefreshLayout.setRefreshing(false);
                mLinearLayout.scrollToPositionWithOffset(10, 0);
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
    }

    private void loadMessages()
    {
        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child("Admin");
        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Conversation message = dataSnapshot.getValue(Conversation.class);
                itemPos++;

                if(itemPos == 1)
                {
                    String messageKey = dataSnapshot.getKey();
                    mLastKey = messageKey;
                    mPrevKey = messageKey;
                }

                messagesList.add(message);
                mAdapter.notifyDataSetChanged();
                mMessagesList.scrollToPosition(messagesList.size() - 1);
                mRefreshLayout.setRefreshing(false);
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
    }

    private void sendMessage()
    {
        String message = mChatMessageView.getText().toString();

        if(!TextUtils.isEmpty(message))
        {
            String current_user_ref = "messages/" + mCurrentUserId + "/Admin";
            String chat_user_ref = "messages/Admin/" + mCurrentUserId;

            DatabaseReference user_message_push = mRootRef.child("messages").child(mCurrentUserId).child("Admin").push();

            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserId);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            mChatMessageView.setText("");

            mRootRef.child("Chat").child(mCurrentUserId).child("Admin").child("seen").setValue(true);
            mRootRef.child("Chat").child(mCurrentUserId).child("Admin").child("timestamp").setValue(ServerValue.TIMESTAMP);
            mRootRef.child("Chat").child("Admin").child(mCurrentUserId).child("seen").setValue(false);
            mRootRef.child("Chat").child("Admin").child(mCurrentUserId).child("timestamp").setValue(ServerValue.TIMESTAMP);

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError != null){
                        Log.d("CHAT_LOG", databaseError.getMessage().toString());
                    }
                }
            });
        }
    }
}