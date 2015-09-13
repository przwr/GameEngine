/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.packets;

/**
 * @author przemek
 */
public class PacketAddMultiPlayer {

    private NewMultiPlayer pl;

    public PacketAddMultiPlayer() {
    }

    public PacketAddMultiPlayer(NewMultiPlayer pl) {
        this.pl = pl;
    }

    public synchronized NewMultiPlayer getPlayer() {
        return pl;
    }
}
