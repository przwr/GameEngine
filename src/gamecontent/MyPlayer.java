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
import game.gameobject.GameObject;
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
import game.text.effects.TextController;
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

/**
 * @author przemek
 */
public class MyPlayer extends Player {

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
    private Weapon universal = new Weapon(0, 0, "Bare Hands", place, 0, null, (short) -1, UNIVERSAL);
    private ArrayList<InteractionSet> actionSets = new ArrayList<>();
    private int activeActionSet;
    private Point centralPoint;
    private MyGUI gui;
    private float jumpDelta = 22.6f; //Potrzebne?

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
            Weapon sword = new Weapon(0, 0, "Sword", place, 2, null, (short) -1, SWORD);
            this.weapon.setWearing(true);
            sword.setModifier(1.2f);
            firstWeapon = sword;
            Weapon bow = new Weapon(0, 0, "Bow", place, 2, null, (short) -1, BOW);
            bow.setModifier(5f);
            secondWeapon = bow;
        }
        activeWeapon = universal;

        // TODO Interactives powinny być raz stworzone w Skillach!
        boolean done = false;
        for (int attack = 0; attack >= 0; attack++) {
            switch (attack) {
                case ATTACK_SLASH:
                    actionSets.get(1).addInteractionToNextFree(Interactive.create(this, new UpdateBasedActivator(),
                            new CurveInteractiveCollision(42, 32, 0, 75, 150), STRENGTH_HURT, SWORD, (byte) attack, 2f, 2.5f, false));
                    break;
                case ATTACK_THRUST:
                    actionSets.get(1).addInteractionToNextFree(Interactive.create(this, new UpdateBasedActivator(),
                            new LineInteractiveCollision(52, 10, 6, 65, 24), STRENGTH_HURT, SWORD, (byte) attack, 2.5f, 1.5f, false));
                    break;
                case ATTACK_WEAK_PUNCH:
                    actionSets.get(0).addInteractionToNextFree(Interactive.create(this, new UpdateBasedActivator(),
                            new LineInteractiveCollision(72, 12, 2, 36, 20), STRENGTH_HURT, UNIVERSAL, (byte) attack, 1f, 2f, false));
                    actionSets.get(1).setInteraction(2, 0, actionSets.get(0).getFirstInteractive());
                    break;
                case ATTACK_STRONG_PUNCH:
                    actionSets.get(0).addInteractionToNextFree(Interactive.create(this, new UpdateBasedActivator(),
                            new LineInteractiveCollision(72, 12, 2, 36, 20), STRENGTH_HURT, UNIVERSAL, (byte) attack, 1.5f, 2f, false));
                    actionSets.get(1).setInteraction(2, 1, actionSets.get(0).getSecondInteractive());
                    break;
                case ATTACK_UPPER_SLASH:
                    actionSets.get(1).addInteractionToNextFree(Interactive.create(this, new UpdateBasedActivator(),
                            new LineInteractiveCollision(0, 128, 10, 65, 40), STRENGTH_HURT, SWORD, (byte) attack, 2f, 5f, false));
                    break;
                case ATTACK_NORMAL_ARROW_SHOT:
                    InteractiveAction arrow = new InteractiveActionArrow();
                    actionSets.get(2).addInteractionToNextFree(Interactive.createSpawner(this, new UpdateBasedActivator(), arrow, BOW, (byte) attack));
                    break;
                default:
                    done = true;
                    break;
            }
            if (done) {
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
            this.weapon.setWearing(true);
        } else {
            items.add(weapon);
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
        Interactive attack = actionSets.get(activeActionSet).getFirstInteractive();
        if (attack != null) {
            return attack.getAttackType();
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


    public void interact() {
        for (GameObject object : map.getInteractiveObjects()) {
//            TODO wyświetlać listę do wyboru, jeśli wiecej, niż 1 który da się aktywować
            if (!getTextController().isStarted() && Methods.pointDistanceSimple(object.getX(), object.getY(),
                    getX(), getY()) <= Place.tileSize * 1.5 + Math.max(object.getActualWidth(), object.getActualHeight()) / 2) {
                if (Math.abs(Methods.angleDifference(getDirection(),
                        (int) Methods.pointAngleCounterClockwise(getX(), getY(), object.getX(), object.getY()))) <= 80) {
                    object.interact(this);
                    break;
                }
            }
        }
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
        setEmitter(true);
        setEmits(false);
        centralPoint = new Point(0, 0);

        appearance = new ClothedAppearance(place, 200, characterName, width);
        loadClothes();
        Point[] renderPoints = place.getStartPointFromFile("characters/" + characterName);
        centralPoint = renderPoints[0];

        setVisible(true);
        setHasStaticShadow(true);
        depth = 0;
        setResistance(2);
        if (lights == null) {
            lights = new ArrayList<>(1);
        }
        if (lights.isEmpty()) {
            addLight(Light.create(place.getSpriteInSize("light", "", 768, 768), new Color(0.85f, 0.85f, 0.85f), 768, 768, this));
        }
        setCollision(Rectangle.create(width, (int) (width * Methods.ONE_BY_SQRT_ROOT_OF_2), OpticProperties.NO_SHADOW, this));
        stats = new PlayerStats(this);
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
        shirt = loadCloth("shirt", Cloth.CLOTH_TYPE);
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
        weapon.setWearing(false);
        ((ClothedAppearance) appearance).setClothes(head, torso, legs, cap, hair, shirt, gloves, pants, boots, weapon,
                loadCloth("bow", Cloth.WEAPON_TYPE),
                loadCloth("shield", Cloth.WEAPON_TYPE));
    }

    public void randomizeClothes() {
        RandomGenerator r = RandomGenerator.create();
        cap.setWearing(false);
        shirt.setWearing(false);
        boots.setWearing(false);
        pants.setWearing(false);
        gloves.setWearing(false);
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

    private void pushOtherPlayers() {
        GameObject other;
        if (place.playersCount > 1) {
            for (int i = 0; i < place.playersCount; i++) {
                other = place.players[i];
                if (Methods.pointDifference(getX(), getY(), other.getX(), other.getY()) < collision.getWidthHalf()) {
                    xEnvironmentalSpeed += (x - other.getXInDouble()) / 10;
                    yEnvironmentalSpeed += (y - other.getYInDouble()) / 10;
                }
            }
        }
    }

    @Override
    protected boolean isCollided(double xMagnitude, double yMagnitude) {
        return !Main.key.key(Keyboard.KEY_TAB) //DO TESTÓW DEMO
                && isInGame() && collision.isCollideSolid((int) (getXInDouble() + xMagnitude), (int) (getYInDouble() + yMagnitude), map);
    }

    @Override
    public void render() {
        if (appearance != null) {
            Drawer.regularShader.translate(getX(), (int) (getY() - floatHeight));
            //Drawer.drawCircle(0, 0, 10, 10);
            if (Main.SHOW_INTERACTIVE_COLLISION) {
                for (Interactive interactive : interactiveObjects) {
                    interactive.render();
                }
            }
            if (colorAlpha < 1f) {
                Drawer.setColorAlpha(colorAlpha);
                appearance.renderPart(0, appearance.getWidth());
                Drawer.refreshColor();
            } else if (((ClothedAppearance) appearance).isUpToDate()) {
                appearance.render();
            }
        }
    }

    @Override
    public void update() {
        appearance.updateFrame();
        appearance.updateTexture(this);
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
            pushOtherPlayers();
            updateChangers();
            updateWithGravity();
            moveWithSliding(xEnvironmentalSpeed + xSpeed, yEnvironmentalSpeed + ySpeed);
            if (camera != null) {
                camera.updateSmooth();
            }
            if (area != -1) {
                for (WarpPoint wp : this.map.getArea(area).getNearWarps()) {
                    if (wp.getCollision() != null && wp.getCollision().isCollideSingle(wp.getX(), wp.getY(), collision)) {
                        wp.warp(this);
                        break;
                    }
                }
            }
            brakeOthers();
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
        for (WarpPoint wp : this.map.getArea(area).getNearWarps()) {
            if (wp.getCollision() != null && wp.getCollision().isCollideSingle(wp.getX(), wp.getY(), collision)) {
                wp.warp(this);
                break;
            }
        }
        brakeOthers();
        if (online.server != null) {
            online.server.sendUpdate(this.map.getID(), getX(), getY(), isEmits(), isHop());
        } else if (online.client != null) {
            online.client.sendPlayerUpdate(this.map.getID(), playerID, getX(), getY(), isEmits(), isHop());
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
    public void renderShadowLit(Figure figure) {
        if (appearance != null) {
            Drawer.drawShapeLit(appearance, getX(), getY() - (int) floatHeight);
        }
    }

    @Override
    public void renderShadow(Figure figure) {
        if (appearance != null) {
            Drawer.drawShapeBlack(appearance, collision.getDarkValue(), getX(), getY() - (int) floatHeight);
        }
    }

    @Override
    public void renderShadowLit(int xStart, int xEnd) {
        if (appearance != null) {
            Drawer.drawShapePartLit(appearance, getX(), getY() - (int) floatHeight, xStart, xEnd);
        }
    }

    @Override
    public void renderShadow(int xStart, int xEnd) {
        if (appearance != null) {
            Drawer.drawShapePartBlack(appearance, collision.getDarkValue(), getX(), getY() - (int) floatHeight, xStart, xEnd);
        }
    }


    public MyGUI getGUI() {
        return gui;
    }
}
