package com.example.quizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Quiz2 extends AppCompatActivity {
    private String correctReponse;
    CountDownTimer countDownTimer;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q2);
        final Button button = findViewById(R.id.button2);
        final RadioGroup radioGrp=findViewById(R.id.radioGrp);
        final Button submit=findViewById(R.id.submit);
        final TextView textCountdown = findViewById(R.id.text_countdown);
        Intent intent = getIntent();
        String type = intent.getStringExtra("Type");
        final int[] score = {intent.getIntExtra("score", 0)};
        if(type==null){
            Intent i = new Intent(getApplicationContext(), QuizMenu.class);
            startActivity(i);
        }

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child(type).child("Quiz2");

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String childKey = childSnapshot.getKey();
                        String childValue = childSnapshot.getValue(String.class);
                        switch (childKey) {
                            case "question":
                                TextView textView = findViewById(R.id.question);
                                textView.setText(childValue);
                                break;
                            case "reponseA":
                                RadioButton radioButton1 = findViewById(R.id.Choice1);
                                radioButton1.setText(childValue);
                                break;
                            case "reponseB":
                                RadioButton radioButton2 = findViewById(R.id.Choice2);
                                radioButton2.setText(childValue);
                                break;
                            case "reponseC":
                                RadioButton radioButton3 = findViewById(R.id.Choice3);
                                radioButton3.setText(childValue);
                                break;
                            case "reponseD":
                                RadioButton radioButton4 = findViewById(R.id.Choice4);
                                radioButton4.setText(childValue);
                                break;
                            case "image":
                                ImageView imageView = findViewById(R.id.imageQuiz1);
                                int resourceId = getResources().getIdentifier(childValue, "drawable", getPackageName());
                                imageView.setImageResource(resourceId);
                                break;
                            case "correct":
                                correctReponse = childValue;
                                break;
                            default:
                                // Handle unexpected keys if necessary
                                break;
                        }


                        Log.d("Firebase", "Key: " + childKey + ", Value: " + childValue);
                    }
                    countDownTimer=new CountDownTimer(30000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            // Mettez à jour le texte du compte à rebours à chaque tick
                            textCountdown.setText("Seconds remaining: " + millisUntilFinished / 1000);
                        }

                        public void onFinish() {
                            textCountdown.setText(correctReponse);
                            Intent intent = new Intent(Quiz2.this, Quiz3.class);
                            intent.putExtra("Type",type);
                            intent.putExtra("score", 0);
                            startActivity(intent);
                        }
                    }.start();

                } else {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idb = radioGrp.getCheckedRadioButtonId();
                if (idb != -1) {
                    RadioButton selectedRadioButton = findViewById(idb);
                    String selectedText = selectedRadioButton.getText().toString();

                    if (selectedText.equals(correctReponse)) {
                        Log.d("TAG", "onClick: "+score[0]);
                        score[0]++;
                    }
                    Intent intent = new Intent(Quiz2.this, Quiz3.class);
                    intent.putExtra("Type",type);
                    intent.putExtra("score", score[0]);
                    countDownTimer.cancel();
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Please select an option", Toast.LENGTH_SHORT).show();
                }

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(Quiz2.this, MainActivity.class);
            startActivity(intent);
        }
        else {
            final TextView textView = findViewById(R.id.email);
            textView.setText(currentUser.getEmail());
        }
    }
    private void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(Quiz2.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel the CountDownTimer
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}