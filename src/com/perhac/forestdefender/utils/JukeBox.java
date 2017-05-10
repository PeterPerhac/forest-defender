package com.perhac.forestdefender.utils;

import pulpcore.sound.Playback;
import pulpcore.sound.Sound;

import com.perhac.forestdefender.game.GameModel;

public class JukeBox {

    private static JukeBox instance = null;
    private String[] trackFiles;
    private Sound[] tracks;

    private int initialVolume = 50;

    private Playback currentPlayback = null;

    private JukeBox() {
	GameModel.load();
	trackFiles = GameModel.getMusicTracks();
	tracks = new Sound[trackFiles.length];
	for (int i = 0; i < trackFiles.length; i++) {
	    tracks[i] = Sound.load(trackFiles[i]);
	}
    }

    public static JukeBox getInstance() {
	if (instance == null) {
	    instance = new JukeBox();
	}
	return instance;
    }

    public void stopPlayback() {
	if (isPlaying()) {
	    currentPlayback.stop();
	    currentPlayback = null;
	}
    }

    private boolean isPlaying() {
	return currentPlayback != null // something was/is playing
		&& !currentPlayback.isFinished() // and it has not finished yet
		&& currentPlayback.level.get() > 0; // and it was/is audible
    }

    public void fadeOut(int fadeOutDuration) {
	if (isPlaying()) {
	    currentPlayback.level.animateTo(0, fadeOutDuration);
	}
    }

    public int getVolume() {
	if (isPlaying()) {
	    return (int) (currentPlayback.level.get() * 100);
	} else {
	    return 0;
	}
    }

    public void setVolume(int i) {
	currentPlayback.level.set((double) i / 100);
	initialVolume = i;
    }

    public void animateVolume(double from, double to, int duration) {
	if (isPlaying()) {
	    currentPlayback.level.animate(from, to, duration);
	}
    }

    public void animateVolume(double to, int duration) {
	animateVolume(currentPlayback.level.get(), to, duration);
    }

    public void play(int trackIndex, int fadeInDuration) {
	stopPlayback();
	currentPlayback = tracks[trackIndex].loop();
	// KNOWN PULPCORE BUG - MUST >>NOT<< ANIMATE VOLUME FROM ZERO
	currentPlayback.level.animate(0.01, (double) initialVolume / 100,
		fadeInDuration);

    }
}
