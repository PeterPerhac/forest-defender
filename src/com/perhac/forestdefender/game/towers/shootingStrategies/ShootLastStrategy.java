package com.perhac.forestdefender.game.towers.shootingStrategies;

import java.util.List;

import com.perhac.forestdefender.game.monsters.MonsterSprite;

public class ShootLastStrategy extends ShootingStrategy {

    @Override
    public MonsterSprite pickMonster(List<MonsterSprite> monstersInRange) {
	MonsterSprite last = monstersInRange.get(monstersInRange.size() - 1);
	for (MonsterSprite monster : monstersInRange) {
	    if (monster.getPosition() < last.getPosition()) {
		last = monster;
	    }
	}
	return last;
    }

}
