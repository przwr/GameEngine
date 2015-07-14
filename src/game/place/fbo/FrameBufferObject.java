package game.place.fbo;

import game.Settings;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_BINDING_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;

public abstract class FrameBufferObject {

    public static final int NATIVE = 0, ARB = 1, EXT = 2;
    public static final FrameBufferType REGULAR_NATIVE = new RegularNative();
    public static final FrameBufferType REGULAR_ARB = new RegularARB();
    public static final FrameBufferType REGULAR_EXT = new RegularEXT();
    public static final FrameBufferType MULTISAMPLE_NATIVE = new MultisampleNative();
    public static final FrameBufferType MULTISAMPLE_ARB = new MultisampleARB();
    public static final FrameBufferType MULTISAMPLE_EXT = new MultisampleEXT();

    protected final FrameBufferType type;
    protected int height, width, texture, frameBufferObject, version;

    public abstract void activate();

    public abstract void deactivate();

    public FrameBufferObject(int width, int height, boolean multisample) {
        this.width = width;
        this.height = height;
        texture = glGenTextures();
        version = Settings.supportedFrameBufferObjectVersion;
        if (multisample && Settings.multiSampleSupported && Settings.samplesCount > 0) {
            if (version == NATIVE) {
                type = MULTISAMPLE_NATIVE;
            } else if (version == ARB) {
                type = MULTISAMPLE_ARB;
            } else {
                type = MULTISAMPLE_EXT;
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

    public int getTexture() {
        return texture;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

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

    public void renderScreenPart(float displayWidth, float displayHeight, float xStart, float yStart, float xEnd, float yEnd, float xTStart, float yTStart, float xTEnd, float yTEnd) {
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

    public void renderPiece(int xStart, int yStart,  float xBeg, float yBeg, float xEnd, float yEnd) {
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

}
