package com.example.dreamteam;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dreamteam.game.DobbleGame;

import java.util.Random;

public class game_activity extends AppCompatActivity implements View.OnClickListener{
    DobbleGame dobbleGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        dobbleGame = new DobbleGame();
        ImageButton imageButton = findViewById(R.id.imageButton);
        ImageButton imageButton2 = findViewById(R.id.imageButton2);
        ImageButton imageButton3 = findViewById(R.id.imageButton3);
        ImageButton imageButton4 = findViewById(R.id.imageButton4);
        ImageButton imageButton5 = findViewById(R.id.imageButton5);
        ImageButton imageButton6 = findViewById(R.id.imageButton6);
        ImageButton imageButton7 = findViewById(R.id.imageButton7);
        ImageButton imageButton8 = findViewById(R.id.imageButton8);
        ImageButton imageButton9 = findViewById(R.id.imageButton9);
        ImageButton imageButton10 = findViewById(R.id.imageButton10);
        ImageButton imageButton11 = findViewById(R.id.imageButton11);
        ImageButton imageButton12 = findViewById(R.id.imageButton12);
        ImageButton imageButton13 = findViewById(R.id.imageButton13);
        ImageButton imageButton14 = findViewById(R.id.imageButton14);
        ImageButton imageButton15 = findViewById(R.id.imageButton15);
        ImageButton imageButton16 = findViewById(R.id.imageButton16);
        ImageButton imageButton17 = findViewById(R.id.imageButton17);
        ImageButton imageButton18 = findViewById(R.id.imageButton18);


        imageButton.setOnClickListener(this);
        imageButton2.setOnClickListener(this);
        imageButton3.setOnClickListener(this);
        imageButton4.setOnClickListener(this);
        imageButton5.setOnClickListener(this);
        imageButton6.setOnClickListener(this);
        imageButton7.setOnClickListener(this);
        imageButton8.setOnClickListener(this);
        imageButton9.setOnClickListener(this);
        imageButton10.setOnClickListener(this);
        imageButton11.setOnClickListener(this);
        imageButton12.setOnClickListener(this);
        imageButton13.setOnClickListener(this);
        imageButton14.setOnClickListener(this);
        imageButton15.setOnClickListener(this);
        imageButton16.setOnClickListener(this);
        imageButton17.setOnClickListener(this);
        imageButton18.setOnClickListener(this);
        //imageButton7.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.imageButton:
                ButtonPressed(v, R.id.imageButton);
                break;
            case R.id.imageButton2:
                ButtonPressed(v, R.id.imageButton2);
                break;
            case R.id.imageButton3:
                ButtonPressed(v, R.id.imageButton3);
                break;
            case R.id.imageButton4:
                ButtonPressed(v, R.id.imageButton4);
                break;
            case R.id.imageButton5:
                ButtonPressed(v, R.id.imageButton5);
                break;
            case R.id.imageButton6:
                ButtonPressed(v, R.id.imageButton6);
                break;
            case R.id.imageButton7:
                ButtonPressed(v, R.id.imageButton7);
                break;

            case R.id.imageButton8:
                ButtonPressed(v, R.id.imageButton8);
                break;
            case R.id.imageButton9:
                ButtonPressed(v, R.id.imageButton9);
                break;
            case R.id.imageButton10:
                ButtonPressed(v, R.id.imageButton10);
                break;
            case R.id.imageButton11:
                ButtonPressed(v, R.id.imageButton11);
                break;
            case R.id.imageButton12:
                ButtonPressed(v, R.id.imageButton12);
                break;
            case R.id.imageButton13:
                ButtonPressed(v, R.id.imageButton13);
                break;
            case R.id.imageButton14:
                ButtonPressed(v, R.id.imageButton14);
                break;
            case R.id.imageButton15:
                ButtonPressed(v, R.id.imageButton15);
                break;
            case R.id.imageButton16:
                ButtonPressed(v, R.id.imageButton16);
                break;
            case R.id.imageButton17:
                ButtonPressed(v, R.id.imageButton17);
                break;
            case R.id.imageButton18:
                ButtonPressed(v, R.id.imageButton18);
                break;

        }
    }
    public void ButtonPressed(View v, int buttonId){
        TextView text=(TextView)findViewById(R.id.ScoreText);
        String[] separated = text.getText().toString().split(" ");
        int value = Integer.parseInt(separated[1]);
        ImageButton img= (ImageButton) findViewById(buttonId);
        int[] images = dobbleGame.getFullImageList();
        Random random = new Random();
        img.setImageResource(images[random.nextInt(images.length)]);
        if (true)//if wins
        {
            value += 1;
            Toast.makeText(getApplicationContext(), "Congratulations!", Toast.LENGTH_LONG).show();
        }
        else
        {
            value-= 1;
            Toast.makeText(getApplicationContext(), "Incorrect, point deducted", Toast.LENGTH_LONG).show();
        }
        String newText = "Score: " + value;

        text.setText(newText);
    }
    public void Button1Press(View v) {
        TextView text=(TextView)findViewById(R.id.ScoreText);
        String[] separated = text.getText().toString().split(" ");
        int value = Integer.parseInt(separated[1]);
        ImageButton img= (ImageButton) findViewById(R.id.imageButton);
        int[] images = dobbleGame.getFullImageList();
        Random random = new Random();
        img.setImageResource(images[random.nextInt(images.length)]);
        if (true)//if wins
        {
            value += 1;
            Toast.makeText(getApplicationContext(), "Congratulations!", Toast.LENGTH_LONG).show();
        }
        else
        {
            value-= 1;
            Toast.makeText(getApplicationContext(), "Incorrect, point deducted", Toast.LENGTH_LONG).show();
        }
        String newText = "Score: " + value;

        text.setText(newText);
    }


}