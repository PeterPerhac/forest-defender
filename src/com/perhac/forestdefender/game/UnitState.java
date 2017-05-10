package com.perhac.forestdefender.game;

import com.perhac.forestdefender.game.upgrades.UpgradeMultipliers;

public interface UnitState {
    
    int getCost();
    int getStrength();
    
    double getSpeed();
    int getRange();
    
    double getSlow();    
    int getEffectDuration();
    int getChance();

    void upgrade(UpgradeMultipliers multipliers);
}
