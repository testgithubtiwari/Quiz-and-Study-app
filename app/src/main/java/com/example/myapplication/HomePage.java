package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View;
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

public class HomePage extends AppCompatActivity {
    private TextView head;
    private TextView name;
    private ImageView imageView;
    private FirebaseAuth authProfile;
    private String fullname;

    private CardView cardView1,cardView2,cardView3,cardView4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        getSupportActionBar().setTitle("HomePage");
        head=findViewById(R.id.textview_homepage_head);
        Typeface typeface= ResourcesCompat.getFont(this,R.font.blacklist);
        head.setTypeface(typeface);

        imageView=findViewById(R.id.profile_image);
        name=findViewById(R.id.title);

        authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser= authProfile.getCurrentUser();

        if(firebaseUser==null)
        {
            Toast.makeText(HomePage.this,"Something went wrong at that moment. User details not available!",Toast.LENGTH_LONG).show();

        }else {
            showUserProfile(firebaseUser);
        }


        ImageView myImageButton = findViewById(R.id.profile_icon);
        myImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start the new activity here
                Intent intent = new Intent(HomePage.this, UserProfileActivity.class);
                startActivity(intent);
            }
        });

        cardView1 = findViewById(R.id.cardview1);
        cardView2 = findViewById(R.id.cardview2);
        cardView3=findViewById(R.id.cardview3);
        cardView4=findViewById(R.id.cardview4);

        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePage.this, Physics.class);
                cardView1.setCardBackgroundColor(getResources().getColor(R.color.cardview_click_background));
                startActivity(i);

                // Reset card background color after a delay
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cardView1.setCardBackgroundColor(getResources().getColor(R.color.cardview_background));
                    }
                }, 1000); // Reset background color after 1 second
            }
        });

        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePage.this, Chemistry.class);
                cardView2.setCardBackgroundColor(getResources().getColor(R.color.cardview_click_background));
                startActivity(i);

                // Reset card background color after a delay
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cardView2.setCardBackgroundColor(getResources().getColor(R.color.cardview_background));
                    }
                }, 1000); // Reset background color after 1 second
            }
        });

        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePage.this, Maths.class);
                cardView3.setCardBackgroundColor(getResources().getColor(R.color.cardview_click_background));
                startActivity(i);

                // Reset card background color after a delay
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cardView3.setCardBackgroundColor(getResources().getColor(R.color.cardview_background));
                    }
                }, 1000); // Reset background color after 1 second
            }
        });

        cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePage.this, Biology.class);
                cardView4.setCardBackgroundColor(getResources().getColor(R.color.cardview_click_background));
                startActivity(i);

                // Reset card background color after a delay
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cardView4.setCardBackgroundColor(getResources().getColor(R.color.cardview_background));
                    }
                }, 1000); // Reset background color after 1 second
            }
        });



    }

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
                    name.setText("Hi " + fullname +"!");

                    // set the dp when photo is uploaded
                    Uri uri=firebaseUser.getPhotoUrl();

                    Picasso.with(HomePage.this).load(uri).into(imageView);
                }else {
                    Toast.makeText(HomePage.this,"Something went wrong!",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomePage.this,"Something went wrong!",Toast.LENGTH_LONG).show();
            }
        });

    }

}
