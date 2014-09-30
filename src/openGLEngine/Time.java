package openGLEngine;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author przemek
 */
public class Time {

    private static final float DAMPING = 16000000;    
    private static long curTime;
    private static long lastTime;

    public static long getTime() {
        return System.nanoTime();
    }

    public static float getDelta() {
        return (curTime - lastTime) / DAMPING;
    }

    public static void update() {
        lastTime = curTime;
        curTime = getTime();
    }

    public static void init() {
        lastTime = getTime();
        curTime = getTime();
    }

}
