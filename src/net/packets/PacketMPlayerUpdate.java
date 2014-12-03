/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.packets;

import java.io.Serializable;

/**
 *
 * @author przemek
 */
public class PacketMPlayerUpdate implements Serializable{

    private MPlayerUpdate mpu;

    public PacketMPlayerUpdate() {
    }

    public PacketMPlayerUpdate(byte id, int x, int y, boolean isEmits, boolean isHop, float SCALE) {
        mpu = new MPlayerUpdate(id, (int) (((float) x) / SCALE), (int) (((float) y) / SCALE), isEmits, isHop);
    }

    public synchronized void Upadte(int x, int y, boolean isEmits, boolean isHop, float SCALE) {
        mpu.Update((int) (((float) x) / SCALE), (int) (((float) y) / SCALE), isEmits, isHop);
    }

    public synchronized MPlayerUpdate MPU() {
        return mpu;
    }
}
