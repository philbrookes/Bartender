package com.philthi.bartender;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by phil on 14/10/17.
 */

interface Touchable {
    public boolean Activate(Order order, ArrayList<Customer> waitingCustomers);
}

class Bar {
    public BarLayout layout;

    public Bar(BarLayout kl) {
        layout = kl;
    }

    public static Bar load(String json) {
        BarLayout layout = new Gson().fromJson(json, BarLayout.class);
        Bar bar = new Bar(layout);
        return bar;
    }

    static class BarEntity implements Touchable{
        public String type;
        public String texture;
        public ArrayList<BarEntityPosition> positions;
        public boolean Activate(Order order, ArrayList<Customer> waitingCustomers){
            Gdx.app.log("BARTENDER", "Activating for type: '" + type + "'");
            if (type.equals("beer") || type.equals("fruit") || type.equals("cider") || type.equals("spirits") || type.equals("juice") ){
                Gdx.app.log("BARTENDER", "adding ingredient");
                if(order.hasIngredient(Ingredient.Factory("clean_glass"))) {
                    order.addIngredient(Ingredient.Factory(type));
                }
            }
            else if(type.equals("dish_washer")) {
                Gdx.app.log("BARTENDER", "cleaning glass");
                if(order.removeIngredient(Ingredient.Factory("dirty_glass"))){
                    order.addIngredient(Ingredient.Factory("clean_glass"));
                }
            }
            else if(type.equals("table")) {
                Gdx.app.log("BARTENDER", "collecting dirty glass");
                if(order.isEmpty()) {
                    order.addIngredient(Ingredient.Factory("dirty_glass"));
                }
            }
            else if(type.equals("bin")) {
                Gdx.app.log("BARTENDER", "dumping order");
                order.reset();
            }
            else if(type.equals("glass")) {
                Gdx.app.log("BARTENDER", "Adding clean glass to order");
                order.addIngredient(Ingredient.Factory("clean_glass"));
            }

            for(Ingredient i: order.ingredients) {
                Gdx.app.log("BARTENDER", "order: " + i.name);
            }

            return true;
        }
    }

    static class BarEntityPosition {
        public int x;
        public int y;
    }

    static class BarLayout {
        public ArrayList<BarEntity> entities;
    }
}
