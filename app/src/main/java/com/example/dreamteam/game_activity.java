package com.example.dreamteam;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dreamteam.game.DobbleGame;

import java.util.Random;

public class game_activity extends AppCompatActivity {
    DobbleGame dobbleGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        dobbleGame = new DobbleGame();

    }

    public void Button1Press(View v) {
        TextView text=(TextView)findViewById(R.id.ScoreText);
        String[] separated = text.getText().toString().split(" ");
        int value = Integer.parseInt(separated[1]);
        ImageButton img= (ImageButton) findViewById(R.id.imageButton);
        int[] images = dobbleGame.getFullImageList();//{androidx.constraintlayout.widget.R.drawable.abc_edit_text_material, androidx.transition.R.drawable.abc_list_focused_holo, R.drawable.wganniversary_background, R.drawable.ic_launcher_foreground};
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