/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.utilities;

import game.place.Place;

/**
 * @author przemek
 */
public class Delay {

    private final boolean realTime;
    private int length;
    private long endTime;
    private boolean started;

    private Delay(int length, boolean realTime) {
        this.length = length;
        this.realTime = realTime;
        started = false;
    }

    public static Delay createEmpty(boolean... realTime) {
        return new Delay(0, realTime.length > 0 && realTime[0]);
    }
    
    public static Delay createInMinutesAndSeconds(int minutes, int seconds, boolean... realTime) {
        return new Delay((seconds + minutes * 60) * 1000, realTime.length > 0 && realTime[0]);
    }

    public static Delay createInSecondsAndMilliseconds(int seconds, int miliseconds, boolean... realTime) {
        return new Delay(miliseconds + seconds * 1000, realTime.length > 0 && realTime[0]);
    }

    public static Delay createInSeconds(int seconds, boolean... realTime) {
        return new Delay(seconds * 1000, realTime.length > 0 && realTime[0]);
    }

    public static Delay createInMilliseconds(int miliseconds, boolean... realTime) {
        return new Delay(miliseconds, realTime.length > 0 && realTime[0]);
    }

    public void setFrameLengthInSecondsAndMilliseconds(int seconds, int miliseconds) {
        this.length = seconds * 1000 + miliseconds;
    }

    public void setFrameLengthInMinutesAndSeconds(int minutes, int seconds) {
        this.length = (minutes * 60 + seconds) * 1000;
    }

    public void setFrameLengthInMilliseconds(int miliseconds) {
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
        return started && endTime <= getCurrentMiliSeconds();
    }

    public boolean isWorking() {
        return started && endTime > getCurrentMiliSeconds();
    }

    public boolean isActive() {
        return started;
    }

    public void start() {
        started = true;
        endTime = length + getCurrentMiliSeconds();
    }

    public void startAt(int start) {
        started = true;
        endTime = start + getCurrentMiliSeconds();
    }

    public void stop() {
        started = false;
    }

    public void terminate() {
        started = true;
        endTime = 0;
    }

    public int getLength() {
        return length;
    }

    public long getDifference() {
        return endTime - getCurrentMiliSeconds();
    }


    private long getCurrentMiliSeconds() {
        return realTime ? System.currentTimeMillis() : Place.getDayCycle().getCurrentTimeInMiliSeconds();
    }
}
