package com.perhac.forestdefender.game;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

public class GameLevel {

    private String name;
    private String description;
    private String filePrefix;
    private int money;
    private int lives;

    private ArrayList<GameWave> waves;

    @SuppressWarnings("unchecked")
    public GameLevel(Element e) {
	name = e.getChildText("name");
	description = e.getChildText("description");
	filePrefix = e.getChildText("file-prefix");
	money = Integer.parseInt(e.getChildText("money"));
	lives = Integer.parseInt(e.getChildText("lives"));
	Element waves = e.getChild("waves");
	List<Element> waveList = waves.getChildren("wave");
	this.waves = new ArrayList<GameWave>(waveList.size());
	for (Element wave : waveList) {
	    this.waves.add(new GameWave(wave));
	}
    }

    public String getName() {
	return name;
    }

    public String getDescription() {
	return description;
    }

    public String getFileName() {
	return filePrefix;
    }

    public GameWave getWave(int waveNumber) {
	if (waveNumber > 0 && waveNumber <= waves.size()) {
	    return waves.get(waveNumber - 1);
	} else {
	    return new NullGameWave();
	}
    }

    public int getMoney() {
	return money;
    }

    public int getLives() {
	return lives;
    }

    public int getWaveCount() {
	return waves.size();
    }

    public String getNextWaveMessage(int waveNo) {
	GameWave wave = waves.get(waveNo);
	return wave.message;
    }
}
