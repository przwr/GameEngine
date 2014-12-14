/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net;

import com.esotericsoftware.kryonet.Connection;
import game.gameobject.AbstractPlayer;
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
    private AbstractPlayer pl;
    private PacketUpdate pu;

    public MPlayer() {
    }

    public MPlayer(String text, byte id, Connection connection) {
        this.name = text;
        this.id = id;
        this.conection = connection;
        pu = new PacketUpdate();

    }

    public void setPlayer(AbstractPlayer pl) {
        this.pl = pl;
    }

    public byte getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Connection getConnection() {
        return conection;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public AbstractPlayer inGame() {
        return pl;
    }

    public void update(int x, int y, float SCALE) {
        this.x = (int) (((float) x) / SCALE);
        this.y = (int) (((float) y) / SCALE);
    }

    public void update(float SCALE) {
        this.x = (int) (((float) pl.getX()) / SCALE);
        this.y = (int) (((float) pl.getY()) / SCALE);
    }

    public void sendUpTCP() {
        pu.Trim();
        conection.sendTCP(pu);
        pu.Reset();
    }

    public PacketUpdate getPU() {
        return pu;
    }
}
