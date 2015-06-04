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
import java.awt.Font;
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
import sprites.SpriteSheet;

/**
 *
 * @author Wojtek
 */
public class TextController extends GUIObject {

    private static final int PROP_SPEED = 0;
    private static final int PROP_FONT_TYPE = 1;

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_SHAKY = 1;
    private static final int TYPE_MUSIC = 2;

    private FontHandler font;
    private final FontHandler[] fonts;
    private final ArrayList<TextRow> events;
    private final RandomGenerator r;
    private final SpriteSheet frame;

    private float index, speed, change;
    private int time, rows, currentLine, endIndex, flushIndex;

    private boolean started, stoppable;

    public TextController(Place place) {
        super("TextController", place);
        events = new ArrayList<>();
        fonts = new FontHandler[]{null/*PLAIN*/, null/*BOLD*/, null/*ITALIC*/};
        font = fonts[0] = place.fonts.add("Amble-Regular", 35);
        started = false;
        priority = 1;
        frame = place.getSpriteSheet("messageFrame");
        r = RandomGenerator.create();
        rows = 3;
    }

    public void startFromFile(String file) {
        events.clear();
        try (BufferedReader read = new BufferedReader(new FileReader("res/text/" + file + ".txt"));) {
            String line = read.readLine();
            speed = Integer.parseInt(line.substring(1, 2));
            int i = 0, lineNum = 0, si, last;
            int type = TYPE_NORMAL;
            float defSpeed = speed;
            TextRow tmp;
            while ((line = read.readLine()) != null) {
                last = 0;
                tmp = new TextRow(lineNum, i);
                if (line.length() != 0) {
                    for (si = 0; si < line.length();) {
                        if (line.charAt(si) == '$') {
                            switch (line.charAt(si + 1)) {
                                case 'v':   //CHANGE SPEED
                                    if (line.charAt(si + 2) != 'n') {
                                        for (int j = si + 2; j < line.length(); j++) {
                                            if (line.charAt(j) == '$') {
                                                tmp.addEvent(new PropertyChanger(i + si, PROP_SPEED,
                                                        Float.parseFloat(line.substring(si + 2, j))));
                                                line = line.substring(0, si) + line.substring(j + 1);
                                                break;
                                            }
                                        }
                                    } else {
                                        tmp.addEvent(new PropertyChanger(i + si, PROP_SPEED, defSpeed));
                                        line = line.substring(0, si) + line.substring(si + 3);
                                    }
                                    break;
                                case 'p':   //PLAIN TEXT
                                    if (last != si) {
                                        tmp.addEvent(generateEvent(type, line.substring(last, si), i + last, font.getWidth(line.substring(0, last)), lineNum));
                                    }
                                    tmp.addEvent(new PropertyChanger(i + si, PROP_FONT_TYPE, Font.PLAIN));
                                    line = line.substring(0, si) + line.substring(si + 2);
                                    last = si;
                                    break;
                                case 'b':   //BOLD TEXT
                                    if (last != si) {
                                        tmp.addEvent(generateEvent(type, line.substring(last, si), i + last, font.getWidth(line.substring(0, last)), lineNum));
                                    }
                                    tmp.addEvent(new PropertyChanger(i + si, PROP_FONT_TYPE, Font.BOLD));
                                    line = line.substring(0, si) + line.substring(si + 2);
                                    last = si;
                                    break;
                                case 'i':   //ITALIC TEXT
                                    if (last != si) {
                                        tmp.addEvent(generateEvent(type, line.substring(last, si), i + last, font.getWidth(line.substring(0, last)), lineNum));
                                    }
                                    tmp.addEvent(new PropertyChanger(i + si, PROP_FONT_TYPE, Font.ITALIC));
                                    line = line.substring(0, si) + line.substring(si + 2);
                                    last = si;
                                    break;
                                case 'n':   //NORMAL TEXT
                                    if (last != si) {
                                        tmp.addEvent(generateEvent(type, line.substring(last, si), i + last, font.getWidth(line.substring(0, last)), lineNum));
                                    }
                                    type = TYPE_NORMAL;
                                    line = line.substring(0, si) + line.substring(si + 2);
                                    last = si;
                                    break;
                                case 's':   //SHAKY TEXT
                                    if (last != si) {
                                        tmp.addEvent(generateEvent(type, line.substring(last, si), i + last, font.getWidth(line.substring(0, last)), lineNum));
                                    }
                                    type = TYPE_SHAKY;
                                    line = line.substring(0, si) + line.substring(si + 2);
                                    last = si;
                                    break;
                                case 'm':   //MELODIC TEXT
                                    if (last != si) {
                                        tmp.addEvent(generateEvent(type, line.substring(last, si), i + last, font.getWidth(line.substring(0, last)), lineNum));
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
                        tmp.addEvent(generateEvent(type, line.substring(last, si), i + last, font.getWidth(line.substring(0, last)), lineNum));
                    }
                } else {
                    tmp.addEvent(new TextRenderer(" ", i, 0, lineNum));
                    i++;
                }
                i += line.length();
                lineNum++;
                events.add(tmp);
            }
            started = true;
            endIndex = i;
            index = 0;
            change = 100;
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
        return ret;
    }

    public void startText(String text) {
        String[] lines = text.split("\n");
        int i = 0, lineNum = 0;
        TextRow tmp;
        for (String line : lines) {
            tmp = new TextRow(lineNum, i);
            tmp.addEvent(new MusicTextRenderer(line, i, 0, lineNum));
            events.add(tmp);
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
            int tile = Place.tileSize;
            glPushMatrix();
            if (Settings.scaled) {
                glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
            }
            if (Settings.scaled) {
                glScaled(1 / Place.getCurrentScale(), 1 / Place.getCurrentScale(), 1);
            }
            glTranslatef(xEffect, yEffect + getCamera().getHeight() - 3 * tile, 0);

            Drawer.setCentralPoint();
            frame.renderPiece(0, 0);
            Drawer.translate(0, tile);
            frame.renderPiece(0, 1);
            Drawer.translate(0, tile);
            frame.renderPiece(0, 2);
            Drawer.translate(tile, - 2 * tile);
            frame.renderPieceResized(1, 0, getCamera().getWidth() - 2 * tile, tile);
            Drawer.translate(0, tile);
            frame.renderPieceResized(1, 1, getCamera().getWidth() - 2 * tile, tile);
            Drawer.translate(0, tile);
            frame.renderPieceResized(1, 2, getCamera().getWidth() - 2 * tile, tile);
            Drawer.translate(getCamera().getWidth() - 2 * tile, - 2 * tile);
            frame.renderPiece(2, 0);
            Drawer.translate(0, tile);
            frame.renderPiece(2, 1);
            Drawer.translate(0, tile);
            frame.renderPiece(2, 2);
            Drawer.returnToCentralPoint();

            Drawer.translate(tile / 2, tile / 3);

            if (index < endIndex) {
                if (change < 1) {
                    change += speed / 20;
                } else {
                    index += speed;
                    change = 1;
                }
            }
            time++;
            if (time == 60) {
                time = 0;
            }
            //place.printMessage(((int) index) + " " + change + " " + currentLine + " " + events.size());
            Drawer.bindFontTexture();

            Drawer.translate(0, -(int) (Math.max((currentLine - rows + change) * font.getHeight() * 1.2, 0)));
            for (TextRow te : events) {
                if (te.isStarting((int) index) && currentLine != te.rowNum) {
                    change = 0;
                    currentLine = te.rowNum;
                }
                if (te.rowNum >= currentLine - rows && te.rowNum <= currentLine) {
                    te.event((int) index);
                }
            }
            Drawer.refreshForRegularDrawing();
            glPopMatrix();
        }
    }

    private class TextRow {

        private final int rowNum, start;
        ArrayList<TextEvent> list;

        public TextRow(int rowNum, int start) {
            this.rowNum = rowNum;
            this.start = start;
            list = new ArrayList<>();
        }

        public void addEvent(TextEvent te) {
            list.add(te);
        }

        public boolean isStarting(int index) {
            return index == Math.max(start - 1, 0);
        }

        public void event(int start) {
            list.stream().forEach((te) -> {
                te.event(start, rowNum);
            });
        }
    }

    private abstract class TextEvent {

        int start, lineNum;

        public TextEvent(int start, int lineNum) {
            this.start = start;
            this.lineNum = lineNum;
        }

        abstract void event(int index, int lineNum);
    }

    private class PropertyChanger extends TextEvent {

        private final int type;
        private final float quatity;

        public PropertyChanger(int start, int type, float quatity) {
            super(start, 0);
            this.type = type;
            this.quatity = Math.min(quatity, 10);
        }

        @Override
        void event(int index, int lineNum) {
            if (index == start) {
                switch (type) {
                    case PROP_SPEED:
                        speed = (float) quatity / 10;
                        break;
                    case PROP_FONT_TYPE:
                        if (fonts[(int) quatity] == null) {
                            fonts[(int) quatity] = place.fonts.changeStyle(font, (int) quatity);
                        }
                        font = fonts[(int) quatity];
                }
            }
        }

    }

    private class TextRenderer extends TextEvent {

        protected final int x, y, end;
        protected final String text;
        protected final float height;

        TextRenderer(String text, int start, int startX, int lineNum) {
            super(start, lineNum);
            this.text = text;
            x = startX;
            height = (float) (font.getHeight() * 1.2);
            y = (int) (font.getHeight() * 1.2 * lineNum);
            end = text.length();
        }

        int getWidth() {
            return font.getWidth(text);
        }

        boolean isVisible(int index, int lineNum) {
            return //(this.lineNum + rows - lineNum) >= 0
                    /*&&*/ index >= start;
            //&& (this.lineNum - lineNum) <= 0;
        }

        Color changeColor(Color base, int lineNum) {
            base.a = lineNum + rows == currentLine ? Math.max(0f, 1f - 3 * change) : 1f;
            return base;
        }

        @Override
        void event(int index, int lineNum) {
            if (isVisible(index, lineNum)) {
                int i = index - start + 1;
                if (i < end) {
                    font.drawLine(text.substring(0, i), x, y, changeColor(Color.black, lineNum));
                } else {
                    font.drawLine(text, x, y, changeColor(Color.black, lineNum));
                }
            }
        }
    }

    private class ShakyTextRenderer extends TextRenderer {

        ShakyTextRenderer(String text, int start, int startX, int lineNum) {
            super(text, start, startX, lineNum);
        }

        @Override
        void event(int index, int lineNum) {
            if (isVisible(index, lineNum)) {
                int e = Math.min(index - start + 1, end);
                String tmp;
                int xd = 0;
                for (int i = 1; i <= e; i++) {
                    tmp = text.substring(i - 1, i);
                    font.drawLine(tmp, x + xd + r.random(2) - 1, y + r.random(2) - 1, 
                            changeColor(Color.black, lineNum));
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
        void event(int index, int lineNum) {
            if (isVisible(index, lineNum)) {
                int e = Math.min(index - start + 1, end);
                String tmp;
                int xd = 0, dt = time;
                for (int i = 1; i <= e; i++) {
                    tmp = text.substring(i - 1, i);
                    font.drawLine(tmp, x + xd, (int) (y + 3 * Math.sin((float) dt / 30 * Math.PI)), 
                            changeColor(Color.black, lineNum));
                    xd += font.getWidth(tmp);
                    dt += 2;
                }
            }
        }
    }
}
