package com.redheadhammer.quizgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.redheadhammer.quizgame.databinding.StartScreenBinding;

public class StartScreen extends AppCompatActivity {

    private StartScreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = StartScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Animation textAnim = AnimationUtils.loadAnimation(this, R.anim.text_anim);
        Animation imageAnim = AnimationUtils.loadAnimation(this, R.anim.icon_anim);

        binding.imageView.startAnimation(imageAnim);
        binding.textView.startAnimation(textAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(StartScreen.this, LogIn.class);
                startActivity(intent);
                finish();
            }
        }, 1300);
    }
}