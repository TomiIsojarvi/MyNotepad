package com.example.mynotepad2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.example.mynotepad2.LoginActivity;
import com.example.mynotepad2.R;

public class MainActivity2 extends AppCompatActivity {

    Button btnLogOut;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        btnLogOut = findViewById(R.id.btnLogout);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(MainActivity2.this, LoginActivity.class));
        } else {
            startActivity(new Intent(MainActivity2.this, MainActivity.class));
        }

        btnLogOut.setOnClickListener(view -> {
            auth.signOut();
            startActivity(new Intent(MainActivity2.this, LoginActivity.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(MainActivity2.this, LoginActivity.class));
        }
    }

    @Override
    protected void onStop() {
        // call the superclass method first
        super.onStop();

        FirebaseAuth.getInstance().signOut();
    }

        @Override
    protected void onDestroy() {
        super.onDestroy();

        FirebaseAuth.getInstance().signOut();
    }
}