/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net;

import net.packets.PacketMessage;
import net.packets.PacketJoinResponse;
import net.packets.PacketJoinRequest;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import engine.Delay;
import engine.Methods;
import game.gameobject.Player;
import java.io.IOException;
import net.packets.PacketAddMPlayer;
import net.packets.PacketMPlayerUpdate;
import net.packets.PacketRemoveMPlayer;
import net.packets.PacketUpdate;

/**
 *
 * @author przemek
 */
public class GameClient {

    private final Client client;
    private final Player pl;
    private PacketMPlayerUpdate mpup;
    private final GameOnline game;
    private final float SCALE;
    private Connection server;
    public boolean isConnected;
    private Delay delay;

    public GameClient(final Player pl, final GameOnline game, String IP) {

        this.pl = pl;
        this.game = game;
        this.SCALE = game.g.settings.SCALE;
        client = new Client();
        delay = new Delay(50);
        delay.terminate();
        try {
            Log.set(Log.LEVEL_DEBUG);
            KryoUtil.registerClientClass(client);
            new Thread(client).start();

            client.addListener(new Listener() {
                @Override
                public void connected(Connection connection) {
                    PacketJoinRequest test = new PacketJoinRequest(pl.getName());
                    client.sendTCP(test);
                }

                @Override
                public void received(Connection connection, Object obj) {
                    try {
                        if (obj instanceof PacketUpdate) {
                            //System.out.println(Methods.sizeInBytes((PacketUpdate) obj));
                            game.update((PacketUpdate) obj);
                        }
                        if (obj instanceof PacketMessage) {
                            System.out.println("Recived from server: " + ((PacketMessage) obj).getMessage());
                        } else if (obj instanceof PacketAddMPlayer) {
                            game.addPlayer(((PacketAddMPlayer) obj).getPlayer());
                        } else if (obj instanceof PacketRemoveMPlayer) {
                            game.removePlayer(((PacketRemoveMPlayer) obj).getId());
                        } else if (obj instanceof PacketJoinResponse) {
                            if (((PacketJoinResponse) obj).getId() != -1) {
                                server = connection;
                                pl.id = ((PacketJoinResponse) obj).getId();
                                pl.setX(Methods.RoundHU(SCALE * (float) ((PacketJoinResponse) obj).getX()));
                                pl.setY(Methods.RoundHU(SCALE * (float) ((PacketJoinResponse) obj).getY()));
                                pl.upDepth();
                                mpup = new PacketMPlayerUpdate(pl.id, ((PacketJoinResponse) obj).getX(), ((PacketJoinResponse) obj).getY(), false, false);
                                System.out.println("Joined with id " + ((PacketJoinResponse) obj).getId());
                            } else {
                                System.out.println("Server is Full!");
                                isConnected = false;
                                Close();
                            }
                        }
                    } catch (Exception e) {
                        cleanUp(e);
                    }
                }

                @Override
                public void disconnected(Connection connection) {
                    System.out.println("Disconnected!");
                    isConnected = false;
                    client.stop();
                    client.close();
                }
            });

            try {
                /* Make sure to connect using both tcp and udp port */
                client.connect(5000, IP, KryoUtil.TCP_PORT, KryoUtil.UDP_PORT);
            } catch (IOException ex) {
                System.out.println(ex);
                client.stop();
                client.close();
                return;
            }
            isConnected = true;
        } catch (Exception e) {
            cleanUp(e);
        }
    }

//    public void sendInput(PacketInput input) {
//        server.sendTCP(input);
//    }
//
//    public void sendPlayerUpdate(MPlayerUpdate update) {
//        server.sendTCP(update);
//    }

    public void sendPlayerUpdate(byte id, int x, int y, boolean isEmits, boolean isHop) {
        try {
            mpup.update(id, x, y, isEmits, isHop, SCALE);
            if (delay.isOver()) {
                server.sendTCP(mpup);
                mpup.reset();
                delay.restart();
            }
        } catch (Exception e) {
            cleanUp(e);
        }
    }

    public void Close() {
        client.stop();
        client.close();
    }

    private void cleanUp(Exception e) {
        game.g.endGame();
        Methods.Exception(e);
    }
}
