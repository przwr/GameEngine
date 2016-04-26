package gamecontent.environment;

import collision.Figure;
import collision.OpticProperties;
import collision.Rectangle;
import engine.systemcommunication.Time;
import engine.utilities.Drawer;
import engine.utilities.Methods;
import engine.utilities.Point;
import engine.utilities.RandomGenerator;
import game.Settings;
import game.gameobject.GameObject;
import net.jodk.lang.FastMath;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import sprites.Sprite;
import sprites.fbo.FrameBufferObject;
import sprites.fbo.MultiSampleFrameBufferObject;
import sprites.vbo.VertexBufferObject;

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
    private static ArrayList<Point> points;
    int width, height;
    float spread;
    private FrameBufferObject fbo;
    private VertexBufferObject vbo;
    private Color branchColor;
    private Color leafColor;
    private Comparator<Point> comparator = (p1, p2) -> Math.abs(p2.getX()) * 100 - Math.abs(p1.getX()) * 100 + p1.getY() - p2.getY();

    private float windStage, windDirectionModifier;
    private boolean windChange;

    public Bush(int x, int y) {
        this(x, y, 14, 80, 0.8f);
    }

    public Bush(int x, int y, int width, int height, float spread) {
        initialize("Bush", x, y);
        setCollision(Rectangle.create(width, Methods.roundDouble(width * Methods.ONE_BY_SQRT_ROOT_OF_2), OpticProperties.NO_SHADOW, this));
        setSimpleLighting(false);
        solid = true;
        canCover = true;
        hasStaticShadow = true;
        this.width = width;
        this.height = height;
        this.spread = spread;
        int fboWidth = Math.round(spread * 3f * height);
        int fboHeight = Math.round(height * 2.5f);
        int ins = random.random(10);
        String bushCode = width + "-" + height + "-" + spread + "-" + ins;
        fbo = fbos.get(bushCode);
        if (fbo == null) {
            fbo = new MultiSampleFrameBufferObject(fboWidth, fboHeight, Settings.maxSamples);
            fbos.put(bushCode, fbo);
        }
        appearance = fbo;
        branchColor = new Color(0x8C6B1F);//new Color(0.4f, 0.3f, 0.15f);
        leafColor = new Color(0.1f, 0.4f, 0.15f);//new Color(0x388A4B);
        instances.add(this);
        windStage = random.randomInRange(0, 31416);
        windDirectionModifier = random.randomInRange(-18, 18);
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
                points = new ArrayList<>();
                bark = map.place.getSprite("bark", "", true);
                leaf = map.place.getSprite("leaf", "", true);
                fbo.activate();
                glClearColor(0.5f, 0.35f, 0.2f, 0);
                glClear(GL_COLOR_BUFFER_BIT);
                Drawer.regularShader.rememberDefaultMatrix();
                Drawer.regularShader.resetDefaultMatrix();
                Drawer.regularShader.translateDefault(fbo.getWidth() / 2, Display.getHeight() - 20);
                drawBush();
                Drawer.regularShader.translateDefault(-fbo.getWidth() / 2, -Display.getHeight() + 20);
                Drawer.regularShader.restoreDefaultMatrix();
                fbo.deactivate();
                points.clear();
                points = null;
            }
        }
        if (vbo == null) {
            float[] vertices = {0};
            int[] indices = {0};
            vbo = VertexBufferObject.create(vertices, vertices, indices);
        }
    }

    @Override
    public void render() {
        preRender();
        Drawer.regularShader.translate(getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20 - fbo.getHeight() + collision.getHeightHalf());
        if (map.getWindStrength() > 2) {
            updateWithWind();
        } else if (vbo.getVertexCount() > 1) {
            float[] vertices = {0};
            int[] indices = {0};
            vbo.updateAll(vertices, vertices, indices);
        }
        if (vbo.getVertexCount() > 1) {
            fbo.bindCheck();
            vbo.renderTexturedTriangles(0, vbo.getVertexCount());
        } else {
            fbo.render();
        }
        Drawer.refreshColor();
    }

    private void updateWithWind() {
        Drawer.streamVertexData.clear();
        Drawer.streamColorData.clear();
        Drawer.streamIndexData.clear();
        int yStart, yShift, yEnd = 0, vc = 0;
        int change = 34;
        int yMax = fbo.getHeight();
        float xMod = 1.1f;
        float xDirection = (float) Methods.xRadius(map.getWindDirection() + windDirectionModifier, map.getWindStrength());
        float yDirection = (float) Methods.yRadius(map.getWindDirection() + windDirectionModifier, map.getWindStrength());
        yDirection = Math.round(yDirection * Methods.ONE_BY_SQRT_ROOT_OF_2);
        if (windStage >= 31416) {
            windChange = false;
        } else if (windStage <= 0) {
            windChange = true;
        }
        windStage += (windChange ? 0.1 : -0.1) * (Time.getDelta() + (random.randomInRange(-5, 5) / 10f));
        float stageValue = (float) FastMath.sin(windStage) * 0.5f;
        xDirection += Methods.roundDouble(xDirection * stageValue);
        yDirection += Methods.roundDouble(yDirection * stageValue);
        int squeezShift = (int) yDirection + (yMax - Methods.roundDouble(FastMath.sqrt(yMax * yMax - xDirection * xDirection)));
        float ySqueez = (yMax - squeezShift) / (float) yMax;
        squeezShift = Math.round(squeezShift / (fbo.getWidth() / (float) yMax));
        int slices = fbo.getHeight() / change + (fbo.getHeight() % change == 0 ? 0 : 1);
        int trunkE = 1;
        int trunkS = 1;
        for (int i = 0; i < slices; i++) {
            yStart = yEnd;
            yEnd += change;
            if (yEnd > yMax) {
                yEnd = yMax;
            }
            if (i == slices - 2) {
                trunkS = 0;
            }
            if (i == slices - 1) {
                trunkE = 0;
            }
            yShift = yMax - yEnd;
            Drawer.streamVertexData.add(
                    trunkE * xDirection * ((yShift + change) / (float) yMax), squeezShift + (yStart) * ySqueez,
                    trunkS * xDirection / xMod * ((yShift) / (float) yMax), squeezShift + (fbo.getHeight() - yShift) * ySqueez,
                    fbo.getWidth() + trunkS * xDirection / xMod * ((yShift) / (float) yMax), squeezShift + (fbo.getHeight() - yShift) * ySqueez,
                    fbo.getWidth() + trunkE * xDirection * ((yShift + change) / (float) yMax), squeezShift + (yStart) * ySqueez
            );
            Drawer.streamColorData.add(
                    0, (fbo.getHeight() - yStart) / (float) fbo.getHeight(),
                    0, (yShift) / (float) fbo.getHeight(),
                    1f, (yShift) / (float) fbo.getHeight(),
                    1f, (fbo.getHeight() - yStart) / (float) fbo.getHeight()
            );
            Drawer.streamIndexData.add(vc, vc + 1, vc + 3, vc + 2, vc + 3, vc + 1);
            vc += 4;
            xDirection /= xMod;
        }
        vbo.updateAll(Drawer.streamVertexData.toArray(), Drawer.streamColorData.toArray(), Drawer.streamIndexData.toArray());
    }

    @Override
    public void renderStaticShadow() {
        if (map.getWindStrength() > 2 && vbo != null && vbo.getVertexCount() > 1) {
            fbo.renderStaticShadowFromVBO(vbo, this, fbo.getHeight() - 20 - collision.getHeightHalf(), -fbo.getWidth() / 2 - collision.getWidthHalf());
        } else {
            fbo.renderStaticShadow(this, fbo.getHeight() - 20 - collision.getHeightHalf(), -fbo.getWidth() / 2 - collision.getWidthHalf());
        }
    }

    private void drawBush() {
        bark.bindCheck();
        Drawer.setColorStatic(new Color(branchColor.r + (random.next(10) / 10240f), branchColor.g + (random.next(10) / 10240f), branchColor.b + (random.next(10) / 10240f)));
        drawBranches();
        drawRoots();
        drawLeafs();
    }

    private void drawRoots() {
        int change1 = -2 + random.next(2) + Math.round(width * 0.15f + width * random.next(10) / 10240f);
        int change2 = -2 + random.next(2) + Math.round(width * 0.15f + width * random.next(10) / 10240f);
        int change3 = -2 + random.next(2) + Math.round(width * 0.15f + width * random.next(10) / 10240f);
        Drawer.drawTextureQuad(width + change1, 2, width + change1, -6, width, -12, width / 2, 0);
        Drawer.drawTextureTriangle(width + change1, -6, width + change1, 2, (width - change1) * 2, change2 < 6 ? change2 : 6);
        Drawer.drawTextureQuad(width / 2, 0, 0, -12, -change2, -6, -change2, 2);
        Drawer.drawTextureTriangle(-width + change1 * 2, change1 < 6 ? change1 : 6, -change2, 2, -change2, -6);
        boolean left = random.nextBoolean();
        if (left) {
            Drawer.drawTextureQuad(2 * width / 3, 0, width / 5, -5, width / 3 - change3 / 2, 4, width / 3 + change3 / 2, 6);
            Drawer.drawTextureTriangle(-3, 12, width / 3 + change3 / 2, 6, width / 3 - change3 / 2, 4);
        } else {
            Drawer.drawTextureQuad(2 * width / 3 - change3 / 2, 6, 2 * width / 3 + change3 / 2, 4, 4 * width / 5, -5, width / 3, 0);
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
        Drawer.drawTextureQuad(x, 0, x + widthBase, 0, xPosition + (widthTop + widthBase) / 2, -length / 2, xPosition, -length / 2);
        Drawer.drawTextureQuad(xPosition, -length / 2, xPosition + (widthTop + widthBase) / 2, -length / 2, x + deviation + widthTop, -length, x + deviation,
                -length);
        // End of branch
        int change2 = -16 + random.next(5);
        Drawer.drawTextureTriangle(x + deviation, -length, x + deviation + widthTop, -length, x + deviation + 2 * deviation / 3 + change2, -length - 2
                * length / 3);
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
            if (deviation > 0) {
                Drawer.drawTextureQuad(x + Math.round(1.3f * xPosition), Math.round(-1f * length), x + Math.round(1.3f * xPosition), Math.round(-1f
                        * length) + Math.round(-0.5f * widthTop), xB, yB, xA, yA);
            } else {
                Drawer.drawTextureQuad(xA, yA, xB, yB,
                        x + Math.round(1.3f * xPosition), Math.round(-1f * length) + Math.round(-0.5f * widthTop),
                        x + Math.round(1.3f * xPosition), Math.round(-1f * length));
            }
            points.add(new Point(x + Math.round(1.3f * xPosition), Math.round(-1f * length) + yShift));
            if (deviation > 0) {
                Drawer.drawTextureQuad(x + Math.round(1.5f * xPosition), Math.round(-0.95f * length) - change1,
                        x + Math.round(1.5f * xPosition), Math.round(-0.95f * length) + Math.round(-0.4f * widthTop) - change1 + Math.round(-0.4f
                                * widthTop),
                        x + Math.round(1.3f * xPosition), Math.round(-1f * length) + Math.round(-0.5f * widthTop),
                        x + Math.round(1.3f * xPosition), Math.round(-1f * length));
            } else {
                Drawer.drawTextureQuad(x + Math.round(1.5f * xPosition), Math.round(-0.95f * length) + Math.round(-0.4f * widthTop) - change1
                        + Math.round(-0.4f * widthTop),
                        x + Math.round(1.5f * xPosition), Math.round(-0.95f * length) - change1,
                        x + Math.round(1.3f * xPosition), Math.round(-1f * length),
                        x + Math.round(1.3f * xPosition), Math.round(-1f * length) + Math.round(-0.5f * widthTop));
            }
            points.add(new Point(x + Math.round(1.5f * xPosition), Math.round(-0.95f * length) - change1 + yShift));
            if (deviation > 0) {
                Drawer.drawTextureTriangle(x + Math.round(1.5f * xPosition), Math.round(-0.95f * length) + Math.round(-0.4f * widthTop) - change1 + Math
                        .round(-0.4f * widthTop),
                        x + Math.round(1.5f * xPosition), Math.round(-0.95f * length) - change1,
                        x + Math.round(1.7f * xPosition), Math.round(-1.3f * length));
            } else {
                Drawer.drawTextureTriangle(x + Math.round(1.7f * xPosition), Math.round(-1.3f * length),
                        x + Math.round(1.5f * xPosition), Math.round(-0.95f * length) - change1,
                        x + Math.round(1.5f * xPosition), Math.round(-0.95f * length) + Math.round(-0.4f * widthTop) - change1 + Math.round(-0.4f
                                * widthTop));
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
            if (deviation > 0) {
                Drawer.drawTextureTriangle(x + Math.round(0.9f * xPosition), Math.round(-0.6f * length) + Math.round(-0.5f * widthTop),
                        x + Math.round(0.9f * xPosition), Math.round(-0.6f * length),
                        x + Math.round(1.8f * xPosition), Math.round(-(0.75f + rand) * length));
            } else {
                Drawer.drawTextureTriangle(x + Math.round(1.8f * xPosition), Math.round(-(0.75f + rand) * length),
                        x + Math.round(0.9f * xPosition), Math.round(-0.6f * length),
                        x + Math.round(0.9f * xPosition), Math.round(-0.6f * length) + Math.round(-0.5f * widthTop));
            }
            points.add(new Point(x + Math.round(1.8f * xPosition), Math.round(-(0.75f + rand) * length) + yShift));
//            points.add(x + Math.round(1.8f * xPosition), -length - 2 * length / 3 + yShift);

            // Small Branch
            rand = random.next(10) / 3072f;
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
            points.add(new Point(xA + Math.round(0.3f * xPosition), Math.round(-1.0f * length) + Math.round(-0.4f * widthTop) + yShift));
            if (deviation > 0) {
                Drawer.drawTextureTriangle(xA + Math.round(0.45f * xPosition), Math.round(-(1.2f + rand) * length),
                        xA + Math.round(0.3f * xPosition), Math.round(-1.0f * length) + Math.round(-0.4f * widthTop),
                        xA + Math.round(0.35f * xPosition), Math.round(-1.0f * length));
            } else {
                Drawer.drawTextureTriangle(xA + Math.round(0.35f * xPosition), Math.round(-1.0f * length),
                        xA + Math.round(0.3f * xPosition), Math.round(-1.0f * length) + Math.round(-0.4f * widthTop),
                        xA + Math.round(0.45f * xPosition), Math.round(-(1.2f + rand) * length));
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
            Drawer.drawTextureQuad(xA, yA, xB, yB,
                    xA + Math.round(Math.signum(xPosition) * length * 0.2f), Math.round(-0.9f * length) + Math.round(-0.5f * widthTop),
                    xA + Math.round(Math.signum(xPosition) * length * 0.2f) + Math.round(-0.4f * widthTop), Math.round(-0.9f * length));
            points.add(new Point(xA + Math.round(Math.signum(xPosition) * length * 0.2f) + Math.round(-0.4f * widthTop), Math.round(-0.9f * length) + yShift));
            Drawer.drawTextureTriangle(xA + Math.round(Math.signum(xPosition) * length * 0.3f) + Math.round(-0.4f * widthTop),
                    Math.round(-(1.1f + rand) * length),
                    xA + Math.round(Math.signum(xPosition) * length * 0.2f) + Math.round(-0.4f * widthTop), Math.round(-0.9f * length),
                    xA + Math.round(Math.signum(xPosition) * length * 0.2f), Math.round(-0.9f * length) + Math.round(-0.5f * widthTop));
            points.add(new Point(xA + Math.round(Math.signum(xPosition) * length * 0.3f) + Math.round(-0.4f * widthTop), Math.round(-(1.1f + rand) * length)
                    + yShift));

            // Small Branch
            rand = random.next(10) / 3072f;
            Drawer.drawTextureQuad(xA - Math.round(Math.signum(xPosition) * length * 0.2f) - Math.round(-0.4f * widthTop), Math.round(-0.9f * length),
                    xA - Math.round(Math.signum(xPosition) * length * 0.2f), Math.round(-0.9f * length) + Math.round(-0.5f * widthTop),
                    xB, yB, xA, yA);
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
        int radius = Methods.roundDouble(Methods.SQRT_ROOT_OF_2 * leaf.getWidth() / 2);
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
            Drawer.regularShader.rotateTranslate(points.get(i).getX() + x, points.get(i).getY() + y, angle);
            Drawer.regularShader.scaleNoReset(1f + random.randomInRange(-50, 50) / 1000f, 1f + random.randomInRange(-50, 50) / 1000f);
            leaf.render();
        }
    }

    @Override
    public void renderShadowLit(Figure figure) {
        if (appearance != null) {
            if (map.getWindStrength() > 2) {
                Drawer.drawShapeLitFromVbo(fbo, vbo, getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20 - fbo.getHeight()
                        + collision.getHeightHalf());
            } else {
                Drawer.drawShapeLit(appearance, getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20 - fbo.getHeight() + collision
                        .getHeightHalf());
            }
        }
    }

    @Override
    public void renderShadow(Figure figure) {
        if (appearance != null) {
            if (map.getWindStrength() > 2) {
                Drawer.drawShapeBlackFromVbo(fbo, vbo, collision.getDarkValue(), getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20 - fbo
                        .getHeight() + collision.getHeightHalf());
            } else {
                Drawer.drawShapeBlack(appearance, collision.getDarkValue(), getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20 - fbo
                        .getHeight() + collision.getHeightHalf());
            }
        }
    }

    @Override
    public void renderShadowLit(int xStart, int xEnd) {
        if (appearance != null) {
            if (map.getWindStrength() > 2) {
                Drawer.drawShapePartLitFromVbo(fbo, vbo, getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20 - fbo.getHeight()
                        + collision.getHeightHalf(), xStart, xEnd);
            } else {
                Drawer.drawShapePartLit(appearance, getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20 - fbo.getHeight() + collision
                        .getHeightHalf(), xStart, xEnd);
            }
        }
    }

    @Override
    public void renderShadow(int xStart, int xEnd) {
        if (appearance != null) {
            if (map.getWindStrength() > 2) {
                Drawer.drawShapePartBlackFromVbo(fbo, vbo, collision.getDarkValue(), getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20 - fbo
                        .getHeight() + collision.getHeightHalf(), xStart, xEnd);
            } else {
                Drawer.drawShapePartBlack(appearance, collision.getDarkValue(), getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20 - fbo
                        .getHeight() + collision.getHeightHalf(), xStart, xEnd);
            }
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
