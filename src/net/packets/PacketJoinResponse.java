
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.packets;

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

    public synchronized byte getId() {
        return pl.getId();
    }

    public synchronized int getX() {
        return pl.getX();
    }

    public synchronized int getY() {
        return pl.getY();
    }

}
