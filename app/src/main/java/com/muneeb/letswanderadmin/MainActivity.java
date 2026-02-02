package com.muneeb.letswanderadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void switchToViewActivity(View view)
    {
        startActivity(new Intent(this, ViewActivity.class));
    }

    public void switchToAddNewActivity(View view)
    {
        startActivity(new Intent(this, AddNewActivity.class));
    }
}
