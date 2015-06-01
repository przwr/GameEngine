/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.text;

import engine.Drawer;
import engine.Main;
import engine.RandomGenerator;
import game.Settings;
import game.gameobject.GUIObject;
import game.place.Place;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_SHAKY = 1;
    private static final int TYPE_MUSIC = 2;

    private final FontHandler font;
    private final ArrayList<TextEvent> events;
    private final RandomGenerator r;

    private float index, speed;
    private int time;

    private boolean started;

    public TextController(Place place) {
        super("TextController", place);
        events = new ArrayList<>();
        font = place.standardFont;
        started = false;
        priority = 1;
        r = RandomGenerator.create();
    }

    public void startFromFile(String file) {
        events.clear();
        try (BufferedReader read = new BufferedReader(new FileReader("res/text/" + file + ".txt"));) {
            String line = read.readLine();
            speed = Integer.parseInt(line.substring(1, 2));
            int i = 0, lineNum = 0, si, last;
            int type = TYPE_NORMAL;
            float defSpeed = speed;
            while ((line = read.readLine()) != null) {
                last = 0;
                System.out.println(line);
                for (si = 0; si < line.length();) {
                    if (line.charAt(si) == '$') {
                        switch (line.charAt(si + 1)) {
                            case 'v':
                                if (line.charAt(si + 2) != 'n') {
                                    for (int j = si + 2; j < line.length(); j++) {
                                        if (line.charAt(j) == '$') {
                                            events.add(new PropertyChanger(i + si, PROP_SPEED,
                                                    Float.parseFloat(line.substring(si + 2, j))));
                                            line = line.substring(0, si) + line.substring(j + 1);
                                            break;
                                        }
                                    }
                                } else {
                                    events.add(new PropertyChanger(i + si, PROP_SPEED, defSpeed));
                                    line = line.substring(0, si) + line.substring(si + 3);
                                }
                                break;
                            case 'n':
                                if (last != si) {
                                    events.add(generateEvent(type, line.substring(last, si), i + last, font.getWidth(line.substring(0, last)), lineNum));
                                }
                                type = TYPE_NORMAL;
                                line = line.substring(0, si) + line.substring(si + 2);
                                last = si;
                                break;
                            case 's':
                                if (last != si) {
                                    events.add(generateEvent(type, line.substring(last, si), i + last, font.getWidth(line.substring(0, last)), lineNum));
                                }
                                type = TYPE_SHAKY;
                                line = line.substring(0, si) + line.substring(si + 2);
                                last = si;
                                break;
                            case 'm':
                                if (last != si) {
                                    events.add(generateEvent(type, line.substring(last, si), i + last, font.getWidth(line.substring(0, last)), lineNum));
                                }
                                type = TYPE_MUSIC;
                                line = line.substring(0, si) + line.substring(si + 2);
                                last = si;
                                break;
                            default:
                                throw new IOException("UNKNOWN SYMBOL \"" + line.charAt(si + 1) + "\"!");
                        }
                    } else {
                        si++;
                    }
                }
                if (last != si) {
                    events.add(generateEvent(type, line.substring(last, si), i + last, font.getWidth(line.substring(0, last)), lineNum));
                }
                System.out.println("  " + line);
                i += line.length();
                lineNum++;
            }
            started = true;
            index = 0;
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private TextEvent generateEvent(int type, String text, int start, int xStart, int lineNum) {
        TextEvent ret = null;
        switch (type) {
            case TYPE_NORMAL:
                ret = new TextRenderer(text, start, xStart, lineNum);
                break;
            case TYPE_SHAKY:
                ret = new ShakyTextRenderer(text, start, xStart, lineNum);
                break;
            case TYPE_MUSIC:
                ret = new MusicTextRenderer(text, start, xStart, lineNum);
                break;
        }
        System.out.println(start + ":" + type + ":" + text);
        return ret;
    }

    public void startText(String text) {
        String[] lines = text.split("\n");
        int i = 0, lineNum = 0;
        for (String line : lines) {
            events.add(new MusicTextRenderer(line, i, 0, lineNum));
            i += line.length();
            lineNum++;
        }
        started = true;
        speed = 0.5f;
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
            time++;
            if (time == 60) {
                time = 0;
            }
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

        private final int start, type;
        private final float quatity;

        public PropertyChanger(int start, int type, float quatity) {
            this.start = start;
            this.type = type;
            this.quatity = quatity;
        }

        @Override
        void event(int index) {
            if (index == start) {
                switch (type) {
                    case PROP_SPEED:
                        speed = (float) quatity / 10;
                        break;
                }
            }
        }

    }

    private class TextRenderer extends TextEvent {

        protected final int start, x, y, end;
        protected final String text;

        TextRenderer(String text, int start, int startX, int lineNum) {
            this.start = start;
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

    private class ShakyTextRenderer extends TextRenderer {

        ShakyTextRenderer(String text, int start, int startX, int lineNum) {
            super(text, start, startX, lineNum);
        }

        @Override
        void event(int index) {
            if (index >= start) {
                int e = Math.min(index - start + 1, end);
                String tmp;
                int xd = 0;
                for (int i = 1; i <= e; i++) {
                    tmp = text.substring(i - 1, i);
                    font.drawLine(tmp, x + xd + r.random(2) - 1, y + r.random(2) - 1, Color.black);
                    xd += font.getWidth(tmp);
                }
            }
        }
    }

    private class MusicTextRenderer extends TextRenderer {

        MusicTextRenderer(String text, int start, int startX, int lineNum) {
            super(text, start, startX, lineNum);
        }

        @Override
        void event(int index) {
            if (index >= start) {
                int e = Math.min(index - start + 1, end);
                String tmp;
                int xd = 0, dt = time;
                for (int i = 1; i <= e; i++) {
                    tmp = text.substring(i - 1, i);
                    font.drawLine(tmp, x + xd, (int) (y + 3 * Math.sin((float) dt / 30 * Math.PI)), Color.black);
                    xd += font.getWidth(tmp);
                    dt += 2;
                }
            }
        }
    }
}
