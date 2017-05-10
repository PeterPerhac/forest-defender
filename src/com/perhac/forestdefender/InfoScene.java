package com.perhac.forestdefender;

import pulpcore.CoreSystem;
import pulpcore.Input;
import pulpcore.Stage;
import pulpcore.animation.Easing;
import pulpcore.image.CoreFont;
import pulpcore.scene.Scene2D;
import pulpcore.sprite.Button;
import pulpcore.sprite.Group;
import pulpcore.sprite.ImageSprite;
import pulpcore.sprite.Label;

import com.perhac.forestdefender.game.GameModel;
import com.perhac.forestdefender.utils.ControlledSound;

public class InfoScene extends Scene2D {

    public static final String STRATEGY_GUIDE_URL = "http://www.perhac.com/shared/forest-defender/guide/index.php";
    private static final int FADE_IN_DURATION = 750;
    private String[] infoTexts;
    private Button backButton, strategyGuideButton;
    private CoreFont yellowFont, smallFont;
    private Group allElements;
    private Group[] infoLayer;
    private Label navigationInfo;
    private ControlledSound nextScreenSound;

    private int currentScreen = 0;

    @Override
    public void load() {
	nextScreenSound = new ControlledSound("sfx/nextInfoScreen.wav");
	yellowFont = CoreFont.load("info.font.png");
	smallFont = CoreFont.load("green.font.png");

	GameModel.load();
	infoTexts = GameModel.getInfoTexts();

	allElements = new Group();
	createAllSceneElements();
	addElementsToLayer(allElements);
	allElements.alpha.set(0);

	addLayer(allElements);
	allElements.alpha.animateTo(255, FADE_IN_DURATION);
    }

    private void addElementsToLayer(Group layer) {
	layer.add(new ImageSprite("bg.jpg", 0, 0));
	layer.add(new ImageSprite("infoSceneBG.png",20,20));
	layer.add(backButton);
	layer.add(strategyGuideButton);
	layer.add(navigationInfo);
	for (Group g : infoLayer) {
	    layer.add(g);
	}
    }

    private void createAllSceneElements() {
	createBackButton();
	createStrategyGuideButton();

	infoLayer = new Group[infoTexts.length];
	for (int i = 0; i < infoTexts.length; i++) {
	    infoLayer[i] = createInfoLabels(infoTexts[i]);
	}
	infoLayer[0].alpha.set(255);

	navigationInfo = new Label(smallFont,
		"CLICK ANYWHERE OR PRESS <<SPACE>> TO SHOW NEXT SCREEN", 320,
		460);
	navigationInfo.setAnchor(0.5, 0);
    }

    private void createBackButton() {
	backButton = Button.createLabeledButton("Back", 213, 500);
	backButton.setAnchor(0.5, 1);
	backButton.setKeyBinding(Input.KEY_ESCAPE);
	backButton.y.animate(550, 450, FADE_IN_DURATION / 2,
		Easing.ELASTIC_OUT, FADE_IN_DURATION);
    }

    private void createStrategyGuideButton() {
	strategyGuideButton = Button.createLabeledButton("Strategy Guide", 426,
		500);
	strategyGuideButton.setAnchor(0.5, 1);
	strategyGuideButton.y.animate(550, 450, FADE_IN_DURATION / 2,
		Easing.ELASTIC_OUT, FADE_IN_DURATION);
    }

    private Group createInfoLabels(String text) {
	Group g = Label.createMultilineLabel(yellowFont, text, 40, 40, 550);
	g.setAnchor(0, 0);
	g.alpha.set(0);
	return g;
    }

    @Override
    public void update(int elapsedTime) {
	if (backButton.isClicked()) {
	    new ControlledSound("sfx/backButtonClicked.wav").play();
	    Stage.popScene();
	} else if (strategyGuideButton.isClicked()) {
	    CoreSystem.showDocument(STRATEGY_GUIDE_URL, "_blank");
	} else if (Input.isMouseReleased() || Input.isPressed(Input.KEY_SPACE)) {
	    // DO NOT RE-ORDER the following 3 lines
	    Group currentInfoLayer = infoLayer[currentScreen];
	    nextScreen();
	    Group nextInfoLayer = infoLayer[currentScreen];

	    currentInfoLayer.alpha.animateTo(0, 500);
	    nextInfoLayer.alpha.animateTo(255, 500);
	}
    }

    private void nextScreen() {
	nextScreenSound.play();
	currentScreen++;
	// loop through screens
	if (currentScreen >= infoLayer.length) {
	    currentScreen = 0;
	}
    }
}
