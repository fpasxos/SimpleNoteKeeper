package com.simplenotekeeper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.SecureRandom;
import java.util.HashMap;

public class PasswordSet extends AppCompatActivity {

    private static final String lockCode = "LOCK_CODE";
    private static String PASSWORD = "PASSWORD";
    private static String SAVED_PASSWORD = "HAS_SAVED_PASSWORD";
    private static boolean hasSavedPassword = false;

    private EditText editTextSetPassword;
    private EditText editTextConfirmPassword;
    private Button btSetPassword;

    public static final String ALIAS = "MYSIMPLEALIAS";

    public static final String SHARED_PREFS = "sharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getPassword()) {

            Intent intent = new Intent(this, PasswordEnter.class);
            finish();
            startActivity(intent);
        } else {
            setContentView(R.layout.activity_password_set);

            editTextSetPassword = findViewById(R.id.etPassword);
            editTextConfirmPassword = findViewById(R.id.edPasswordConfirm);
            btSetPassword = findViewById(R.id.btSetPassword);

            btSetPassword.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    setPassword();

                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setPassword() {

        if (editTextSetPassword.getText().toString().equals(editTextConfirmPassword.getText().toString()) &&
                !(editTextConfirmPassword.getText().toString().trim()).isEmpty()) {

            String password = editTextConfirmPassword.getText().toString();

            HashMap<String, byte[]> map = Encryption.encryptBytes(password.getBytes(), password);

            SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).edit();

            Gson gson = new Gson();
            String json = gson.toJson(map);
            editor.putString("myMap", json);
            editor.putBoolean(SAVED_PASSWORD, true);
            editor.apply();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            finish();
            startActivity(intent);

        } else if((editTextSetPassword.getText().toString().trim()).isEmpty() && editTextConfirmPassword.getText().toString().isEmpty()){

            Toast.makeText(this, "Password can not be empty!", Toast.LENGTH_SHORT).show();

        }
        else {

            Toast.makeText(this, "Passwords do not match!!", Toast.LENGTH_SHORT).show();

        }
    }

    private boolean getPassword() {

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        hasSavedPassword = sharedPreferences.getBoolean(SAVED_PASSWORD, false);

        return hasSavedPassword;
    }

    public static void secureWipeFile(File file) throws IOException {
        if (file != null && file.exists()) {
            final long length = file.length();
            final SecureRandom random = new SecureRandom();
            final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rws");
            randomAccessFile.seek(0);
            randomAccessFile.getFilePointer();
            byte[] data = new byte[64];
            int position = 0;
            while (position < length) {
                random.nextBytes(data);
                randomAccessFile.write(data);
                position += data.length;
            }
            randomAccessFile.close();
            file.delete();
        }
    }

}