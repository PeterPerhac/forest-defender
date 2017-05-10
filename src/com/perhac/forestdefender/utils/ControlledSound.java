package com.perhac.forestdefender.utils;

import java.util.Map;
import java.util.TreeMap;

import pulpcore.sound.Playback;
import pulpcore.sound.Sound;

public class ControlledSound {

    private static Map<String, LineControlledSound> sounds = new TreeMap<String, LineControlledSound>();

    private String resourceName;

    public ControlledSound(String resourceName, int n) {
	this.resourceName = resourceName;
	if (!sounds.containsKey(resourceName)) {
	    sounds.put(resourceName, new LineControlledSound(resourceName, n));
	}
    }

    public ControlledSound(String resourceName) {
	this(resourceName, 10);
    }

    public void play() {
	if (sounds.containsKey(resourceName)) {
	    sounds.get(resourceName).play();
	}
    }

    private class LineControlledSound {

	private Playback[] lines;
	private Sound sound;
	private int roundRobin = 0;

	private LineControlledSound(String res, int n) {
	    lines = new Playback[n];
	    sound = Sound.load(res);
	}

	private void play() {
	    int freeLineNumber = getFreeLine();
	    if (freeLineNumber >= 0) {
		lines[freeLineNumber] = sound.play();
	    } else {
		if (roundRobin == lines.length - 1) {
		    roundRobin = 0;
		} else {
		    roundRobin++;
		}
		lines[roundRobin].rewind();
	    }
	}

	private int getFreeLine() {
	    int freeLine = -1;
	    for (int i = 0; i < lines.length; i++) {
		if (lines[i] == null || lines[i].isFinished()) {
		    freeLine = i;
		    break;
		}
	    }
	    return freeLine;
	}
    }

}
