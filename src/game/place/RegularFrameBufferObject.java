package game.place;

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

public class RegularFrameBufferObject extends FrameBufferObject {

    public RegularFrameBufferObject(int width, int height) {
        super(width, height);
        activates[NATIVE] = () -> {
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBufferObject);
        };
        activates[ARB] = () -> {
            ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, frameBufferObject);
        };
        activates[EXT] = () -> {
            EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, frameBufferObject);
        };
        deactivates[NATIVE] = () -> {
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        };
        deactivates[ARB] = () -> {
            ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, 0);
        };
        deactivates[EXT] = () -> {
            EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
        };
        makeTextures[NATIVE] = () -> {
            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);
        };
        makeTextures[ARB] = () -> {
            ARBFramebufferObject.glFramebufferTexture2D(ARBFramebufferObject.GL_FRAMEBUFFER, ARBFramebufferObject.GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);
        };
        makeTextures[EXT] = () -> {
            EXTFramebufferObject.glFramebufferTexture2DEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, texture, 0);
        };
        activates[version].activate();
        makeTexture();
        deactivates[version].deactivate();
    }

    @Override
    public void activate() {
        activates[version].activate();
    }

    @Override
    public void deactivate() {
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
