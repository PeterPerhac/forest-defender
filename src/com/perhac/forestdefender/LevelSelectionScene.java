package com.perhac.forestdefender;

import static pulpcore.image.Colors.BLACK;

import java.util.ArrayList;

import pulpcore.Input;
import pulpcore.animation.Easing;
import pulpcore.animation.Timeline;
import pulpcore.animation.event.SceneChangeEvent;
import pulpcore.image.CoreFont;
import pulpcore.image.CoreImage;
import pulpcore.scene.Scene2D;
import pulpcore.sprite.*;

import com.perhac.forestdefender.game.GameLevel;
import com.perhac.forestdefender.game.GameModel;
import com.perhac.forestdefender.utils.ControlledSound;

public class LevelSelectionScene extends Scene2D {

    private static final int FADE_DURATION = 750;
    private Button backButton;
    private CoreFont yellowFont;
    private Group allElements;
    private ArrayList<GameLevelButton> levelButtons;
    private ControlledSound clickSound, enterButtonSound;
    private boolean playSound = true;

    @Override
    public void load() {
	clickSound = new ControlledSound("sfx/buttonClicked.wav");
	enterButtonSound = new ControlledSound("sfx/mouseEntered.wav");

	add(new FilledSprite(BLACK));
	yellowFont = CoreFont.load("info.font.png");

	allElements = new Group();
	backButton = Button.createLabeledButton("Back", 320, FADE_DURATION);
	backButton.setAnchor(0.5, 1);
	backButton.setKeyBinding(Input.KEY_ESCAPE);
	backButton.y.animate(550, 450, FADE_DURATION / 2, Easing.ELASTIC_OUT,
		FADE_DURATION);
	allElements.add(new ImageSprite("bg.jpg", 0, 0));
	allElements.add(backButton);
	addLevelButtons(allElements);
	allElements.alpha.set(0);

	addLayer(allElements);
	allElements.alpha.animateTo(255, FADE_DURATION);
    }

    private void addLevelButtons(Group layer) {
	levelButtons = new ArrayList<GameLevelButton>();
	CoreImage[] buttonImages = CoreImage.load("LevelButton.png")
		.split(1, 3);
	int i = 0;
	for (GameLevel level : GameModel.levels) {
	    int y = 140 + ((buttonImages[0].getHeight() + 10) * i);
	    GameLevelButton glb = new GameLevelButton(buttonImages, 0, y, level);
	    layer.add(glb);
	    Label buttonLabel = new Label(yellowFont, level.getName(), 320,
		    y + 10);
	    buttonLabel.setAnchor(0.5, 0);
	    buttonLabel.enabled.set(false);
	    layer.add(buttonLabel);

	    levelButtons.add(glb);
	    i++;
	}
	addLevelDescriptionLabels(layer);
    }

    private void addLevelDescriptionLabels(Group layer) {
	for (GameLevelButton glb : levelButtons) {
	    layer.add(glb.descriptionLabel);
	}
    }

    @Override
    public void update(int elapsedTime) {
	checkForMouseHover();
	if (backButton.isClicked()) {
	    new ControlledSound("sfx/backButtonClicked.wav").play();
	    addEvent(new SceneChangeEvent(new TitleScene(), 0));
	}
    }

    private void checkForMouseHover() {
	for (GameLevelButton glb : levelButtons) {
	    if (glb.isMouseHover()) {
		playSound();
		return;
	    }
	}
	playSound = true;
    }

    private void playSound() {
	if (playSound) {
	    enterButtonSound.play();
	    playSound = false;
	}
    }

    private class GameLevelButton extends Button {

	private GameLevel gameLevel;
	private Group descriptionLabel;

	public GameLevelButton(CoreImage[] arg0, int x, int y,
		GameLevel gameLevel) {
	    super(arg0, x, y);
	    this.gameLevel = gameLevel;
	    descriptionLabel = Label.createMultilineLabel(yellowFont, gameLevel
		    .getDescription(), 0, 0, 480);
	    descriptionLabel.alpha.set(0);
	    descriptionLabel.setAnchor(0.25, -0.25);
	    FilledSprite blackDrapes = new FilledSprite(BLACK);
	    blackDrapes.alpha.set(150);
	    blackDrapes.setSize(descriptionLabel.width.get(),
		    descriptionLabel.height.get());
	    descriptionLabel.add(0, blackDrapes);
	}

	@Override
	public void update(int elapsedTime) {
	    super.update(elapsedTime);
	    if (isClicked()) {
		clickSound.play();
		Timeline t = new Timeline();
		t.animateTo(allElements.alpha, 0, FADE_DURATION);
		t.addEvent(new SceneChangeEvent(new ForestDefender(gameLevel),
			FADE_DURATION));
		addTimeline(t);
	    } else {
		displayDescription();
	    }
	}

	private void displayDescription() {
	    if (isMouseOver()) {
		descriptionLabel.setLocation(Input.getMouseX(), Input
			.getMouseY());
		if (descriptionLabel.alpha.get() == 0) {
		    descriptionLabel.alpha.animateTo(255, 500);
		}
	    } else {
		if (descriptionLabel.alpha.get() > 0) {
		    descriptionLabel.alpha.set(0);
		}
	    }
	}
    }

}
