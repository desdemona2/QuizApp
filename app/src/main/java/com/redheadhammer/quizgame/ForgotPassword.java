package com.redheadhammer.quizgame;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.redheadhammer.quizgame.databinding.ForgotPassBinding;

import java.util.Objects;

public class ForgotPassword extends DialogFragment {
    private static final String TAG = "ForgotPassword";
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private ForgotPassBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = ForgotPassBinding.inflate(inflater, container, false);

        binding.submit.setOnClickListener((view) -> resetPassword());
        Objects.requireNonNull(getDialog()).getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return binding.getRoot();
    }

    private void resetPassword() {
        String email = binding.emailEntered.getText().toString();
        if (!email.isEmpty()) {
            firebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "A mail has been sent to you",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    Toast.makeText(getContext(), "No such email exist",
                                            Toast.LENGTH_SHORT).show();
                                } catch (FirebaseAuthInvalidUserException e) {
                                    Toast.makeText(getContext(),
                                            "No one is registered with this email", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "Some Error Occurred",
                                            Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "onComplete: " + e);
                                }
                            }
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Please enter a valid mail", Toast.LENGTH_SHORT).show();
        }
    }
}
