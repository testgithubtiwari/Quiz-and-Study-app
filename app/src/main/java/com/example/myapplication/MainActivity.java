package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView welcome;
    private TextView account;
    private String texttotype="Welcome to this App ";
    private Handler mHandler;
    private Runnable mTypeRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        welcome=findViewById(R.id.text_main_head);
        Typeface typeface= ResourcesCompat.getFont(this,R.font.blacklist);
        welcome.setTypeface(typeface);

        account=findViewById(R.id.text_notaccount);
        Typeface typeface1= ResourcesCompat.getFont(this,R.font.blacklist);
        account.setTypeface(typeface1);

        welcome.setText("");
        mHandler=new Handler();

        mTypeRunnable = new Runnable() {
            @Override
            public void run() {
                if (texttotype.length() > 0) {
                    welcome.setText(welcome.getText().toString() + texttotype.charAt(0));
                    texttotype = texttotype.substring(1);
                    mHandler.postDelayed(mTypeRunnable, 70); // Change the delay time to adjust typing speed
                }
            }
        };
        mHandler.postDelayed(mTypeRunnable, 1000);

        getSupportActionBar().setTitle("Study and quiz app");
        Button login=findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        Button Register=findViewById(R.id.register_button);
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}