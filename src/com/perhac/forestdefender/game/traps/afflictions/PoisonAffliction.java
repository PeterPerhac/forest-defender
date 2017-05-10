package com.perhac.forestdefender.game.traps.afflictions;

import com.perhac.forestdefender.game.monsters.MonsterState;

public class PoisonAffliction extends Affliction {

    public PoisonAffliction(AfflictionDescription ad) {
	super(ad.base, ad.type, ad.weakness);
    }

    @Override
    protected void updateMonsterState(MonsterState monsterState, double damage,
	    double slow) {
	monsterState.setHealth(monsterState.getHealth() - damage);
    }

}
