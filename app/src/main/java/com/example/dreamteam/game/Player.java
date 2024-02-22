package com.example.dreamteam.game;

public class Player {

    private final String name;
    private int score;

    public Player(String name) {
        this.name = name;
        score = 0;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void addPoint() {
        score++;
    }

    public void addPoints(int points) {
        score += points;
    }

    public void losePoint() {
        score--;
    }

    public void losePoints(int points) {
        score -= points;
    }

}