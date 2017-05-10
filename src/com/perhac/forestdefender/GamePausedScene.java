package com.perhac.forestdefender;

import pulpcore.CoreSystem;
import pulpcore.Input;
import pulpcore.Stage;
import pulpcore.animation.Timeline;
import pulpcore.animation.event.AddSpriteEvent;
import pulpcore.animation.event.SceneChangeEvent;
import pulpcore.image.CoreFont;
import pulpcore.image.CoreImage;
import pulpcore.scene.Scene2D;
import pulpcore.sprite.*;

import com.perhac.forestdefender.game.Player;
import com.perhac.forestdefender.utils.ControlledSound;
import com.perhac.forestdefender.utils.JukeBox;

public class GamePausedScene extends Scene2D {

    private static final int FADE_IN_DURATION = 150;
    private CoreFont yellowFont;
    private Group allElements;
    private ControlledSound escapeToMenuSound;
    private final Player player;

    public GamePausedScene(Player player) {
	super();
	this.player = player;
    }

    @Override
    public void load() {
	escapeToMenuSound = new ControlledSound("sfx/buttonClicked.wav");
	yellowFont = CoreFont.load("info.font.png");

	allElements = new Group();
	allElements.add(new ImageSprite("bg.jpg", 0, 0));

	Label title = new Label(yellowFont, "Game menu", 320, 30);
	title.setAnchor(0.5, 0);
	allElements.add(title);

	Button backButton = new BackButton(0, 100);
	backButton.setAnchor(0, 0.5);
	backButton.setKeyBinding(Input.KEY_ESCAPE);
	allElements.add(backButton);
	Label buttonLabel = new Label(yellowFont, "Resume Game", 320, 100);
	buttonLabel.setAnchor(0.5, 0.5);
	buttonLabel.enabled.set(false);
	allElements.add(buttonLabel);

	Button guideButton = new StrategyGuideButton(0, 170);
	guideButton.setAnchor(0, 0.5);
	allElements.add(guideButton);
	buttonLabel = new Label(yellowFont, "Strategy Guide", 320, 170);
	buttonLabel.setAnchor(0.5, 0.5);
	buttonLabel.enabled.set(false);
	allElements.add(buttonLabel);

	Button restartLevelButton = new LevelRestartButton(0, 240);
	restartLevelButton.setAnchor(0, 0.5);
	allElements.add(restartLevelButton);
	buttonLabel = new Label(yellowFont, "Restart Level", 320, 240);
	buttonLabel.setAnchor(0.5, 0.5);
	buttonLabel.enabled.set(false);
	allElements.add(buttonLabel);

	buttonLabel = new Label(yellowFont, "Music Volume", 320, 290);
	buttonLabel.setAnchor(0.5, 0.5);
	allElements.add(buttonLabel);
	VolumeSlider volumeSlider = new VolumeSlider(320, 310);
	allElements.add(volumeSlider);

	Button quitButton = new QuitToMenuButton(0, 410);
	quitButton.setAnchor(0, 0.5);
	allElements.add(quitButton);
	buttonLabel = new Label(yellowFont, "Quit to Main Menu", 320, 410);
	buttonLabel.setAnchor(0.5, 0.5);
	buttonLabel.enabled.set(false);
	allElements.add(buttonLabel);

	allElements.y.set(480);
	addLayer(allElements);
	allElements.y.animateTo(0, FADE_IN_DURATION);
    }

    private class VolumeSlider extends Slider {

	private JukeBox jukeBox;

	public VolumeSlider(int x, int y) {
	    super("widgets/slider.png", "widgets/slider-thumb.png", x, y);
	    setAnchor(0.5, 0);
	    setRange(1, 100);

	    jukeBox = JukeBox.getInstance();
	    value.set(jukeBox.getVolume());
	    jukeBox.setVolume(value.getAsInt());
	}

	@Override
	public void update(int et) {
	    super.update(et);
	    if (this.isAdjusting()) {
		jukeBox.animateVolume((double) value.getAsInt() / 100, et);
	    }
	}

    }

    private class BackButton extends Button {

	public BackButton(double x, double y) {
	    super(CoreImage.load("LevelButton.png").split(1, 3), x, y);
	}

	@Override
	public void update(int et) {
	    super.update(et);
	    if (isClicked()) {
		new ControlledSound("sfx/backButtonClicked.wav").play();
		Stage.popScene();
		player.resume();
	    }
	}

    }

    private class StrategyGuideButton extends Button {

	public StrategyGuideButton(double x, double y) {
	    super(CoreImage.load("LevelButton.png").split(1, 3), x, y);
	}

	@Override
	public void update(int et) {
	    super.update(et);
	    if (isClicked()) {
		new ControlledSound("sfx/backButtonClicked.wav").play();
		CoreSystem.showDocument(InfoScene.STRATEGY_GUIDE_URL, "_blank");
	    }
	}

    }

    private class LevelRestartButton extends Button {

	public LevelRestartButton(double x, double y) {
	    super(CoreImage.load("LevelButton.png").split(1, 3), x, y);
	}

	@Override
	public void update(int et) {
	    super.update(et);
	    if (isClicked()) {
		new ControlledSound("sfx/backButtonClicked.wav").play();
		String gameStatus = Player.getGameStatus("Level Restart");
		ForestDefender.uploadGameDetails(gameStatus);
		Stage.setScene(new ForestDefender(ForestDefender.gameLevel));
	    }
	}

    }

    private class QuitToMenuButton extends Button {

	private static final int FADE_OUT_DURATION = 750;

	public QuitToMenuButton(double x, double y) {
	    super(CoreImage.load("LevelButton.png").split(1, 3), x, y);
	}

	@Override
	public void update(int et) {
	    super.update(et);
	    if (isClicked()) {
		escapeToMenuSound.play();
		String gameStatus = Player.getGameStatus("Quit to Menu");
		ForestDefender.uploadGameDetails(gameStatus);
		JukeBox.getInstance().fadeOut(FADE_OUT_DURATION);
		Timeline t = new Timeline();
		FilledSprite blackCurtain = new FilledSprite(
			pulpcore.image.Colors.BLACK);
		blackCurtain.alpha.set(0);
		t.addEvent(new AddSpriteEvent(allElements, blackCurtain, 0));
		t.animateTo(blackCurtain.alpha, 255, FADE_OUT_DURATION - 100);
		t.addEvent(new SceneChangeEvent(new TitleScene(),
			FADE_OUT_DURATION));
		addTimeline(t);
	    }
	}

    }

}
