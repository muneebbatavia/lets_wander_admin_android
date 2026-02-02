package com.muneeb.letswanderadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewActivity extends AppCompatActivity {
    List<addLocation> locationList = new ArrayList<>();
    DatabaseReference locationRef = Constants.databaseReference().child("Markers");
    private String id;
    ProgressDialog progressDialog;

    LocationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
       // Constants.checkApp(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        RecyclerView recyclerView = findViewById(R.id.recyclerViewLocations);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(false);

        ExtendedFloatingActionButton add = findViewById(R.id.add);

        add.setOnClickListener(v -> startActivity(new Intent(this, AddNewActivity.class)));
        EditText et = findViewById(R.id.search);

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        locationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                locationList.clear(); // Clear the list to avoid duplicate data

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    addLocation location = dataSnapshot.getValue(addLocation.class);
                    if (location != null) {
                        location.setId(dataSnapshot.getKey());
                        locationList.add(location);
                    }
                }
                locationList.add(new addLocation());
                adapter = new LocationAdapter(locationList, ViewActivity.this);
                recyclerView.setAdapter(adapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(ViewActivity.this, "Failed to retrieve data from Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
