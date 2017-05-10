package com.perhac.forestdefender.game.traps.afflictions;

import com.perhac.forestdefender.game.monsters.MonsterState;
import com.perhac.forestdefender.game.traps.TrapBase;
import com.perhac.forestdefender.game.traps.Trap.TrapType;

public abstract class Affliction {

    protected static final double WEAKNESS_DAMAGE_MULTIPLIER = 1.5;
    private int time;
    private int duration;
    private int strength;
    private double slow;
    private TrapType trapType;
    protected boolean weakness;

    public Affliction(TrapBase base, TrapType trapType, boolean weakness) {
	this.time = 0;
	this.duration = base.getEffectDuration();
	this.strength = base.getStrength();
	this.slow = base.getSlow();
	this.weakness = weakness;
	this.trapType = trapType;
    }

    public void reset() {
	time = 0;
    }

    public void afflict(MonsterState monsterState, int elapsedTime) {
	time += elapsedTime;
	double damage = 0;
	if (duration == 0) {
	    damage = strength;
	} else {
	    damage = ((double) elapsedTime / duration) * strength;
	}
	damage *= (weakness) ? WEAKNESS_DAMAGE_MULTIPLIER : 1.0;
	double slow = this.slow;
	slow *= (weakness) ? WEAKNESS_DAMAGE_MULTIPLIER : 1.0;
	updateMonsterState(monsterState, damage, slow);
    }

    protected abstract void updateMonsterState(MonsterState monsterState,
	    double damage, double slow);

    public boolean isDone() {
	return time >= duration;
    }

    public boolean affectsSpeed() {
	return (slow > 0.0);
    }

    public TrapType getType() {
	return trapType;
    }
}
