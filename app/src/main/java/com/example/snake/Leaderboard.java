package com.example.snake;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.content.SharedPreferences.Editor;

public class Leaderboard extends AppCompatActivity {

    int highScore;
    int lastScore;
    TextView high;
    TextView last;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        // Get the high score and last score from the shared preferences
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Scores", 0);
        Editor editor = pref.edit();
        highScore = pref.getInt("high", 0);
        lastScore = pref.getInt("last", 0);

        if (lastScore > highScore) {
            highScore = lastScore;
            editor.putInt("high", lastScore);
            editor.apply();
        }

        // set the text of the high score TextView
        high = (TextView) findViewById(R.id.high);
        high.setText(Integer.toString(highScore));

        // set the text of the last score TextView
        last = (TextView) findViewById(R.id.last);
        last.setText(Integer.toString(lastScore));
    }
    public void buttonClicked(View view) {
        System.out.println("Leaderboard button clicked");

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
