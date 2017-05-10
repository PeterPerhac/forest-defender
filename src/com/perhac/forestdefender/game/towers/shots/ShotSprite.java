package com.perhac.forestdefender.game.towers.shots;

import pulpcore.image.CoreImage;
import pulpcore.sprite.ImageSprite;

import com.perhac.forestdefender.ForestDefender;
import com.perhac.forestdefender.game.monsters.MonsterSprite;
import com.perhac.forestdefender.game.towers.Tower;
import com.perhac.forestdefender.game.towers.Tower.TowerType;

public class ShotSprite extends ImageSprite {

    private MonsterSprite target;

    private static final int SHOT_SPEED = 350; // pixels per second
    private int strength;
    private static CoreImage fastImage = CoreImage.load("fast-shot.png");
    private static CoreImage strongImage = CoreImage.load("strong-shot.png");

    public ShotSprite(Tower shooter, MonsterSprite target) {
	super((shooter.getType().equals(TowerType.FAST)) ? fastImage
		: strongImage, shooter.x.get(), shooter.y.get());
	this.target = target;
	this.setAnchor(0.5, 0.5);
	this.alpha.set(200);
	this.strength = shooter.getStrength();
    }

    @Override
    public void update(int elapsedTime) {
	if (target.isAlive() && !targetHit()) {
	    double pixelsToMove = (double) (ForestDefender.SPEED_MULTIPLIER
		    * elapsedTime * SHOT_SPEED) / 1000;
	    double dx = target.x.get() - x.get();
	    double dy = target.y.get() - y.get();
	    double totalDistance = Math.sqrt(dx * dx + dy * dy);
	    double fraction = pixelsToMove / totalDistance;
	    x.set(x.get() + (fraction * dx));
	    y.set(y.get() + (fraction * dy));
	    super.update(ForestDefender.SPEED_MULTIPLIER * elapsedTime);
	} else {
	    removeFromParent();
	}
    }

    private boolean targetHit() {
	boolean hit = target.contains(x.getAsInt(), y.getAsInt());
	if (hit) {
	    hit();
	}
	return hit;
    }

    private void hit() {
	target.hurt(strength);
    }

}
