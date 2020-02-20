package com.example.androidlabs;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {

    SharedPreferences prefs = null;
    EditText typeField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        prefs = getSharedPreferences("Email", Context.MODE_PRIVATE);
        String savedString = prefs.getString("email", "");
        typeField = findViewById(R.id.email);
        typeField.setText(savedString);

        EditText profileEmail = findViewById(R.id.email);
        Button login = findViewById(R.id.login);
        login.setOnClickListener(click -> {
            Intent goToProfile = new Intent(MainActivity.this, ProfileActivity.class);
            goToProfile.putExtra("EMAIL", profileEmail.getText().toString());
            startActivity(goToProfile);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveSharedPrefs( typeField.getText().toString());
    }

    private void saveSharedPrefs(String stringToSave)
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("email", stringToSave);
        editor.commit();
    }
}
