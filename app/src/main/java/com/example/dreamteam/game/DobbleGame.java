package com.example.dreamteam.game;

import com.example.dreamteam.R;

public class DobbleGame {


    Player player;
    Deck deck;
    CardPair currCardPair;
    //example of storing available images
    int[] fullImageList = new int[]{R.drawable.anchor, R.drawable.apple, R.drawable.birdie, R.drawable.bolt, R.drawable.bomb, R.drawable.bottle, R.drawable.cactus};


    public void Create () {

        player = new Player("Human");
        //player2 actr model
        deck = new Deck("Dobble9Deck",9);
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
}
