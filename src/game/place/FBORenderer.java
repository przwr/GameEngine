package game.place;

import game.Settings;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.EXTFramebufferObject;
import static org.lwjgl.opengl.GL11.glGenTextures;
import org.lwjgl.opengl.GL30;

public abstract class FBORenderer {

    protected final int height;
    protected final int width;
    protected final int texture;
    protected final int fbo;
    protected final int fboVer;
    protected final boolean fboMSSup;
    protected final activate[] activates;
    protected final deactivate[] deactivates;
    protected final makeTexture[] makeTextures;

    public FBORenderer(int w, int h, Settings settings) {
        width = w;
        height = h;
        texture = glGenTextures();
        if (settings.isSupfboVer3 == 0) {
            fbo = GL30.glGenFramebuffers();
            fboVer = 0;
        } else if (settings.isSupfboVer3 == 1) {
            fbo = ARBFramebufferObject.glGenFramebuffers();
            fboVer = 1;
        } else {
            fbo = EXTFramebufferObject.glGenFramebuffersEXT();
            fboVer = 2;
        }
        fboMSSup = settings.isSupfboMS;
        
        activates = new activate[3];
        deactivates = new deactivate[3];
        makeTextures = new makeTexture[3];
    }

    public abstract void activate();

    public abstract void deactivate();

    public int getTexture() {
        return texture;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    protected interface activate {

        void activate();
    }

    protected interface makeTexture {

        void makeTexture();
    }

    protected interface deactivate {

        void deactivate();
    }
}
