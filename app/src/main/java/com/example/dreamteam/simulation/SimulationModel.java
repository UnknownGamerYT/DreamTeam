package com.example.dreamteam.simulation;


import static java.lang.Math.random;

import com.example.dreamteam.game.CardPair;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class SimulationModel {

    String name;

    private double realTime;
    private double modelTime;
    boolean runUntilStop = false;

    long timeToFind = 1000;
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
        return 250;
    }
    //cards > look for a match > select matching pic
    public int lookForMatch(CardPair cardPair){
        this.timeToFind = 1000;
        List<Integer> topCardImages = cardPair.cardT.getimages();
        List<Integer> bottomCardImages = cardPair.cardB.getimages();

        //todo: create random search sequence, or base it on most apparent colors

        //memorize image from one card, cycle through other card images to look for match
        //track model time of course.
        //^repeat until a match is found.
        int prediction = 0;
        for (int i=0; i<=topCardImages.size()-1; i++){
            this.timeToFind += eyeMovementDuration();//todo: increase this by eye movement and other cognitive stages instead
            //memorize topCardImages[i];
            //add looking delay
            for (int j=0; j<=bottomCardImages.size()-1; j++) {
                this.timeToFind += eyeMovementDuration();//todo: increase this by eye movement and other cognitive stages instead
                //look if there is a match, keep track of what u check, but dont 'hard' memorize the whole card.
                if(Objects.equals(topCardImages.get(i), bottomCardImages.get(j))){
                    prediction = i;
                    return prediction;
                }

            }
        }

        return prediction;
        //return image/button/match/selection
    }
}
