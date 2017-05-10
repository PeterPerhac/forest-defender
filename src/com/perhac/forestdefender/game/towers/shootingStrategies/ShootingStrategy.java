package com.perhac.forestdefender.game.towers.shootingStrategies;

import java.util.List;

import com.perhac.forestdefender.game.monsters.MonsterSprite;

public abstract class ShootingStrategy {

    private static final ShootFirstStrategy shootFirstStrategy = new ShootFirstStrategy();
    private static final ShootLastStrategy shootLastStrategy = new ShootLastStrategy();
    private static final ShootStrongestStrategy shootStrongestStrategy = new ShootStrongestStrategy();
    private static final ShootWeakestStrategy shootWeakestStrategy = new ShootWeakestStrategy();

    public static ShootingStrategy first() {
	return shootFirstStrategy;
    }

    public static ShootingStrategy last() {
	return shootLastStrategy;
    }

    public static ShootingStrategy strongest() {
	return shootStrongestStrategy;
    }

    public static ShootingStrategy weakest() {
	return shootWeakestStrategy;
    }

    public abstract MonsterSprite pickMonster(List<MonsterSprite> array);

}