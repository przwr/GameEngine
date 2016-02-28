/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.packets;

/**
 * @author przemek
 */
public class PacketJoinRequest {

    private String name;

    public PacketJoinRequest() {
    }

    public PacketJoinRequest(String name) {
        this.name = name;
    }

    public synchronized String getName() {
        return name;
    }
}
