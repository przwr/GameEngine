package game.place;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL11.*;

public abstract class FBORenderer {

    protected final int height;
    protected final int width;
    protected final int texture;
    protected final int fbo;

    public FBORenderer(int w, int h) {
        width = w;
        height = h;
        texture = glGenTextures();
        fbo = glGenFramebuffers();
    }

    public abstract void activate();

    public abstract void deactivate();

    public int getTexture() {
        return texture;
    }
}
