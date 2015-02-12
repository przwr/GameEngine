/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.fbo;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.opengl.ARBFramebufferObject.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.ARBFramebufferObject.GL_DRAW_FRAMEBUFFER;
import static org.lwjgl.opengl.ARBFramebufferObject.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.ARBFramebufferObject.GL_READ_FRAMEBUFFER;
import static org.lwjgl.opengl.ARBFramebufferObject.glBindFramebuffer;
import static org.lwjgl.opengl.ARBFramebufferObject.glBlitFramebuffer;
import static org.lwjgl.opengl.ARBFramebufferObject.glFramebufferTexture2D;
import static org.lwjgl.opengl.ARBTextureMultisample.GL_TEXTURE_2D_MULTISAMPLE;
import static org.lwjgl.opengl.ARBTextureMultisample.glTexImage2DMultisample;
import static org.lwjgl.opengl.GL11.GL_BYTE;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

/**
 *
 * @author Domi
 */
public class MultisampleARB implements FrameBufferType {

	@Override
	public void activate(int frameBufferObjectMultisample) {
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, frameBufferObjectMultisample);
	}

	@Override
	public void deactivate() {
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
	}

	@Override
	public void deactivate(int frameBufferObjectMultisample, int frameBufferObject, int width, int height) {
		glBindFramebuffer(GL_READ_FRAMEBUFFER, frameBufferObjectMultisample);
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, frameBufferObject);
		glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_COLOR_BUFFER_BIT, GL_NEAREST);
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
	}

	@Override
	public void makeTexture(int texture, int frameBufferObject, int width, int height) {
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, frameBufferObject);
		glBindTexture(GL_TEXTURE_2D, texture);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
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
