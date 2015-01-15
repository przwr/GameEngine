/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.packets;

import java.io.Serializable;
import java.util.ArrayList;
import net.MPlayer;

/**
 *
 * @author przemek
 */
public class PacketUpdate implements Serializable {

    private final ArrayList<MPlayerUpdate> players;
    private final ArrayList<MobUpdate> mobs;
    private short mapId;

    public PacketUpdate() {
        players = new ArrayList<>(8);
        mobs = new ArrayList<>(8);
    }

    public PacketUpdate(MPlayerUpdate mp) {
        players = new ArrayList<>(8);
        players.add(mp);
        mobs = new ArrayList<>(8);
    }

    public synchronized void playerUpdate(MPlayer mpl, boolean isEmits, boolean isHop) {
        MPlayerUpdate mp = getPlayer(mpl.getId());
        if (mp == null || mp.getMapId() != mpl.getMapId()) {
            mp = new MPlayerUpdate(mpl.getMapId(), mpl.getId(), mpl.getX(), mpl.getY(), isEmits, isHop);
            players.add(mp);
        } else {
            mp.Update(mpl.getX(), mpl.getY());
        }
    }

    public synchronized void PlayerUpdate(MPlayerUpdate mpu) {
        players.add(mpu);
    }

    public synchronized void MobUpdate(short id, int x, int y, float SCALE) {
        MobUpdate mp = getMob(id);
        if (mp != null) {
            mp.Update((int) (((float) x) / SCALE), (int) (((float) y) / SCALE));
        } else {
            mp = new MobUpdate(id, (int) (((float) x) / SCALE), (int) (((float) y) / SCALE));
            mobs.add(mp);
        }
    }

    public synchronized MPlayerUpdate getPlayer(byte id) {
        for (MPlayerUpdate mpu : players) {
            if (mpu.getId() == id) {
                return mpu;
            }
        }
        return null;
    }

    public synchronized MobUpdate getMob(short id) {
        for (MobUpdate mu : mobs) {
            if (mu.getId() == id) {
                return mu;
            }
        }
        return null;
    }

    public synchronized short getMapId() {
        return mapId;
    }

    public synchronized void Trim() {
        players.trimToSize();
        mobs.trimToSize();
        for (MPlayerUpdate mpu : players) {
            mpu.Trim();
        }
        for (MobUpdate mu : mobs) {
            mu.Trim();
        }
    }

    public synchronized void Reset() {
        players.clear();
        mobs.clear();
    }

    public synchronized ArrayList<MPlayerUpdate> players() {
        return players;
    }

    public synchronized ArrayList<MobUpdate> mobs() {
        return mobs;
    }
}
