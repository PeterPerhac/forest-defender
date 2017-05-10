package com.perhac.forestdefender.game.monsters;

import com.perhac.forestdefender.game.ObservableUnitState;
import com.perhac.forestdefender.game.upgrades.UpgradeMultipliers;

public class MonsterState extends ObservableUnitState {

    public MonsterState(double health, double speed, boolean active) {
	this.setHealth(health);
	this.setSpeed(speed);
    }

    public void setHealth(double health) {
	this.health = health;
	setChanged();
	notifyObservers();
    }

    public double getHealth() {
	return health;
    }

    public void setSpeed(double speed) {
	this.speed = speed;
	setChanged();
	notifyObservers();
    }

    public double getSpeed() {
	return speed;
    }

    public void setActive(boolean active) {
	this.active = active;
	setChanged();
	notifyObservers();
    }

    public boolean getActive() {
	return active;
    }

    private double health;
    private double speed;
    private boolean active;

    @Override
    public void upgrade(UpgradeMultipliers multipliers) {
	// can't upgrade monsters :-)
    }

    @Override
    public boolean doApply(UpgradeMultipliers multipliers) {
	//can't apply any upgrade multipliers to monsters
	return false;
    }

}
