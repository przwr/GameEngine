package game.gameobject.interactive;

/**
 * Created by przemek on 31.08.15.
 */
public class InteractiveResponse {

    public static final byte BACK = 0, FRONT = 1, SIDE = 2;
    public static InteractiveResponse NO_RESPONSE = new InteractiveResponse();
    private float pixels = -1f;
    private byte direction = -1;


    public float getPixels() {
        return pixels;
    }

    public void setPixels(float pixels) {
        this.pixels = pixels;
    }

    public byte getDirection() {
        return direction;
    }

    public void setDirection(byte direction) {
        this.direction = direction;
    }
}
