package game.place.fbo;

public class RegularFrameBufferObject extends FrameBufferObject {

	public RegularFrameBufferObject(int width, int height) {
		super(width, height, false);
		type.activate(frameBufferObject);
		makeTexture();
		type.deactivate();
	}

	@Override
	public void activate() {
		type.activate(frameBufferObject);
	}

	@Override
	public void deactivate() {
		type.deactivate();
	}

	private void makeTexture() {
		type.makeTexture(texture, frameBufferObject, width, height);
	}
}
