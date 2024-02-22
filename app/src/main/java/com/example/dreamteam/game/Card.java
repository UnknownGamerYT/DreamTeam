package com.example.dreamteam.game;

import android.provider.MediaStore;

import java.util.Arrays;

public class Card {


    /* Variables */

    private final MediaStore.Images[] images;
    private  int[] textureArray;



    /* Constructor */

    public Card(MediaStore.Images[] images) {
        this.images = images;
    }


    /* Methods */

    public boolean contains(MediaStore.Images s) {
        for (MediaStore.Images Image : images) {
            if (Image.equals(s)) {
                return true;
            }
        }
        return false;
    }




    public MediaStore.Images[] getimages() {
        return images;
    }

    @Override
    public String toString() {
        return "Card->{" +
                "images=" + Arrays.toString(images) +
                "}";
    }

    public String toFormattedString() {
        String s = "Card->{\n\t\tsymbols=\n";
        for (MediaStore.Images image : images) {
            s = s.concat(String.format("\t\t\t%s\n", image.toString()));
        }
        return s.concat("\t\t}");
    }





}
