package com.perhac.forestdefender.game.upgrades;

import java.util.ArrayList;

import pulpcore.Input;
import pulpcore.image.CoreImage;
import pulpcore.sprite.Button;
import pulpcore.sprite.Sprite;

import com.perhac.forestdefender.ForestDefender.UnitInfoBubble;
import com.perhac.forestdefender.game.panels.InformationPanel;

public class UpgradeButtonFactory {

    public enum UpgradeButtonType {
	TOWER_STRENGTH_SPEED, TOWER_STRENGTH_RANGE, TOWER_SPEED_RANGE, TRAP_STRENGTH, TRAP_SLOW, TRAP_CHANCE
    }

    private static final UpgradeMultipliers towerStrengthSpeedMultipliers = UpgradeMultipliers
	    .getTowerUpgradeMultipliers(1.5, 0.8, 1.0, 2.0);
    private static final UpgradeMultipliers towerStrengthRangeMultipliers = UpgradeMultipliers
	    .getTowerUpgradeMultipliers(1.5, 1.0, 1.2, 2.0);
    private static final UpgradeMultipliers towerSpeedRangeMultipliers = UpgradeMultipliers
	    .getTowerUpgradeMultipliers(1.0, 0.6, 1.15, 2.0);

    private static final UpgradeMultipliers trapStrengthMultipliers = UpgradeMultipliers
	    .getTrapUpgradeMultipliers(1.5, 1.0, 1.0, 1.0, 1.5);
    private final UpgradeMultipliers trapSlowMultipliers = UpgradeMultipliers
	    .getTrapUpgradeMultipliers(1.0, 1.15, 1.0, 1.0, 1.5);
    private final UpgradeMultipliers trapChanceMultipliers = UpgradeMultipliers
	    .getTrapUpgradeMultipliers(1.0, 1.0, 1.0, 1.15, 1.5);

    private static UpgradeButtonFactory instance = null;

    private UpgradeButtonFactory() {
    }

    public static UpgradeButtonFactory getInstance() {
	if (instance == null) {
	    instance = new UpgradeButtonFactory();
	}
	return instance;
    }

    public Sprite getButton(UpgradeButtonType buttonType,
	    InformationPanel panel, UnitInfoBubble infoBubble) {
	switch (buttonType) {
	case TOWER_STRENGTH_SPEED: {
	    return new UnitUpgradeButton(
		    "upgradeButtons/tower-strength-speed.png", 270, panel,
		    infoBubble, "Strength (&Speed)",
		    towerStrengthSpeedMultipliers);
	}
	case TOWER_STRENGTH_RANGE: {
	    return new UnitUpgradeButton(
		    "upgradeButtons/tower-strength-range.png", 300, panel,
		    infoBubble, "Strength (&Range)",
		    towerStrengthRangeMultipliers);
	}
	case TOWER_SPEED_RANGE: {
	    return new UnitUpgradeButton(
		    "upgradeButtons/tower-speed-range.png", 330, panel,
		    infoBubble, "Speed (&Range)", towerSpeedRangeMultipliers);
	}
	case TRAP_STRENGTH: {
	    return new UnitUpgradeButton("upgradeButtons/trap-strength.png",
		    270, panel, infoBubble, "Strength", trapStrengthMultipliers);
	}
	case TRAP_SLOW: {
	    return new UnitUpgradeButton("upgradeButtons/trap-slow.png", 300,
		    panel, infoBubble, "Slow", trapSlowMultipliers);
	}
	case TRAP_CHANCE: {
	    return new UnitUpgradeButton("upgradeButtons/trap-chance.png", 330,
		    panel, infoBubble, "Chance", trapChanceMultipliers);
	}
	default:
	    return null;
	}

    }

    public class UnitUpgradeButton extends Button {
	private final InformationPanel panel;
	private final UpgradeMultipliers multipliers;
	private final UnitInfoBubble infoBubble;
	private String name;
	private ArrayList<String> info = new ArrayList<String>(2);

	public UnitUpgradeButton(String assetName, int y,
		InformationPanel panel, UnitInfoBubble infoBubble, String name,
		UpgradeMultipliers multipliers) {
	    super(CoreImage.load(assetName).split(1, 3), 620, y);
	    setAnchor(1, 0);
	    setPixelLevelChecks(false);
	    this.panel = panel;
	    this.multipliers = multipliers;
	    this.infoBubble = infoBubble;
	    this.name = name;
	}

	private ArrayList<String> getInfo() {
	    ArrayList<String> al = new ArrayList<String>(3);
	    al.add(this.name);
	    al.add("(upgrade)");
	    al.add("Cost: " + panel.getSelectedUnitCost());
	    return al;
	}

	public void update(int elapsedTime) {
	    super.update(elapsedTime);
	    if (isClicked()) {
		panel.upgradeSelectedUnit(multipliers);
	    } else {
		if (isMouseOver()) {
		    info = getInfo();
		    infoBubble
			    .setLocation(Input.getMouseX(), Input.getMouseY());
		    infoBubble.showUnitInfo(info);
		} else {
		    if (infoBubble.isShowing(info)) {
			infoBubble.hide();
		    }
		}
	    }
	}
    }

}
