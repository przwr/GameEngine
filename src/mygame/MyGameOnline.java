/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

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
        server = new GameServer(g.players[0], this);
        server.Start();
        if (!server.isRunning) {
            server.Close();
            server = null;
        } else {
            g.runServer();
            g.mode = 1;
        }
    }

    @Override
    public synchronized void joinServer() {
        client = new GameClient(g.players[0], this, g.settings.serverIP);
        if (client.isConnected) {
            g.runClient();
            g.mode = 1;
            g.players[0].changeMap(g.place.getMapById(client.tempMapId));
        } else {
            client.Close();
            client = null;
        }
    }

    @Override
    public synchronized void addPlayer(NewMPlayer pl
    ) {
        if (newPls[0] == null) {
            newPls[0] = pl;
        } else if (newPls[1] == null) {
            newPls[1] = pl;
        } else if (newPls[2] == null) {
            newPls[2] = pl;
        }
        isChanged[0] = true;
    }

    @Override
    public synchronized void removePlayer(byte id) {
        if (removeIDs[0] == 0) {
            removeIDs[0] = id;
        } else if (removeIDs[1] == 0) {
            removeIDs[1] = id;
        } else if (removeIDs[1] == 0) {
            removeIDs[2] = id;
        }
        isChanged[1] = true;
    }

    @Override
    public synchronized void update(PacketUpdate pl) {
        if (g.started) {
            UpdateMobs(pl.mobs());
            UpdatePlayers(pl.players());
        }
    }

    private synchronized void UpdatePlayers(ArrayList<MPlayerUpdate> players) {
        tempPlace = g.place;
        if (tempPlace == null) {
            return;
        }
        Player plr;
        for (MPlayerUpdate pUp : players) {
            for (int p = 1; p < tempPlace.playersLength; p++) {
                if (pUp.getId() == g.players[p].id) {
                    plr = g.players[p];
                    plr.ups[plr.lastAdded] = pUp;
                    if (plr.lastAdded == 3) {
                        plr.lastAdded = 0;
                    } else {
                        plr.lastAdded++;
                    }
                    break;
                }
            }
        }
    }

    private synchronized void UpdateMobs(ArrayList<MobUpdate> mobs) {
        if (!isMUps1) {
            mUps1 = mobs;
            isMUps1 = true;
        } else {
            mUps2 = mobs;
            isMUps1 = false;
        }
        isChanged[2] = true;
    }

    @Override
    public synchronized void playerUpdate(PacketMPlayerUpdate p) {
        tempPlace = g.place;
        if (tempPlace == null) {
            return;
        }
        Player plr;
        for (int i = 0; i < tempPlace.playersLength; i++) {
            if (p.up().getId() == g.players[i].id) {
                plr = g.players[i];
                plr.ups[plr.lastAdded] = p.up();
                if (plr.lastAdded == 3) {
                    plr.lastAdded = 0;
                } else {
                    plr.lastAdded++;
                }
                break;
            }
        }

    }

    @Override
    public synchronized void initChanges() {
        changes[0] = new change() {
            @Override
            public void doIt() {
                for (int i = 0; i < newPls.length; i++) {
                    if (newPls[i] != null) {
                        NewMPlayer temp = newPls[i];
                        System.out.println("Adding player with ID: " + temp.getId() + " - " + temp.getName());
                        g.players[tempPlace.playersLength].init(4, 4, 56, 56, tempPlace, temp.getX(), temp.getY());
                        g.players[tempPlace.playersLength].id = temp.getId();
                        g.players[tempPlace.playersLength].setName(temp.getName());
                        tempPlace.players[tempPlace.playersLength] = g.players[tempPlace.playersLength];
                        Map m = tempPlace.getMapById(newPls[i].getMapId());
                        g.players[tempPlace.playersLength].setMap(m);
                        m.addObj(g.players[tempPlace.playersLength]);
                        if (server != null) {
                            server.findPlayer(temp.getId()).setPlayer(g.players[tempPlace.playersLength]);
                        }
                        tempPlace.playersLength++;
                        newPls[i] = null;
                    }
                }
            }
        };
        changes[1] = new change() {
            @Override
            public void doIt() {
                for (int i = 0; i < removeIDs.length; i++) {
                    for (int p = 1; p < tempPlace.playersLength; p++) {
                        if (g.players[p].id == removeIDs[i]) {
                            ((Player) tempPlace.players[p]).setPlaceToNull();
                            tempPlace.players[p].getMap().deleteObj(tempPlace.players[p]);
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
            }
        };
        changes[2] = new change() {
            @Override
            public synchronized void doIt() {
                boolean found;
                boolean addNew = false;
                ArrayList<MobUpdate> mobs;
                if (isMUps1) {
                    mobs = mUps1;
                } else {
                    mobs = mUps2;
                }
                for (MobUpdate mUp : mobs) {
                    found = false;
                    Mob mob;
                    Map map = tempPlace.getMapById(mUp.getMapId());
                    for (Iterator<Mob> it = map.sMobs.iterator(); it.hasNext();) {
                        mob = it.next();
                        if (mUp.getId() == mob.id) {
                            mob.ups[mob.lastAdded] = mUp;
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
                        for (int i = 0; i < newMob.length; i++) {
                            if (newMob[i] != null && newMob[i].getId() == mUp.getId()) {
                                newMob[i] = mUp;
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            for (int i = 0; i < newMob.length; i++) {
                                if (newMob[i] == null) {
                                    newMob[i] = mUp;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (addNew) {
                    for (int i = 0; i < newMob.length; i++) {
                        if (newMob[i] != null) {
                            System.out.println("Adding Mob with ID: " + newMob[i].getId());
                            Mob mob = new MyMob(newMob[i].getX(), newMob[i].getY(), 0, 8, 128, 112, 4, 512, "rabbit", tempPlace, true, newMob[i].getId());
                            Map map = tempPlace.getMapById(newMob[i].getMapId());
                            map.addObj(mob);
                            mob.setMap(map);
                            newMob[i] = null;
                        }
                    }
                }
            }
        };
    }

    @Override
    public synchronized void up() {
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
            if (pl.id == id) {
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
