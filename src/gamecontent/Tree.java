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
        int fboHeight = height * 3;
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
        glTranslatef(getX() - fbo.getWidth() / 2 - collision.getWidth() / 2, getY() + 20 - fbo.getHeight() + collision.getHeight() / 2, 0);
        fbo.render();
        Drawer.refreshColor();
        glPopMatrix();
    }


    private void drawTrunkAndBranches() {
        Drawer.setColor(new Color(0.5f * Place.getDayCycle().getShade().r, 0.35f * Place.getDayCycle().getShade().g, 0.2f * Place.getDayCycle().getShade().b));
        change1 = -4 + random.next(3);
        change2 = -4 + random.next(3);
        change3 = -4 + random.next(3);
        Drawer.drawTriangle(0, 0, width, -10, width + 16 + change1, 4);
        Drawer.drawTriangle(width, 0, 0, -10, -20 + change2, 6);
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

        glTranslatef(change5, -height, 0);
        drawBranch(0, 80 + random.next(4), -68 + random.next(4), thick, thick / 2, 1);
        drawBranch(width / 2 - thick / 2, 80 + random.next(4), 0 + random.next(4), thick, thick / 2, 1);
        glTranslatef(0, 10, 0);
        drawBranch(width - thick, 72 + random.next(4), 36, thick, thick / 2, 1);
        glTranslatef(0, 30, 0);
        drawBranch(change4 - change5, 52 + random.next(4), -36, thick, thick / 2, 1);
        glTranslatef(0, (height - height2) / 2, 0);
        drawBranch(-change5, 80, 68 + random.next(4), thick, 10, 1);
        drawBranch(change4 - change5, 40 + random.next(4), -78 + random.next(3), 16, 10, 1);
//        glTranslatef(0, (height - height2) / 2, 0);
//        drawBranch(change4 - change5, 50 + random.next(4), -78 + random.next(3), 16, 10, 1);
    }

    private void drawBranch(int x, int height, int deviation, int widthBase, int withTop, int smallCount) {
        change = -8 + random.next(4);
        xPosition = x + deviation / 2 + change;
        Drawer.drawQuad(x, 0, xPosition, -height / 2, xPosition + (withTop + widthBase) / 2, -height / 2, x + widthBase, 0);
        Drawer.drawQuad(xPosition, -height / 2, x + deviation, -height, x + deviation + withTop, -height, xPosition + (withTop + widthBase) / 2, -height / 2);

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
        return getX();
    }

    public int getYSpriteBegin() {
        return getY();
    }

    public int getXSpriteEnd() {
        return getX() + width;
    }

    public int getYSpriteEnd() {
        return getY() + height;
    }
}
