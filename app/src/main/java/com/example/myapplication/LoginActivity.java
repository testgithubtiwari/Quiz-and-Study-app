package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity {

    private EditText edittextloginemail,edittextloginpwd;
    private TextView forgotpwd;
    private FirebaseAuth authprofile;
    private static final  String TAG="LoginActivity";
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("LOGIN PAGE");


        edittextloginemail=findViewById(R.id.edittext_login_full_email);
        edittextloginpwd=findViewById(R.id.edittext_login_full_password);
        pd=new ProgressDialog(this);


        authprofile= FirebaseAuth.getInstance();

        forgotpwd=findViewById(R.id.button_forgot_password);
        forgotpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this,"You can reset your password!",Toast.LENGTH_LONG).show();
                startActivity(new Intent(LoginActivity.this,UpdatePasswordActivity.class));
            }
        });


        Button buttonLogin=findViewById(R.id.Loginpage_button);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textemail=edittextloginemail.getText().toString();
                String textpwd=edittextloginpwd.getText().toString();

                if(TextUtils.isEmpty(textemail))
                {
                    Toast.makeText(LoginActivity.this,"Please enter your registered e-mail",Toast.LENGTH_LONG).show();
                    edittextloginemail.setError("E-mail is required");
                    edittextloginemail.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(textemail).matches())
                {
                    Toast.makeText(LoginActivity.this,"Entered e-mail is not valid",Toast.LENGTH_LONG).show();
                    edittextloginemail.setError("Valid e-mail is required");
                    edittextloginemail.requestFocus();
                }else if(TextUtils.isEmpty(textpwd))
                {
                    Toast.makeText(LoginActivity.this,"Please enter your registered password",Toast.LENGTH_LONG).show();
                    edittextloginpwd.setError("Password is required");
                    edittextloginpwd.requestFocus();
                }else
                {
                    loginUser(textemail,textpwd);
                }
            }
        });
    }

    private void loginUser(String email, String pwd) {
        pd.setMessage("Loading...");
        pd.show();
        authprofile.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {

                    // get the instance of the currrent user
                    FirebaseUser firebaseUser = authprofile.getCurrentUser();
                    pd.dismiss();
                    // check if email is verified before the user get into user profile
                    Toast.makeText(LoginActivity.this,"You have successfully Loged in",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LoginActivity.this,HomePage.class));
                    finish();



                }else {
                    pd.dismiss();

                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        edittextloginemail.setError("User doesn't exist or no longer valid. Please register again");
                        edittextloginemail.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        edittextloginpwd.setError("Invalid credentials. kindly check and re-enter");
                        edittextloginpwd.requestFocus();
                    }catch (Exception e){
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }

                }

            }
        });
    }

    private void showAlertDialog() {
        // setup the alert builder
        AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("E-mail not verified");
        builder.setMessage("Please verify your mail, without verification you can't log in");

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(authprofile.getCurrentUser()!=null)
        {
            Toast.makeText(LoginActivity.this,"You are already Logged In!",Toast.LENGTH_LONG).show();

            // userprofile start screen
            startActivity(new Intent(LoginActivity.this,HomePage.class));
            finish();

        }else {
            Toast.makeText(LoginActivity.this, "You can Log In now!",Toast.LENGTH_LONG).show();
        }
    }
}