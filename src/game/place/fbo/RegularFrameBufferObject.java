package game.place.fbo;

import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.EXTFramebufferObject;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScissor;
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
//		glMatrixMode(GL_MODELVIEW);
//		glPushMatrix();
//		glLoadIdentity();
//		glViewport(0, 0, width, height);
//		glScissor(0, 0, width, height);
	}

	@Override
	public void deactivate() {
		type.deactivate();
//		glPopMatrix();
//		glMatrixMode(GL_PROJECTION);
//		glViewport(0, 0, Display.getWidth(), Display.getHeight());
//		glScissor(0, 0, Display.getWidth(), Display.getHeight());

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
