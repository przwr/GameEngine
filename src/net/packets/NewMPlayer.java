/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.packets;

import net.MPlayer;

/**
 * @author przemek
 */
public class NewMPlayer {

    private short mapId;
    private byte id;
    private String name;
    private int x, y;

    public NewMPlayer() {
    }

    public NewMPlayer(short mapId, byte id, String name) {
        this.mapId = mapId;
        this.id = id;
        this.name = name;
    }

    public NewMPlayer(MPlayer pl) {
        this.mapId = pl.getMapId();
        this.id = pl.getId();
        this.name = pl.getName();
        this.x = pl.getX();
        this.y = pl.getY();
    }

    public synchronized short getMapId() {
        return mapId;
    }

    public synchronized byte getId() {
        return id;
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public synchronized int getX() {
        return x;
    }

    public synchronized int getY() {
        return y;
    }
}
