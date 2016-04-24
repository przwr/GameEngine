package sprites.fbo;

import engine.utilities.Drawer;
import engine.utilities.Methods;
import game.Settings;
import game.gameobject.GameObject;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL30;
import sprites.Appearance;
import sprites.vbo.VertexBufferObject;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public abstract class FrameBufferObject implements Appearance {

    public static final int NATIVE = 0, ARB = 1, EXT = 2;
    private static final FrameBufferType REGULAR_NATIVE = new RegularNative();
    private static final FrameBufferType REGULAR_ARB = new RegularARB();
    private static final FrameBufferType REGULAR_EXT = new RegularEXT();
    private static final FrameBufferType MULTI_SAMPLE_NATIVE = new MultiSampleNative();
    private static final FrameBufferType MULTI_SAMPLE_ARB = new MultiSampleARB();
    private static final FrameBufferType MULTI_SAMPLE_EXT = new MultiSampleEXT();

    private static float lastScreenData[] = new float[10];
    private static float checkSum = 0;
    private static List<FrameBufferObject> instances = new ArrayList<>();
    final FrameBufferType type;
    final int height;
    final int width;
    final int texture;
    public boolean generated;
    float heightSlice, heightShift;
    int frameBufferObject;
    int version;
    private VertexBufferObject vbo;
    private int partShift;

    FrameBufferObject(int width, int height, boolean multiSample) {
        this.width = width;
        this.height = height;
        texture = glGenTextures();
        version = Settings.supportedFrameBufferObjectVersion;
        if (multiSample && Settings.multiSampleSupported) {
            if (version == NATIVE) {
                type = MULTI_SAMPLE_NATIVE;
            } else if (version == ARB) {
                type = MULTI_SAMPLE_ARB;
            } else {
                type = MULTI_SAMPLE_EXT;
                version = EXT;
            }
        } else {
            if (version == NATIVE) {
                type = REGULAR_NATIVE;
            } else if (version == ARB) {
                type = REGULAR_ARB;
            } else {
                type = REGULAR_EXT;
                version = EXT;
            }
        }
        instances.add(this);
    }

    public static void cleanUp() {
        for (FrameBufferObject fbo : instances) {
            fbo.clear();
        }
        instances.clear();
        checkSum = -1;
    }

    public void setHeightSlice(float heightSlice) {
        this.heightSlice = heightSlice;
        if (vbo != null) {
            vbo.clear();
            vbo = null;
        }
    }

    protected void initializeBuffers() {
        if (heightSlice != 0) {
            float[] vertices = {
                    0, 0,
                    0, heightSlice,
                    width, heightSlice,
                    width, 0,

                    partShift, heightShift + heightSlice,
                    partShift, heightShift + height,
                    partShift + width / 2, heightShift + height,
                    partShift + width / 2, heightShift + heightSlice,

                    -partShift + width / 2, heightShift + heightSlice,
                    -partShift + width / 2, heightShift + height,
                    -partShift + width, heightShift + height,
                    -partShift + width, heightShift + heightSlice,
                    0, 0,
                    0, heightSlice,
                    width, heightSlice,
                    width, 0,

                    -partShift + width / 2, heightShift + heightSlice,
                    -partShift + width / 2, heightShift + height,
                    -partShift + width, heightShift + height,
                    -partShift + width, heightShift + heightSlice,

                    partShift, heightShift + heightSlice,
                    partShift, heightShift + height,
                    partShift + width / 2, heightShift + height,
                    partShift + width / 2, heightShift + heightSlice,
            };
            float[] textureCoordinates = {
                    0, heightSlice / height,         //Dół i góra
                    0, 0,
                    1f, 0,
                    1f, heightSlice / height,

                    0, 1f,
                    0, heightSlice / height,
                    0.5f, heightSlice / height,
                    0.5f, 1f,

                    0.5f, 1f,
                    0.5f, heightSlice / height,
                    1f, heightSlice / height,
                    1f, 1f,

                    0, heightSlice / height,         //Dół i góra
                    0, 0,
                    1f, 0,
                    1f, heightSlice / height,

                    0.5f, 1f,
                    0.5f, heightSlice / height,
                    1f, heightSlice / height,
                    1f, 1f,

                    0, 1f,
                    0, heightSlice / height,
                    0.5f, heightSlice / height,
                    0.5f, 1f,
            };
            int[] indices = {0, 1, 3, 2, 3, 1, 4, 5, 7, 6, 7, 5, 8, 9, 11, 10, 11, 9, 12, 13, 15, 14, 15, 13,
                    16, 17, 19, 18, 19, 17, 20, 21, 23, 22, 23, 21,
            };
            vbo = VertexBufferObject.create(vertices, textureCoordinates, indices);
        } else {
            float[] vertices = {
                    0, 0,
                    0, 0 + height,
                    0 + width, 0 + height,
                    0 + width, 0
            };
            float[] textureCoordinates = {
                    0, 1f,                           //Całość
                    0, 0,
                    1f, 0,
                    1f, 1f
            };
            int[] indices = {0, 1, 3, 2};
            vbo = VertexBufferObject.create(vertices, textureCoordinates, indices);
        }
    }

    public void clear() {
        for (int texture : getTextures()) {
            glDeleteTextures(texture);
        }
        for (int buffer : getBuffers()) {
            if (version == NATIVE) {
                GL30.glDeleteFramebuffers(buffer);
            } else if (version == ARB) {
                ARBFramebufferObject.glDeleteFramebuffers(frameBufferObject);
            } else {
                EXTFramebufferObject.glDeleteFramebuffersEXT(frameBufferObject);
            }
        }
    }

    public void delete() {
        instances.remove(this);
    }

    public abstract void activate();

    public abstract void deactivate();

    public void render() {
        render(0);
    }

    private void render(int type) {
        bindCheck();
        vbo.renderTextured(type * 4, 4);
    }

    public void renderTopAndBottom(boolean order) {
        bindCheck();
        vbo.renderTexturedTriangles(order ? 0 : 18, 18);
    }

    public void renderShadow(float color) {
        renderShadow(0, color);
    }

    public void renderShadowBottom(float color) {
        bindCheck();
        Drawer.shadowShader.loadColorModifier(color);
        vbo.renderTextured(0, 4);
    }

    public void renderShadowBottomFromVbo(float color, VertexBufferObject vbo) {
        bindCheck();
        Drawer.shadowShader.loadColorModifier(color);
        vbo.renderTexturedTriangles(0, 12);
    }

    public void renderShadowTop(float color) {
        bindCheck();
        Drawer.shadowShader.translateNoReset(0, -heightShift);
        Drawer.shadowShader.loadColorModifier(color);
        vbo.renderTexturedTriangles(6, 12);
        Drawer.shadowShader.translateNoReset(0, heightShift);
    }

    public void renderShadowTopFromVbo(float color, VertexBufferObject vbo) {
        bindCheck();
        Drawer.shadowShader.translateNoReset(0, -heightShift);
        Drawer.shadowShader.loadColorModifier(color);
        vbo.renderTexturedTriangles(12, vbo.getVertexCount() - 12);
        Drawer.shadowShader.translateNoReset(0, heightShift);
    }

    public void renderShadowFromVbo(float color, VertexBufferObject vbo) {
        bindCheck();
        Drawer.shadowShader.translateNoReset(0, -heightShift);
        Drawer.shadowShader.loadColorModifier(color);
        vbo.renderTexturedTriangles(0, vbo.getVertexCount());
        Drawer.shadowShader.translateNoReset(0, heightShift);
    }

    public void renderShadowPartFromVbo(int partXStart, int partXEnd, float color, VertexBufferObject vbo) {
        if (bindCheck()) {
            if (partXStart > partXEnd) {
                int temp = partXStart;
                partXStart = partXEnd;
                partXEnd = temp;
            }
            if (partXEnd > width) {
                partXEnd = width;
            }
            if (partXStart < 0) {
                partXStart = 0;
            }
            Drawer.shadowShader.loadColorModifier(color);
            vectorModifier.set(partXStart, partXEnd - width, partXStart / (float) width, (partXEnd - width) / (float) width);
            Drawer.shadowShader.loadSizeModifier(vectorModifier);
            vbo.renderTextured(0, vbo.getVertexCount());
            Drawer.shadowShader.loadSizeModifier(ZERO_VECTOR);
        }
    }

    public void renderShadow(int type, float color) {
        bindCheck();
        Drawer.shadowShader.loadColorModifier(color);
        vbo.renderTextured(type * 4, 4);
    }


    public void renderBottom() {
        render(0);
    }

    public void renderBottomPart(int partXStart, int partXEnd) {
        renderPart(0, partXStart, partXEnd);
    }

    public void renderShadowBottomPart(int partXStart, int partXEnd, float color) {
        renderShadowPart(0, partXStart, partXEnd, color);
    }

    public void renderShadowBottomPartFromVbo(int partXStart, int partXEnd, float color, VertexBufferObject vbo) {
        if (bindCheck()) {
            if (partXStart > partXEnd) {
                int temp = partXStart;
                partXStart = partXEnd;
                partXEnd = temp;
            }
            if (partXEnd > width) {
                partXEnd = width;
            }
            if (partXStart < 0) {
                partXStart = 0;
            }
            Drawer.shadowShader.loadColorModifier(color);
            vectorModifier.set(partXStart, partXEnd - width, partXStart / (float) width, (partXEnd - width) / (float) width);
            Drawer.shadowShader.loadSizeModifier(vectorModifier);
            vbo.renderTextured(0, 12);
            Drawer.shadowShader.loadSizeModifier(ZERO_VECTOR);
        }
    }


    public void renderShadowPart(int type, int partXStart, int partXEnd, float color) {
        if (bindCheck()) {
            if (partXStart > partXEnd) {
                int temp = partXStart;
                partXStart = partXEnd;
                partXEnd = temp;
            }
            if (partXEnd > width) {
                partXEnd = width;
            }
            if (partXStart < 0) {
                partXStart = 0;
            }
            Drawer.shadowShader.loadColorModifier(color);
            vectorModifier.set(partXStart, partXEnd - width, partXStart / (float) width, (partXEnd - width) / (float) width);
            Drawer.shadowShader.loadSizeModifier(vectorModifier);
            vbo.renderTextured(type * 4, 4);
            Drawer.shadowShader.loadSizeModifier(ZERO_VECTOR);
        }
    }

    public void renderScreenPart(float displayWidth, float displayHeight, float xStart, float yStart, float xEnd, float yEnd, float xTStart, float yTStart,
                                 float xTEnd, float yTEnd) {
        if (areDifferent(displayWidth, displayHeight, xStart, yStart, xEnd, yEnd, xTStart, yTStart, xTEnd, yTEnd)) {
            float[] vertices = {
                    xStart * displayWidth, yStart * displayHeight,
                    xStart * displayWidth, yEnd * displayHeight,
                    xEnd * displayWidth, yEnd * displayHeight,
                    xEnd * displayWidth, yStart * displayHeight
            };
            float[] textureCoordinates = {
                    xTStart, yTEnd,
                    xTStart, yTStart,
                    xTEnd, yTStart,
                    xTEnd, yTEnd
            };
            Drawer.screenVBO.updateVerticesAndTextureCoords(vertices, textureCoordinates);
        }
        bindCheck();
        Drawer.shadowShader.resetTransformationMatrix();
        Drawer.screenVBO.renderTextured(0, 4);
    }


    private boolean areDifferent(float displayWidth, float displayHeight, float xStart, float yStart, float xEnd, float yEnd, float xTStart, float yTStart,
                                 float xTEnd, float yTEnd) {
        float curSum = displayWidth * 2 * displayHeight + xStart * 77 + yStart * 33 + xEnd * 22 + yEnd * 11 + xTStart * 5 + yTStart * 3 + xTEnd * 2 + yTEnd;
        if (curSum != checkSum) {
            checkSum = curSum;
            lastScreenData[0] = displayWidth;
            lastScreenData[1] = displayHeight;
            lastScreenData[2] = xStart;
            lastScreenData[3] = yStart;
            lastScreenData[4] = xEnd;
            lastScreenData[5] = yEnd;
            lastScreenData[6] = xTStart;
            lastScreenData[7] = yTStart;
            lastScreenData[8] = xTEnd;
            lastScreenData[9] = yTEnd;
            return true;
        }
        return false;
    }

    public int getTexture() {
        return texture;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    @Override
    public boolean bindCheck() {
        if (vbo == null) {
            initializeBuffers();
        }
        if (glGetInteger(GL_TEXTURE_BINDING_2D) != texture) {
            glBindTexture(GL_TEXTURE_2D, texture);
        }
        return true;
    }

    public void renderPart(int type, int partXStart, int partXEnd) {
        if (bindCheck()) {
            if (partXStart > partXEnd) {
                int temp = partXStart;
                partXStart = partXEnd;
                partXEnd = temp;
            }
            if (partXEnd > width) {
                partXEnd = width;
            }
            if (partXStart < 0) {
                partXEnd = 0;
            }
            vectorModifier.set(partXStart, partXEnd - width, partXStart / (float) width, (partXEnd - width) / (float) width);
            Drawer.regularShader.loadSizeModifier(vectorModifier);
            vbo.renderTextured(type * 4, 4);
            Drawer.regularShader.loadSizeModifier(ZERO_VECTOR);
        }
    }

    @Override
    public void renderPart(int partXStart, int partXEnd) {
        renderPart(0, partXStart, partXEnd);
    }


    @Override
    public void renderShadowPart(int partXStart, int partXEnd, float color) {
        renderShadowPart(0, partXStart, partXEnd, color);
    }

    @Override
    public void updateTexture(GameObject owner) {
    }

    @Override
    public void renderStaticShadow(GameObject object) {
    }

    public void renderStaticShadowTopAndBottom(GameObject object, int x, int y) {
        float changeX = x + (float) (object.getFloatHeight());
        float changeY = y;
        float scale = (float) Methods.ONE_BY_SQRT_ROOT_OF_2;
        Drawer.regularShader.scaleNoReset(1f, scale);
        Drawer.regularShader.translateNoReset(changeX, changeY);
        Drawer.regularShader.rotateNoReset(90);
        renderTopAndBottom(true);
        Drawer.regularShader.rotateNoReset(-90);
        Drawer.regularShader.translateNoReset(-changeX, -changeY);
        Drawer.regularShader.scaleNoReset(1f, 1f / scale);
    }

    public void renderStaticShadowBottom(GameObject object, int x, int y) {
        float changeX = x + (float) (object.getFloatHeight());
        float changeY = y;
        float scale = (float) Methods.ONE_BY_SQRT_ROOT_OF_2;
        Drawer.regularShader.scaleNoReset(1f, scale);
        Drawer.regularShader.translateNoReset(changeX, changeY);
        Drawer.regularShader.rotateNoReset(90);
        renderBottom();
        Drawer.regularShader.rotateNoReset(-90);
        Drawer.regularShader.translateNoReset(-changeX, -changeY);
        Drawer.regularShader.scaleNoReset(1f, 1f / scale);
    }

    public void renderStaticShadowFromVBO(VertexBufferObject vbo, GameObject object, int x, int y) {
        float changeX = x + (float) (object.getFloatHeight());
        float changeY = y;
        float scale = (float) Methods.ONE_BY_SQRT_ROOT_OF_2;
        Drawer.regularShader.scaleNoReset(1f, scale);
        Drawer.regularShader.translateNoReset(changeX, changeY);
        Drawer.regularShader.rotateNoReset(90);
        bindCheck();
        vbo.renderTexturedTriangles(0, vbo.getVertexCount());
        Drawer.regularShader.rotateNoReset(-90);
        Drawer.regularShader.translateNoReset(-changeX, -changeY);
        Drawer.regularShader.scaleNoReset(1f, 1f / scale);
    }

    public void renderStaticShadow(GameObject object, int x, int y) {
        float changeX = x + (float) (object.getFloatHeight());
        float changeY = y;
        float scale = (float) Methods.ONE_BY_SQRT_ROOT_OF_2;

        Drawer.regularShader.scaleNoReset(1f, scale);
        Drawer.regularShader.translateNoReset(changeX, changeY);
        Drawer.regularShader.rotateNoReset(90);
        render();
        Drawer.regularShader.rotateNoReset(-90);
        Drawer.regularShader.translateNoReset(-changeX, -changeY);
        Drawer.regularShader.scaleNoReset(1f, 1f / scale);
    }

    @Override
    public void updateFrame() {
    }

    @Override
    public int getCurrentFrameIndex() {
        return 0;
    }

    @Override
    public int getXStart() {
        return 0;
    }

    @Override
    public int getYStart() {
        return 0;
    }

    @Override
    public int getActualWidth() {
        return width;
    }

    @Override
    public int getActualHeight() {
        return height;
    }

    @Override
    public int getXOffset() {
        return 0;
    }

    @Override
    public int getYOffset() {
        return 0;
    }

    protected int[] getBuffers() {
        int[] buffers = {frameBufferObject};
        return buffers;
    }

    protected int[] getTextures() {
        int[] textures = {texture};
        return textures;
    }

    public void setHeightShift(float heightShift) {
        this.heightShift = heightShift;
    }

    public void setPartShift(int partShift) {
        this.partShift = partShift;
    }
}
