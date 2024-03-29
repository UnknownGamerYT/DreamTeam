package com.example.dreamteam.game;

import com.example.dreamteam.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DobbleGame {


    Player player;
    Deck deck;
    CardPair currCardPair;
    //example of storing available images
    int[] fullImageList = new int[]{R.drawable.anchor, R.drawable.apple, R.drawable.birdie, R.drawable.bolt, R.drawable.bomb, R.drawable.bottle, R.drawable.cactus,R.drawable.candle,R.drawable.car,R.drawable.carrot,R.drawable.cheese,R.drawable.chess_knight,R.drawable.clock,R.drawable.clover,R.drawable.clown,R.drawable.daisy,R.drawable.dinosaur,R.drawable.dobble,R.drawable.dog,R.drawable.dolphin,R.drawable.dragon,R.drawable.exclamation_mark,R.drawable.eye,R.drawable.fire,R.drawable.ghost,R.drawable.glasses,R.drawable.hammer,R.drawable.heart,R.drawable.icecube,R.drawable.igloo,R.drawable.key,R.drawable.kitty,R.drawable.ladybug,R.drawable.lightbulb,R.drawable.lips,R.drawable.man,R.drawable.maple_leaf,R.drawable.moon,R.drawable.net,R.drawable.no_entry,R.drawable.padlock,R.drawable.pencil,R.drawable.question_mark,R.drawable.scissors,R.drawable.skull,R.drawable.snowflake,R.drawable.snowman,R.drawable.spider,R.drawable.spots,R.drawable.sun,R.drawable.target,R.drawable.treble_clef,R.drawable.tree,R.drawable.turtle,R.drawable.waterdrop,R.drawable.yin_yang,R.drawable.zebra};

    public ImageProperties constructImageProperties(){
        ImageProperties imgp = new ImageProperties();
        imgp.setImage(R.drawable.anchor);
        imgp.setProperties(new String[]{"round","orange", "anchor"});
        List<ImageProperties> list = new ArrayList<ImageProperties>();
        list.add(imgp);
        imgp.setImage(R.drawable.apple);
        imgp.setProperties(new String[]{"round","red", "apple"});
        list.add(imgp);
        imgp.setImage(R.drawable.birdie);
        imgp.setProperties(new String[]{"abstract","purple","birdie"});
        list.add(imgp);
        imgp.setImage(R.drawable.bolt);
        imgp.setProperties(new String[]{"abstract", "yellow","bolt"});
        list.add(imgp);
        imgp.setImage(R.drawable.bomb);
        imgp.setProperties(new String[]{"round","black", "bomb"});
        list.add(imgp);
        imgp.setImage(R.drawable.bottle);
        imgp.setProperties(new String[]{"abstract","white", "bottle"});
        list.add(imgp);
        imgp.setImage(R.drawable.cactus);
        imgp.setProperties(new String[]{"abstract","green","cactus"});
        list.add(imgp);
        imgp.setImage(R.drawable.candle);
        imgp.setProperties(new String[]{"abstract","red", "candle"});
        list.add(imgp);
        imgp.setImage(R.drawable.car);
        imgp.setProperties(new String[]{"abstract","red", "car"});
        list.add(imgp);
        imgp.setImage(R.drawable.carrot);
        imgp.setProperties(new String[]{"abstract","orange", "carrot"});
        list.add(imgp);
        imgp.setImage(R.drawable.cheese);
        imgp.setProperties(new String[]{"abstract","yellow","cheese"});
        list.add(imgp);
        imgp.setImage(R.drawable.chess_knight);
        imgp.setProperties(new String[]{"abstract","black", "chess_knight"});
        list.add(imgp);
        imgp.setImage(R.drawable.clock);
        imgp.setProperties(new String[]{"round","white", "clock"});
        list.add(imgp);
        imgp.setImage(R.drawable.clover);
        imgp.setProperties(new String[]{"abstract","green", "clover"});
        list.add(imgp);
        imgp.setImage(R.drawable.clown);
        imgp.setProperties(new String[]{"round","white", "clown"});
        list.add(imgp);
        imgp.setImage(R.drawable.daisy);
        imgp.setProperties(new String[]{"round","white", "daisy"});
        list.add(imgp);
        imgp.setImage(R.drawable.dinosaur);
        imgp.setProperties(new String[]{"abstract","green", "dinosaur"});
        list.add(imgp);
        imgp.setImage(R.drawable.dobble);
        imgp.setProperties(new String[]{"abstract", "purple","dobble"});
        list.add(imgp);
        imgp.setImage(R.drawable.dog);
        imgp.setProperties(new String[]{"abstract", "yellow","dog"});
        list.add(imgp);
        imgp.setImage(R.drawable.dolphin);
        imgp.setProperties(new String[]{"abstract", "blue","dolphin"});
        list.add(imgp);
        imgp.setImage(R.drawable.dragon);
        imgp.setProperties(new String[]{"abstract", "purple","dragon"});
        list.add(imgp);
        imgp.setImage(R.drawable.exclamation_mark);
        imgp.setProperties(new String[]{"abstract","yellow","exclamation_mark"});
        list.add(imgp);
        imgp.setImage(R.drawable.eye);
        imgp.setProperties(new String[]{"abstract", "white","eye"});
        list.add(imgp);
        imgp.setImage(R.drawable.fire);
        imgp.setProperties(new String[]{"abstract","red", "fire"});
        list.add(imgp);
        imgp.setImage(R.drawable.ghost);
        imgp.setProperties(new String[]{"abstract", "white","ghost"});
        list.add(imgp);
        imgp.setImage(R.drawable.glasses);
        imgp.setProperties(new String[]{"abstract","black","glasses"});
        list.add(imgp);
        imgp.setImage(R.drawable.hammer);
        imgp.setProperties(new String[]{"abstract", "orange","hammer"});
        list.add(imgp);
        imgp.setImage(R.drawable.heart);
        imgp.setProperties(new String[]{"abstract", "red","heart"});
        list.add(imgp);
        imgp.setImage(R.drawable.icecube);
        imgp.setProperties(new String[]{"abstract", "blue","icecube"});
        list.add(imgp);
        imgp.setImage(R.drawable.igloo);
        imgp.setProperties(new String[]{"abstract", "blue","igloo"});
        list.add(imgp);
        imgp.setImage(R.drawable.key);
        imgp.setProperties(new String[]{"abstract", "red","key"});
        list.add(imgp);
        imgp.setImage(R.drawable.kitty);
        imgp.setProperties(new String[]{"abstract", "purple","kitty"});
        list.add(imgp);
        imgp.setImage(R.drawable.ladybug);
        imgp.setProperties(new String[]{"round","red", "ladybug"});
        list.add(imgp);
        imgp.setImage(R.drawable.lightbulb);
        imgp.setProperties(new String[]{"abstract", "yellow","lightbulb"});
        list.add(imgp);
        imgp.setImage(R.drawable.lips);
        imgp.setProperties(new String[]{"abstract", "red","lips"});
        list.add(imgp);
        imgp.setImage(R.drawable.man);
        imgp.setProperties(new String[]{"abstract", "orange","man"});
        list.add(imgp);
        imgp.setImage(R.drawable.maple_leaf);
        imgp.setProperties(new String[]{"abstract", "red","maple_leaf"});
        list.add(imgp);
        imgp.setImage(R.drawable.moon);
        imgp.setProperties(new String[]{"abstract", "yellow","moon"});
        list.add(imgp);
        imgp.setImage(R.drawable.net);
        imgp.setProperties(new String[]{"abstract", "purple","net"});
        list.add(imgp);
        imgp.setImage(R.drawable.no_entry);
        imgp.setProperties(new String[]{"round","red", "no_entry"});
        list.add(imgp);
        imgp.setImage(R.drawable.padlock);
        imgp.setProperties(new String[]{"abstract", "gray","padlock"});
        list.add(imgp);
        imgp.setImage(R.drawable.pencil);
        imgp.setProperties(new String[]{"abstract", "blue","pencil"});
        list.add(imgp);
        imgp.setImage(R.drawable.question_mark);
        imgp.setProperties(new String[]{"abstract", "green","question_mark"});
        list.add(imgp);
        imgp.setImage(R.drawable.scissors);
        imgp.setProperties(new String[]{"abstract", "purple","scissors"});
        list.add(imgp);
        imgp.setImage(R.drawable.skull);
        imgp.setProperties(new String[]{"abstract", "white","skull"});
        list.add(imgp);
        imgp.setImage(R.drawable.snowflake);
        imgp.setProperties(new String[]{"round","blue","snowflake"});
        list.add(imgp);
        imgp.setImage(R.drawable.spider);
        imgp.setProperties(new String[]{"abstract", "black","spider"});
        list.add(imgp);
        imgp.setImage(R.drawable.spots);
        imgp.setProperties(new String[]{"abstract", "green","spots"});
        list.add(imgp);
        imgp.setImage(R.drawable.sun);
        imgp.setProperties(new String[]{"round","yellow", "sun"});
        list.add(imgp);
        imgp.setImage(R.drawable.target);
        imgp.setProperties(new String[]{"round","red", "target"});
        list.add(imgp);
        imgp.setImage(R.drawable.treble_clef);
        imgp.setProperties(new String[]{"abstract", "red","treble_clef"});
        list.add(imgp);
        imgp.setImage(R.drawable.tree);
        imgp.setProperties(new String[]{"abstract", "green","tree"});
        list.add(imgp);
        imgp.setImage(R.drawable.turtle);
        imgp.setProperties(new String[]{"abstract", "green","turtle"});
        list.add(imgp);
        imgp.setImage(R.drawable.waterdrop);
        imgp.setProperties(new String[]{"abstract", "blue","waterdrop"});
        list.add(imgp);
        imgp.setImage(R.drawable.yin_yang);
        imgp.setProperties(new String[]{"round","black", "yin_yang"});
        list.add(imgp);
        imgp.setImage(R.drawable.zebra);
        imgp.setProperties(new String[]{"abstract", "black","zebra"});
        list.add(imgp);

        return imgp;
    }


    public DobbleGame(){

        player = new Player("Human");
        //player2 actr model
        deck = new Deck("Dobble8Deck",8);
        currCardPair = null;

    }

    public void updateCardPair() {
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
    public CardPair getCurrCardPair(){
        return currCardPair;
    }
}
