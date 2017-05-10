package com.perhac.forestdefender.game.panels;

import java.util.Observable;

import pulpcore.sprite.Label;

import com.perhac.forestdefender.ForestDefender;
import com.perhac.forestdefender.game.traps.TrapBase;
import com.perhac.forestdefender.game.upgrades.UpgradeButtonFactory;
import com.perhac.forestdefender.game.upgrades.UpgradeButtonFactory.UpgradeButtonType;

public class TrapInfoPanel extends InformationPanel {

    private static final int LABEL_NAME = 0;
    private static final int LABEL_STRENGTH = 1;
    private static final int LABEL_SLOW = 2;
    private static final int LABEL_DURATION = 3;
    private static final int LABEL_CHANCE = 4;

    private Label[] trapStateLabel;

    public TrapInfoPanel(ForestDefender game) {
	super(game);
	addStatusLabels();
	addUpgradeButtons();
    }

    private void addStatusLabels() {
	trapStateLabel = new Label[5];
	trapStateLabel[LABEL_NAME] = new Label(font, "%s", 500, 250);
	trapStateLabel[LABEL_STRENGTH] = new Label(font, "Strength: %d", 500,
		270);
	trapStateLabel[LABEL_SLOW] = new Label(font, "Slow: %d%", 500, 290);
	trapStateLabel[LABEL_DURATION] = new Label(font, "Duration: %fs", 500,
		310);
	trapStateLabel[LABEL_CHANCE] = new Label(font, "Chance: %d%", 500, 330);
	for (Label l : trapStateLabel) {
	    add(l);
	}
    }

    private void addUpgradeButtons() {
	UpgradeButtonFactory upgradeButtonFactory = UpgradeButtonFactory
		.getInstance();
	add(upgradeButtonFactory.getButton(UpgradeButtonType.TRAP_STRENGTH,
		this, game.unitInfoBubble));
	add(upgradeButtonFactory.getButton(UpgradeButtonType.TRAP_SLOW, this,
		game.unitInfoBubble));
	add(upgradeButtonFactory.getButton(UpgradeButtonType.TRAP_CHANCE, this,
		game.unitInfoBubble));
    }

    @Override
    public void update(Observable unitState, Object arg) {
	trapStateLabel[LABEL_NAME].setFormatArg(unit.getName());
	TrapBase tb = (TrapBase) unitState;
	trapStateLabel[LABEL_STRENGTH].setFormatArg(tb.getStrength());
	trapStateLabel[LABEL_SLOW].setFormatArg((int) (tb.getSlow() * 100));
	trapStateLabel[LABEL_DURATION].setFormatArg((double) tb
		.getEffectDuration() / 1000);
	trapStateLabel[LABEL_CHANCE].setFormatArg(tb.getChance());
    }

    @Override
    protected void initialiseUnitState(Observable o) {
	update(o, null);
    }

}
