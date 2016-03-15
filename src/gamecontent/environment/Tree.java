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
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import sprites.Sprite;
import sprites.fbo.FrameBufferObject;
import sprites.fbo.MultiSampleFrameBufferObject;
import sprites.fbo.RegularFrameBufferObject;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by przemek on 30.11.15.
 */
public class Tree extends GameObject {

    public static Map<String, FrameBufferObject> fbos = new HashMap<>();
    public static List<Tree> instances = new ArrayList();
    static Sprite bark;
    static Sprite leaf;
    private static RandomGenerator random = RandomGenerator.create();
    private final Comparator<Point> comparator = (p1, p2) -> Math.abs(p2.getX()) * 100 - Math.abs(p1.getX()) * 100 + p1.getY() - p2.getY();
    int width, height;
    float spread;
    boolean branchless;
    private FrameBufferObject fbo;
    private Color branchColor;
    private Color leafColor;
    private ArrayList<Point> points = new ArrayList<>();
    private int woodHeight, leafHeight;

    private Tree(int x, int y, int width, int height, float spread, boolean branchless) {
        initialize("Tree", x, y);
        if (!branchless) {
            setCollision(Rectangle.create(width, Methods.roundDouble(width * Methods.ONE_BY_SQRT_ROOT_OF_2), OpticProperties.FULL_SHADOW, this));
            setSimpleLighting(false);
        } else {
            setCollision(Rectangle.create(width, Methods.roundDouble(width * Methods.ONE_BY_SQRT_ROOT_OF_2), OpticProperties.NO_SHADOW, this));
        }
        canCover = true;
        solid = !branchless;
        this.branchless = branchless;
        this.width = width;
        this.height = height;
        this.spread = spread;
        int fboWidth = Math.round(spread * 2.5f * height);
        int fboHeight = Math.round(height * 3.8f);
        int ins = random.random(12);
        String treeCode = width + "-" + height + "-" + spread + "-" + branchless + "-" + ins;
        fbo = fbos.get(treeCode);
        if (fbo == null) {
            fbo = (Settings.samplesCount > 0) ? new MultiSampleFrameBufferObject(fboWidth, fboHeight) : new RegularFrameBufferObject(fboWidth, fboHeight);
            fbos.put(treeCode, fbo);
        }
        woodHeight = height * 2 + 20;
        leafHeight = Math.round(height * 1.6f);
        appearance = fbo;
        branchColor = new Color(0x8C6B1F);//new Color(0.4f, 0.3f, 0.15f);
        leafColor = new Color(0.1f, 0.4f, 0.15f);//new Color(0x388A4B);
        instances.add(this);
    }

    public static Tree create(int x, int y, int width, int height, float spread) {
        return new Tree(x, y, width, height, spread, false);
    }

    public static Tree create(int x, int y) {
        return new Tree(x, y, 32, 200, 0.8f, false);
    }

    public static Tree createBranchless(int x, int y, int width, int height, float spread) {
        return new Tree(x, y, width, height, spread, true);
    }

    public static boolean allGenerated() {
        for (Tree tree : instances) {
            tree.preRender();
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
                drawTree();
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
        Drawer.translate(getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20 - woodHeight + collision.getHeightHalf());
        fbo.renderBottom(woodHeight);
        Drawer.translate(0, -fbo.getHeight() + leafHeight);
        fbo.renderTop(woodHeight);
        Drawer.refreshColor();
        glPopMatrix();
    }

    private void drawTree() {
        glEnable(GL_TEXTURE_2D);
        bark.bindCheck();
        Drawer.setColorStatic(new Color(branchColor.r + (random.next(10) / 10240f), branchColor.g + (random.next(10) / 10240f), branchColor.b + (random.next(10)
                / 10240f)));
//        TODO liczyć gdzie są prawdziwe granice tekstury
//        Drawer.setCentralPoint();
//        Drawer.drawRectangle(-Math.round(fbo.getWidth() * 0.4f), -fbo.getHeight() + 20, Math.round(fbo.getWidth() * 0.8f), fbo.getHeight());
//        Drawer.returnToCentralPoint();
        if (!branchless) {
            drawRoots();
        }
        drawTrunkAndBranches();
        glTranslatef(0, -leafHeight, 0);
        drawLeafs();
    }

    private void drawRoots() {
        int change1 = -2 + random.next(4) + Math.round(width * 0.2f + width * random.next(10) / 10240f);
        int change2 = -2 + random.next(4) + Math.round(width * 0.2f + width * random.next(10) / 10240f);
        int change3 = -2 + random.next(2) + Math.round(width * 0.15f + width * random.next(10) / 10240f);
        Drawer.drawTextureQuad(width + change1, 2, width + change1, -6, width, -12, width / 2, 0);
        Drawer.drawTextureTriangle(width + change1, -6, width + change1, 2, width + change1 * 2, change2 < 6 ? change2 : 6);
        Drawer.drawTextureQuad(width / 2, 0, 0, -12, -change2, -6, -change2, 2);
        Drawer.drawTextureTriangle(-change1 - width / 2, change1 < 6 ? change1 : 6, -change2, 2, -change2, -6);
        boolean left = random.nextBoolean();
        if (left) {
            Drawer.drawTextureQuad(2 * width / 3, 0, width / 5, -5, width / 3 - change3 / 2, 4, width / 3 + change3 / 2, 6);
            Drawer.drawTextureTriangle(-3, 12, width / 3 + change3 / 2, 6, width / 3 - change3 / 2, 4);
        } else {
            Drawer.drawTextureQuad(2 * width / 3 - change3 / 2, 6, 2 * width / 3 + change3 / 2, 4, 4 * width / 5, -5, width / 3, 0);
            Drawer.drawTextureTriangle(2 * width / 3 + change3 / 2, 4, 2 * width / 3 - change3 / 2, 6, width + 3, 12);
        }
    }

    private void drawTrunkAndBranches() {
        // Trunk
        int levelsCount = 1 + (2 * height / 3) / 50;
        int[] levels = new int[levelsCount];
        int[] changes = new int[levelsCount * 2];
        float fraction = 1 / (float) levelsCount;
        int lastChange;
        int randHigh = Math.round(fraction * height / 5);
        int randLow = -randHigh;
        int randWidthHigh = Math.round(fraction * width / 3);
        int randWidthLow = -randWidthHigh;
        for (int i = 0; i < levelsCount; i++) {
            levels[i] = Math.round(fraction * (i + 1) * height + (random.randomInRange(randLow, randHigh)));
            changes[i * 2] = random.randomInRange(randWidthLow, randWidthHigh);
            changes[i * 2 + 1] = random.randomInRange(randWidthLow, randWidthHigh);
        }
        levels[levelsCount - 1] = height;
        lastChange = changes[levelsCount * 2 - 2];
        changes[levelsCount * 2 - 1] = lastChange;
        int lastX1 = 0;
        int lastY = 0;
        int lastX2 = width;
        for (int i = 0; i < levelsCount; i++) {
            if (!branchless) {
                Drawer.drawTextureQuad(lastX1, lastY, lastX2, lastY, width + changes[i + i], -levels[i], changes[i + i + 1], -levels[i]);
            }
            lastX1 = changes[i + i + 1];
            lastY = -levels[i];
            lastX2 = width + changes[i + i];
            points.add(new Point(0, levels[i] - height));
        }
        // Branches
        int thick = 2 * width / 3;
        float heightModifier = 0.9f;
        float spreadModifier = 0.6f;
        glTranslatef(lastChange, -height, 0);
        Drawer.setCentralPoint();
        drawBranch(width / 2 - thick / 2, height, 0, thick, thick / 2, 0);
        boolean left = random.nextBoolean();
        if (left) {
            drawBranch(0, Math.round(height * heightModifier), -spread * spreadModifier, thick, thick / 2, 0);
            Drawer.translate(0, Math.round(height * fraction / 4));
            drawBranch(width - thick, Math.round(height * heightModifier), spread * spreadModifier, thick, thick / 2, Math.round(height * fraction / 3));
        } else {
            drawBranch(width - thick, Math.round(height * heightModifier), spread * spreadModifier, thick, thick / 2, 0);
            Drawer.translate(0, Math.round(height * fraction / 4));
            drawBranch(0, Math.round(height * heightModifier), -spread * spreadModifier, thick, thick / 2, Math.round(height * fraction / 3));
        }
        int sum = 0;
        for (int i = 0; i < levelsCount - 1; i++) {
            if (levels[i] > 3 * height / 5) {
                int change = random.randomInRange(Math.round(height * fraction / 4), Math.round(height * fraction / 2));
                Drawer.translate(0, change);
                i--;
                if (left) {
                    drawBranch(changes[(levelsCount - 1 - i) * 2 - 1] - lastChange + 2, height, -spread, thick, thick / 2, levels[i] + Math.round(height
                            * fraction / 3) + change);
                } else {
                    drawBranch(width + changes[(levelsCount - 1 - i) * 2 - 2] - lastChange - thick - 2, height, spread, thick, thick / 2, levels[i] + Math
                            .round(height * fraction / 3) + change);
                }
                Drawer.translate(0, -change);
                break;
            }
            Drawer.translate(0, levels[i] - sum);
            sum += levels[i] - sum;
            if (left) {
                drawBranch(changes[(levelsCount - 1 - i) * 2 - 1] - lastChange + 2, height, -spread, thick, thick / 2, levels[i] + Math.round(height
                        * fraction / 3));
            } else {
                drawBranch(width + changes[(levelsCount - 1 - i) * 2 - 2] - lastChange - thick - 2, height, spread, thick, thick / 2, levels[i] + Math.round(height * fraction / 3));
            }
            left = !left;
        }
        Drawer.returnToCentralPoint();
    }

    private void drawBranch(int x, int height, float spread, int widthBase, int widthTop, int yShift) {
        int length = height / 2 + random.randomInRange(0, height / 25);
        int deviation = Math.round(spread * (height / 3 + random.randomInRange(0, height / 6)));
        int change = -8 + random.next(4);
        int xPosition = x + deviation / 2 + change;
        if (!branchless) {
            Drawer.drawTextureQuad(x, 0, x + widthBase, 0, xPosition + (widthTop + widthBase) / 2, -length / 2, xPosition, -length / 2);
            Drawer.drawTextureQuad(xPosition,
                    -length / 2, xPosition + (widthTop + widthBase) / 2, -length / 2, x + deviation + widthTop, -length, x + deviation, -length);
        }
        // End of branch
        int change2 = -16 + random.next(5);
        if (!branchless) {
            Drawer.drawTextureTriangle(x + deviation, -length, x + deviation + widthTop, -length, x + deviation + 2 * deviation / 3 + change2, -length - 2
                    * length / 3);
        }
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
            if (!branchless) {
                if (deviation > 0) {
                    Drawer.drawTextureQuad(x + Math.round(1.3f * xPosition), Math.round(-1f * length), x + Math.round(1.3f * xPosition), Math.round(-1f
                            * length) + Math.round(-0.5f * widthTop), xB, yB, xA, yA);
                } else {
                    Drawer.drawTextureQuad(xA, yA, xB, yB,
                            x + Math.round(1.3f * xPosition), Math.round(-1f * length) + Math.round(-0.5f * widthTop),
                            x + Math.round(1.3f * xPosition), Math.round(-1f * length));
                }
            }
            points.add(new Point(x + Math.round(1.3f * xPosition), Math.round(-1f * length) + yShift));
            if (!branchless) {
                if (deviation > 0) {
                    Drawer.drawTextureQuad(x + Math.round(1.5f * xPosition), Math.round(-0.95f * length) - change1,
                            x + Math.round(1.5f * xPosition), Math.round(-0.95f * length) + Math.round(-0.4f * widthTop) - change1 + Math.round(-0.4f
                                    * widthTop),
                            x + Math.round(1.3f * xPosition), Math.round(-1f * length) + Math.round(-0.5f * widthTop),
                            x + Math.round(1.3f * xPosition), Math.round(-1f * length)
                    );
                } else {
                    Drawer.drawTextureQuad(
                            x + Math.round(1.5f * xPosition), Math.round(-0.95f * length) + Math.round(-0.4f * widthTop) - change1 + Math.round(-0.4f
                                    * widthTop),
                            x + Math.round(1.5f * xPosition), Math.round(-0.95f * length) - change1,
                            x + Math.round(1.3f * xPosition), Math.round(-1f * length),
                            x + Math.round(1.3f * xPosition), Math.round(-1f * length) + Math.round(-0.5f * widthTop));
                }
            }
            points.add(new Point(x + Math.round(1.5f * xPosition), Math.round(-0.95f * length) - change1 + yShift));
            if (!branchless) {
                if (deviation > 0) {
                    Drawer.drawTextureTriangle(
                            x + Math.round(1.5f * xPosition), Math.round(-0.95f * length) + Math.round(-0.4f * widthTop) - change1 + Math.round(-0.4f
                                    * widthTop),
                            x + Math.round(1.5f * xPosition), Math.round(-0.95f * length) - change1,
                            x + Math.round(1.7f * xPosition), Math.round(-1.3f * length)
                    );
                } else {
                    Drawer.drawTextureTriangle(
                            x + Math.round(1.7f * xPosition), Math.round(-1.3f * length),
                            x + Math.round(1.5f * xPosition), Math.round(-0.95f * length) - change1,
                            x + Math.round(1.5f * xPosition), Math.round(-0.95f * length) + Math.round(-0.4f * widthTop) - change1 + Math.round(-0.4f
                                    * widthTop)
                    );
                }
            }
            points.add(new Point(x + Math.round(1.7f * xPosition), Math.round(-1.3f * length) + yShift));
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
            if (!branchless) {
                if (deviation > 0) {
                    Drawer.drawTextureQuad(x + Math.round(0.9f * xPosition), Math.round(-0.6f * length),
                            x + Math.round(0.9f * xPosition), Math.round(-0.6f * length) + Math.round(-0.5f * widthTop),
                            xB, yB, xA, yA);
                } else {
                    Drawer.drawTextureQuad(xA, yA, xB, yB,
                            x + Math.round(0.9f * xPosition), Math.round(-0.6f * length) + Math.round(-0.5f * widthTop),
                            x + Math.round(0.9f * xPosition), Math.round(-0.6f * length));
                }
                points.add(new Point(x + Math.round(0.9f * xPosition), Math.round(-0.6f * length) + yShift));
            }
            if (!branchless) {
                if (deviation > 0) {
                    Drawer.drawTextureTriangle(x + Math.round(0.9f * xPosition), Math.round(-0.6f * length) + Math.round(-0.5f * widthTop),
                            x + Math.round(0.9f * xPosition), Math.round(-0.6f * length),
                            x + Math.round(1.8f * xPosition), Math.round(-(0.75f + rand) * length));
                } else {
                    Drawer.drawTextureTriangle(x + Math.round(1.8f * xPosition), Math.round(-(0.75f + rand) * length),
                            x + Math.round(0.9f * xPosition), Math.round(-0.6f * length),
                            x + Math.round(0.9f * xPosition), Math.round(-0.6f * length) + Math.round(-0.5f * widthTop));
                }
            }
            points.add(new Point(x + Math.round(1.8f * xPosition), Math.round(-(0.75f + rand) * length) + yShift));
//            points.add(x + Math.round(1.8f * xPosition), -length - 2 * length / 3 + yShift);

            // Small Branch
            rand = random.next(10) / 3072f;
            if (!branchless) {
                if (deviation > 0) {
                    Drawer.drawTextureQuad(xA, yA, xA + Math.round(0.125f * xPosition), yA - Math.round(length * 0.1f),
                            xA + Math.round(0.35f * xPosition), Math.round(-1.0f * length),
                            xA + Math.round(0.3f * xPosition), Math.round(-1.0f * length) + Math.round(-0.4f * widthTop));
                } else {
                    Drawer.drawTextureQuad(xA + Math.round(0.3f * xPosition), Math.round(-1.0f * length) + Math.round(-0.4f * widthTop),
                            xA + Math.round(0.35f * xPosition), Math.round(-1.0f * length),
                            xA + Math.round(0.125f * xPosition), yA - Math.round(length * 0.1f),
                            xA, yA);
                }
            }
            points.add(new Point(xA + Math.round(0.3f * xPosition), Math.round(-1.0f * length) + Math.round(-0.4f * widthTop) + yShift));
            if (!branchless) {
                if (deviation > 0) {
                    Drawer.drawTextureTriangle(xA + Math.round(0.45f * xPosition), Math.round(-(1.2f + rand) * length),
                            xA + Math.round(0.3f * xPosition), Math.round(-1.0f * length) + Math.round(-0.4f * widthTop),
                            xA + Math.round(0.35f * xPosition), Math.round(-1.0f * length));
                } else {
                    Drawer.drawTextureTriangle(xA + Math.round(0.35f * xPosition), Math.round(-1.0f * length),
                            xA + Math.round(0.3f * xPosition), Math.round(-1.0f * length) + Math.round(-0.4f * widthTop),
                            xA + Math.round(0.45f * xPosition), Math.round(-(1.2f + rand) * length));
                }
            }
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
            if (!branchless) {
                Drawer.drawTextureQuad(xA, yA, xB, yB,
                        xA + Math.round(Math.signum(xPosition) * length * 0.2f), Math.round(-0.9f * length) + Math.round(-0.5f * widthTop),
                        xA + Math.round(Math.signum(xPosition) * length * 0.2f) + Math.round(-0.4f * widthTop), Math.round(-0.9f * length));
            }
            points.add(new Point(xA + Math.round(Math.signum(xPosition) * length * 0.2f) + Math.round(-0.4f * widthTop), Math.round(-0.9f * length) + yShift));
            if (!branchless) {
                Drawer.drawTextureTriangle(xA + Math.round(Math.signum(xPosition) * length * 0.3f) + Math.round(-0.4f * widthTop),
                        Math.round(-(1.1f + rand) * length),
                        xA + Math.round(Math.signum(xPosition) * length * 0.2f) + Math.round(-0.4f * widthTop), Math.round(-0.9f * length),
                        xA + Math.round(Math.signum(xPosition) * length * 0.2f), Math.round(-0.9f * length) + Math.round(-0.5f * widthTop));
            }
            points.add(new Point(xA + Math.round(Math.signum(xPosition) * length * 0.3f) + Math.round(-0.4f * widthTop), Math.round(-(1.1f + rand) * length)
                    + yShift));

            // Small Branch
            rand = random.next(10) / 3072f;
            if (!branchless) {
                Drawer.drawTextureQuad(xA - Math.round(Math.signum(xPosition) * length * 0.2f) - Math.round(-0.4f * widthTop), Math.round(-0.9f * length),
                        xA - Math.round(Math.signum(xPosition) * length * 0.2f), Math.round(-0.9f * length) + Math.round(-0.5f * widthTop),
                        xB, yB, xA, yA);
            }
            points.add(new Point(xA - Math.round(Math.signum(xPosition) * length * 0.2f), Math.round(-0.9f * length) + Math.round(-0.5f * widthTop) + yShift));
            if (!branchless) {
                Drawer.drawTextureTriangle(xA - Math.round(Math.signum(xPosition) * length * 0.2f), Math.round(-0.9f * length) + Math.round(-0.5f * widthTop),
                        xA - Math.round(Math.signum(xPosition) * length * 0.2f) - Math.round(-0.4f * widthTop), Math.round(-0.9f * length),
                        xA - Math.round(Math.signum(xPosition) * length * 0.3f) - Math.round(-0.4f * widthTop), Math.round(-(1.1f + rand) * length));
            }
            points.add(new Point(xA - Math.round(Math.signum(xPosition) * length * 0.3f) - Math.round(-0.4f * widthTop), Math.round(-(1.1f + rand) * length)
                    + yShift));
        }
    }

    private void drawLeafs() {
        points.sort(comparator);
        int rand1, rand2;
        Drawer.setCentralPoint();
        int radius = height / 25;
        int dif = (radius + radius) / 3;
        int dif2 = dif * dif * 100;
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
                rand2 += random.randomInRange(-dif, dif);
                if (rand1 * rand1 + rand2 * rand2 > dif2) {
                    rand1 = random.randomInRange(-dif, dif);
                    rand2 = random.randomInRange(-dif, dif);
                }
            }
        }
    }

    private void randomLeaf(int i, int x, int y, float maxX, float maxY, float minY) {
        if (Math.abs(points.get(i).getY() + y) < leafHeight * 0.7f
                && (points.get(i).getY() + y < 0 || points.get(i).getY() + y < leafHeight * 0.3)
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
            Drawer.translate(xEffect + getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), yEffect + getY() + 20 - woodHeight + collision.getHeightHalf());
            Drawer.drawShapeBottomInShade(fbo, 1, woodHeight);
            Drawer.translate(0, -fbo.getHeight() + leafHeight);
            Drawer.drawShapeTopInBlack(fbo, woodHeight);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure) {
        if (appearance != null) {
            glPushMatrix();
            Drawer.translate(xEffect + getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), yEffect + getY() + 20 - woodHeight + collision.getHeightHalf());
            Drawer.drawShapeBottomInBlack(fbo, woodHeight);
            Drawer.translate(0, -fbo.getHeight() + leafHeight);
            Drawer.drawShapeTopInBlack(fbo, woodHeight);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, int xStart, int xEnd) {
        if (appearance != null) {
            glPushMatrix();
            Drawer.translate(xEffect + getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), yEffect + getY() + 20 - woodHeight + collision.getHeightHalf());
            Drawer.drawShapeBottomPartInShade(fbo, 1, fbo.getWidth() / 2 - collision.getWidth() + xStart, fbo.getWidth() / 2 - collision.getWidth() + xEnd,
                    woodHeight);
            Drawer.translate(0, -fbo.getHeight() + leafHeight);
            Drawer.drawShapeTopInBlack(fbo, woodHeight);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, int xStart, int xEnd) {
        if (appearance != null) {
            glPushMatrix();
            Drawer.translate(xEffect + getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), yEffect + getY() + 20 - woodHeight + collision.getHeightHalf());
            Drawer.drawShapeBottomPartInBlack(fbo, fbo.getWidth() / 2 - collision.getWidth() + xStart, fbo.getWidth() / 2 - collision.getWidth() + xEnd,
                    woodHeight);
            Drawer.translate(0, -fbo.getHeight() + leafHeight);
            Drawer.drawShapeTopInBlack(fbo, woodHeight);
            glPopMatrix();
        }
    }

    @Override
    public int getXSpriteBegin(boolean... forCover) {
        if (forCover.length > 0) {
            if (forCover[0]) {
                return getX() - fbo.getActualWidth() / 2;
            } else {
                return getX() - collision.getWidth();
            }
        }
        return getX() - fbo.getActualWidth() / 2;
    }

    @Override
    public int getYSpriteBegin(boolean... forCover) {
        if (forCover.length > 0) {
            if (forCover[0]) {
                return getY() + 20 + collision.getHeightHalf() - (fbo.getHeight() - leafHeight);
            } else {
                return getY() - collision.getHeight() - height;
            }
        }
        return getY() + 20 + collision.getHeightHalf() - (fbo.getHeight() - leafHeight);
    }

    @Override
    public int getXSpriteEnd(boolean... forCover) {
        if (forCover.length > 0) {
            if (forCover[0]) {
                return getX() + fbo.getActualWidth() / 2;
            } else {
                return getX() + collision.getWidth();
            }
        }
        return getX() + fbo.getActualWidth() / 2;
    }

    @Override
    public int getYSpriteEnd(boolean... forCover) {
        if (forCover.length > 0 && forCover[0]) {
            return getY() + 20 + collision.getHeightHalf() - (fbo.getHeight() - leafHeight * 2);
        }
        return getY() + 20 + collision.getHeightHalf();
    }
}
