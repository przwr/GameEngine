/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package openGLEngine;

/**
 *
 * @author przemek
 */
public class Delay {

    private final int length;
    private long endTime;
    private boolean started;

    public Delay(int length) {
        this.length = length;
        this.started = false;
    }

    public boolean isOver() {
        if (!started) {
            return false;
        }
        return Time.getTime() >= endTime;
    }

    public boolean isActive() {
        return started;
    }

    public void restart() {
        started = true;
        endTime = length * 1000000 + Time.getTime();
    }

    public void stop() {
        started = false;
    }

    public void terminate() {
        started = true;
        endTime = 0;
    }
}
