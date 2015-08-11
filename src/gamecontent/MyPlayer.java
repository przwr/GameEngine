/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import collision.*;
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
import sprites.SpriteSheet;

import java.io.FileNotFoundException;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public class MyPlayer extends Player {

    private final int framesPerDir = 26;
    private Cloth torso;
    private Cloth legs;
    private Cloth dress;
    private TextController textControl;

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
        int[] frames = {21, 22, 23, 24, 25, 47, 48, 49, 50, 51, 73, 74, 75, 76, 77, 99, 100, 101, 102, 103, 125, 126, 127, 128, 129, 151, 152, 153, 154, 155, 177, 178, 179, 180, 181, 203, 204, 205, 206, 207};
        addInteractive(new Interactive(this, new InteractiveActivatorFrames(frames), new CircleInteractiveCollision(32), Interactive.HURT));
        stats = new PlayerStats(this);
    }

    private void initializeControllerForFirst() {
        playerController = new MyController(this);
        playerController.inputs[0] = new InputKeyBoard(Keyboard.KEY_UP);
        playerController.inputs[1] = new InputKeyBoard(Keyboard.KEY_DOWN);
        playerController.inputs[2] = new InputKeyBoard(Keyboard.KEY_RETURN);
        playerController.inputs[3] = new InputKeyBoard(Keyboard.KEY_ESCAPE);
        playerController.initialize();
    }

    private void initializeController() {
        playerController = new MyController(this);
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

        //test = place.getSpriteSheet("kulka");         //NIE KASOWAĆ! <('o'<)
        //testBody = place.getSpriteSheet("kulka1");
        try {
            RandomGenerator r = RandomGenerator.create();
            torso = new Cloth(r.choose("sweater", "torso", "blueSweater"), place);
            legs = new Cloth(r.choose("boots", "legs"), place);
            dress = r.chance(30) ? new Cloth(r.choose("dress", "blueDress"), place) : null;
            Point[] p = SpriteSheet.getMergedDimensions(new SpriteSheet[]{legs.getLeftPart(), legs.getRightPart(),
                    dress != null ? dress.getLeftPart() : null,
                    dress != null ? dress.getRightPart() : null,
                    torso.getLeftPart(), torso.getCentralPart(), torso.getRightPart()});
            System.out.println("WIADOMOŚĆ DLA PRZEMKA!!"
                    + "\nWymiary połączonej ubranej babki : " + p[0]
                    + "\nPunkt centralny obrazka : " + p[1]
                    + "\nUWAGA! wymiary nie są 2-ójkowe");
        } catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage());
        }
        appearance = new Animation(place.getSpriteSheet("cloth/test"), 200, framesPerDir);
        visible = true;
        depth = 0;
        setResistance(2);
        if (lights.isEmpty()) {
            addLight(Light.create(place.getSpriteInSize("light", 768, 768), new Color(0.85f, 0.85f, 0.85f), 768, 768, this));
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
            appearance.updateTexture(this);
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
//            renderClothed(animation.getCurrentFrameIndex());  //NIE KASOWAĆ ! <('o'<)
            appearance.updateFrame();
            Drawer.returnToCentralPoint();

            //glTranslatef(50, 0, 0);
            //Drawer.setCentralPoint();
            //testBody.renderPiece((int) testIndex);
            //Drawer.returnToCentralPoint();
            //test.renderPiece((int) testIndex);
            //testIndex += 0.1;
            //if (testIndex >= 80) {
            //    testIndex = 0;
            //}
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
        moveWithSliding(xEnvironmentalSpeed + xSpeed, yEnvironmentalSpeed + ySpeed);
        if (area != -1) {
            for (WarpPoint warp : map.getArea(area).getNearWarps()) {
                if (warp.getCollision() != null && warp.getCollision().isCollideSingle(warp.getX(), warp.getY(), collision)) {
                    warp.Warp(this);
                    break;
                }
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
        moveWithSliding(xEnvironmentalSpeed + xSpeed, yEnvironmentalSpeed + ySpeed);
        for (WarpPoint warp : map.getArea(area).getNearWarps()) {
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
