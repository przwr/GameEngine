/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.packets;

import java.io.Serializable;
import net.jodk.lang.FastMath;

/**
 *
 * @author przemek
 */
public class MPlayerUpdate extends Update implements Serializable {

    private byte id;
    private short mapId;
    private boolean isEmits, isHop;

    public MPlayerUpdate() {
    }

    public MPlayerUpdate(short mapId, byte id) {
        this.mapId = mapId;
        this.id = id;
    }

    public MPlayerUpdate(short mapId, byte id, int x, int y, boolean isEmits, boolean isHop) {
        this.mapId = mapId;
        this.id = id;
        this.x = x;
        this.y = y;
        this.isEmits = isEmits;
        this.isHop = isHop;
    }

    public synchronized void Update(int x, int y) {
        int deltaX = this.x - x;
        int deltaY = this.y - y;
        if (FastMath.abs(deltaX) <= 32767 && FastMath.abs(deltaY) <= 32767) {
            delsX.add((short) deltaX);
            delsY.add((short) deltaY);
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

    public synchronized short getMapId() {
        return mapId;
    }

    public synchronized byte getId() {
        return id;
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
    }
}
