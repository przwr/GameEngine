/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import engine.utilities.Drawer;
import game.gameobject.GUIObject;
import game.place.cameras.Camera;
import org.newdawn.slick.Color;

/**
 * @author Wojtek
 */
public class Console extends GUIObject {

    private final String[] messages;
    private final int tile;
    private final String[] stat;
    private float alpha;
    private Camera camera;
    private int statNumber;
    private boolean stats;

    public Console(Place place) {
        super("Console", place);
        this.alpha = 0f;
        this.messages = new String[20];
        this.stat = new String[30];
        statNumber = 0;
        tile = Place.tileSize;
//        stats = true;
    }

    public void printMessage(String message) {
        alpha = 3f;
        System.arraycopy(messages, 0, messages, 1, messages.length - 1);
        messages[0] = message;
    }

    public void printStats(String message) {
        if (statNumber < stat.length) {
            stat[statNumber++] = message;
        }
    }

    public void clearStats() {
        for (int i = 0; i < stat.length; i++) {
            stat[i] = "";
        }
    }

    public void setStatsRendered(boolean stats) {
        this.stats = stats;
    }

    public boolean areStatsRendered() {
        return stats;
    }

    public void setCamera(Camera cam) {
        camera = cam;
    }

    @Override
    public void render() {
        if (alpha > 0f || stats) {
            if (alpha > 0f) {
                for (int i = 0; i < messages.length; i++) {
                    if (messages[i] != null) {
                        Drawer.renderString(messages[i], (int) ((tile * 0.1) * camera.getScale()),
                                (int) (camera.getHeight() - (i + 1.1) * tile * 0.5 * camera.getScale()),
                                place.standardFont, new Color(1f, 1f, 1f, Math.min(alpha, 1) * (i == 0 ? 1f : 0.5f)));
                    }
                }
                alpha -= 0.01f;
            }
            if (stats) {
                for (int i = 0; i < stat.length; i++) {
                    if (stat[i] != null) {
                        Drawer.renderString(stat[i], (int) ((tile * 0.1) * camera.getScale()),
                                (int) (i * tile * 0.5 * camera.getScale()),
                                place.standardFont, Color.white);
                    }
                }
                statNumber = 0;
            }
            Drawer.refreshForRegularDrawing();
        }
    }
}
