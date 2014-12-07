/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myGame;

import engine.Methods;
import game.Game;
import net.GameOnline;
import game.gameobject.GameObject;
import game.gameobject.Mob;
import game.gameobject.Player;
import java.util.ArrayList;
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
    public void runServer() {
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
    public void joinServer() {
        client = new GameClient(g.players[0], this, g.settings.serverIP);
        if (client.isConnected) {
            g.runClient();
            g.mode = 1;
        } else {
            client = null;
        }
    }

    @Override
    public synchronized void addPlayer(NewMPlayer pl) {
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
        if (g.place != null) {
            UpdateMobs(pl.mobs());
            UpdatePlayers(pl.players());
        }
    }

    private synchronized void UpdatePlayers(ArrayList<MPlayerUpdate> players) {
        Player plr;
        for (MPlayerUpdate pUp : players) {
            for (int p = 0; p < g.place.playersLength; p++) {
                if (pUp.getId() == g.players[p].id) {
                    plr = g.players[p];
                    if (plr.isFirst()) {
                        plr.up2 = pUp;
                    } else {
                        plr.up1 = pUp;
                    }
                    plr.setIsFirst(!plr.isFirst());
                    plr.dCount = -1;
//                    plr.setX(pUp.getX() * g.place.SCALE());
//                    plr.setY(pUp.getY()* g.place.SCALE());
//                    plr.upDepth();
                    if (pUp.isHop()) {
                        plr.setIsJumping(true);
                    }
                    plr.setEmits(pUp.isEmits());
                    break;
                }
            }
        }
    }

    private synchronized void UpdateMobs(ArrayList<MobUpdate> mobs) {
        boolean found;
        for (MobUpdate mUp : mobs) {
            found = false;
            for (Mob mob : g.place.sMobs) {
                if (mUp.getId() == mob.id) {
                    if (mob.isFirst()) {
                        mob.up2 = mUp;
                    } else {
                        mob.up1 = mUp;
                    }
                    mob.setIsFirst(!mob.isFirst());
                    mob.dCount = -1;
                    found = true;
                    break;
                }
            }
            if (!found) {
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
                isChanged[2] = true;
            }
        }
    }

    @Override
    public synchronized void playerUpdate(PacketMPlayerUpdate p) {
        Player plr;
        if (g.place != null) {
            for (int i = 0; i < g.place.playersLength; i++) {
                if (p.Up().getId() == g.players[i].id) {
                    plr = g.players[i];
                    if (plr.isFirst()) {
                        plr.up2 = p.Up();
                    } else {
                        plr.up1 = p.Up();
                    }
                    plr.setIsFirst(!plr.isFirst());
                    plr.dCount = -1;
                    if (p.Up().isHop()) {
                        plr.setIsJumping(true);
                    }
                    plr.setEmits(p.Up().isEmits());
                    break;
                }
            }
        }
    }

    @Override
    public void initChanges() {
        changes[0] = new change() {
            @Override
            public void change() {
                for (int i = 0; i < newPls.length; i++) {
                    if (newPls[i] != null) {
                        System.out.println("Adding player with ID: " + newPls[i].getId() + " - " + newPls[i].getName());
                        g.players[g.place.playersLength].init(4, 4, 56, 56, g.place, newPls[i].getX(), newPls[i].getY());
                        g.players[g.place.playersLength].id = newPls[i].getId();
                        g.players[g.place.playersLength].setName(newPls[i].getName());
                        g.place.players[g.place.playersLength] = g.players[g.place.playersLength];
                        if (server != null) {
                            server.findPlayer(newPls[i].getId()).setPlayer(g.players[g.place.playersLength]);
                        }
                        g.place.playersLength++;
                        newPls[i] = null;
                    }
                }
            }
        };
        changes[1] = new change() {
            @Override
            public void change() {
                for (int i = 0; i < removeIDs.length; i++) {
                    for (int p = 1; p < g.place.playersLength; p++) {
                        if (g.players[p].id == removeIDs[i]) {
                            ((Player) g.place.players[p]).setPlaceToNull();
                            g.place.deleteObj(g.place.players[p]);
                            if (p != g.place.playersLength - 1) {
                                Player tempG = g.players[g.place.playersLength - 1];
                                GameObject tempP = g.place.players[g.place.playersLength - 1];
                                g.players[g.place.playersLength - 1] = g.players[p];
                                g.place.players[g.place.playersLength - 1] = g.place.players[p];
                                g.players[p] = tempG;
                                g.place.players[p] = tempP;
                            }
                            g.place.playersLength--;
                            removeIDs[i] = 0;
                        }
                    }
                }
            }
        };
        changes[2] = new change() {
            @Override
            public synchronized void change() {
                for (int i = 0; i < newMob.length; i++) {
                    if (newMob[i] != null) {
                        System.out.println("Adding Mob with ID: " + newMob[i].getId());
                        Mob mob = new MyMob(newMob[i].getX(), newMob[i].getY(), 0, 8, 128, 112, 4, 512, "rabbit", g.place, true, newMob[i].getId());
                        mob.upDepth();
                        g.place.addObj(mob);
                        newMob[i] = null;
                    }
                }
            }
        };
    }

    @Override
    public void up() {
        if (g.place != null) {
            for (int i = 0; i < isChanged.length; i++) {
                if (isChanged[i]) {
                    isChanged[i] = false;
                    changes[i].change();
                }
            }
        }
    }

    @Override
    public void cleanUp() {
        if (server != null) {
            server.Close();
            server = null;
        }
        if (client != null) {
            client.Close();
            client = null;
        }
    }

    @Override
    public Player getPlayer(byte id) {
        for (Player pl : g.players) {
            if (pl.id == id) {
                return pl;
            }
        }
        return null;
    }
}
