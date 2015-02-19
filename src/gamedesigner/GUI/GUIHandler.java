/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner.GUI;

import engine.Drawer;
import engine.Methods;
import game.Settings;
import game.gameobject.GUIObject;
import game.place.Place;
import gamedesigner.ObjectPlace;
import java.util.ArrayList;
import org.lwjgl.input.Keyboard;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslatef;
import org.newdawn.slick.Color;

/**
 *
 * @author Wojtek
 */
public class GUIHandler extends GUIObject {

    int mode;
    ArrayList<String> list;
    String text = "";
    int tile;
    ObjectPlace objPlace;

    public static GUIHandler createNamingConsole(Place place) {
        return new GUIHandler(place, 0, null);
    }

    public static GUIHandler createChooser(Place place, ArrayList<String> list) {
        return new GUIHandler(place, 1, list);
    }

    public static GUIHandler createHelpingScreen(Place place) {
        return new GUIHandler(place, 2, null);
    }

    private GUIHandler(Place place, int mode, ArrayList<String> list) {
        super("guih", place);
        this.mode = mode;
        this.list = list;
        tile = place.getTileSize();
        objPlace = (ObjectPlace) place;
    }

    private void renderNamingConsole() {
        if (Settings.scaled) {
            glScaled(1 / Settings.scale, 1 / Settings.scale, 1);
        }
        text = Methods.editWithKeyboard(text);
        place.renderMessageCentered(0, (int) ((tile * 1.5) * Settings.scale), (int) ((tile * 0.5) * Settings.scale),
                "Wpisz nazwę:\n" + text, new Color(1f, 1f, 1f));

        if (objPlace.keyPressed(Keyboard.KEY_RETURN)) {
            mode = -1;
        }
    }

    private void renderQuestion() {

    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (camera != null) {
            glPushMatrix();
            if (Settings.scaled) {
                glScaled(Settings.scale, Settings.scale, 1);
            }
            glTranslatef(xEffect, yEffect, 0);
            if (Settings.scaled) {
                glScaled(1 / Settings.scale, 1 / Settings.scale, 1);
            }
            switch (mode) {
                case -1:
                    renderQuestion();
                    break;
                case 0:
                    renderNamingConsole();
                    break;
                case 1:
                    renderNamingConsole();
                    break;
                case 2:
                    renderNamingConsole();
                    break;
            }
            Drawer.refreshForRegularDrawing();
            glPopMatrix();
        }
    }

}
