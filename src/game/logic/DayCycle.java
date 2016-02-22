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
public final class DayCycle {

    public static final float NIGHT = 0.2f;
    private static final short REAL_MINUTES_IN_HOUR = 6;
    private final short TIME_BEGIN_PREDAWN = 5 * 60, TIME_BEGIN_DAWN = 6 * 60, TIME_END_DAWN = 8 * 60, TIME_END_AFTERDAWN = 9 * 60;
    private final short TIME_BEGIN_PREDUSK = 17 * 60, TIME_BEGIN_DUSK = 18 * 60, TIME_END_DUSK = 20 * 60, TIME_END_AFTERDUSK = 21 * 60;
    private final Color lightColor = new Color(0.2f, 0.2f, 0.2f);
    private Color NIGHT_SKY, DARK_BLUE_SKY, RED_SKY, ORANGE_SKY, YELLOW_SKY, DAY_SKY;
    private short timeInMinutes = 0;
    private long midnightTime;
    private long currentTime;
    private long difference;
    private long stoppedAt;
    private boolean stopped;

    private float dayShadowAlpha;
    private float nightLightAlpha;

    public DayCycle() {
        midnightTime = System.currentTimeMillis();
        setNormalDayColors();
    }

    public void setNormalDayColors() {
        NIGHT_SKY = new Color(NIGHT, NIGHT, NIGHT);
        DARK_BLUE_SKY = new Color(0x5A5A91);
        RED_SKY = new Color(0x824544);
        ORANGE_SKY = new Color(0x82513F);
        YELLOW_SKY = new Color(0xFFFF69);
        DAY_SKY = new Color(Color.white);
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
        float delta;
        if (timeInMinutes < TIME_BEGIN_PREDAWN || timeInMinutes > TIME_END_AFTERDUSK) {
            //NOC
            lightColor.r = lightColor.g = lightColor.b = NIGHT;
        } else if (timeInMinutes <= TIME_BEGIN_DAWN) {
            //PRZED-WSCHOD
            delta = (float) (timeInMinutes - TIME_BEGIN_PREDAWN) / (TIME_BEGIN_DAWN - TIME_BEGIN_PREDAWN);
            mixColors(lightColor, NIGHT_SKY, DARK_BLUE_SKY, delta);
        } else if (timeInMinutes <= TIME_END_DAWN) {
            //WSCHOD
            delta = (float) (timeInMinutes - TIME_BEGIN_DAWN) / ((TIME_END_DAWN - TIME_BEGIN_DAWN) / 2);
            if (delta < 1f) {
                mixColors(lightColor, DARK_BLUE_SKY, ORANGE_SKY, delta);
            } else {
                mixColors(lightColor, ORANGE_SKY, YELLOW_SKY, delta - 1f);
            }
        } else if (timeInMinutes <= TIME_END_AFTERDAWN) {
            //PO-WSCHOD
            delta = (float) (timeInMinutes - TIME_END_DAWN) / (TIME_END_AFTERDAWN - TIME_END_DAWN);
            mixColors(lightColor, YELLOW_SKY, DAY_SKY, delta);
        } else if (timeInMinutes <= TIME_BEGIN_PREDUSK) {
            //DZIEN
            lightColor.r = lightColor.g = lightColor.b = 1f;
        } else if (timeInMinutes <= TIME_BEGIN_DUSK) {
            //PRZED-ZACHOD
            delta = (float) (timeInMinutes - TIME_BEGIN_PREDUSK) / (TIME_BEGIN_DUSK - TIME_BEGIN_PREDUSK);
            mixColors(lightColor, DAY_SKY, YELLOW_SKY, delta);
        } else if (timeInMinutes <= TIME_END_DUSK) {
            //ZACHOD
            delta = (float) (timeInMinutes - TIME_BEGIN_DUSK) / ((TIME_END_DUSK - TIME_BEGIN_DUSK) / 2);
            if (delta < 1f) {
                mixColors(lightColor, YELLOW_SKY, RED_SKY, delta);
            } else {
                mixColors(lightColor, RED_SKY, DARK_BLUE_SKY, delta - 1f);
            }
        } else if (timeInMinutes <= TIME_END_AFTERDUSK) {
            //PO-ZACHOD
            delta = (float) (timeInMinutes - TIME_END_DUSK) / (TIME_END_AFTERDUSK - TIME_END_DUSK);
            mixColors(lightColor, DARK_BLUE_SKY, NIGHT_SKY, delta);
        }

        delta = (lightColor.r + lightColor.g + lightColor.b) / 3;
        dayShadowAlpha = (delta - NIGHT) / (1 - NIGHT);
        if (delta <= NIGHT * 2f) {
            if (delta != NIGHT) {
                nightLightAlpha = 1f - (delta - NIGHT) / (NIGHT);
            } else {
                nightLightAlpha = 1f;
            }
        } else {
            nightLightAlpha = 0f;
        }
    }

    private void mixColors(Color mix, Color from, Color to, float alpha) {
        mix.r = from.r + (to.r - from.r) * alpha;
        mix.g = from.g + (to.g - from.g) * alpha;
        mix.b = from.b + (to.b - from.b) * alpha;
        mix.a = from.a + (to.a - from.a) * alpha;
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

    public long getCurrentTimeInMiliSeconds() {
        return currentTime;
    }

    public Color getShade() {
        return lightColor;
    }

    public float getDayShadowAlpha() {
        return dayShadowAlpha;
    }

    public float getNightLightAlpha() {
        return nightLightAlpha;
    }

    public boolean isNightNow() {
        return timeInMinutes < TIME_BEGIN_PREDAWN || timeInMinutes > TIME_END_AFTERDUSK;
    }

    public boolean isDawnNow() {
        return timeInMinutes >= TIME_BEGIN_PREDAWN && timeInMinutes < TIME_END_AFTERDAWN;
    }

    public boolean isDayNow() {
        return timeInMinutes >= TIME_END_AFTERDAWN && timeInMinutes < TIME_BEGIN_PREDUSK;
    }

    public boolean isDuskNow() {
        return timeInMinutes >= TIME_BEGIN_PREDUSK && timeInMinutes <= TIME_END_AFTERDUSK;
    }

    @Override
    public String toString() {
        int hours = this.timeInMinutes / 60;
        int minutes = this.timeInMinutes % 60;
        return (hours < 10 ? "0" : "") + hours + ":" + (minutes < 10 ? "0" : "") + minutes;
    }
}
