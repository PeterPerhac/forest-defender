package com.perhac.forestdefender.game.towers.shootingStrategies;

import java.util.List;

import com.perhac.forestdefender.game.monsters.MonsterSprite;

public class ShootWeakestStrategy extends ShootingStrategy {

    @Override
    public MonsterSprite pickMonster(List<MonsterSprite> monstersInRange) {
	MonsterSprite weakest = monstersInRange.get(0);
	for (MonsterSprite monster : monstersInRange) {
	    if (monster.getHealth() < weakest.getHealth()) {
		weakest = monster;
	    }
	}
	return weakest;
    }
}
