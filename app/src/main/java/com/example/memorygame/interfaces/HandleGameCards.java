package com.example.memorygame.interfaces;

public interface HandleGameCards {
    public void updateShuffle(int curShuffle);
    public void updateTurns(int turns);
    public void updateStage(int stage);
    public void handleGameover();
    public void victoryManager(int score);
    public void closeInventory();
    public void cancelShuffle();
    public void updateDoubleCoins();

}
