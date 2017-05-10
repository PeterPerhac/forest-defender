package com.perhac.forestdefender.game.panels;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import com.perhac.forestdefender.ForestDefender.UnitInfoBubble;

import pulpcore.Input;
import pulpcore.sprite.Group;
import pulpcore.sprite.ImageSprite;

public class ShootingOptionSelector extends Group {

    public enum ShootingOption {
	FIRST, LAST, STRONGEST, WEAKEST
    }

    private final ImageSprite selectedOptionImage;
    private final TowerInfoPanel panel;

    public ShootingOptionSelector(TowerInfoPanel towerInfoPanel,
	    UnitInfoBubble infoBubble, int x, int y) {
	super(x, y);
	this.panel = towerInfoPanel;
	this.add(new OptionSelector(0, 0, infoBubble));
	selectedOptionImage = new ImageSprite("range-circle.png", 0, 0);
	selectedOptionImage.setSize(20, 20);
	selectedOptionImage.setAnchor(0, 0);
	this.add(selectedOptionImage);
    }

    public void setShootingOption(ShootingOption option) {
	panel.changeUnitsShootingStrategy(option);
	showShootingOption(option);
    }

    private class OptionSelector extends ImageSprite {

	private final Map<Integer, ShootingOption> xToOptionMap;
	private final UnitInfoBubble bubble;
	private ArrayList<String> info;

	public OptionSelector(int x, int y, UnitInfoBubble bubble) {
	    super("shooting-options.png", x, y);
	    setPixelLevelChecks(false);
	    xToOptionMap = new TreeMap<Integer, ShootingOption>();
	    xToOptionMap.put(60, ShootingOption.WEAKEST);
	    xToOptionMap.put(40, ShootingOption.STRONGEST);
	    xToOptionMap.put(20, ShootingOption.LAST);
	    xToOptionMap.put(0, ShootingOption.FIRST);
	    this.bubble = bubble;
	}

	public void update(int elapsedTime) {
	    super.update(elapsedTime);
	    int x = (int) getLocalX(Input.getMouseX(), Input.getMouseY());
	    x = (x / 20) * 20;
	    if (isMousePressed()) {
		setShootingOption(xToOptionMap.get(x));
	    } else {
		if (isMouseOver()) {
		    info = getInfo(x);
		    bubble.setLocation(Input.getMouseX(), Input.getMouseY());
		    bubble.showUnitInfo(info);
		} else {
		    if (bubble.isShowing(info)) {
			bubble.hide();
		    }
		}
	    }
	}

	private ArrayList<String> getInfo(int x) {
	    ArrayList<String> al = new ArrayList<String>(3);
	    al.add("STRATEGY");
	    al.add(String.format("Attack %s ", xToOptionMap.get(x)
		    .toString()));
	    al.add("creeps in range");
	    return al;
	}
    }

    public void showShootingOption(ShootingOption shootingStrategy) {
	int x = 0;
	switch (shootingStrategy) {
	case FIRST:
	    x = 0;
	    break;
	case LAST:
	    x = 20;
	    break;
	case STRONGEST:
	    x = 40;
	    break;
	case WEAKEST:
	    x = 60;
	    break;
	}
	selectedOptionImage.x.animateTo(getParent().x.get() + x, 300);
    }

}
