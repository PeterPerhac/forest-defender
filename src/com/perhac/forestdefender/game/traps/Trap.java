package com.perhac.forestdefender.game.traps;

import java.util.Random;

import pulpcore.image.CoreImage;

import com.perhac.forestdefender.ForestDefender;
import com.perhac.forestdefender.game.GameModel;
import com.perhac.forestdefender.game.GameUnit;
import com.perhac.forestdefender.game.ObservableUnitState;
import com.perhac.forestdefender.game.monsters.MonsterSprite;
import com.perhac.forestdefender.game.traps.afflictions.AfflictionFactory;
import com.perhac.forestdefender.utils.ControlledSound;

public class Trap extends GameUnit {

    protected TrapBase base;
    private TrapType type;
    private final ControlledSound afflictionSound;
    private ForestDefender game;

    protected static final Random random = new Random();

    public Trap(TrapType trapType, ForestDefender game) {
	super(CoreImage.load(trapType.toString() + ".png"), -100, -100);
	this.type = trapType;
	this.game = game;
	base = GameModel.getTrapBase(trapType);
	afflictionSound = new ControlledSound("sfx/afflict-"
		+ trapType.toString() + ".wav", 2);
    }

    public enum TrapType {
	ICE, TAR, WATER, POISON, DEATH;

	@Override
	public String toString() {
	    return super.toString().toLowerCase();
	}

    }

    @Override
    public int getPrice() {
	return base.getCost();
    }

    public String getType() {
	return type.toString();
    }

    public void afflictMonster(MonsterSprite monster) {
	if (base.getChance() > 99
		|| (chanceHit(monster.hasWeakness(type.toString())))) {
	    doAfflictMonster(monster);
	}
    }

    private boolean chanceHit(boolean weak) {
	int randomNumber = random.nextInt(100);
	double chanceMultiplier = 1.0;
	if (weak) {
	    chanceMultiplier *= 1.5;
	}
	int chanceToAfflict = (int) Math.round(base.getChance()
		* chanceMultiplier);
	return randomNumber <= chanceToAfflict;
    }

    private void doAfflictMonster(MonsterSprite monster) {
	afflictionSound.play();
	monster.setAffliction(AfflictionFactory.getAffliction(base, type,
		monster.hasWeakness(type.toString())));
    }

    public boolean steppedOn(MonsterSprite monsterSprite) {
	return monsterSprite.contains(x.getAsInt(), y.getAsInt());
    }

    @Override
    public void update(int elapsedTime) {
	super.update(ForestDefender.SPEED_MULTIPLIER * elapsedTime);
	if (isMousePressed()) {
	    game.setInfoPanel(ForestDefender.trapInfoPanel, this);
	}
    }

    @Override
    public CoreImage getUnitImage() {
	return CoreImage.load(type.toString() + ".png");
    }

    @Override
    public String getName() {
	return base.getName() + " (lv. " + base.getUpgradeLevel() + ")";
    }

    @Override
    public ObservableUnitState getObservableState() {
	return base;
    }

}
