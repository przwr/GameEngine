package engine.systemcommunication;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author przemek
 */
public final class Time {

    private static float DAMPING = 50000000 / 3f;
    private static long currentTime;
    private static long lastTime;
    public static float speed = 1;

    private Time() {
    }

    public static float getDelta() {
        return (currentTime - lastTime) / DAMPING;
    }

    public static void update() {
        lastTime = currentTime;
        currentTime = System.nanoTime();
    }

    public static void initialize() {
        lastTime = System.nanoTime();
        currentTime = System.nanoTime();
    }

    public static void resetGameSpeed() {
        DAMPING = 50000000 / 3f;
    }

    public static void setGameSpeed(float factor) {
        resetGameSpeed();
        speed = factor;
        DAMPING /= speed;
    }
}
