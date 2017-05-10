package com.perhac.forestdefender.game.traps;

import org.jdom.Element;

import com.perhac.forestdefender.game.ObservableUnitState;
import com.perhac.forestdefender.game.upgrades.UpgradeMultipliers;

public class TrapBase extends ObservableUnitState implements Cloneable {

    private int cost, effectDuration, chance, strength, upgradeLevel;
    private double slow;
    private String name;

    public TrapBase(Element trap) {
	name = trap.getChildText("name");
	cost = Integer.parseInt(trap.getChildText("cost"));
	effectDuration = Integer.parseInt(trap.getChildText("effect-duration"));
	chance = Integer.parseInt(trap.getChildText("chance"));
	strength = Integer.parseInt(trap.getChildText("strength"));
	slow = (double) Integer.parseInt(trap.getChildText("slow")) / 100;
	upgradeLevel = 1;
    }

    private TrapBase(TrapBase trapBase) {
	name = trapBase.name;
	cost = trapBase.cost;
	effectDuration = trapBase.effectDuration;
	chance = trapBase.chance;
	strength = trapBase.strength;
	slow = trapBase.slow;
	upgradeLevel = trapBase.upgradeLevel;
    }

    public int getCost() {
	return cost;
    }

    public int getEffectDuration() {
	return effectDuration;
    }

    public int getChance() {
	return chance;
    }

    public int getStrength() {
	return strength;
    }

    public double getSlow() {
	return slow;
    }

    public String getName() {
	return name;
    }

    @Override
    public TrapBase clone() {
	return new TrapBase(this);
    }

    public int getUpgradeLevel() {
	return upgradeLevel;
    }

    public void upgrade(UpgradeMultipliers multipliers) {
	slow *= multipliers.get(UpgradeMultipliers.KEY_SLOW);
	chance *= multipliers.get(UpgradeMultipliers.KEY_CHANCE);
	effectDuration *= multipliers
		.get(UpgradeMultipliers.KEY_EFFECT_DURATION);
	strength *= multipliers.get(UpgradeMultipliers.KEY_STRENGTH);
	cost *= multipliers.get(UpgradeMultipliers.KEY_COST);
	upgradeLevel++;
	setChanged();
	notifyObservers();
    }

    @Override
    public int getRange() {
	return 0;
    }

    @Override
    public double getSpeed() {
	return 0;
    }

    @Override
    public boolean doApply(UpgradeMultipliers multipliers) {
	// here goes the logic! woo hoo!
	if (multipliers.get(UpgradeMultipliers.KEY_CHANCE) > 1.0) {
	    // in case of a chance upgrade, current chance must be less than 100
	    return (chance < 100);
	}

	if (multipliers.get(UpgradeMultipliers.KEY_STRENGTH) > 1.0) {
	    return (strength < 10000 && strength > 0);
	}

	if (multipliers.get(UpgradeMultipliers.KEY_SLOW) > 1.0) {
	    return (slow < 0.7 && slow > 0);
	}
	return false;
    }
}
