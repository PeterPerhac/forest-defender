package com.perhac.forestdefender.game.traps.afflictions;

import com.perhac.forestdefender.game.monsters.MonsterState;

public class IceAffliction extends Affliction {

    public IceAffliction(AfflictionDescription ad) {
	super(ad.base, ad.type, ad.weakness);
    }

    @Override
    protected void updateMonsterState(MonsterState monsterState, double damage,
	    double slow) {
	monsterState.setSpeed(0);
    }

}
