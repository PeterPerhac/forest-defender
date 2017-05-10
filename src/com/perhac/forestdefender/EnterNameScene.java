package com.perhac.forestdefender;

import static pulpcore.image.Colors.BLACK;
import pulpcore.Build;
import pulpcore.CoreSystem;
import pulpcore.Input;
import pulpcore.Stage;
import pulpcore.animation.Easing;
import pulpcore.animation.Timeline;
import pulpcore.image.CoreFont;
import pulpcore.scene.Scene2D;
import pulpcore.sprite.FilledSprite;
import pulpcore.sprite.ImageSprite;
import pulpcore.sprite.Label;

import com.perhac.forestdefender.utils.ControlledSound;

public class EnterNameScene extends Scene2D {

    private static final String DEFAULT_PLAYER_NAME = "Guest";
    private StringBuilder nameEntered;
    private Label inputLabel;
    private ControlledSound greeting;

    public EnterNameScene() {
	nameEntered = new StringBuilder(20);
    }

    @Override
    public void update(int elapsedTime) {
	super.update(elapsedTime);
	if (handleEnter())
	    return;
	if (handleBackSpace())
	    return;

	if (nameEntered.length() < 20) {
	    nameEntered.append(Input.getTypedChars());
	}
	inputLabel.setFormatArg(nameEntered.toString());
    }

    private boolean handleBackSpace() {
	if (Input.isTyped(Input.KEY_BACK_SPACE)) {
	    if (nameEntered.length() > 0) {
		nameEntered.deleteCharAt(nameEntered.length() - 1);
	    }
	    return true;
	}
	return false;
    }

    private boolean handleEnter() {
	//FIXME add/remove !
	if (Build.DEBUG || Input.isTyped(Input.KEY_ENTER)) {
	    String playerName = DEFAULT_PLAYER_NAME;
	    String entered = nameEntered.toString().trim();
	    if (entered.length() > 0) {
		playerName = entered;
	    }
	    CoreSystem.setTalkBackField("player_name", playerName);
	    greeting.play();
	    Stage.setScene(new TitleScene());
	    return true;
	}
	return false;
    }

    @Override
    public void load() {
	super.load();
	nameEntered.setLength(0);
	greeting = new ControlledSound ("sfx/greeting.wav",1);
	add(new FilledSprite(BLACK));
	Label label1 = new Label(CoreFont.load("info.font.png"),"Player's name:",320,120);
	label1.setAnchor(0.5, 0.5);
	add(label1);
	ImageSprite inputBG = new ImageSprite("inputBG.png",320,240);
	inputBG.setAnchor(0.5, 0.5);
	add(inputBG);
	inputLabel = new Label(CoreFont.load("input.font.png"), "%s", 320, 240);
	inputLabel.setAnchor(0.5, 0.5);
	add(inputLabel);
	Label label2 = new Label(CoreFont.load("info.font.png"),"Type your name and press ENTER to proceed...",320,360);
	label2.setAnchor(0.5, 0.5);
	add(label2);
	Timeline t = new Timeline();
	t.animate(inputBG.alpha, 255, 128, 500, Easing.NONE, 0);
	t.animate(inputBG.alpha, 128, 255, 500, Easing.NONE, 500);
	t.loopForever();
	addTimeline(t);
    }

}
