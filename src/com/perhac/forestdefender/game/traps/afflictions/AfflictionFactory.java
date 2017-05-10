package com.perhac.forestdefender.game.traps.afflictions;

import com.perhac.forestdefender.game.traps.TrapBase;
import com.perhac.forestdefender.game.traps.Trap.TrapType;

public class AfflictionFactory {

    public static Affliction getAffliction(TrapBase base, TrapType type,
	    boolean weaknessHit) {
	Affliction a = null;
	AfflictionDescription ad = new AfflictionDescription(base, type,
		weaknessHit);
	switch (type) {
	case ICE: {
	    a = new IceAffliction(ad);
	    break;
	}
	case WATER: {
	    a = new WaterAffliction(ad);
	    break;
	}
	case TAR: {
	    a = new TarAffliction(ad);
	    break;
	}
	case POISON: {
	    a = new PoisonAffliction(ad);
	    break;
	}
	case DEATH: {
	    a = new DeathAffliction(ad);
	    break;
	}
	}
	return a;
    }
}
