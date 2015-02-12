package game.place.fbo;

import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.glViewport;
import org.lwjgl.opengl.GL30;

public class RegularFrameBufferObject extends FrameBufferObject {

	public RegularFrameBufferObject(int width, int height) {
		super(width, height, false);
		createFrameBuferObjects();
		type.activate(frameBufferObject);
		type.makeTexture(texture, frameBufferObject, width, height);
		type.deactivate();
	}

	@Override
	public void activate() {
		type.activate(frameBufferObject);
	//	glViewport(0, 0, (int) (height * ((float) Display.getWidth() / (float) Display.getHeight())), height);
	}

	@Override
	public void deactivate() {
		type.deactivate();
	//	glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}

	private void createFrameBuferObjects() {
		if (version == NATIVE) {
			frameBufferObject = GL30.glGenFramebuffers();
		} else if (version == ARB) {
			frameBufferObject = ARBFramebufferObject.glGenFramebuffers();
		} else {
			frameBufferObject = EXTFramebufferObject.glGenFramebuffersEXT();
		}

	}
}
