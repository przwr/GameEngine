package gamecontent;

import collision.Figure;
import collision.OpticProperties;
import collision.Rectangle;
import engine.utilities.Drawer;
import engine.utilities.Methods;
import engine.utilities.RandomGenerator;
import game.Settings;
import game.gameobject.GameObject;
import game.place.Place;
import game.place.fbo.FrameBufferObject;
import game.place.fbo.MultiSampleFrameBufferObject;
import game.place.fbo.RegularFrameBufferObject;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by przemek on 30.11.15.
 */
public class Tree extends GameObject {


    private static RandomGenerator random = RandomGenerator.create();
    int width, height;
    int change, xPosition, height1, height2, change1, change2, change3, change4, change5, xA, yA, xB, yB;
    FrameBufferObject fbo;
    boolean prerendered;


    public Tree(int x, int y, int width, int height) {
        initialize("Tree", x, y);
        setCollision(Rectangle.create(width, Methods.roundDouble(width * Methods.ONE_BY_SQRT_ROOT_OF_2), OpticProperties.FULL_SHADOW, this));
        setSimpleLighting(false);
        collision.setSmall(true);
        solid = true;
        this.width = width;
        this.height = height;
        int fboWidth = height * 2;
        int fboHeight = Math.round(height * 2.2f);
        fbo = (Settings.samplesCount > 0) ? new MultiSampleFrameBufferObject(fboWidth, fboHeight) :
                new RegularFrameBufferObject(fboWidth, fboHeight);
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (!prerendered) {
            fbo.activate();
            glPushMatrix();
            glClearColor(0.5f * Place.getDayCycle().getShade().r, 0.35f * Place.getDayCycle().getShade().g, 0.2f * Place.getDayCycle().getShade().b, 0);
            glClear(GL_COLOR_BUFFER_BIT);
            glTranslatef(fbo.getWidth() / 2, Display.getHeight() - 20, 0);
            drawTrunkAndBranches();
            glPopMatrix();
            fbo.deactivate();
            prerendered = true;
        }
        glPushMatrix();
        glTranslatef(xEffect, yEffect, 0);
        glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
        glTranslatef(getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20 - fbo.getHeight() + collision.getHeightHalf(), 0);
        fbo.render();
        Drawer.refreshColor();
        glPopMatrix();
    }


    private void drawTrunkAndBranches() {
        Drawer.setColor(new Color(0.5f * Place.getDayCycle().getShade().r, 0.35f * Place.getDayCycle().getShade().g, 0.2f * Place.getDayCycle().getShade().b));
        change1 = -4 + random.next(3) + Math.round(height * 0.05f + height * random.next(10) / 10240f);
        change2 = -4 + random.next(3) - Math.round(height * 0.05f + height * random.next(10) / 10240f);
        change3 = -2 + random.next(2) + Math.round(height * 0.025f + height * random.next(10) / 20480f);
        Drawer.drawTriangle(0, 0, width, -10, width + change1, 4);
        Drawer.drawTriangle(width, 0, 0, -10, change2, 6);
        Drawer.drawTriangle(0, 0, width - width / 4, 0, change3 + width / 2, 12);
        height1 = Math.round(0.3f * height + (random.next(8) / 1024f) * 0.7f * height);
        height2 = Math.round(0.6f * height + (random.next(8) / 1024f) * 0.7f * height);
        change1 = -4 + random.next(3);      // RIGHT
        change2 = -4 + random.next(3);      // LEFT
        change3 = -4 + random.next(3);      // RIGHT
        change4 = -4 + random.next(3);      // LEFT
        change5 = -4 + random.next(3);      // WHOLE
        Drawer.drawQuad(0, 0, width, 0, width + change1, -height1, change2, -height1);
        Drawer.drawQuad(change2, -height1, width + change1, -height1, width + change3, -height2, change4, -height2);
        Drawer.drawQuad(change4, -height2, width + change3, -height2, width + change5, -height, change5, -height);
        int thick = 16;
        int length = height / 2 + Math.round(height * random.next(8) / 1024f);
        int deviation = -8 + random.next(4);
        glTranslatef(change5, -height, 0);

//      Top Branches
        drawBranch(width / 2 - thick / 2, length, deviation, thick, thick / 2, 1);
        boolean leftHigher = random.nextBoolean();

        if (leftHigher) {
            length = height / 2 + Math.round(height * random.next(6) / 1024f);
            deviation = height / 3 + Math.round(height * random.next(4) / 1024f);
            drawBranch(0, length, -deviation, thick, thick / 2, 1);
            length = height / 2 + Math.round(height * random.next(8) / 1024f);
            deviation = height / 3 + Math.round(height * random.next(3) / 1024f);
            glTranslatef(0, Math.round(height * 0.1f), 0);
            drawBranch(width - thick, length, deviation, thick, thick / 2, 1);
        } else {
            length = height / 2 + Math.round(height * random.next(6) / 1024f);
            deviation = height / 3 + Math.round(height * random.next(4) / 1024f);
            drawBranch(width - thick, length, deviation, thick, thick / 2, 1);
            length = height / 2 + Math.round(height * random.next(6) / 1024f);
            deviation = height / 3 + Math.round(height * random.next(3) / 1024f);
            glTranslatef(0, Math.round(height * 0.1f), 0);
            drawBranch(0, length, -deviation, thick, thick / 2, 1);
        }
        glTranslatef(0, Math.round(height * 0.3f), 0);
        if (leftHigher) {
            length = height / 2 + Math.round(height * random.next(3) / 1024f);
            deviation = height / 3 + Math.round(height * random.next(3) / 1024f);
            drawBranch(-change5, length, -deviation, thick, thick / 2, 1);
        } else {
            length = height / 2 + Math.round(height * random.next(3) / 1024f);
            deviation = height / 3 + Math.round(height * random.next(3) / 1024f);
            drawBranch(change4 - change5, length, deviation, thick, thick / 2, 1);
        }

        glTranslatef(0, (height - height2) / 2, 0);
        leftHigher = random.nextBoolean();
        if (leftHigher) {
            length = height / 3 + Math.round(height * random.next(3) / 1024f);
            deviation = height / 3 + Math.round(height * random.next(3) / 1024f);
            drawBranch(-change5, length, -deviation, thick, thick / 2, 1);
            leftHigher = random.nextBoolean();
            if (leftHigher) {
                length = height / 2 + Math.round(height * random.next(3) / 1024f);
                deviation = height / 3 + Math.round(height * random.next(3) / 1024f);
                drawBranch(change4 - change5 + (width - thick) / 2, length, deviation, thick, thick / 2, 1);
            }
        } else {
            length = height / 3 + Math.round(height * random.next(3) / 1024f);
            deviation = height / 3 + Math.round(height * random.next(3) / 1024f);
            drawBranch(change4 - change5 + (width - thick) / 2, length, deviation, thick, thick / 2, 1);
            leftHigher = random.nextBoolean();
            if (leftHigher) {
                length = height / 2 + Math.round(height * random.next(3) / 1024f);
                deviation = height / 3 + Math.round(height * random.next(3) / 1024f);
                drawBranch(-change5, length, -deviation, thick, thick / 2, 1);
            }
        }
    }

    private void drawBranch(int x, int height, int deviation, int widthBase, int withTop, int smallCount) {
        change = -8 + random.next(4);
        xPosition = x + deviation / 2 + change;
        Drawer.drawQuad(x, 0, xPosition, -height / 2, xPosition + (withTop + widthBase) / 2, -height / 2, x + widthBase, 0);
        Drawer.drawQuad(xPosition, -height / 2, x + deviation, -height, x + deviation + withTop, -height, xPosition + (withTop + widthBase) / 2, -height / 2);

//      small branch
        xPosition = deviation + change / 2;
        xA = x + Math.round(xPosition * 0.75f) + (deviation < 0 ? withTop / 2 : withTop / 2);
        yA = -Math.round(height * 0.75f);
        xB = x + xPosition + (deviation < 0 ? withTop / 2 : withTop / 2);
        yB = -height;
        while (Methods.pointDistanceSimple2(xA, yA, xB, yB) > withTop * withTop * 4) {
            xA += Math.round(xPosition * 0.1f);
            yA -= Math.round(height * 0.1f);
        }
        Drawer.drawTriangle(xA, yA, xB, yB, x + 5 * xPosition / 3, Math.round(-1.2f * height));

//      small branch
        xPosition = deviation + change;
        xA = x + Math.round(xPosition * 0.35f) + (deviation < 0 ? withTop / 2 : withTop / 2);
        yA = -Math.round(height * 0.35f);
        xB = x + Math.round(xPosition * 0.6f) + (deviation < 0 ? withTop / 2 : withTop / 2);
        yB = -Math.round(height * 0.6f);
        while (Methods.pointDistanceSimple2(xA, yA, xB, yB) > withTop * withTop * 6) {
            xA += Math.round(xPosition * 0.1f);
            yA -= Math.round(height * 0.1f);
        }
        Drawer.drawTriangle(xA, yA, xB, yB, x + 3 * xPosition / 2, Math.round(-0.8f * height));

//      End of branch
        change = -16 + random.next(5);
        Drawer.drawTriangle(x + deviation, -height, x + deviation + withTop, -height, x + deviation + 2 * deviation / 3 + change, -height - 2 * height / 3);
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

    public int getXSpriteBegin() {
        return getX() - (collision.getWidth() + fbo.getWidth()) / 2;
    }

    public int getYSpriteBegin() {
        return getY() - (collision.getHeight() + fbo.getHeight()) / 2;
    }

    public int getXSpriteEnd() {
        return getX() + (collision.getWidth() + fbo.getWidth()) / 2;
    }

    public int getYSpriteEnd() {
        return getY() + (collision.getHeight() + fbo.getHeight()) / 2;
    }
}
