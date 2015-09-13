/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net;

import engine.utilities.Point;
import game.Game;
import game.gameobject.entities.Player;
import game.place.Place;
import net.packets.*;

import java.util.ArrayList;

/**
 * @author przemek
 */
public abstract class GameOnline {

    public final Point[] pastPositions = new Point[2048];
    public final Game game;
    protected final OnlineChange[] changes;
    protected final boolean[] isChanged;
    protected final NewMultiPlayer[] newPlayers;
    protected final byte removeIDs[];
    protected final MobUpdate[] newMobs;
    protected final short[] mapIDsForUpdate = new short[2];
    public int pastPositionsNumber;
    public GameServer server;
    public GameClient client;
    protected ArrayList<MobUpdate> firstMobsUpdates;
    protected ArrayList<MobUpdate> secondMobsUpdates;
    protected boolean activeFirstMobsUpdates;
    protected Place tempPlace;

    protected GameOnline(Game game, int nrChanges, int players) {
        this.game = game;
        changes = new OnlineChange[nrChanges];
        isChanged = new boolean[nrChanges];
        newPlayers = new NewMultiPlayer[players];
        removeIDs = new byte[players];
        MultiPlayerUpdate[] playersUpdates = new MultiPlayerUpdate[players];
        newMobs = new MobUpdate[1024];
        for (int i = 0; i < pastPositions.length; i += 8) {
            pastPositions[i] = new Point();
            pastPositions[i + 1] = new Point();
            pastPositions[i + 2] = new Point();
            pastPositions[i + 3] = new Point();
            pastPositions[i + 4] = new Point();
            pastPositions[i + 5] = new Point();
            pastPositions[i + 6] = new Point();
            pastPositions[i + 7] = new Point();
        }
    }

    public abstract void runServer();

    public abstract void joinServer();

    public abstract void cleanUp();

    public abstract void update();

    public abstract void initializeChanges();

    public abstract void addPlayer(NewMultiPlayer pl);

    public abstract void removePlayer(byte id);

    public abstract void update(PacketUpdate update);

    public abstract void playerUpdate(PacketMultiPlayerUpdate mPlayerUpdate);

    public abstract Player getPlayerByID(byte id);

}
