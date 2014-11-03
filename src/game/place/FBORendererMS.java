package game.place;

import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.ARBTextureMultisample.GL_TEXTURE_2D_MULTISAMPLE;
import static org.lwjgl.opengl.ARBTextureMultisample.glTexImage2DMultisample;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL11.*;

public class FBORendererMS extends FBORenderer {

    private final int fboMS;

    private final int texture2;

    public FBORendererMS(int w, int h, int nrSamples) {
        super(w, h);
        texture2 = glGenTextures();
        fboMS = glGenFramebuffers();

        makeMultiSample(nrSamples);
        makeTexture();
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
    }

    @Override
    public final void activate() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fboMS);
    }

    @Override
    public final void deactivate() {
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fboMS);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fbo);
        glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_COLOR_BUFFER_BIT, GL_NEAREST);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
    }

    private void makeTexture() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fbo);
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_BYTE, BufferUtils.createByteBuffer(4 * width * height));
        glFramebufferTexture2D(GL_DRAW_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);
    }

    private void makeMultiSample(int nrSamples) {
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, texture2);
        glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, nrSamples, GL_RGBA8, width, height, false);
        glBindFramebuffer(GL_FRAMEBUFFER, fboMS);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D_MULTISAMPLE, texture2, 0);
    }

}
