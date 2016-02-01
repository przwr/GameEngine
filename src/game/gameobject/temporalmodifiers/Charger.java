/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.temporalmodifiers;

import game.gameobject.entities.Entity;

/**
 * @author Wojtek
 */
public class Charger extends TemporalChanger {

    public final static byte NORMAL = 0, INCREASING = 1, DECREASING = 2;
    double currentValue, value, modifier;
    byte type;

    public Charger() {
        super();
    }

    public Charger(int frames, double value) {
        super(frames);
        currentValue = 0;
        this.value = value;
    }

    @Override
    public void start() {
        super.start();
        currentValue = 0;
    }

    public Charger setType(int frames, double value, byte type, double modifier) {
        setFrames(frames);
        this.value = value;
        this.type = type;
        this.modifier = modifier;
        return this;
    }

    public Charger setType(byte type) {
        this.type = type;
        this.modifier = 2;
        return this;
    }

    public Charger setType(byte type, double modifier) {
        this.type = type;
        this.modifier = modifier;
        return this;
    }

    public double getChargedValue() {
        switch (type) {
            case NORMAL:
                return value * getPercentDone();
            case INCREASING:
                return value * Math.pow(getPercentDone(), modifier);
            case DECREASING:
                return value * (1 - Math.pow(getPercentLeft(), modifier));
        }
        return 0;
    }

    @Override
    public void modifyEffect(Entity en) {
    }

    @Override
    public String toString() {
        return "Charge : " + getChargedValue() + " / " + value + " " + getPercentDone() + " : " + super.toString();
    }

}
