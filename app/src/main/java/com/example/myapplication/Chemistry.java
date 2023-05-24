package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.net.Uri;
import android.os.Bundle;
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

public class Chemistry extends AppCompatActivity {

    private TextView name1;
    private ImageView imageView1;
    private FirebaseAuth authProfile;
    private String fullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chemistry);

        getSupportActionBar().setTitle("Chemistry Section");

        name1=findViewById(R.id.textview_name);
        imageView1=findViewById(R.id.imageview);


        authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser= authProfile.getCurrentUser();

        if(firebaseUser==null)
        {
            Toast.makeText(Chemistry.this,"Something went wrong at that moment. User details not available!",Toast.LENGTH_LONG).show();

        }else {
            showUserProfile(firebaseUser);
        }

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

                    Picasso.with(Chemistry.this).load(uri).into(imageView1);
                }else {
                    Toast.makeText(Chemistry.this,"Something went wrong!",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Chemistry.this,"Something went wrong!",Toast.LENGTH_LONG).show();
            }
        });

    }
}