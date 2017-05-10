package com.perhac.forestdefender.game.panels;

import static pulpcore.image.Colors.BLACK;
import static pulpcore.image.Colors.BLUE;
import static pulpcore.image.Colors.CYAN;
import static pulpcore.image.Colors.GREEN;
import static pulpcore.image.Colors.LIGHTGRAY;
import static pulpcore.image.Colors.RED;
import pulpcore.image.CoreFont;
import pulpcore.image.filter.Glow;
import pulpcore.sprite.FilledSprite;
import pulpcore.sprite.Group;
import pulpcore.sprite.Label;
import pulpcore.sprite.Sprite;

import com.perhac.forestdefender.game.GameWave;
import com.perhac.forestdefender.game.traps.Trap.TrapType;

public class RWDisplay extends Group {

    private Group resistances;
    private Group weaknesses;
    private final Glow GLOW = new Glow(2);

    private CoreFont font;

    public RWDisplay(CoreFont font, int x, int y) {
	this.font = font;

	resistances = setupDisplay("R", TrapType.values());
	resistances.setLocation(x, y);

	weaknesses = setupDisplay("W", TrapType.values());
	weaknesses.setLocation(x + 65, y);

	add(resistances);
	add(weaknesses);
    }

    public void update(GameWave wave) {
	hideAll(resistances);
	for (String s : wave.resistance) {
	    unhideSprite(resistances.findWithTag(s));
	}
	hideAll(weaknesses);
	for (String s : wave.weakness) {
	    unhideSprite(weaknesses.findWithTag(s));
	}
    }

    private void unhideSprite(Sprite sprite) {
	sprite.visible.set(true);
    }

    private Group setupDisplay(String rw, TrapType[] tags) {
	Group g = new Group();
	FilledSprite lightBG = new FilledSprite(LIGHTGRAY);
	lightBG.setSize(55, 10);
	lightBG.alpha.set(128);
	g.add(lightBG);
	g.add(new Label(font, rw, 0, -2));
	int[] colors = new int[] { CYAN, BLACK, BLUE, GREEN, RED };
	for (int i = 0; i < colors.length; i++) {
	    FilledSprite fs = new FilledSprite(colors[i]);
	    fs.setSize(4, 4);
	    fs.setLocation(20 + (i * 8), 3);
	    fs.setFilter(GLOW);
	    // tag it, so can be looked up
	    fs.setTag(tags[i].toString());
	    g.add(fs);
	}
	hideAll(g);
	return g;
    }

    private void hideAll(Group g) {
	// start from 2:
	// 0 is the background rect
	// 1 is the R/W label respectively
	for (int i = 2; i < g.getNumSprites(); i++) {
	    hideSprite(g.get(i));
	}
    }

    private void hideSprite(Sprite sprite) {
	sprite.visible.set(false);
    }

}