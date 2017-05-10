package com.perhac.forestdefender.game.traps.afflictions;

import com.perhac.forestdefender.game.monsters.MonsterState;

public class DeathAffliction extends Affliction {

    public DeathAffliction(AfflictionDescription ad) {
	super(ad.base, ad.type, ad.weakness);
    }

    @Override
    protected void updateMonsterState(MonsterState monsterState, double damage,
	    double slow) {
	monsterState.setHealth(-1);
    }

}
