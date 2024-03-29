package com.example.dreamteam.simulation;


import static java.lang.Math.random;

import android.widget.ImageButton;

import com.example.dreamteam.R;
import com.example.dreamteam.game.CardPair;
import com.example.dreamteam.game.Deck;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
public class SimulationModel {

    String name;

    private double realTime;
    private double modelTime;
    boolean runUntilStop = false;

    double movementInitiationTime = .050;

    double featurePrepTime = .050;
    double utilityNoiseS = 0;
    long timeToFind = 1000;
    List<AttentionTrack> attentionTrack = new ArrayList<AttentionTrack>();
    String[] memory;

    public SimulationModel(String name) {
        this.name = name;
    }


    public long getTimeToFind() {
        return timeToFind;
    }

    public void setTimeToFind(long timeToFind) {
        this.timeToFind = timeToFind;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRealTime() {
        return realTime;
    }

    public void setRealTime(double realTime) {
        this.realTime = realTime;
    }

    public double getModelTime() {
        return modelTime;
    }

    public void setModelTime(double modelTime) {
        this.modelTime = modelTime;
    }
    public double noise(double s){
        double rangeMin = 0.001;
        double rangeMax =  0.005;
        double rand = ThreadLocalRandom.current().nextDouble(rangeMin,rangeMax);
        return s * Math.log((1-rand)/rand);
    }

    public int timeToPulse(double time ){
        double pulse_duration = 0.009;
        double a = 1.1;
        double b = 0.015;
        int pulses = 0;
        while (time >= 0){
            time -=pulse_duration;
            pulses += 1;
            pulse_duration = a * pulse_duration + noise(b*a*pulse_duration);
        }

        return pulses;
    }

    public double pulsesToTime(int pulses){
        double pulse_duration = 0.009;
        double a = 1.1;
        double b = 0.015;
        double time = 0;

        while (pulses > 0){
            time += pulse_duration;
            pulses -=  1;
            pulse_duration = a * pulse_duration + noise(b * a * pulse_duration);
        }

        return time;
    }
    public void simulateClick(){

        double motorAction = 0.1;
        modelTime += motorAction;
        realTime += motorAction;
    }

    public long eyeMovementDuration(){
        return 300;
    }

    public long eyeMovementNearDuration(){
        return 85;
    }//For nearby vision
    //cards > look for a match > select matching pic
    public int lookForMatch(CardPair cardPair){
        long startDelay = 500;
        this.timeToFind = startDelay;
        List<Integer> topCardImages = cardPair.cardT.getimages();
        List<Integer> bottomCardImages = cardPair.cardB.getimages();

        //time tracker to display where model looks
        // struct: Time, attendeditemid
        this.attentionTrack = new ArrayList<AttentionTrack>();

        //todo: create random search sequence, or base it on most apparent colors

        //memorize image from one card, cycle through other card images to look for match
        //track model time of course.
        //^repeat until a match is found.
        int prediction = 0;
        for (int i=0; i<=topCardImages.size()-1; i++){

            this.timeToFind += eyeMovementDuration() +150;//todo: increase this by eye movement and other cognitive stages instead
            this.attentionTrack.add(new AttentionTrack(timeToFind-startDelay, i));
            //memorize topCardImages[i];
            //add looking delay
            for (int j=0; j<=bottomCardImages.size()-1; j++) {
                this.timeToFind += eyeMovementNearDuration();//todo: increase this by eye movement and other cognitive stages instead
                this.attentionTrack.add(new AttentionTrack(timeToFind-startDelay, topCardImages.size() + j));
                //look if there is a match, keep track of what u check, but dont 'hard' memorize the whole card.
                if(Objects.equals(topCardImages.get(i), bottomCardImages.get(j))){
                    prediction = i;
                    return prediction;
                }

            }
        }
        //setAttentionTrack(attentionTrack);

        return prediction;
        //return image/button/match/selection
    }

    public List<AttentionTrack> getAttentionTrack() {
        return attentionTrack;
    }

    public void setAttentionTrack(List<AttentionTrack> attentionTrack) {
        this.attentionTrack = attentionTrack;
    }

    public List<String> getimageinfo(int integer){
        List<String> carddetails = new ArrayList<>();
        if (integer == R.drawable.target ||integer == R.drawable.no_entry || integer == R.drawable.maple_leaf || integer == R.drawable.man || integer == R.drawable.lips || integer == R.drawable.ladybug || integer == R.drawable.key || integer == R.drawable.heart || integer == R.drawable.hammer || integer == R.drawable.fire || integer == R.drawable.clown || integer == R.drawable.clock || integer == R.drawable.anchor || integer == R.drawable.car || integer == R.drawable.bottle){
            carddetails.add("red");
        }
        if (integer == R.drawable.turtle ||integer == R.drawable.tree ||integer == R.drawable.spots ||integer == R.drawable.question_mark || integer == R.drawable.dinosaur || integer == R.drawable.carrot || integer == R.drawable.apple || integer == R.drawable.cactus || integer == R.drawable.clover) {
            carddetails.add("green");
        }
        if (integer == R.drawable.scissors || integer == R.drawable.net || integer == R.drawable.kitty || integer == R.drawable.eye || integer == R.drawable.dragon || integer == R.drawable.dobble || integer == R.drawable.birdie || integer == R.drawable.candle) {
            carddetails.add("purple");
        }
        if (integer == R.drawable.sun ||integer == R.drawable.moon || integer == R.drawable.lightbulb || integer == R.drawable.exclamation_mark || integer == R.drawable.dog || integer == R.drawable.daisy ||integer == R.drawable.bolt || integer == R.drawable.cheese) {
            carddetails.add("yellow");
        }
        if (integer == R.drawable.zebra ||integer == R.drawable.yin_yang ||integer == R.drawable.spider || integer == R.drawable.skull || integer == R.drawable.padlock || integer == R.drawable.ladybug || integer == R.drawable.glasses || integer == R.drawable.bomb || integer == R.drawable.chess_knight) {
            carddetails.add("black");
        }
        if (integer == R.drawable.daisy) {
            //old (integer == R.drawable.zebra ||integer == R.drawable.spider || integer == R.drawable.snowman || integer == R.drawable.skull || integer == R.drawable.pencil || integer == R.drawable.no_entry || integer == R.drawable.lightbulb || integer == R.drawable.igloo || integer == R.drawable.icecube || integer == R.drawable.ghost || integer == R.drawable.eye || integer == R.drawable.dobble || integer == R.drawable.daisy ||integer == R.drawable.car || integer == R.drawable.bottle || integer == R.drawable.clock || integer == R.drawable.clown || integer == R.drawable.birdie)
            carddetails.add("white");
        }
        if (integer == R.drawable.tree ||integer == R.drawable.treble_clef ||integer == R.drawable.snowman || integer == R.drawable.carrot) {
            carddetails.add("orange");
        }
        if (integer == R.drawable.waterdrop ||integer == R.drawable.snowman || integer == R.drawable.snowflake || integer == R.drawable.pencil || integer == R.drawable.igloo || integer == R.drawable.icecube || integer == R.drawable.ghost || integer == R.drawable.dolphin){
            carddetails.add("blue");
        }
        return carddetails;
    }
    public Integer LookForColoursFirst(CardPair cardPair){
        Integer prediction = 0;
        List<Integer> topColors = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0));
        List<Integer> topCardImages = cardPair.cardT.getimages();
        List<Integer> bottomCardImages = cardPair.cardB.getimages();
        //get color count?
        topColors = getImageColors(topCardImages, topColors);

        List<Integer> sortedcolourslist = new ArrayList<>(topColors);
        Integer previousamount = 0;
        Integer push = 0;
        //just a quick look through both cards
        ScanCards(topCardImages,bottomCardImages);
        do {
            Collections.sort(sortedcolourslist,Collections.reverseOrder());
            if (previousamount == sortedcolourslist.get(0)){
                push +=1;
            }else{
                push =0;
            }
            previousamount = sortedcolourslist.get(0);
            String color = findcolour(topColors, sortedcolourslist.get(0),push);
            sortedcolourslist.set(0, 0);
            List<Integer> topimages = new ArrayList<>();
            List<Integer> botimages = new ArrayList<>();

            //set attention  to the most common color images
            for (int i = 0; i <= topCardImages.size() - 1; i++) {
                if (getimageinfo(topCardImages.get(i)).contains(color)) {
                    topimages.add(i);
                }
                if (getimageinfo(bottomCardImages.get(i)).contains(color)) {
                    botimages.add(i);
                }
            }

            //if color match exists check one by one
            if (botimages.size() != 0) {
                for (int i = 0; i <= topimages.size() - 1; i++) {
                    this.timeToFind += eyeMovementDuration();
                    this.attentionTrack.add(new AttentionTrack(timeToFind , topimages.get(i)));
                    for (int j = 0; j <= botimages.size() - 1; j++) {
                        this.timeToFind += eyeMovementDuration() ;
                        this.attentionTrack.add(new AttentionTrack(timeToFind , topCardImages.size() + botimages.get(j)));
                        if (Objects.equals(topCardImages.get(topimages.get(i)), bottomCardImages.get(botimages.get(j)))) {
                            prediction = topimages.get(i);
                            return prediction;
                        }
                    }
                }
            }
            //repeat until match
        }while(prediction == 0);

        return prediction;
    }

    public void ScanCards(List<Integer> topCardImages, List<Integer> bottomCardImages){
        //set attention to all images momenterally to find most common color
        for (int i = 0; i <= topCardImages.size() - 1; i++) {
            this.timeToFind += eyeMovementNearDuration();
            this.attentionTrack.add(new AttentionTrack(timeToFind , i));
        }
        for (int i = 0; i <= bottomCardImages.size() - 1; i++) {
            this.timeToFind += eyeMovementNearDuration();
            this.attentionTrack.add(new AttentionTrack(timeToFind ,topCardImages.size() + i));
        }
    }
    public String findcolour(List<Integer> topcolours, Integer maxColorAmount,Integer push){

        boolean found = false;
        int counter = 0;
        String color = "";
        do{
            if (maxColorAmount == topcolours.get(counter) && push >= 0){
                if (push == 0){
                    found = true;
                }
                if(push > 0){
                    push -=1;
                }else if (counter == 0){
                    color = "red";
                } else if (counter == 1) {
                    color = "green";
                } else if (counter == 2) {
                    color = "purple";
                } else if (counter == 3) {
                    color = "yellow";
                } else if (counter == 4) {
                    color = "black";
                } else if (counter == 5) {
                    color = "white";
                } else if (counter == 6) {
                    color = "orange";
                } else if (counter == 7) {
                    color = "blue";
                }else if (counter == 8){
                    color = "grey";
                }

            }
            if (maxColorAmount == 0){
                color = "none";
                found = true;
            }
            counter +=1;
        }while(!found);
        return color;
    }

    private List<Integer> getImageColors(List<Integer> CardImages, List<Integer> Colors){
        for (int i=0; i<= CardImages.size()-1; i++){
            List<String> imagecolours = getimageinfo(CardImages.get(i));
            if (imagecolours.contains("red")){
                Colors.set(0,Colors.get(0)+1);
            }
            if (imagecolours.contains("green")){
                Colors.set(1,Colors.get(1)+1);
            }
            if (imagecolours.contains("purple")){
                Colors.set(2,Colors.get(2)+1);
            }
            if (imagecolours.contains("yellow")){
                Colors.set(3,Colors.get(3)+1);
            }
            if (imagecolours.contains("black")){
                Colors.set(4,Colors.get(4)+1);
            }
            if (imagecolours.contains("white")){
                Colors.set(5,Colors.get(5)+1);
            }
            if (imagecolours.contains("orange")){
                Colors.set(6,Colors.get(6)+1);
            }
            if (imagecolours.contains("blue")){
                Colors.set(7,Colors.get(7)+1);
            }
        }
        return Colors;
    }

}
