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
        for (MPlayerUpdate plUp : players) {
            for (int p = 0; p < g.place.playersLength; p++) {
                if (plUp.getId() == g.players[p].id) {
                    plr = g.players[p];
                    plr.isReady = false;
                    plr.prevX = Methods.RoundHU((g.settings.SCALE) * plUp.getX());
                    plr.prevY = Methods.RoundHU((g.settings.SCALE) * plUp.getY());
                    int xd = plr.prevX - plr.getX();
                    int yd = plr.prevY - plr.getY();
                    plr.setX(plr.prevX);
                    plr.setY(plr.prevY);
//                    plr.canMove(xd, yd);
                    plr.isReady = true;
                    if (plUp.isHop()) {
                        plr.setIsJumping(true);
                    }
                    plr.setEmits(plUp.isEmits());
                    break;

//                    g.players[p].setX(Methods.RoundHU((g.settings.SCALE) * plUp.getX()));
//                    g.players[p].setY(Methods.RoundHU((g.settings.SCALE) * plUp.getY()));
//                    g.players[p].upDepth();
//                    if (plUp.isHop()) {
//                        g.players[p].setIsJumping(true);
//                    }
//                    g.players[p].setEmits(plUp.isEmits());
//                    break;
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
                    mob.isReady = false;
                    found = true;
                    mob.prevX = (Methods.RoundHU((g.settings.SCALE) * mUp.getX()));
                    mob.prevY = (Methods.RoundHU((g.settings.SCALE) * mUp.getY()));
                    int xd = mob.prevX - mob.getX();
                    int yd = mob.prevY - mob.getY();
                    Player p = mob.getCollided(xd, yd);
                    if (p != null) {
                        p.setX(p.getX() + xd);
                        p.setY(p.getY() + yd);
                        p.upDepth();
                    }
                    mob.setX(mob.prevX);
                    mob.setY(mob.prevY);
                    mob.upDepth();
                    mob.dX = mUp.delsX();
                    mob.dY = mUp.delsY();
                    mob.dCount = 0;
                    mob.isReady = true;
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
    public synchronized void playerUpdate(PacketMPlayerUpdate pl) {
        Player plr;
        if (g.place != null) {
            for (int p = 0; p < g.place.playersLength; p++) {
                if (pl.MPU().getId() == g.players[p].id) {
                    plr = g.players[p];
                    plr.isReady = false;
                    plr.prevX = Methods.RoundHU((g.settings.SCALE) * pl.MPU().getX());
                    plr.prevY = Methods.RoundHU((g.settings.SCALE) * pl.MPU().getY());
                    int xd = plr.prevX - plr.getX();
                    int yd = plr.prevY - plr.getY();
                    plr.setX(plr.prevX);
                    plr.setY(plr.prevY);
//                    plr.canMove(xd, yd);
                    if (pl.MPU().isHop()) {
                        plr.setIsJumping(true);
                    }
                    plr.setEmits(pl.MPU().isEmits());
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
                        g.place.addObj(new MyMob(newMob[i].getX(), newMob[i].getY(), 0, 8, 128, 112, 4, 512, "rabbit", g.place, true, newMob[i].getId()));
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
