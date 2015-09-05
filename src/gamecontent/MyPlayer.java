/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import collision.Figure;
import collision.OpticProperties;
import collision.Rectangle;
import collision.interactive.CurveInteractiveCollision;
import collision.interactive.InteractiveActivatorFrames;
import collision.interactive.LineInteractiveCollision;
import engine.*;
import game.gameobject.Interactive;
import game.gameobject.Player;
import game.gameobject.PlayerStats;
import game.gameobject.inputs.InputKeyBoard;
import game.place.Map;
import game.place.Place;
import game.place.WarpPoint;
import game.text.TextController;
import gamecontent.equipment.Cloth;
import net.jodk.lang.FastMath;
import net.packets.MPlayerUpdate;
import net.packets.Update;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;
import sprites.Animation;

import java.io.FileNotFoundException;

import static gamecontent.MyController.*;
import static org.lwjgl.opengl.GL11.*;


/**
 * @author przemek
 */
public class MyPlayer extends Player {

    private final int framesPerDir = 42;
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
        int[] attacks = ((MyController) playerController).getAttackFrames();
        for (int attack = 0; attack < attacks.length; attack++) {
            int[] frames = new int[8];
            for (int i = 0; i < frames.length; i++) {
                frames[i] = i * framesPerDir + attacks[attack];
            }
            switch (attack) {
                case ATTACK_SLASH:
                    addInteractive(new Interactive(this, new InteractiveActivatorFrames(frames), new CurveInteractiveCollision(42, 32, 0, 64, 120), Interactive.HURT, 2f));
                    break;
                case ATTACK_THRUST:
                    addInteractive(new Interactive(this, new InteractiveActivatorFrames(frames), new LineInteractiveCollision(52, 10, 6, 84, 24), Interactive.HURT, 2.5f));
                    break;
                case ATTACK_WEAK_PUNCH:
                    addInteractive(new Interactive(this, new InteractiveActivatorFrames(frames), new LineInteractiveCollision(72, 12, 2, 30, 20), Interactive.HURT, 1f));
                    break;
                case ATTACK_STRONG_PUNCH:
                    addInteractive(new Interactive(this, new InteractiveActivatorFrames(frames), new LineInteractiveCollision(72, 12, 2, 34, 20), Interactive.HURT, 1.5f));
                    break;
                case ATTACK_UPPER_SLASH:
                    addInteractive(new Interactive(this, new InteractiveActivatorFrames(frames), new LineInteractiveCollision(0, 128, 16, 66, 40), Interactive.HURT, 2f));
                    break;
            }
        }
    }

    public int getAttackType() {
        return ((MyController) playerController).getAttackType();
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
    protected boolean isCollided(int xMagnitude, int yMagnitude) {
        return isInGame() && collision.isCollideSolid(getX() + xMagnitude, getY() + yMagnitude, map);
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
            glTranslatef(0, (int) -aboveGroundHeight, 0);
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
            aboveGroundHeight = FastMath.abs(Methods.xRadius(jumpDelta * 4, 270));
            jumpDelta += Time.getDelta();
            if ((int) jumpDelta >= 68) {
                jumping = false;
                jumpDelta = 22.6f;
            }
        }
        moveWithSliding(xEnvironmentalSpeed + xSpeed, yEnvironmentalSpeed + ySpeed);
        if (area != -1) {
            for (WarpPoint warp : map.getArea(area).getNearWarps()) {
                if (warp.getCollision() != null && warp.getCollision().isCollideSingle(warp.getX(), warp.getY(), collision)) {
                    warp.warp(this);
                    break;
                }
            }
        }
        brakeOthers();
        appearance.updateTexture(this);
    }

    @Override
    public synchronized void sendUpdate() {
        if (jumping) {
            aboveGroundHeight = FastMath.abs(Methods.xRadius(jumpDelta * 4, 70));
            jumpDelta += Time.getDelta();
            if ((int) jumpDelta >= 68) {
                jumping = false;
                jumpDelta = 22.6f;
            }
        }
        moveWithSliding(xEnvironmentalSpeed + xSpeed, yEnvironmentalSpeed + ySpeed);
        for (WarpPoint warp : map.getArea(area).getNearWarps()) {
            if (warp.getCollision() != null && warp.getCollision().isCollideSingle(warp.getX(), warp.getY(), collision)) {
                warp.warp(this);
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
            Map currentMap = getPlace().getMapById(((MPlayerUpdate) update).getMapId());
            if (currentMap != null && this.map != currentMap) {
                changeMap(currentMap, getX(), getY());
            }
            if (((MPlayerUpdate) update).isHop()) {
                setJumping(true);
            }
            setEmits(((MPlayerUpdate) update).isEmits());
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
                aboveGroundHeight = FastMath.abs(Methods.xRadius(jumpDelta * 4, 70));
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
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) aboveGroundHeight, 0);
            Drawer.drawShapeInShade(appearance, 1);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) aboveGroundHeight, 0);
            Drawer.drawShapeInBlack(appearance);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, int xStart, int xEnd) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) aboveGroundHeight, 0);
            Drawer.drawShapePartInShade(appearance, 1, xStart, xEnd);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, int xStart, int xEnd) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) aboveGroundHeight, 0);
            Drawer.drawShapePartInBlack(appearance, xStart, xEnd);
            glPopMatrix();
        }
    }

    public TextController getTextController() {
        return textControl;
    }
}
