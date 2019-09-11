package com.simplenotekeeper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

public class PasswordEnter extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
    private static String PASSWORD = "PASSWORD";
    private static String SAVED_PASSWORD = "HAS_SAVED_PASSWORD";

    private EditText editTextPassword;
    private Button buttonUnlock;
    private byte[] ivs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_enter);

        editTextPassword = findViewById(R.id.edit_text_password);
        buttonUnlock = findViewById(R.id.buttonUnlock);

        buttonUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sharedPreferencesPassword = null;
                sharedPreferencesPassword = getPasswordFromSharedPrefs();

                if (!editTextPassword.getText().toString().isEmpty() && sharedPreferencesPassword.equals(editTextPassword.getText().toString())) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    finish();
                    startActivity(intent);
                } else {
                    Toast.makeText(PasswordEnter.this, "Your password is incorrect!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getPasswordFromSharedPrefs() {

        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = preferences.getString("myMap", "");
        java.lang.reflect.Type type = new TypeToken<HashMap<String, byte[]>>() {
        }.getType();
        HashMap<String, byte[]> obj = gson.fromJson(json, type);

        byte[] decrypted = Encryption.decryptData(obj, editTextPassword.getText().toString());
        String decryptedString = "";
        if (decrypted != null) {
            decryptedString = new String(decrypted);
        }

        return decryptedString;
    }

}