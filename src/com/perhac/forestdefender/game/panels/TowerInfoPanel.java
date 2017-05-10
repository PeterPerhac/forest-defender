package com.perhac.forestdefender.game.panels;

import java.text.NumberFormat;
import java.util.Observable;

import pulpcore.sprite.Label;

import com.perhac.forestdefender.ForestDefender;
import com.perhac.forestdefender.game.panels.ShootingOptionSelector.ShootingOption;
import com.perhac.forestdefender.game.towers.Tower;
import com.perhac.forestdefender.game.towers.TowerBase;
import com.perhac.forestdefender.game.upgrades.UpgradeButtonFactory;
import com.perhac.forestdefender.game.upgrades.UpgradeButtonFactory.UpgradeButtonType;

public class TowerInfoPanel extends InformationPanel {

    private static final int LABEL_NAME = 0;
    private static final int LABEL_STRENGTH = 1;
    private static final int LABEL_RANGE = 2;
    private static final int LABEL_SPEED = 3;

    private Label[] towerStateLabel;
    private ShootingOptionSelector shootingOptions;

    public TowerInfoPanel(ForestDefender game) {
	super(game);
	addStatusLabels();
	addUpgradeButtons();
	addShootingOptionSelector();
    }

    private void addUpgradeButtons() {
	UpgradeButtonFactory upgradeButtonFactory = UpgradeButtonFactory
		.getInstance();
	add(upgradeButtonFactory.getButton(
		UpgradeButtonType.TOWER_STRENGTH_SPEED, this,
		game.unitInfoBubble));
	add(upgradeButtonFactory.getButton(
		UpgradeButtonType.TOWER_STRENGTH_RANGE, this,
		game.unitInfoBubble));
	add(upgradeButtonFactory.getButton(UpgradeButtonType.TOWER_SPEED_RANGE,
		this, game.unitInfoBubble));
    }

    private void addShootingOptionSelector() {
	shootingOptions = new ShootingOptionSelector(this, game.unitInfoBubble, 500, 330);
	add(shootingOptions);
    }

    private void addStatusLabels() {
	towerStateLabel = new Label[4];
	towerStateLabel[LABEL_NAME] = new Label(font, "%s", 500, 250);
	towerStateLabel[LABEL_STRENGTH] = new Label(font, "Strength: %d", 500,
		270);
	towerStateLabel[LABEL_SPEED] = new Label(font, "Speed: %s sps", 500,
		290);
	towerStateLabel[LABEL_RANGE] = new Label(font, "Range: %d", 500, 310);
	for (Label l : towerStateLabel) {
	    add(l);
	}
    }

    @Override
    public void update(Observable unitState, Object arg) {
	towerStateLabel[LABEL_NAME].setFormatArg(unit.getName());

	TowerBase tb = (TowerBase) unitState;
	towerStateLabel[LABEL_STRENGTH].setFormatArg(tb.getStrength());
	NumberFormat nf = NumberFormat.getNumberInstance();
	nf.setMaximumFractionDigits(2);
	towerStateLabel[LABEL_SPEED].setFormatArg(nf.format((double) 1000
		/ tb.getSpeed()));
	towerStateLabel[LABEL_RANGE].setFormatArg(tb.getRange());
	shootingOptions
		.showShootingOption(((Tower) unit).getShootingStrategy());
	game.showTowerRange((Tower) unit);
    }

    @Override
    protected void initialiseUnitState(Observable o) {
	update(o, null);
    }

    public void changeUnitsShootingStrategy(ShootingOption shootingOption) {
	((Tower) unit).setShootingStrategy(shootingOption);
    }
}
