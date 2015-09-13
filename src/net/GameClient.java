/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import engine.utilities.Delay;
import engine.utilities.ErrorHandler;
import game.Settings;
import game.gameobject.entities.Player;
import net.packets.*;

import java.io.IOException;

/**
 * @author przemek
 */
public class GameClient {

    private final Client client;
    private final GameOnline game;
    private final Delay delay;
    public boolean isConnected;
    public short tempMapId = -1;
    private PacketMultiPlayerUpdate mpUp;
    private Connection server;

    public GameClient(final Player player, final GameOnline game, String IP) {
        this.game = game;
        delay = new Delay(20);
        delay.terminate();
        Client temp = null;
        try {
            temp = new Client(99999999, 99999999);
        } catch (Exception e) {
            cleanUp(e);
        }
        client = temp;

        try {
            Log.set(Log.LEVEL_DEBUG);
            KryoUtil.registerClientClass(client);
            client.start();
            client.addListener(new Listener() {
                @Override
                public void connected(Connection connection) {
                    PacketJoinRequest test = new PacketJoinRequest(player.getName());
                    client.sendTCP(test);
                }

                @Override
                public void received(Connection connection, Object obj) {
                    if (obj instanceof PacketUpdate) {
                        game.update((PacketUpdate) obj);
                    }
                    if (obj instanceof PacketMessage) {
                        System.out.println("Received from server: " + ((PacketMessage) obj).getMessage());
                    } else if (obj instanceof PacketAddMultiPlayer) {
                        game.addPlayer(((PacketAddMultiPlayer) obj).getPlayer());
                    } else if (obj instanceof PacketRemoveMultiPlayer) {
                        game.removePlayer(((PacketRemoveMultiPlayer) obj).getId());
                    } else if (obj instanceof PacketJoinResponse) {
                        if (((PacketJoinResponse) obj).getId() != -1) {
                            server = connection;
                            player.playerID = ((PacketJoinResponse) obj).getId();
                            player.setPositionAreaUpdate(((PacketJoinResponse) obj).getX(), ((PacketJoinResponse) obj).getY());
                            tempMapId = ((PacketJoinResponse) obj).getMapId();
                            mpUp = new PacketMultiPlayerUpdate(tempMapId, player.playerID, ((PacketJoinResponse) obj).getX(), ((PacketJoinResponse) obj).getY(), false, false);
                            System.out.println("Joined with id " + ((PacketJoinResponse) obj).getId());
                        } else {
                            cleanUp(Settings.language.menu.FullServer);
                        }
                    }
                }

                @Override
                public void disconnected(Connection connection) {
                    if (game.game.started) {
                        cleanUp(Settings.language.menu.Disconnected);
                    } else {
                        cleanUp();
                    }
                }
            });
            try {
                /* Make sure to connect using both tcp and udp port */
                client.connect(5000, IP, KryoUtil.TCP_PORT, KryoUtil.UDP_PORT);
            } catch (IOException exception) {
                ErrorHandler.exception(exception);
                client.stop();
                client.close();
                return;
            }
            client.getUpdateThread().setUncaughtExceptionHandler((Thread thread, Throwable exception) -> {
                if (exception instanceof Exception) {
                    cleanUp((Exception) exception);
                } else {
                    cleanUp(exception.getMessage());
                }
            });
            isConnected = true;
        } catch (Exception e) {
            cleanUp(e);
        }
    }

    //    public void sendInput(PacketInput input) {
//        server.sendTCP(input);
//    }
//
//    public void sendPlayerUpdate(MultiPlayerUpdate update) {
//        server.sendTCP(update);
//    }
    public void sendPlayerUpdate(short mapId, byte id, int x, int y, boolean isEmits, boolean isHop) {
        mpUp.update(mapId, id, x, y, isEmits, isHop);
        if (delay.isOver()) {
            server.sendTCP(mpUp);
            mpUp.reset();
            delay.start();
        }
    }

    public synchronized void Close() {
        client.stop();
        client.close();
    }

    private synchronized void cleanUp() {
        isConnected = false;
        Close();
        game.game.endGame();
    }

    private void cleanUp(Exception e) {
        isConnected = false;
        Close();
        game.game.endGame();
        ErrorHandler.exception(e);
    }

    private synchronized void cleanUp(String msg) {
        isConnected = false;
        Close();
        game.game.endGame();
        ErrorHandler.error(msg);
    }
}
