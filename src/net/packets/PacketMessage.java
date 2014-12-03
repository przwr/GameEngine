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
public class PacketMessage {

    private String message;

    public PacketMessage() {
    }

    public PacketMessage(String message) {
        this.message = message;
    }

    public synchronized String getMessage() {
        return message;
    }

    public synchronized void setMessage(String message) {
        this.message = message;
    }
}
