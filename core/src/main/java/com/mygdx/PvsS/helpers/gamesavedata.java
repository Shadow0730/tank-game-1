package com.mygdx.PvsS.helpers;

public class gamesavedata {
    public Player1Data player1;
    public Player2Data player2;
    public int currentPlayerIndex;

    public static class Player1Data {
        public float posX;
        public float posY;
        public int health;
    }

    public static class Player2Data {
        public float posX;
        public float posY;
        public int health;
    }
}
