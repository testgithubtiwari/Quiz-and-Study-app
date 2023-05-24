package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextregisterfullname,editTextregisterfullemail,editTextregistercontactnumber,editTextregisterdob,
    editTextpassword;
    private RadioGroup radiogroupregistergender;
    private ProgressBar progressBar;
    private RadioButton radiobuttonregisterselectedgender;
    private DatePickerDialog picker;
    private static final  String TAG="RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("REGISTER PAGE");


        Toast.makeText(RegisterActivity.this,"You can register now",Toast.LENGTH_LONG).show();

        editTextregisterfullname=findViewById(R.id.edittext_register_full_name);
        editTextregisterfullemail=findViewById(R.id.edittext_register_full_email);
        editTextregistercontactnumber=findViewById(R.id.edittext_register_full_contactNumber);
        editTextregisterdob=findViewById(R.id.edittext_register_full_DOB);
        editTextpassword=findViewById(R.id.edittext_register_full_password);
        progressBar=findViewById(R.id.progressbar);


        radiogroupregistergender=findViewById(R.id.radio_group_register_gender);
        radiogroupregistergender.clearCheck();

        // setting up datepicker on edit text
        editTextregisterdob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar=Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month=calendar.get(Calendar.MONTH);
                int year=calendar.get(Calendar.YEAR);

                // datepicker dialog
                picker=new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            editTextregisterdob.setText(dayOfMonth + "/" + (month+1) + "/" + year);
                    }
                },year,month,day);
                picker.show();
            }
        });

        Button buttonRegister=findViewById(R.id.regsiterpage_button);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedgenderId=radiogroupregistergender.getCheckedRadioButtonId();
                radiobuttonregisterselectedgender=findViewById(selectedgenderId);


                String fullname=editTextregisterfullname.getText().toString();
                String email=editTextregisterfullemail.getText().toString();
                String dob=editTextregisterdob.getText().toString();
                String contactnumber=editTextregistercontactnumber.getText().toString();
                String password=editTextpassword.getText().toString();
                String textgender;


                // validate the mobile number using regular expression
                String mobileRegx="[6-9][0-9]{9}";
                Matcher mobileMatcher;
                Pattern mobilePattern=Pattern.compile(mobileRegx);
                mobileMatcher=mobilePattern.matcher(contactnumber);


                if(TextUtils.isEmpty(fullname)){
                    Toast.makeText(RegisterActivity.this,"Please enter your full name",Toast.LENGTH_LONG).show();
                    editTextregisterfullname.setError("Full name is Required");
                    editTextregisterfullname.requestFocus();
                }else if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(RegisterActivity.this,"Please enter your Email",Toast.LENGTH_LONG).show();
                    editTextregisterfullemail.setError("Email is Required");
                    editTextregisterfullemail.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    Toast.makeText(RegisterActivity.this,"Please re-enter Email",Toast.LENGTH_LONG).show();
                    editTextregisterfullemail.setError("Valid E-mail is Required");
                    editTextregisterfullemail.requestFocus();
                }else if(TextUtils.isEmpty(contactnumber))
                {
                    Toast.makeText(RegisterActivity.this,"Please enter your contact number",Toast.LENGTH_LONG).show();
                    editTextregistercontactnumber.setError("Conatct number is Required");
                    editTextregistercontactnumber.requestFocus();
                }else if(contactnumber.length()!=10)
                {
                    Toast.makeText(RegisterActivity.this,"Please enter correct mobile number",Toast.LENGTH_LONG).show();
                    editTextregistercontactnumber.setError("Contact number should be of 10 digits");
                    editTextregistercontactnumber.requestFocus();
                }else if(!mobileMatcher.find()){
                    Toast.makeText(RegisterActivity.this,"Please re-enter your mobile number",Toast.LENGTH_LONG).show();
                    editTextregistercontactnumber.setError("Format is wrong");
                    editTextregistercontactnumber.requestFocus();
                }
                else if(TextUtils.isEmpty(dob))
                {
                    Toast.makeText(RegisterActivity.this,"Please enter your date of Birth",Toast.LENGTH_LONG).show();
                    editTextregisterdob.setError("DOB is Required");
                    editTextregisterdob.requestFocus();
                }else if(radiogroupregistergender.getCheckedRadioButtonId()==-1)
                {
                    Toast.makeText(RegisterActivity.this,"Select your gender",Toast.LENGTH_LONG).show();
                    radiobuttonregisterselectedgender.setError("Gender is Required");
                    radiobuttonregisterselectedgender.requestFocus();
                }else if(TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this,"Please enter your password",Toast.LENGTH_LONG).show();
                    editTextpassword.setError("Password is required");
                    editTextpassword.requestFocus();
                }else if(password.length()<=6)
                {
                    Toast.makeText(RegisterActivity.this,"Password should be greater than 6 digits",Toast.LENGTH_LONG).show();
                    editTextpassword.setError("Password is too weak!");
                    editTextpassword.requestFocus();
                }else {
                    textgender=radiobuttonregisterselectedgender.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(fullname,dob,email,contactnumber,password,textgender);
                }

            }
        });

    }
    private void registerUser(String fullname, String dob, String email, String contactnumber, String password, String textgender) {
        FirebaseAuth auth=FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseUser firebaseUser=auth.getCurrentUser();

                            UserProfileChangeRequest profileChangeRequest=new UserProfileChangeRequest.Builder().setDisplayName(fullname).build();
                            firebaseUser.updateProfile(profileChangeRequest);

                            // enter user data firebase in the real time
                            ReadWriteUserDetails writeUserDetails =new ReadWriteUserDetails(dob,textgender,contactnumber);

                            /// extracting reference for databse registered users
                            DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered Users");

                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful())
                                    {
                                        // send verification email
                                        firebaseUser.sendEmailVerification();

                                        Toast.makeText(RegisterActivity.this,"User registered succesfully, Please verify your e-mail",Toast.LENGTH_LONG).show();

                                        // open user homepage after registered succesfully
                                        Intent intent=new Intent(RegisterActivity.this,HomePage.class);

                                        // upon registration user can't go to register page
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();// to close register activity
                                    }else {

                                        Toast.makeText(RegisterActivity.this,"Registration failed , try again",Toast.LENGTH_LONG).show();

                                    }
                                    progressBar.setVisibility(View.GONE);
                                }
                            });

                        }
                        else {
                            try{
                                throw task.getException();
                            }
                            catch (FirebaseAuthWeakPasswordException e){
                                editTextpassword.setError("Your password is too weak. Kindly use numeric,alphabets and special characters");
                                editTextpassword.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e){
                                editTextregisterfullemail.setError("Your e-mail is invalid or already in use. Kindly re-enter");
                                editTextregisterfullemail.requestFocus();

                            }catch(FirebaseAuthUserCollisionException e){
                                editTextregisterfullemail.setError("User is already registered with this e-mail, use another");
                                editTextregisterfullemail.requestFocus();
                            }

                            catch (Exception e) {
                                Log.e(TAG,e.getMessage());
                                Toast.makeText(RegisterActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                });


    }
}