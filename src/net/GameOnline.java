/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net;

import engine.Point;
import game.Game;
import game.gameobject.Player;
import game.place.Place;
import java.util.ArrayList;
import net.packets.MPlayerUpdate;
import net.packets.MobUpdate;
import net.packets.NewMPlayer;
import net.packets.PacketMPlayerUpdate;
import net.packets.PacketUpdate;

/**
 *
 * @author przemek
 */
public abstract class GameOnline {

    public Point[] pastPositions = new Point[2048];
    protected final OnlineChange[] changes;
    protected final boolean[] isChanged;
    protected final NewMPlayer[] newPlayers;
    protected final byte removeIDs[];
    protected final MPlayerUpdate[] playersUpdates;
    protected MobUpdate[] newMobs;
    protected ArrayList<MobUpdate> firstMobsUpdates;
    protected ArrayList<MobUpdate> secondMobsUpdates;
    protected short[] mapIDsForUpdate = new short[2];
    protected boolean activeFirstMobsUpdates;
    public int pastPositionsNumber;
    public final Game game;
    public GameServer server;
    public GameClient client;
    protected Place tempPlace;

    public GameOnline(Game game, int nrChanges, int players) {
        this.game = game;
        changes = new OnlineChange[nrChanges];
        isChanged = new boolean[nrChanges];
        newPlayers = new NewMPlayer[players];
        removeIDs = new byte[players];
        playersUpdates = new MPlayerUpdate[players];
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

    public abstract void addPlayer(NewMPlayer pl);

    public abstract void removePlayer(byte id);

    public abstract void update(PacketUpdate update);

    public abstract void playerUpdate(PacketMPlayerUpdate mPlayerUpdate);

    public abstract Player getPlayerByID(byte id);

}
