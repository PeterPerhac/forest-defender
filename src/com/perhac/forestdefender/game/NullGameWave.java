package com.perhac.forestdefender.game;

import java.util.ArrayList;

public class NullGameWave extends GameWave {

    public NullGameWave() {
	count = 0;
	name = "";
	hitpoints = 0;
	monsterFile = "";
	recurrent = false;
	resistance = new ArrayList<String>(0);
	weakness = new ArrayList<String>(0);
	reward = 0;
	speed = 0;
	spread = 0;
    }
}
