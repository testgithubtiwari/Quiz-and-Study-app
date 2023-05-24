package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Class3 extends AppCompatActivity {
    TextView link1, link2, link3,quiz1,quiz2,quiz3;
    private TextView youtube;
    private ImageView youtube1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class3);

        getSupportActionBar().setTitle("Class 3rd");

        youtube=findViewById(R.id.textview_links);
        Typeface typeface= ResourcesCompat.getFont(this,R.font.blacklist);
        youtube.setTypeface(typeface);

        youtube1=findViewById(R.id.imageview_youtube);
        Animation anim= AnimationUtils.loadAnimation(this,R.anim.myanim);
        youtube1.setAnimation(anim);

        link1 = findViewById(R.id.textview_link1);
        link2 = findViewById(R.id.textview_link2);
        link3 = findViewById(R.id.textview_link3);


        quiz1=findViewById(R.id.textview_quiz1);
        quiz2=findViewById(R.id.textview_quiz2);
        quiz3=findViewById(R.id.textview_quiz3);



        // Set OnClickListener for link1 TextView
        link1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                link1.setTextColor(getResources().getColor(R.color.clickedcolour));
                openLink("https://www.youtube.com/watch?v=GOoiRv1Gn68");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        link1.setTextColor(getResources().getColor(R.color.black));
                    }
                }, 1000);
            }
        });


        link2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                link2.setTextColor(getResources().getColor(R.color.clickedcolour));
                openLink("https://www.youtube.com/watch?v=GOoiRv1Gn68");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        link2.setTextColor(getResources().getColor(R.color.black));
                    }
                }, 1000);
            }
        });


        link3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                link3.setTextColor(getResources().getColor(R.color.clickedcolour));
                openLink("https://www.youtube.com/watch?v=GOoiRv1Gn68");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        link3.setTextColor(getResources().getColor(R.color.black));
                    }
                }, 1000);
            }
        });

        quiz1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quiz1.setTextColor(getResources().getColor(R.color.clickedcolour));
                openLink("https://www.helpteaching.com/questions/Physics/Grade_3");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        quiz1.setTextColor(getResources().getColor(R.color.black));
                    }
                }, 1000);
            }
        });

        quiz2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quiz2.setTextColor(getResources().getColor(R.color.clickedcolour));
                openLink("https://www.helpteaching.com/questions/Physics/Grade_3?pageNum=2");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        quiz2.setTextColor(getResources().getColor(R.color.black));
                    }
                }, 1000);
            }
        });

        quiz3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quiz3.setTextColor(getResources().getColor(R.color.clickedcolour));
                openLink("https://www.helpteaching.com/questions/Physics/Grade_3?pageNum=3");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        quiz3.setTextColor(getResources().getColor(R.color.black));
                    }
                }, 1000);
            }
        });
    }

    public void openLink(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }
}