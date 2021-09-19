package com.example.memorygame.model;

public class Buyable {
    private String name;
    private int picResId;
    private int maxAmount;
    private int type;
    private int price;
    private int id;
    private int amount;

    public Buyable(String name, int picResId,int type,int price,int id,int amount) {
        this.name = name;
        this.picResId = picResId;
        this.type = type;
        this.price = price;
        this.id = id;
        this.amount = amount;

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPicResId() {
        return picResId;
    }

    public void setPicResId(int picResId) {
        this.picResId = picResId;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
