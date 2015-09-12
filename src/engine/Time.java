package engine;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author przemek
 */
public final class Time {

    private static final float DAMPING = 16000000;
    private static long currentTime;
    private static long lastTime;

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
}
