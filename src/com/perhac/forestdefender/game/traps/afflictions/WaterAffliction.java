package com.perhac.forestdefender.game.traps.afflictions;

import com.perhac.forestdefender.game.monsters.MonsterState;

public class WaterAffliction extends Affliction {
    private boolean harmedOnce = false;

    public WaterAffliction(AfflictionDescription ad) {
	super(ad.base, ad.type, ad.weakness);
    }

    @Override
    protected void updateMonsterState(MonsterState monsterState, double damage,
	    double slow) {
	monsterState.setHealth(monsterState.getHealth() - damage);
	if (!harmedOnce) {
	    if (slow > 0.99) {
		slow = 0.99;
	    }
	    monsterState.setSpeed(monsterState.getSpeed() * (1 - slow));
	    harmedOnce = true;
	}
    }

}
