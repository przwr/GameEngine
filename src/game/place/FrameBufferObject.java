package game.place;

import game.Settings;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.EXTFramebufferObject;
import static org.lwjgl.opengl.GL11.glGenTextures;
import org.lwjgl.opengl.GL30;

public abstract class FrameBufferObject {

    private static final int NATIVE = 0, ARB = 1, EXT = 2;
    protected int height, width, texture, frameBufferObject, version;
    protected final boolean multiSampleSupported;
    protected final activateType[] activates;
    protected final deactivateType[] deactivates;
    protected final makeTextureType[] makeTextures;

    public abstract void activate();

    public abstract void deactivate();

    public FrameBufferObject(int width, int height, Settings settings) {
        this.width = width;
        this.height = height;
        texture = glGenTextures();
        switch (settings.supportedFrameBufferObjectVersion) {
            case NATIVE:
                frameBufferObject = GL30.glGenFramebuffers();
                version = NATIVE;
            case ARB:
                frameBufferObject = ARBFramebufferObject.glGenFramebuffers();
                version = ARB;
            default:
                frameBufferObject = EXTFramebufferObject.glGenFramebuffersEXT();
                version = EXT;
        }
        multiSampleSupported = settings.multiSampleSupported;
        activates = new activateType[3];
        deactivates = new deactivateType[3];
        makeTextures = new makeTextureType[3];
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
