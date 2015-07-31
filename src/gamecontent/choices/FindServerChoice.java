/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.choices;

import com.esotericsoftware.kryonet.Client;
import engine.Delay;
import engine.inout.AnalyzerSettings;
import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.place.Menu;
import net.KryoUtil;

import java.net.InetAddress;

/**
 * @author przemek
 */
public class FindServerChoice extends MenuChoice {

    private final Client client;
    private final Runnable run;
    private final Delay delay;
    private String status;
    private Thread thread;
    private boolean isSearching;

    public FindServerChoice(String label, final Menu menu) {
        super(label, menu);
        client = new Client();
        status = "";
        delay = new Delay(2000);
        run = () -> {
            menu.isMapping = true;
            InetAddress address = client.discoverHost(KryoUtil.TCP_PORT, KryoUtil.UDP_PORT);
            if (address == null) {
                status = " - " + Settings.language.menu.NotFound;
            } else {
                status = " - " + Settings.language.menu.Found;
                Settings.serverIP = address.toString().replace("/", "");
                AnalyzerSettings.update();
            }
            end();
        };
    }

    @Override
    public void action() {
        status = " - " + Settings.language.menu.Searching;
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
        menu.delay.start();
        menu.isMapping = false;
        isSearching = false;
        delay.start();
    }
}
