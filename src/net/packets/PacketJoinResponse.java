
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.packets;

import net.MPlayerUpdate;

/**
 *
 * @author przemek
 */
public class PacketJoinResponse {

    private MPlayerUpdate pl;

    public PacketJoinResponse() {
    }

    public PacketJoinResponse(byte id, int x, int y) {
        pl = new MPlayerUpdate(id);
        pl.setPosition(x, y);
    }

    public PacketJoinResponse(byte id) {
        pl = new MPlayerUpdate(id);
    }

    public byte getId() {
        return pl.getId();
    }

    public int getX() {
        return pl.getX();
    }

    public int getY() {
        return pl.getY();
    }

}
