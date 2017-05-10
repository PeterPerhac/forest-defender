package com.perhac.forestdefender.game;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

public class GameWave {

    public String name;
    public String message;
    public String monsterFile;
    public int count;
    public int hitpoints;
    public int reward;
    public int spread;

    public boolean recurrent;

    public double speed;

    public ArrayList<String> weakness;
    public ArrayList<String> resistance;

    public GameWave() {
    }

    @SuppressWarnings("unchecked")
    public GameWave(Element wave) {
	Element monsters = wave.getChild("monsters");

	count = Integer.parseInt(monsters.getChildText("count"));
	hitpoints = Integer.parseInt(monsters.getChildText("hitpoints"));
	monsterFile = monsters.getChildText("monster-file");
	name = wave.getChildText("name");
	message = wave.getChildText("message");
	if (message == null) {
	    message = "";
	}
	recurrent = monsters.getChild("recurrent") != null;
	reward = Integer.parseInt(monsters.getChildText("reward"));
	speed = Double.parseDouble(monsters.getChildText("speed"));
	spread = Integer.parseInt(monsters.getChildText("spread"));

	weakness = new ArrayList<String>();
	Element weaknesses = monsters.getChild("weakness");
	resistance = new ArrayList<String>();
	Element resistances = monsters.getChild("resistance");
	List<Element> ws = weaknesses.getChildren();
	List<Element> rs = resistances.getChildren();
	for (Element e : ws) {
	    weakness.add(e.getName());
	}
	for (Element e : rs) {
	    resistance.add(e.getName());
	}
    }

}
