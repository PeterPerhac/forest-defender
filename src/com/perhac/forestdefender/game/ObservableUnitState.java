package com.perhac.forestdefender.game;

import java.util.Observable;

import com.perhac.forestdefender.game.upgrades.UpgradeMultipliers;

public abstract class ObservableUnitState extends Observable implements
	UnitState {

    @Override
    public int getChance() {
	return 0;
    }

    @Override
    public int getCost() {
	return 0;
    }

    @Override
    public int getEffectDuration() {
	return 0;
    }

    @Override
    public int getRange() {
	return 0;
    }

    @Override
    public double getSlow() {
	return 0;
    }

    @Override
    public double getSpeed() {
	return 0;
    }

    @Override
    public int getStrength() {
	return 0;
    }

    public abstract boolean doApply(UpgradeMultipliers multipliers);

}
