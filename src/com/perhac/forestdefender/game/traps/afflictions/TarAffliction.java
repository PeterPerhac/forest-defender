package com.perhac.forestdefender.game.traps.afflictions;

import com.perhac.forestdefender.game.monsters.MonsterState;

public class TarAffliction extends Affliction {

    private boolean harmedOnce = false;

    public TarAffliction(AfflictionDescription ad) {
	super(ad.base, ad.type, ad.weakness);
    }

    @Override
    protected void updateMonsterState(MonsterState monsterState, double damage,
	    double slow) {
	if (!harmedOnce) {
	    if (slow > 0.90) {
		slow = 0.90;
	    }
	    monsterState.setSpeed(monsterState.getSpeed() * (1 - slow));
	    harmedOnce = true;
	}
    }

}
