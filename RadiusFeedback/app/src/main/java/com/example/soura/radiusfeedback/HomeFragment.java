package com.example.soura.radiusfeedback;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import static com.example.soura.radiusfeedback.CustomerActivity.CURRENT_TAG;
import static com.example.soura.radiusfeedback.CustomerActivity.navItemIndex;

public class HomeFragment extends Fragment {

    View view;
    Button Feedback,Help;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.fragment_home, container, false);

       Feedback = (Button)view.findViewById(R.id.bfeedback);
       Help = (Button)view.findViewById(R.id.bhelp);

       Feedback.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v)
           {
               CURRENT_TAG = "feedback";
               FeedbackFragment fragment = new FeedbackFragment();
               FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
               fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
               fragmentTransaction.add(R.id.containerfragment, fragment, CURRENT_TAG);
               fragmentTransaction.addToBackStack(CURRENT_TAG);
               fragmentTransaction.commitAllowingStateLoss();
           }
       });

        Help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                CURRENT_TAG = "helpdesk";
                HelpdeskFragment fragment = new HelpdeskFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                fragmentTransaction.add(R.id.containerfragment, fragment, CURRENT_TAG);
                fragmentTransaction.addToBackStack(CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        });

        return view;
    }
}