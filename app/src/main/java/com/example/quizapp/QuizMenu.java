package com.example.quizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class QuizMenu extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_menu);
        final Button button = findViewById(R.id.logout);
        final View football = findViewById(R.id.Football);
        final View tvShow = findViewById(R.id.tv_show);
        final View it = findViewById(R.id.It);
        final View ai = findViewById(R.id.Ai);
        football.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuizMenu.this, Quiz1.class);
                intent.putExtra("Type", "Football");
                startActivity(intent);
            }
        });
        tvShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuizMenu.this, Quiz1.class);
                intent.putExtra("Type", "tvShow");
                startActivity(intent);
            }
        });
        it.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(QuizMenu.this, Quiz1.class);
//                intent.putExtra("Type", "It");
//                startActivity(intent);
                Toast.makeText(QuizMenu.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });
        ai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(QuizMenu.this, Quiz1.class);
//                intent.putExtra("Type", "Ai");
//                startActivity(intent);
                Toast.makeText(QuizMenu.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(QuizMenu.this, MainActivity.class));
                finish();
            }
        });



    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(QuizMenu.this, MainActivity.class);
            startActivity(intent);
        }
        else {
            final TextView textView = findViewById(R.id.email);
            textView.setText(currentUser.getEmail());
        }
    }
}