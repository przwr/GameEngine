/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

/**
 *
 * @author przemek
 */
public class Delay {

    private final int length;
    private long endTime;
    private boolean started;
    private long tillEnd;

    public Delay(int length) {
        this.length = length;
        started = false;
    }

    public boolean isOver() {
        if (!started) {
            tillEnd = 0;
            return false;
        }
        return endTime <= Time.getTime();
    }

    public boolean isActive() {
        return started;
    }

    public void start() {
        started = true;
        endTime = length * 1000000 + Time.getTime();
    }

    public void startAt(int start) {
        started = true;
        endTime = start * 1000000 + Time.getTime();
    }

    public void stop() {
        started = false;
    }

    public void terminate() {
        started = true;
        endTime = 0;
    }

    public long getTillEnd() {
        return tillEnd;
    }
}
