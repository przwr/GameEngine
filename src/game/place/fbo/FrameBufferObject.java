package game.place.fbo;

import game.Settings;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.EXTFramebufferObject;
import static org.lwjgl.opengl.GL11.glGenTextures;
import org.lwjgl.opengl.GL30;

public abstract class FrameBufferObject {

	public static final int NATIVE = 0, ARB = 1, EXT = 2;
	public static final FrameBufferType REGULAR_NATIVE = new RegularNative();
	public static final FrameBufferType REGULAR_ARB = new RegularARB();
	public static final FrameBufferType REGULAR_EXT = new RegularNative();
	public static final FrameBufferType MULTISAMPLE_NATIVE = new MultisampleNative();
	public static final FrameBufferType MULTISAMPLE_ARB = new MultisampleARB();
	public static final FrameBufferType MULTISAMPLE_EXT = new MultisampleEXT();

	protected boolean multiSampleSupported;
	protected final FrameBufferType type;
	protected int height, width, texture, frameBufferObject, version;

	protected final activateType[] activates = new activateType[3];
	protected final deactivateType[] deactivates = new deactivateType[3];
	protected final makeTextureType[] makeTextures = new makeTextureType[3];

	public abstract void activate();

	public abstract void deactivate();

	public FrameBufferObject(int width, int height, boolean multisample) {
		this.width = width;
		this.height = height;
		texture = glGenTextures();
		version = Settings.supportedFrameBufferObjectVersion;
		if (multisample && Settings.multiSampleSupported && Settings.samplesCount > 0) {
			if (version == NATIVE) {
				type = MULTISAMPLE_NATIVE;
				frameBufferObject = GL30.glGenFramebuffers();
			} else if (version == ARB) {
				type = MULTISAMPLE_ARB;
				frameBufferObject = ARBFramebufferObject.glGenFramebuffers();
			} else {
				type = MULTISAMPLE_EXT;
				frameBufferObject = EXTFramebufferObject.glGenFramebuffersEXT();
				version = EXT;
			}
		} else {
			if (version == NATIVE) {
				type = REGULAR_NATIVE;
				frameBufferObject = GL30.glGenFramebuffers();
			} else if (version == ARB) {
				type = REGULAR_ARB;
				frameBufferObject = ARBFramebufferObject.glGenFramebuffers();
			} else {
				type = REGULAR_EXT;
				frameBufferObject = EXTFramebufferObject.glGenFramebuffersEXT();
				version = EXT;
			}
		}
	}

	public int getTexture() {
		return texture;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;

	}

	protected interface activateType {

		void activate();
	}

	protected interface makeTextureType {

		void makeTexture();
	}

	protected interface deactivateType {

		void deactivate();
	}
}
