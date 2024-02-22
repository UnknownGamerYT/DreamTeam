package com.example.dreamteam.game;

import android.provider.MediaStore;

public class CardPair {

    public Card cardB, cardT;
    public boolean solved;

    public CardPair(Card cardB, Card cardT) {
        this.cardB = cardB;
        this.cardT = cardT;
        solved = false;
    }

    public boolean isMatchingSymbol(MediaStore.Images image) {
        return cardB.contains(image) && cardT.contains(image);
    }

    @Override
    public String toString() {
        return "CardPair{" +
                "cardB=" + cardB +
                ", cardT=" + cardT +
                ", solved=" + solved +
                '}';
    }

    public String toFormattedString() {
        return "CardPair{" +
                "\n\tcardB=" + cardB +
                ",\n\tcardT=" + cardT +
                ",\n\tsolved=" + solved +
                "\n}";
    }
}