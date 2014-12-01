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
import game.gameobject.Player;
import net.GameClient;
import net.GameServer;
import net.NewMPlayer;
import net.packets.PacketInput;
import net.packets.PacketMPlayerUpdate;

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
            server.Stop();
            server = null;
        } else {
            g.runClient();
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
    public synchronized void playerUpdate(PacketMPlayerUpdate pl) {
        if (g.place != null) {
            for (int i = 0; i < g.place.playersLength; i++) {
                if (g.players[i].id == pl.getId()) {
                    plUps[i] = pl;
                    break;
                }
            }
            isChanged[2] = true;
        }
    }

    @Override
    public synchronized void updatePlayersInput(Player pl, PacketInput input) {
        pl.ctrl.setInput(input.inputs());
    }

    @Override
    public void initChanges() {
        changes[0] = new change() {
            @Override
            public void change() {
                for (int i = 0; i < newPls.length; i++) {
                    if (newPls[i] != null) {
                        System.out.println("Adding player: " + newPls[i].getId() + " " + newPls[i].getName());
                        g.players[g.place.playersLength].init(4, 4, 56, 56, 64, 64, g.place, newPls[i].getX(), newPls[i].getY());
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
            public void change() {
                for (PacketMPlayerUpdate plUp : plUps) {
                    if (plUp != null) {
                        for (int p = 0; p < g.place.playersLength; p++) {
                            if (plUp.getId() == g.players[p].id) {
                                g.players[p].setX(Methods.RoundHU((g.settings.SCALE) * plUp.getX()));
                                g.players[p].setY(Methods.RoundHU((g.settings.SCALE) * plUp.getY()));
                                if (g.players[p].getCam() != null) {
                                    g.players[p].getCam().update();
                                }
                                if (plUp.isJumping()) {
                                    g.players[p].setisJumping(true);
                                }
                                g.players[p].setEmits(plUp.isEmits());
                                break;
                            }
                        }
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
            server.Stop();
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
