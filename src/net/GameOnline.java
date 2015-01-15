/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net;

import net.packets.NewMPlayer;
import game.Game;
import game.gameobject.Player;
import game.place.Place;
import java.util.ArrayList;
import net.packets.MPlayerUpdate;
import net.packets.MobUpdate;
import net.packets.PacketMPlayerUpdate;
import net.packets.PacketUpdate;

/**
 *
 * @author przemek
 */
public abstract class GameOnline {

    public PastPosition[] past = new PastPosition[2048];
    protected final change[] changes;
    protected final boolean[] isChanged;
    protected final NewMPlayer newPls[];
    protected final byte removeIDs[];
    protected final MPlayerUpdate[] plUps;
    protected MobUpdate[] newMob;
    protected ArrayList<MobUpdate> mUps1;
    protected ArrayList<MobUpdate> mUps2;
    protected short[] mapIdsForUpdate = new short[2];
    protected boolean isMUps1;
    public int pastNr;
    public final Game g;
    public GameServer server;
    public GameClient client;
    protected Place tempPlace;

    public GameOnline(Game game, int nrChanges, int players) {
        g = game;
        changes = new change[nrChanges];
        isChanged = new boolean[nrChanges];
        newPls = new NewMPlayer[players];
        removeIDs = new byte[players];
        plUps = new MPlayerUpdate[players];
        newMob = new MobUpdate[1024];
        for (int i = 0; i < past.length; i += 8) {
            past[i] = new PastPosition();
            past[i + 1] = new PastPosition();
            past[i + 2] = new PastPosition();
            past[i + 3] = new PastPosition();
            past[i + 4] = new PastPosition();
            past[i + 5] = new PastPosition();
            past[i + 6] = new PastPosition();
            past[i + 7] = new PastPosition();
        }
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

    public abstract Player getPlayerByID(byte id);

    protected interface change {

        void doIt();
    }
}
