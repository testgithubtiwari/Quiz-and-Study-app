package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdateEmailActivity extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private TextView textViewAuthenticate;
    private ProgressBar progressBar1;
    private ProgressBar progressBar2;
    private String userOldEmail,userNewEmail,userPwd;
    private Button buttonUpdateEmail;
    private EditText editTextNewEmail,editTextPwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);
        getSupportActionBar().setTitle("Update Email");

        progressBar1=findViewById(R.id.progressbar_update_RL1);
        progressBar2=findViewById(R.id.progressbar_update_RL2);
        textViewAuthenticate=findViewById(R.id.textview_update_email_authenticate);
        editTextNewEmail=findViewById(R.id.edittext_update_email_new);
        editTextPwd=findViewById(R.id.edittext_update_verify_pwd);
        buttonUpdateEmail=findViewById(R.id.button_update_email);

        buttonUpdateEmail.setEnabled(false);
        editTextNewEmail.setEnabled(false);

        authProfile=FirebaseAuth.getInstance();
        firebaseUser= authProfile.getCurrentUser();

        userOldEmail= firebaseUser.getEmail();
        TextView textViewOldEmail=findViewById(R.id.textview_update_email_old);
        textViewOldEmail.setText(userOldEmail);

        if(firebaseUser.equals(" "))
        {
            Toast.makeText(UpdateEmailActivity.this,"Something went wrong! User details not availabe!",Toast.LENGTH_LONG).show();

        }else
        {
            reAuthenticate(firebaseUser);
        }

    }
/// reauthenticate/verify the user before any update in email
    private void reAuthenticate(FirebaseUser firebaseUser) {
        Button buttonVerifyUser=findViewById(R.id.button_update_email_authneticate);
        buttonVerifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPwd=editTextPwd.getText().toString();

                if(TextUtils.isEmpty(userPwd))
                {
                    Toast.makeText(UpdateEmailActivity.this,"Enter your current password to verify!",Toast.LENGTH_LONG).show();
                    editTextPwd.setError("Password is required!");
                    editTextPwd.requestFocus();
                }else
                {

                    AuthCredential credential= EmailAuthProvider.getCredential(userOldEmail,userPwd);
                    progressBar1.setVisibility(View.VISIBLE);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                progressBar1.setVisibility(View.GONE);
                                Toast.makeText(UpdateEmailActivity.this,"You are verified " + "Now you can update your email!",Toast.LENGTH_LONG).show();
                                textViewAuthenticate.setText("You are verified! ");

                                editTextNewEmail.setEnabled(true);
                                editTextPwd.setEnabled(false);
                                buttonVerifyUser.setEnabled(false);
                                buttonUpdateEmail.setEnabled(true);

                                buttonUpdateEmail.setBackgroundTintList(ContextCompat.getColorStateList(UpdateEmailActivity.this,R.color.dark_green));

                                buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        userNewEmail=editTextNewEmail.getText().toString();
                                        if(TextUtils.isEmpty(userNewEmail))
                                        {
                                            Toast.makeText(UpdateEmailActivity.this,"New Email is Required",Toast.LENGTH_LONG).show();
                                            editTextNewEmail.setError("Enter new email!");
                                            editTextNewEmail.requestFocus();
                                        }else if(!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches())
                                        {
                                            Toast.makeText(UpdateEmailActivity.this,"Vlaid Email is Required",Toast.LENGTH_LONG).show();
                                            editTextNewEmail.setError("Enter valid email!");
                                            editTextNewEmail.requestFocus();
                                        }else if(userNewEmail.matches(userOldEmail))
                                        {
                                            Toast.makeText(UpdateEmailActivity.this,"New Email is not same as Old email",Toast.LENGTH_LONG).show();
                                            editTextNewEmail.setError("Re-enter new email!");
                                            editTextNewEmail.requestFocus();
                                        }else
                                        {
                                            progressBar2.setVisibility(View.VISIBLE);
                                            updateEmail(firebaseUser);
                                        }
                                    }
                                });
                            }else
                            {
                                try{
                                    throw task.getException();
                                }catch(Exception e)
                                {
                                    Toast.makeText(UpdateEmailActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();

                                }
                            }

                        }
                    });
                }
            }
        });
    }

    private void updateEmail(FirebaseUser firebaseUser) {
        firebaseUser.updateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isComplete())
                {
                    firebaseUser.sendEmailVerification();
                    Toast.makeText(UpdateEmailActivity.this,"Email has been updated but you have to verify!",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(UpdateEmailActivity.this,UserProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    {
                        try{
                            throw task.getException();
                        }catch(Exception e)
                        {
                            Toast.makeText(UpdateEmailActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();

                        }
                    }
                }
                progressBar2.setVisibility(View.GONE);
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
        }
        else if(id==R.id.menu_update_profile)
        {
            Intent intent =new Intent(UpdateEmailActivity.this,UpdateProfileActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.menu_update_email)
        {
            Intent intent =new Intent(UpdateEmailActivity.this,UpdateEmailActivity.class);
            startActivity(intent);
        }else if(id==R.id.menu_update_pwd)
        {
            Intent intent =new Intent(UpdateEmailActivity.this,UpdatePwdActivity.class);
            startActivity(intent);
            finish();
        }else if(id==R.id.menu_update_delete_profile)
        {
            Intent intent =new Intent(UpdateEmailActivity.this,DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.menu_update_log_out)
        {
            authProfile.signOut();
            Toast.makeText(UpdateEmailActivity.this,"You are logged out!",Toast.LENGTH_LONG).show();
            Intent intent=new Intent(UpdateEmailActivity.this,MainActivity.class);

            // clear stack to prevent user coming back to this activity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(UpdateEmailActivity.this,"Something went wrong!",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}