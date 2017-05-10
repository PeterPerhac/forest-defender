package com.perhac.forestdefender.game.towers;

import org.jdom.Element;

import com.perhac.forestdefender.game.ObservableUnitState;
import com.perhac.forestdefender.game.upgrades.UpgradeMultipliers;

public class TowerBase extends ObservableUnitState implements Cloneable {

    private static final int MAX_UPGRADE_LEVEL = 10;
    private int cost, speed, strength, range, upgradeLevel;
    private String name;

    public TowerBase(Element tower) {
	name = tower.getChildText("name");
	cost = Integer.parseInt(tower.getChildText("cost"));
	speed = Integer.parseInt(tower.getChildText("speed"));
	strength = Integer.parseInt(tower.getChildText("strength"));
	range = Integer.parseInt(tower.getChildText("range"));
	upgradeLevel = 1;
    }

    private TowerBase(TowerBase towerBase) {
	name = towerBase.name;
	cost = towerBase.cost;
	speed = towerBase.speed;
	strength = towerBase.strength;
	range = towerBase.range;
	upgradeLevel = towerBase.upgradeLevel;
    }

    public int getCost() {
	return cost;
    }

    public double getSpeed() {
	return speed;
    }

    public int getStrength() {
	return strength;
    }

    public int getRange() {
	return range;
    }

    public String getName() {
	return name;
    }

    @Override
    public TowerBase clone() {
	return new TowerBase(this);
    }

    public int getUpgradeLevel() {
	return upgradeLevel;
    }

    public void upgrade(UpgradeMultipliers multipliers) {
	range *= multipliers.get(UpgradeMultipliers.KEY_RANGE);
	speed *= multipliers.get(UpgradeMultipliers.KEY_SPEED);
	strength *= multipliers.get(UpgradeMultipliers.KEY_STRENGTH);
	cost *= multipliers.get(UpgradeMultipliers.KEY_COST);
	upgradeLevel++;
	setChanged();
	notifyObservers();
    }

    @Override
    public int getChance() {
	return 0;
    }

    @Override
    public int getEffectDuration() {
	return 0;
    }

    @Override
    public double getSlow() {
	return 0;
    }

    @Override
    public boolean doApply(UpgradeMultipliers multipliers) {
	return upgradeLevel<MAX_UPGRADE_LEVEL;
    }

}
