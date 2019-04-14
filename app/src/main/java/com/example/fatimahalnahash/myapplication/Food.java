package com.example.fatimahalnahash.myapplication;

import java.util.ArrayList;

public class Food {
    ArrayList<String> ingredients = new ArrayList<>();

    public Food() {
    }

    public Food(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public void addIngredients(String s){
        ingredients.add(s);
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public boolean hasIngredients(String target){
        if(ingredients.contains(target)){
            return true;
        }else{
            return false;
        }
    }
}
