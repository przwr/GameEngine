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

    private Delay(int length) {
        this.length = length;
        started = false;
    }

    public static Delay createInMinutesAndSeconds(int minutes, int seconds) {
        return new Delay((seconds + minutes * 60) * 1000);
    }

    public static Delay createInSecondsAndMiliseconds(int seconds, int miliseconds) {
        return new Delay(miliseconds + seconds * 1000);
    }

    public static Delay createInSeconds(int seconds) {
        return new Delay(seconds * 1000);
    }

    public static Delay createInMiliseconds(int miliseconds) {
        return new Delay(miliseconds);
    }

    public void setFrameLengthInSecondsAndMiliseconds(int seconds, int miliseconds) {
        this.length = seconds * 1000 + miliseconds;
    }
    
    public void setFrameLengthInMinutesAndSeconds(int minutes, int seconds) {
        this.length = (minutes * 60 + seconds) * 1000;
    }
    
    public void setFrameLengthInMiliseconds(int miliseconds) {
        this.length = miliseconds;
    }
    
    public void setFrameLengthInSeconds(int seconds) {
        this.length = seconds * 1000;
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
    
    public boolean isWorking() {
        return started && endTime > System.currentTimeMillis();
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
