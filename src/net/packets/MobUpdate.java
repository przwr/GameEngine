/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.packets;

import net.jodk.lang.FastMath;

/**
 * @author przemek
 */
public class MobUpdate extends Update {

    private short id;

    public MobUpdate() {
    }

    public MobUpdate(short id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public void Update(int x, int y) {
        int deltaX = this.x - x;
        int deltaY = this.y - y;
        if (FastMath.abs(deltaX) <= Short.MAX_VALUE && FastMath.abs(deltaY) <= Short.MAX_VALUE) {
            xDeltas.add((short) deltaX);
            yDeltas.add((short) deltaY);
        }
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public short getId() {
        return id;
    }

    public void Trim() {
        xDeltas.trimToSize();
        yDeltas.trimToSize();
    }

    public void setID(short id) {
        this.id = id;
    }
}
