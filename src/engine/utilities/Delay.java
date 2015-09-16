/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.utilities;

/**
 * @author przemek
 */
public class Delay {

    private int length;
    private long endTime;
    private boolean started;

    public Delay(int length) {
        this.length = length;
        started = false;
    }

    public void setFrameLength(int length) {
        this.length = length;
    }

    public void setFPS(int fps) {
        if (fps != 0) {
            endTime -= length;
            this.length = 1000 / fps;
            started = true;
            endTime += length;
        } else {
            started = false;
        }
    }

    public boolean isOver() {
        return started && endTime <= System.currentTimeMillis();
    }

    public boolean isActive() {
        return started;
    }

    public void start() {
        started = true;
        endTime = length + System.currentTimeMillis();
    }

    public void startAt(int start) {
        started = true;
        endTime = start + System.currentTimeMillis();
    }

    public void stop() {
        started = false;
    }

    public void terminate() {
        started = true;
        endTime = 0;
    }
}
