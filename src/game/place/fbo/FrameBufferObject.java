package game.place.fbo;

import game.Settings;

import static org.lwjgl.opengl.GL11.*;

public abstract class FrameBufferObject {

    public static final int NATIVE = 0, ARB = 1, EXT = 2;
    private static final FrameBufferType REGULAR_NATIVE = new RegularNative();
    private static final FrameBufferType REGULAR_ARB = new RegularARB();
    private static final FrameBufferType REGULAR_EXT = new RegularEXT();
    private static final FrameBufferType MULTI_SAMPLE_NATIVE = new MultiSampleNative();
    private static final FrameBufferType MULTI_SAMPLE_ARB = new MultiSampleARB();
    private static final FrameBufferType MULTI_SAMPLE_EXT = new MultiSampleEXT();

    final FrameBufferType type;
    final int height;
    final int width;
    final int texture;
    int frameBufferObject;
    int version;

    FrameBufferObject(int width, int height, boolean multiSample) {
        this.width = width;
        this.height = height;
        texture = glGenTextures();
        version = Settings.supportedFrameBufferObjectVersion;
        if (multiSample && Settings.multiSampleSupported && Settings.samplesCount > 0) {
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
    }

    public abstract void activate();

    public abstract void deactivate();

    public void render() {
        checkBind();
        glBegin(GL_QUADS);
        glTexCoord2f(0, 1);
        glVertex2f(0, 0);
        glTexCoord2f(1, 1);
        glVertex2f(width, 0);
        glTexCoord2f(1, 0);
        glVertex2f(width, height);
        glTexCoord2f(0, 0);
        glVertex2f(0, height);
        glEnd();
    }

    public void renderScreenPart(float displayWidth, float displayHeight, float xStart, float yStart, float xEnd, float yEnd, float xTStart, float yTStart,
                                 float xTEnd, float yTEnd) {
        glPushMatrix();
        checkBind();
        glBegin(GL_QUADS);
        glTexCoord2f(xTStart, yTEnd);
        glVertex2f(xStart * displayWidth, yStart * displayHeight);
        glTexCoord2f(xTEnd, yTEnd);
        glVertex2f(xEnd * displayWidth, yStart * displayHeight);
        glTexCoord2f(xTEnd, yTStart);
        glVertex2f(xEnd * displayWidth, yEnd * displayHeight);
        glTexCoord2f(xTStart, yTStart);
        glVertex2f(xStart * displayWidth, yEnd * displayHeight);
        glEnd();
        glPopMatrix();
    }

    public void renderPiece(int xStart, int yStart, float xBeg, float yBeg, float xEnd, float yEnd) {
        checkBind();
        glTranslatef(xStart, yStart, 0);
        glBegin(GL_QUADS);
        glTexCoord2f(xBeg / width, yBeg / height);
        glVertex2f(xBeg, yEnd);
        glTexCoord2f(xBeg / width, yEnd / height);
        glVertex2f(xBeg, yBeg);
        glTexCoord2f(xEnd / width, yEnd / height);
        glVertex2f(xEnd, yBeg);
        glTexCoord2f(xEnd / width, yBeg / height);
        glVertex2f(xEnd, yEnd);
        glEnd();
        glTranslatef(-xStart, -yStart, 0);
    }

    private void checkBind() {
        if (glGetInteger(GL_TEXTURE_BINDING_2D) != texture) {
            glBindTexture(GL_TEXTURE_2D, texture);
        }
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
}
