package com.perhac.forestdefender.game;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;

import pulpcore.Assets;

import com.perhac.forestdefender.game.towers.TowerBase;
import com.perhac.forestdefender.game.towers.Tower.TowerType;
import com.perhac.forestdefender.game.traps.TrapBase;
import com.perhac.forestdefender.game.traps.Trap.TrapType;

public class GameModel {

    private static boolean ready = false;

    public static final Hashtable<String, TrapBase> traps = new Hashtable<String, TrapBase>(
	    5);
    public static final Hashtable<String, TowerBase> towers = new Hashtable<String, TowerBase>(
	    2);
    public static final ArrayList<GameLevel> levels = new ArrayList<GameLevel>(
	    3);

    private static final ArrayList<String> infos = new ArrayList<String>();
    private static final ArrayList<String> musicTracks = new ArrayList<String>();

    private static Element configRoot;

    public static void load() {
	if (ready)
	    return;
	SAXBuilder builder = new SAXBuilder();
	Document doc;
	try {
	    doc = builder.build(Assets.getAsStream("levels.xml"));
	    configRoot = doc.getRootElement();
	    loadLevels();
	    loadInfos();
	    loadMusicTracks();
	    loadTraps();
	    loadTowers();

	    ready = true;
	} catch (Exception e) {
	    throw new RuntimeException("Can't load level data with message: "
		    + e.getMessage());
	}
    }

    @SuppressWarnings("unchecked")
    private static void loadLevels() {
	Iterator it = configRoot.getDescendants(new ElementFilter("level"));
	while (it.hasNext()) {
	    Element e = (Element) it.next();
	    levels.add(new GameLevel(e));
	}
    }

    @SuppressWarnings("unchecked")
    private static void loadInfos() {
	Iterator it = configRoot.getDescendants(new ElementFilter("infotext"));
	while (it.hasNext()) {
	    Element e = (Element) it.next();
	    infos.add(e.getText());
	}
    }

    @SuppressWarnings("unchecked")
    private static void loadMusicTracks() {
	Iterator it = configRoot.getDescendants(new ElementFilter("track"));
	while (it.hasNext()) {
	    Element e = (Element) it.next();
	    musicTracks.add("music/" + e.getText());
	}
    }

    @SuppressWarnings("unchecked")
    private static void loadTraps() {
	Iterator it = configRoot.getDescendants(new ElementFilter("trap"));
	while (it.hasNext()) {
	    Element e = (Element) it.next();
	    String eid = e.getAttributeValue("type");
	    traps.put(eid, new TrapBase(e));
	}
    }

    @SuppressWarnings("unchecked")
    private static void loadTowers() {
	Iterator it = configRoot.getDescendants(new ElementFilter("tower"));
	while (it.hasNext()) {
	    Element e = (Element) it.next();
	    String eid = e.getAttributeValue("type");
	    towers.put(eid, new TowerBase(e));
	}
    }

    public static String[] getInfoTexts() {
	return infos.toArray(new String[infos.size()]);
    }

    public static String[] getMusicTracks() {
	return musicTracks.toArray(new String[musicTracks.size()]);
    }

    public static TrapBase getTrapBase(TrapType trapType) {
	return traps.get(trapType.toString()).clone();
    }

    public static TowerBase getTowerBase(TowerType towerType) {
	return towers.get(towerType.toString()).clone();
    }

    public static ArrayList<String> getTrapInfo(TrapType trapType) {
	TrapBase base = getTrapBase(trapType);
	ArrayList<String> trapInfo = new ArrayList<String>(10);
	trapInfo.add(base.getName());
	trapInfo.add("Cost : " + String.valueOf(base.getCost()));
	if (trapType != TrapType.DEATH) {
	    trapInfo.add("Damage : " + base.getStrength() + " hp");
	} else {
	    trapInfo.add("Damage : FATAL");
	}
	if (trapType == TrapType.ICE) { // don't bother when it's ice trap
	    trapInfo.add("Slow : FREEZE");
	} else {
	    trapInfo.add("Slow : " + (base.getSlow() * 100) + " %");
	}
	NumberFormat nf = NumberFormat.getNumberInstance();
	nf.setMaximumFractionDigits(2);
	trapInfo.add("Duration : "
		+ nf.format((double) base.getEffectDuration() / 1000) + " sec");
	trapInfo.add("Chance : " + base.getChance() + " %");
	return trapInfo;
    }

    public static ArrayList<String> getTowerInfo(TowerType towerType) {
	TowerBase base = getTowerBase(towerType);
	ArrayList<String> towerInfo = new ArrayList<String>(10);
	towerInfo.add(base.getName());
	towerInfo.add("Cost : " + String.valueOf(base.getCost()));
	towerInfo.add("Damage : " + base.getStrength() + " hp");
	NumberFormat nf = NumberFormat.getNumberInstance();
	nf.setMaximumFractionDigits(2);
	towerInfo.add("Speed : " + nf.format(1000 / base.getSpeed()) + " sps");
	towerInfo.add("Range : " + base.getRange());
	return towerInfo;
    }
}
