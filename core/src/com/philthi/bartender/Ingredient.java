package com.philthi.bartender;

/**
 * Created by phil on 14/10/17.
 */

public class Ingredient {
    public String name;

    public static Ingredient Factory(String type) {
        Ingredient ing = new Ingredient();

        ing.name = type;

        return ing;
    }
}

