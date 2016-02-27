/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import collision.Figure;
import collision.OpticProperties;
import collision.Rectangle;
import engine.Main;
import engine.lights.Light;
import engine.systemcommunication.Time;
import engine.utilities.*;
import game.gameobject.entities.Player;
import game.gameobject.inputs.InputKeyBoard;
import game.gameobject.interactive.InteractionSet;
import game.gameobject.interactive.Interactive;
import game.gameobject.interactive.action.InteractiveAction;
import game.gameobject.interactive.action.InteractiveActionArrow;
import game.gameobject.interactive.activator.UpdateBasedActivator;
import game.gameobject.interactive.collision.CurveInteractiveCollision;
import game.gameobject.interactive.collision.LineInteractiveCollision;
import game.gameobject.items.Weapon;
import game.gameobject.stats.PlayerStats;
import game.place.Place;
import game.place.map.Map;
import game.place.map.WarpPoint;
import game.text.TextController;
import gamecontent.equipment.Cloth;
import net.jodk.lang.FastMath;
import net.packets.MultiPlayerUpdate;
import net.packets.Update;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;
import sprites.ClothedAppearance;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import static game.gameobject.interactive.Interactive.STRENGTH_HURT;
import static game.gameobject.items.Weapon.*;
import static gamecontent.MyController.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public class MyPlayer extends Player {

    private final int framesPerDir = 52;
    private final String characterName = "aria";
    private Cloth head = Cloth.nullCloth;
    private Cloth torso = Cloth.nullCloth;
    private Cloth legs = Cloth.nullCloth;
    private Cloth nudeTorso = Cloth.nullCloth;
    private Cloth nudeLegs = Cloth.nullCloth;
    private Cloth cap = Cloth.nullCloth;
    private Cloth hair = Cloth.nullCloth;
    private Cloth shirt = Cloth.nullCloth;
    private Cloth gloves = Cloth.nullCloth;
    private Cloth pants = Cloth.nullCloth;
    private Cloth boots = Cloth.nullCloth;
    private Cloth weapon = Cloth.nullCloth;
    private Weapon firstWeapon;
    private Weapon secondWeapon;
    private Weapon lastWeapon;
    private Weapon universal = new Weapon("Hands", UNIVERSAL);
    private ArrayList<InteractionSet> actionSets = new ArrayList<>();
    private int activeActionSet;
    private TextController textControl;
    private Point centralPoint;
    private MyGUI gui;
    private float jumpDelta = 22.6f;
    private long lastEnergyUp = 0;

    public MyPlayer(boolean first, String name) {
        super(name);
        this.first = first;
        if (first) {
            initializeControllerForFirst();
        } else {
            initializeController();
        }
    }

    private void initializeAttacks() {
        actionSets.add(new InteractionSet(UNIVERSAL));
        actionSets.add(new InteractionSet(SWORD));
        actionSets.add(new InteractionSet(BOW));
        if (!Main.TEST) {
            Weapon sword = new Weapon("Sword", SWORD);
            sword.setModifier(1.2f);
            firstWeapon = sword;
            Weapon bow = new Weapon("Bow", BOW);
            bow.setModifier(5f);
            secondWeapon = bow;
        }
        activeWeapon = universal;

        // TODO Interactives powinny być raz stworzone w Skillach!
        int[] attacks = ((MyController) playerController).getAttackFrames();
        for (int attack = 0; attack < attacks.length; attack++) {
            int[] frames = new int[8];
            for (int i = 0; i < frames.length; i++) {
                frames[i] = i * framesPerDir + attacks[attack];
            }
            switch (attack) {
                case ATTACK_SLASH:
                    actionSets.get(1).addInteractionToNextFree(Interactive.create(this, new UpdateBasedActivator(),
                            new CurveInteractiveCollision(42, 32, 0, 70, 150), STRENGTH_HURT, SWORD, (byte) attack, 2f, 2.5f));
                    break;
                case ATTACK_THRUST:
                    actionSets.get(1).addInteractionToNextFree(Interactive.create(this, new UpdateBasedActivator(),
                            new LineInteractiveCollision(52, 10, 6, 60, 24), STRENGTH_HURT, SWORD, (byte) attack, 2.5f, 1.5f));
                    break;
                case ATTACK_WEAK_PUNCH:
                    actionSets.get(0).addInteractionToNextFree(Interactive.create(this, new UpdateBasedActivator(),
                            new LineInteractiveCollision(72, 12, 2, 36, 20), STRENGTH_HURT, UNIVERSAL, (byte) attack, 1f, 2f));
                    actionSets.get(1).setInteraction(2, 0, actionSets.get(0).getFirstInteractive());
                    break;
                case ATTACK_STRONG_PUNCH:
                    actionSets.get(0).addInteractionToNextFree(Interactive.create(this, new UpdateBasedActivator(),
                            new LineInteractiveCollision(72, 12, 2, 36, 20), STRENGTH_HURT, UNIVERSAL, (byte) attack, 1.5f, 2f));
                    actionSets.get(1).setInteraction(2, 1, actionSets.get(0).getSecondInteractive());
                    break;
                case ATTACK_UPPER_SLASH:
                    actionSets.get(1).addInteractionToNextFree(Interactive.create(this, new UpdateBasedActivator(),
                            new LineInteractiveCollision(0, 128, 10, 55, 40), STRENGTH_HURT, SWORD, (byte) attack, 2f, 5f));
                    break;
                case ATTACK_NORMAL_ARROW_SHOT:
                    InteractiveAction arrow = new InteractiveActionArrow();
                    actionSets.get(2).addInteractionToNextFree(Interactive.createSpawner(this, new UpdateBasedActivator(), arrow, BOW, (byte) attack));
                    break;
            }
            updateActionSets();
            for (InteractionSet a : actionSets) {
                Interactive i = a.getFirstInteractive();
                if (i != null) {
                    i.setHalfEnvironmentalCollision(true);
                }
                i = a.getSecondInteractive();
                if (i != null) {
                    i.setHalfEnvironmentalCollision(true);
                }
            }
            gui.changeAttackIcon(getFirstAttackType(), getSecondAttackType());
        }

        for (InteractionSet set : actionSets) {
            for (Interactive interactive : set.getAllInteractives()) {
                if (!interactiveObjects.contains(interactive)) {
                    addInteractive(interactive);
                }
            }
        }

    }

    public void addWeapon(Weapon weapon) {
        if (firstWeapon == null) {
            firstWeapon = weapon;
        } else if (secondWeapon == null) {
            secondWeapon = weapon;
        } else {
//            TODO add to backpack
        }
    }

    public Weapon getFirstWeapon() {
        return firstWeapon;
    }

    public Weapon getSecondWeapon() {
        return secondWeapon;
    }

    public byte getFirstAttackType() {
        Interactive first = actionSets.get(activeActionSet).getFirstInteractive();
        if (first != null) {
            return first.getAttackType();
        } else {
            return -1;
        }
    }

    public byte getSecondAttackType() {
        Interactive second = actionSets.get(activeActionSet).getSecondInteractive();
        if (second != null) {
            return second.getAttackType();
        } else {
            return -1;
        }
    }

    public void setActionPair(int pair) {
        actionSets.get(activeActionSet).setActivePair(pair);
    }

    public int getActiveActionPairID() {
        return actionSets.get(activeActionSet).getActivePair();
    }

    public boolean changeWeapon() {
        if (activeWeapon == universal) {
            if (lastWeapon != null && lastWeapon != universal) {
                activeWeapon = lastWeapon;
            } else if (firstWeapon != null) {
                activeWeapon = firstWeapon;
            } else if (secondWeapon != null) {
                activeWeapon = secondWeapon;
            }
        } else {
            if (activeWeapon == firstWeapon && secondWeapon != null) {
                activeWeapon = secondWeapon;
            } else if (activeWeapon == secondWeapon && firstWeapon != null) {
                activeWeapon = firstWeapon;
            } else if (activeWeapon == null) {
                activeWeapon = universal;
            }
        }
        return updateActionSets();
    }

    public boolean hideWeapon() {
        if (activeWeapon != universal) {
            lastWeapon = activeWeapon;
        }
        activeWeapon = universal;
        return updateActionSets();
    }

    private boolean updateActionSets() {
        if (activeWeapon.getType() != actionSets.get(activeActionSet).getWeaponType()) {
            for (int i = 0; i < actionSets.size(); i++) {
                if (actionSets.get(i).getWeaponType() == activeWeapon.getType()) {
                    activeActionSet = i;
                    break;
                } else if (actionSets.get(activeActionSet).getWeaponType() != UNIVERSAL && actionSets.get(i).getWeaponType() == UNIVERSAL) {
                    activeActionSet = i;
                }
            }
            return true;
        }
        return false;
    }

    private void initializeControllerForFirst() {
        playerController = new MyController(this, gui);
        playerController.inputs[0] = new InputKeyBoard(Keyboard.KEY_UP);
        playerController.inputs[1] = new InputKeyBoard(Keyboard.KEY_DOWN);
        playerController.inputs[2] = new InputKeyBoard(Keyboard.KEY_RETURN);
        playerController.inputs[3] = new InputKeyBoard(Keyboard.KEY_ESCAPE);
        playerController.inputs[4] = new InputKeyBoard(Keyboard.KEY_LEFT);
        playerController.inputs[5] = new InputKeyBoard(Keyboard.KEY_RIGHT);
        playerController.initialize();
    }

    private void initializeController() {
        playerController = new MyController(this, gui);
        playerController.initialize();
    }

    @Override
    public void initializeSetPosition(int width, int height, Place place, int x, int y) {
        initialize(width, height, place);
        initialize(name, x, y);
        spawnPosition.set(getX(), getY());
    }

    @Override
    public void initialize(int width, int height, Place place) {
        this.place = place;
        this.online = place.game.online;
        emitter = true;
        emits = false;
        centralPoint = new Point(0, 0);

        appearance = new ClothedAppearance(place, 200, characterName, width);
        loadClothes();
        //randomizeClothes();

        Point[] dims = calculateDimensions();
        Point[] renderPoints = place.getStartPointFromFile("characters/" + characterName);
        centralPoint = renderPoints[0];

        /*appearance = Animation.createFBOAnimation(place.getSpriteSheet("test", "characters/" + characterName), 200, framesPerDir, dims[0], dims[1],
         centralPoint);*/
        visible = true;
        depth = 0;
        setResistance(2);
        if (lights == null) {
            lights = new ArrayList<>(1);
        }
        if (lights.isEmpty()) {
            addLight(Light.create(place.getSpriteInSize("light", "", 768, 768), new Color(0.85f, 0.85f, 0.85f), 768, 768, this));
        }
        setCollision(Rectangle.create(width, (int) (width * Methods.ONE_BY_SQRT_ROOT_OF_2), OpticProperties.NO_SHADOW, this));
//        collision.setCollide(false);
        stats = new PlayerStats(this);
//        stats.setMaxHealth(1000);
//        stats.setHealth(1000);
        textControl = new TextController(place);
        addGui(textControl);
        gui = new MyGUI("Player " + name + "'s GUI", place);
        addGui(gui);
        ((MyController) playerController).setPlayersGUI(gui);
        initializeAttacks();
        addPushInteraction();
    }

    private void loadClothes() {
        cap = loadCloth("cap", Cloth.CLOTH_TYPE);
        shirt = loadCloth(/*"tshirt"*/"shirt", Cloth.CLOTH_TYPE);
        boots = loadCloth("boots", Cloth.CLOTH_TYPE);
        pants = loadCloth("dress", Cloth.CLOTH_TYPE);
        gloves = loadCloth("gloves", Cloth.CLOTH_TYPE);
        weapon = loadCloth("sword", Cloth.WEAPON_TYPE);

        head = loadCloth("head", Cloth.BODY_TYPE);
        hair = loadCloth("hair", Cloth.BODY_TYPE);
        torso = loadCloth("torso", Cloth.BODY_TYPE);
        legs = loadCloth("leg", Cloth.BODY_TYPE);
        nudeTorso = loadCloth("nudetorso", Cloth.BODY_TYPE);
        nudeLegs = loadCloth("nudeleg", Cloth.BODY_TYPE);
        ((ClothedAppearance) appearance).setClothes(head, nudeTorso, nudeLegs, cap, hair, shirt, gloves, pants, boots, weapon,
                loadCloth("bow", Cloth.WEAPON_TYPE),
                loadCloth("shield", Cloth.WEAPON_TYPE));
    }

    public void randomizeClothes() {
        RandomGenerator r = RandomGenerator.create();
        cap.setWearing(r.chance(50));
        shirt.setWearing(r.chance(50));
        boots.setWearing(r.chance(50));
        pants.setWearing(r.chance(50));
        gloves.setWearing(r.chance(50));
    }

    private Cloth loadCloth(String name, String type) {
        try {
            return new Cloth(name, type, characterName, place);
        } catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage());
        }
        return Cloth.nullCloth;
    }

    private Point[] calculateDimensions() {
        Point[] dims = Cloth.getMergedDimensions(
                head, torso, legs, hair,
                cap, shirt, gloves, pants, boots, weapon);
        int tempx = dims[0].getX(), tempy = dims[0].getY();
        dims[0].set(Methods.roundUpToBinaryNumber(dims[0].getX()),
                Methods.roundUpToBinaryNumber(dims[0].getY()));
        tempx = dims[0].getX() - tempx;
        tempy = dims[0].getY() - tempy;
        dims[1].set(centralPoint.getX() - (dims[1].getX() - tempx / 2),
                centralPoint.getY() - (dims[1].getY() - tempy / 2));
        return dims;
    }

    @Override
    protected boolean isCollided(double xMagnitude, double yMagnitude) {
        return !Main.key.key(Keyboard.KEY_TAB) //DO TESTÓW
                && isInGame() && collision.isCollideSolid((int) (getXInDouble() + xMagnitude), (int) (getYInDouble() + yMagnitude), map);
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef((int) (getX() * Place.getCurrentScale() + xEffect), (int) (getY() * Place.getCurrentScale() + yEffect), 0);
            Drawer.setColorStatic(JUMP_SHADOW_COLOR);
            Drawer.drawEllipse(0, 0, Methods.roundDouble(collision.getWidth() * Place.getCurrentScale() / 2f), Methods.roundDouble(collision.getHeight()
                    * Place.getCurrentScale() / 2f), 24);
            glTranslatef(0, -(int) (floatHeight * Place.getCurrentScale()), 0);
            Drawer.refreshColor();
            Drawer.renderStringCentered(name, 0, -(((appearance.getActualHeight() + Place.tileHalf) * Place.getCurrentScale()) / 2), place.standardFont,
                    Drawer.getCurrentColor());
            Drawer.refreshColor();
            glPopMatrix();

            if (Main.SHOW_INTERACTIVE_COLLISION) {
                interactiveObjects.stream().forEach((interactive) -> {
                    interactive.render(xEffect, yEffect);
                });
            }

            glPushMatrix();
            glTranslatef(xEffect, yEffect, 0);
            glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
            glTranslatef(getX(), (int) (getY() - floatHeight), 0);
            Drawer.setCentralPoint();
            if (colorAlpha < 1f) {
                Drawer.setColorAlpha(colorAlpha);
            }
            if (((ClothedAppearance) appearance).isUpToDate()) {
                appearance.render();
            }
            if (colorAlpha < 1f) {
                Drawer.refreshColor();
            }

            appearance.updateFrame();
            glPopMatrix();
        }
    }

    public void preRenderGroundGUI() {
//        gui.getFrameBufferObject().activate();
//        glPushMatrix();
//        clearScreen(0);
//        glTranslatef(gui.getFrameBufferObject().getWidth() / 2, -gui.getFrameBufferObject().getHeight() / 2 + Display.getHeight() - 1, 0);
//        renderLifeIndicator();
//        renderEnergyIndicator();
//        glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
//        Drawer.drawShapeInShade(appearance, 0);
//        glPopMatrix();
//        gui.getFrameBufferObject().deactivate();
    }

    private void renderLifeIndicator() {
//        int halfLifeAngle = 180, startAngle, endAngle;
//        int minimumLifePercentage = Methods.roundDouble(45f / (collision.getHeight() * Place.getCurrentScale() / 2f));
//        int lifePercentageAngle = Methods.roundDouble(stats.getHealth() * halfLifeAngle / (float) stats.getMaxHealth());
//        if (lifePercentageAngle < minimumLifePercentage && stats.getHealth() != 0) {
//            lifePercentageAngle = minimumLifePercentage;
//        }
//        startAngle = 90;
//        endAngle = lifePercentageAngle + 90;
//        int precision = (12 * lifePercentageAngle) / halfLifeAngle;
//        if (precision == 0) {
//            precision = 1;
//        }
//        Drawer.setColor(Drawer.setPercentToRGBColor((halfLifeAngle - lifePercentageAngle) * 100 / halfLifeAngle, gui.getLifeColor()));
//        Drawer.drawEllipseBow(0, 0, Methods.roundDouble(collision.getWidth() * Place.getCurrentScale() / 2f), Methods.roundDouble(collision.getHeight()
//                * Place.getCurrentScale() / 2f), Methods.roundDouble(4 * Place.getCurrentScale()), startAngle, endAngle, precision);
    }

    private void renderEnergyIndicator() {
//        PlayerStats playerStats = (PlayerStats) stats;
//        int halfEnergyAngle = 180, startAngle, endAngle;
//        int minimumEnergyPercentage = Methods.roundDouble(45f / (collision.getHeight() * Place.getCurrentScale() / 2f));
//        int energyPercentageAngle = Methods.roundDouble(playerStats.getEnergy() * halfEnergyAngle / playerStats.getMaxEnergy());
//        if (energyPercentageAngle < minimumEnergyPercentage && playerStats.getEnergy() != 0) {
//            energyPercentageAngle = minimumEnergyPercentage;
//        }
//        startAngle = 450 - energyPercentageAngle;
//        endAngle = 450;
//        int precision = (12 * energyPercentageAngle) / halfEnergyAngle;
//        if (precision == 0) {
//            precision = 1;
//        }
//        Drawer.setColorStatic(gui.getEnergyColor());
//        Drawer.drawEllipseBow(0, 0, Methods.roundDouble(collision.getWidth() * Place.getCurrentScale() / 2f), Methods.roundDouble(
//                collision.getHeight() * Place.getCurrentScale() / 2f), Methods.roundDouble(4 * Place.getCurrentScale()), startAngle, endAngle, precision);
    }

    @Override
    public void renderClothedUpperBody(int frame) {
    }

    @Override
    public void renderClothedLowerBody(int frame) {
    }

    @Override
    public void update() {
        if (((ClothedAppearance) appearance).isUpToDate()) {
            if (warp != null) {
                warp.warp(this);
            }
            if (jumping) {
                hop = false;
                floatHeight = FastMath.abs(Methods.xRadius(jumpDelta * 4, 270));
                jumpDelta += Time.getDelta();
                if ((int) jumpDelta >= 68) {
                    jumping = false;
                    jumpDelta = 22.6f;
                }
            }
            updateChangers();
            updateWithGravity();
            moveWithSliding(xEnvironmentalSpeed + xSpeed, yEnvironmentalSpeed + ySpeed);
            if (camera != null) {
                camera.updateSmooth();
            }
            if (area != -1) {
                for (WarpPoint wp : map.getArea(area).getNearWarps()) {
                    if (wp.getCollision() != null && wp.getCollision().isCollideSingle(wp.getX(), wp.getY(), collision)) {
                        wp.warp(this);
                        break;
                    }
                }
            }
            brakeOthers();
            appearance.updateTexture(this);
            updateEnergy();
        }
    }

    private void updateEnergy() {
        float energyGain = 0.009f;
        if (lastEnergyUp > 0) {
            long current = Place.getDayCycle().getCurrentTimeInMiliSeconds();
            long difference = current - lastEnergyUp;
            if (((MyController) playerController).isRunning() && getSpeed() > 0 && isAbleToMove()) {
                float decrease = 0.4f - energyGain * difference;
                if (decrease < 0) {
                    decrease = 0;
                }
                ((PlayerStats) stats).decreaseEnergy(decrease);
            } else if (!stats.isProtectionState()) {
                ((PlayerStats) stats).increaseEnergy(energyGain * difference);
            }
            lastEnergyUp = current;
        } else {
            lastEnergyUp = Place.getDayCycle().getCurrentTimeInMiliSeconds();
        }
    }

    @Override
    public synchronized void sendUpdate() {
        if (jumping) {
            floatHeight = FastMath.abs(Methods.xRadius(jumpDelta * 4, 70));
            jumpDelta += Time.getDelta();
            if ((int) jumpDelta >= 68) {
                jumping = false;
                jumpDelta = 22.6f;
            }
        }
        moveWithSliding(xEnvironmentalSpeed + xSpeed, yEnvironmentalSpeed + ySpeed);
        for (WarpPoint wp : map.getArea(area).getNearWarps()) {
            if (wp.getCollision() != null && wp.getCollision().isCollideSingle(wp.getX(), wp.getY(), collision)) {
                wp.warp(this);
                break;
            }
        }
        brakeOthers();
        if (online.server != null) {
            online.server.sendUpdate(map.getID(), getX(), getY(), isEmits(), isHop());
        } else if (online.client != null) {
            online.client.sendPlayerUpdate(map.getID(), playerID, getX(), getY(), isEmits(), isHop());
            online.pastPositions[online.pastPositionsNumber++].set(getX(), getY());
            if (online.pastPositionsNumber >= online.pastPositions.length) {
                online.pastPositionsNumber = 0;
            }
        } else {
            online.game.endGame();
        }
        hop = false;
    }

    @Override
    public synchronized void updateRest(Update update) {
        try {
            Map currentMap = getPlace().getMapById(((MultiPlayerUpdate) update).getMapId());
            if (currentMap != null && this.map != currentMap) {
                changeMap(currentMap, getX(), getY());
            }
            if (((MultiPlayerUpdate) update).isHop()) {
                setJumping(true);
            }
            setEmits(((MultiPlayerUpdate) update).isEmits());
        } catch (Exception exception) {
            String error = "ERROR: - " + exception.getMessage() + " in " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this
                    .getClass();
            ErrorHandler.logAndPrint(error);
        }
    }

    @Override
    public synchronized void updateOnline() {
        try {
            if (jumping) {
                hop = false;
                floatHeight = FastMath.abs(Methods.xRadius(jumpDelta * 4, 70));
                jumpDelta += Time.getDelta();
                if ((int) jumpDelta == 68) {
                    jumping = false;
                    jumpDelta = 22.6f;
                }
            }
        } catch (Exception exception) {
            String error = "ERROR: - " + exception.getMessage() + " in " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this
                    .getClass();
            ErrorHandler.logAndPrint(error);
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, Figure figure) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) floatHeight, 0);
            Drawer.drawShapeInShade(appearance, 1);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) floatHeight, 0);
            Drawer.drawShapeInBlack(appearance);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, int xStart, int xEnd) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) floatHeight, 0);
            Drawer.drawShapePartInShade(appearance, 1, xStart, xEnd);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, int xStart, int xEnd) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) floatHeight, 0);
            Drawer.drawShapePartInBlack(appearance, xStart, xEnd);
            glPopMatrix();
        }
    }

    public TextController getTextController() {
        return textControl;
    }

    public MyGUI getGUI() {
        return gui;
    }
}
