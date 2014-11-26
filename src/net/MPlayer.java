/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net;

import com.esotericsoftware.kryonet.Connection;
import game.gameobject.Player;

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

    public MPlayer() {
    }

    public MPlayer(String text, byte id, Connection connection) {
        this.name = text;
        this.id = id;
        this.conection = connection;

    }

    public void setPlayer(Player pl) {
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

    public Player inGame() {
        return pl;
    }

    public void Update(int x, int y, float SCALE) {
        this.x = (int) (((float) x) / SCALE);
        this.y = (int) (((float) y) / SCALE);
    }
}
