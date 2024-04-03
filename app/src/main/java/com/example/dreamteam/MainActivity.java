package com.example.dreamteam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button StartButton = findViewById(R.id.StartButton);
        StartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, game_activity.class);
                myIntent.putExtra("ModelVariant", "ColorModel");
                startActivity(myIntent);
            }
        });

        Button StartButton2 = findViewById(R.id.StartButton2);
        StartButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, game_activity.class);
                myIntent.putExtra("ModelVariant", "1v1");
                startActivity(myIntent);
            }
        });
        Button StartButton3 = findViewById(R.id.StartButton3);
        StartButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, game_activity.class);
                myIntent.putExtra("ModelVariant", "SequenceModel");
                startActivity(myIntent);
            }
        });

    }
}