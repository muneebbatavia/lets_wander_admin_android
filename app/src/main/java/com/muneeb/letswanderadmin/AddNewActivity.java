package com.muneeb.letswanderadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

public class AddNewActivity extends AppCompatActivity {

    Button submitButton;
    ProgressDialog progressDialog;
    EditText latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        submitButton = findViewById(R.id.submitButton);

        latitude = findViewById(R.id.editTextLatitude);
        longitude = findViewById(R.id.editTextLongitude);

        setSixDigitDecimalInputFilter(latitude);
        setSixDigitDecimalInputFilter(longitude);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = ((EditText) findViewById(R.id.editTextTitle)).getText().toString();
                String latitudeStr = latitude.getText().toString();
                String longitudeStr = longitude.getText().toString();
                String description = ((EditText) findViewById(R.id.editTextDescription)).getText().toString();
                boolean isStar = ((Switch) findViewById(R.id.switchIsStar)).isChecked();

                if (title.isEmpty() || latitudeStr.isEmpty() || longitudeStr.isEmpty() || description.isEmpty()) {
                    Toast.makeText(AddNewActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    try {
                        double latitude = Double.parseDouble(latitudeStr);
                        double longitude = Double.parseDouble(longitudeStr);
                        DatabaseReference markersRef = Constants.databaseReference().child("Markers");

                        DatabaseReference newLocationRef = markersRef.push();
                        String locationKey = newLocationRef.getKey();
                        addLocation location = new addLocation(locationKey, latitude, longitude, title, description, isStar);

                        newLocationRef.setValue(location);

                        clearInputFields();
                        progressDialog.dismiss();

                        Toast.makeText(AddNewActivity.this, "Data submitted successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddNewActivity.this, ViewActivity.class));
                    } catch (NumberFormatException e) {
                        progressDialog.dismiss();
                        // Handle invalid latitude or longitude format
                        Toast.makeText(AddNewActivity.this, "Invalid latitude or longitude format", Toast.LENGTH_SHORT).show();
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

    private void clearInputFields() {
        EditText editTextTitle = findViewById(R.id.editTextTitle);
        EditText editTextLatitude = findViewById(R.id.editTextLatitude);
        EditText editTextLongitude = findViewById(R.id.editTextLongitude);
        EditText editTextDescription = findViewById(R.id.editTextDescription);
        Switch switchIsStar = findViewById(R.id.switchIsStar);

        editTextTitle.setText("");
        editTextLatitude.setText("");
        editTextLongitude.setText("");
        editTextDescription.setText("");
        switchIsStar.setChecked(false);
    }
}
