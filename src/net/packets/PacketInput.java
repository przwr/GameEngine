
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
public class PacketInput {

    private byte id;
    private boolean[] inputs = new boolean[10];

    public PacketInput() {
    }

    public PacketInput(byte id, boolean[] inputs) {
        this.id = id;
        this.inputs = inputs;
    }

    public byte getId() {
        return id;
    }

    public boolean[] inputs() {
        return inputs;
    }
}
