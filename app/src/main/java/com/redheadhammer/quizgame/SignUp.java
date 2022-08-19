package com.redheadhammer.quizgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.redheadhammer.quizgame.databinding.ActivitySignUpBinding;

public class SignUp extends AppCompatActivity {
    private static final String TAG = "SignUp";
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private ActivitySignUpBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.signUp.setOnClickListener(this::addUser);
    }
    private void addUser(View view) {
        binding.signUp.setClickable(false);
        binding.progressBar.setVisibility(View.VISIBLE);
        String userMail = binding.email.getText().toString();
        String password = binding.password.getText().toString();

        firebaseAuth.createUserWithEmailAndPassword(userMail, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(SignUp.this,
                                "Account Created Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.progressBar.setVisibility(View.GONE);
                        // TODO: show result on the basis of error occurred
                        try {
                            throw e;
                        } catch (FirebaseAuthUserCollisionException exception) {
                            Toast.makeText(SignUp.this,
                                    "User with this email already exist", Toast.LENGTH_SHORT).show();
                        } catch (Exception except) {
                            Toast.makeText(SignUp.this,
                                    "Account Creation Failed", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onFailure: " + except);
                        }
                    }
                });
    }
}