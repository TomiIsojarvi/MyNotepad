package com.example.mynotepad2;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Database
{
    private DatabaseReference databaseReference;

    public Database() {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://mynotepad2-cfc48-default-rtdb.europe-west1.firebasedatabase.app");
        databaseReference = db.getReference("");
    }

    public Task<Void> add(Note note, String uid) {
        return databaseReference.child("users").child(uid).child("notes").push().setValue(note);
    }
}
