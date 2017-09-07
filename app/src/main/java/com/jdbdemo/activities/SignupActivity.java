package com.jdbdemo.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jdbdemo.R;
import com.jdbdemo.pojo.User;
import com.jdbdemo.sqlite.UserDBHelper;
import com.jdbdemo.utils.SecurePreferences;

public class SignupActivity extends AppCompatActivity {

    EditText edt_reg_email, edt_reg_username, edt_reg_password, edt_reg_conpwd;
    TextView txt_done;
    UserDBHelper userDBHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        userDBHelper = new UserDBHelper(this);

        initView();
        setListner();
    }


    private void initView() {
        edt_reg_email = (EditText) findViewById(R.id.edt_reg_email);
        edt_reg_username = (EditText) findViewById(R.id.edt_reg_username);
        edt_reg_password = (EditText) findViewById(R.id.edt_reg_password);
        edt_reg_conpwd = (EditText) findViewById(R.id.edt_reg_conpwd);

        txt_done = (TextView) findViewById(R.id.txt_done);
    }

    private void setListner() {

        txt_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isValidEmail(edt_reg_email.getText().toString())) {
                    Toast.makeText(SignupActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                } else if (edt_reg_username.getText().toString().isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please Enter UserName", Toast.LENGTH_SHORT).show();
                } else if (edt_reg_password.getText().toString().isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please Enter  Password", Toast.LENGTH_SHORT).show();
                } else if (edt_reg_conpwd.getText().toString().isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please Enter Confirm Password", Toast.LENGTH_SHORT).show();
                } else if (!edt_reg_password.getText().toString().equals(edt_reg_conpwd.getText().toString())) {
                    Toast.makeText(SignupActivity.this, "Password not match", Toast.LENGTH_SHORT).show();
                } else {
                    SecurePreferences.savePreferences(SignupActivity.this, "userName", edt_reg_username.getText().toString());
                    storeToDatabase(edt_reg_email.getText().toString().trim(), edt_reg_username.getText().toString().trim(), edt_reg_password.getText().toString().trim(), edt_reg_conpwd.getText().toString().trim());
                }
            }
        });

    }

    private void storeToDatabase(String email, String name, String password, String conpasword) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(name);
        user.setPassword(password);
        user.setCon_password(conpasword);
        userDBHelper.addContact(user);
        navigateToLoginActivity();

    }

    private void navigateToLoginActivity() {
        Intent itoLogin = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(itoLogin);
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
