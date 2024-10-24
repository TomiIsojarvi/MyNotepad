package com.example.mynotepad2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
//Firebase
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Firebase related member objects
    DatabaseReference databaseRef;
    Database database;
    String uid;

    // Buttons and widgets
    Button buttonSend;
    EditText editNotes;
    RecyclerView noteRecycler;
    LinearLayoutManager linearLayoutManager;

    // Array list for storing note-data
    ArrayList<Note> noteList;

    // Adapter for RecyclerView
    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configure firebase
        database = new Database();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseRef = FirebaseDatabase.getInstance("https://mynotepad2-cfc48-default-rtdb.europe-west1.firebasedatabase.app").getReference("")
                .child("users").child(uid).child("notes");

        // Button Views
        buttonSend = findViewById(R.id.SendBtn);
        editNotes = findViewById(R.id.EditNotes);

        // RecyclerView
        noteRecycler = findViewById(R.id.NoteRecycler);
        noteRecycler.setHasFixedSize(true);
        noteRecycler.setLayoutManager(new LinearLayoutManager(this));

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        // ArrayList
        noteList = new ArrayList();

        // Adapter for recycler
        myAdapter = new MyAdapter(this, noteList);
        noteRecycler.setAdapter(myAdapter);

        // Create a new note
        buttonSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String text = editNotes.getText().toString();

                // Check if the EditText is empty.
                if (!text.matches("")) {
                    noteList.clear();

                    // Get time
                    String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                            .format(new Date());
                    String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                            .format(new Date());

                    Note note = new Note(text, currentDate, currentTime);

                    database.add(note, uid);
                }

                hideKeyboard();
                editNotes.setText("");

            }
        });

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Note note = dataSnapshot.getValue(Note.class);
                    noteList.add(note);
                }
                myAdapter.notifyDataSetChanged();
                noteRecycler.scrollToPosition(noteList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itemLogOut) {
                FirebaseAuth.getInstance().signOut();
                finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public  void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();

        if (view == null) {
            view = new View(this);
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}

