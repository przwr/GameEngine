/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic;

import org.newdawn.slick.Color;

/**
 * @author Domi
 */
public class DayCycle {

    public static final float NIGHT = 0.4f;
    private static final short REAL_MINUTES_IN_HOUR = 6, SUNRISE = 300, SUNSET = 1260, 
            TRANSITION_TIME = 120, NOONTIME = 240, HALF_TRANSITION_TIME = TRANSITION_TIME / 2, 
            QUARTER_TRANSITION_TIME = TRANSITION_TIME / 4, 
            THREE_QUARTERS_TRANSITION_TIME = 3 * QUARTER_TRANSITION_TIME;
    private static final int DAWN = (SUNRISE + TRANSITION_TIME), 
            DUSK = (SUNSET - TRANSITION_TIME - QUARTER_TRANSITION_TIME), 
            NOON = ((SUNSET + SUNRISE) / 2);
    private final Color lightColor = new Color(0.2f, 0.2f, 0.2f);
    private short timeInMinutes = 0;
    private long midnightTime;
    private long currentTime;
    private long difference;
    private long stoppedAt;
    private boolean stopped;

    public DayCycle() {
        midnightTime = System.currentTimeMillis();
    }

    public void stopTime() {
        if (!stopped) {
            stopped = true;
            updateDifference();
            stoppedAt = difference - (timeInMinutes * REAL_MINUTES_IN_HOUR * 1000);
        }
    }

    public void resumeTime() {
        if (stopped) {
            updateDifference();
            midnightTime -= (timeInMinutes * REAL_MINUTES_IN_HOUR * 1000 - difference + stoppedAt);
            stopped = false;
            updateTime();
        }
    }

    public void updateTime() {
        if (!stopped) {
            updateDifference();
            calculateMinutes(difference / 1000);
            updateLightColor();
        }
    }

    private void updateDifference() {
        currentTime = System.currentTimeMillis();
        difference = currentTime - midnightTime;
    }

    private void calculateMinutes(long seconds) {
        long tempMinutes = (seconds / REAL_MINUTES_IN_HOUR);
        if (tempMinutes > 1440) {
            tempMinutes -= 1440;
            resetMidnightTime();
        }
        timeInMinutes = (short) tempMinutes;
    }

    private void resetMidnightTime() {
        midnightTime = currentTime;
    }

    private void updateLightColor() {
        float delta = (1 - NIGHT) / (NOON - SUNRISE - NOONTIME);
        float temp;
        if (timeInMinutes >= SUNRISE && timeInMinutes < DAWN) {
            temp = (timeInMinutes - SUNRISE) * delta;
            lightColor.r = lightColor.g = NIGHT + temp;
            if (timeInMinutes - SUNRISE < QUARTER_TRANSITION_TIME) {
                delta = 0.25f / QUARTER_TRANSITION_TIME;
                temp = 1 + ((timeInMinutes - SUNRISE) * delta);
                lightColor.b = lightColor.r * temp;
            } else if (timeInMinutes - SUNRISE < THREE_QUARTERS_TRANSITION_TIME) {
                delta = 0.75f / HALF_TRANSITION_TIME;
                temp = 1.25f - ((timeInMinutes - SUNRISE - QUARTER_TRANSITION_TIME) * delta);
                lightColor.b = lightColor.r * temp;
            } else {
                delta = 0.50f / QUARTER_TRANSITION_TIME;
                temp = 0.50f + ((timeInMinutes - SUNRISE - THREE_QUARTERS_TRANSITION_TIME) * delta);
            }
            lightColor.b = lightColor.r * temp;
            if (lightColor.b < 1)
                lightColor.g = (0.9f * lightColor.b + lightColor.r) / 1.9f;
        } else if (timeInMinutes >= DAWN && timeInMinutes < NOON - NOONTIME) {
            temp = (timeInMinutes - SUNRISE) * delta;
            lightColor.r = lightColor.g = lightColor.b = NIGHT + temp;
        } else if (timeInMinutes >= NOON - NOONTIME && timeInMinutes < NOON + NOONTIME) {
            lightColor.r = lightColor.g = lightColor.b = 1f;
        } else if (timeInMinutes >= NOON + NOONTIME && timeInMinutes < DUSK) {
            temp = (timeInMinutes - NOON - NOONTIME) * delta;
            lightColor.r = lightColor.g = lightColor.b = 1f - temp;
        } else if (timeInMinutes >= DUSK && timeInMinutes < SUNSET) {
            temp = (timeInMinutes - NOON - NOONTIME) * delta;
            lightColor.r = lightColor.g = 1f - temp;
            if (timeInMinutes - DUSK < HALF_TRANSITION_TIME) {
                delta = 0.50f / HALF_TRANSITION_TIME;
                temp = 1 - ((timeInMinutes - DUSK) * delta);
                lightColor.b = lightColor.r * temp;
            } else if (timeInMinutes - DUSK < TRANSITION_TIME) {
                delta = 0.75f / HALF_TRANSITION_TIME;
                temp = 0.50f + ((timeInMinutes - DUSK - HALF_TRANSITION_TIME) * delta);
            } else {
                delta = 0.25f / QUARTER_TRANSITION_TIME;
                temp = 1.25f - ((timeInMinutes - DUSK - TRANSITION_TIME) * delta);
            }
            lightColor.b = lightColor.r * temp;
            if (lightColor.b < 1)
                lightColor.g = (0.9f * lightColor.b + lightColor.r) / 1.9f;
        } else if (timeInMinutes >= SUNSET) {
            if (timeInMinutes >= SUNSET + HALF_TRANSITION_TIME) {
                lightColor.r = lightColor.g = lightColor.b = NIGHT / 2;
            } else {
                lightColor.r = lightColor.g = lightColor.b = NIGHT - (NIGHT / 2) * (timeInMinutes - SUNSET) / (float) HALF_TRANSITION_TIME;
            }
        } else if (timeInMinutes < SUNRISE) {
            if (timeInMinutes < SUNRISE - HALF_TRANSITION_TIME) {
                lightColor.r = lightColor.g = lightColor.b = NIGHT / 2;
            } else {
                lightColor.r = lightColor.g = lightColor.b = NIGHT / 2 + (NIGHT / 2) * (timeInMinutes + HALF_TRANSITION_TIME - SUNRISE) / (float) HALF_TRANSITION_TIME;
            }
        }
    }

    public short getTime() {
        return timeInMinutes;
    }

    public void setTime(int hour, int minutes) {
        updateDifference();
        midnightTime -= (((hour * 60 + minutes) * REAL_MINUTES_IN_HOUR * 1000) - difference);
        updateTime();
    }

    public void addOneHour() {
        addHours(1);
    }

    private void addHours(int hours) {
        midnightTime -= hours * REAL_MINUTES_IN_HOUR * 60000;
        updateTime();
    }

    public void addMinutes(int minutes) {
        midnightTime -= (minutes * REAL_MINUTES_IN_HOUR * 1000);
        updateTime();
    }

    public Color getShade() {
        return lightColor;
    }

    @Override
    public String toString() {
        int hours = this.timeInMinutes / 60;
        int minutes = this.timeInMinutes % 60;
        return (hours < 10 ? "0" : "") + hours + ":" + (minutes < 10 ? "0" : "") + minutes;
    }
}
