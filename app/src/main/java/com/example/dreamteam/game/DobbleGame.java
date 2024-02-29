package com.example.dreamteam.game;

import com.example.dreamteam.R;

import java.io.File;

public class DobbleGame {


    Player player;
    Deck deck;
    CardPair currCardPair;
    //example of storing available images
    int[] fullImageList = new int[]{R.drawable.anchor, R.drawable.apple, R.drawable.birdie, R.drawable.bolt, R.drawable.bomb, R.drawable.bottle, R.drawable.cactus,R.drawable.candle,R.drawable.car,R.drawable.carrot,R.drawable.cheese,R.drawable.chess_knight,R.drawable.clock,R.drawable.clover,R.drawable.clown,R.drawable.daisy,R.drawable.dinosaur,R.drawable.dobble,R.drawable.dog,R.drawable.dolphin,R.drawable.dragon,R.drawable.exclamation_mark,R.drawable.eye,R.drawable.fire,R.drawable.ghost,R.drawable.glasses,R.drawable.hammer,R.drawable.heart,R.drawable.icecube,R.drawable.igloo,R.drawable.key,R.drawable.kitty,R.drawable.ladybug,R.drawable.lightbulb,R.drawable.lips,R.drawable.man,R.drawable.maple_leaf,R.drawable.moon,R.drawable.net,R.drawable.no_entry,R.drawable.padlock,R.drawable.pencil,R.drawable.question_mark,R.drawable.scissors,R.drawable.skull,R.drawable.snowflake,R.drawable.snowman,R.drawable.spider,R.drawable.spots,R.drawable.sun,R.drawable.target,R.drawable.treble_clef,R.drawable.tree,R.drawable.turtle,R.drawable.waterdrop,R.drawable.yin_yang,R.drawable.zebra};


    public DobbleGame(){

        player = new Player("Human");
        //player2 actr model
        deck = new Deck("Dobble8Deck",8);
        currCardPair = null;

    }
    private void updateCardPair() {
        if (deck.isEmpty()) {
            //endgame
        } else if (currCardPair == null) {
            currCardPair = deck.pickCardPair();
        } else if (currCardPair.solved) {
            currCardPair = deck.pickCardPair();
            //score points
        }

    }

    public int[] getFullImageList()
    {
        return fullImageList;
    }

    public Deck getDeck() {
        return deck;
    }
}
