package com.perhac.forestdefender.game.towers;

import java.util.ArrayList;
import java.util.List;

import pulpcore.image.CoreImage;

import com.perhac.forestdefender.ForestDefender;
import com.perhac.forestdefender.game.GameModel;
import com.perhac.forestdefender.game.GameUnit;
import com.perhac.forestdefender.game.ObservableUnitState;
import com.perhac.forestdefender.game.monsters.MonsterSprite;
import com.perhac.forestdefender.game.panels.ShootingOptionSelector.ShootingOption;
import com.perhac.forestdefender.game.towers.shootingStrategies.ShootingStrategy;
import com.perhac.forestdefender.game.towers.shots.ShotSprite;
import com.perhac.forestdefender.utils.ControlledSound;

public class Tower extends GameUnit {

    private TowerBase base;
    private TowerType type;
    private ForestDefender game;
    private int time;
    private ControlledSound shotSound;
    private boolean active;
    private ShootingStrategy shootingStrategy;
    private ShootingOption shootingOption;

    public Tower(TowerType towerType, ForestDefender game) {
	super(CoreImage.load(towerType.toString() + "-tower.png"), -100, -100);
	this.type = towerType;
	this.game = game;
	base = GameModel.getTowerBase(towerType);
	shotSound = new ControlledSound("sfx/shot.wav", 1);
	// make it capable of firing right away!
	this.time = (int) base.getSpeed();
	this.active = true;
	this.setShootingStrategy(ShootingOption.FIRST);
    }

    public enum TowerType {
	FAST, STRONG;

	public String toString() {
	    return super.toString().toLowerCase();
	}
    }

    @Override
    public int getPrice() {
	return base.getCost();
    }

    public int getRange() {
	return base.getRange();
    }

    public int getStrength() {
	return base.getStrength();
    }

    public TowerType getType() {
	return type;
    }

    public void hitMonster(MonsterSprite monster) {
	// keep any left-over time, good for 3x accel. gameplay
	time -= base.getSpeed();

	if (monster != null) {
	    shotSound.play();
	    game.shotsLayer.add(new ShotSprite(this, monster));
	}
    }

    public boolean inRange(MonsterSprite monsterSprite) {
	double distance = Math.hypot((monsterSprite.x.get() - x.get()),
		(monsterSprite.y.get() - y.get()));
	return (int) distance <= base.getRange();
    }

    public void deactivate() {
	this.active = false;
    }

    @Override
    public void update(int elapsedTime) {
	super.update(ForestDefender.SPEED_MULTIPLIER * elapsedTime);
	if (!active) {
	    return;
	}
	if (isMouseReleased()) {
	    game.setInfoPanel(ForestDefender.towerInfoPanel, this);
	}
	shootAtMonsters(elapsedTime);
    }

    private void shootAtMonsters(int elapsedTime) {
	time += (ForestDefender.SPEED_MULTIPLIER * elapsedTime);
	if (time >= base.getSpeed()) { // ready to fire
	    List<MonsterSprite> monstersInRange = getMonstersInRange();
	    if (monstersInRange.size() > 0) {
		int shotsToFire = (int) (time / base.getSpeed());
		for (int i = 0; i < shotsToFire; i++) {
		    hitMonster(shootingStrategy.pickMonster(monstersInRange));
		}
	    } else {
		// this is to prevent idle towers from building up strength
		time = (int) base.getSpeed();
	    }
	}
    }

    private List<MonsterSprite> getMonstersInRange() {
	List<MonsterSprite> monstersInRange = new ArrayList<MonsterSprite>(10);
	for (MonsterSprite monster : game.monsters) {
	    if (inRange(monster)) {
		monstersInRange.add(monster);
	    }
	}
	return monstersInRange;
    }

    public String getName() {
	return base.getName() + " (lv. " + base.getUpgradeLevel() + ")";
    }

    public CoreImage getUnitImage() {
	return CoreImage.load(type.toString() + "-tower.png");
    }

    @Override
    public ObservableUnitState getObservableState() {
	return base;
    }

    public void setShootingStrategy(ShootingOption shootingOption) {
	this.shootingOption = shootingOption;
	switch (shootingOption) {
	case FIRST: {
	    shootingStrategy = ShootingStrategy.first();
	    break;
	}
	case LAST: {
	    shootingStrategy = ShootingStrategy.last();
	    break;
	}
	case WEAKEST: {
	    shootingStrategy = ShootingStrategy.weakest();
	    break;
	}
	case STRONGEST: {
	    shootingStrategy = ShootingStrategy.strongest();
	    break;
	}
	default:
	    shootingStrategy = ShootingStrategy.first();
	}
    }

    public ShootingOption getShootingStrategy() {
	return this.shootingOption;
    }

}
