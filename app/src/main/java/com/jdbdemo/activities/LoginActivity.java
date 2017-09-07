package com.jdbdemo.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jdbdemo.R;
import com.jdbdemo.pojo.User;
import com.jdbdemo.sqlite.UserDBHelper;
import com.jdbdemo.utils.SecurePreferences;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    EditText edt_email, edt_password;
    TextView txt_login, txt_register;
    UserDBHelper userDBHelper;
    private String TAG = getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (SecurePreferences.getBooleanPreference(LoginActivity.this, "isLogin")) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            userDBHelper = new UserDBHelper(this);
            initView();
            setListener();
        }

    }


    private void initView() {
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_password = (EditText) findViewById(R.id.edt_password);
        txt_login = (TextView) findViewById(R.id.txt_login);
        txt_register = (TextView) findViewById(R.id.txt_register);
    }

    private void setListener() {
        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isValidEmail(edt_email.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();

                } else if (edt_password.getText().toString().trim().equals("")) {
                    Toast.makeText(LoginActivity.this, "Password can not Empty", Toast.LENGTH_SHORT).show();
                } else {
                    checkEntryInDatabase(edt_email.getText().toString(), edt_password.getText().toString());
                }

            }
        });

        txt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(registerIntent);
            }
        });

    }

    private void checkEntryInDatabase(String email, String password) {
        List<User> contacts = userDBHelper.getAllContacts();
        if (contacts.size() > 0 && contacts != null) {
            for (int i = 0; i < contacts.size(); i++) {

                if (contacts.get(i).getEmail().toString().equals(email) && contacts.get(i).getPassword().equals(password)) {
                    Log.e(TAG, "contacts is-->" + contacts.get(i).getEmail());
                    Toast.makeText(LoginActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                    SecurePreferences.savePreferences(LoginActivity.this, "userID", contacts.get(i).getId());
                    SecurePreferences.savePreferences(LoginActivity.this, "isLogin", true);
                    redirectToMainActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "Record not found", Toast.LENGTH_SHORT).show();
                }


            }
        }
    }

    private void redirectToMainActivity() {
        Intent itoMainActivity = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(itoMainActivity);
        finish();
    }


    private boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


}
