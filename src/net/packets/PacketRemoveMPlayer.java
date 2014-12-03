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
public class PacketRemoveMPlayer {

    private byte id;

    public PacketRemoveMPlayer() {
    }

    public PacketRemoveMPlayer(byte id) {
        this.id = id;
    }

    public synchronized byte getId() {
        return id;
    }
}
