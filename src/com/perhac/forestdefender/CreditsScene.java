package com.perhac.forestdefender;

import static pulpcore.image.Colors.BLACK;
import pulpcore.Input;
import pulpcore.Stage;
import pulpcore.animation.Easing;
import pulpcore.image.CoreFont;
import pulpcore.scene.Scene2D;
import pulpcore.sprite.*;

import com.perhac.forestdefender.utils.ControlledSound;

public class CreditsScene extends Scene2D {

    private static final int FADE_IN_DURATION = 2000;
    private Button backButton;
    private static final CoreFont font = CoreFont.load("green.font.png");
    private ControlledSound buttonClickedSound;

    @Override
    public void load() {
	buttonClickedSound = new ControlledSound("sfx/backButtonClicked.wav");
	createBackButton();
	add(new FilledSprite(BLACK));

	Group contentsLayer = new Group();
	Label poweredByLabel = new Label(font,
		"powered by PulpCore, jOrbis, jDOM", 630, 470);
	poweredByLabel.setAnchor(1, 1);

	contentsLayer.add(new ImageSprite("Credits.jpg", 0, 0));
	contentsLayer.add(poweredByLabel);
	contentsLayer.add(backButton);
	contentsLayer.alpha.set(0);

	addLayer(contentsLayer);
	contentsLayer.alpha.animate(0, 255, FADE_IN_DURATION,
		Easing.REGULAR_OUT);
    }

    private void createBackButton() {
	backButton = Button.createLabeledButton("Back", 320, 520);
	backButton.setAnchor(0.5, 1);
	backButton.setKeyBinding(Input.KEY_ESCAPE);
	backButton.y.animateTo(470, FADE_IN_DURATION / 2, Easing.ELASTIC_OUT,
		FADE_IN_DURATION);
    }

    @Override
    public void update(int elapsedTime) {
	if (backButton.isClicked()) {
	    buttonClickedSound.play();
	    Stage.popScene();
	}
    }
}
