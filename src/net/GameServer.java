/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net;

import net.packets.PacketMessage;
import net.packets.PacketJoinResponse;
import net.packets.PacketJoinRequest;
import net.packets.PacketAddMPlayer;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import engine.Methods;
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
    private final Player pl;
    private final GameOnline game;
    private final float SCALE;
    public boolean isRunning;
    private MPlayer[] MPlayers = new MPlayer[4];
    private boolean[] isConnected = new boolean[4];
    private int nrPlayers = 0;
    private byte id = 0;

    public GameServer(final Player pl, final GameOnline game) {
        this.pl = pl;
        this.game = game;
        this.SCALE = game.g.settings.SCALE;
        this.server = new Server();

        try {
            Log.set(Log.LEVEL_DEBUG);
            KryoUtil.registerServerClasses(server);
            server.addListener(new Listener() {
                @Override
                public void connected(Connection connection) {
                    try {
                        System.out.println("Received a connection from " + connection.getRemoteAddressTCP().getHostString() + " (" + connection.getID() + ")");
                    } catch (Exception e) {
                        cleanUp(e);
                    }
                }

                @Override
                public void disconnected(Connection connection) {
                    try {
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
                                MPlayers[i] = null;
                                if (i < nrPlayers - 1) {
                                    for (int j = i; j < nrPlayers - 1; j++) {
                                        MPlayers[j] = MPlayers[j + 1];
                                    }
                                }
                                nrPlayers--;
                            }
                        }
                        game.removePlayer(id);
                        for (i = 1; i < nrPlayers; i++) {
                            MPlayers[i].getConnection().sendTCP(new PacketRemoveMPlayer(id));
                        }
                        System.out.println(name + " (" + id + ") disconnected!");
                    } catch (Exception e) {
                        cleanUp(e);
                    }
                }

                @Override
                public void received(Connection connection, Object obj) {
                    try {
                        if (obj instanceof PacketMessage) {
                            connection.sendUDP(new PacketMessage("Hello Client!"));
                        } else if (obj instanceof PacketJoinRequest) {
                            if (nrPlayers < 4) {
                                makeSureIdIsUnique();
                                NewMPlayer nmp = AddNewPlayer(((PacketJoinRequest) obj).getName(), connection);
                                connection.sendTCP(new PacketJoinResponse(id++, MPlayers[nrPlayers].getX(), MPlayers[nrPlayers].getY()));
                                sendToAll(nmp);
                                sendToNew(connection);
                                nrPlayers++;
                                System.out.println(MPlayers[nrPlayers - 1].getName() + " (" + MPlayers[nrPlayers - 1].getId() + ") connected");
                            } else {
                                connection.sendTCP(new PacketJoinResponse((byte) -1));
                            }
                        } else if (obj instanceof PacketMPlayerUpdate) {
                            MPlayer curPl = findPlayer(((PacketMPlayerUpdate) obj).getId());
                            if (curPl != null) {
                                curPl.Update(((PacketMPlayerUpdate) obj).getX(), ((PacketMPlayerUpdate) obj).getY(), 1);
                                PacketMPlayerUpdate mpup = new PacketMPlayerUpdate(curPl);
                                sendToAllButOwner(mpup, ((PacketMPlayerUpdate) obj).getId());
                                game.playerUpdate((((PacketMPlayerUpdate) obj)));
                            }
                        }
//                        else if (obj instanceof PacketInput) {
//                            MPlayer curPl = findPlayer(((PacketInput) obj).getId());
//                            if (curPl != null) {
//                                curPl.inGame().ctrl.setInput(((PacketInput) obj).inputs());
//                                curPl.Update(curPl.inGame().getX(), curPl.inGame().getY(), SCALE);
//                                PacketMPlayerUpdate mpup = new PacketMPlayerUpdate(curPl);
//                                sendToAllButOwner(mpup, ((PacketInput) obj).getId());
//                            }
//                        }
                    } catch (Exception e) {
                        cleanUp(e);
                    }
                }

            });
            try {
                server.bind(KryoUtil.TCP_PORT, KryoUtil.UDP_PORT);
            } catch (IOException ex) {
                Methods.Error(ex.getMessage() + "!");
                return;
            }

            MPlayers[0] = new MPlayer("Server", id, null);
            MPlayers[0].setPosition(128 + id * 128, 256);
            MPlayers[0].setPlayer(pl);
            pl.setName(MPlayers[0].getName());
            pl.id = id++;
            pl.setX(((float) MPlayers[0].getX()) * SCALE);
            pl.setY(((float) MPlayers[0].getY()) * SCALE);
            nrPlayers++;

            isRunning = true;
            System.out.println("Server started!");
        } catch (Exception e) {
            cleanUp(e);
        }
    }

    public void Start() {
        server.start();
    }

    public void Close() {
        server.stop();
        server.close();
    }

    public void sendPlayerUpdate(byte id, int x, int y) {
        try {
            MPlayers[0].Update(x, y, SCALE);
            PacketMPlayerUpdate mpup = new PacketMPlayerUpdate(MPlayers[0]);
            for (int i = 1; i < nrPlayers; i++) {
                MPlayers[i].getConnection().sendTCP(mpup);
            }
        } catch (Exception e) {
            cleanUp(e);
        }
    }

    public MPlayer findPlayer(byte id) {
        for (int i = 1; i < nrPlayers; i++) {
            if (MPlayers[i].getId() == id) {
                return MPlayers[i];
            }
        }
        return null;
    }

    private void cleanUp(Exception e) {
        game.g.endGame();
        Methods.Exception(e);
    }

    private void makeSureIdIsUnique() {
        for (int j = 0; j < nrPlayers; j++) {
            for (int i = 0; i < nrPlayers; i++) {
                if (id == MPlayers[i].getId()) {
                    id++;
                }
            }
        }
    }

    private NewMPlayer AddNewPlayer(String name, Connection connection) {
        MPlayers[nrPlayers] = new MPlayer(name, id, connection);
        MPlayers[nrPlayers].setPosition(128 + id * 128, 256);
        NewMPlayer nmp = new NewMPlayer(MPlayers[nrPlayers]);
        game.addPlayer(nmp);
        return nmp;
    }

    private void sendToAll(NewMPlayer nmp) {
        for (int i = 1; i < nrPlayers; i++) {   // send NewPlayer to All
            MPlayers[i].getConnection().sendTCP(new PacketAddMPlayer(nmp));
        }
    }

    private void sendToAll(PacketMPlayerUpdate mpup) {
        for (int i = 1; i < nrPlayers; i++) {
            MPlayers[i].getConnection().sendTCP(mpup);
        }
    }

    private void sendToAllButOwner(PacketMPlayerUpdate mpup, int id) {
        for (int i = 1; i < nrPlayers; i++) {
            if (MPlayers[i].getId() != id) {
                MPlayers[i].getConnection().sendTCP(mpup);
            }
        }
    }

    private void sendToNew(Connection connection) {
        for (int i = 0; i < nrPlayers; i++) {   // send Players to NewPlayer
            connection.sendTCP(new PacketAddMPlayer(new NewMPlayer(MPlayers[i])));
        }
    }
}
