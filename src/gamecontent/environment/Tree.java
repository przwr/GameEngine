package gamecontent.environment;

import collision.Figure;
import collision.OpticProperties;
import collision.Rectangle;
import engine.lights.ShadowRenderer;
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
public class Tree extends GameObject {

    public static Map<String, FrameBufferObject> fbos = new HashMap<>();
    public static List<Tree> instances = new ArrayList();
    static Sprite bark;
    static Sprite leaf;
    private static RandomGenerator random = RandomGenerator.create();
    private static ArrayList<Point> points;
    private static int maxInstances = 12;
    private final Comparator<Point> comparator = (p1, p2) -> Math.abs(p2.getX()) * 100 - Math.abs(p1.getX()) * 100 + p1.getY() - p2.getY();
    int width, height, leafXShift;
    float spread;
    boolean leafless;
    int partShift;
    private FrameBufferObject fbo;
    private VertexBufferObject vbo;
    private Color branchColor;
    private Color leafColor;
    private int woodHeight, leafHeight;
    private float windStage, windDirectionModifier;
    private boolean windChange, order;

    private Tree(int x, int y, int width, int height, float spread, boolean leafless, boolean background) {
        initialize("Tree", x, y);
        int ins = random.randomInRange(0, maxInstances);
        this.width = width - (int) (width * (ins / (maxInstances * 10f)));
        this.height = height;
        this.leafless = leafless;
        this.spread = spread;
        this.setCanCover(!background);
        this.setHasStaticShadow(!background);
        this.setSolid(!background);
        this.partShift = 48 + height / 40;
        this.woodHeight = height * 2 + 20;
        this.leafHeight = Math.round(height * 1.6f);
        String treeCode = width + "-" + height + "-" + spread + "-" + leafless + "-" + ins;
        fbo = fbos.get(treeCode);
        if (fbo == null) {
            int fboWidth = Math.round(spread * 2.8f * height);
            int fboHeight = Math.round(height * 3.8f);
            fbo = new MultiSampleFrameBufferObject(fboWidth, fboHeight, Settings.maxSamples);
            fbo.setHeightSlice(woodHeight);
            fbo.setHeightShift(-fbo.getHeight() + leafHeight);
            fbo.setPartShift(partShift);
            fbos.put(treeCode, fbo);
        }
        setCollision(Rectangle.create(width, Methods.roundDouble(width * Methods.ONE_BY_SQRT_ROOT_OF_2), background ? OpticProperties.NO_SHADOW
                : OpticProperties.FULL_SHADOW, this));
        appearance = fbo;
        branchColor = new Color(0x8C6B1F);//new Color(0.4f, 0.3f, 0.15f);
        leafColor = new Color(0.1f, 0.4f, 0.15f);//new Color(0x388A4B);
        windStage = random.randomInRange(0, 31416);
        windDirectionModifier = random.randomInRange(-18, 18);
        order = random.chance(50);
        instances.add(this);
    }

    //    32, 200, 0.8f
    public static Tree create(int x, int y, int width, int height, float spread) {
        return new Tree(x, y, width, height, spread, false, false);
    }

    public static Tree createLeafless(int x, int y, int width, int height, float spread) {
        return new Tree(x, y, width, height, spread, true, false);
    }

    public static Tree createBackgroundLeafless(int x, int y, int width, int height, float spread) {
        return new Tree(x, y, width, height, spread, true, false);
    }

    public static Tree createBackground(int x, int y, int width, int height, float spread) {
        return new Tree(x, y, width, height, spread, false, true);
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
                points = new ArrayList<>();
                bark = map.place.getSprite("bark", "", true);
                leaf = map.place.getSprite("leaf", "", true);
                fbo.activate();
                glClearColor(0.5f, 0.35f, 0.2f, 0);
                glClear(GL_COLOR_BUFFER_BIT);

                Drawer.regularShader.rememberDefaultMatrix();
                Drawer.regularShader.resetDefaultMatrix();
                Drawer.regularShader.translateDefault(fbo.getWidth() / 2, Display.getHeight() - 20);
                drawTree();
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
        if (map != null && vbo != null) {
            Drawer.regularShader.translate(getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20 - woodHeight + collision.getHeightHalf());
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
                if (leafless) {
                    fbo.renderBottom();
                } else {
                    fbo.renderTopAndBottom(order);
                }
            }
        }
    }

    private void updateWithWind() {
        Drawer.streamVertexData.clear();
        Drawer.streamColorData.clear();
        Drawer.streamIndexData.clear();
        int yStart, yShift, yEnd = 0, vc = 0;
        int yMax = fbo.getHeight() - woodHeight;
        int change = 32;
        float xMod = 1.1f;
        float xDirection2;
        float heightShift = -fbo.getHeight() + leafHeight;
        float xDirection = xDirection2 = (float) Methods.xRadius(map.getWindDirection() + windDirectionModifier, map.getWindStrength());
        float yDirection = (float) Methods.yRadius(map.getWindDirection() + windDirectionModifier, map.getWindStrength());
        yDirection = Math.round(yDirection * Methods.ONE_BY_SQRT_ROOT_OF_2);
        if (windStage >= 31416) {
            windChange = false;
        } else if (windStage <= 0) {
            windChange = true;
        }
        windStage += (windChange ? 0.1 : -0.1) * (Time.getDelta() + (random.randomInRange(-5, 5) / 10f));
        float stageValue = (float) FastMath.sin(windStage);
        xDirection += Methods.roundDouble(xDirection * stageValue);
        yDirection += Methods.roundDouble(yDirection * stageValue);
        int squeezShift = (int) yDirection + (yMax - Methods.roundDouble(FastMath.sqrt(yMax * yMax - xDirection * xDirection)));
        float ySqueez = (yMax - squeezShift) / (float) yMax;
        squeezShift = Math.round(squeezShift / (fbo.getWidth() / (float) yMax));
        xDirection2 += Methods.roundDouble(xDirection2 * FastMath.sin(windStage + 0.75f + windDirectionModifier / 72f));
        Drawer.streamVertexData.add(
                xDirection / 2, 0,
                0, (2 * woodHeight / 3),
                0 + fbo.getWidth(), (2 * woodHeight / 3),
                xDirection / 2 + fbo.getWidth(), 0,
                0, (2 * woodHeight / 3),
                0, woodHeight,
                0 + fbo.getWidth(), woodHeight,
                0 + fbo.getWidth(), (2 * woodHeight / 3)
        );
        Drawer.streamColorData.add(
                0, woodHeight / (float) fbo.getHeight(),
                0, woodHeight / 3 / (float) fbo.getHeight(),
                1f, woodHeight / 3 / (float) fbo.getHeight(),
                1f, woodHeight / (float) fbo.getHeight(),
                0, woodHeight / 3 / (float) fbo.getHeight(),
                0, 0,
                1f, 0,
                1f, woodHeight / 3 / (float) fbo.getHeight()
        );
        Drawer.streamIndexData.add(vc, vc + 1, vc + 3, vc + 2, vc + 3, vc + 1, vc + 4, vc + 5, vc + 7, vc + 6, vc + 7, vc + 5);
        if (!leafless) {
            vc = 8;
            int slices = yMax / change + (yMax % change == 0 ? 0 : 1);
            for (int i = 0; i < slices; i++) {
                yStart = yEnd;
                yEnd += change;
                if (yEnd > yMax) {
                    yEnd = yMax;
                }
                yShift = yMax - yEnd;
                Drawer.streamVertexData.add(
                        partShift + (xDirection) * ((float) (yShift + change) / (float) (yMax)), squeezShift + (heightShift + woodHeight
                                + yStart) * ySqueez,
                        partShift + (xDirection / xMod) * ((float) (yShift) / (float) (yMax)), squeezShift + (heightShift + fbo.getHeight() - yShift)
                                * ySqueez,
                        partShift + fbo.getWidth() / 2 + (xDirection / xMod) * ((float) (yShift) / (float) (yMax)), squeezShift + (heightShift + fbo
                                .getHeight() - yShift) * ySqueez,
                        partShift + fbo.getWidth() / 2 + (xDirection) * ((float) (yShift + change) / (float) (yMax)), squeezShift + (heightShift
                                + woodHeight + yStart) * ySqueez,
                        -partShift + fbo.getWidth() / 2 + xDirection2 * ((yShift + change) / (float) (yMax)),
                        squeezShift + (heightShift + woodHeight + yStart) * ySqueez,
                        -partShift + fbo.getWidth() / 2 + xDirection2 / xMod * (yShift / (float) (yMax)),
                        squeezShift + (heightShift + fbo.getHeight() - yShift) * ySqueez,
                        -partShift + fbo.getWidth() + xDirection2 / xMod * (yShift / (float) (yMax)),
                        squeezShift + (heightShift + fbo.getHeight() - yShift) * ySqueez,
                        -partShift + fbo.getWidth() + xDirection2 * ((yShift + change) / (float) (yMax)),
                        squeezShift + (heightShift + woodHeight + yStart) * ySqueez
                );
                Drawer.streamColorData.add(
                        0, (fbo.getHeight() - yStart) / (float) fbo.getHeight(),
                        0, (woodHeight + yShift) / (float) fbo.getHeight(),
                        0.5f, (woodHeight + yShift) / (float) fbo.getHeight(),
                        0.5f, (fbo.getHeight() - yStart) / (float) fbo.getHeight(),
                        0.5f, (fbo.getHeight() - yStart) / (float) fbo.getHeight(),
                        0.5f, (woodHeight + yShift) / (float) fbo.getHeight(),
                        1f, (woodHeight + yShift) / (float) fbo.getHeight(),
                        1f, (fbo.getHeight() - yStart) / (float) fbo.getHeight()
                );
                if (order) {
                    Drawer.streamIndexData.add(vc, vc + 1, vc + 3, vc + 2, vc + 3, vc + 1, vc + 4, vc + 5, vc + 7, vc + 6, vc + 7, vc + 5);
                } else {
                    Drawer.streamIndexData.add(vc + 4, vc + 5, vc + 7, vc + 6, vc + 7, vc + 5, vc, vc + 1, vc + 3, vc + 2, vc + 3, vc + 1);
                }
                vc += 8;
                xDirection /= xMod;
                xDirection2 /= xMod;
            }
        }
        vbo.updateAll(Drawer.streamVertexData.toArray(), Drawer.streamColorData.toArray(), Drawer.streamIndexData.toArray());
    }

    @Override
    public void renderStaticShadow() {
        if (map != null && map.getWindStrength() > 2 && vbo != null && vbo.getVertexCount() > 1) {
            fbo.renderStaticShadowFromVBO(vbo, this, woodHeight - 20 - collision.getHeightHalf(), -fbo.getWidth() / 2 - collision.getWidthHalf());
        } else {
            if (leafless) {
                fbo.renderStaticShadowBottom(this, woodHeight - 20 - collision.getHeightHalf(), -fbo.getWidth() / 2 - collision.getWidthHalf());
            } else {
                fbo.renderStaticShadowTopAndBottom(this, woodHeight - 20 - collision.getHeightHalf(), -fbo.getWidth() / 2 - collision.getWidthHalf());
            }
        }
    }

    private void drawTree() {
        bark.bindCheck();
        Drawer.setColorStatic(new Color(branchColor.r + (random.next(10) / 10240f), branchColor.g + (random.next(10) / 10240f), branchColor.b + (random.next(10)
                / 10240f)));
        drawTrunkAndBranches();
        if (!leafless) {
            drawLeafs();
        }
    }

    private void drawRoots() {
        int change1 = Math.round(width * 0.2f + width * random.next(10) / 10240f);
        int change2 = Math.round(width * 0.2f + width * random.next(10) / 10240f);
        int change3 = Math.round(width * 0.15f + width * random.next(10) / 10240f);
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
        int levelsCount = 1 + (2 * height / 3) / (leafless ? 40 : 50);
        int[] levels = new int[levelsCount];
        int[] changes = new int[levelsCount * 2];
        float fraction = 1 / (float) levelsCount;
        int lastChange;
        int randHigh = Math.round(fraction * height / 5f);
        int randLow = -randHigh;
        int randWidthHigh = Math.round(fraction * width / 10f);
        int randWidthLow = -randWidthHigh;
        float tilt = random.randomInRange((int) (-0.2f * width), (int) (0.2f * width));
        for (int i = 0; i < levelsCount; i++) {
            levels[i] = Math.round(fraction * (i + 1) * height + (random.randomInRange(randLow, randHigh)));
            changes[i * 2] = random.randomInRange(randWidthLow, randWidthHigh) + (int) tilt;
            changes[i * 2 + 1] = random.randomInRange(randWidthLow, randWidthHigh) + (int) tilt;
            tilt /= 1.1f;
        }
        levels[levelsCount - 1] = height;
        lastChange = changes[levelsCount * 2 - 2];
        leafXShift = lastChange;
        changes[levelsCount * 2 - 1] = lastChange;
        int lastX1 = 0;
        int lastY = 0;
        int lastX2 = width;
        for (int i = 0; i < levelsCount; i++) {
            Drawer.drawTextureQuad(lastX1, lastY, lastX2, lastY, width + changes[i + i], -levels[i], changes[i + i + 1], -levels[i]);
            lastX1 = changes[i + i + 1];
            lastY = -levels[i];
            lastX2 = width + changes[i + i];
            points.add(new Point(0, levels[i] - height));
        }
        Drawer.regularShader.translate(lastChange * 0.2f, 0);
        drawRoots();
        Drawer.regularShader.translate(0, 0);
        // Branches
        int thick = 2 * width / 3;
        float heightModifier = 0.9f;
        float spreadModifier = 0.6f;
        Drawer.regularShader.translate(lastChange, -height);
        drawBranch(width / 2 - thick / 2, height, 0, thick, thick / 2, 0, true);
        boolean left = random.nextBoolean();
        if (left) {
            drawBranch(0, Math.round(height * heightModifier), -spread * spreadModifier, thick, thick / 2, 0, true);
            Drawer.regularShader.translateNoReset(0, Math.round(height * fraction / 4));
            drawBranch(width - thick, Math.round(height * heightModifier), spread * spreadModifier, thick, thick / 2, Math.round(height * fraction / 3), true);
        } else {
            drawBranch(width - thick, Math.round(height * heightModifier), spread * spreadModifier, thick, thick / 2, 0, true);
            Drawer.regularShader.translateNoReset(0, Math.round(height * fraction / 4));
            drawBranch(0, Math.round(height * heightModifier), -spread * spreadModifier, thick, thick / 2, Math.round(height * fraction / 3), true);
        }
        int sum = 0;
        for (int i = 0; i < levelsCount - 1; i++) {
            if (levels[i] > 3 * height / 5) {
                int change = random.randomInRange(Math.round(height * fraction / 4), Math.round(height * fraction / 2));
                Drawer.regularShader.translateNoReset(0, change);
                i--;
                if (left) {
                    drawBranch(changes[(levelsCount - 1 - i) * 2 - 1] - lastChange + 2, height, -spread, thick, thick / 2, levels[i] + Math.round(height
                            * fraction / 3) + change, false);
                } else {
                    drawBranch(width + changes[(levelsCount - 1 - i) * 2 - 2] - lastChange - thick - 2, height, spread, thick, thick / 2, levels[i] + Math
                            .round(height * fraction / 3) + change, false);
                }
                Drawer.regularShader.translateNoReset(0, -change);
                break;
            }
            Drawer.regularShader.translateNoReset(0, levels[i] - sum);
            sum += levels[i] - sum;
            if (left) {
                drawBranch(changes[(levelsCount - 1 - i) * 2 - 1] - lastChange + 2, height, -spread, thick, thick / 2, levels[i] + Math.round(height
                        * fraction / 3), false);
            } else {
                drawBranch(width + changes[(levelsCount - 1 - i) * 2 - 2] - lastChange - thick - 2, height, spread, thick, thick / 2, levels[i] + Math.round
                        (height * fraction / 3), false);
            }
            left = !left;
        }
    }

    private void drawBranch(int x, int height, float spread, int widthBase, int widthTop, int yShift, boolean top) {
        int length = height / 2 + random.randomInRange(0, height / 25);
        int deviation = Math.round(spread * (height / 3 + random.randomInRange(0, height / 6)));
        int change = -8 + random.next(4);
        int xPosition = x + deviation / 2 + change;
        if (top) {
            Drawer.drawTextureQuad(x, 0, x + widthBase, 0, xPosition + (widthTop + widthBase) / 2, -length / 2, xPosition, -length / 2);
        } else {
            if (deviation < 0) {
                Drawer.drawTextureQuad(x + widthBase / 3, (int) (widthBase * 1.5f), x + widthBase / 3, 0, xPosition + (widthTop
                        + widthBase) / 2, -length / 2, xPosition, -length / 2);
            } else {
                Drawer.drawTextureQuad(x + 2 * widthBase / 3, 0, x + 2 * widthBase / 3, (int) (widthBase * 1.5f), xPosition
                        + (widthTop + widthBase) / 2, -length / 2, xPosition, -length / 2);
            }
        }
        Drawer.drawTextureQuad(xPosition,
                -length / 2, xPosition + (widthTop + widthBase) / 2, -length / 2, x + deviation + widthTop, -length, x + deviation, -length);
//         End of branch
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
            points.add(new Point(x + Math.round(1.5f * xPosition), Math.round(-0.95f * length) - change1 + yShift));
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
        int dif = height / 40;
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
        if (Math.abs(points.get(i).getY() + y) < leafHeight * 0.66 - leaf.getHeight() / 2
                && (points.get(i).getY() + y < 0 || points.get(i).getY() + y < leafHeight * 0.33f - leaf.getHeight() / 2)
                && Math.abs(points.get(i).getX() + x + leafXShift) < fbo.getWidth() / 2 - partShift - leaf.getHeight()) {
            float change = Math.abs(points.get(i).getY() + minY + y) / (maxY - minY);
            change -= Math.abs(points.get(i).getX() + x) / maxX / 4;
            if (change < 0) {
                change = 0;
            }
            int rand = random.randomInRange(-10, 10);
            Drawer.setColorStatic(new Color(leafColor.r * (1 + change / 2f + rand / 20f), leafColor.g * (1 + change / 2f + rand / 75f),
                    leafColor.b * (1 + change / 2f + rand / 25f)));
            float angle = 90f * (points.get(i).getX() + x + random.randomInRange(-10, 10)) / maxX;

            int xShift = partShift;
            if (points.get(i).getX() + leafXShift < points.get(points.size() - 1).getX()) {
                xShift = -partShift;
            }
            Drawer.regularShader.rotateTranslate(points.get(i).getX() + x + leafXShift + xShift, points.get(i).getY() + y - leafHeight - height, angle);
            Drawer.regularShader.scaleNoReset(1f + random.randomInRange(-50, 50) / 1000f, 1f + random.randomInRange(-50, 50) / 1000f);
            leaf.render();
        }
    }

    @Override
    public void renderShadowLit(Figure figure) {
        if (appearance != null) {
            if (map.getWindStrength() > 2) {
                Drawer.drawShapeBottomLitFromVBO(fbo, vbo, getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20 - woodHeight + collision
                        .getHeightHalf());
                if (!leafless) {
                    Drawer.drawShapeTopBlackFromVbo(fbo, vbo, ShadowRenderer.maxDarkness, getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20
                            - woodHeight + collision.getHeightHalf() - fbo.getHeight() + leafHeight);
                }
            } else {
                Drawer.drawShapeBottomLit(fbo, getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20 - woodHeight + collision.getHeightHalf());
                if (!leafless) {
                    Drawer.drawShapeTopBlack(fbo, ShadowRenderer.maxDarkness, getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20 - woodHeight
                            + collision.getHeightHalf() - fbo.getHeight() + leafHeight);
                }
            }
        }
    }

    @Override
    public void renderShadow(Figure figure) {
        if (appearance != null && fbo != null && vbo != null) {
            if (map.getWindStrength() > 2) {
                Drawer.drawShapeBottomBlackFromVbo(fbo, vbo, collision.getDarkValue(), getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20
                        - woodHeight + collision.getHeightHalf());
                if (!leafless) {
                    Drawer.drawShapeTopBlackFromVbo(fbo, vbo, ShadowRenderer.maxDarkness, getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20
                            - woodHeight + collision.getHeightHalf() - fbo.getHeight() + leafHeight);
                }
            } else {
                Drawer.drawShapeBottomBlack(fbo, collision.getDarkValue(), getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20 - woodHeight
                        + collision.getHeightHalf());
                if (!leafless) {
                    Drawer.drawShapeTopBlack(fbo, ShadowRenderer.maxDarkness, getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20 - woodHeight
                            + collision.getHeightHalf() - fbo.getHeight() + leafHeight);
                }
            }
        }
    }

    @Override
    public void renderShadowLit(int xStart, int xEnd) {
        if (appearance != null) {
            if (xStart > xEnd) {
                int temp = xStart;
                xStart = xEnd;
                xEnd = temp;
            }
            if (xStart < 0) {
                xStart = 0;
            }
            if (xEnd > 2 * collision.getWidth()) {
                xEnd = 2 * collision.getWidth();
            }
            if (map.getWindStrength() > 2) {
                Drawer.drawShapeBottomPartLitFromVbo(fbo, vbo, getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20 - woodHeight + collision
                        .getHeightHalf(), fbo.getWidth() / 2 - collision.getWidth() / 2 + xStart, fbo.getWidth() / 2 - collision.getWidth() / 2 + xEnd);
                if (!leafless) {
                    Drawer.drawShapeTopBlackFromVbo(fbo, vbo, ShadowRenderer.maxDarkness, getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20
                            - woodHeight + collision.getHeightHalf() - fbo.getHeight() + leafHeight);
                }
            } else {
                Drawer.drawShapeBottomPartLit(fbo, getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20 - woodHeight + collision.getHeightHalf
                        (), fbo.getWidth() / 2 - collision.getWidth() / 2 + xStart, fbo.getWidth() / 2 - collision.getWidth() / 2 + xEnd);
                if (!leafless) {
                    Drawer.drawShapeTopBlack(fbo, ShadowRenderer.maxDarkness, getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20 - woodHeight
                            + collision.getHeightHalf() - fbo.getHeight() + leafHeight);
                }
            }
        }
    }

    @Override
    public void renderShadow(int xStart, int xEnd) {
        if (appearance != null) {
            if (xStart > xEnd) {
                int temp = xStart;
                xStart = xEnd;
                xEnd = temp;
            }
            if (xStart < 0) {
                xStart = 0;
            }
            if (xEnd > 2 * collision.getWidth()) {
                xEnd = 2 * collision.getWidth();
            }
            if (map.getWindStrength() > 2) {
                Drawer.drawShapeBottomPartBlackFromVbo(fbo, vbo, collision.getDarkValue(), getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20
                        - woodHeight + collision.getHeightHalf(), fbo.getWidth() / 2 - collision.getWidth() / 2 + xStart, fbo.getWidth() / 2 - collision
                        .getWidth() / 2 + xEnd);
                if (!leafless) {
                    Drawer.drawShapeTopBlackFromVbo(fbo, vbo, ShadowRenderer.maxDarkness, getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20
                            - woodHeight + collision.getHeightHalf() - fbo.getHeight() + leafHeight);
                }
            } else {
                Drawer.drawShapeBottomPartBlack(fbo, collision.getDarkValue(), getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20
                        - woodHeight + collision.getHeightHalf(), fbo.getWidth() / 2 - collision.getWidth() / 2 + xStart, fbo.getWidth() / 2 - collision
                        .getWidth() / 2 + xEnd);
                if (!leafless) {
                    Drawer.drawShapeTopBlack(fbo, ShadowRenderer.maxDarkness, getX() - fbo.getWidth() / 2 - collision.getWidthHalf(), getY() + 20 - woodHeight
                            + collision.getHeightHalf() - fbo.getHeight() + leafHeight);
                }
            }
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
