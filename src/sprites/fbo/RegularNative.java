/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites.fbo;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author Domi
 */
public class RegularNative implements FrameBufferType {

    @Override
    public void activate(int frameBufferObject) {
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferObject);
    }

    @Override
    public void deactivate() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    public void deactivate(int frameBufferObjectMultiSample, int frameBufferObject, int width, int height) {
    }

    @Override
    public void makeTexture(int texture, int frameBufferObject, int width, int height) {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, frameBufferObject);
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_BYTE, BufferUtils.createByteBuffer(4 * width * height));
        glFramebufferTexture2D(GL_DRAW_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);
    }

    @Override
    public void makeMultiSample(int samplesCount, int multiSampleTexture, int width, int height, int frameBufferObjectMultiSample) {
    }
}
