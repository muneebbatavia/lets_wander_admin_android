package com.muneeb.letswanderadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.muneeb.letswanderadmin.Constants;

public class EditActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextLatitude, editTextLongitude, editTextDescription;
    private Switch switchIsStar;
    private Button submitButton;

    private String id;

    private addLocation originalLocationData;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getString("id");
            Log.d("EditActivity", "Received id from intent: " + id);
        } else {
            Log.e("EditActivity", "Intent extras are null");
        }

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextLatitude = findViewById(R.id.editTextLatitude);
        editTextLongitude = findViewById(R.id.editTextLongitude);
        editTextDescription = findViewById(R.id.editTextDescription);
        switchIsStar = findViewById(R.id.switchIsStar);
        submitButton = findViewById(R.id.submitButton);

        setSixDigitDecimalInputFilter(editTextLatitude);
        setSixDigitDecimalInputFilter(editTextLongitude);


        DatabaseReference locationRef = Constants.databaseReference().child("Markers").child(id);
        locationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    originalLocationData = dataSnapshot.getValue(addLocation.class);

                    editTextTitle.setText(originalLocationData.getTitle());
                    editTextLatitude.setText(String.valueOf(originalLocationData.getLatitude()));
                    editTextLongitude.setText(String.valueOf(originalLocationData.getLongitude()));
                    editTextDescription.setText(originalLocationData.getDescription());
                    Boolean starValue = originalLocationData.getStar();
                    if (starValue != null) {
                        switchIsStar.setChecked(starValue.booleanValue());
                    } else {
                        switchIsStar.setChecked(false);
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(EditActivity.this, "Error Fetching Data", Toast.LENGTH_SHORT).show();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String title = editTextTitle.getText().toString().trim();
                String latitudeStr = editTextLatitude.getText().toString().trim();
                String longitudeStr = editTextLongitude.getText().toString().trim();
                String description = editTextDescription.getText().toString().trim();
                boolean star = switchIsStar.isChecked(); // Get the state of the switch

                if (originalLocationData != null
                        && title.equals(originalLocationData.getTitle())
                        && latitudeStr.equals(String.valueOf(originalLocationData.getLatitude()))
                        && longitudeStr.equals(String.valueOf(originalLocationData.getLongitude()))
                        && description.equals(originalLocationData.getDescription())
                        && (star == originalLocationData.getStar().booleanValue())) {
                    progressDialog.dismiss();
                    Toast.makeText(EditActivity.this, "No changes made", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditActivity.this, ViewActivity.class));
                } else {
                    if (title.isEmpty() || latitudeStr.isEmpty() || longitudeStr.isEmpty() || description.isEmpty()) {
                        progressDialog.dismiss();
                        Toast.makeText(EditActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            double latitude = Double.parseDouble(latitudeStr);
                            double longitude = Double.parseDouble(longitudeStr);

                            DatabaseReference locationRef = Constants.databaseReference().child("Markers").child(id);

                            locationRef.child("title").setValue(title);
                            locationRef.child("latitude").setValue(latitude);
                            locationRef.child("longitude").setValue(longitude);
                            locationRef.child("description").setValue(description);
                            locationRef.child("star").setValue(star); // Update the "isStar" field in Firebase
                            progressDialog.dismiss();

                            Toast.makeText(EditActivity.this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditActivity.this, ViewActivity.class));
                        } catch (NumberFormatException e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditActivity.this, "Invalid latitude or longitude format", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    public void setSixDigitDecimalInputFilter(EditText editText) {
        final String decimalPattern = "^-?[0-9]{0,6}(\\.[0-9]{0,6})?$";

        InputFilter inputFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String newText = dest.toString().substring(0, dstart) +
                        source.toString() +
                        dest.toString().substring(dend);
                if (newText.matches(decimalPattern)) {
                    return null; // Accept the input
                } else {
                    return ""; // Reject the input
                }
            }
        };

        editText.setFilters(new InputFilter[]{inputFilter});
    }

}
