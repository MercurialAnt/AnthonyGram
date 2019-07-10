package com.example.anthonygram;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class Signup extends AppCompatActivity {

    private EditText etHandle;
    private EditText etSignUser;
    private EditText etSignPass;
    private EditText etSignPass2;
    private Button btnCreateP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etHandle = findViewById(R.id.etHandle);
        etSignUser = findViewById(R.id.etSignUser);
        etSignPass = findViewById(R.id.etSignPass);
        etSignPass2 = findViewById(R.id.etSignPassCheck);
        btnCreateP = findViewById(R.id.btnCreateP);




        btnCreateP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password1 = etSignPass.getText().toString();
                String password2 = etSignPass2.getText().toString();
                String username = etSignUser.getText().toString();
                String handle = etHandle.getText().toString();

                ParseUser newUser = new ParseUser();

                if (checkUniqueUser(username) && samePassword(password1, password2)) {
                    newUser.setUsername(username);
                    newUser.setPassword(password1);
                    newUser.put("handle", handle);
                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("Signup Activity", "Signup successful");
                                final Intent intent = new Intent(Signup.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.e("Signup Activity", "Login error");
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });

    }

    private boolean checkUniqueUser(String user) {
        // -TODO check if the username is not taken
        return true;
    }

    /*
        Checks if the passwords are the same and is atleast one char
     */
    private boolean samePassword(String password1, String password2) {

        if (!password1.equals(password2)) {
            Toast.makeText(Signup.this, "The passwords don't match", Toast.LENGTH_LONG).show();
            return false;
        }
        if (password1.length() <= 0) {
            Toast.makeText(Signup.this, "The password mustn't be empty", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


}
