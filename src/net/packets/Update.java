/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.packets;

import java.util.ArrayList;

/**
 *
 * @author przemek
 */
public abstract class Update {

    protected short mapId;
    protected int x, y;
    protected ArrayList<Short> delsX = new ArrayList<>();
    protected ArrayList<Short> delsY = new ArrayList<>();

    public synchronized int getX() {
        return x;
    }

    public synchronized int getY() {
        return y;
    }

    public synchronized ArrayList<Short> delsX() {
        return delsX;
    }

    public synchronized ArrayList<Short> delsY() {
        return delsY;
    }

    public synchronized short getMapId() {
        return mapId;
    }

}
