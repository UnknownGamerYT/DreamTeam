package com.example.dreamteam;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.dreamteam.actr.core.*;
import com.example.dreamteam.actr.core.task.Task;
import com.example.dreamteam.game.Card;
import com.example.dreamteam.game.CardPair;
import com.example.dreamteam.game.DobbleGame;
import com.example.dreamteam.simulation.AttentionTrack;
import com.example.dreamteam.simulation.SimulationModel;

import java.util.List;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class game_activity extends AppCompatActivity implements View.OnClickListener {
    DobbleGame dobbleGame;
    boolean runModel = true;// disable for 1v1 enable for 1 v model
    boolean runCountdown = true;//mostly for testing, determines if the game is played with cooldown breaks
    boolean enabled = true;//enable or disable buttons. other methods were causing problems...
    int[] bottomCardButtons = new int[] {R.id.imageButton,R.id.imageButton2,R.id.imageButton3,R.id.imageButton4,R.id.imageButton6,R.id.imageButton7,R.id.imageButton8,R.id.imageButton9};
    int[] topCardButtons = new int[] {R.id.imageButton10, R.id.imageButton11,R.id.imageButton12,R.id.imageButton13,R.id.imageButton15,R.id.imageButton16,R.id.imageButton17,R.id.imageButton18};
    int[] allButtons = new int[] {R.id.imageButton,R.id.imageButton2,R.id.imageButton3,R.id.imageButton4,R.id.imageButton6,R.id.imageButton7,R.id.imageButton8,R.id.imageButton9,R.id.imageButton10, R.id.imageButton11,R.id.imageButton12,R.id.imageButton13,R.id.imageButton15,R.id.imageButton16,R.id.imageButton17,R.id.imageButton18};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //visual input, separate images into chunks, and put them into visicon.
        //desrcibe each image with with words. color, shape, etc. *

        //1 option use actr(txt)

        //2nd option:
        //create a simulation what actr model would do
        // simulation of what model is doing and write in it java.
        // what it can see, how long eye movement takes, hardcode everything.
        //if it is harder to find a match,
        // it should be harder to find a match for computer as well(size,rotation)

        //simulation. most important things to simulate. Timing is very imortant,
        // memory timing, eye, 'finger/motor',
        //what features you can identify, make different objects hard to notice,
        //ability to make mistakes, keep track of what you attended, but also forget
        // .


        //I think we proceed with simulation of actR


        Task task = new Task("dobble");
        Model model = new Model("Model1", task);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        this.dobbleGame = new DobbleGame();
        ImageButton imageButton = findViewById(R.id.imageButton);
        ImageButton imageButton2 = findViewById(R.id.imageButton2);
        ImageButton imageButton3 = findViewById(R.id.imageButton3);
        ImageButton imageButton4 = findViewById(R.id.imageButton4);
        //ImageButton imageButton5 = findViewById(R.id.imageButton5);
        ImageButton imageButton6 = findViewById(R.id.imageButton6);
        ImageButton imageButton7 = findViewById(R.id.imageButton7);
        ImageButton imageButton8 = findViewById(R.id.imageButton8);
        ImageButton imageButton9 = findViewById(R.id.imageButton9);
        ImageButton imageButton10 = findViewById(R.id.imageButton10);
        ImageButton imageButton11 = findViewById(R.id.imageButton11);
        ImageButton imageButton12 = findViewById(R.id.imageButton12);
        ImageButton imageButton13 = findViewById(R.id.imageButton13);
        //ImageButton imageButton14 = findViewById(R.id.imageButton14);
        ImageButton imageButton15 = findViewById(R.id.imageButton15);
        ImageButton imageButton16 = findViewById(R.id.imageButton16);
        ImageButton imageButton17 = findViewById(R.id.imageButton17);
        ImageButton imageButton18 = findViewById(R.id.imageButton18);


        imageButton.setOnClickListener(this);
        imageButton2.setOnClickListener(this);
        imageButton3.setOnClickListener(this);
        imageButton4.setOnClickListener(this);
        //imageButton5.setOnClickListener(this);
        imageButton6.setOnClickListener(this);
        imageButton7.setOnClickListener(this);
        imageButton8.setOnClickListener(this);
        imageButton9.setOnClickListener(this);
        imageButton10.setOnClickListener(this);
        imageButton11.setOnClickListener(this);
        imageButton12.setOnClickListener(this);
        imageButton13.setOnClickListener(this);
        //imageButton14.setOnClickListener(this);
        imageButton15.setOnClickListener(this);
        imageButton16.setOnClickListener(this);
        imageButton17.setOnClickListener(this);
        imageButton18.setOnClickListener(this);
        //imageButton7.setOnClickListener(this);
        Countdown();




    }

    @Override
    public void onClick(View v) {
        //if one button is enabled all should be. had to add this because app would crash when button is clicked during countdown before first pair is drawn
        ImageButton img1 = (ImageButton) findViewById(bottomCardButtons[0]);
        if(enabled)
            switch (v.getId()) {
                case R.id.imageButton:
                    ButtonPressed(R.id.imageButton);
                    break;
                case R.id.imageButton2:
                    ButtonPressed( R.id.imageButton2);
                    break;
                case R.id.imageButton3:
                    ButtonPressed( R.id.imageButton3);
                    break;
                case R.id.imageButton4:
                    ButtonPressed( R.id.imageButton4);
                    break;
                case R.id.imageButton5:
                    ButtonPressed( R.id.imageButton5);
                    break;
                case R.id.imageButton6:
                    ButtonPressed( R.id.imageButton6);
                    break;
                case R.id.imageButton7:
                    ButtonPressed( R.id.imageButton7);
                    break;

                case R.id.imageButton8:
                    ButtonPressed( R.id.imageButton8);
                    break;
                case R.id.imageButton9:
                    ButtonPressed( R.id.imageButton9);
                    break;
                case R.id.imageButton10:
                    ButtonPressed( R.id.imageButton10);
                    break;
                case R.id.imageButton11:
                    ButtonPressed( R.id.imageButton11);
                    break;
                case R.id.imageButton12:
                    ButtonPressed( R.id.imageButton12);
                    break;
                case R.id.imageButton13:
                    ButtonPressed( R.id.imageButton13);
                    break;
                case R.id.imageButton14:
                    ButtonPressed( R.id.imageButton14);
                    break;
                case R.id.imageButton15:
                    ButtonPressed( R.id.imageButton15);
                    break;
                case R.id.imageButton16:
                    ButtonPressed( R.id.imageButton16);
                    break;
                case R.id.imageButton17:
                    ButtonPressed( R.id.imageButton17);
                    break;
                case R.id.imageButton18:
                    ButtonPressed( R.id.imageButton18);
                    break;


        }
    }

    public void ButtonPressed(int buttonId) {
        ImageButton img = (ImageButton) findViewById(buttonId);
        if(enabled){
            CardPair cardPair = dobbleGame.getCurrCardPair();

            TextView text = (TextView) findViewById(R.id.ScoreText);
            String[] separated = text.getText().toString().split(" ");
            int value = Integer.parseInt(separated[1]);
            TextView text1 = (TextView) findViewById(R.id.ScoreText1);
            String[] separated1 = text1.getText().toString().split(" ");
            int value1 = Integer.parseInt(separated1[1]);


            int symbolId = (Integer) img.getTag();//Integer.parseInt(
            if(cardPair.isMatchingSymbol(symbolId)){
                cardPair.solved = true;
                Countdown();
                //Update_Game();
            }

            //int[] images = dobbleGame.getFullImageList();
            //Random random = new Random();
            //img.setImageResource(images[random.nextInt(images.length)]);
            if (cardPair.solved == true)//if wins
            {
                if (Arrays.stream(topCardButtons).anyMatch(i -> i == buttonId)){
                    value1 += 1;
                }
                else {
                    value += 1;
                }
                Toast.makeText(getApplicationContext(), "Congratulations!", Toast.LENGTH_SHORT).show();
                String newText = "Score: " + value;
                text.setText(newText);
                String newText1 = "Score: " + value1;
                text1.setText(newText1);
            }
            else {
                if (Arrays.stream(topCardButtons).anyMatch(i -> i == buttonId)) {
                    value1 -= 1;
                }
                else {
                    value -= 1;
                }
                Toast.makeText(getApplicationContext(), "Incorrect, point deducted", Toast.LENGTH_SHORT).show();
                String newText = "Score: " + value;
                text.setText(newText);
                String newText1 = "Score: " + value1;
                text1.setText(newText1);

            }
        }


        //text.setText(newText);
    }

    public void Update_Game(){

        dobbleGame.updateCardPair();//.getDeck().pickCardPair();
        CardPair cardPair = dobbleGame.getCurrCardPair();


        List<Integer> card1Images = cardPair.cardB.getimages();
        List<Integer> card2Images = cardPair.cardT.getimages();

        for (int i=0; i<=bottomCardButtons.length-1; i++){
            ImageButton img1 = (ImageButton) findViewById(bottomCardButtons[i]);
            img1.setImageResource(card1Images.get(i));
            img1.setTag(card1Images.get(i));
            //
            ImageButton img2 = (ImageButton) findViewById(topCardButtons[i]);
            img2.setImageResource(card2Images.get(i));
            img2.setTag(card2Images.get(i));
        }

        if(runModel){
            initModel();
        }
    }

    public void initModel(){
        Handler handler = new Handler();
        SimulationModel modl = new SimulationModel("");
        CardPair currentpair = dobbleGame.getCurrCardPair();
        int prediction = modl.LookForColoursFirst(currentpair);

        //time for model to find prediction
        long timeToFind = modl.getTimeToFind();
        //display how long model will take
        TextView timeText = (TextView) findViewById(R.id.timeText);
        timeText.setText(timeToFind+"" );

        List<AttentionTrack> attentionTrack = modl.getAttentionTrack();
        highlightButton(attentionTrack, currentpair);//todo: restart/stop this when player guesses
        // correctly first(currently buggy with countdown and little less buggy without countdown)
        final Runnable r = new Runnable() {
            public void run() {
                //Only make guess if the card pair was not chosen correctly by the other player
                if (currentpair == dobbleGame.getCurrCardPair()&& !currentpair.solved) {
                    ButtonPressed(topCardButtons[prediction]);
                }

                //handler.postDelayed(this, 5000);
            }


        };


        handler.postDelayed(r,timeToFind);
    }

    private void highlightButton(List<AttentionTrack> attentionTrack, CardPair currentpair) {
        long start = 0;


        for (int i=0; i<=attentionTrack.size()-1; i++){
            long end = start + attentionTrack.get(i).getTime();
            if (i>0){
                start = attentionTrack.get(i-1).getTime();
                end =  attentionTrack.get(i).getTime();}


            //top card
            if (attentionTrack.get(i).getattendedItemId() < 8){
                ImageButton btn = (ImageButton) findViewById(topCardButtons[attentionTrack.get(i).getattendedItemId()]);
                //btn.setBackgroundColor(Color.WHITE);
                Handler handler = new Handler();
                final Runnable r = new Runnable() {
                    public void run() {
                        //Only make guess if the card pair was not chosen correctly by the other player, also if solved dont update(countdown in progress)
                        if (currentpair == dobbleGame.getCurrCardPair() && !currentpair.solved) {
                            btn.setBackgroundColor(Color.parseColor("#80FF0000"));
                        }
                        else{
                            btn.setBackgroundColor(Color.WHITE);
                        }
                    }
                };
                handler.postDelayed(r,start);
                final Runnable r2 = new Runnable() {
                    public void run() {
                        //Only make guess if the card pair was not chosen correctly by the other player
                        if (currentpair == dobbleGame.getCurrCardPair()) {
                            btn.setBackgroundColor(Color.WHITE);
                        }
                    }
                };
                handler.postDelayed(r2,end);

            }//bottom card
            else{
                ImageButton btn = (ImageButton) findViewById(bottomCardButtons[attentionTrack.get(i).getattendedItemId()-8]);
                btn.setBackgroundColor(Color.WHITE);
                Handler handler = new Handler();
                final Runnable r = new Runnable() {
                    public void run() {
                        //Only make guess if the card pair was not chosen correctly by the other player, also if solved dont update(countdown in progress)
                        if (currentpair == dobbleGame.getCurrCardPair()&& !currentpair.solved) {
                            btn.setBackgroundColor(Color.parseColor("#80FF0000"));
                        }
                        else{
                            btn.setBackgroundColor(Color.WHITE);
                        }
                    }
                };
                handler.postDelayed(r,start);
                final Runnable r2 = new Runnable() {
                    public void run() {
                        //Only make guess if the card pair was not chosen correctly by the other player
                        if (currentpair == dobbleGame.getCurrCardPair()) {
                            btn.setBackgroundColor(Color.WHITE);
                        }
                    }
                };
                handler.postDelayed(r2,end);


            }
            start = end;

        }
    }

    public void Countdown(){
        if(!runCountdown){
            Update_Game();
        }
        else{
            final TextView textView = (TextView) findViewById(R.id.CountdownText);
            //DisableButtons();
            enabled= false;
            new CountDownTimer(5000, 1000) {
                public void onTick(long millisUntilFinished) {
                    textView.setText(millisUntilFinished/1000 + "");
                    enabled = false;
                    //DisableButtons();
                }

                public void onFinish() {
                    textView.setText("");
                    enabled = true;
                    //EnableButtons();
                    Update_Game();
                }

            }.start();
        }
    }

    //works weird
    private void EnableButtons(){
        for (int i=0; i<=allButtons.length-1; i++){
            ImageButton btn = (ImageButton) findViewById(allButtons[0]);
            btn.setEnabled(true);
        }
    }
    //works weird
    private void DisableButtons(){
        for (int i=0; i<=allButtons.length-1; i++){
            ImageButton btn = (ImageButton) findViewById(allButtons[0]);
            btn.setEnabled(false);
        }
    }



}