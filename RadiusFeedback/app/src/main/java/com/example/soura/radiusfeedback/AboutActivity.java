package com.example.soura.radiusfeedback;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription("Team Maverick \n" +
                        "It has two sections: Admin and Customers. Admin will define the user type in the database. Customer will simply have to " +
                        " login and give the feedback. If they have any query that they want to be addressed then they can talk to Helpdesk.\n\n" +
                        "Additional Features:\n" +
                        "Image Uploading: In this feature the customer is allowed to capture the image if he/she wants and upload for the admin to address.\n" +
                        "Rating: In this feature the customer can rate a particular department upto his will.\n" +
                        "Help Desk: In this feature the customer can directly put his queries to be addressed by the admin.")
                .setImage(R.drawable.logo)
                .addItem(new Element().setTitle("Version 1.2"))
                .addGroup("Connect with us")
                .addEmail("sourabh.654321@outlook.com")
                .addWebsite("https://github.com/sourabh48")
                .addFacebook("sourabh.sarkar.750")
                .create();
        setContentView(aboutPage);
    }
}