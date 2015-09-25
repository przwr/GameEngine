/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import collision.Figure;
import collision.OpticProperties;
import collision.Rectangle;
import engine.lights.Light;
import engine.systemcommunication.Time;
import engine.utilities.*;
import game.gameobject.entities.Player;
import game.gameobject.inputs.InputKeyBoard;
import game.gameobject.interactive.*;
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
import sprites.Animation;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import static game.gameobject.interactive.Interactive.HURT;
import static game.gameobject.items.Weapon.SWORD;
import static game.gameobject.items.Weapon.UNIVERSAL;
import static gamecontent.MyController.*;
import static org.lwjgl.opengl.GL11.*;


/**
 * @author przemek
 */
public class MyPlayer extends Player {

    private final int framesPerDir = 46;
    private final String characterName = "aria";
    private Cloth head;
    private Cloth torso;
    private Cloth legs;

    private Cloth hat;
    private Cloth hair;
    private Cloth shirt;
    private Cloth gloves;
    private Cloth pants;
    private Cloth boots;

    private Cloth weapon;
    private Weapon activeWeapon;
    private Weapon firstWeapon;
    private Weapon secondWeapon;
    private Weapon lastWeapon;
    private Weapon universal = new Weapon("Hands", UNIVERSAL);
    private ArrayList<InteractionSet> actionSets = new ArrayList<>();
    private int activeActionSet;
    private TextController textControl;

    private MyGUI gui;

    //---------<('.'<) TYMCZASOWE!-------------//
    private float jumpDelta = 22.6f;
    //private SpriteSheet test, testBody;    //NIE KASOWAĆ! <('o'<)
    //float testIndex = 0;
    //---------------------------------------//

    public MyPlayer(boolean first, String name) {
        super(name);
        this.first = first;
        if (first) {
            initializeControllerForFirst();
        } else {
            initializeController();
        }
        initializeAttacks();
        stats = new PlayerStats(this);
    }

    private void initializeAttacks() {
        actionSets.add(new InteractionSet(UNIVERSAL));
        actionSets.add(new InteractionSet(SWORD));
        Weapon sword = new Weapon("Sword", SWORD);
        activeWeapon = universal;
        firstWeapon = sword;
        activeActionSet = 0;

        // TODO Interactives powinny być raz stworzone w Skillach!

        int[] attacks = ((MyController) playerController).getAttackFrames();
        for (int attack = 0; attack < attacks.length; attack++) {
            int[] frames = new int[8];
            for (int i = 0; i < frames.length; i++) {
                frames[i] = i * framesPerDir + attacks[attack];
            }
            switch (attack) {
                case ATTACK_SLASH:
                    actionSets.get(1).addInteractionToNextFree(new Interactive(this, 
                            new InteractiveActivatorFrames(frames), 
                            new CurveInteractiveCollision(42, 32, 0, 64, 120), 
                            HURT, SWORD, (byte) attack, 2f));
                    break;
                case ATTACK_THRUST:
                    actionSets.get(1).addInteractionToNextFree(new Interactive(this, 
                            new InteractiveActivatorFrames(frames), 
                            new LineInteractiveCollision(52, 10, 6, 84, 24), 
                            HURT, SWORD, (byte) attack, 2.5f));
                    break;
                case ATTACK_WEAK_PUNCH:
                    actionSets.get(0).addInteractionToNextFree(new Interactive(this, 
                            new InteractiveActivatorFrames(frames), 
                            new LineInteractiveCollision(72, 12, 2, 30, 20), 
                            HURT, UNIVERSAL, (byte) attack, 1f));
                    actionSets.get(1).setInteraction(2, 0, actionSets.get(0).getFirstInteractive());
                    break;
                case ATTACK_STRONG_PUNCH:
                    actionSets.get(0).addInteractionToNextFree(new Interactive(this, 
                            new InteractiveActivatorFrames(frames), 
                            new LineInteractiveCollision(72, 12, 2, 34, 20), 
                            HURT, UNIVERSAL, (byte) attack, 1.5f));
                    actionSets.get(1).setInteraction(2, 1, actionSets.get(0).getSecondInteractive());
                    break;
                case ATTACK_UPPER_SLASH:
                    actionSets.get(1).addInteractionToNextFree(new Interactive(this, new InteractiveActivatorFrames(frames), new LineInteractiveCollision(0, 128, 16, 66, 40), HURT, SWORD, (byte) attack, 2f));
                    break;
            }
        }


        for (InteractionSet set : actionSets) {
            for (Interactive interactive : set.getAllInteractives()) {
                if (!interactiveObjects.contains(interactive))
                    addInteractive(interactive);
            }
        }

    }

    public int getAttackType() {
        return ((MyController) playerController).getAttackType();
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

    public void changeWeapon() {
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
        updateActionSets();
    }

    public void hideWeapon() {
        if (activeWeapon != universal)
            lastWeapon = activeWeapon;
        activeWeapon = universal;
        updateActionSets();
    }

    private void updateActionSets() {
        if (activeWeapon.getType() != actionSets.get(activeActionSet).getWeaponType()) {
            for (int i = 0; i < actionSets.size(); i++) {
                if (actionSets.get(i).getWeaponType() == activeWeapon.getType()) {
                    activeActionSet = i;
                    break;
                } else if (actionSets.get(activeActionSet).getWeaponType() != UNIVERSAL && actionSets.get(i).getWeaponType() == UNIVERSAL) {
                    activeActionSet = i;
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
    }

    @Override
    public void initialize(int width, int height, Place place) {
        this.place = place;
        this.online = place.game.online;
        emitter = true;
        emits = false;
        textControl = new TextController(place);
        addGui(textControl);
        gui = new MyGUI("Player " + name + "'s GUI", place);
        addGui(gui);
        ((MyController) playerController).setPlayersGUI(gui);

        //test = place.getSpriteSheet("kulka");         //NIE KASOWAĆ! <('o'<)
        //testBody = place.getSpriteSheet("kulka1");
        Point[] dims = null;
        Point centralPoint = null;
        try {
            RandomGenerator r = RandomGenerator.create();
            head = new Cloth("glowa", characterName, place);
            hair = new Cloth("wlosy", characterName, place);
            torso = new Cloth("tors", characterName, place);
            legs = new Cloth("noga", characterName, place);
            weapon = new Cloth("miecz", characterName, place);
            dims = Cloth.getMergedDimensions(
                    head, torso, legs, hair,
                    hat, shirt, gloves, pants, boots, weapon);
            int tempx = dims[0].getX(), tempy = dims[0].getY();
            dims[0].set(Methods.roundUpToBinaryNumber(dims[0].getX()),
                    Methods.roundUpToBinaryNumber(dims[0].getY()));
            centralPoint = place.getStartPointFromFile("atrapa", "cloth/" + characterName);
            tempx = dims[0].getX() - tempx;
            tempy = dims[0].getY() - tempy;
            dims[1].set(centralPoint.getX() - (dims[1].getX() - tempx / 2),
                    centralPoint.getY() - (dims[1].getY() - tempy / 2));
        } catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage());
        }
        appearance = Animation.createFBOAnimation(place.getSpriteSheet("test", "cloth/" + characterName), 200, framesPerDir, dims[0], dims[1], centralPoint);
        visible = true;
        depth = 0;
        setResistance(2);
        if (lights.isEmpty()) {
            addLight(Light.create(place.getSpriteInSize("light", "", 768, 768), new Color(0.85f, 0.85f, 0.85f), 768, 768, this));
        }
        setCollision(Rectangle.create(width, (int) (width * Methods.ONE_BY_SQRT_ROOT_OF_2), OpticProperties.NO_SHADOW, this));
    }

    @Override
    protected boolean isCollided(double xMagnitude, double yMagnitude) {
        return isInGame() && collision.isCollideSolid((int) (getXInDouble() + xMagnitude), (int) (getYInDouble() + yMagnitude), map);
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(xEffect, yEffect, 0);

            glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);

            glTranslatef(getX(), getY(), 0);
            Drawer.setColor(JUMP_SHADOW_COLOR);
            Drawer.drawEllipse(0, 0, Methods.roundDouble((float) collision.getWidth() / 2), Methods.roundDouble((float) collision.getHeight() / 2), 15);
            Drawer.refreshColor();
            glTranslatef(0, (int) -jumpHeight, 0);
            Drawer.setCentralPoint();
            appearance.render();
            //((Animation)appearance).renderWhole();
            //renderClothed(appearance.getCurrentFrameIndex());  //NIE KASOWAĆ ! <('o'<)
            appearance.updateFrame();
            Drawer.returnToCentralPoint();

            glScaled(1 / Place.getCurrentScale(), 1 / Place.getCurrentScale(), 1);

            Drawer.renderStringCentered(name, 0, -(int) ((appearance.getActualHeight() * Place.getCurrentScale()) / 1.2),
                    place.standardFont, map.getLightColor());
            glPopMatrix();
        }
    }

    @Override
    public void renderClothed(int frame) {
        boolean rightUp = frame < 4 * framesPerDir;
        boolean frontUp = (frame < 3 * framesPerDir) || (frame >= 6 * framesPerDir);
//        glTranslatef(sprite.getXStart(), sprite.getYStart(), 0);  // Translatuję przy aktualizacji, odkomentuj, jakbyś testował <(,o,<)
        if (legs != null) {
            if (rightUp) {
                legs.getFirstPart().renderPieceAndReturn(frame);
                legs.getLastPart().renderPieceAndReturn(frame);
            } else {
                legs.getLastPart().renderPieceAndReturn(frame);
                legs.getFirstPart().renderPieceAndReturn(frame);
            }
        }
        if (pants != null) {
            if (frontUp) {
                pants.getLastPart().renderPieceAndReturn(frame);
                pants.getFirstPart().renderPieceAndReturn(frame);
            } else {
                pants.getFirstPart().renderPieceAndReturn(frame);
                pants.getLastPart().renderPieceAndReturn(frame);
            }
        }
        if (torso != null) {
            if (rightUp) {
                torso.getSecondPart().renderPieceAndReturn(frame);
                torso.getFirstPart().renderPieceAndReturn(frame);
                torso.getLastPart().renderPieceAndReturn(frame);
            } else {
                torso.getLastPart().renderPieceAndReturn(frame);
                torso.getFirstPart().renderPieceAndReturn(frame);
                torso.getSecondPart().renderPieceAndReturn(frame);
            }
        }
        if (head != null) {
            head.getFirstPart().renderPieceAndReturn(frame);
        }
        if (hair != null) {
            hair.getFirstPart().renderPieceAndReturn(frame);
        }
        if (weapon != null) {
            weapon.getFirstPart().renderPieceAndReturn(frame);
        }
    }

    @Override
    public void update() {
        if (map == place.loadingMap) {
            warp.warp(this);
        }
        
        if (jumping) {
            hop = false;
            jumpHeight = FastMath.abs(Methods.xRadius(jumpDelta * 4, 270));
            jumpDelta += Time.getDelta();
            if ((int) jumpDelta >= 68) {
                jumping = false;
                jumpDelta = 22.6f;
            }
        }
        updateChangers();
        moveWithSliding(xEnvironmentalSpeed + xSpeed, yEnvironmentalSpeed + ySpeed);
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
        updateWithGravity();
    }

    @Override
    public synchronized void sendUpdate() {
        if (jumping) {
            jumpHeight = FastMath.abs(Methods.xRadius(jumpDelta * 4, 70));
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
            String error = "ERROR: - " + exception.getMessage() + " in " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this.getClass();
            ErrorHandler.logAndPrint(error);
        }
    }

    @Override
    public synchronized void updateOnline() {
        try {
            if (jumping) {
                hop = false;
                jumpHeight = FastMath.abs(Methods.xRadius(jumpDelta * 4, 70));
                jumpDelta += Time.getDelta();
                if ((int) jumpDelta == 68) {
                    jumping = false;
                    jumpDelta = 22.6f;
                }
            }
        } catch (Exception exception) {
            String error = "ERROR: - " + exception.getMessage() + " in " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this.getClass();
            ErrorHandler.logAndPrint(error);
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, Figure figure) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) jumpHeight, 0);
            Drawer.drawShapeInShade(appearance, 1);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) jumpHeight, 0);
            Drawer.drawShapeInBlack(appearance);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, int xStart, int xEnd) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) jumpHeight, 0);
            Drawer.drawShapePartInShade(appearance, 1, xStart, xEnd);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, int xStart, int xEnd) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) jumpHeight, 0);
            Drawer.drawShapePartInBlack(appearance, xStart, xEnd);
            glPopMatrix();
        }
    }

    public TextController getTextController() {
        return textControl;
    }
}
