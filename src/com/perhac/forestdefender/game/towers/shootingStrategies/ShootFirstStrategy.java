package com.perhac.forestdefender.game.towers.shootingStrategies;

import java.util.List;

import com.perhac.forestdefender.game.monsters.MonsterSprite;

public class ShootFirstStrategy extends ShootingStrategy {

    @Override
    public MonsterSprite pickMonster(List<MonsterSprite> monstersInRange) {
	MonsterSprite first = monstersInRange.get(0);
	for (MonsterSprite monster : monstersInRange){
	    if (monster.getPosition()>first.getPosition()){
		first = monster;
	    }
	}
	return first;
    }

}
