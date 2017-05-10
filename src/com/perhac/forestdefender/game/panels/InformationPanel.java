package com.perhac.forestdefender.game.panels;

import java.util.Observable;
import java.util.Observer;

import pulpcore.Input;
import pulpcore.image.CoreFont;
import pulpcore.image.CoreImage;
import pulpcore.sprite.Button;
import pulpcore.sprite.Group;
import pulpcore.sprite.ImageSprite;
import pulpcore.sprite.Sprite;

import com.perhac.forestdefender.ForestDefender;
import com.perhac.forestdefender.game.GameUnit;
import com.perhac.forestdefender.game.ObservableUnitState;
import com.perhac.forestdefender.game.upgrades.UpgradeMultipliers;
import com.perhac.forestdefender.utils.ControlledSound;

public abstract class InformationPanel extends Group implements Observer {

    private static final String TAG_OLD_SPRITE = "oldSprite";
    protected static final CoreFont font = CoreFont.load("white.font.png");
    protected ObservableUnitState observableUnitState = null;
    protected ForestDefender game;
    protected GameUnit unit;

    private final ControlledSound invalidAction = new ControlledSound(
	    "sfx/invalidAction.wav", 4);
    private final ControlledSound upgradeSound = new ControlledSound(
	    "sfx/upgrade.wav", 2);

    public InformationPanel(ForestDefender game) {
	this.game = game;
	if (!(this instanceof LevelInfoPanel)) {
	    CloseInfoPanelButton b = new CloseInfoPanelButton();
	    add(b);
	}
    }

    private void showUnitImage(GameUnit unit) {
	Sprite s = findWithTag(TAG_OLD_SPRITE);
	if (s != null) {
	    s.removeFromParent();
	}
	ImageSprite monsterImageSprite = new ImageSprite(unit.getUnitImage(),
		520, 220);
	monsterImageSprite.setTag(TAG_OLD_SPRITE);
	add(monsterImageSprite);
    }

    public void observeUnit(GameUnit unit) {
	if (observableUnitState != null) {
	    observableUnitState.deleteObservers();
	}
	observableUnitState = unit.getObservableState();
	observableUnitState.addObserver(this);
	this.unit = unit;
	showUnitImage(unit);
	initialiseUnitState(unit.getObservableState());
    }

    protected abstract void initialiseUnitState(Observable o);

    private class CloseInfoPanelButton extends Button {

	private CloseInfoPanelButton() {
	    super(CoreImage.load("closeInfoPanel.png").split(1, 3), 610, 200);
	    setAnchor(1, 0);
	    setPixelLevelChecks(false);
	    setKeyBinding(Input.KEY_W);
	}

	@Override
	public void update(int elapsedTime) {
	    if (isClicked()) {
		if (observableUnitState != null) {
		    observableUnitState.deleteObservers();
		}
		game.setInfoPanel(ForestDefender.levelInfoPanel, null);
	    }
	    super.update(elapsedTime);
	}
    }

    public void upgradeSelectedUnit(UpgradeMultipliers multipliers) {
	if (observableUnitState.doApply(multipliers)) {
	    if (game.player.buy(observableUnitState.getCost())) {
		upgradeSound.play();
		observableUnitState.upgrade(multipliers);
	    } else {
		ForestDefender.levelInfoPanel.showLowCash();
	    }
	} else {
	    invalidAction.play();
	}
    }

    public String getSelectedUnitCost() {
	return "" + observableUnitState.getCost();
    }

}
