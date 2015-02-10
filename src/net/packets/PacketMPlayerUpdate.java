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

	public PacketMPlayerUpdate(short mapId, byte id, int x, int y, boolean isEmits, boolean isHop) {
		mpu = new MPlayerUpdate(mapId, id, x, y, isEmits, isHop);
		mpu.Trim();
	}

	public synchronized void update(short mapID, byte id, int x, int y, boolean isEmits, boolean isHop) {
		if (mpu == null || mpu.getMapId() != mapID) {
			mpu = new MPlayerUpdate(mapID, id, x, y, isEmits, isHop);
		} else {
			mpu.Update(x, y);
		}
		mpu.Trim();
	}

	public synchronized void reset() {
		mpu = null;
	}

	public synchronized MPlayerUpdate up() {
		return mpu;
	}
}
