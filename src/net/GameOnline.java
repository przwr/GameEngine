/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net;

import net.packets.NewMPlayer;
import game.Game;
import game.gameobject.Player;
import net.packets.MPlayerUpdate;
import net.packets.MobUpdate;
import net.packets.PacketMPlayerUpdate;
import net.packets.PacketUpdate;

/**
 *
 * @author przemek
 */
public abstract class GameOnline {

    protected final change[] changes;
    protected final boolean[] isChanged;
    protected final NewMPlayer newPls[];
    protected final byte removeIDs[];
    protected final MPlayerUpdate[] plUps;
    protected MobUpdate[] newMob;
    public final Game g;
    public GameServer server;
    public GameClient client;

    public GameOnline(Game game, int nrChanges, int players) {
        g = game;
        changes = new change[nrChanges];
        isChanged = new boolean[nrChanges];
        newPls = new NewMPlayer[players];
        removeIDs = new byte[players];
        plUps = new MPlayerUpdate[players];
        newMob = new MobUpdate[1024];
    }

    public abstract void runServer();

    public abstract void joinServer();

    public abstract void cleanUp();

    public abstract void up();

    public abstract void initChanges();

    public abstract void addPlayer(NewMPlayer pl);

    public abstract void removePlayer(byte id);

    public abstract void update(PacketUpdate update);

    public abstract void playerUpdate(PacketMPlayerUpdate mPlayerUpdate);

    public abstract Player getPlayer(byte id);

    protected interface change {

        void change();
    }
}
