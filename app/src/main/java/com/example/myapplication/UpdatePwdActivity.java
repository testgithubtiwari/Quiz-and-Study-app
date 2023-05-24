package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.usb.UsbRequest;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
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

public class UpdatePwdActivity extends AppCompatActivity {

    TextView head;
    private FirebaseAuth authProfile;
    private EditText editTextcurrpwd,editTextnewpwd,editTextconpwd;

    private TextView authenticated;
    private Button buttonauthenticate,buttonsavepwd;
    private ProgressBar progressBar;
    private String currPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pwd);

        getSupportActionBar().setTitle("Update Password Page");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        head=findViewById(R.id.textview_update_pwd_head);
        Typeface typeface= ResourcesCompat.getFont(this,R.font.blacklist);
        head.setTypeface(typeface);


        editTextcurrpwd=findViewById(R.id.textview_update_pwd_old);
        editTextnewpwd=findViewById(R.id.edittext_update_pwd_new);
        editTextconpwd=findViewById(R.id.edittext_update_pwd_confirm);
        authenticated=findViewById(R.id.textview_update_pwd_authenticate);
        buttonauthenticate=findViewById(R.id.button_update_pwd_authneticate);
        buttonsavepwd=findViewById(R.id.button_update_pwd);
        progressBar=findViewById(R.id.progressbar_update_RL1);


        editTextnewpwd.setEnabled(false);
        editTextconpwd.setEnabled(false);
        buttonsavepwd.setEnabled(false);

        authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=authProfile.getCurrentUser();

        if(firebaseUser.equals(""))
        {
            Toast.makeText(UpdatePwdActivity.this,"Something went wrong! User details not available ",Toast.LENGTH_LONG).show();
            Intent intent=new Intent(UpdatePwdActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish();
        }else {
            reauthenticateuser(firebaseUser);
        }

    }

    private void reauthenticateuser(FirebaseUser firebaseUser) {
        buttonauthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currPwd=editTextcurrpwd.getText().toString();

                if(TextUtils.isEmpty(currPwd))
                {
                    Toast.makeText(UpdatePwdActivity.this,"Enter your current password!",Toast.LENGTH_LONG).show();
                    editTextcurrpwd.setError("Please enter your current password to authenticate");
                    editTextcurrpwd.requestFocus();
                }else {
                    progressBar.setVisibility(View.VISIBLE);


                    AuthCredential credential= EmailAuthProvider.getCredential(firebaseUser.getEmail(),currPwd);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                progressBar.setVisibility(View.GONE);

                                editTextcurrpwd.setEnabled(false);
                                editTextconpwd.setEnabled(true);
                                editTextnewpwd.setEnabled(true);


                                buttonauthenticate.setEnabled(false);
                                buttonsavepwd.setEnabled(true);


                                authenticated.setText("You are authenticted! Now you can change your password");

                                Toast.makeText(UpdatePwdActivity.this,"Password has been verified! Now you can generate new password",Toast.LENGTH_LONG).show();

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    buttonsavepwd.setBackgroundTintList(ContextCompat.getColorStateList(UpdatePwdActivity.this,R.color.dark_green));
                                }

                                buttonsavepwd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        changePwd(firebaseUser);
                                    }
                                });


                            }
                            else {
                                try {
                                    throw  task.getException();
                                }catch (Exception e){
                                    Toast.makeText(UpdatePwdActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void changePwd(FirebaseUser firebaseUser) {
        String userPwdnew=editTextnewpwd.getText().toString();
        String userConpwd=editTextconpwd.getText().toString();

        if(TextUtils.isEmpty(userPwdnew))
        {
            Toast.makeText(UpdatePwdActivity.this,"You have not entered any new Password",Toast.LENGTH_LONG).show();
            editTextnewpwd.setError("Enter new password!");
            editTextnewpwd.requestFocus();
        }else if(TextUtils.isEmpty(userConpwd))
        {
            Toast.makeText(UpdatePwdActivity.this,"You have not confirm your Password",Toast.LENGTH_LONG).show();
            editTextconpwd.setError("Enter confirm password!");
            editTextconpwd.requestFocus();
        }else if(!userPwdnew.matches(userConpwd))
        {
            Toast.makeText(UpdatePwdActivity.this,"Password not match with the new one",Toast.LENGTH_LONG).show();
            editTextconpwd.setError("Again enter confirm password!");
            editTextconpwd.requestFocus();
        }else if(currPwd.matches(userPwdnew))
        {
            Toast.makeText(UpdatePwdActivity.this,"Your old password matches with new one!",Toast.LENGTH_LONG).show();
            editTextnewpwd.setError("Re-enter your password");
            editTextnewpwd.requestFocus();
        }else {
            progressBar.setVisibility(View.VISIBLE);

            firebaseUser.updatePassword(userPwdnew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(UpdatePwdActivity.this,"Your new password has been updated",Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(UpdatePwdActivity.this,UserProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        try {
                            throw task.getException();
                        }catch (Exception e){
                            Toast.makeText(UpdatePwdActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }

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
        if(id==android.R.id.home)
        {
            NavUtils.navigateUpFromSameTask(UpdatePwdActivity.this);
        }

        if(id==R.id.menu_refresh)
        {
            // refresh activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        }
        else if(id==R.id.menu_update_profile)
        {
            Intent intent =new Intent(UpdatePwdActivity.this,UpdateProfileActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.menu_update_email)
        {
            Intent intent =new Intent(UpdatePwdActivity.this,UpdateEmailActivity.class);
            startActivity(intent);
        }else if(id==R.id.menu_update_pwd)
        {
            Intent intent =new Intent(UpdatePwdActivity.this,UpdatePwdActivity.class);
            startActivity(intent);
            finish();
        }else if(id==R.id.menu_update_delete_profile)
        {
            Intent intent =new Intent(UpdatePwdActivity.this,DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.menu_update_log_out)
        {
            authProfile.signOut();
            Toast.makeText(UpdatePwdActivity.this,"You are logged out!",Toast.LENGTH_LONG).show();
            Intent intent=new Intent(UpdatePwdActivity.this,MainActivity.class);

            // clear stack to prevent user coming back to this activity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(UpdatePwdActivity.this,"Something went wrong!",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}