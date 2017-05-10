package com.perhac.forestdefender;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import pulpcore.Build;
import pulpcore.CoreSystem;
import pulpcore.Stage;
import pulpcore.image.CoreFont;
import pulpcore.sprite.FilledSprite;
import pulpcore.sprite.Group;
import pulpcore.sprite.Label;

import com.perhac.forestdefender.game.Player;

public class LoadingScene extends pulpcore.scene.LoadingScene {

    private Label percentageLabel;

    public LoadingScene() {
	super("ForestDefender-" + ProjectBuild.VERSION + ".zip",
		new EnterNameScene());

	CoreSystem.setTalkBackField("app.name", "ForestDefender");
	CoreSystem.setTalkBackField("app.version", ProjectBuild.VERSION);

	Stage.setUncaughtExceptionScene(new UncaughtExceptionScene());
	Stage.invokeOnShutdown(new Runnable() {
	    public void run() {
		ForestDefender.uploadGameDetails(Player.getGameStatus("Exit"));
	    }
	});
    }

    @Override
    public void load() {
	String[] validHosts = { "perhac.com", "www.perhac.com" };
	if ((!Build.DEBUG) && !CoreSystem.isValidHost(validHosts)) {
	    CoreSystem
		    .showDocument("http://www.perhac.com/shared/forest-defender/index.html");
	} else {
	    // Start loading the zip
	    super.load();
	    setupCustomLoadingScene();
	}
    }

    private void setupCustomLoadingScene() {
	Group mainLayer = getMainLayer();
	mainLayer.removeAll();

	FilledSprite bg = new FilledSprite(pulpcore.image.Colors.BLACK);
	mainLayer.add(bg);

	CoreFont font = CoreFont.getSystemFont().tint(
		pulpcore.image.Colors.WHITE);

	Label l1 = new Label(font, "Creeps are plotting to invade your forest",
		320, 210);
	l1.setAnchor(0.5, 0.5);
	mainLayer.add(l1);

	percentageLabel = new Label(font,
		"%d% of their evil plan is worked out", 320, 240);
	percentageLabel.setAnchor(0.5, 0.5);
	percentageLabel.setFormatArg(0);
	mainLayer.add(percentageLabel);

	Label l3 = new Label(font, "Soon you'll be called upon to stop them!",
		320, 270);
	l3.setAnchor(0.5, 0.5);
	mainLayer.add(l3);

    }

    @Override
    public void update(int elapsedTime) {
	super.update(elapsedTime);
	if (getProgress() >= 0) {
	    NumberFormat nf = new DecimalFormat();
	    nf.setMaximumFractionDigits(2);
	    percentageLabel.setFormatArg(nf.format(getProgress() * 100));
	}
    }
}
