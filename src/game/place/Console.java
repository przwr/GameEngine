/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import engine.Drawer;
import game.Settings;
import game.gameobject.GUIObject;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslatef;
import org.newdawn.slick.Color;

/**
 *
 * @author Wojtek
 */
public class Console extends GUIObject {

    float alpha;
    String[] messages;
    int tile;

    public Console(Place place) {
        super("Console", place);
        this.alpha = 0f;
        this.messages = new String[10];
        tile = place.getTileSize();
    }

    public void write(String message) {
        alpha = 1f;
        for (int i = messages.length - 1; i > 0; i--) {
            messages[i] = messages[i - 1];
        }
        messages[0] = message;
    }
    
    @Override
    public void render(int xEffect, int yEffect) {
        if (camera != null && alpha > 0f) {
            glPushMatrix();
            if (Settings.scaled) {
                glScaled(Settings.scale, Settings.scale, 1);
            }
            glTranslatef(xEffect, yEffect, 0);
            if (Settings.scaled) {
                glScaled(1 / Settings.scale, 1 / Settings.scale, 1);
            }
            int num = 1;
            for (int i = messages.length - 1; i >= 0; i--) {
                place.renderMessage(0, (int) ((tile * 0.5) * Settings.scale),
                        (int) ((num * tile * 0.5) * Settings.scale),
                        messages[i], new Color(1f, 1f, 1f, alpha));
                num++;
            }
            alpha -= 0.001f;
            Drawer.refreshForRegularDrawing();
            glPopMatrix();
        }
    }

}
