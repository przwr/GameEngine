/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.packets;

/**
 * @author przemek
 */
public class PacketJoinResponse {

    private MultiPlayerUpdate pl;

    public PacketJoinResponse() {
    }

    public PacketJoinResponse(short mapId, byte id, int x, int y) {
        pl = new MultiPlayerUpdate(mapId, id);
        pl.setPosition(x, y);
    }

    public PacketJoinResponse(byte id) {
        pl = new MultiPlayerUpdate((short) -1, id);
    }

    public short getMapId() {
        return pl.getMapId();
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
