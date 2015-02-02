package game.place;

import game.Settings;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
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
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glScissor;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glViewport;
import org.lwjgl.opengl.GL30;

public class RegularFrameBufferObject extends FrameBufferObject {

    public RegularFrameBufferObject(int w, int h, Settings settings) {
        super(w, h, settings);
        activates[0] = new activateType() {
            @Override
            public void activate() {
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBufferObject);
            }
        };
        activates[1] = new activateType() {
            @Override
            public void activate() {
                ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, frameBufferObject);
            }
        };
        activates[2] = new activateType() {
            @Override
            public void activate() {
                EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, frameBufferObject);
            }
        };
        deactivates[0] = new deactivateType() {
            @Override
            public void deactivate() {
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
            }
        };
        deactivates[1] = new deactivateType() {
            @Override
            public void deactivate() {
                ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, 0);
            }
        };
        deactivates[2] = new deactivateType() {
            @Override
            public void deactivate() {
                EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
            }
        };
        makeTextures[0] = new makeTextureType() {

            @Override
            public void makeTexture() {
                GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);
            }
        };
        makeTextures[1] = new makeTextureType() {

            @Override
            public void makeTexture() {
                ARBFramebufferObject.glFramebufferTexture2D(ARBFramebufferObject.GL_FRAMEBUFFER, ARBFramebufferObject.GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);
            }
        };
        makeTextures[2] = new makeTextureType() {

            @Override
            public void makeTexture() {
                EXTFramebufferObject.glFramebufferTexture2DEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, texture, 0);
            }
        };
        activates[version].activate();
        makeTexture();
        deactivates[version].deactivate();
    }

   //double scale = 2;

    @Override
    public void activate() {
       // glViewport(0, -Display.getHeight(), 2 * Display.getWidth(), 2 * Display.getHeight());
       // glOrtho(-scale, scale, -scale, scale, 1.0, -1.0);
        activates[version].activate();
    }

    @Override
    public void deactivate() {
       // glOrtho(-1 / scale, 1 / scale, -1 / scale, 1 / scale, 1.0, -1.0);
        //glOrtho(-0.5, 0.5, -0.5, 0.5, 1.0, -1.0);
       // glViewport(0, 0, Display.getWidth(), Display.getHeight());
        deactivates[version].deactivate();
    }

    private void makeTexture() {
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_BYTE, BufferUtils.createByteBuffer(4 * width * height));
        makeTextures[version].makeTexture();
    }

}
