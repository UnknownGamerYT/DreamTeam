package com.example.dreamteam.game;

import android.provider.MediaStore;

import java.util.Arrays;
import java.util.List;

public class Card {


    /* Variables */

    private final List<Integer> images;
    public List<Float> rotations;
    public List<Float> sizes;
    private  int[] textureArray;



    /* Constructor */

    public Card(List<Integer> images) {
        this.images = images;
    }


    /* Methods */

    public boolean contains(int s) {
        for (int Image : images) {
            if (Image == s) {
                return true;
            }
        }
        return false;
    }




    public List<Integer> getimages() {
        return images;
    }

    @Override
    public String toString() {
        return "Card->{" +
                "images=" + images.toString() +
                "}";
    }

    public String toFormattedString() {
        String s = "Card->{\n\t\tsymbols=\n";
        for (int image : images) {
            s = s.concat(String.format("\t\t\t%s\n", image));
        }
        return s.concat("\t\t}");
    }





}
