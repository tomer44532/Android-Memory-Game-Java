package com.example.memorygame.model;

import android.content.Context;

import com.example.memorygame.model.Buyable;
import com.google.gson.annotations.SerializedName;

public class InventorySlot  {
    @SerializedName("slot")
    private Buyable buyable;
    private int ItemCount;

    public InventorySlot(Context context) {
        //super(context);
        ItemCount = 0;
    }



    public Buyable getBuyable() {
        return buyable;
    }

    public void setBuyable(Buyable buyable) {
        this.buyable = buyable;
    }

    public int getItemCount() {
        return ItemCount;
    }

    public void setItemCount(int itemCount) {
        ItemCount = itemCount;
    }
}
