package com.example.memorygame.model;

import android.app.Activity;

import com.example.memorygame.R;
import com.example.memorygame.model.Buyable;

import java.util.ArrayList;

public class Shop {
    public static ArrayList<Buyable> createShop(Activity activity) {
        ArrayList<Buyable> buyablesArray = new ArrayList<>();
        buyablesArray.add(new Buyable(activity.getResources().getString(R.string.extra_life), R.drawable.extra_life, 0, 2000, 0, 1));
        buyablesArray.add(new Buyable(activity.getResources().getString(R.string.double_coins), R.drawable.coins_increase, 0, 1000, 1, 1));
        buyablesArray.add(new Buyable(activity.getResources().getString(R.string.cancel_shuffle), R.drawable.unshuffle, 0, 800, 3, 1));
        buyablesArray.add(new Buyable(activity.getResources().getString(R.string.skip_stage), R.drawable.skip_stage, 0, 5000, 4, 1));
        buyablesArray.add(new Buyable(activity.getResources().getString(R.string.extra_life), R.drawable.extra_life_stack, 0, 5000, 0, 3));
        buyablesArray.add(new Buyable(activity.getResources().getString(R.string.double_coins), R.drawable.coins_increase_stack, 0, 2500, 1, 3));
        buyablesArray.add(new Buyable(activity.getResources().getString(R.string.cancel_shuffle), R.drawable.unshuffle_stack, 0, 2000, 3, 3));
        buyablesArray.add(new Buyable(activity.getResources().getString(R.string.skip_stage), R.drawable.skip_stage_stack, 0, 12000, 4, 3));
        return  buyablesArray;
    }
}