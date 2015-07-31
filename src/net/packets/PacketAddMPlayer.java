/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.packets;

/**
 * @author przemek
 */
public class PacketAddMPlayer {

    private NewMPlayer pl;

    public PacketAddMPlayer() {
    }

    public PacketAddMPlayer(NewMPlayer pl) {
        this.pl = pl;
    }

    public synchronized NewMPlayer getPlayer() {
        return pl;
    }
}
