package game.place;

import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL11.*;

public class FBORendererRegular extends FBORenderer {

    public FBORendererRegular(int w, int h) {
        super(w, h);
        activate();
        makeTexture();
        deactivate();
    }

    @Override
    public final void activate() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fbo);
    }

    @Override
    public final void deactivate() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
    }

    private void makeTexture() {
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_BYTE, BufferUtils.createByteBuffer(4 * width * height));
        glFramebufferTexture2D(GL_DRAW_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);
    }
}
