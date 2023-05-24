package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {

    private TextView textViewWelcome,textViewFullName,textViewEmail,textViewConatct, textViewGender,textViewDob;
    private ProgressBar progressBar;
    private String fullname,email,dob,gender,mobile;
    private ImageView imageView;
    private FirebaseAuth authProfile;
    private SwipeRefreshLayout swipeContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getSupportActionBar().setTitle("SETTINGS");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        swipeTorefresh();

        textViewWelcome=findViewById(R.id.text_view_welcome_message);
        textViewFullName=findViewById(R.id.textview_full_name);
        textViewEmail=findViewById(R.id.textview_full_email);
        textViewConatct=findViewById(R.id.textview_full_contact);
        textViewGender=findViewById(R.id.textview_full_gender);
        textViewDob=findViewById(R.id.textview_full_dob);

        imageView=findViewById(R.id.image_view_dp);

        progressBar=findViewById(R.id.progress_bar);

        // set onclick listener onImageview to upload profile picture
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(UserProfileActivity.this,UploadProfilePicActivity.class);
                startActivity(intent);
            }
        });

        authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser= authProfile.getCurrentUser();

        if(firebaseUser==null)
        {
            Toast.makeText(UserProfileActivity.this,"Something went wrong at that moment. User details not available!",Toast.LENGTH_LONG).show();

        }else {
            //checkifEmailVerified(firebaseUser);
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }



    }

    private void swipeTorefresh() {
        swipeContainer=findViewById(R.id.swiperefresh);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startActivity(getIntent());
                finish();
                overridePendingTransition(0,0);
                swipeContainer.setRefreshing(false);

            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,android.R.color.holo_green_light
        ,android.R.color.holo_orange_light,android.R.color.holo_red_light);
    }

  /*  private void checkifEmailVerified(FirebaseUser firebaseUser) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users/" + firebaseUser.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean isEmailVerified = snapshot.child("isEmailVerified").getValue(Boolean.class);
                if (isEmailVerified == null || !isEmailVerified) {
                    showAlertDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Toast.makeText(UserProfileActivity.this,"Something went wrong!",Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    private void showAlertDialog() {

        AlertDialog.Builder builder=new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle("E-mail not verified");
        builder.setMessage("Please verify your mail, without verification you can't log in next time");

        // open the email app for verification
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        // create the alert dialog box

        AlertDialog alertDialog= builder.create();

        //show the alertdialog
        alertDialog.show();
    }*/

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userId= firebaseUser.getUid();

        DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails=snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserDetails!=null)
                {
                    fullname= firebaseUser.getDisplayName();
                    email= firebaseUser.getEmail();
                    dob=readUserDetails.do_b;
                    mobile=readUserDetails.contact_number;
                    gender=readUserDetails.gen_der;


                    textViewWelcome.setText("Welcome " + fullname +"!");
                    textViewFullName.setText(fullname);
                    textViewEmail.setText(email);
                    textViewConatct.setText(mobile);
                    textViewDob.setText(dob);
                    textViewGender.setText(gender);

                    // set the dp when photo is uploaded
                    Uri uri=firebaseUser.getPhotoUrl();

                    Picasso.with(UserProfileActivity.this).load(uri).into(imageView);
                }else {
                    Toast.makeText(UserProfileActivity.this,"Something went wrong!",Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this,"Something went wrong!",Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    // creating action bar menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // inflate items
        getMenuInflater().inflate(R.menu.common_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
// when any menu item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home)
        {
            NavUtils.navigateUpFromSameTask(UserProfileActivity.this);
        }

        else if(id==R.id.menu_refresh)
        {
            // refresh activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        }
        else if(id==R.id.menu_update_profile)
        {
            Intent intent =new Intent(UserProfileActivity.this,UpdateProfileActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.menu_update_email)
        {
            Intent intent =new Intent(UserProfileActivity.this,UpdateEmailActivity.class);
            startActivity(intent);
        }else if(id==R.id.menu_update_pwd)
        {
            Intent intent =new Intent(UserProfileActivity.this,UpdatePwdActivity.class);
            startActivity(intent);
        }else if(id==R.id.menu_update_delete_profile)
        {
            Intent intent =new Intent(UserProfileActivity.this,DeleteProfileActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.menu_update_log_out)
        {
            authProfile.signOut();
            Toast.makeText(UserProfileActivity.this,"You are logged out!",Toast.LENGTH_LONG).show();
            Intent intent=new Intent(UserProfileActivity.this,MainActivity.class);

            // clear stack to prevent user coming back to this activity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(UserProfileActivity.this,"Something went wrong!",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}