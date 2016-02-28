/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.packets;

import net.MultiPlayer;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author przemek
 */
public class PacketUpdate implements Serializable {

    private final ArrayList<MultiPlayerUpdate> players;
    private final ArrayList<MobUpdate> mobs;
    private short mapId;

    public PacketUpdate() {
        players = new ArrayList<>(8);
        mobs = new ArrayList<>(8);
    }

    public PacketUpdate(MultiPlayerUpdate mp) {
        players = new ArrayList<>(8);
        players.add(mp);
        mobs = new ArrayList<>(8);
    }

    public void playerUpdate(MultiPlayer mpl, boolean isEmits, boolean isHop) {
        MultiPlayerUpdate mp = getPlayer(mpl.getId());
        if (mp == null || mp.getMapId() != mpl.getMapId()) {
            mp = new MultiPlayerUpdate(mpl.getMapId(), mpl.getId(), mpl.getX(), mpl.getY(), isEmits, isHop);
            players.add(mp);
        } else {
            mp.Update(mpl.getX(), mpl.getY());
        }
    }

    public void PlayerUpdate(MultiPlayerUpdate mpu) {
        players.add(mpu);
    }

    public void MobUpdate(short id, int x, int y) {
        MobUpdate mp = getMob(id);
        if (mp != null) {
            mp.Update(x, y);
        } else {
            mp = new MobUpdate(id, x, y);
            mobs.add(mp);
        }
    }

    private MultiPlayerUpdate getPlayer(byte id) {
        for (MultiPlayerUpdate mpu : players) {
            if (mpu.getId() == id) {
                return mpu;
            }
        }
        return null;
    }

    private MobUpdate getMob(short id) {
        for (MobUpdate mu : mobs) {
            if (mu.getId() == id) {
                return mu;
            }
        }
        return null;
    }

    public short getMapId() {
        return mapId;
    }

    public void Trim() {
        players.trimToSize();
        mobs.trimToSize();
        players.forEach(MultiPlayerUpdate::Trim);
        mobs.forEach(net.packets.MobUpdate::Trim);
    }

    public void Reset() {
        players.clear();
        mobs.clear();
    }

    public ArrayList<MultiPlayerUpdate> players() {
        return players;
    }

    public ArrayList<MobUpdate> mobs() {
        return mobs;
    }
}
