package com.perhac.forestdefender.game.traps.afflictions;

import com.perhac.forestdefender.game.traps.TrapBase;
import com.perhac.forestdefender.game.traps.Trap.TrapType;

public class AfflictionDescription {
    public TrapBase base;
    public TrapType type;
    public boolean weakness;

    AfflictionDescription(TrapBase tb, TrapType tp, boolean w) {
	this.base = tb;
	this.type = tp;
	this.weakness = w;
    }
}
