package game.place;

import game.Settings;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.EXTFramebufferObject;
import static org.lwjgl.opengl.GL11.GL_BYTE;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import org.lwjgl.opengl.GL30;

public class FBORendererRegular extends FBORenderer {

    public FBORendererRegular(int w, int h, Settings settings) {

        super(w, h, settings);
        activates[0] = new activate() {
            @Override
            public void activate() {
                GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, fbo);
            }
        };
        activates[1] = new activate() {
            @Override
            public void activate() {
                ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, fbo);
            }
        };
        activates[2] = new activate() {
            @Override
            public void activate() {
                EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, fbo);
            }
        };
        deactivates[0] = new deactivate() {
            @Override
            public void deactivate() {
                GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
            }
        };
        deactivates[1] = new deactivate() {
            @Override
            public void deactivate() {
                ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, 0);
            }
        };
        deactivates[2] = new deactivate() {
            @Override
            public void deactivate() {
                EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
            }
        };
        makeTextures[0] = new makeTexture() {

            @Override
            public void makeTexture() {
                GL30.glFramebufferTexture2D(GL30.GL_DRAW_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);
            }
        };
        makeTextures[1] = new makeTexture() {

            @Override
            public void makeTexture() {
                ARBFramebufferObject.glFramebufferTexture2D(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, ARBFramebufferObject.GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);
            }
        };
        makeTextures[2] = new makeTexture() {

            @Override
            public void makeTexture() {
                EXTFramebufferObject.glFramebufferTexture2DEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, texture, 0);
            }
        };
        activates[fboVer].activate();
        makeTexture();
        deactivates[fboVer].deactivate();
    }

    @Override
    public void activate() {
        activates[fboVer].activate();
    }

    @Override
    public void deactivate() {
        deactivates[fboVer].deactivate();
    }

    private void makeTexture() {
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_BYTE, BufferUtils.createByteBuffer(4 * width * height));
        makeTextures[fboVer].makeTexture();
    }

}
