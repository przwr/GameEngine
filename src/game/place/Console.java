/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import game.Settings;
import game.gameobject.GUIObject;
import game.text.fonts.TextMaster;
import game.text.fonts.TextPiece;
import org.lwjgl.opengl.Display;

/**
 * @author Wojtek
 */
public class Console extends GUIObject {

    private static TextPiece text;
    private final String[] messages;
    private final int tile;
    private final String[] stat;
    private float alpha;
    private int statNumber;
    private boolean stats;

    public Console(Place place) {
        super("Console", place);
        this.alpha = 0f;
        this.messages = new String[20];
        this.stat = new String[30];
        statNumber = 0;
        tile = Place.tileSize;
        text = new TextPiece("", 24, TextMaster.getFont("Lato-Regular"), Display.getWidth(), false);
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

    @Override
    public void render() {
        if (alpha > 0f || stats) {
            TextMaster.startRenderText();
            if (alpha > 0f) {
                for (int i = 0; i < messages.length; i++) {
                    if (messages[i] != null) {
                        text.setText(messages[i]);
                        TextMaster.render(text, (int) ((tile * 0.1) * Settings.nativeScale),
                                Display.getHeight() - (int) ((i + 1.1) * tile * 0.5 * Settings.nativeScale));
                    }
                }
                alpha -= 0.01f;
            }
            if (stats) {
                for (int i = 0; i < stat.length; i++) {
                    if (stat[i] != null) {
                        text.setText(stat[i]);
                        TextMaster.render(text, (int) ((tile * 0.1) * Settings.nativeScale), 0);
                    }
                }
                statNumber = 0;
            }
            TextMaster.endRenderText();
        }
    }
}
