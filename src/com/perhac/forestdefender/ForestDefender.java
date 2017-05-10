package com.perhac.forestdefender;

import static pulpcore.image.Colors.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;

import pulpcore.Build;
import pulpcore.CoreSystem;
import pulpcore.Input;
import pulpcore.Stage;
import pulpcore.animation.Easing;
import pulpcore.animation.Fixed;
import pulpcore.animation.Timeline;
import pulpcore.animation.event.AddSpriteEvent;
import pulpcore.animation.event.RemoveSpriteEvent;
import pulpcore.animation.event.SceneChangeEvent;
import pulpcore.animation.event.TimelineEvent;
import pulpcore.image.CoreFont;
import pulpcore.image.CoreImage;
import pulpcore.image.filter.Glow;
import pulpcore.math.Path;
import pulpcore.scene.Scene2D;
import pulpcore.sprite.*;

import com.perhac.forestdefender.game.*;
import com.perhac.forestdefender.game.monsters.MonsterSprite;
import com.perhac.forestdefender.game.panels.*;
import com.perhac.forestdefender.game.towers.Tower;
import com.perhac.forestdefender.game.towers.Tower.TowerType;
import com.perhac.forestdefender.game.traps.Trap;
import com.perhac.forestdefender.game.traps.Trap.TrapType;
import com.perhac.forestdefender.utils.ControlledSound;
import com.perhac.forestdefender.utils.JukeBox;
import com.perhac.forestdefender.utils.PathReader;

public class ForestDefender extends Scene2D {

    private static final int INITIAL_DELAY = 2000;
    private static final int FADE_OUT_DURATION = 1000;

    private CoreFont yellowFont, font;
    private ControlledSound towerClicked, buildTrapSound, buttonClicked,
	    spacebarHit, invalidAction;

    private Button[] trapButton, towerButton;
    private NextWaveButton nextWaveButton;

    private RangeCircle rangeCircle;
    public UnitInfoBubble unitInfoBubble;

    private Fixed goToMenuOnClick = new Fixed(0);

    private ImageSprite sidepanel;
    private ImageSprite panelShadow;
    private ImageSprite battlefield;
    private ImageSprite mapOverlay;
    private Group constructionLayer = new ConstructionLayer();

    private Group informationPanels = new Group();
    private Group currentInformationPanel = null;
    public static LevelInfoPanel levelInfoPanel;
    public static MonsterInfoPanel monsterInfoPanel;
    public static TowerInfoPanel towerInfoPanel;
    public static TrapInfoPanel trapInfoPanel;

    public Group shotsLayer = new Group();
    public Group monsterLayer = new Group();
    private Group unitLayer = new Group();
    private Group mainLayer = new Group();

    public static GameLevel gameLevel;
    private static int waveNumber;
    public Path path;
    public static Player player;
    public boolean underAttack;

    private ConstructionMode constructionMode;
    private TrapType trapToBuild;
    private TowerType towerToBuild;

    public Collection<MonsterSprite> monsters = new ArrayList<MonsterSprite>(0);
    public Collection<Trap> traps = new ArrayList<Trap>(20);
    private Collection<Tower> towers = new ArrayList<Tower>(10);
    private WaveMessage waveMessage;

    public static int SPEED_MULTIPLIER = 1;
    private static boolean dataUploaded;

    private enum ConstructionMode {
	NONE, TRAP, TOWER
    }

    public ForestDefender(GameLevel gameLevel) {
	super();
	ForestDefender.gameLevel = gameLevel;
    }

    @Override
    public void load() {
	towerClicked = new ControlledSound("sfx/buildTower.wav");
	buildTrapSound = new ControlledSound("sfx/buildTrap.wav");
	buttonClicked = new ControlledSound("sfx/buttonClicked.wav");
	spacebarHit = new ControlledSound("sfx/nextInfoScreen.wav");
	invalidAction = new ControlledSound("sfx/invalidAction.wav");
	path = PathReader.readSVGPath("level/" + gameLevel.getFileName()
		+ ".path");
	yellowFont = CoreFont.load("info.font.png");
	font = CoreFont.load("white.font.png");

	dataUploaded = false;
	SPEED_MULTIPLIER = 1;
	waveNumber = 0;
	constructionMode = ConstructionMode.NONE;

	setupMainLayer();
	addLayers();
	levelInfoPanel = new LevelInfoPanel(this, gameLevel.getWaveCount());
	addToInfoPanels(levelInfoPanel);
	monsterInfoPanel = new MonsterInfoPanel(this);
	addToInfoPanels(monsterInfoPanel);
	towerInfoPanel = new TowerInfoPanel(this);
	addToInfoPanels(towerInfoPanel);
	trapInfoPanel = new TrapInfoPanel(this);
	addToInfoPanels(trapInfoPanel);

	setInfoPanel(levelInfoPanel, null);

	updateWaveLabels();

	// player must be setup AFTER the side panel
	player = Player.getInstance();
	player.addObserver(levelInfoPanel);
	player.setGameLevel(gameLevel);

	// to stop the user from sending in waves during intro
	underAttack = true;

	addTimeline(intro());
	JukeBox jb = JukeBox.getInstance();
	jb.play(0, INITIAL_DELAY);
    }

    private void addToInfoPanels(Group panel) {
	panel.alpha.set(0);
	panel.visible.set(false);
	panel.enabled.set(false);
	informationPanels.add(panel);
    }

    private void updateWaveLabels() {
	levelInfoPanel.updateWaveLabels(gameLevel, waveNumber, monsters.size());
    }

    private void addLayers() {
	add(new FilledSprite(BLACK));
	add(mainLayer);
	mainLayer.add(battlefield);
	mainLayer.add(rangeCircle);
	mainLayer.add(unitLayer);
	mainLayer.add(monsterLayer);
	mainLayer.add(shotsLayer);
	mainLayer.add(constructionLayer);
	mainLayer.add(mapOverlay);
	mainLayer.add(panelShadow);
	mainLayer.add(sidepanel);
	addTowerButtons();
	addTrapButtons();
	mainLayer.add(informationPanels);
	nextWaveButton = new NextWaveButton(480, 350);
	nextWaveButton.setKeyBinding(Input.KEY_N);
	mainLayer.add(nextWaveButton);
	mainLayer.add(new ImageSprite("speed.png", 480, 400));
	mainLayer.add(new SpeedUpButton(580, 400));
	mainLayer.add(new MenuPauseButton(480, 430));
	mainLayer.add(waveMessage);
	mainLayer.add(unitInfoBubble);
    }

    private Timeline intro() {
	Label spinningLabel = new Label(yellowFont, "Protect your forest!",
		320, 240);
	spinningLabel.setAnchor(0.5, 0.5);
	mainLayer.add(spinningLabel);

	Timeline t = new Timeline();
	t.scaleTo(spinningLabel, spinningLabel.width.get() * 2,
		spinningLabel.height.get() * 2, INITIAL_DELAY);
	t.animate(spinningLabel.angle, 0, 4 * Math.PI, INITIAL_DELAY,
		Easing.STRONG_IN_OUT);
	t.addEvent(new RemoveSpriteEvent(spinningLabel, INITIAL_DELAY));
	t.addEvent(new TimelineEvent(INITIAL_DELAY) {

	    @Override
	    public void run() {
		underAttack = false; // allow player to send in waves
		endOfWave(); // to display an optional pre-wave message
	    }
	});
	return t;
    }

    private void setupMainLayer() {
	battlefield = new ImageSprite("level/" + gameLevel.getFileName()
		+ ".jpg", 0, 0);
	rangeCircle = new RangeCircle();
	unitInfoBubble = new UnitInfoBubble();
	waveMessage = new WaveMessage();
	panelShadow = new ImageSprite("panel-shadow.png", 475, 0);
	sidepanel = new ImageSprite("panel.jpg", 480, 0);
	mapOverlay = new ImageSprite("level/" + gameLevel.getFileName()
		+ "-path.png", 0, 0);
	mapOverlay.visible.set(false);

    }

    private void nextWave() {
	if (!underAttack) {
	    waveMessage.hide();
	    waveNumber++;
	    monsterLayer.removeAll();
	    shotsLayer.removeAll();
	    GameWave wave = gameLevel.getWave(waveNumber);
	    monsters = new ArrayList<MonsterSprite>(wave.count);
	    addMonsters(wave);
	    underAttack = true;
	    updateWaveLabels();
	}
    }

    public void endOfWave() {
	setInfoPanel(levelInfoPanel, null);
	String nextWaveMessage = gameLevel.getNextWaveMessage(waveNumber);
	if (!nextWaveMessage.equals("")) {
	    waveMessage.display(nextWaveMessage);
	}
    }

    private void addMonsters(GameWave gameWave) {
	for (int i = 0; i < gameWave.count; i++) {
	    MonsterSprite monster = new MonsterSprite(gameWave, this, i
		    * gameWave.spread);
	    monsterLayer.add(0, monster);
	    monsterLayer.add(1, monster.healthIndicator);
	    monsters.add(monster);
	}
    }

    private void addTrapButtons() {
	trapButton = new TrapButton[TrapType.values().length];
	for (int i = 0; i < TrapType.values().length; i++) {
	    trapButton[i] = new TrapButton(TrapType.values()[i], 0, 150);
	    trapButton[i].x.set(480 + (i * trapButton[i].width.getAsInt()));
	    trapButton[i].setKeyBinding(Input.KEY_1 + i);
	    mainLayer.add(trapButton[i]);
	}
    }

    private void addTowerButtons() {
	towerButton = new Button[TowerType.values().length];
	for (int i = 0; i < TowerType.values().length; i++) {
	    towerButton[i] = new TowerButton(TowerType.values()[i], 0, 70);
	    towerButton[i].x.set(480 + (i * towerButton[i].width.getAsInt()));
	    mainLayer.add(towerButton[i]);
	}
    }

    @Override
    public void unload() {
	JukeBox.getInstance().stopPlayback();
    }

    @Override
    public void update(int elapsedTime) {
	if (goToMenuOnClick.get() < 1) {
	    handleSpaceBar(elapsedTime);
	} else {
	    // if the game is over, only wait for the user to CLICK
	    if (Input.isMousePressed() || Input.isPressed(Input.KEY_SPACE)) {
		endGame();
	    }
	}
    }

    private void handleSpaceBar(int elapsedTime) {
	if (Input.isPressed(Input.KEY_SPACE)) {
	    if (constructionMode != ConstructionMode.NONE) {
		stopConstruction();
	    }
	}
    }

    private void stopConstruction() {
	constructionMode = ConstructionMode.NONE;
	constructionLayer.setDirty(true);
    }

    private void endGame() {
	uploadGameDetails((player.getLives() > 0) ? "win" : "loss");
	JukeBox.getInstance().fadeOut(FADE_OUT_DURATION);
	Timeline t = new Timeline();
	t.animateTo(mainLayer.alpha, 0, FADE_OUT_DURATION);
	t.after(1000).addEvent(new SceneChangeEvent(new TitleScene(), 0));
	addTimeline(t);
    }

    private class WaveMessage extends Group {

	public WaveMessage() {
	    enabled.set(false);
	    alpha.set(0);
	    setSize(480, 480);
	    setLocation(0, 0);
	    FilledSprite bg = new FilledSprite(BLACK);
	    bg.setSize(480, 480);
	    bg.alpha.set(128);
	    Label clickme = new Label(yellowFont, "click to dismiss", 240, 470);
	    clickme.setAnchor(0.5, 1);
	    add(bg);
	    add(clickme);
	}

	public void display(String message) {
	    remove(findWithTag("labels"));
	    Group msg = Label.createMultilineLabel(yellowFont, message, 260,
		    240, width.getAsInt() - 40);
	    msg.setAnchor(0.5, 0.5);
	    msg.setTag("labels");
	    add(msg);
	    enabled.set(true);
	    constructionLayer.enabled.set(false);
	    alpha.animateTo(255, 300);
	}

	public void hide() {
	    constructionLayer.enabled.set(true);
	    remove(findWithTag("labels"));
	    enabled.set(false);
	    alpha.animateTo(0, 200);
	}

	@Override
	public void update(int et) {
	    if (isMouseOver() && isMouseReleased()) {
		hide();
	    }
	    super.update(et);
	}
    }

    private class NextWaveButton extends Button {

	public NextWaveButton(int x, int y) {
	    super(CoreImage.load("NextWaveButton.png").split(1, 3), x, y);
	}

	@Override
	public void update(int t) {
	    super.update(t);
	    this.enabled.set(!underAttack);
	    this.alpha.set((underAttack) ? 128 : 255);
	    if (underAttack)
		return;

	    if (this.isClicked()) {
		spacebarHit.play();
		checkForCheats();
		nextWave();
	    }
	}

	private void checkForCheats() {
	    // FIXME A CHEAT TO ALLOW ME TO RE-SEND WAVES
	    if (Input.isAltDown() && Input.isControlDown()
		    && Input.isShiftDown()) {
		if (waveNumber < (gameLevel.getWaveCount() - 1)) {
		    waveNumber++;
		}
		return;
	    }
	    if (Input.isAltDown() && Input.isControlDown() && waveNumber > 0) {
		waveNumber--;
		return;
	    }
	}

    }

    private class SpeedUpButton extends Button {

	public SpeedUpButton(int x, int y) {
	    super(CoreImage.load("SpeedUpButton.png").split(3, 2), x, y, true);
	    setPixelLevelChecks(false);
	    setKeyBinding(Input.KEY_Q);
	}

	@Override
	public void update(int t) {
	    super.update(t);
	    if (this.isClicked()) {
		spacebarHit.play();
		if (isSelected()) {
		    SPEED_MULTIPLIER = 3;
		} else {
		    SPEED_MULTIPLIER = 1;
		}
	    }
	}

    }

    private class MenuPauseButton extends Button {

	public MenuPauseButton(int x, int y) {
	    super(CoreImage.load("MenuPauseButton.png").split(1, 3), x, y);
	    super.setKeyBinding(Input.KEY_ESCAPE);
	}

	@Override
	public void update(int t) {
	    super.update(t);
	    if (isClicked()) {
		player.pause();
		buttonClicked.play();
		Stage.pushScene(new GamePausedScene(player));
	    }
	}

    }

    private class TrapButton extends Button {

	private TrapType trapType;
	private final ArrayList<String> info;

	public TrapButton(TrapType trapType, int x, int y) {
	    super(CoreImage.load(trapType + "-button.png").split(1, 3), x, y);
	    info = GameModel.getTrapInfo(trapType);
	    this.trapType = trapType;
	}

	@Override
	public void update(int elapsedTime) {
	    super.update(elapsedTime);
	    if (isMouseOver()) {
		unitInfoBubble
			.setLocation(Input.getMouseX(), Input.getMouseY());
		unitInfoBubble.showUnitInfo(info);
	    } else {
		if (unitInfoBubble.isShowing(info)) {
		    unitInfoBubble.hide();
		}
	    }
	    if (isClicked()) {
		trapToBuild = trapType;
		constructionMode = ConstructionMode.TRAP;
		buildTrapSound.play();
	    }
	}
    }

    private class TowerButton extends Button {

	private TowerType towerType;
	private final ArrayList<String> info;

	public TowerButton(TowerType towerType, int x, int y) {
	    super(CoreImage.load(towerType.toString() + "-tower-button.png")
		    .split(1, 3), x, y);
	    this.towerType = towerType;
	    info = GameModel.getTowerInfo(towerType);
	    int key = (towerType.equals(TowerType.FAST)) ? Input.KEY_F
		    : Input.KEY_S;
	    this.setKeyBinding(key);
	}

	@Override
	public void update(int elapsedTime) {
	    super.update(elapsedTime);
	    if (isMouseOver()) {
		unitInfoBubble
			.setLocation(Input.getMouseX(), Input.getMouseY());
		unitInfoBubble.showUnitInfo(info);
	    } else {
		if (unitInfoBubble.isShowing(info)) {
		    unitInfoBubble.hide();
		}
	    }
	    if (isClicked()) {
		towerToBuild = towerType;
		constructionMode = ConstructionMode.TOWER;
		towerClicked.play();
	    }
	}
    }

    private class ConstructionLayer extends Group {

	private FilledSprite mock;

	public ConstructionLayer() {
	    super();
	    setSize(480, 480);
	    setLocation(0, 0);

	    mock = new FilledSprite(CYAN);
	    mock.setSize(30, 30);
	    mock.setAnchor(0.5, 0.5);
	    mock.alpha.set(150);

	    add(mock);
	}

	@Override
	public void update(int et) {
	    if (this.isMouseOver()) {
		if (constructionMode.equals(ConstructionMode.NONE)) {
		    mock.visible.set(false);
		    super.update(et);
		    return;
		}

		updateUnitConstruction();
		super.update(et);
	    }
	}

	private void updateUnitConstruction() {
	    updateConstructionMock();
	    if (isMouseReleased()) {
		attemptUnitConstruction();
	    }
	}

	private void attemptUnitConstruction() {
	    switch (constructionMode) {
	    case TRAP: {
		if (canBuildTrap()) {
		    buildUnit(new Trap(trapToBuild, ForestDefender.this));
		} else {
		    invalidAction.play();
		}
		break;
	    }
	    case TOWER: {
		if (canBuildTower()) {
		    buildUnit(new Tower(towerToBuild, ForestDefender.this));
		} else {
		    invalidAction.play();
		}
		break;
	    }
	    }
	}

	private void updateConstructionMock() {
	    mock.setLocation(Input.getMouseX(), Input.getMouseY());
	    if (constructionMode.equals(ConstructionMode.TRAP)) {
		mock.setSize(20, 20);
	    } else {
		mock.setSize(30, 30);
	    }
	    mock.visible.set(true);
	}

	private boolean canBuildTower() {
	    return spriteContainsSprite(battlefield, mock)
		    && !(mapOverlay.intersects(mock))
		    && !isOccupied(towers, mock);
	}

	private boolean canBuildTrap() {
	    return spriteContainsSprite(mapOverlay, mock)
		    && !isOccupied(traps, mock);
	}

	private void buildUnit(GameUnit unit) {
	    unit.setAnchor(0.5, 0.5);
	    unit
		    .setLocation(Input.getMouseReleaseX(), Input
			    .getMouseReleaseY());
	    if (player.buy(unit.getPrice())) {
		// can afford
		construct(unit);
	    } else {
		// LOW ON FUNDS
		levelInfoPanel.showLowCash();
	    }
	}

	private void construct(GameUnit unit) {
	    if (unit instanceof Tower) {
		towers.add((Tower) unit);
		towerClicked.play();
	    } else if (unit instanceof Trap) {
		traps.add((Trap) unit);
		buildTrapSound.play();
	    }
	    unitLayer.add(unit);
	    // allow for multiple units to be placed
	    if (!Input.isShiftDown()) {
		stopConstruction();
	    }
	}

	private boolean isOccupied(Collection<? extends ImageSprite> sprites,
		FilledSprite mock) {
	    for (ImageSprite sprite : sprites) {
		if (sprite.intersects(mock)) {
		    return true;
		}
	    }
	    return false;
	}

	private boolean spriteContainsSprite(ImageSprite biggerSprite,
		FilledSprite smallerSprite) {
	    int mx = Input.getMouseReleaseX();
	    int my = Input.getMouseReleaseY();
	    int wh = smallerSprite.width.getAsInt() / 2;
	    int hh = smallerSprite.height.getAsInt() / 2;
	    Point tl = new Point(mx - wh, my - hh);
	    Point br = new Point(mx + wh, my + hh);
	    return biggerSprite.contains(tl.x, tl.y)
		    && biggerSprite.contains(br.x, br.y)
		    && biggerSprite.contains(br.x, tl.y)
		    && biggerSprite.contains(tl.x, br.y);
	}
    }

    private class RangeCircle extends ImageSprite {

	public RangeCircle() {
	    super(CoreImage.load("range-circle.png"), 240, 240);
	    alpha.set(0);
	}

	public void showTowerRange(Tower t) {
	    alpha.animateTo(170, 300);
	    x.animateTo(t.x.get(), 300);
	    y.animateTo(t.y.get(), 300);
	    // range is radius, so *2
	    width.animateTo(t.getRange() * 2, 300);
	    height.animateTo(t.getRange() * 2, 300);
	}

	public void hide() {
	    Timeline t = new Timeline();
	    t.animateTo(alpha, 0, 300);
	    t.after().setLocation(this, 240, 240);
	    addTimeline(t);
	}

    }

    public class UnitInfoBubble extends Group {

	private ArrayList<String> lastInfoShown;
	private Group textLayer;

	public UnitInfoBubble() {
	    Sprite bubbleImage = new ImageSprite(CoreImage
		    .load("InfoBubble.png"), 0, 0);
	    setSize(bubbleImage.width.get(), bubbleImage.height.get());
	    enabled.set(false);
	    add(bubbleImage);
	    textLayer = new Group();
	    textLayer.setSize(width.get(), height.get());
	    add(textLayer);
	    lastInfoShown = new ArrayList<String>(10);
	    alpha.set(0);
	    setAnchor(0.8, -0.2);
	}

	public boolean isShowing(ArrayList<String> info) {
	    return lastInfoShown.equals(info);
	}

	public void hide() {
	    if (alpha.get() > 0) {
		lastInfoShown = new ArrayList<String>(0);
		alpha.animateTo(0, 150);
	    }
	}

	public void showUnitInfo(ArrayList<String> unitInfo) {
	    if (unitInfo.equals(lastInfoShown) && alpha.get() > 0) {
		return;
	    }
	    lastInfoShown = unitInfo;
	    textLayer.removeAll();
	    int i = 0;
	    for (String s : unitInfo) {
		Label l = new Label(font, s, textLayer.x.get() + 20,
			textLayer.y.get() + (10 + (20 * i++)));
		textLayer.add(l);
	    }
	    alpha.animateTo(240, 300);
	}

    }

    public void removeMonster(MonsterSprite monsterSprite) {
	monsters.remove(monsterSprite);
	updateWaveLabels();
	monsterLayer.remove(monsterSprite);
	monsterLayer.remove(monsterSprite.healthIndicator);
	underAttack = (monsters.size() > 0);
    }

    public boolean isLastWave() {
	return waveNumber == gameLevel.getWaveCount();
    }

    public boolean isLastMonster() {
	return monsters.size() == 1;
    }

    public void win() {
	stopAllAction();
	player.win();
	Timeline t = new Timeline();
	FilledSprite whiteCurtain = new FilledSprite(WHITE);
	whiteCurtain.alpha.animate(0, 164, 500, Easing.BACK_OUT);
	t.addEvent(new AddSpriteEvent(mainLayer, whiteCurtain, 0));
	Group winLabel = Label.createMultilineLabel(yellowFont, CoreSystem
		.getTalkBackField("player_name")
		+ ", you made it!"
		+ "\nCONGRATULATIONS"
		+ "\nScore: "
		+ player.getScore()
		+ "\nYou shot "
		+ player.getKills()
		+ " creeps\nAnd it only took you: " + player.getTimeWasted(),
		320, 240);
	winLabel.setAnchor(0.5, 0.5);
	t.addEvent(new AddSpriteEvent(getMainLayer(), winLabel, 0));
	t.scale(winLabel, 2, 2, winLabel.width.get(), winLabel.height.get(),
		INITIAL_DELAY);
	t.animate(winLabel.angle, 0, 4 * Math.PI, INITIAL_DELAY,
		Easing.STRONG_IN_OUT);
	t.after().set(goToMenuOnClick, 1);
	addTimeline(t);
    }

    private void stopAllAction() {
	nextWaveButton.enabled.set(false);
	nextWaveButton.visible.set(false);
	deactivateAllTowers();
	monsters.clear();
	monsterLayer.removeAll();
	shotsLayer.removeAll();
    }

    private void deactivateAllTowers() {
	for (Tower t : towers) {
	    t.deactivate();
	}
    }

    public void defeat() {
	stopAllAction();
	Timeline t = new Timeline();
	FilledSprite blackDrape = new FilledSprite(BLACK);
	blackDrape.alpha.animate(0, 170, 500);
	t.addEvent(new AddSpriteEvent(mainLayer, blackDrape, 0));
	Group defeatLabel = Label.createMultilineLabel(yellowFont,
		"Good golly! You failed to protect your forest!\nTime wasted: "
			+ player.getTimeWasted(), 320, 240);
	defeatLabel.setAnchor(0.5, 0.5);
	t.addEvent(new AddSpriteEvent(getMainLayer(), defeatLabel, 0));
	t.scale(defeatLabel, 2, 2, defeatLabel.width.get(), defeatLabel.height
		.get(), INITIAL_DELAY);
	t.animate(defeatLabel.angle, 0, 4 * Math.PI, INITIAL_DELAY,
		Easing.STRONG_IN_OUT);
	t.after().set(goToMenuOnClick, 1);
	addTimeline(t);
    }

    public void showTowerRange(Tower tower) {
	rangeCircle.showTowerRange(tower);
    }

    private void removeHighlight(Collection<? extends ImageSprite> sprites) {
	for (ImageSprite s : sprites) {
	    s.setFilter(null);
	}
    }

    public void setInfoPanel(InformationPanel panelToDisplay, GameUnit unit) {
	if (currentInformationPanel != null) {
	    currentInformationPanel.alpha.set(0);
	    currentInformationPanel.visible.set(false);
	    currentInformationPanel.enabled.set(false);
	}
	panelToDisplay.visible.set(true);
	panelToDisplay.enabled.set(true);
	panelToDisplay.alpha.stopAnimation(true);
	panelToDisplay.alpha.animateTo(255, 400);
	currentInformationPanel = panelToDisplay;
	hideRangeAndHighlights(panelToDisplay, unit);
	if (unit != null) {
	    unit.setFilter(new Glow());
	    if (unit instanceof MonsterSprite) {
		((MonsterInfoPanel) panelToDisplay).observeUnit(unit, gameLevel
			.getWave(waveNumber));
	    } else {
		panelToDisplay.observeUnit(unit);
	    }
	}
    }

    private void hideRangeAndHighlights(InformationPanel panelToDisplay,
	    GameUnit unit) {
	removeHighlights();
	if (panelToDisplay instanceof TowerInfoPanel) {
	    rangeCircle.showTowerRange((Tower) unit);
	} else {
	    rangeCircle.hide();
	}
    }

    private void removeHighlights() {
	removeHighlight(monsters);
	removeHighlight(towers);
	removeHighlight(traps);
    }

    public void hideMonsterInfoPanel() {
	if (currentInformationPanel != null) {
	    if (currentInformationPanel instanceof MonsterInfoPanel) {
		setInfoPanel(levelInfoPanel, null);
	    }
	}
    }

    public static void uploadGameDetails(String gameStatus) {
	if (!dataUploaded) {
	    dataUploaded = true;
	    CoreSystem.setTalkBackField("time_wasted", player.getTimeWasted());
	    CoreSystem.setTalkBackField("seconds", "" + player.getSeconds());

	    CoreSystem.setTalkBackField("level_played", gameLevel.getName());
	    CoreSystem.setTalkBackField("game_status", gameStatus);
	    CoreSystem.setTalkBackField("wave_number", "" + waveNumber);
	    CoreSystem.setTalkBackField("kills", "" + player.getKills());
	    int score = 0;
	    if (gameStatus.equals("win")) {
		score = player.getScore();
	    }
	    CoreSystem.setTalkBackField("score", "" + score);
	    String URL = (!Build.DEBUG) ? "http://www.perhac.com/shared/forest-defender/playtime.php"
		    : "http://localhost:8080/playtime.php";
	    CoreSystem.uploadTalkBackFields(URL);
	}
    }

}
