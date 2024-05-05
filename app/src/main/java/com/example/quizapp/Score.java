package com.example.quizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Score extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        Intent intent = getIntent();
        int score = intent.getIntExtra("score",0);
        String type = intent.getStringExtra("Type");
        TextView textview = findViewById(R.id.textView3);
        textview.setText(score * 100 / 5 + "%");
        Toast.makeText(getApplicationContext(), "Score =" + score, Toast.LENGTH_SHORT).show();
        ImageView imageView = findViewById(R.id.image);
        int resourceId = getResources().getIdentifier("start_"+score, "drawable", getPackageName());
        imageView.setImageResource(resourceId);
        String currentUser = mAuth.getCurrentUser().getEmail();
        String encodedEmail = currentUser.replace(".", ",");

        //Had l code KaySave score basant sur email dyal user ila kan deja endo chi score kyt ecrasa
        DatabaseReference scoreReference = FirebaseDatabase.getInstance().getReference().child("Score");
        scoreReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("TAG", "onDataChange: "+score+"  "+type+"  "+encodedEmail);
                scoreReference.child(type).child(encodedEmail).setValue("Score : "+score);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Score.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Had l code kanjeb feh gae les Scores li kaynin f choosing Type o kanderhum f list View ;)
        ListView recyclerView = findViewById(R.id.recycle);
        List<String> dataList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        recyclerView.setAdapter(adapter);
        scoreReference.child(type).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot typeSnapshot : dataSnapshot.getChildren()) {
                    String userEmail = typeSnapshot.getKey(); // Get the user's email (encoded)
                    String scoreValue = typeSnapshot.getValue(String.class); // Get the score value
                    dataList.add("Email: " + userEmail + ", " + scoreValue);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Toast.makeText(Score.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        Button menu=findViewById(R.id.menu);
        Button tryAgain=findViewById(R.id.try_again);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 =new Intent(Score.this,QuizMenu.class);
                startActivity(intent1);
            }
        });
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 =new Intent(Score.this,Quiz1.class);
                intent1.putExtra("Type", type);
                startActivity(intent1);
            }
        });
    }

}
