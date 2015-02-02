/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import game.Game;
import net.GameOnline;
import game.gameobject.GameObject;
import game.gameobject.Mob;
import game.gameobject.Player;
import game.place.Map;
import java.util.ArrayList;
import java.util.Iterator;
import net.GameClient;
import net.GameServer;
import net.packets.MPlayerUpdate;
import net.packets.MobUpdate;
import net.packets.NewMPlayer;
import net.packets.PacketMPlayerUpdate;
import net.packets.PacketUpdate;

/**
 *
 * @author przemek
 */
public class MyGameOnline extends GameOnline {

    public MyGameOnline(Game game, int changes, int players) {
        super(game, changes, players);
    }

    @Override
    public synchronized void runServer() {
        try {
            server = new GameServer(g.players[0], this);
            server.Start();
            if (!server.isRunning) {
                server.Close();
                server = null;
            } else {
                g.runServer();
                g.mode = 1;
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    @Override
    public synchronized void joinServer() {
        try {
            client = new GameClient(g.players[0], this, g.settings.serverIP);
            if (client.isConnected) {
                g.runClient();
                g.mode = 1;
                while (client.tempMapId == -1) {
                }
                g.players[0].changeMap(g.place.getMapById(client.tempMapId));
            } else {
                client.Close();
                client = null;
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    @Override
    public synchronized void addPlayer(NewMPlayer pl) {
        try {
            if (newPlayers[0] == null) {
                newPlayers[0] = pl;
            } else if (newPlayers[1] == null) {
                newPlayers[1] = pl;
            } else if (newPlayers[2] == null) {
                newPlayers[2] = pl;
            }
            isChanged[0] = true;
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    @Override
    public synchronized void removePlayer(byte id) {
        try {
            if (removeIDs[0] == 0) {
                removeIDs[0] = id;
            } else if (removeIDs[1] == 0) {
                removeIDs[1] = id;
            } else if (removeIDs[1] == 0) {
                removeIDs[2] = id;
            }
            isChanged[1] = true;
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    @Override
    public synchronized void update(PacketUpdate pl) {
        try {
            if (g.started) {
                UpdateMobs(pl.mobs(), pl.getMapId());
                UpdatePlayers(pl.players());
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private synchronized void UpdatePlayers(ArrayList<MPlayerUpdate> players) {
        try {
            tempPlace = g.place;
            if (tempPlace == null) {
                return;
            }
            Player plr;
            for (MPlayerUpdate pUp : players) {
                for (int p = 1; p < tempPlace.playersLength; p++) {
                    if (pUp.getId() == g.players[p].ID) {
                        plr = g.players[p];
                        plr.updates[plr.lastAdded] = pUp;
                        if (plr.lastAdded == 3) {
                            plr.lastAdded = 0;
                        } else {
                            plr.lastAdded++;
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
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
            isChanged[2] = true;
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    @Override
    public synchronized void playerUpdate(PacketMPlayerUpdate p) {
        try {
            tempPlace = g.place;
            if (tempPlace == null) {
                return;
            }
            Player plr;
            for (int i = 0; i < tempPlace.playersLength; i++) {
                if (p.up().getId() == g.players[i].ID) {
                    plr = g.players[i];
                    plr.updates[plr.lastAdded] = p.up();
                    if (plr.lastAdded == 3) {
                        plr.lastAdded = 0;
                    } else {
                        plr.lastAdded++;
                    }
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    int maxNrMobs; // temp for testing!

    @Override
    public synchronized void initializeChanges() {
        changes[0] = () -> {
            try {
                for (int i = 0; i < newPlayers.length; i++) {
                    if (newPlayers[i] != null) {
                        NewMPlayer temp = newPlayers[i];
                        System.out.println("Adding player with ID: " + temp.getId() + " - " + temp.getName());
                        g.players[tempPlace.playersLength].initialize(4, 4, 56, 56, tempPlace, temp.getX(), temp.getY());
                        g.players[tempPlace.playersLength].ID = temp.getId();
                        g.players[tempPlace.playersLength].setName(temp.getName());
                        tempPlace.players[tempPlace.playersLength] = g.players[tempPlace.playersLength];
                        Map m = tempPlace.getMapById(newPlayers[i].getMapId());
                        g.players[tempPlace.playersLength].setMapNotChange(m);
                        m.addObject(g.players[tempPlace.playersLength]);
                        if (server != null) {
                            server.findPlayer(temp.getId()).setPlayer(g.players[tempPlace.playersLength]);
                        }
                        tempPlace.playersLength++;
                        newPlayers[i] = null;
                    }
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        };
        changes[1] = new change() {
            @Override
            public void doIt() {
                try {
                    for (int i = 0; i < removeIDs.length; i++) {
                        for (int p = 1; p < tempPlace.playersLength; p++) {
                            if (g.players[p].ID == removeIDs[i]) {
                                ((Player) tempPlace.players[p]).setPlaceToNull();
                                tempPlace.players[p].getMap().deleteObject(tempPlace.players[p]);
                                if (p != tempPlace.playersLength - 1) {
                                    Player tempG = g.players[tempPlace.playersLength - 1];
                                    GameObject tempP = tempPlace.players[tempPlace.playersLength - 1];
                                    g.players[tempPlace.playersLength - 1] = g.players[p];
                                    tempPlace.players[tempPlace.playersLength - 1] = tempPlace.players[p];
                                    g.players[p] = tempG;
                                    tempPlace.players[p] = tempP;
                                }
                                tempPlace.playersLength--;
                                removeIDs[i] = 0;
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("ERROR: " + e.getMessage());
                }
            }
        };
        changes[2] = () -> {
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
//                    maxNrMobs = maxNrMobs > mobs.size() ? maxNrMobs : mobs.size();
//                    System.out.println("Max Updating nr of Mobs: " + maxNrMobs);
                for (MobUpdate mUp : mobs) {
                    found = false;
                    Mob mob;
                    for (Iterator<Mob> it = map.getSolidMobs().iterator(); it.hasNext();) {
                        mob = it.next();
                        if (mUp.getId() == mob.ID) {
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
                            Mob mob = new MyMob(newMobs[i].getX(), newMobs[i].getY(), 0, 8, 128, 112, 4, 512, "rabbit", tempPlace, true, newMobs[i].getId());
                            map.addObject(mob);
                            mob.setMapNotChange(map);
                            newMobs[i] = null;
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        };
    }

    @Override
    public synchronized void update() {
        tempPlace = g.place;
        if (tempPlace == null) {
            return;
        }
        for (int i = 0; i < isChanged.length; i++) {
            if (isChanged[i]) {
                isChanged[i] = false;
                changes[i].doIt();
            }
        }
    }

    @Override
    public synchronized Player getPlayerByID(byte id) {
        for (Player pl : g.players) {
            if (pl.ID == id) {
                return pl;
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
    }
}
