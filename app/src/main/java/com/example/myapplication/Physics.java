package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
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

public class Physics extends AppCompatActivity {

    private CardView cardView1_class,cardView3_class,cardView4_class;
    private CardView cardView2_class;
    private TextView name1;
    private ImageView imageView1;
    private FirebaseAuth authProfile;
    private String fullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physics);

        getSupportActionBar().setTitle("Physics Section");
        cardView1_class=findViewById(R.id.class_1);
        cardView2_class=findViewById(R.id.cardview_class_2);
        cardView3_class=findViewById(R.id.cardview_class_3);
        cardView4_class=findViewById(R.id.cardview_class4);

        name1=findViewById(R.id.text1);
        imageView1=findViewById(R.id.im1);

        authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser= authProfile.getCurrentUser();

        if(firebaseUser==null)
        {
            Toast.makeText(Physics.this,"Something went wrong at that moment. User details not available!",Toast.LENGTH_LONG).show();

        }else {
            showUserProfile(firebaseUser);
        }


        cardView1_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Physics.this, Class1.class);
                cardView1_class.setCardBackgroundColor(getResources().getColor(R.color.cardview_click_background));
                startActivity(i);

                // Reset card background color after a delay
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cardView1_class.setCardBackgroundColor(getResources().getColor(R.color.purple_200));
                    }
                }, 1000); // Reset background color after 1 second
            }
        });


        cardView2_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Physics.this, Class2.class);
                cardView2_class.setCardBackgroundColor(getResources().getColor(R.color.cardview_click_background));
                startActivity(i);

                // Reset card background color after a delay
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cardView2_class.setCardBackgroundColor(getResources().getColor(R.color.purple_200));
                    }
                }, 1000); // Reset background color after 1 second
            }
        });

        cardView3_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Physics.this, Class3.class);
                cardView3_class.setCardBackgroundColor(getResources().getColor(R.color.cardview_click_background));
                startActivity(i);

                // Reset card background color after a delay
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cardView3_class.setCardBackgroundColor(getResources().getColor(R.color.purple_200));
                    }
                }, 1000); // Reset background color after 1 second
            }
        });

        cardView4_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Physics.this, Class4.class);
                cardView4_class.setCardBackgroundColor(getResources().getColor(R.color.cardview_click_background));
                startActivity(i);

                // Reset card background color after a delay
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cardView4_class.setCardBackgroundColor(getResources().getColor(R.color.purple_200));
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
                    name1.setText("Hi " + fullname +"!");

                    // set the dp when photo is uploaded
                    Uri uri=firebaseUser.getPhotoUrl();

                    Picasso.with(Physics.this).load(uri).into(imageView1);
                }else {
                    Toast.makeText(Physics.this,"Something went wrong!",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Physics.this,"Something went wrong!",Toast.LENGTH_LONG).show();
            }
        });

    }
}