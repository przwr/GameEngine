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
    private short mapId;
    private byte id;
    private String name;
    private int x, y;
    private Player pl;
    private PacketUpdate pu;

    public MPlayer() {
    }

    public MPlayer(short mapId, byte id, String name, Connection connection) {
        this.mapId = mapId;
        this.id = id;
        this.name = name;
        this.conection = connection;
        pu = new PacketUpdate();
    }

    public void update(short mapId, int x, int y, float SCALE) {
        this.mapId = mapId;
        this.x = (int) (((float) x) / SCALE);
        this.y = (int) (((float) y) / SCALE);
    }

    public void sendUpTCP() {
        pu.Trim();
        if (conection.isConnected()) {
            conection.sendTCP(pu);
        }
        pu.Reset();
    }

    public void setPlayer(Player pl) {
        this.pl = pl;
    }

    public byte getId() {
        return id;
    }

    public short getMapId() {
        return mapId;
    }

    public void setMapId(short mapId) {
        this.mapId = mapId;
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

    public Player inGame() {
        return pl;
    }

    public PacketUpdate getPU() {
        return pu;
    }
}
