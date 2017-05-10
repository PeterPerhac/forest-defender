package com.perhac.forestdefender.game;

import java.util.Observable;

import com.perhac.forestdefender.utils.Stopwatch;

public class Player extends Observable {

    private static Player instance;
    private int money = 0;
    private static int lives = 0;
    private int startLives = 0;
    private int kills = 0;

    private int numberOfPurchases = 0;

    private final Stopwatch stopwatch;
    private static boolean won = false;

    private Player() {
	stopwatch = new Stopwatch();
	won = false;
    }
    
    public void win(){
	won = true;
    }

    /**
     * Called when a monster makes it to the forest.
     * 
     * @return true if the player is killed (no more lives)
     */
    public boolean hurt() {
	if (lives > 0) {
	    lives--;
	}
	setChanged();
	return (lives < 1);
    }

    /**
     * Bill any purchases to the player
     * 
     * @param price
     * @return true when enough funds for purchase. Money is deducted.
     */
    public boolean buy(int price) {
	if (money - price < 0) {
	    return false;
	}
	numberOfPurchases++;
	money -= price;
	setChanged();
	return true;
    }

    /**
     * When a monster is killed, reward money is cashed in to player
     * 
     * @param reward
     *            amount of money credited
     */
    public void cashIn(int reward) {
	kills++;
	money += reward;
	setChanged();
    }

    public int getMoney() {
	return money;
    }

    public int getLives() {
	return lives;
    }

    @Override
    protected void setChanged() {
	super.setChanged();
	notifyObservers();
    }

    public static Player getInstance() {
	if (instance == null) {
	    instance = new Player();
	}
	return instance;
    }

    public void setGameLevel(GameLevel gameLevel) {
	money = gameLevel.getMoney();
	startLives = gameLevel.getLives();
	lives = gameLevel.getLives();
	kills = 0;
	won = false;
	numberOfPurchases = 0;
	stopwatch.start();
	setChanged();
    }

    public int getKills() {
	return kills;
    }

    public int getScore() {
	return (int) (((double) lives / startLives) * 10000) + money
		+ numberOfPurchases;
    }

    public String getTimeWasted() {
	return stopwatch.toString();
    }

    public void pause() {
	stopwatch.pause();
    }

    public void resume() {
	stopwatch.resume();
    }

    public int getSeconds() {
	return (int) stopwatch.getMillis() / 1000;
    }

    public static String getGameStatus(String def) {
	String gameStatus = def;
	if (won) {
	    gameStatus = "win";
	} else {
	    if (lives > 0) { // not loss
		gameStatus = def;
	    } else {
		gameStatus = "loss";
	    }
	}
	return gameStatus;
    }


}
