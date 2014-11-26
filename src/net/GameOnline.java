/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net;

import game.Game;
import game.gameobject.Player;
import net.packets.PacketInput;
import net.packets.PacketMPlayerUpdate;

/**
 *
 * @author przemek
 */
public abstract class GameOnline {

    protected final change[] changes;
    protected final boolean[] isChanged;
    protected final NewMPlayer newPls[];
    protected final byte removeIDs[];
    protected final PacketMPlayerUpdate[] plUps;
    public final Game g;
    public GameServer server;
    public GameClient client;

    public GameOnline(Game game, int changes, int players) {
        this.g = game;
        this.changes = new change[changes];
        this.isChanged = new boolean[changes];
        this.newPls = new NewMPlayer[players];
        this.removeIDs = new byte[players];
        this.plUps = new PacketMPlayerUpdate[players];
    }

    public abstract void runServer();

    public abstract void joinServer();

    public abstract void cleanUp();

    public abstract void up();

    public abstract void initChanges();

    public abstract void addPlayer(NewMPlayer pl);

    public abstract void removePlayer(byte id);

    public abstract void playerUpdate(PacketMPlayerUpdate mPlayerUpdate);

    public abstract void updatePlayersInput(Player pl, PacketInput input);

    public abstract Player getPlayer(byte id);

    protected interface change {

        void change();
    }
}
