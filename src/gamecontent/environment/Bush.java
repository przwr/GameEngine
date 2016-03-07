package gamecontent.environment;

import collision.Figure;
import collision.OpticProperties;
import collision.Rectangle;
import engine.utilities.Drawer;
import engine.utilities.Methods;
import engine.utilities.Point;
import engine.utilities.RandomGenerator;
import game.Settings;
import game.gameobject.GameObject;
import game.place.Place;
import game.place.fbo.FrameBufferObject;
import game.place.fbo.MultiSampleFrameBufferObject;
import game.place.fbo.RegularFrameBufferObject;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import sprites.Sprite;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by przemek on 30.11.15.
 */
public class Bush extends GameObject {


    public static Map<String, FrameBufferObject> fbos = new HashMap<>();
    public static List<Bush> instances = new ArrayList();
    static Sprite bark;
    static Sprite leaf;
    private static RandomGenerator random = RandomGenerator.create();
    FrameBufferObject fbo;
    int width, height;
    float spread;
    private Color branchColor;
    private Color leafColor;
    private ArrayList<Point> points = new ArrayList<>();
    private Comparator<Point> comparator = (p1, p2) -> Math.abs(p2.getX()) * 100 - Math.abs(p1.getX()) * 100 + p1.getY() - p2.getY();

    public Bush(int x, int y) {
        this(x, y, 14, 80, 0.8f);
    }

    public Bush(int x, int y, int width, int height, float spread) {
        initialize("Bush", x, y);
        setCollision(Rectangle.create(width, Methods.roundDouble(width * Methods.ONE_BY_SQRT_ROOT_OF_2), OpticProperties.NO_SHADOW, this));
        setSimpleLighting(false);
        solid = true;
        canCover = true;
        this.width = width;
        this.height = height;
        this.spread = spread;
        int fboWidth = Math.round(spread * 3f * height);
        int fboHeight = Math.round(height * 2.5f);
        int ins = random.random(10);
        String bushCode = width + "-" + height + "-" + spread + "-" + ins;
        fbo = fbos.get(bushCode);
        if (fbo == null) {
            fbo = (Settings.samplesCount > 0) ? new MultiSampleFrameBufferObject(fboWidth, fboHeight) : new RegularFrameBufferObject(fboWidth, fboHeight);
            fbos.put(bushCode, fbo);
        }
        appearance = fbo;
        branchColor = new Color(0x8C6B1F);//new Color(0.4f, 0.3f, 0.15f);
        leafColor = new Color(0.1f, 0.4f, 0.15f);//new Color(0x388A4B);
        instances.add(this);
    }

    public static boolean allGenerated() {
        for (Bush bush : instances) {
            bush.preRender();
        }
        Iterator it = fbos.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (!((FrameBufferObject) pair.getValue()).generated) {
                return false;
            }
        }
        instances.clear();
        return true;
    }

    private void preRender() {
        if (!fbo.generated) {
            if (map != null) {
                bark = map.place.getSprite("bark", "", true);
                leaf = map.place.getSprite("leaf", "", true);
                fbo.activate();
                glPushMatrix();
                glClearColor(0.5f, 0.35f, 0.2f, 0);
                glClear(GL_COLOR_BUFFER_BIT);
                glTranslatef(fbo.getWidth() / 2, Display.getHeight() - 20, 0);
                drawBush();
                glPopMatrix();
                fbo.deactivate();
                points.clear();
                points = null;
            }
        }
    }

    @Override
    public void render(int xEffect, int yEffect) {
        preRender();
        glPushMatrix();
        glTranslatef(xEffect, yEffect, 0);
        glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
        glTranslatef(getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20 - fbo.getHeight() + collision.getHeightHalf(), 0);
        fbo.render();
        Drawer.refreshColor();
        glPopMatrix();
    }

    private void drawBush() {
        glEnable(GL_TEXTURE_2D);
        bark.bindCheck();
        Drawer.setColorStatic(new Color(branchColor.r + (random.next(10) / 10240f), branchColor.g + (random.next(10) / 10240f), branchColor.b + (random.next
                (10) / 10240f)));
        drawRoots();
        drawBranches();
        drawLeafs();
    }

    private void drawRoots() {
        int change1 = -2 + random.next(2) + Math.round(width * 0.15f + width * random.next(10) / 10240f);
        int change2 = -2 + random.next(2) + Math.round(width * 0.15f + width * random.next(10) / 10240f);
        int change3 = -2 + random.next(2) + Math.round(width * 0.15f + width * random.next(10) / 10240f);
        Drawer.drawTextureQuad(width / 2, 0, width, -12, width + change1, -6, width + change1, 2);
        Drawer.drawTextureTriangle(width + change1, -6, width + change1, 2, (width - change1) * 2, change2 < 6 ? change2 : 6);
        Drawer.drawTextureQuad(width / 2, 0, 0, -12, -change2, -6, -change2, 2);
        Drawer.drawTextureTriangle(-change2, -6, -change2, 2, -width + change1 * 2, change1 < 6 ? change1 : 6);
        boolean left = random.nextBoolean();
        if (left) {
            Drawer.drawTextureQuad(2 * width / 3, 0, width / 4, -5, width / 3 - change3 / 2, 4, width / 3 + change3 / 2, 6);
            Drawer.drawTextureTriangle(width / 3 - change3 / 2, 4, width / 3 + change3 / 2, 6, -3, 12);
        } else {
            Drawer.drawTextureQuad(width / 3, 0, 3 * width / 4, -5, 2 * width / 3 + change3 / 2, 4, 2 * width / 3 - change3 / 2, 6);
            Drawer.drawTextureTriangle(2 * width / 3 + change3 / 2, 4, 2 * width / 3 - change3 / 2, 6, width + 3, 12);
        }
        points.add(new Point(0, -height / 2));
    }

    private void drawBranches() {
        int thick = 2 * width / 3;
        float spreadModifier = 0.6f;
        drawBranch(width / 2 - thick / 2, height, 0, thick, thick / 2, 0);
        boolean left = random.nextBoolean();
        if (left) {
            drawBranch(0, height, -spread * spreadModifier, thick, thick / 2, 0);
            drawBranch(width - thick, height, spread * spreadModifier, thick, thick / 2, 0);
        } else {
            drawBranch(width - thick, height, spread * spreadModifier, thick, thick / 2, 0);
            drawBranch(0, height, -spread * spreadModifier, thick, thick / 2, 0);
        }
    }

    private void drawBranch(int x, int height, float spread, int widthBase, int widthTop, int yShift) {
        int length = height + random.randomInRange(0, height / 10);
        int deviation = Math.round(spread * (height / 2 + random.randomInRange(0, height / 4)));
        int change = -8 + random.next(4);
        int xPosition = x + deviation / 2 + change;
        Drawer.drawTextureQuad(x, 0, xPosition, -length / 2, xPosition + (widthTop + widthBase) / 2, -length / 2, x + widthBase, 0);
        Drawer.drawTextureQuad(xPosition, -length / 2, x + deviation, -length, x + deviation + widthTop, -length, xPosition + (widthTop + widthBase) / 2,
                -length / 2);
        // End of branch
        int change2 = -16 + random.next(5);
        Drawer.drawTextureTriangle(x + deviation, -length, x + deviation + widthTop, -length, x + deviation + 2 * deviation / 3 + change2, -length - 2 *
                length / 3);
        points.add(new Point(x + deviation + 2 * deviation / 3 + change2, -length - 2 * length / 3 + yShift));
        if (Math.abs(deviation) > 20) {
            // Small Branch
            xPosition = deviation + change / 2;
            int xA = x + Math.round(xPosition * 0.75f) + widthTop / 2;
            int yA = -Math.round(length * 0.75f);
            int xB = x + xPosition + widthTop / 2;
            int yB = -length;
            while (Methods.pointDistanceSimple2(xA, yA, xB, yB) > widthTop * widthTop * 4) {
                xA += Math.round(xPosition * 0.1f);
                yA -= Math.round(length * 0.1f);
            }
            int change1 = Math.round(length * (random.next(10) / 4096f));
            Drawer.drawTextureQuad(xA, yA, xB, yB,
                    x + Math.round(1.3f * xPosition), Math.round(-1f * length) + Math.round(-0.5f * widthTop),
                    x + Math.round(1.3f * xPosition), Math.round(-1f * length));
            points.add(new Point(x + Math.round(1.3f * xPosition), Math.round(-1f * length) + yShift));
            Drawer.drawTextureQuad(x + Math.round(1.3f * xPosition), Math.round(-1f * length) + Math.round(-0.5f * widthTop),
                    x + Math.round(1.3f * xPosition), Math.round(-1f * length),
                    x + Math.round(1.5f * xPosition), Math.round(-0.95f * length) - change1,
                    x + Math.round(1.5f * xPosition), Math.round(-0.95f * length) + Math.round(-0.4f * widthTop) - change1 + Math.round(-0.4f * widthTop));
            points.add(new Point(x + Math.round(1.5f * xPosition), Math.round(-0.95f * length) - change1 + yShift));
            Drawer.drawTextureTriangle(x + Math.round(1.5f * xPosition), Math.round(-0.95f * length) - change1,
                    x + Math.round(1.5f * xPosition), Math.round(-0.95f * length) - change1 + Math.round(-0.4f * widthTop),
                    x + Math.round(1.6f * xPosition), Math.round(-1.2f * length));
            points.add(new Point(x + Math.round(1.6f * xPosition), Math.round(-1.2f * length) + yShift));
            // Small Branch
            xPosition = deviation + change;
            xA = x + Math.round(xPosition * 0.35f) + widthTop / 2;
            yA = -Math.round(length * 0.35f);
            xB = x + Math.round(xPosition * 0.6f) + widthTop / 2;
            yB = -Math.round(length * 0.6f);
            while (Methods.pointDistanceSimple2(xA, yA, xB, yB) > widthTop * widthTop * 6) {
                xA += Math.round(xPosition * 0.1f);
                yA -= Math.round(length * 0.1f);
            }
            float rand = random.next(10) / 3072f;
            Drawer.drawTextureQuad(xA, yA, xB, yB,
                    x + Math.round(0.9f * xPosition), Math.round(-0.6f * length) + Math.round(-0.5f * widthTop),
                    x + Math.round(0.9f * xPosition), Math.round(-0.6f * length));
            points.add(new Point(x + Math.round(0.9f * xPosition), Math.round(-0.6f * length) + yShift));
            Drawer.drawTextureTriangle(x + Math.round(0.9f * xPosition), Math.round(-0.6f * length) + Math.round(-0.5f * widthTop),
                    x + Math.round(0.9f * xPosition), Math.round(-0.6f * length),
                    x + Math.round(1.8f * xPosition), Math.round(-(0.75f + rand) * length));
            points.add(new Point(x + Math.round(1.8f * xPosition), Math.round(-(0.75f + rand) * length) + yShift));
//            points.add(x + Math.round(1.8f * xPosition), -length - 2 * length / 3 + yShift);

            // Small Branch
            rand = random.next(10) / 3072f;
            Drawer.drawTextureQuad(xA, yA, xA + Math.round(0.125f * xPosition), yA - Math.round(length * 0.1f),
                    xA + Math.round(0.35f * xPosition), Math.round(-1.0f * length),
                    xA + Math.round(0.3f * xPosition), Math.round(-1.0f * length) + Math.round(-0.4f * widthTop));
            points.add(new Point(xA + Math.round(0.3f * xPosition), Math.round(-1.0f * length) + Math.round(-0.4f * widthTop) + yShift));
            Drawer.drawTextureTriangle(xA + Math.round(0.35f * xPosition), Math.round(-1.0f * length),
                    xA + Math.round(0.3f * xPosition), Math.round(-1.0f * length) + Math.round(-0.4f * widthTop),
                    xA + Math.round(0.45f * xPosition), Math.round(-(1.2f + rand) * length));
            points.add(new Point(xA + Math.round(0.45f * xPosition), Math.round(-(1.2f + rand) * length) + yShift));
//            points.add(xA + Math.round(0.45f * xPosition), -length - 2 * length / 3 + yShift);

        } else {
            // Small Branch
            xPosition = deviation + change;
            int xA = x + Math.round(xPosition * 0.35f) + widthTop / 2;
            int yA = -Math.round(length * 0.35f);
            int xB = x + Math.round(xPosition * 0.6f) + widthTop / 2;
            int yB = -Math.round(length * 0.6f);
            while (Methods.pointDistanceSimple2(xA, yA, xB, yB) > widthTop * widthTop * 6) {
                xA += Math.round(xPosition * 0.1f);
                yA -= Math.round(length * 0.1f);
            }
            float rand = random.next(10) / 3072f;
            Drawer.drawTextureQuad(xA, yA, xB, yB,
                    xA + Math.round(Math.signum(xPosition) * length * 0.2f), Math.round(-0.9f * length) + Math.round(-0.5f * widthTop),
                    xA + Math.round(Math.signum(xPosition) * length * 0.2f) + Math.round(-0.4f * widthTop), Math.round(-0.9f * length));
            points.add(new Point(xA + Math.round(Math.signum(xPosition) * length * 0.2f) + Math.round(-0.4f * widthTop), Math.round(-0.9f * length) + yShift));
            Drawer.drawTextureTriangle(xA + Math.round(Math.signum(xPosition) * length * 0.2f), Math.round(-0.9f * length) + Math.round(-0.5f * widthTop),
                    xA + Math.round(Math.signum(xPosition) * length * 0.2f) + Math.round(-0.4f * widthTop), Math.round(-0.9f * length),
                    xA + Math.round(Math.signum(xPosition) * length * 0.3f) + Math.round(-0.4f * widthTop), Math.round(-(1.1f + rand) * length));
            points.add(new Point(xA + Math.round(Math.signum(xPosition) * length * 0.3f) + Math.round(-0.4f * widthTop), Math.round(-(1.1f + rand) * length)
                    + yShift));

            // Small Branch
            rand = random.next(10) / 3072f;
            Drawer.drawTextureQuad(xA, yA, xB, yB,
                    xA - Math.round(Math.signum(xPosition) * length * 0.2f), Math.round(-0.9f * length) + Math.round(-0.5f * widthTop),
                    xA - Math.round(Math.signum(xPosition) * length * 0.2f) - Math.round(-0.4f * widthTop), Math.round(-0.9f * length));
            points.add(new Point(xA - Math.round(Math.signum(xPosition) * length * 0.2f), Math.round(-0.9f * length) + Math.round(-0.5f * widthTop) + yShift));
            Drawer.drawTextureTriangle(xA - Math.round(Math.signum(xPosition) * length * 0.2f), Math.round(-0.9f * length) + Math.round(-0.5f * widthTop),
                    xA - Math.round(Math.signum(xPosition) * length * 0.2f) - Math.round(-0.4f * widthTop), Math.round(-0.9f * length),
                    xA - Math.round(Math.signum(xPosition) * length * 0.3f) - Math.round(-0.4f * widthTop), Math.round(-(1.1f + rand) * length));
            points.add(new Point(xA - Math.round(Math.signum(xPosition) * length * 0.3f) - Math.round(-0.4f * widthTop), Math.round(-(1.1f + rand) * length)
                    + yShift));
        }
    }

    private void drawLeafs() {
        points.sort(comparator);
        int rand1, rand2;
        Drawer.setCentralPoint();
        int radius = Methods.roundDouble(leaf.getActualWidth() / 2 * Methods.SQRT_ROOT_OF_2);
        int dif = (radius + radius) / 3;
        int dif2 = dif * dif * 9;
        int count = height * 2;
        int maxX = 0;
        int maxY = 0;
        int minY = Integer.MAX_VALUE;
        for (int i = 0; i < points.size(); i++) {
            if (Math.abs(points.get(i).getY()) > maxY) {
                maxY = Math.abs(points.get(i).getY());
            }
            if (points.get(i).getY() < minY) {
                minY = points.get(i).getY();
            }
            if (Math.abs(points.get(i).getX()) > maxX) {
                maxX = Math.abs(points.get(i).getX());
            }
        }
        for (int i = 0; i < points.size(); i++) {
            rand1 = rand2 = 0;
            for (int j = 0; j < count; j++) {
                randomLeaf(i, rand1, rand2, maxX, maxY, minY);
                rand1 += random.randomInRange(-dif, dif);
                rand2 += random.randomInRange(-dif / 2, dif / 2);
                if (rand1 * rand1 + rand2 * rand2 > dif2) {
                    rand1 = random.randomInRange(-dif, dif);
                    rand2 = random.randomInRange(-dif / 2, dif / 2);
                }
            }
        }
    }

    private void randomLeaf(int i, int x, int y, float maxX, float maxY, float minY) {
        if (Math.abs(points.get(i).getY() + y) < fbo.getHeight() - leaf.getWidth() - leaf.getHeight()
                && Math.abs(points.get(i).getX() + x) < fbo.getWidth() / 2 - leaf.getWidth() - leaf.getHeight()) {
            float change = Math.abs(points.get(i).getY() + minY + y) / (maxY - minY);
            change -= Math.abs(points.get(i).getX() + x) / maxX / 4;
            if (change < 0) {
                change = 0;
            }
            int rand = random.randomInRange(-10, 10);
            Drawer.setColorStatic(new Color(leafColor.r * (1 + change / 2f + rand / 20f), leafColor.g * (1 + change / 2f + rand / 75f),
                    leafColor.b * (1 + change / 2f + rand / 25f)));
            float angle = 90f * (points.get(i).getX() + x + random.randomInRange(-10, 10)) / maxX;
            Drawer.translate(points.get(i).getX() + x, points.get(i).getY() + y);
            glPushMatrix();
            leaf.renderRotate(angle);
            glPopMatrix();
            Drawer.translate(-points.get(i).getX() - x, -points.get(i).getY() - y);
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, Figure figure) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(xEffect, yEffect, 0);
            glTranslatef(getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20 - fbo.getHeight() + collision.getHeightHalf(), 0);
            Drawer.drawShapeInShade(appearance, 1);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(xEffect, yEffect, 0);
            glTranslatef(getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20 - fbo.getHeight() + collision.getHeightHalf(), 0);
            Drawer.drawShapeInBlack(appearance);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, int xStart, int xEnd) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(xEffect, yEffect, 0);
            glTranslatef(getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20 - fbo.getHeight() + collision.getHeightHalf(), 0);
            Drawer.drawShapePartInShade(appearance, 1, xStart, xEnd);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, int xStart, int xEnd) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(xEffect, yEffect, 0);
            glTranslatef(getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20 - fbo.getHeight() + collision.getHeightHalf(), 0);
            Drawer.drawShapePartInBlack(appearance, xStart, xEnd);
            glPopMatrix();
        }
    }

    @Override
    public int getXSpriteBegin(boolean... forCover) {
        if (forCover.length > 0 && forCover[0]) {
            return getX() - Math.round(fbo.getActualWidth() * 0.4f);
        }
        return getX() - fbo.getActualWidth() / 2;
    }

    @Override
    public int getYSpriteBegin(boolean... forCover) {
        if (forCover.length > 0 && forCover[0]) {
            return getY() + 20 - collision.getHeight() - Math.round(fbo.getActualHeight() * 0.9f);
        }
        return getY() + 20 - collision.getHeight() - fbo.getActualHeight();
    }

    @Override
    public int getXSpriteEnd(boolean... forCover) {
        if (forCover.length > 0 && forCover[0]) {
            return getX() + Math.round(fbo.getActualWidth() * 0.4f);
        }
        return getX() + fbo.getActualWidth() / 2;
    }

    @Override
    public int getYSpriteEnd(boolean... forCover) {
        return getY() + 20 + collision.getHeight();
    }
}
