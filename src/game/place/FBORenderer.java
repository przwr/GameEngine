package game.place;

import java.nio.ByteBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;

public class FBORenderer {

    private final int fbo;
    private final int texture;
    private final int height;
    private final int width;
    private final ByteBuffer byteBuffer;

    public FBORenderer(int w, int h, int texture) {
        fbo = GL30.glGenFramebuffers();
        this.texture = texture;
        width = w;
        height = h;

        activate();

        byteBuffer = BufferUtils.createByteBuffer(4 * width * height);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_BYTE, byteBuffer);

        glFramebufferTexture2D(GL_DRAW_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, texture, 0);

        deactivate();
    }

    public void activate() {

        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fbo);
    }

    public void deactivate() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
    }

    public int getTexture() {
        return texture;
    }
}
