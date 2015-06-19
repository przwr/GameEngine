/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import collision.Figure;
import collision.OpticProperties;
import collision.Rectangle;
import game.gameobject.Player;
import game.place.Place;
import engine.Light;
import sprites.Animation;
import engine.Drawer;
import engine.Methods;
import engine.RandomGenerator;
import engine.Time;
import game.Settings;
import game.gameobject.inputs.InputKeyBoard;
import game.place.Map;
import game.place.WarpPoint;
import game.text.TextController;
import gamecontent.equipment.Cloth;
import java.io.FileNotFoundException;
import net.jodk.lang.FastMath;
import net.packets.MPlayerUpdate;
import net.packets.Update;
import org.lwjgl.input.Keyboard;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;
import sprites.SpriteSheet;

/**
 *
 * @author przemek
 */
public class MyPlayer extends Player {

    private Cloth torso;
    private Cloth legs;
    private Cloth dress;

    private final int framesPerDir = 19;

    private int xTempSpeed, yTempSpeed;

    private TextController textControl;

    //---------<('.'<) TYMCZASOWE!-------------//
    private float jumpDelta = 22.6f;
    //private SpriteSheet test, testBody;    //NIE KASOWAĆ! <('o'<)
    float testIndex = 0;
    //---------------------------------------//

    public MyPlayer(boolean first, String name) {
        super(name);
        this.first = first;
        if (first) {
            initializeControllerForFirst();
        } else {
            initializeController();
        }
    }

    private void initializeControllerForFirst() {
        controler = new MyController(this);
        controler.inputs[0] = new InputKeyBoard(Keyboard.KEY_UP);
        controler.inputs[1] = new InputKeyBoard(Keyboard.KEY_DOWN);
        controler.inputs[2] = new InputKeyBoard(Keyboard.KEY_RETURN);
        controler.inputs[3] = new InputKeyBoard(Keyboard.KEY_ESCAPE);
        controler.initialize();
    }

    private void initializeController() {
        controler = new MyController(this);
        controler.initialize();
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
        sprite = place.getSpriteSheet("test");

        textControl = new TextController(place);
        addGui(textControl);

        //test = place.getSpriteSheet("wynik");         //NIE KASOWAĆ! <('o'<)
        //testBody = place.getSpriteSheet("wynik1");
        try {
            RandomGenerator r = RandomGenerator.create();
            torso = new Cloth((String) r.choose("sweater", "torso", "blueSweater"), place);
            legs = new Cloth((String) r.choose("boots", "legs"), place);
            dress = r.chance(30) ? new Cloth((String) r.choose("dress", "blueDress"), place) : null;
        } catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage());
        }
        animation = new Animation((SpriteSheet) sprite, 200);
        visible = true;
        depth = 0;
        setResistance(2);
        if (lights.isEmpty()) {
            addLight(Light.create(place.getSpriteInSize("light", 768, 768), new Color(0.85f, 0.85f, 0.85f), 768, 768, this));
        }
        setCollision(Rectangle.create(width, height / 4, OpticProperties.NO_SHADOW, this));
    }

    @Override
    protected boolean isColided(int xMagnitude, int yMagnitude) {
        if (isInGame()) {
            return collision.isCollideSolid(getX() + xMagnitude, getY() + yMagnitude, map);
        }
        return false;
    }

    public void renderClothed(int frame) {
        boolean rightUp = frame < 4 * framesPerDir;
        boolean frontUp = (frame < 3 * framesPerDir) || (frame >= 6 * framesPerDir);

        glTranslatef(sprite.getXStart(), sprite.getYStart(), 0);
        if (legs != null) {
            if (rightUp) {
                legs.getLeftPart().renderPieceHere(frame);
                legs.getRightPart().renderPieceHere(frame);
            } else {
                legs.getRightPart().renderPieceHere(frame);
                legs.getLeftPart().renderPieceHere(frame);
            }
        }
        if (dress != null) {
            if (frontUp) {
                dress.getRightPart().renderPieceHere(frame);
                dress.getLeftPart().renderPieceHere(frame);
            } else {
                dress.getLeftPart().renderPieceHere(frame);
                dress.getRightPart().renderPieceHere(frame);
            }
        }
        if (torso != null) {
            if (rightUp) {
                torso.getLeftPart().renderPieceHere(frame);
                torso.getCentralPart().renderPieceHere(frame);
                torso.getRightPart().renderPieceHere(frame);
            } else {
                torso.getRightPart().renderPieceHere(frame);
                torso.getCentralPart().renderPieceHere(frame);
                torso.getLeftPart().renderPieceHere(frame);
            }
        }
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef(xEffect, yEffect, 0);
            if (Settings.scaled) {
                glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
            }
            glTranslatef(getX(), getY(), 0);
            Drawer.setColor(JUMP_SHADOW_COLOR);
            Drawer.drawElipse(0, 0, Methods.roundDouble((float) collision.getWidth() / 2), Methods.roundDouble((float) collision.getHeight() / 2), 15);
            Drawer.refreshColor();
            glTranslatef(0, (int) -jumpHeight, 0);
            getAnimation().render();

            //renderClothed(animation.getCurrentFrameIndex());  //NIE KASOWAĆ ! <('o'<)
            //glTranslatef(50, 0, 0);
            //testBody.renderPiece((int) testIndex);
            //test.renderPiece((int) testIndex);
            testIndex += 0.1;
            if (testIndex >= 80) {
                testIndex = 0;
            }

            if (Settings.scaled) {
                glScaled(1 / Place.getCurrentScale(), 1 / Place.getCurrentScale(), 1);
            }
            Drawer.renderStringCentered(name, (int) ((collision.getWidth() * Place.getCurrentScale()) / 2),
                    (int) ((collision.getHeight() * Place.getCurrentScale()) / 2), place.standardFont, map.getLightColor());
            glPopMatrix();
        }
    }

    @Override
    public void update() {
        if (jumping) {
            hop = false;
            jumpHeight = FastMath.abs(Methods.xRadius(jumpDelta * 4, 270));
            jumpDelta += Time.getDelta();
            if ((int) jumpDelta >= 68) {
                jumping = false;
                jumpDelta = 22.6f;
            }
        }
        xTempSpeed = (int) (xEnvironmentalSpeed + super.xSpeed);
        yTempSpeed = (int) (yEnvironmentalSpeed + super.ySpeed);
        moveIfPossible(xTempSpeed, yTempSpeed);
        for (WarpPoint warp : map.getWarps(getX(), getY())) {
            if (warp.getCollision() != null && warp.getCollision().isCollideSingle(warp.getX(), warp.getY(), collision)) {
                warp.Warp(this);
                break;
            }
        }
        brakeOthers();
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
        xTempSpeed = (int) (xEnvironmentalSpeed + super.xSpeed);
        yTempSpeed = (int) (yEnvironmentalSpeed + super.ySpeed);
        moveIfPossible(xTempSpeed, yTempSpeed);
        for (WarpPoint warp : map.getWarps(getX(), getY())) {
            if (warp.getCollision() != null && warp.getCollision().isCollideSingle(warp.getX(), warp.getY(), collision)) {
                warp.Warp(this);
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
                changeMap(currentMap);
            }
            if (((MPlayerUpdate) update).isHop()) {
                setJumping(true);
            }
            setEmits(((MPlayerUpdate) update).isEmits());
        } catch (Exception exception) {
            String error = "ERROR: - " + exception.getMessage() + " in " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this.getClass();
            Methods.logAndPrint(error);
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
            Methods.logAndPrint(error);
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, Figure figure) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) jumpHeight, 0);
            Drawer.drawShapeInShade(animation, 1);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) jumpHeight, 0);
            Drawer.drawShapeInBlack(animation);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, Figure figure, int xStart, int xEnd) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) jumpHeight, 0);
            Drawer.drawShapePartInShade(animation, 1, xStart, xEnd);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure f, int xStart, int xEnd) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) jumpHeight, 0);
            Drawer.drawShapePartInBlack(animation, xStart, xEnd);
            glPopMatrix();
        }
    }

    public TextController getTextController() {
        return textControl;
    }
}
