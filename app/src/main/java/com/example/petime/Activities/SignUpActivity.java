package com.example.petime.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petime.Model.Model;
import com.example.petime.R;

public class SignUpActivity extends AppCompatActivity {

    private EditText email, password, repassword ,displayname;
    private Button signup;
    private static final String TAG = SignUpActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        email = findViewById(R.id.username);
        displayname = findViewById(R.id.displayname);
        password = findViewById(R.id.password);
        repassword = findViewById(R.id.repassword);
        signup = findViewById(R.id.signup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (password.getText().toString().equals("") || repassword.getText().toString().equals("") || email.getText().toString().equals("") || displayname.getText().toString().equals("")) {
                    Toast.makeText(SignUpActivity.this, "E-mail, User Name, Password and Re-password are required. please try again",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    if (password.getText().toString().equals(repassword.getText().toString())) {
                        Model.instance.signUp(email.getText().toString().trim(),displayname.getText().toString().trim(),password.getText().toString().trim(),new Model.SignUpListener() {
                            @Override
                            public void onComplete(boolean status,Exception e) {
                                if (status == true) {
                                    Log.d(TAG, "createUserWithEmail:success");
                                    Toast.makeText(SignUpActivity.this, "User has been created.",
                                            Toast.LENGTH_SHORT).show();
                                    SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                                    //editor.putString("profileid", Model.instance.getCurrentUser().getUid());
                                    editor.putString("email", Model.instance.getCurrentUser().getEmail());
                                    editor.apply();
                                    finish();
                                } else {
                                    Log.w(TAG, "createUserWithEmail:failure",e);
                                    Toast.makeText(SignUpActivity.this, e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }});

                        }else{
                        Toast.makeText(SignUpActivity.this, "password and re-password are different. please try again",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}