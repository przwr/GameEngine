/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.packets;

/**
 * @author przemek
 */
public class PacketRemoveMultiPlayer {

    private byte id;

    public PacketRemoveMultiPlayer() {
    }

    public PacketRemoveMultiPlayer(byte id) {
        this.id = id;
    }

    public byte getId() {
        return id;
    }
}
