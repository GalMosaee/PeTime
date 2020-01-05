package com.example.petime.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petime.Model.Model;
import com.example.petime.R;

//import java.sql.Timestamp;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private TextView signup;
    private Button login;
    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);

        if(Model.instance.getCurrentUser() != null) {
            updateUI();
        }


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "email:" + email.getText().toString());
                Log.d(TAG, "password:" + password.getText().toString());

                if (password.getText().toString().equals("") || email.getText().toString().equals("")){
                    Toast.makeText(LoginActivity.this, "e-mail and password are required. please try again",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    //signin(email.getText().toString().trim(), password.getText().toString().trim());
                    Model.instance.signIn(email.getText().toString().trim(), password.getText().toString().trim(),new Model.SignInListener() {
                        @Override
                        public void onComplete(boolean status, Exception e) {
                            if (status == true) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                Toast.makeText(LoginActivity.this, "User has been login.",
                                        Toast.LENGTH_SHORT).show();
                                //FirebaseUser user = mAuth.getCurrentUser();
                                //updateUI(user);
                                updateUI();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure",e);
                                //  Toast.makeText(LoginActivity.this, "Authentication failed.",
                                //        Toast.LENGTH_SHORT).show();
                                Toast.makeText(LoginActivity.this, e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                                updateUI();
                            }
                        }
                        /*public void onComplete(@Nonnull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                Toast.makeText(LoginActivity.this, "User has been login.",
                                        Toast.LENGTH_SHORT).show();
                                //FirebaseUser user = mAuth.getCurrentUser();
                                //updateUI(user);
                                updateUI();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                //  Toast.makeText(LoginActivity.this, "Authentication failed.",
                                //        Toast.LENGTH_SHORT).show();
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                                updateUI();
                            }
                        }*/
                    });
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
               // onActivityResult(requestCode, resultCode, intent);
               // String intEmail = intent.getStringExtra("intEmail");
               // String intPassword = intent.getStringExtra("intPassword");
               // signin(intEmail, intPassword);
            }
        });
    }

    private void updateUI() {
        if (Model.instance.getCurrentUser() != null){
            // Check if user's email is verified
            boolean emailVerified = Model.instance.getCurrentUser().isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = Model.instance.getCurrentUser().getUid();

            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            //editor.putString("profileid", Model.instance.getCurrentUser().getUid());
            editor.putString("email", Model.instance.getCurrentUser().getEmail());
            editor.apply();

            //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            //startActivity(intent);
            finish();

            Log.d(TAG, "name:" + Model.instance.getCurrentUser().getDisplayName());
            Log.d(TAG, "email:" + Model.instance.getCurrentUser().getEmail());
            Log.d(TAG, "uid:" + Model.instance.getCurrentUser().getUid());
        }
    }
}