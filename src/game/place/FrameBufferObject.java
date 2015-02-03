package game.place;

import game.Settings;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.EXTFramebufferObject;
import static org.lwjgl.opengl.GL11.glGenTextures;
import org.lwjgl.opengl.GL30;

public abstract class FrameBufferObject {

    public static final int NATIVE = 0, ARB = 1, EXT = 2;
    protected int height, width, texture, frameBufferObject, version;
    protected final boolean multiSampleSupported;
    protected final activateType[] activates = new activateType[3];
    protected final deactivateType[] deactivates = new deactivateType[3];
    protected final makeTextureType[] makeTextures = new makeTextureType[3];

    public abstract void activate();

    public abstract void deactivate();

    public FrameBufferObject(int width, int height) {
        this.width = width;
        this.height = height;
        texture = glGenTextures();
        multiSampleSupported = Settings.multiSampleSupported;
        version = Settings.supportedFrameBufferObjectVersion;
        if (version == NATIVE) {
            frameBufferObject = GL30.glGenFramebuffers();
        } else if (version == ARB) {
            frameBufferObject = ARBFramebufferObject.glGenFramebuffers();
        } else {
            frameBufferObject = EXTFramebufferObject.glGenFramebuffersEXT();
            version = EXT;
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

    protected interface activateType {

        void activate();
    }

    protected interface makeTextureType {

        void makeTexture();
    }

    protected interface deactivateType {

        void deactivate();
    }
}
