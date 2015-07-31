/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.fbo;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.opengl.ARBFramebufferObject.*;
import static org.lwjgl.opengl.ARBTextureMultisample.GL_TEXTURE_2D_MULTISAMPLE;
import static org.lwjgl.opengl.ARBTextureMultisample.glTexImage2DMultisample;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author Domi
 */
public class MultiSampleARB implements FrameBufferType {

    @Override
    public void activate(int frameBufferObjectMultiSample) {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, frameBufferObjectMultiSample);
    }

    @Override
    public void deactivate() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
    }

    @Override
    public void deactivate(int frameBufferObjectMultiSample, int frameBufferObject, int width, int height) {
        glBindFramebuffer(GL_READ_FRAMEBUFFER, frameBufferObjectMultiSample);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, frameBufferObject);
        glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_COLOR_BUFFER_BIT, GL_NEAREST);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
    }

    @Override
    public void makeTexture(int texture, int frameBufferObject, int width, int height) {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, frameBufferObject);
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_BYTE, createByteBuffer(4 * width * height));
        glFramebufferTexture2D(GL_DRAW_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);
    }

    @Override
    public void makeMultiSample(int samplesCount, int multiSampleTexture, int width, int height, int frameBufferObjectMultiSample) {
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, multiSampleTexture);
        glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, samplesCount, GL_RGBA8, width, height, false);
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferObjectMultiSample);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D_MULTISAMPLE, multiSampleTexture, 0);
    }
}
