package com.anioncode.memory.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.anioncode.memory.R;
import com.anioncode.memory.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.text.TextUtils.isEmpty;

public class Registration extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RegisterActivity";


    private EditText mEmail, mPassword, mNick;

    private FirebaseFirestore mDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mNick = (EditText) findViewById(R.id.nick_registration);
        mEmail = (EditText) findViewById(R.id.email_registration);
        mPassword = (EditText) findViewById(R.id.password_registration);
        mDb = FirebaseFirestore.getInstance();

        findViewById(R.id.registration).setOnClickListener(this);
    }

    /**
     * Register a new email and password to Firebase Authentication
     *
     * @param email
     * @param password
     */

    public void registerNewEmail(final String email, String password, final String nick) {


        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.getException());

                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: AuthState: " + FirebaseAuth.getInstance().getCurrentUser().getUid());

                            //insert some default data
                            User user = new User();
                            user.setEmail(email);
                            user.setUsername(nick);
                            user.setUser_id(FirebaseAuth.getInstance().getUid());

//                            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                                    .setTimestampsInSnapshotsEnabled(true)
//                                    .build();
//                            mDb.setFirestoreSettings(settings);

                            DocumentReference newUserRef = mDb
                                    .collection("USERS")
                                    .document(FirebaseAuth.getInstance().getUid());

                            newUserRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {


                                    if (task.isSuccessful()) {
                                        redirectLoginScreen();
                                    } else {
                                        View parentLayout = findViewById(android.R.id.content);
                                        Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show();
                                        Log.d(TAG, "onComplete: " + task.getException());
                                    }
                                }
                            });

                        } else {
                            View parentLayout = findViewById(android.R.id.content);
                            Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }

    private void redirectLoginScreen() {
        Log.d(TAG, "redirectLoginScreen: redirecting to login screen.");

        Intent intent = new Intent(Registration.this, Login.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onClick(View v) {

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        switch (v.getId()) {
            case R.id.registration: {
                Log.d(TAG, "onClick: attempting to register.");

                //check for null valued EditText fields
                if (!isEmpty(mEmail.getText().toString())
                        && !isEmpty(mPassword.getText().toString())
                        && !isEmpty(mNick.getText().toString())
                ) {

                    //check if passwords match

                    //Initiate registration task
                    registerNewEmail(mEmail.getText().toString().trim(), mPassword.getText().toString().trim(), mNick.getText().toString().trim());


                } else {
                    Toast.makeText(Registration.this, "You must fill out all the fields", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
}
