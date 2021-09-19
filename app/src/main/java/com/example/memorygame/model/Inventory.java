package com.example.memorygame.model;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Inventory {
    @SerializedName("inventory")

    private ArrayList<InventorySlot> useables;


    public Inventory(Context context) {

        useables = new ArrayList<InventorySlot>(10);

        for(int i = 0; i < 10; i++)
        {
            useables.add(new InventorySlot(context));

        }

    }

    public ArrayList<InventorySlot> getUseables() {
        return useables;
    }

    public void setUseables(ArrayList<InventorySlot> useables) {
        this.useables = useables;
    }



}
