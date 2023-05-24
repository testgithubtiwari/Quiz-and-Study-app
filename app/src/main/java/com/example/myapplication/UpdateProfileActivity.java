package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText editTextUpdateName,editTextUpdateDob,editTextUpdateMobile;
    private RadioGroup radioGroupUpdateGender;
    private RadioButton radioButtonUpdateSelectedGender;
    private String textfullName,textDob,textMobile,textGender;
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        getSupportActionBar().setTitle("Update Profile Page");

        editTextUpdateName=findViewById(R.id.edittext_upadate_full_name);
        editTextUpdateMobile=findViewById(R.id.edittext_upadate_mobile);
        editTextUpdateDob=findViewById(R.id.edittext_upadate_dob);
        radioGroupUpdateGender=findViewById(R.id.radio_group_update_gender);

        progressBar=findViewById(R.id.progress_bar_update_profile);

        authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser= authProfile.getCurrentUser();

        showProfile(firebaseUser);


        Button buttonUploadProfilePic=findViewById(R.id.button_update_profile_pic);
        buttonUploadProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UpdateProfileActivity.this,UploadProfilePicActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // setting up datepicker on edit text
        editTextUpdateDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // extracting dd/mm/yy in a varaible
                String textSADoB[]=textDob.split("/");


                int day = Integer.parseInt(textSADoB[0]);
                int month= Integer.parseInt(textSADoB[1]) - 1;
                int year= Integer.parseInt(textSADoB[2]);

                DatePickerDialog picker;

                // datepicker dialog
                picker=new DatePickerDialog(UpdateProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextUpdateDob.setText(dayOfMonth + "/" + (month+1) + "/" + year);
                    }
                },year,month,day);
                picker.show();
            }
        });

        Button buttonUpdateProfile=findViewById(R.id.button_update_profile);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(firebaseUser);
            }
        });

    }

    private void updateProfile(FirebaseUser firebaseUser) {
        int selectedGenderID=radioGroupUpdateGender.getCheckedRadioButtonId();
        radioButtonUpdateSelectedGender=findViewById(selectedGenderID);



        // validate the mobile number using regular expression
        String mobileRegx="[6-9][0-9]{9}";
        Matcher mobileMatcher;
        Pattern mobilePattern=Pattern.compile(mobileRegx);
        mobileMatcher=mobilePattern.matcher(textMobile);


        if(TextUtils.isEmpty(textfullName)){
            Toast.makeText(UpdateProfileActivity.this,"Please enter your full name",Toast.LENGTH_LONG).show();
            editTextUpdateName.setError("Full name is Required");
            editTextUpdateName.requestFocus();
        }else if(TextUtils.isEmpty(textMobile))
        {
            Toast.makeText(UpdateProfileActivity.this,"Please enter your contact number",Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Conatct number is Required");
            editTextUpdateMobile.requestFocus();
        }else if(textMobile.length()!=10)
        {
            Toast.makeText(UpdateProfileActivity.this,"Please enter correct mobile number",Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Contact number should be of 10 digits");
            editTextUpdateMobile.requestFocus();
        }else if(!mobileMatcher.find()){
            Toast.makeText(UpdateProfileActivity.this,"Please re-enter your mobile number",Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Format is wrong");
            editTextUpdateMobile.requestFocus();
        }
        else if(TextUtils.isEmpty(textDob))
        {
            Toast.makeText(UpdateProfileActivity.this,"Please enter your date of Birth",Toast.LENGTH_LONG).show();
            editTextUpdateDob.setError("DOB is Required");
            editTextUpdateDob.requestFocus();
        }else if(TextUtils.isEmpty(radioButtonUpdateSelectedGender.getText()))
        {
            Toast.makeText(UpdateProfileActivity.this,"Select your gender",Toast.LENGTH_LONG).show();
            radioButtonUpdateSelectedGender.setError("Gender is Required");
            radioButtonUpdateSelectedGender.requestFocus();
        }else {
            textGender=radioButtonUpdateSelectedGender.getText().toString();
            textDob=editTextUpdateDob.getText().toString();
            textMobile=editTextUpdateMobile.getText().toString();
            textfullName=editTextUpdateName.getText().toString();


            ReadWriteUserDetails writeUserDetails=new ReadWriteUserDetails(textDob,textGender,textMobile);

            DatabaseReference referenceProfile=FirebaseDatabase.getInstance().getReference("Registered Users");

            String userID= firebaseUser.getUid();
            progressBar.setVisibility(View.VISIBLE);
            referenceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        UserProfileChangeRequest profileUpdates=new UserProfileChangeRequest.Builder().setDisplayName(textfullName).build();
                        firebaseUser.updateProfile(profileUpdates);

                        Toast.makeText(UpdateProfileActivity.this,"Update Successful!",Toast.LENGTH_LONG);

                        Intent intent=new Intent(UpdateProfileActivity.this,UserProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        try{
                            throw task.getException();
                        }catch(Exception e)
                        {
                            Toast.makeText(UpdateProfileActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();

                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });

        }



    }

    private void showProfile(FirebaseUser firebaseUser) {
        String userIdregistered= firebaseUser.getUid();

        DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered Users");
        progressBar.setVisibility(View.VISIBLE);

        referenceProfile.child(userIdregistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails=snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserDetails!=null)
                {
                    textfullName= firebaseUser.getDisplayName();
                    textDob= readUserDetails.do_b;
                    textGender= readUserDetails.gen_der;
                    textMobile= readUserDetails.contact_number;

                    editTextUpdateName.setText(textfullName);
                    editTextUpdateDob.setText(textDob);
                    editTextUpdateMobile.setText(textMobile);

                    if(textGender.equals("Male"))
                    {
                        radioButtonUpdateSelectedGender=findViewById(R.id.radio_male);
                    }else {
                        radioButtonUpdateSelectedGender=findViewById(R.id.radio_female);
                    }
                    radioButtonUpdateSelectedGender.setChecked(true);
                }else
                {
                    Toast.makeText(UpdateProfileActivity.this,"Something went wrong!",Toast.LENGTH_LONG).show();

                }
                progressBar.setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfileActivity.this,"Something went wrong!",Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);

            }
        });
    }

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

        if(id==R.id.menu_refresh)
        {
            // refresh activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        }else if(id==R.id.menu_update_profile)
        {
            Intent intent =new Intent(UpdateProfileActivity.this,UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.menu_update_email)
        {
            Intent intent =new Intent(UpdateProfileActivity.this,UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        }else if(id==R.id.menu_update_pwd)
        {
            Intent intent =new Intent(UpdateProfileActivity.this,UpdatePwdActivity.class);
            startActivity(intent);
            finish();
        }else if(id==R.id.menu_update_delete_profile)
        {
            Intent intent =new Intent(UpdateProfileActivity.this,DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.menu_update_log_out)
        {
            authProfile.signOut();
            Toast.makeText(UpdateProfileActivity.this,"You are logged out!",Toast.LENGTH_LONG).show();
            Intent intent=new Intent(UpdateProfileActivity.this,MainActivity.class);

            // clear stack to prevent user coming back to this activity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(UpdateProfileActivity.this,"Something went wrong!",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}