package game.place;

import java.nio.ByteBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL11.*;

public class FBORenderer {

    private final int fbo;
    private final int texture;
    private final int height;
    private final int width;
    private final ByteBuffer byteBuffer;

    public FBORenderer(int w, int h, int texture) {
        fbo = glGenFramebuffers();
        this.texture = texture;
        width = w;
        height = h;

        activate();

        byteBuffer = BufferUtils.createByteBuffer(4 * width * height);

        glBindTexture(GL_TEXTURE_2D, texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_BYTE, byteBuffer);
        glFramebufferTexture2D(GL_DRAW_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);

        deactivate();
    }

    public final void activate() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fbo);
    }

    public final void deactivate() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
    }

    public int getTexture() {
        return texture;
    }
}
