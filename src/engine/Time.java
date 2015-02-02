package engine;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author przemek
 */
public final class Time {

    private static final float DAMPING = 16000000;
    private static long curentTime;
    private static long lastTime;

    public static long getTime() {
        return System.nanoTime();
    }

    public static float getDelta() {
        return (curentTime - lastTime) / DAMPING;
    }

    public static void update() {
        lastTime = curentTime;
        curentTime = getTime();
    }

    public static void initialize() {
        lastTime = getTime();
        curentTime = getTime();
    }

    private Time() {
    }
}
