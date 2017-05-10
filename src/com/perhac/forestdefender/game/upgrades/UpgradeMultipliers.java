package com.perhac.forestdefender.game.upgrades;

import java.util.Hashtable;

public class UpgradeMultipliers {

    public static final String KEY_RANGE = "range";
    public static final String KEY_SPEED = "speed";
    public static final String KEY_STRENGTH = "strength";
    public static final String KEY_SLOW = "slow";
    public static final String KEY_CHANCE = "chance";
    public static final String KEY_EFFECT_DURATION = "duration";
    public static final String KEY_COST = "cost";

    private final Hashtable<String, Double> multipliers;

    private UpgradeMultipliers() {
	multipliers = new Hashtable<String, Double>(5);
    }

    public void addMultiplier(String key, Double value) {
	multipliers.put(key, value);
    }

    public double get(String key) {
	Double d = multipliers.get(key);
	return (d == null) ? 1.0 : d;
    }

    public static UpgradeMultipliers getTowerUpgradeMultipliers(
	    double strength, double speed, double range, double costMultiplier) {
	UpgradeMultipliers um = new UpgradeMultipliers();
	um.addMultiplier(KEY_RANGE, range);
	um.addMultiplier(KEY_STRENGTH, strength);
	um.addMultiplier(KEY_SPEED, speed);
	um.addMultiplier(KEY_COST, costMultiplier);
	return um;
    }

    public static UpgradeMultipliers getTrapUpgradeMultipliers(double strength,
	    double slow, double effectDuration, double chance, double costMultiplier) {
	UpgradeMultipliers um = new UpgradeMultipliers();
	um.addMultiplier(KEY_STRENGTH, strength);
	um.addMultiplier(KEY_SLOW, slow);
	um.addMultiplier(KEY_EFFECT_DURATION, effectDuration);
	um.addMultiplier(KEY_CHANCE, chance);
	um.addMultiplier(KEY_COST, costMultiplier);
	return um;
    }

}
