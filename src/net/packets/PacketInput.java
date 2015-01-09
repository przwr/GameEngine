
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
    private boolean[] inputs;

    public PacketInput() {
    }

    public PacketInput(byte id, boolean[] inputs) {
        this.id = id;
        this.inputs = new boolean[inputs.length];
        System.arraycopy(inputs, 0, this.inputs, 0, inputs.length);
    }

    public synchronized byte getId() {
        return id;
    }

    public synchronized boolean[] getInputs() {
        return inputs;
    }
}
