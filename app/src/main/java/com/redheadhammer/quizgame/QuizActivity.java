package com.redheadhammer.quizgame;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.redheadhammer.quizgame.databinding.ActivityQuizBinding;

import java.util.Locale;
import java.util.Objects;

public class QuizActivity extends AppCompatActivity {

    private ActivityQuizBinding binding;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final DatabaseReference reference = database.getReference().child("Questions");
    private int questionNum = 1;
    private static int totalQuestions;
    private String correctAnswer;
    private int rightAnswered = 0;
    private static final long TOTAL_TIME = 25000;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getColor(R.color.orange)));
        setContentView(binding.getRoot());

        getQuestion();

        timer = new CountDownTimer(TOTAL_TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.countDown.setText("" + (int) millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                disableButtons();
                if (questionNum < totalQuestions) {
                    questionNum++;
                }
                FirebaseUser user = firebaseAuth.getCurrentUser();
                DatabaseReference reference1 = database.getReference();
                reference1.child("Results").child(user.getUid()).child("total").setValue(totalQuestions)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(QuizActivity.this,
                                        "push success: " + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                            }
                        });
                reference1.child("Results").child(user.getUid()).child("correct").setValue(correctAnswer);
            }
        };

        timer.start();

        binding.nextQuestion.setOnClickListener(this::nextQuestion);
        binding.finish.setOnClickListener(this::finishQuiz);
        binding.option0.setOnClickListener((view) -> answered("a", binding.option0));
        binding.option1.setOnClickListener((view) -> answered("b", binding.option1));
        binding.option2.setOnClickListener((view) -> answered("c", binding.option2));
        binding.option3.setOnClickListener((view) -> answered("d", binding.option3));
    }


    private void answered(String answer, TextView textView) {
        if (correctAnswer.equals(answer)) {
            rightAnswered++;
            Toast.makeText(this, "You are right", Toast.LENGTH_SHORT).show();
            textView.setTextColor(Color.GREEN);
        } else {
            textView.setTextColor(Color.RED);
            Toast.makeText(this, "Correct answer is: " + correctAnswer, Toast.LENGTH_SHORT).show();
        }
        disableButtons();
        timer.cancel();
    }

    private void disableButtons() {
        binding.option0.setClickable(false);
        binding.option1.setClickable(false);
        binding.option2.setClickable(false);
        binding.option3.setClickable(false);

        Toast.makeText(this, "Correct answer is: " + correctAnswer, Toast.LENGTH_SHORT).show();
    }

    private void getQuestion() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalQuestions = (int) snapshot.getChildrenCount();
                snapshot = snapshot.child("" + questionNum);
                binding.question.setText(
                        snapshot.child("q").getValue().toString()
                );

                binding.option0.setText(
                        snapshot.child("a").getValue().toString()
                );

                binding.option1.setText(
                        snapshot.child("b").getValue().toString()
                );

                binding.option2.setText(
                        snapshot.child("c").getValue().toString()
                );

                binding.option3.setText(
                        snapshot.child("d").getValue().toString()
                );

                correctAnswer = snapshot.child("answer").getValue().toString();
                binding.answerRatio.setText(String.format(Locale.ENGLISH, "%d/%d", questionNum, totalQuestions));

                if (questionNum <= totalQuestions) {
                    if (questionNum == totalQuestions)
                        binding.nextQuestion.setClickable(false);
                    questionNum++;
                } else {
                    Toast.makeText(QuizActivity.this,
                            "You answered all questions", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuizActivity.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void nextQuestion(View view) {
        getQuestion();
        timer.start();
        binding.option0.setClickable(true);
        binding.option1.setClickable(true);
        binding.option2.setClickable(true);
        binding.option3.setClickable(true);

        binding.option0.setTextColor(Color.WHITE);
        binding.option1.setTextColor(Color.WHITE);
        binding.option2.setTextColor(Color.WHITE);
        binding.option3.setTextColor(Color.WHITE);
    }

    private void finishQuiz(View view) {
        Toast.makeText(this, "" + rightAnswered, Toast.LENGTH_SHORT).show();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        DatabaseReference reference1 = database.getReference();
        reference1.child("Results").child(user.getUid()).child("total").setValue(totalQuestions)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(QuizActivity.this,
                                "push success: " + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                    }
                });
        reference1.child("Results").child(user.getUid()).child("correct").setValue(correctAnswer);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            firebaseAuth.signOut();
            Intent intent = new Intent(QuizActivity.this, LogIn.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}