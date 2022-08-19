package com.redheadhammer.quizgame;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.redheadhammer.quizgame.databinding.ActivityLogInBinding;

import java.util.Objects;

public class LogIn extends AppCompatActivity {
    private static final String TAG = "LogIn";
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private ActivityLogInBinding binding;
    private ActivityResultLauncher<Intent> googleSignInResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(
                getResources().getColor(R.color.darkOrange ,getTheme())));
        binding = ActivityLogInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        registerActivityForGoogleSignIn();

        binding.signIn.setOnClickListener(this::signIn);
        binding.signInGoogle.setOnClickListener(this::signInGoogle);
        binding.signUp.setOnClickListener(this::signUp);
        binding.forgot.setOnClickListener(this::forgotPass);
    }

    private void registerActivityForGoogleSignIn() {
        googleSignInResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int resultCode = result.getResultCode();
                        Intent data = result.getData();
                        if (resultCode == RESULT_OK && data != null) {
                            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                            try {
                                GoogleSignInAccount account = task.getResult(ApiException.class);
                                getDeviceIdToken(account);
                            } catch (ApiException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
    }

    private void getDeviceIdToken(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LogIn.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LogIn.this, QuizActivity.class);
                            startActivity(intent);
                            finish();
//                            FirebaseUser user = firebaseAuth.getCurrentUser();
                        } else {
                            Toast.makeText(LogIn.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signIn(View view) {
        binding.signIn.setClickable(false);
        binding.progressBar2.setVisibility(View.VISIBLE);
        String username = binding.userName.getText().toString();
        String password = binding.password.getText().toString();

        firebaseAuth.signInWithEmailAndPassword(username, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Intent intent = new Intent(LogIn.this, QuizActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.progressBar2.setVisibility(View.GONE);
                        // TODO: show result as per exception.
                        try {
                            Log.d(TAG, "onFailure: " + e);
                            throw e;
                        } catch (Exception except) {
                            Toast.makeText(LogIn.this,
                                    "Some Unknown Error Occurred", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signInGoogle(View view) {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken("259246650605-dve2kpfb0496i01i42sshc52ej4ntr22.apps.googleusercontent.com")
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()     // this will ask user to choose an email
                .build();


        GoogleSignInClient client = GoogleSignIn.getClient(this, options);

        // start intent to make user select a google account.
        Intent signInIntent = client.getSignInIntent();
        googleSignInResult.launch(signInIntent);
    }

    private void signUp(View view) {
        Intent intent = new Intent(LogIn.this, SignUp.class);
        startActivity(intent);
    }

    private void forgotPass(View view) {
        ForgotPassword forgotPassword = new ForgotPassword();
        forgotPassword.showNow(getSupportFragmentManager(), "ForgotPasswordFragment");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(LogIn.this, QuizActivity.class);
            startActivity(intent);
            finish();
        }
    }
}