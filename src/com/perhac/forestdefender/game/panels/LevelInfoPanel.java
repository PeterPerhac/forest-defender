package com.perhac.forestdefender.game.panels;

import java.util.Observable;

import pulpcore.animation.Easing;
import pulpcore.animation.Timeline;
import pulpcore.image.CoreFont;
import pulpcore.scene.Scene2D;
import pulpcore.sprite.Label;

import com.perhac.forestdefender.ForestDefender;
import com.perhac.forestdefender.game.GameLevel;
import com.perhac.forestdefender.game.GameWave;
import com.perhac.forestdefender.utils.ControlledSound;

public class LevelInfoPanel extends InformationPanel {

    private static final int GSL_WAVE_NUMBER = 0;
    private static final int GSL_CURR_WAVE_NAME = 1;
    private static final int GSL_CURR_WAVE_SW = 2;
    private static final int GSL_NEXT_WAVE_NAME = 3;
    private static final int GSL_NEXT_WAVE_SW = 4;
    private static final int GSL_MONEY = 5;
    private static final int GSL_LIVES = 6;

    private Label[] gameStateLabel;
    private RWDisplay rwDisplay_curr, rwDisplay_next;
    private int waveCount;

    private static final ControlledSound lowCash = new ControlledSound(
	    "sfx/lowCash.wav");

    private static final CoreFont font = CoreFont.load("white.font.png");

    public LevelInfoPanel(ForestDefender game, int waveCount) {
	super(game);
	this.waveCount = waveCount;
	addStatusLabels();

	rwDisplay_curr = new RWDisplay(font, 500, 260);
	rwDisplay_next = new RWDisplay(font, 500, 310);
	add(rwDisplay_curr);
	add(rwDisplay_next);
    }

    private void addStatusLabels() {
	gameStateLabel = new Label[7];
	gameStateLabel[GSL_WAVE_NUMBER] = new Label(font, "Attack wave %d / "
		+ waveCount, 500, 200);
	gameStateLabel[GSL_CURR_WAVE_NAME] = new Label(font, "%s", 500, 220);
	gameStateLabel[GSL_CURR_WAVE_SW] = new Label(font, "HP: %d; Count: %d",
		500, 240);
	gameStateLabel[GSL_NEXT_WAVE_NAME] = new Label(font, "NEXT: %s", 500,
		280);
	gameStateLabel[GSL_NEXT_WAVE_SW] = new Label(font, "HP: %d; Count: %d",
		500, 295);
	gameStateLabel[GSL_MONEY] = new Label(font, "Money: %d", 500, 320);
	gameStateLabel[GSL_LIVES] = new Label(font, "Lives: %d", 500, 340);
	for (Label l : gameStateLabel) {
	    add(l);
	}
    }

    public void updateWaveLabels(GameLevel gameLevel, int waveNumber,
	    int monstersAlive) {
	GameWave currentWave = gameLevel.getWave(waveNumber);
	GameWave nextWave = gameLevel.getWave(waveNumber + 1);
	gameStateLabel[GSL_WAVE_NUMBER].setFormatArg(waveNumber);
	gameStateLabel[GSL_CURR_WAVE_NAME].setFormatArg(currentWave.name);
	gameStateLabel[GSL_NEXT_WAVE_NAME].setFormatArg(nextWave.name);
	gameStateLabel[GSL_CURR_WAVE_SW].setFormatArgs(new Object[] { 0, 0 });
	gameStateLabel[GSL_CURR_WAVE_SW].setFormatArgs(new Object[] {
		currentWave.hitpoints, monstersAlive });
	rwDisplay_curr.update(currentWave);
	gameStateLabel[GSL_NEXT_WAVE_SW].setFormatArgs(new Object[] {
		nextWave.hitpoints, nextWave.count });
	rwDisplay_next.update(nextWave);
    }

    public void updatePlayerStatLabels() {
	gameStateLabel[GSL_MONEY].setFormatArg(game.player.getMoney());
	gameStateLabel[GSL_LIVES].setFormatArg(game.player.getLives());
    }

    public void showLowCash() {
	lowCash.play();
	game.setInfoPanel(ForestDefender.levelInfoPanel, null);
	updatePlayerStatLabels();
	Label l = gameStateLabel[GSL_MONEY];
	double labelWidth = l.width.get();
	double labelHeight = l.height.get();
	Timeline t = new Timeline();
	t.scale(l, labelWidth, labelHeight, 2 * labelWidth, 2 * labelHeight,
		500);
	t.scale(l, 2 * labelWidth, 2 * labelHeight, labelWidth, labelHeight,
		500, Easing.NONE, 500);
	Scene2D scene = getScene2D();
	scene.addTimeline(t);
    }

    @Override
    public void update(Observable o, Object arg) {
	updatePlayerStatLabels();
    }

    @Override
    protected void initialiseUnitState(Observable o) {
	update(o, null);
    }

}
