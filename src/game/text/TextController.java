/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.text;

import engine.Drawer;
import game.Settings;
import game.gameobject.GUIObject;
import game.place.Place;
import java.util.ArrayList;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslatef;
import org.newdawn.slick.Color;

/**
 *
 * @author Wojtek
 */
public class TextController extends GUIObject {

    private static final int PROP_SPEED = 0;

    private final FontHandler font;
    private final ArrayList<TextEvent> events;

    private float index, speed;

    private boolean started;

    public TextController(Place place) {
        super("TextController", place);
        events = new ArrayList<>();
        font = place.standardFont;
        started = false;
        priority = 1;
    }

    public void startText(String text) {
        String[] lines = text.split("\n");
        int i = 0, lineNum = 0;
        for (String line : lines) {
            events.add(new TextRenderer(line, i, 0, lineNum, 0));
            i += line.length();
            lineNum++;
        }
        started = true;
        speed = 0.1f;
        index = 0;
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (started) {
            glPushMatrix();
            if (Settings.scaled) {
                glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
            }
            glTranslatef(xEffect, yEffect, 0);
            if (Settings.scaled) {
                glScaled(1 / Place.getCurrentScale(), 1 / Place.getCurrentScale(), 1);
            }
            index += speed;
            Drawer.bindFontTexture();
            for (TextEvent te : events) {
                te.event((int) index);
            }
            Drawer.refreshForRegularDrawing();
            glPopMatrix();
        }
    }

    private abstract class TextEvent {

        abstract void event(int index);
    }

    private class PropertyChanger extends TextEvent {

        private final int start, type, quatity;

        public PropertyChanger(int start, int type, int quatity) {
            this.start = start;
            this.type = type;
            this.quatity = quatity;
        }

        @Override
        void event(int index) {
            if (index >= start) {
                switch (type) {
                    case PROP_SPEED:
                        speed = (float) quatity / 10;
                        break;
                }
            }
        }

    }

    private class TextRenderer extends TextEvent {

        private final int start, x, y, end;
        private final int type;
        private final String text;

        TextRenderer(String text, int start, int startX, int lineNum, int type) {
            this.start = start;
            this.type = type;
            this.text = text;
            x = startX;
            y = (int) (font.getHeight() * 1.2 * lineNum);
            end = text.length();
        }

        int getWidth() {
            return font.getWidth(text);
        }

        @Override
        void event(int index) {
            if (index >= start) {
                int i = index - start + 1;
                if (i < end) {
                    font.drawLine(text.substring(0, i), x, y, Color.black);
                } else {
                    font.drawLine(text, x, y, Color.black);
                }
            }
        }
    }
}
