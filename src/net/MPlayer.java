/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net;

import com.esotericsoftware.kryonet.Connection;
import game.gameobject.Player;
import net.packets.PacketUpdate;

/**
 *
 * @author przemek
 */
public class MPlayer {

    private Connection conection;
    private byte id;
    private String name;
    private int x, y;
    private Player pl;
    private PacketUpdate pu;

    public MPlayer() {
    }

    public MPlayer(String text, byte id, Connection connection) {
        this.name = text;
        this.id = id;
        this.conection = connection;
        pu = new PacketUpdate();

    }

    public synchronized void setPlayer(Player pl) {
        this.pl = pl;
    }

    public synchronized byte getId() {
        return id;
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized Connection getConnection() {
        return conection;
    }

    public synchronized void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public synchronized int getX() {
        return x;
    }

    public synchronized int getY() {
        return y;
    }

    public synchronized Player inGame() {
        return pl;
    }

    public synchronized void Update(int x, int y, float SCALE) {
        this.x = (int) (((float) x) / SCALE);
        this.y = (int) (((float) y) / SCALE);
    }

    public synchronized PacketUpdate PU() {
        return pu;
    }

    public synchronized void resetPU() {
        pu.Reset();
    }
}
