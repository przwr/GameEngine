package game.place.fbo;

import game.Settings;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.EXTFramebufferObject;
import static org.lwjgl.opengl.GL11.glGenTextures;
import org.lwjgl.opengl.GL30;

public class MultisampleFrameBufferObject extends FrameBufferObject {

    private int frameBufferObjectMultiSample;
    private final int multiSampleTexture;

    public MultisampleFrameBufferObject(int width, int height) {
        super(width, height, true);
        multiSampleTexture = glGenTextures();
        createFrameBufferObjects();
        type.makeMultiSample(Settings.samplesCount, multiSampleTexture, width, height, frameBufferObjectMultiSample);
        type.makeTexture(texture, frameBufferObject, width, height);
        type.deactivate();
    }

    private void createFrameBufferObjects() {
        if (version == NATIVE) {
            frameBufferObject = GL30.glGenFramebuffers();
            frameBufferObjectMultiSample = GL30.glGenFramebuffers();
        } else if (version == ARB) {
            frameBufferObject = ARBFramebufferObject.glGenFramebuffers();
            frameBufferObjectMultiSample = ARBFramebufferObject.glGenFramebuffers();
        } else {
            frameBufferObject = EXTFramebufferObject.glGenFramebuffersEXT();
            frameBufferObjectMultiSample = EXTFramebufferObject.glGenFramebuffersEXT();
        }
    }

    @Override
    public void activate() {
        type.activate((version == EXT) ? frameBufferObject : frameBufferObjectMultiSample);
    }

    @Override
    public void deactivate() {
        type.deactivate(frameBufferObjectMultiSample, frameBufferObject, width, height);
    }
}
