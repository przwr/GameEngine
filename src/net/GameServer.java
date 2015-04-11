/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net;

import net.packets.NewMPlayer;
import net.packets.PacketMessage;
import net.packets.PacketJoinResponse;
import net.packets.PacketJoinRequest;
import net.packets.PacketAddMPlayer;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import engine.Delay;
import engine.Methods;
import game.gameobject.Mob;
import game.gameobject.Player;
import java.io.IOException;
import net.packets.PacketMPlayerUpdate;
import net.packets.PacketRemoveMPlayer;

/**
 *
 * @author przemek
 */
public class GameServer {

    private final Server server;
    private final GameOnline game;
    private final int scopeX, scopeY;
    public boolean isRunning;
    private MPlayer temp;
    private Player tmpInGame;
    private MPlayer[] MPlayers = new MPlayer[4];
    private boolean[] isConnected = new boolean[4];
    private int nrPlayers = 0;
    private byte id = 0;
    private Delay delay;
    private Thread thread;

    public GameServer(final Player player, final GameOnline game) {
        this.game = game;
        this.scopeX = 2400;
        this.scopeY = 1500;
        delay = new Delay(50);
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
                            if (MPlayers[i].getConnection() == c) {
                                isConnected[i - 1] = true;
                            }
                        }
                    }
                    for (i = 1; i < nrPlayers; i++) {
                        if (!isConnected[i - 1]) {
                            name = MPlayers[i].getName();
                            id = MPlayers[i].getId();
                            nrPlayers--;
                            MPlayers[i] = null;
                            if (i < nrPlayers + 1) {
                                for (int j = i; j < nrPlayers; j++) {
                                    MPlayers[j] = MPlayers[j + 1];
                                }
                            }
                        }
                    }
                    game.removePlayer(id);
                    for (i = 1; i < nrPlayers; i++) {
                        MPlayers[i].getConnection().sendTCP(new PacketRemoveMPlayer(id));
                    }
                    System.out.println(name + " (" + id + ") disconnected!");
                }

                @Override
                public synchronized void received(Connection connection, Object obj) {
                    if (obj instanceof PacketMPlayerUpdate) {
                        PacketMPlayerUpdate pmpu = (PacketMPlayerUpdate) obj;
                        game.playerUpdate(pmpu);
                        MPlayer curPl = findPlayer(pmpu.up().getId());
                        if (curPl != null) {
                            curPl.update(pmpu.up().getMapId(), pmpu.up().getX(), pmpu.up().getY());
                            for (int i = 1; i < nrPlayers; i++) {
                                if (MPlayers[i].getId() != pmpu.up().getId()) {
                                    MPlayers[i].getPU().PlayerUpdate(pmpu.up());
                                }
                            }
                        }
                    } else if (obj instanceof PacketMessage) {
                        connection.sendUDP(new PacketMessage("Hello Client!"));
                    } else if (obj instanceof PacketJoinRequest) {
                        if (nrPlayers < 4) {
                            makeSureIdIsUnique();
                            NewMPlayer nmp = addNewPlayer(MPlayers[0].getMapId(), ((PacketJoinRequest) obj).getName(), connection);
                            connection.sendTCP(new PacketJoinResponse(MPlayers[0].getMapId(), id++, MPlayers[0].getX(), MPlayers[0].getY()));
                            sendToAll(nmp);
                            sendToNew(connection);
                            nrPlayers++;
                            if (MPlayers[nrPlayers - 1] != null) {
                                System.out.println(MPlayers[nrPlayers - 1].getName() + " (" + MPlayers[nrPlayers - 1].getId() + ") connected");
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
                Methods.error(ex.getMessage() + "!");
                return;
            }
            MPlayers[0] = new MPlayer((short) 0, id, "Server", null);
            MPlayers[0].setPosition(128 + id * 128, 256);
            MPlayers[0].setPlayer(player);
            player.setName(MPlayers[0].getName());
            player.playerID = id++;
            player.setX(MPlayers[0].getX());
            player.setY(MPlayers[0].getY());
            nrPlayers++;
            isRunning = true;
            System.out.println("Server started!");
        } catch (Exception e) {
            cleanUp(e);
        }
    }

    public synchronized void Start() {
        thread = new Thread(server, "Server");
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
            MPlayers[0].update(mapId, x, y);
            int mobX, mobY;
            for (int i = 1; i < nrPlayers; i++) {
                temp = MPlayers[i];
                if (temp != null) {
                    tmpInGame = temp.inGame();
                    if (tmpInGame != null) {
                        for (Mob mob : game.game.getPlace().getMapById(temp.getMapId()).getSolidMobs()) {
                            mobX = mob.getX();
                            mobY = mob.getY();
                            if (Math.abs(mobX - temp.inGame().getX()) < scopeX && Math.abs(mobY - temp.inGame().getY()) < scopeY) {
                                temp.getPU().MobUpdate(mob.mobID, mobX, mobY);
                            }
                        }
                    }
                    temp.getPU().playerUpdate(MPlayers[0], isEmits, isHop);
                }
            }
            if (delay.isOver()) {
                for (int i = 1; i < nrPlayers; i++) {
                    temp = MPlayers[i];
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

    public synchronized MPlayer findPlayer(byte playerID) {
        for (int i = 1; i < nrPlayers; i++) {
            if (MPlayers[i] != null && MPlayers[i].getId() == playerID) {
                return MPlayers[i];
            }
        }
        return null;
    }

    private synchronized void cleanUp(Exception exception) {
        isRunning = false;
        Close();
        game.game.endGame();
        Methods.exception(exception);
    }

    private synchronized void makeSureIdIsUnique() {
        for (int j = 0; j < nrPlayers; j++) {
            for (int i = 0; i < nrPlayers; i++) {
                if (id == MPlayers[i].getId()) {
                    id++;
                }
            }
        }
    }

    private synchronized NewMPlayer addNewPlayer(short mapId, String name, Connection connection) {
        MPlayers[nrPlayers] = new MPlayer(mapId, id, name, connection);
        MPlayers[nrPlayers].setPosition(128 + id * 128, 256);
        NewMPlayer nmp = new NewMPlayer(MPlayers[nrPlayers]);
        game.addPlayer(nmp);
        return nmp;
    }

    private synchronized void sendToAll(NewMPlayer nmp) {
        for (int i = 1; i < nrPlayers; i++) {   // send NewPlayer to All
            MPlayers[i].getConnection().sendTCP(new PacketAddMPlayer(nmp));
        }
    }

    private synchronized void sendToNew(Connection connection) {
        for (int i = 0; i < nrPlayers; i++) {   // send Players to NewPlayer
            connection.sendTCP(new PacketAddMPlayer(new NewMPlayer(MPlayers[i])));
        }
    }
}
