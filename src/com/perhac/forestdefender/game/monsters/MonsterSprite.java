package com.perhac.forestdefender.game.monsters;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pulpcore.image.CoreImage;
import pulpcore.sprite.FilledSprite;
import pulpcore.sprite.Group;

import com.perhac.forestdefender.ForestDefender;
import com.perhac.forestdefender.game.GameUnit;
import com.perhac.forestdefender.game.GameWave;
import com.perhac.forestdefender.game.ObservableUnitState;
import com.perhac.forestdefender.game.traps.Trap;
import com.perhac.forestdefender.game.traps.afflictions.Affliction;
import com.perhac.forestdefender.utils.ControlledSound;

public class MonsterSprite extends GameUnit {

    private static final Dimension HEALTH_BAR_SIZE = new Dimension(20, 5);
    private static final double PI_HALF = Math.PI / 2;

    public Group healthIndicator;
    private double position;
    public MonsterState monsterState;

    private GameWave wave;

    private int activateAt;

    private FilledSprite healthBar, healthRectangle;
    private ArrayList<Affliction> afflictions = new ArrayList<Affliction>(2);
    private List<Trap> trapsSteppedOn = new ArrayList<Trap>();

    private final ControlledSound monsterEscapedSound = new ControlledSound(
	    "sfx/MonsterEscaped.wav");
    private final ControlledSound monsterDead = new ControlledSound(
	    "sfx/MonsterDead.wav");

    private ForestDefender game;
    private boolean hasComeOut;
    private int time;

    public MonsterSprite(GameWave wave, ForestDefender gameScene,
	    int activationTime) {
	super(CoreImage.load("monster/" + wave.monsterFile + ".png"), -500,
		-500);
	setPixelLevelChecks(false);
	game = gameScene;
	this.wave = wave;

	monsterState = new MonsterState(wave.hitpoints, wave.speed, false);
	setupHealthIndicator();

	time = 0;
	position = 0;

	hasComeOut = false;
	activateAt = activationTime;
	visible.set(false);

	x.set(game.path.getX(position));
	y.set(game.path.getY(position));
    }

    @Override
    public CoreImage getUnitImage() {
	return CoreImage.load("monster/" + wave.monsterFile + ".png");
    }

    private void setupHealthIndicator() {
	healthRectangle = new FilledSprite(pulpcore.image.Colors.BLACK);
	healthRectangle.setSize(HEALTH_BAR_SIZE.width, HEALTH_BAR_SIZE.height);

	healthBar = new FilledSprite(pulpcore.image.Colors.ORANGE);
	healthBar.setSize(HEALTH_BAR_SIZE.width, HEALTH_BAR_SIZE.height);

	double Y_OFFSET = 5;

	healthIndicator = new Group();
	healthIndicator.setSize(HEALTH_BAR_SIZE.width, HEALTH_BAR_SIZE.height);
	healthIndicator.add(healthRectangle);
	healthIndicator.add(healthBar);

	healthIndicator.setAnchor(0.5, Y_OFFSET);
	healthIndicator.bindLocationTo(this);
    }

    @Override
    public void update(int elapsedTime) {
	int et = ForestDefender.SPEED_MULTIPLIER * elapsedTime;
	tryToUnleashMonster(et);
	if (!monsterState.getActive()) {
	    return;
	}
	if (isMousePressed()) {
	    game.setInfoPanel(ForestDefender.monsterInfoPanel, this);
	}
	updatePosition(et);
	updateTrapAfflictions(et);
	if (monsterState.getHealth() <= 0 || position >= 1.00) {
	    deactivate();
	    return;
	}
	checkForTraps();
	updateHealthBar();
	super.update(et);
    }

    private void tryToUnleashMonster(int et) {
	if (!hasComeOut) {
	    time += et;
	    if (time >= activateAt) {
		activate();
	    }
	}
    }

    private void checkForTraps() {
	for (Trap t : game.traps) {
	    if (t.steppedOn(this)) {
		if (!trapsSteppedOn.contains(t)) {
		    fallIntoATrap(t);
		    trapsSteppedOn.add(t);
		}
	    } else {
		// re-activate upon leaving the trap
		trapsSteppedOn.remove(t);
	    }
	}
    }

    private void fallIntoATrap(Trap t) {
	String ttype = t.getType();
	if (!isAlreadyAfflictedByTrapType(ttype)
		&& !wave.resistance.contains(ttype)) {
	    t.afflictMonster(this);
	}
    }

    private boolean isAlreadyAfflictedByTrapType(String ttype) {
	for (Affliction a : afflictions) {
	    if (a.getType().equals(ttype)) {
		a.reset();
		return true;
	    }
	}
	return false;
    }

    private void updateTrapAfflictions(int elapsedTime) {
	Iterator<Affliction> it = afflictions.iterator();
	while (it.hasNext()) {
	    Affliction a = it.next();
	    a.afflict(monsterState, elapsedTime);
	    if (a.isDone()) {
		if (a.affectsSpeed()) {
		    monsterState.setSpeed(wave.speed);
		}
		it.remove();
	    }
	}
    }

    private void updatePosition(int elapsedTime) {
	position += ((double) elapsedTime / 1000)
		* (monsterState.getSpeed() / 100);
	setLocation(game.path.getX(position), game.path.getY(position));
	rotateWithMovement(elapsedTime);
    }

    private void updateHealthBar() {
	double percentHealth = (monsterState.getHealth() / wave.hitpoints);
	healthBar.setSize(percentHealth * HEALTH_BAR_SIZE.width,
		HEALTH_BAR_SIZE.height);
	healthIndicator.setDirty(true);
    }

    private boolean doDeactivate() {
	if (monsterState.getHealth() > 0) { // monster gets through
	    monsterEscapedSound.play();
	    if (game.player.hurt()) {
		game.defeat();
	    }
	    if (wave.recurrent) {
		position = 0;
		return false;
	    }
	} else { // player kills a sucker
	    monsterDead.play();
	    game.player.cashIn(wave.reward);
	}
	return true;
    }

    public void deactivate() {
	if (doDeactivate()) {
	    monsterState.setActive(false);
	    // check for winning situation
	    if (game.isLastMonster()) {
		if (game.isLastWave()) {
		    game.win();
		    return;
		} else {
		    game.endOfWave();
		}
	    }
	    game.removeMonster(this);
	}
    }

    public void activate() {
	hasComeOut = true;
	monsterState.setActive(true);
	visible.set(true);
    }

    private void rotateWithMovement(int elapsedTime) {
	double dx = game.path.getX(position + 0.01) - x.get();
	double dy = game.path.getY(position + 0.01) - y.get();
	double a = 0.000;
	if (dx == 0) {
	    a = (dy >= 0) ? PI_HALF : -PI_HALF;
	} else {
	    a = Math.atan(dy / dx);
	    if (dx < 0) {
		a += Math.PI;
	    }
	}
	angle.set(a);
    }

    public void setAffliction(Affliction affliction) {
	afflictions.add(affliction);
    }

    public boolean hasWeakness(String trapType) {
	return wave.weakness.contains(trapType);
    }

    public boolean isAlive() {
	return monsterState.getHealth() >= 0;
    }

    public void hurt(int strength) {
	if (isAlive()) {
	    monsterState.setHealth(monsterState.getHealth() - strength);
	}
    }

    public GameWave getWave() {
	return wave;
    }

    @Override
    public String getName() {
	return wave.name;
    }

    @Override
    public ObservableUnitState getObservableState() {
	return monsterState;
    }

    @Override
    public int getPrice() {
	return 0;
    }

    public double getHealth() {
	return monsterState.getHealth();
    }

    public double getPosition() {
	return position;
    }

}
