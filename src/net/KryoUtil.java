/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net;

import net.packets.PacketMessage;
import net.packets.PacketJoinResponse;
import net.packets.PacketJoinRequest;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;
import net.packets.PacketAddMPlayer;
import net.packets.PacketInput;
import net.packets.PacketMPlayerUpdate;
import net.packets.PacketRemoveMPlayer;

/**
 *
 * @author przemek
 */
public class KryoUtil {

    public static final int TCP_PORT = 11155;
    public static final int UDP_PORT = 11155;

    public static void registerServerClasses(Server server) {
        register(server.getKryo());
    }

    public static void registerClientClass(Client client) {
        register(client.getKryo());
    }

    private static void register(Kryo kryo) {
        kryo.register(MPlayer.class);
        kryo.register(NewMPlayer.class);
        kryo.register(MPlayerUpdate.class);
        kryo.register(byte.class);
        kryo.register(boolean.class);
        kryo.register(boolean[].class);
        kryo.register(int.class);
        kryo.register(String.class);

        // network messages
        kryo.register(PacketJoinRequest.class);
        kryo.register(PacketJoinResponse.class);
        kryo.register(PacketAddMPlayer.class);
        kryo.register(PacketRemoveMPlayer.class);
        kryo.register(PacketMPlayerUpdate.class);
        kryo.register(PacketInput.class);
        kryo.register(PacketMessage.class);
    }
}
