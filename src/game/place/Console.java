/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import engine.Drawer;
import game.gameobject.GUIObject;
import game.place.cameras.Camera;
import org.newdawn.slick.Color;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Wojtek
 */
public class Console extends GUIObject {

    private final String[] messages;
    private final int tile;
    private float alpha;
    private Camera camera;

    public Console(Place place) {
        super("Console", place);
        this.alpha = 0f;
        this.messages = new String[20];
        tile = Place.tileSize;
    }

    public void write(String message) {
        alpha = 3f;
        System.arraycopy(messages, 0, messages, 1, messages.length - 1);
        messages[0] = message;
    }

    public void setCamera(Camera cam) {
        camera = cam;
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (alpha > 0f) {
            glPushMatrix();
            glScaled(camera.getScale(), camera.getScale(), 1);
            glTranslatef(xEffect, yEffect, 0);
            glScaled(1 / camera.getScale(), 1 / camera.getScale(), 1);
            for (int i = 0; i < messages.length; i++) {
                if (messages[i] != null) {
                    Drawer.renderString(messages[i], (int) ((tile * 0.1) * camera.getScale()),
                            (int) (camera.getHeight() - (i + 1.1) * tile * 0.5 * camera.getScale()),
                            place.standardFont, new Color(1f, 1f, 1f, Math.min(alpha, 1) * (i == 0 ? 1f : 0.7f)));
                }
            }
            alpha -= 0.01f;
            Drawer.refreshForRegularDrawing();
            glPopMatrix();
        }
    }

}
