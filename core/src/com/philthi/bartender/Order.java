package com.philthi.bartender;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;

import sun.rmi.server.InactiveGroupException;

/**
 * Created by phil on 14/10/17.
 */

public class Order {
    public ArrayList<Ingredient> ingredients;

    public Order () {
        ingredients = new ArrayList<Ingredient>();
    }

    public void reset(){
        ingredients.clear();
    }

    public boolean isEmpty(){
        return ingredients.size() == 0;
    }

    public boolean equals(Order o) {
        if(ingredients.size() != o.ingredients.size()){
            return false;
        }

        for(Ingredient i: ingredients){
            if(!o.hasIngredient(i)){
                return false;
            }
        }

        return true;
    }

    public boolean hasIngredient(Ingredient ing) {
        for (Ingredient i: ingredients) {
            if(i.name.equals(ing.name)) {
                return true;
            }
        }
        return false;
    }

    public String getType() {
        if(hasIngredient(Ingredient.Factory("clean_glass")) && ingredients.size() == 1) {
            return "order_clean_glass";
        }
        if(hasIngredient(Ingredient.Factory("dirty_glass")) && ingredients.size() == 1) {
            return "order_dirty_glass";
        }
        if(hasIngredient(Ingredient.Factory("clean_glass")) && ingredients.size() >= 2) {
            if(hasIngredient(Ingredient.Factory("beer")) && ingredients.size() == 2) {
                return "order_beer";
            }
            if(hasIngredient(Ingredient.Factory("cider")) && ingredients.size() == 2) {
                return "order_cider";
            }
            if(hasIngredient(Ingredient.Factory("spirits"))){
                if(ingredients.size() == 2) {
                    return "order_spirits";
                }
                if(hasIngredient(Ingredient.Factory("fruit"))){
                    if (ingredients.size() == 3) {
                        return "order_fruit_spirits";
                    }
                    if(hasIngredient(Ingredient.Factory("juice"))){
                        if(ingredients.size() == 4) {
                            return "order_spirits_juice_fruit";
                        }
                    }
                }
                if(hasIngredient(Ingredient.Factory("juice")) && ingredients.size() == 3){
                    return "order_spirits_juice";
                }
            }
            if(hasIngredient(Ingredient.Factory("juice"))) {
                if(ingredients.size() == 2) {
                    return "order_juice";
                }
                if(hasIngredient(Ingredient.Factory("fruit")) && ingredients.size() == 3){
                    return "order_fruit_juice";
                }
            }
            if(hasIngredient(Ingredient.Factory("fruit"))) {
                if(ingredients.size() == 2) {
                    return "order_fruit";
                }
            }
        }
        return "order_unknown";
    }

    public boolean addIngredient(Ingredient ing) {
        Gdx.app.log("BARTENDER", "adding ingredient: " + ing.name);
        if (hasIngredient(ing)){
            Gdx.app.log("BARTENDER", "failed");
            return false;
        }
        ingredients.add(ing);
        Gdx.app.log("BARTENDER", "succeeded");
        return true;
    }

    public boolean removeIngredient(Ingredient ing){
        for (Ingredient i: ingredients) {
            if(i.name == ing.name) {
                ingredients.remove(i);
                return true;
            }
        }
        return false;
    }
}
