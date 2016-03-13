/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites.fbo;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author Domi
 */
public class RegularEXT implements FrameBufferType {

    @Override
    public void activate(int frameBufferObject) {
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, frameBufferObject);
    }

    @Override
    public void deactivate() {
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
    }

    @Override
    public void deactivate(int frameBufferObjectMultiSample, int frameBufferObject, int width, int height) {
    }

    @Override
    public void makeTexture(int texture, int frameBufferObject, int width, int height) {
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, frameBufferObject);
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_BYTE, createByteBuffer(4 * width * height));
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, texture, 0);
    }

    @Override
    public void makeMultiSample(int samplesCount, int multiSampleTexture, int width, int height, int frameBufferObjectMultiSample) {
    }
}
