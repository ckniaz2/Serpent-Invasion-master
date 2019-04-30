package com.example.snake;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    Switch bombs;
    AutoCompleteTextView textView;
    String name = "";
    boolean bombsOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get the imageView on the left
        ImageView leftImage = (ImageView) findViewById(R.id.imageView);
        leftImage.setImageResource(R.drawable.snake);

        // get the textView (Unless you want to do the enter name thing.  I deleted
        // it because it would have been more work than just last score & high score.
        //textView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);

        // get the switch view
        bombs = (Switch) findViewById(R.id.switch1);
        bombs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    System.out.println("Bombs are turned on");
                    bombsOn = isChecked;
                } else {
                    System.out.println("Bombs are turned off");
                    bombsOn = isChecked;
                }
            }
        });

    }

    public void startClicked(View view) {
        System.out.println("Start button clicked");
        //System.out.println(textView.getText().toString());

        Intent intent = new Intent(MainActivity.this, Snake.class);
        intent.putExtra("bombs", bombsOn);
        startActivity(intent);
    }

    public void leaderboardClicked(View view) {
        System.out.println("Leaderboard button clicked");

        Intent intent = new Intent(getBaseContext(), Leaderboard.class);
        startActivity(intent);
    }

}
