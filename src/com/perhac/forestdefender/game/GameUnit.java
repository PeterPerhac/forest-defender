package com.perhac.forestdefender.game;

import pulpcore.image.CoreImage;
import pulpcore.sprite.ImageSprite;

public abstract class GameUnit extends ImageSprite {

    public GameUnit(CoreImage image, int x, int y) {
	super(image, x, y);
	super.setPixelLevelChecks(false);
    }

    public abstract int getPrice();

    public abstract ObservableUnitState getObservableState();

    public abstract CoreImage getUnitImage();

    public abstract String getName();

}
