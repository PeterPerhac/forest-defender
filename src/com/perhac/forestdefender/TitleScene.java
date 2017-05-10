package com.perhac.forestdefender;

import pulpcore.Input;
import pulpcore.Stage;
import pulpcore.animation.Easing;
import pulpcore.animation.event.SceneChangeEvent;
import pulpcore.image.CoreImage;
import pulpcore.scene.Scene2D;
import pulpcore.sprite.Button;
import pulpcore.sprite.Group;
import pulpcore.sprite.ImageSprite;

import com.perhac.forestdefender.game.GameModel;
import com.perhac.forestdefender.utils.JukeBox;
import com.perhac.forestdefender.utils.ControlledSound;

public class TitleScene extends Scene2D {

    private static final int FADE_OUT_DURATION = 750;
    private Button playButton;
    private Button infoButton;
    private Button creditsButton;
    private Group componentLayer;

    private boolean playSound = true;
    private ControlledSound buttonClickedSound, enterButtonSound;

    @Override
    public void load() {
	GameModel.load();
	JukeBox.getInstance();
	enterButtonSound = new ControlledSound("sfx/mouseEntered.wav");
	buttonClickedSound = new ControlledSound("sfx/buttonClicked.wav");

	playButton = new Button(CoreImage.load("PlayButton.png").split(1, 3),
		20, 20);
	playButton.setKeyBinding(Input.KEY_SPACE);

	infoButton = new Button(CoreImage.load("InfoButton.png").split(1, 3),
		20, 110);

	creditsButton = new Button(CoreImage.load("CreditsButton.png").split(1,
		3), 630, 470);
	creditsButton.setAnchor(1, 1);

	componentLayer = new Group();
	componentLayer.add(playButton);
	componentLayer.add(infoButton);
	componentLayer.add(creditsButton);

	add(new ImageSprite("bg.jpg", 0, 0));
	addLayer(componentLayer);
    }

    @Override
    public void update(int elapsedTime) {
	checkForMouseHover();

	if (playButton.isClicked()) {
	    // Animated alternative to Stage.setScene(new HelloWorld());
	    buttonClickedSound.play();
	    componentLayer.x.animate(componentLayer.x.get(), -640,
		    FADE_OUT_DURATION, Easing.BACK_IN);
	    addEvent(new SceneChangeEvent(new LevelSelectionScene(),
		    FADE_OUT_DURATION));
	} else if (infoButton.isClicked()) {
	    playSoundAndPushScene(new InfoScene());
	} else if (creditsButton.isClicked()) {
	    playSoundAndPushScene(new CreditsScene());
	}
    }

    private void playSoundAndPushScene(Scene2D sceneToPush) {
	buttonClickedSound.play();
	// Pushes the scene onto the stack (doesn't unload this Scene)
	Stage.pushScene(sceneToPush);
    }

    private void checkForMouseHover() {
	if (playButton.isMouseHover())
	    playSound();
	else if (infoButton.isMouseHover())
	    playSound();
	else if (creditsButton.isMouseHover())
	    playSound();
	else
	    playSound = true;
    }

    private void playSound() {
	if (playSound) {
	    enterButtonSound.play();
	    playSound = false;
	}
    }
}
