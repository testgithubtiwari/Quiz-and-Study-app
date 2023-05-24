package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DeleteProfileActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    TextView head;
    private TextView authenticated;
    private EditText editTextPwd;
    private Button buttonAuth,buttonDeleteProfile;
    private ProgressBar progressBar;
    private String Pwd;
    private static final String TAG="DeleteProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_profile);

        getSupportActionBar().setTitle("Delete Profile Screen");

        head=findViewById(R.id.textview_delete_head);
        Typeface typeface= ResourcesCompat.getFont(this,R.font.blacklist);
        head.setTypeface(typeface);

        editTextPwd=findViewById(R.id.textview_update_pwd_old);
        buttonAuth=findViewById(R.id.button_update_pwd_authneticate);
        buttonDeleteProfile=findViewById(R.id.button_update_pwd);
        progressBar=findViewById(R.id.progressbar_update_RL1);
        authenticated=findViewById(R.id.textview_delete_profile_authenticate);

        buttonDeleteProfile.setEnabled(false);

        authProfile=FirebaseAuth.getInstance();
        firebaseUser=authProfile.getCurrentUser();

        if(firebaseUser.equals(""))
        {
            Toast.makeText(DeleteProfileActivity.this,"Something went wrong! User details not available ",Toast.LENGTH_LONG).show();
            Intent intent=new Intent(DeleteProfileActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish();
        }else {
            reauthenticateuser(firebaseUser);
        }

    }

    private void reauthenticateuser(FirebaseUser firebaseUser) {
        buttonAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pwd=editTextPwd.getText().toString();

                if(TextUtils.isEmpty(Pwd))
                {
                    Toast.makeText(DeleteProfileActivity.this,"Enter your password!",Toast.LENGTH_LONG).show();
                    editTextPwd.setError("Please enter your password to authenticate");
                    editTextPwd.requestFocus();
                }else {
                    progressBar.setVisibility(View.VISIBLE);


                    AuthCredential credential= EmailAuthProvider.getCredential(firebaseUser.getEmail(),Pwd);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                progressBar.setVisibility(View.GONE);


                                editTextPwd.setEnabled(false);
                                buttonAuth.setEnabled(false);
                                buttonDeleteProfile.setEnabled(true);


                                authenticated.setText("You are authenticted! Now you can Delete your Profile");
                                Typeface typeface1= ResourcesCompat.getFont(DeleteProfileActivity.this,R.font.blacklist);
                                authenticated.setTypeface(typeface1);
                                Toast.makeText(DeleteProfileActivity.this,"Password has been verified! Now you can Delete your profile",Toast.LENGTH_LONG).show();

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    buttonDeleteProfile.setBackgroundTintList(ContextCompat.getColorStateList(DeleteProfileActivity.this,R.color.PrimaryDark));
                                }

                                buttonDeleteProfile.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showAlertDialog();
                                    }
                                });


                            }
                            else {
                                try {
                                    throw  task.getException();
                                }catch (Exception e){
                                    Toast.makeText(DeleteProfileActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void showAlertDialog() {

        AlertDialog.Builder builder=new AlertDialog.Builder(DeleteProfileActivity.this);
        builder.setTitle("Delete User and Related Data?");
        builder.setMessage("Are you sure to delete User and it's Related Data? This action is irreversible!");

        // open the email app for verification
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 deleteUser(firebaseUser);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(DeleteProfileActivity.this,UserProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // create the alert dialog box

        AlertDialog alertDialog= builder.create();

        // change the button color of continue ;
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
            }
        });

        //show the alertdialog
        alertDialog.show();
    }

    private void deleteUser(FirebaseUser firebaseUser) {

        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    deleteUserData();
                    authProfile.signOut();
                    Toast.makeText(DeleteProfileActivity.this,"User has been deleted!",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(DeleteProfileActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    try {
                        throw task.getException();
                    }catch (Exception e){
                        Toast.makeText(DeleteProfileActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void deleteUserData() {
        // delete the profile pic
        FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();
        StorageReference storageReference=firebaseStorage.getReferenceFromUrl(firebaseUser.getPhotoUrl().toString());
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG,"OnSuccess: Photo Deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,e.getMessage());
                Toast.makeText(DeleteProfileActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        // delete data from relatime database;
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Registered Users");
        databaseReference.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG,"OnSuccess: User Data Deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,e.getMessage());
                Toast.makeText(DeleteProfileActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
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
            Intent intent =new Intent(DeleteProfileActivity.this,UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.menu_update_email)
        {
            Intent intent =new Intent(DeleteProfileActivity.this,UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        }else if(id==R.id.menu_update_pwd)
        {
            Intent intent =new Intent(DeleteProfileActivity.this,UpdatePwdActivity.class);
            startActivity(intent);
            finish();
        }else if(id==R.id.menu_update_delete_profile)
        {
            Intent intent =new Intent(DeleteProfileActivity.this,DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.menu_update_log_out)
        {
            authProfile.signOut();
            Toast.makeText(DeleteProfileActivity.this,"You are logged out!",Toast.LENGTH_LONG).show();
            Intent intent=new Intent(DeleteProfileActivity.this,MainActivity.class);

            // clear stack to prevent user coming back to this activity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(DeleteProfileActivity.this,"Something went wrong!",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}