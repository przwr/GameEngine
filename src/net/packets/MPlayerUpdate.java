/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.packets;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author przemek
 */
public class MPlayerUpdate implements Serializable{

    private byte id;
    private int x, y;
    private boolean isEmits, isHop;
    private ArrayList<Short> delsX;
    private ArrayList<Short> delsY;
    private ArrayList<Boolean> delsEmits;
    private ArrayList<Boolean> delsHop;

    public MPlayerUpdate() {
    }

    public MPlayerUpdate(byte id) {
        this.id = id;
        delsX = new ArrayList<>();
        delsY = new ArrayList<>();
        delsEmits = new ArrayList<>();
        delsHop = new ArrayList<>();
    }

    public MPlayerUpdate(byte id, int x, int y, boolean isEmits, boolean isHop) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.isEmits = isEmits;
        this.isHop = isHop;
        delsX = new ArrayList<>();
        delsY = new ArrayList<>();
        delsEmits = new ArrayList<>();
        delsHop = new ArrayList<>();
    }

    public synchronized void Update(int x, int y, boolean isEmits, boolean isHop) {
        int deltaX = this.x - x;
        int deltaY = this.y - y;
        if (Math.abs(deltaX) <= 32767 && Math.abs(deltaY) <= 32767) {
            delsX.add((short) deltaX);
            delsY.add((short) deltaY);
            delsEmits.add(isEmits);
            delsHop.add(isHop);
        }
    }

    public synchronized void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public synchronized void setBooleans(boolean isEmits, boolean isHop) {
        this.isEmits = isEmits;
        this.isHop = isHop;
    }

    public synchronized byte getId() {
        return id;
    }

    public synchronized int getX() {
        return x;
    }

    public synchronized int getY() {
        return y;
    }

    public synchronized boolean isEmits() {
        return isEmits;
    }

    public synchronized boolean isHop() {
        return isHop;
    }

    public synchronized void Trim() {
        delsX.trimToSize();
        delsY.trimToSize();
        delsEmits.trimToSize();
        delsHop.trimToSize();
    }
}
