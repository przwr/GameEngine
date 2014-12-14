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
public class PacketMPlayerUpdate implements Serializable {

    private MPlayerUpdate mpu;

    public PacketMPlayerUpdate() {
    }

    public PacketMPlayerUpdate(byte id, int x, int y, boolean isEmits, boolean isHop) {
        mpu = new MPlayerUpdate(id, x, y, isEmits, isHop);
        mpu.Trim();
    }

    public void update(byte id, int x, int y, boolean isEmits, boolean isHop, float SCALE) {
        if (mpu == null) {
            mpu = new MPlayerUpdate(id, (int) (((float) x) / SCALE), (int) (((float) y) / SCALE), isEmits, isHop);
        } else {
            mpu.Update((int) (((float) x) / SCALE), (int) (((float) y) / SCALE));
        }
        mpu.Trim();
    }

    public void reset() {
        mpu = null;
    }

    public MPlayerUpdate up() {
        return mpu;
    }
}
