package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

import java.util.regex.Pattern;

public class UpdatePasswordActivity extends AppCompatActivity {


    private EditText edittextemailreset;
    private Button buttonreset;
    private ProgressDialog pd;
    private FirebaseAuth authProfile;

    private static final  String TAG="UpdatePasswordActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        getSupportActionBar().setTitle("Reset Password Page");
        edittextemailreset=findViewById(R.id.edit_text_email);
        buttonreset=findViewById(R.id.buttom_reset_password);
        pd=new ProgressDialog(this);

        buttonreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=edittextemailreset.getText().toString();

                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(UpdatePasswordActivity.this,"Please enter your email",Toast.LENGTH_LONG).show();
                    edittextemailreset.setError("Email is required");
                    edittextemailreset.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    Toast.makeText(UpdatePasswordActivity.this,"Please re-enter your email",Toast.LENGTH_LONG).show();
                    edittextemailreset.setError("Email format is not valid");
                    edittextemailreset.requestFocus();
                }else
                {
                    resetPassword(email);
                }
            }
        });
    }

    private void resetPassword(String email) {
        pd.setMessage("Getting new password");
        pd.show();
        authProfile=FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    pd.dismiss();
                    Toast.makeText(UpdatePasswordActivity.this,"Please check your email inbox to get link for password reset",Toast.LENGTH_LONG).show();

                    Intent intent=new Intent(UpdatePasswordActivity.this,MainActivity.class);

                    // clear stack to prevent user coming back to this activity
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }else
                {
                    pd.dismiss();
                    try{
                        throw task.getException();
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        edittextemailreset.setError("User doesn't exist or no longer exist. Please register again");

                    }catch (Exception e){
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(UpdatePasswordActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }


            }
        });
    }
}