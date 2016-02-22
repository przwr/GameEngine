/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import engine.utilities.Delay;
import engine.utilities.ErrorHandler;
import game.gameobject.entities.Mob;
import game.gameobject.entities.Player;
import net.packets.*;

import java.io.IOException;

/**
 * @author przemek
 */
public class GameServer {

    private final Server server;
    private final GameOnline game;
    private final MultiPlayer[] MultiPlayers = new MultiPlayer[4];
    private final boolean[] isConnected = new boolean[4];
    private final Delay delay;
    //    private final int scopeX, scopeY;
    public boolean isRunning;
    private int nrPlayers = 0;
    private byte id = 0;

    public GameServer(final Player player, final GameOnline game) {
        this.game = game;
//        this.scopeX = 2400;
//        this.scopeY = 1500;
        delay = Delay.createInMilliseconds(50, true);
        delay.terminate();
        Server tempServer = null;
        try {
            tempServer = new Server(99999999, 99999999);
        } catch (Exception exception) {
            cleanUp(exception);
        }
        this.server = tempServer;
        try {
            Log.set(Log.LEVEL_DEBUG);
            KryoUtil.registerServerClasses(server);
            server.addListener(new Listener() {
                @Override
                public synchronized void connected(Connection connection) {
                    System.out.println("Received a connection from " + connection.getRemoteAddressTCP().getHostString() + " (" + connection.getID() + ")");
                }

                @Override
                public synchronized void disconnected(Connection connection) {
                    int i;
                    String name = "Client";
                    byte id = -1;
                    for (i = 0; i < isConnected.length; i++) {
                        isConnected[i] = false;
                    }
                    for (Connection c : server.getConnections()) {
                        for (i = 1; i < nrPlayers; i++) {
                            if (MultiPlayers[i].getConnection() == c) {
                                isConnected[i - 1] = true;
                            }
                        }
                    }
                    for (i = 1; i < nrPlayers; i++) {
                        if (!isConnected[i - 1]) {
                            name = MultiPlayers[i].getName();
                            id = MultiPlayers[i].getId();
                            nrPlayers--;
                            MultiPlayers[i] = null;
                            if (i < nrPlayers + 1) {
                                System.arraycopy(MultiPlayers, i + 1, MultiPlayers, i, nrPlayers - i);
                            }
                        }
                    }
                    game.removePlayer(id);
                    for (i = 1; i < nrPlayers; i++) {
                        MultiPlayers[i].getConnection().sendTCP(new PacketRemoveMultiPlayer(id));
                    }
                    System.out.println(name + " (" + id + ") disconnected!");
                }

                @Override
                public synchronized void received(Connection connection, Object obj) {
                    if (obj instanceof PacketMultiPlayerUpdate) {
                        PacketMultiPlayerUpdate pmPu = (PacketMultiPlayerUpdate) obj;
                        game.playerUpdate(pmPu);
                        MultiPlayer curPl = findPlayer(pmPu.up().getId());
                        if (curPl != null) {
                            curPl.update(pmPu.up().getMapId(), pmPu.up().getX(), pmPu.up().getY());
                            for (int i = 1; i < nrPlayers; i++) {
                                if (MultiPlayers[i].getId() != pmPu.up().getId()) {
                                    MultiPlayers[i].getPU().PlayerUpdate(pmPu.up());
                                }
                            }
                        }
                    } else if (obj instanceof PacketMessage) {
                        connection.sendUDP(new PacketMessage("Hello Client!"));
                    } else if (obj instanceof PacketJoinRequest) {
                        if (nrPlayers < 4) {
                            makeSureIdIsUnique();
                            NewMultiPlayer nmp = addNewPlayer(MultiPlayers[0].getMapId(), ((PacketJoinRequest) obj).getName(), connection);
                            connection.sendTCP(new PacketJoinResponse(MultiPlayers[0].getMapId(), id++, MultiPlayers[0].getX(), MultiPlayers[0].getY()));
                            sendToAll(nmp);
                            sendToNew(connection);
                            nrPlayers++;
                            if (MultiPlayers[nrPlayers - 1] != null) {
                                System.out.println(MultiPlayers[nrPlayers - 1].getName() + " (" + MultiPlayers[nrPlayers - 1].getId() + ") connected");
                            }
                        } else {
                            connection.sendTCP(new PacketJoinResponse((byte) -1));
                        }
                    }
                }

            });
            try {
                server.bind(KryoUtil.TCP_PORT, KryoUtil.UDP_PORT);
            } catch (IOException ex) {
                ErrorHandler.error(ex.getMessage() + "!");
                return;
            }
            MultiPlayers[0] = new MultiPlayer((short) 0, id, "Server", null);
            MultiPlayers[0].setPosition(128 + id * 128, 256);
            MultiPlayers[0].setPlayer(player);
            player.setName(MultiPlayers[0].getName());
            player.playerID = id++;
            player.setPosition(MultiPlayers[0].getX(), MultiPlayers[0].getY());
            nrPlayers++;
            isRunning = true;
            System.out.println("Server started!");
        } catch (Exception e) {
            cleanUp(e);
        }
    }

    public synchronized void Start() {
        Thread thread = new Thread(server, "Server");
        try {
            thread.start();
        } catch (Exception e) {
            cleanUp(e);
        }
    }

    public synchronized void Close() {
        server.stop();
        server.close();
    }

    public synchronized void sendUpdate(short mapId, int x, int y, boolean isEmits, boolean isHop) {
        try {
            MultiPlayers[0].update(mapId, x, y);
            int mobX, mobY;
            MultiPlayer temp;
            for (int i = 1; i < nrPlayers; i++) {
                temp = MultiPlayers[i];
                if (temp != null) {
                    Player tempInGame = temp.inGame();
                    if (tempInGame != null) {
                        for (Mob mob : game.game.getPlace().getMapById(temp.getMapId()).getArea(tempInGame.getX(), tempInGame.getY()).getNearSolidMobs()) {
                            mobX = mob.getX();
                            mobY = mob.getY();
                            temp.getPU().MobUpdate(mob.mobID, mobX, mobY);
//                            if (Math.abs(mobX - temp.inGame().getX()) < scopeX && Math.abs(mobY - temp.inGame().getY()) < scopeY) {
//
//                            }
                        }
                    }
                    temp.getPU().playerUpdate(MultiPlayers[0], isEmits, isHop);
                }
            }
            if (delay.isOver()) {
                for (int i = 1; i < nrPlayers; i++) {
                    temp = MultiPlayers[i];
                    if (temp != null) {
                        temp.sendUpTCP();
                    }
                }
                delay.start();
            }
        } catch (Exception e) {
            cleanUp(e);
        }
    }

    public synchronized MultiPlayer findPlayer(byte playerID) {
        for (int i = 1; i < nrPlayers; i++) {
            if (MultiPlayers[i] != null && MultiPlayers[i].getId() == playerID) {
                return MultiPlayers[i];
            }
        }
        return null;
    }

    private synchronized void cleanUp(Exception exception) {
        isRunning = false;
        Close();
        game.game.endGame();
        ErrorHandler.exception(exception);
    }

    private synchronized void makeSureIdIsUnique() {
        for (int j = 0; j < nrPlayers; j++) {
            for (int i = 0; i < nrPlayers; i++) {
                if (id == MultiPlayers[i].getId()) {
                    id++;
                }
            }
        }
    }

    private synchronized NewMultiPlayer addNewPlayer(short mapId, String name, Connection connection) {
        MultiPlayers[nrPlayers] = new MultiPlayer(mapId, id, name, connection);
        MultiPlayers[nrPlayers].setPosition(128 + id * 128, 256);
        NewMultiPlayer nmp = new NewMultiPlayer(MultiPlayers[nrPlayers]);
        game.addPlayer(nmp);
        return nmp;
    }

    private synchronized void sendToAll(NewMultiPlayer nmp) {
        for (int i = 1; i < nrPlayers; i++) {   // send NewPlayer to All
            MultiPlayers[i].getConnection().sendTCP(new PacketAddMultiPlayer(nmp));
        }
    }

    private synchronized void sendToNew(Connection connection) {
        for (int i = 0; i < nrPlayers; i++) {   // send Players to NewPlayer
            connection.sendTCP(new PacketAddMultiPlayer(new NewMultiPlayer(MultiPlayers[i])));
        }
    }
}
