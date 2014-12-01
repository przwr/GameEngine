/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.packets;

import net.MPlayer;

/**
 *
 * @author przemek
 */
public class PacketMPlayerUpdate {

    private byte id;
    private int x, y;
    private boolean isEmits;
    private boolean isHop;

    public PacketMPlayerUpdate() {
    }

    public PacketMPlayerUpdate(byte id) {
        this.id = id;
    }

    public PacketMPlayerUpdate(byte id, int x, int y, boolean isEmits, boolean isHop, float SCALE) {
        this.id = id;
        this.x = (int) (((float) x) / SCALE);
        this.y = (int) (((float) y) / SCALE);
        this.isEmits = isEmits;
        this.isHop = isHop;
    }

    public PacketMPlayerUpdate(MPlayer pl) {
        this.id = pl.getId();
        this.x = pl.getX();
        this.y = pl.getY();
        this.isEmits = pl.inGame().isEmits();
        this.isHop = pl.inGame().isJumping();
    }

    public byte getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isEmits() {
        return isEmits;
    }

    public boolean isJumping() {
        return isHop;
    }
}
