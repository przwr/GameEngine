package game.place.fbo;

import game.Settings;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.EXTFramebufferObject;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GL30;

public class MFrameBufferObject extends FrameBufferObject {

	private final int frameBufferObjectMultiSample;
	private final int multiSampleTexture;

	public MFrameBufferObject(int width, int height) {
		super(width, height, true);
		multiSampleTexture = glGenTextures();
		if (version == NATIVE) {
			frameBufferObjectMultiSample = GL30.glGenFramebuffers();
		} else if (version == ARB) {
			frameBufferObjectMultiSample = ARBFramebufferObject.glGenFramebuffers();
		} else {
			frameBufferObjectMultiSample = EXTFramebufferObject.glGenFramebuffersEXT();
		}
		makeMultiSample();
		makeTexture();
		if (version == NATIVE) {
			GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
		} else if (version == ARB) {
			ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, 0);
		} else {
			EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
		}
	}

	@Override
	public void activate() {
		type.activate((version == EXT) ? frameBufferObject : frameBufferObjectMultiSample);
	}

	@Override
	public void deactivate() {
		type.deactivate(frameBufferObjectMultiSample, frameBufferObject, width, height);
	}

	private void makeTexture() {
		type.makeTexture(texture, frameBufferObject, width, height);
	}

	private void makeMultiSample() {
		type.makeMultiSample(Settings.samplesCount, multiSampleTexture, width, height, frameBufferObjectMultiSample);
	}
}
