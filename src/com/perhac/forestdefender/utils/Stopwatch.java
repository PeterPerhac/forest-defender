package com.perhac.forestdefender.utils;

import pulpcore.CoreSystem;

public class Stopwatch {

    private long startMillis = 0;
    private long startPauseMillis = 0;

    public Stopwatch() {
    }

    public void start() {
	startMillis = CoreSystem.getTimeMillis();
    }

    public void pause() {
	startPauseMillis = CoreSystem.getTimeMillis();
    }

    public void resume() {
	startMillis += (CoreSystem.getTimeMillis() - startPauseMillis);
    }

    public long getMillis() {
	return CoreSystem.getTimeMillis() - startMillis;
    }

    @Override
    public String toString() {
	int secondsTotal = (int) (getMillis() / 1000);
	int hours = secondsTotal / 3600;
	int minutes = (secondsTotal - (hours * 3600)) / 60;
	int seconds = secondsTotal - (hours * 3600) - (minutes * 60);

	if (hours > 0) {
	    return String.format("%dh : %dm : %ds", new Object[] { hours,
		    minutes, seconds });
	} else {
	    return String
		    .format("%dm : %ds", new Object[] { minutes, seconds });
	}
    }

}
