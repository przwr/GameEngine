package game.place.fbo;

import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL30;

public class RegularFrameBufferObject extends FrameBufferObject {

    public RegularFrameBufferObject(int width, int height) {
        super(width, height, false);
        createFrameBufferObjects();
        type.activate(frameBufferObject);
        type.makeTexture(texture, frameBufferObject, width, height);
        type.deactivate();
    }

    @Override
    public void activate() {
        type.activate(frameBufferObject);
    }

    @Override
    public void deactivate() {
        type.deactivate();
    }

    private void createFrameBufferObjects() {
        if (version == NATIVE) {
            frameBufferObject = GL30.glGenFramebuffers();
        } else if (version == ARB) {
            frameBufferObject = ARBFramebufferObject.glGenFramebuffers();
        } else {
            frameBufferObject = EXTFramebufferObject.glGenFramebuffersEXT();
        }
    }
}
