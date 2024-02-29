package com.example.dreamteam.game;

import android.provider.MediaStore;

import com.example.dreamteam.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Deck {

    /* Variables */

    private final String setName;

    private final Card[] cards;
    private int index;
    int[] fullImageList = new int[]{R.drawable.anchor, R.drawable.apple, R.drawable.birdie, R.drawable.bolt, R.drawable.bomb, R.drawable.bottle, R.drawable.cactus,R.drawable.candle,R.drawable.car,R.drawable.carrot,R.drawable.cheese,R.drawable.chess_knight,R.drawable.clock,R.drawable.clover,R.drawable.clown,R.drawable.daisy,R.drawable.dinosaur,R.drawable.dobble,R.drawable.dog,R.drawable.dolphin,R.drawable.dragon,R.drawable.exclamation_mark,R.drawable.eye,R.drawable.fire,R.drawable.ghost,R.drawable.glasses,R.drawable.hammer,R.drawable.heart,R.drawable.icecube,R.drawable.igloo,R.drawable.key,R.drawable.kitty,R.drawable.ladybug,R.drawable.lightbulb,R.drawable.lips,R.drawable.man,R.drawable.maple_leaf,R.drawable.moon,R.drawable.net,R.drawable.no_entry,R.drawable.padlock,R.drawable.pencil,R.drawable.question_mark,R.drawable.scissors,R.drawable.skull,R.drawable.snowflake,R.drawable.snowman,R.drawable.spider,R.drawable.spots,R.drawable.sun,R.drawable.target,R.drawable.treble_clef,R.drawable.tree,R.drawable.turtle,R.drawable.waterdrop,R.drawable.yin_yang,R.drawable.zebra};
//triple anchor R.drawable.anchor,R.drawable.anchor, no more needed?


    /* Constructor */

    public Deck(String name, int symbolsPerCard) {
        setName = name;
        cards = generateCards(symbolsPerCard);

        index = cards.length - 1;
    }


    /* Methods */

    public CardPair pickCardPair() {
        Card cardB = pickCard();
        Card cardT= pickCard();
        if (cardB != null && cardT != null) {
            return new CardPair(cardB, cardT);
        }
        else {
            return null;
        }
    }

    public Card pickCard() {
        if (index == -1) {
            return null;
        }
        else {
            Card c = cards[index];;
            index--;
            return c;
        }
    }

    public boolean isEmpty() {
        return index == -1;
    }

    public void resetDeck() {
        index = cards.length - 1;
    }


    private boolean contains(Card c) {
        for (Card card : cards) {
            if (card.equals(c)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Deck->{" +
                "setName=\"" + setName +
                "\", index=" + index +
                ", cards=" + Arrays.toString(cards) +
                "}";
    }

    public String toFormattedString() {
        String s = "Deck->{" +
                "\n\tsetName=\"" + setName +
                "\",\n\tindex=" + index +
                ",\n\tcards=\n";
        for (int i = 0; i < cards.length; i++) {
            s = s.concat(String.format("\t\t%s\n", cards[i].toFormattedString()));
        }
        return s.concat("}");
    }



    //work in progress
    private Card[] generateCards(int symbolPerCard) {//Symbol is MediaStore.Images Image
        if (!isPrime(symbolPerCard - 1)) {
            throw new IllegalArgumentException("Number of symbols must be prime + 1");
        }
        double temp = Math.pow(symbolPerCard - 1, 2);
        if (symbolPerCard + Math.pow(symbolPerCard - 1, 2) > 57) {//atlas.getRegions().size
            throw new IllegalArgumentException("Not enough symbols available");
        }



        ArrayList<Card> deck = new ArrayList<>();

        for (int i = 0; i <= symbolPerCard - 1; i++) {
            //int[] cardSymbols = new int[0];
            List<Integer> cardSymbols = new ArrayList<>();
            cardSymbols.add(fullImageList[0]);//????????????????????????
            for (int j = 1; j <= symbolPerCard - 1; j++) {
                int curr = (symbolPerCard - 1) + (symbolPerCard - 1) * (i - 1) + (j);
                //int curr = (symbolPerCard) + (symbolPerCard - 1) * (i) + (j);

                cardSymbols.add(fullImageList[curr]);
                //cardSymbols[curr] = fullImageList[curr];//
            }
            deck.add(new Card(shuffleImages(cardSymbols)));
        }
        for (int i = 1; i <= symbolPerCard - 1; i++) {
            for (int j = 1; j <= symbolPerCard - 1; j++) {
                List<Integer> cardSymbols = new ArrayList<>();
                cardSymbols.add(fullImageList[i]);
                for (int k = 1; k <= symbolPerCard - 1; k++) {
                    int curr = (symbolPerCard + 1) + (symbolPerCard - 1) * (k -1)
                            + ((i - 1) * (k - 1) + (j - 1)) % (symbolPerCard - 1) -1;
                    cardSymbols.add(fullImageList[curr]);
                }
                deck.add(new Card(shuffleImages(cardSymbols)));
            }
        }

        return shuffleCards(deck.toArray(new Card[0]));
    }

    private boolean isPrime(int n) {
        for (int i = 2; i < n; i++) {
            if (n % i == 0){
                return false;
            }
        }
        return true;
    }

    private List<Integer> shuffleImages(List<Integer> images) {
        for (int i = 0; i < images.size(); i++) {
            Random random = new Random();
            int indexToSwap = random.nextInt(images.size() - 1);
            int temp = images.get(indexToSwap);
            images.set(indexToSwap, images.get(i));
            images.set(i, temp);
        }
        return images;
    }

    private Card[] shuffleCards(Card[] cards) {
        for (int i = 0; i < cards.length; i++) {
            Random random = new Random();
            int indexToSwap = random.nextInt(cards.length);
            Card temp = cards[indexToSwap];
            cards[indexToSwap] = cards[i];
            cards[i] = temp;
        }
        return cards;
    }

}