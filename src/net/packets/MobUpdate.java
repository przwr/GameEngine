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
public class MobUpdate extends Update {

    private short id;

    public MobUpdate() {
    }

    public MobUpdate(short id) {
        this.id = id;
        delsX = new ArrayList<>();
        delsY = new ArrayList<>();
    }

    public MobUpdate(short id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        delsX = new ArrayList<>();
        delsY = new ArrayList<>();
    }

    public synchronized void Update(int x, int y) {
        int deltaX = this.x - x;
        int deltaY = this.y - y;
        if (Math.abs(deltaX) <= 32767 && Math.abs(deltaY) <= 32767) {
            delsX.add((short) deltaX);
            delsY.add((short) deltaY);
        }
    }

    public synchronized void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public synchronized short getId() {
        return id;
    }

    public synchronized void Trim() {
        delsX.trimToSize();
        delsY.trimToSize();
    }

    public void setID(short id) {
        this.id = id;
    }
}
