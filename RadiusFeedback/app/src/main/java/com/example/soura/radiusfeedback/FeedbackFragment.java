package com.example.soura.radiusfeedback;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hsalf.smilerating.SmileRating;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class FeedbackFragment extends Fragment {

    View view;
    Spinner feedbacktype;
    String Rating, FeedbackDetails, FeedbackSuggession, type,subject;
    private Uri filePath;
    Button SubmitButton;
    ImageView feedbackimage;
    EditText FeedbackDetailstext, FeedbackSuggessiontext, Subjecttext;
    private int GALLERY_PICK = 1;
    SmileRating smileRating;
    ProgressDialog progressDialog;
    StorageReference storageReference;
    private DatabaseReference mFeedbackDatabase;


    public FeedbackFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_feedback, container, false);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Submitting...");
        progressDialog.setMessage("Please Wait While We Submit.");
        progressDialog.setCancelable(false);

        smileRating = (SmileRating) view.findViewById(R.id.smile_rating);

        smileRating.setOnRatingSelectedListener(new SmileRating.OnRatingSelectedListener() {
            @Override
            public void onRatingSelected(int level, boolean reselected) {
                switch (level) {
                    case 1:
                        Rating = "Terrible";
                        break;
                    case 2:
                        Rating = "Bad";
                        break;
                    case 3:
                        Rating = "Okay";
                        break;
                    case 4:
                        Rating = "Good";
                        break;
                    case 5:
                        Rating = "Great";
                        break;

                    default:
                        Rating = "No Rating Done";
                }
            }
        });

        storageReference = FirebaseStorage.getInstance().getReference().child("Feedback");


        feedbacktype = (Spinner) view.findViewById(R.id.feedbacktype);

        FeedbackDetailstext = (EditText) view.findViewById(R.id.Details);
        FeedbackSuggessiontext = (EditText) view.findViewById(R.id.Suggestions);
        Subjecttext = (EditText) view.findViewById(R.id.subject);

        mFeedbackDatabase = FirebaseDatabase.getInstance().getReference().child("Feedback");
        mFeedbackDatabase.keepSynced(true);

        feedbackimage = (ImageView) view.findViewById(R.id.feedbackimage);
        feedbackimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                chooseImage();
            }
        });

        SubmitButton = (Button) view.findViewById(R.id.submitButton);

        SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitButton.setClickable(false);
                submitfunction();
            }
        });

        return view;
    }


    private void submitfunction() {
        type = feedbacktype.getSelectedItem().toString().trim();
        FeedbackDetails = FeedbackDetailstext.getText().toString().trim();
        FeedbackSuggession = FeedbackSuggessiontext.getText().toString().trim();
        subject = Subjecttext.getText().toString().trim();

        if(FeedbackSuggession.isEmpty())
        {
            FeedbackSuggession = "No Suggesions";
        }

        if (type.equalsIgnoreCase("Department:"))
        {
            ((TextView) feedbacktype.getSelectedView()).setError("Required");
        }

        else if (FeedbackDetails.isEmpty())
        {
            FeedbackDetailstext.setError("Required");
        }
        else if (subject.isEmpty())
        {
            Subjecttext.setError("Required");
        }

        else
        {
            progressDialog.show();

            if (filePath != null) {

                final StorageReference ref = storageReference.child( type + UUID.randomUUID().toString());

                ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final Uri fileUrl = uri;

                                final String downloadFileUrl = fileUrl.toString();


                                Map<String, Object> requestMap = new HashMap<>();
                                requestMap.put("image", downloadFileUrl);
                                requestMap.put("suggession", FeedbackSuggession);
                                requestMap.put("subject",subject);
                                requestMap.put("details", FeedbackDetails);
                                requestMap.put("timestamp", ServerValue.TIMESTAMP);
                                requestMap.put("rating", Rating);

                                mFeedbackDatabase.child(type).push().updateChildren(requestMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        progressDialog.dismiss();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setTitle("Thank You").setMessage("We Received Your Feedback. Thank You For giving us Your Valueable Time")
                                            .setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();
                                                        SubmitButton.setClickable(true);
                                                    }
                                                });
                                        AlertDialog alert = builder.create();
                                        alert.show();
                                    }
                                });
                            }
                        });
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show(); // code is
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage("Uploaded " + (int) progress + "%");
                            }
                        });

                progressDialog.dismiss();

            }
            else
            {
                Map<String, Object> requestMap = new HashMap<>();

                requestMap.put("image", "default");
                requestMap.put("rating", Rating);
                requestMap.put("subject",subject);
                requestMap.put("suggession", FeedbackSuggession);
                requestMap.put("details", FeedbackDetails);
                requestMap.put("timestamp", ServerValue.TIMESTAMP);

                mFeedbackDatabase.child(type).push().updateChildren(requestMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        progressDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Thank You").setMessage("We Received Your Feedback. Thank You For giving us Your Valueable Time")
                               .setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        SubmitButton.setClickable(true);
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });
            }
        }
    }


    private void chooseImage() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryIntent, GALLERY_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null && data.getData() != null ) {

            filePath = data.getData();

            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                feedbackimage.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}