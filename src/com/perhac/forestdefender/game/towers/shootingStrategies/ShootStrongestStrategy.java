package com.perhac.forestdefender.game.towers.shootingStrategies;

import java.util.List;

import com.perhac.forestdefender.game.monsters.MonsterSprite;

public class ShootStrongestStrategy extends ShootingStrategy {

    @Override
    public MonsterSprite pickMonster(List<MonsterSprite> monstersInRange) {
	MonsterSprite strongest = monstersInRange.get(0);
	for (MonsterSprite monster : monstersInRange) {
	    if (monster.getHealth() > strongest.getHealth()) {
		strongest = monster;
	    }
	}
	return strongest;
    }
}
