package com.example.soura.radiusfeedback;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.soura.radiusfeedback.CustomerActivity.navItemIndex;

public class AdminActivity extends AppCompatActivity {

    Button hr,manager,food,security,employees,repairing,others,helpdesk;

    private FirebaseUser mCurrentUser;
    private String current_uid;
    private FirebaseAuth mAuth;

    private Toolbar toolbar;


    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        toolbar = (Toolbar) findViewById(R.id.status_app_bar);
        setSupportActionBar(toolbar);

        hr = (Button) findViewById(R.id.hr);
        manager = (Button) findViewById(R.id.manager);
        food = (Button) findViewById(R.id.food);
        security = (Button) findViewById(R.id.security);
        employees = (Button)findViewById(R.id.employees);
        repairing = (Button)findViewById(R.id.repairing);
        others = (Button) findViewById(R.id.others);
        helpdesk = (Button) findViewById(R.id.conversation);

        mAuth = FirebaseAuth.getInstance();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        current_uid = mCurrentUser.getUid();

        hr.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(AdminActivity.this,HRActivity.class);
                startActivity(intent);
            }
        });

        manager.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent2 = new Intent(AdminActivity.this,ManagerActivity.class);
                startActivity(intent2);
            }
        });

        food.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent3 = new Intent(AdminActivity.this,FoodActivity.class);
                startActivity(intent3);
            }
        });

        security.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent4 = new Intent(AdminActivity.this,SecurityActivity.class);
                startActivity(intent4);
            }
        });

        employees.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent5 = new Intent(AdminActivity.this,EmployeesActivity.class);
                startActivity(intent5);
            }
        });

        repairing.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent5 = new Intent(AdminActivity.this,RepairingActivity.class);
                startActivity(intent5);
            }
        });

        others.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent5 = new Intent(AdminActivity.this,OthersActivity.class);
                startActivity(intent5);
            }
        });

        helpdesk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent5 = new Intent(AdminActivity.this,ConversationListActivity.class);
                startActivity(intent5);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.customer, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
        }
        else if (id == R.id.action_about) {
            goAbout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void goAbout()
    {
        Intent about = new Intent(AdminActivity.this,AboutActivity.class);
        startActivity(about);
    }

    private void logout()
    {
        mAuth.signOut();
        updateUI();

    }

    private void updateUI()
    {
        Intent account = new Intent(AdminActivity.this,LoginActivity.class);
        account.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(account);
        finish();
    }

}
