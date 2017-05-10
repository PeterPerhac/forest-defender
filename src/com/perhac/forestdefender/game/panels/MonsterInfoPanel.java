package com.perhac.forestdefender.game.panels;

import java.util.Observable;

import pulpcore.sprite.Label;

import com.perhac.forestdefender.ForestDefender;
import com.perhac.forestdefender.game.GameUnit;
import com.perhac.forestdefender.game.GameWave;
import com.perhac.forestdefender.game.monsters.MonsterState;

public class MonsterInfoPanel extends InformationPanel {

    private static final int LABEL_NAME = 0;
    private static final int LABEL_HITPOINTS = 1;
    private static final int LABEL_SPEED = 2;

    private Label[] monsterStateLabel;
    private RWDisplay rwDisplay;

    public MonsterInfoPanel(ForestDefender game) {
	super(game);
	addStatusLabels();
	rwDisplay = new RWDisplay(font, 500, 320);
	add(rwDisplay);
    }

    private void addStatusLabels() {
	monsterStateLabel = new Label[3];
	monsterStateLabel[LABEL_NAME] = new Label(font, "%s", 500, 250);
	monsterStateLabel[LABEL_HITPOINTS] = new Label(font, "HP: %d", 500, 270);
	monsterStateLabel[LABEL_SPEED] = new Label(font, "Speed: %d", 500, 290);
	for (Label l : monsterStateLabel) {
	    add(l);
	}
    }

    public void observeUnit(GameUnit unit, GameWave wave) {
	super.observeUnit(unit);
	rwDisplay.update(wave);
    }

    @Override
    public void update(Observable monsterState, Object arg) {
	MonsterState ms = (MonsterState) monsterState;
	if (ms.getHealth() <= 0 || !ms.getActive()) {
	    ms.deleteObservers();
	    game.hideMonsterInfoPanel();
	    return;
	}
	monsterStateLabel[LABEL_NAME].setFormatArg(unit.getName());

	monsterStateLabel[LABEL_HITPOINTS].setFormatArg((int) ms.getHealth());
	monsterStateLabel[LABEL_SPEED].setFormatArg((int) ms.getSpeed());
    }

    @Override
    protected void initialiseUnitState(Observable o) {
	update(o, null);
    }

}
