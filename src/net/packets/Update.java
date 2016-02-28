/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.packets;

import java.util.ArrayList;

/**
 * @author przemek
 */
public abstract class Update {

    final ArrayList<Short> xDeltas = new ArrayList<>();
    final ArrayList<Short> yDeltas = new ArrayList<>();
    int x;
    int y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public ArrayList<Short> getXDeltas() {
        return xDeltas;
    }

    public ArrayList<Short> getYDeltas() {
        return yDeltas;
    }
}
