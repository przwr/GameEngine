package game.gameobject.interactive;

import game.gameobject.GameObject;

/**
 * Created by przemek on 31.08.15.
 */
public class InteractiveResponse {

    public static final byte BACK = 0, FRONT = 1, SIDE = 2;
    public static InteractiveResponse NO_RESPONSE = new InteractiveResponse();
    private float pixels = -1f;
    private float knockback = -1f;
    private int maxPixels = -1;
    private byte direction = -1;
    private byte attackType = -1;
    private GameObject attacker;


    public float getPixels() {
        return pixels;
    }

    public void setPixels(float pixels) {
        this.pixels = pixels;
    }
    
    public float getKnockBack() {
        return knockback;
    }

    public void setKnockBack(float knockback) {
        this.knockback = knockback;
    }

    public void setResponse(float pixels, int maxPixels, byte direction, byte attackType, GameObject attacker) {
        this.pixels = pixels;
        this.maxPixels = maxPixels;
        this.direction = direction;
        this.attacker = attacker;
        this.attackType = attackType;
    }

    public byte getDirection() {
        return direction;
    }

    public void setDirection(byte direction) {
        this.direction = direction;
    }

    public byte getAttackType() {
        return attackType;
    }

    public void setAttackType(byte attackType) {
        this.attackType = attackType;
    }

    public GameObject getAttacker() {
        return attacker;
    }

    public void setAttacker(GameObject attacker) {
        this.attacker = attacker;
    }

    public int getMaxPixels() {
        return maxPixels;
    }

    public void setMaxPixels(int maxPixels) {
        this.maxPixels = maxPixels;
    }
}
