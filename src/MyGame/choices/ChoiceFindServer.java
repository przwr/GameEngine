/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyGame.choices;

import com.esotericsoftware.kryonet.Client;
import engine.Delay;
import game.AnalizerSettings;
import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.place.Menu;
import java.net.InetAddress;
import net.KryoUtil;

/**
 *
 * @author przemek
 */
public class ChoiceFindServer extends MenuChoice {

    private final Client client;
    private String status;
    private Thread thread;
    private final Runnable run;
    private final Delay delay;
    private boolean isSearching;

    public ChoiceFindServer(String label, final Menu menu, final Settings settings) {
        super(label, menu, settings);
        client = new Client();
        status = "";
        delay = new Delay(2000);
        run = new Runnable() {
            @Override
            public void run() {
                menu.isMapping = true;
                InetAddress address = client.discoverHost(KryoUtil.TCP_PORT, KryoUtil.UDP_PORT);
                if (address == null) {
                    status = " - " + settings.language.m.NotFound;
                } else {
                    status = " - " + settings.language.m.Found;
                    settings.serverIP = address.toString().replace("/", "");
                    AnalizerSettings.update(settings);
                }
                end();
            }
        };
    }

    @Override
    public void action() {
        status = " - " + settings.language.m.Searching;
        isSearching = true;
        thread = new Thread(run);
        thread.start();
    }

    @Override
    public String getLabel() {
        if (!delay.isOver() || isSearching) {
            return label + status;
        }
        return label;
    }

    private void end() {
        thread = null;
        menu.delay.restart();
        menu.isMapping = false;
        isSearching = false;
        delay.restart();
    }
}
