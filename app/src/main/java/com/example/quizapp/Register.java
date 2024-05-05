package com.example.quizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Register extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://formal-ember-394310-default-rtdb.firebaseio.com/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final TextView Login = findViewById(R.id.registerLogin);
        final TextView firstName = findViewById(R.id.registerFirstName);
        final TextView lastName = findViewById(R.id.registerLastName);
        final TextView email = findViewById(R.id.registerEmail);
        final TextView tel = findViewById(R.id.registerPhoneNum);
        final TextView password = findViewById(R.id.registerPassword);
        final TextView cPassword = findViewById(R.id.registerCPassword);
        final TextView button = findViewById(R.id.registerSubmit);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "switched to login", Toast.LENGTH_SHORT).show();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user=new User();
                user.firstName = firstName.getText().toString();
                user.lastName = lastName.getText().toString();
                user.email = email.getText().toString();
                user.phoneNum = tel.getText().toString();
                user.password = password.getText().toString();
                String cPasswordValue = cPassword.getText().toString();
                registerAction(user,cPasswordValue);
            }
        });



    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(Register.this, QuizMenu.class);
            startActivity(intent);
        }
    }
    private void registerAction(User user, String cPasswordValue){
        if(user.firstName.isEmpty() || user.lastName.isEmpty() || user.email.isEmpty() || user.phoneNum.isEmpty() || user.password.isEmpty() || cPasswordValue.isEmpty()){
            Toast.makeText(getApplicationContext(),"Fill in all fields" , Toast.LENGTH_SHORT).show();
        }
        if(!user.password.equals(cPasswordValue)){
            Toast.makeText(getApplicationContext(),"Fill in all fields" , Toast.LENGTH_SHORT).show();
        }
        else{
            writeData(user);

        }
    }
    private void writeData(User user) {
        String encodedEmail = user.email.replace(".", ",");
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("User").child(encodedEmail);
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(Register.this, "Email already Exists", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.email, user.password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        try {
                                            userReference.child("firstName").setValue(user.firstName);
                                            userReference.child("lastName").setValue(user.lastName);
                                            userReference.child("email").setValue(user.email);
                                            userReference.child("tel").setValue(user.phoneNum);
                                            userReference.child("password").setValue(hashPassword(user.password));
                                            Toast.makeText(getApplicationContext(), "User Created ;)", Toast.LENGTH_SHORT).show();
                                            finish();
                                            Intent intent = new Intent(Register.this, QuizMenu.class);
                                            startActivity(intent);
                                        } catch (NoSuchAlgorithmException e) {
                                            Toast.makeText(Register.this, "Error !!!!", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // User registration failed
                                        Toast.makeText(Register.this, "User registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Register.this, "Error !!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static String hashPassword(String password) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.reset();
        md.update(password.getBytes());
        byte[] mdArray = md.digest();
        StringBuilder sb = new StringBuilder(mdArray.length * 2);
        for(byte b : mdArray) {
            int v = b & 0xff;
            if(v < 16)
                sb.append('0');
            sb.append(Integer.toHexString(v));
        }

        return sb.toString();
    }

}