/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import org.newdawn.slick.Color;

/**
 *
 * @author Domi
 */
public class DayCycle {

    Color lightColor = new Color(0.2f, 0.2f, 0.2f);

    short timeInMinutes = 0;
    long midnightTime;
    long currentTime, diffrence, tempMinutes;
    short REAL_MINUTES_IN_HOUR = 6;

    // stop start Time!
    public DayCycle() {
        midnightTime = System.currentTimeMillis();
    }

    public void updateTime() {
        updateDiffrence();
        calculateMinutes(diffrence / 1000);
        updateLightColor();
    }

    private void updateDiffrence() {
        currentTime = System.currentTimeMillis();
        diffrence = currentTime - midnightTime;
    }

    private void calculateMinutes(long seconds) {
        tempMinutes = (seconds / REAL_MINUTES_IN_HOUR);
        if (tempMinutes > 1440) {
            tempMinutes -= 1440;
            resetMidnightTime();
        }
        timeInMinutes = (short) tempMinutes;
    }

    private void resetMidnightTime() {
        midnightTime = currentTime;
    }

    short SUNRISE = 300, SUNSET = 1260, TRANSITION_TIME = 120, NOONTIME = 240;
    float NIGHT = 0.2f, delta, temp;
    int DAWN = (SUNRISE + TRANSITION_TIME), DUSK = (SUNSET - TRANSITION_TIME), NOON = ((SUNSET + SUNRISE) / 2), HALF_TRANSITION_TIME = TRANSITION_TIME / 2, QUARTER_TRANSITION_TIME = TRANSITION_TIME / 4, THREE_QUARTERS_TRANSITION_TIME = 3 * QUARTER_TRANSITION_TIME;

    private void updateLightColor() {
        delta = (1 - NIGHT) / (NOON - SUNRISE - NOONTIME);
        if (timeInMinutes >= SUNRISE && timeInMinutes < DAWN) {
            temp = (timeInMinutes - SUNRISE) * delta;
            lightColor.r = lightColor.g = NIGHT + temp;
            if (timeInMinutes - SUNRISE < QUARTER_TRANSITION_TIME) {
                delta = 0.25f / QUARTER_TRANSITION_TIME;
                temp = 1 + ((timeInMinutes - SUNRISE) * delta);
                lightColor.b = lightColor.r * temp;
            } else if (timeInMinutes - SUNRISE < THREE_QUARTERS_TRANSITION_TIME) {
                delta = 0.5f / HALF_TRANSITION_TIME;
                temp = 1.25f - ((timeInMinutes - SUNRISE - QUARTER_TRANSITION_TIME) * delta);
                lightColor.b = lightColor.r * temp;
            } else {
                delta = 0.25f / QUARTER_TRANSITION_TIME;
                temp = 0.75f + ((timeInMinutes - SUNRISE - THREE_QUARTERS_TRANSITION_TIME) * delta);
                lightColor.b = lightColor.r * temp;
            }
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
            if (timeInMinutes - DUSK < QUARTER_TRANSITION_TIME) {
                delta = 0.25f / QUARTER_TRANSITION_TIME;
                temp = 1 - ((timeInMinutes - DUSK) * delta);
                lightColor.b = lightColor.r * temp;
            } else if (timeInMinutes - DUSK < THREE_QUARTERS_TRANSITION_TIME) {
                delta = 0.5f / HALF_TRANSITION_TIME;
                temp = 0.75f + ((timeInMinutes - DUSK - QUARTER_TRANSITION_TIME) * delta);
                lightColor.b = lightColor.r * temp;
            } else {
                delta = 0.25f / QUARTER_TRANSITION_TIME;
                temp = 1.25f - ((timeInMinutes - DUSK - THREE_QUARTERS_TRANSITION_TIME) * delta);
                lightColor.b = lightColor.r * temp;
            }
        } else if (timeInMinutes >= SUNSET || timeInMinutes < SUNRISE) {
            lightColor.r = lightColor.g = lightColor.b = NIGHT;
        }
    }

    public short getTime() {
        return timeInMinutes;
    }

    public void setTime(int hour, int minutes) {
        updateDiffrence();
        midnightTime -= (((hour * 60 + minutes) * REAL_MINUTES_IN_HOUR * 1000) - diffrence);
        updateTime();
    }

    public void addOneHour() {
        addHours(1);
    }

    public void addHours(int hours) {
        midnightTime -= hours * REAL_MINUTES_IN_HOUR * 60000;
        updateTime();
    }

    public void addMinutes(int minutes) {
        midnightTime -= (minutes * REAL_MINUTES_IN_HOUR * 1000);
        updateTime();
    }

    public Color getColor() {
        return lightColor;
    }

    @Override
    public String toString() {
        int hours = this.timeInMinutes / 60;
        int minutes = this.timeInMinutes % 60;
        return (hours < 10 ? "0" : "") + hours + ":" + (minutes < 10 ? "0" : "") + minutes;
    }
}
