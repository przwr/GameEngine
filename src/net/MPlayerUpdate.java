/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net;

/**
 *
 * @author przemek
 */
public class MPlayerUpdate {

    private byte id;
    private int x, y;

    public MPlayerUpdate() {
    }

    public MPlayerUpdate(byte id) {
        this.id = id;
    }

    public MPlayerUpdate(MPlayer pl) {
        this.id = pl.getId();
        this.x = pl.getX();
        this.y = pl.getY();
    }

    public MPlayerUpdate(byte id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public byte getId() {
        return id;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }  
    
}
