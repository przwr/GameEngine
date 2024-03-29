/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import engine.utilities.ErrorHandler;
import game.Game;
import game.Settings;
import game.gameobject.entities.Mob;
import game.gameobject.entities.Player;
import game.place.map.Map;
import gamecontent.mobs.Rabbit;
import net.GameClient;
import net.GameOnline;
import net.GameServer;
import net.packets.*;

import java.util.ArrayList;

/**
 * @author przemek
 */
public class MyGameOnline extends GameOnline {

    private static final int ADD_PLAYER = 0;
    private static final int REMOVE_PLAYER = 1;
    private static final int UPDATE_MOBS = 2;

    public MyGameOnline(Game game, int changes, int players) {
        super(game, changes, players);
    }

    @Override
    public synchronized void runServer() {
        server = new GameServer(game.players[0], this);
        server.Start();
        if (!server.isRunning) {
            server.Close();
            server = null;
        } else {
            game.runServer();
            game.mode = 1;
        }
    }

    @Override
    public synchronized void joinServer() {
        try {
            client = new GameClient(game.players[0], this, Settings.serverIP);
            if (client.isConnected) {
                game.runClient();
                game.mode = 1;
                while (client.tempMapId == -1) {
                }
                game.players[0].changeMap(game.getPlace().getMapById(client.tempMapId), game.players[0].getX(), game.players[0].getY());
            } else {
                client.Close();
                client = null;
            }
        } catch (Exception exception) {
            String error = "ERROR: - " + exception.getMessage() + " in " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this
                    .getClass();
            ErrorHandler.logAndPrint(error);
        }
    }

    @Override
    public synchronized void addPlayer(NewMultiPlayer player) {
        try {
            if (newPlayers[0] == null) {
                newPlayers[0] = player;
            } else if (newPlayers[1] == null) {
                newPlayers[1] = player;
            } else if (newPlayers[2] == null) {
                newPlayers[2] = player;
            }
            isChanged[ADD_PLAYER] = true;
        } catch (Exception exception) {
            String error = "ERROR: - " + exception.getMessage() + " in " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this
                    .getClass();
            ErrorHandler.logAndPrint(error);
        }
    }

    @Override
    public synchronized void removePlayer(byte playerID) {
        try {
            if (removeIDs[0] == 0) {
                removeIDs[0] = playerID;
            } else if (removeIDs[1] == 0) {
                removeIDs[1] = playerID;
            } else if (removeIDs[1] == 0) {
                removeIDs[2] = playerID;
            }
            isChanged[REMOVE_PLAYER] = true;
        } catch (Exception exception) {
            String error = "ERROR: - " + exception.getMessage() + " in " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this
                    .getClass();
            ErrorHandler.logAndPrint(error);
        }
    }

    @Override
    public synchronized void update(PacketUpdate update) {
        try {
            if (game.getPlace() != null) {
                UpdateMobs(update.mobs(), update.getMapId());
                UpdatePlayers(update.players());
            }
        } catch (Exception exception) {
            String error = "ERROR: - " + exception.getMessage() + " in " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this
                    .getClass();
            ErrorHandler.logAndPrint(error);
        }
    }

    private synchronized void UpdatePlayers(ArrayList<MultiPlayerUpdate> players) {
        try {
            tempPlace = game.getPlace();
            if (tempPlace != null) {
                Player player;
                for (MultiPlayerUpdate playerUpdate : players) {
                    for (int p = 1; p < tempPlace.playersCount; p++) {
                        if (playerUpdate.getId() == game.players[p].playerID) {
                            player = game.players[p];
                            player.updates[player.lastAdded] = playerUpdate;
                            if (player.lastAdded == 3) {
                                player.lastAdded = 0;
                            } else {
                                player.lastAdded++;
                            }
                            break;
                        }
                    }
                }
            }
        } catch (Exception exception) {
            String error = "ERROR: - " + exception.getMessage() + " in " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this
                    .getClass();
            ErrorHandler.logAndPrint(error);
        }
    }

    private synchronized void UpdateMobs(ArrayList<MobUpdate> mobs, short mapId) {
        try {
            if (!activeFirstMobsUpdates) {
                firstMobsUpdates = mobs;
                mapIDsForUpdate[0] = mapId;
                activeFirstMobsUpdates = true;
            } else {
                secondMobsUpdates = mobs;
                mapIDsForUpdate[1] = mapId;
                activeFirstMobsUpdates = false;
            }
            isChanged[UPDATE_MOBS] = true;
        } catch (Exception exception) {
            String error = "ERROR: - " + exception.getMessage() + " in " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this
                    .getClass();
            ErrorHandler.logAndPrint(error);
        }
    }

    @Override
    public synchronized void playerUpdate(PacketMultiPlayerUpdate p) {
        try {
            tempPlace = game.getPlace();
            if (tempPlace != null) {
                Player player;
                for (int i = 0; i < tempPlace.playersCount; i++) {
                    if (p.up().getId() == game.players[i].playerID) {
                        player = game.players[i];
                        player.updates[player.lastAdded] = p.up();
                        if (player.lastAdded == 3) {
                            player.lastAdded = 0;
                        } else {
                            player.lastAdded++;
                        }
                        break;
                    }
                }
            }
        } catch (Exception exception) {
            String error = "ERROR: - " + exception.getMessage() + " in " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this
                    .getClass();
            ErrorHandler.logAndPrint(error);
        }
    }

    @Override
    public synchronized void initializeChanges() {
        changes[ADD_PLAYER] = () -> {
            try {
                tempPlace = game.getPlace();
                if (tempPlace != null) {
                    for (int i = 0; i < newPlayers.length; i++) {
                        if (newPlayers[i] != null) {
                            NewMultiPlayer temp = newPlayers[i];
                            System.out.println("Adding player with ID: " + temp.getId() + " - " + temp.getName());
                            game.players[tempPlace.playersCount].initializeSetPosition(56, 104, tempPlace, temp.getX(), temp.getY());
                            game.players[tempPlace.playersCount].playerID = temp.getId();
                            game.players[tempPlace.playersCount].setName(temp.getName());
                            tempPlace.players[tempPlace.playersCount] = game.players[tempPlace.playersCount];
                            Map map = tempPlace.getMapById(newPlayers[i].getMapId());
                            map.addObject(game.players[tempPlace.playersCount]);
                            if (server != null) {
                                server.findPlayer(temp.getId()).setPlayer(game.players[tempPlace.playersCount]);
                            }
                            tempPlace.playersCount++;
                            newPlayers[i] = null;
                        }
                    }
                }
            } catch (Exception exception) {
                String error = "ERROR: - " + exception.getMessage() + " in " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this
                        .getClass();
                ErrorHandler.logAndPrint(error);
            }
        };
        changes[REMOVE_PLAYER] = () -> {
            try {
                tempPlace = game.getPlace();
                if (tempPlace != null) {
                    for (int i = 0; i < removeIDs.length; i++) {
                        for (int p = 1; p < tempPlace.playersCount; p++) {
                            if (game.players[p].playerID == removeIDs[i]) {
                                tempPlace.players[p].setNotInGame();
                                tempPlace.players[p].getMap().deleteObject(tempPlace.players[p]);
                                if (p != tempPlace.playersCount - 1) {
                                    Player tempG = game.players[tempPlace.playersCount - 1];
                                    Player tempP = tempPlace.players[tempPlace.playersCount - 1];
                                    game.players[tempPlace.playersCount - 1] = game.players[p];
                                    tempPlace.players[tempPlace.playersCount - 1] = tempPlace.players[p];
                                    game.players[p] = tempG;
                                    tempPlace.players[p] = tempP;
                                }
                                tempPlace.playersCount--;
                                removeIDs[i] = 0;
                            }
                        }
                    }
                }
            } catch (Exception exception) {
                String error = "ERROR: - " + exception.getMessage() + " in " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this
                        .getClass();
                ErrorHandler.logAndPrint(error);
            }
        };
        changes[UPDATE_MOBS] = () -> {
            try {
                boolean found;
                boolean addNew = false;
                ArrayList<MobUpdate> mobs;
                short mapId;
                if (activeFirstMobsUpdates) {
                    mapId = mapIDsForUpdate[0];
                    mobs = firstMobsUpdates;
                } else {
                    mapId = mapIDsForUpdate[1];
                    mobs = secondMobsUpdates;
                }
                Map map = tempPlace.getMapById(mapId);
                for (MobUpdate mUp : mobs) {
                    found = false;
                    for (Mob mob : map.getArea(game.players[0].getArea()).getNearSolidMobs()) {
                        if (mUp.getId() == mob.mobID) {
                            mob.updates[mob.lastAdded] = mUp;
                            if (mob.lastAdded == 3) {
                                mob.lastAdded = 0;
                            } else {
                                mob.lastAdded++;
                            }
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        addNew = true;
                        for (int i = 0; i < newMobs.length; i++) {
                            if (newMobs[i] != null && newMobs[i].getId() == mUp.getId()) {
                                newMobs[i] = mUp;
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            for (int i = 0; i < newMobs.length; i++) {
                                if (newMobs[i] == null) {
                                    newMobs[i] = mUp;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (addNew) {
                    for (int i = 0; i < newMobs.length; i++) {
                        if (newMobs[i] != null) {
                            System.out.println("Adding Mob with ID: " + newMobs[i].getId());
                            Mob mob = new Rabbit(newMobs[i].getX(), newMobs[i].getY(), 128, 28, 3, 512, "rabbit", tempPlace, true, newMobs[i].getId());
                            map.addObject(mob);
                            newMobs[i] = null;
                        }
                    }
                }
            } catch (Exception exception) {
                String error = "ERROR: - " + exception.getMessage() + " in " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this
                        .getClass();
                ErrorHandler.logAndPrint(error);
            }
        };
    }

    @Override
    public synchronized void update() {
        tempPlace = game.getPlace();
        if (tempPlace != null) {
            for (int i = 0; i < isChanged.length; i++) {
                if (isChanged[i]) {
                    isChanged[i] = false;
                    changes[i].change();
                }
            }
        }
    }

    @Override
    public synchronized Player getPlayerByID(byte playerID) {
        for (Player player : game.players) {
            if (player.playerID == playerID) {
                return player;
            }
        }
        return null;
    }

    @Override
    public synchronized void cleanUp() {
        if (server != null) {
            server.Close();
            server = null;
        }
        if (client != null) {
            client.Close();
            client = null;
        }
        if (pastPositions != null) {
            for (int i = 0; i < pastPositions.length; i++) {
                pastPositions[i] = null;
            }
            pastPositions = null;
        }
        game = null;
        for (int i = 0; i < changes.length; i++) {
            changes[i] = null;
        }
        for (int i = 0; i < newPlayers.length; i++) {
            newPlayers[i] = null;
        }
        for (int i = 0; i < newMobs.length; i++) {
            newMobs[i] = null;
        }
        if (firstMobsUpdates != null) {
            firstMobsUpdates.clear();
        }
        if (secondMobsUpdates != null) {
            secondMobsUpdates.clear();
        }
        tempPlace = null;
    }
}
