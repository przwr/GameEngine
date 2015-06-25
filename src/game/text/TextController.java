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
import game.gameobject.Entity;
import game.gameobject.GUIObject;
import game.place.Place;
import gamecontent.MyController;
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
import sprites.Sprite;
import sprites.SpriteSheet;

/**
 *
 * @author Wojtek
 */
public class TextController extends GUIObject {

    private static final int PROP_SPEED = 0;
    private static final int PROP_SPEAKER = 1;
    private static final int PROP_FLUSH = 2;
    private static final int PROP_PORTRAIT = 3;
    private static final int PROP_EXPRESSION = 4;

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_SHAKY = 1;
    private static final int TYPE_MUSIC = 2;

    private static final int STYLE_NORMAL = 0;
    private static final int STYLE_BOLD = 1;
    private static final int STYLE_ITALIC = 2;

    private final FontHandler[] fonts;
    private final FontHandler littleFont;
    private final ArrayList<TextRow> events;
    private final RandomGenerator r;
    private final SpriteSheet frame;

    private float index, speed, change, realSpeed;
    private int time, rows, deltaLines, endIndex, rowsInPlace, speaker, portrait, expression;

    private boolean started, stoppable, flushing, flushReady, playerOnRight;

    private Entity[] locked;
    private final ArrayList<String> speakers;
    private final ArrayList<Portrait> portraits;

    public TextController(Place place) {
        super("TextController", place);
        events = new ArrayList<>();
        fonts = new FontHandler[]{null/*PLAIN*/, null/*BOLD*/, null/*ITALIC*/};
        fonts[0] = place.fonts.getFont("Amble-Regular", 0, 35);
        fonts[1] = place.fonts.changeStyle(fonts[0], 1);
        fonts[2] = place.fonts.changeStyle(fonts[0], 2);
        littleFont = place.fonts.getFont("Amble-Regular", 0, 20);
        started = false;
        priority = 1;
        frame = place.getSpriteSheet("messageFrame");
        r = RandomGenerator.create();
        rows = 3;
        stoppable = true;
        speakers = new ArrayList<>(1);
        portraits = new ArrayList<>(1);
    }

    public void startFromFile(String file, boolean isPlayerOnRight) {
        if (!started) {
            playerOnRight = isPlayerOnRight;
            events.clear();
            try (BufferedReader read = new BufferedReader(new FileReader("res/text/" + file + ".txt"));) {
                String line;
                String[] tab;
                speed = 1;
                while ((line = read.readLine()).length() > 0) {
                    tab = line.split(":");
                    switch (tab[0]) {
                        case "sp":
                            speed = Float.parseFloat(tab[1]);
                            break;
                        case "au":
                            speakers.add(tab[1]);
                            break;
                        case "po":
                            portraits.add(new Portrait(place.getSpriteSheet(tab[1]), tab[2].equals("1")));
                            break;
                    }
                }
                if (speakers.isEmpty()) {
                    speakers.add("???");
                }
                int i = 0, lineNum = 0, si, last;
                int type = TYPE_NORMAL;
                FontHandler font = fonts[STYLE_NORMAL];
                float defSpeed = speed;
                TextRow tmp = null;
                while ((line = read.readLine()) != null) {
                    last = 0;
                    if (tmp != null) {
                        tmp.setEnd(i - 1);
                    }
                    tmp = new TextRow(lineNum);
                    if (line.length() != 0) {
                        for (si = 0; si < line.length();) {
                            if (line.charAt(si) == '$') {
                                switch (line.substring(si + 1, si + 3).toLowerCase()) {
                                    case "ve":   //CHANGE SPEED
                                        if (line.charAt(si + 3) != 'n' && line.charAt(si + 3) != 'N') {
                                            for (int j = si + 3; j < line.length(); j++) {
                                                if (line.charAt(j) == '$') {
                                                    tmp.addEvent(new PropertyChanger(i + si, PROP_SPEED,
                                                            Float.parseFloat(line.substring(si + 3, j))));
                                                    line = line.substring(0, si) + line.substring(j + 1);
                                                    break;
                                                }
                                            }
                                        } else {
                                            tmp.addEvent(new PropertyChanger(i + si, PROP_SPEED, defSpeed));
                                            line = line.substring(0, si) + line.substring(si + 4);
                                        }
                                        break;
                                    case "au":   //SPEAKER'S NAME
                                        for (int j = si + 3; j < line.length(); j++) {
                                            if (line.charAt(j) == '$') {
                                                tmp.addEvent(new PropertyChanger(i + si, PROP_SPEAKER,
                                                        Integer.parseInt(line.substring(si + 3, j))));
                                                line = line.substring(0, si) + line.substring(j + 1);
                                                break;
                                            }
                                        }
                                        break;
                                    case "po":   //PORTRAIT
                                        for (int j = si + 3; j < line.length(); j++) {
                                            if (line.charAt(j) == '$') {
                                                tmp.addEvent(new PropertyChanger(i + si, PROP_PORTRAIT,
                                                        Integer.parseInt(line.substring(si + 3, j))));
                                                line = line.substring(0, si) + line.substring(j + 1);
                                                break;
                                            }
                                        }
                                        break;
                                    case "ex":   //EXPRESSION
                                        for (int j = si + 3; j < line.length(); j++) {
                                            if (line.charAt(j) == '$') {
                                                tmp.addEvent(new PropertyChanger(i + si, PROP_EXPRESSION,
                                                        Integer.parseInt(line.substring(si + 3, j))));
                                                line = line.substring(0, si) + line.substring(j + 1);
                                                break;
                                            }
                                        }
                                        break;
                                    case "fl":   //FLUSH LINES
                                        if (last != si) {
                                            tmp.addEvent(generateEvent(type, line.substring(last, si), i + last, font.getWidth(line.substring(0, last)), lineNum, font));
                                        }
                                        tmp.addEvent(new PropertyChanger(i + si - 1, PROP_FLUSH, 0));
                                        line = line.substring(0, si) + line.substring(si + 3);
                                        last = si;
                                        break;
                                    case "pl":   //PLAIN TEXT
                                        if (last != si) {
                                            tmp.addEvent(generateEvent(type, line.substring(last, si), i + last, font.getWidth(line.substring(0, last)), lineNum, font));
                                        }
                                        font = fonts[STYLE_NORMAL];
                                        line = line.substring(0, si) + line.substring(si + 3);
                                        last = si;
                                        break;
                                    case "bo":   //BOLD TEXT
                                        if (last != si) {
                                            tmp.addEvent(generateEvent(type, line.substring(last, si), i + last, font.getWidth(line.substring(0, last)), lineNum, font));
                                        }
                                        font = fonts[STYLE_BOLD];
                                        line = line.substring(0, si) + line.substring(si + 3);
                                        last = si;
                                        break;
                                    case "it":   //ITALIC TEXT
                                        if (last != si) {
                                            tmp.addEvent(generateEvent(type, line.substring(last, si), i + last, font.getWidth(line.substring(0, last)), lineNum, font));
                                        }
                                        font = fonts[STYLE_ITALIC];
                                        line = line.substring(0, si) + line.substring(si + 3);
                                        last = si;
                                        break;
                                    case "no":   //NORMAL TEXT
                                        if (last != si) {
                                            tmp.addEvent(generateEvent(type, line.substring(last, si), i + last, font.getWidth(line.substring(0, last)), lineNum, font));
                                        }
                                        type = TYPE_NORMAL;
                                        line = line.substring(0, si) + line.substring(si + 3);
                                        last = si;
                                        break;
                                    case "sh":   //SHAKY TEXT
                                        if (last != si) {
                                            tmp.addEvent(generateEvent(type, line.substring(last, si), i + last, font.getWidth(line.substring(0, last)), lineNum, font));
                                        }
                                        type = TYPE_SHAKY;
                                        line = line.substring(0, si) + line.substring(si + 3);
                                        last = si;
                                        break;
                                    case "me":   //MELODIC TEXT
                                        if (last != si) {
                                            tmp.addEvent(generateEvent(type, line.substring(last, si), i + last, font.getWidth(line.substring(0, last)), lineNum, font));
                                        }
                                        type = TYPE_MUSIC;
                                        line = line.substring(0, si) + line.substring(si + 3);
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
                            tmp.addEvent(generateEvent(type, line.substring(last, si), i + last, font.getWidth(line.substring(0, last)), lineNum, font));
                        }
                    } else {
                        tmp.addEvent(new TextRenderer(" ", i, 0, lineNum, font));
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
                rowsInPlace = 0;
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private TextEvent generateEvent(int type, String text, int start, int xStart, int lineNum, FontHandler font) {
        TextEvent ret = null;
        switch (type) {
            case TYPE_NORMAL:
                ret = new TextRenderer(text, start, xStart, lineNum, font);
                break;
            case TYPE_SHAKY:
                ret = new ShakyTextRenderer(text, start, xStart, lineNum, font);
                break;
            case TYPE_MUSIC:
                ret = new MusicTextRenderer(text, start, xStart, lineNum, font);
                break;
        }
        return ret;
    }

    public void startText(String text) {
        String[] lines = text.split("\n");
        int i = 0, lineNum = 0;
        TextRow tmp = null;
        for (String line : lines) {
            if (tmp != null) {
                tmp.setEnd(i - 1);
            }
            tmp = new TextRow(lineNum);
            tmp.addEvent(new MusicTextRenderer(line, i, 0, lineNum, fonts[0]));
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
            glTranslatef(xEffect, yEffect + getCamera().getHeight() - 3.5f * tile, 0);

            Drawer.setCentralPoint();
            frame.renderPiece(0, 0);
            Drawer.translate(0, tile);
            frame.renderPieceResized(0, 1, tile, tile * 1.5f);
            Drawer.translate(0, tile * 1.5f);
            frame.renderPiece(0, 2);
            Drawer.translate(tile, -2.5f * tile);
            frame.renderPieceResized(1, 0, getCamera().getWidth() - 2 * tile, tile);
            Drawer.translate(0, tile);
            frame.renderPieceResized(1, 1, getCamera().getWidth() - 2 * tile, tile * 1.5f);
            Drawer.translate(0, tile * 1.5f);
            frame.renderPieceResized(1, 2, getCamera().getWidth() - 2 * tile, tile);
            Drawer.translate(getCamera().getWidth() - 2 * tile, -2.5f * tile);
            frame.renderPiece(2, 0);
            Drawer.translate(0, tile);
            frame.renderPieceResized(2, 1, tile, tile * 1.5f);
            Drawer.translate(0, tile * 1.5f);
            frame.renderPiece(2, 2);
            if (flushReady) {
                Drawer.translate(-tile / 2, (float) (3 * Math.sin((float) time / 30 * Math.PI)));
                frame.renderPiece(3, 0);
            }
            if (index >= endIndex) {
                Drawer.translate(-tile / 2, 0);
                frame.renderPiece(3, 1);
            }
            Drawer.returnToCentralPoint();

            if (portraits.get(portrait).onRight ^ playerOnRight) {
                Drawer.translate(getCamera().getWidth() - 3 * tile, 0);
                portraits.get(portrait).image.renderPieceMirrored(expression);
            } else {
                Drawer.translate(3 * tile, 0);
                portraits.get(portrait).image.renderPiece(expression);
            }

            Drawer.returnToCentralPoint();

            littleFont.drawLine(speakers.get(speaker) + ":", tile / 2, tile / 5, Color.gray);

            Drawer.translate(tile / 2, 2 * tile / 3
                    - (int) (Math.max((deltaLines + (flushing ? change : 0)) * fonts[0].getHeight() * 1.2, 0)));

            time++;
            if (time == 60) {
                time = 0;
            }
            realSpeed = speed * (controler.isKeyPressed(MyController.RUN) ? 2f : 1f);

            if (flushing) {
                change += realSpeed / 15;
                if (change >= 1) {
                    deltaLines++;
                    rowsInPlace--;
                    if (rowsInPlace == 0) {
                        flushing = false;
                        change = 1;
                    } else {
                        change = 0;
                    }
                }
            } else {
                if (!flushReady) {
                    if (index < endIndex) {
                        index += realSpeed;
                        change = 1;
                    } else if (controler.isKeyClicked(MyController.JUMP)) {
                        stopTextViewing();
                    }
                } else {
                    if (controler.isKeyClicked(MyController.JUMP)) {
                        flushing = true;
                        flushReady = false;
                        change = 0;
                    }
                }
            }
            //place.printMessage(speed + " : " + flushing + " " + flushReady + " " + change + " " + rowsInPlace);
            Drawer.bindFontTexture();

            for (TextRow te : events) {
                te.changers((int) index);
                if (te.isEnding((int) index) && !flushReady && !flushing) {
                    rowsInPlace++;
                    if (rowsInPlace == rows) {
                        flushReady = true;
                    }
                }
                if (te.rowNum >= deltaLines && te.rowNum <= deltaLines + rows) {
                    te.event((int) index);
                }
            }
            Drawer.refreshForRegularDrawing();
            glPopMatrix();
        }
    }

    public boolean isStarted() {
        return started;
    }

    public void lockEntities(Entity[] locked) {
        this.locked = locked;
        for (Entity e : locked) {
            e.setUnableToMove(true);
        }
    }

    public void lockEntity(Entity locked) {
        lockEntities(new Entity[]{locked});
    }

    private void stopTextViewing() {
        started = false;
        index = 0;
        speed = 1;
        change = 1;
        time = 0;
        deltaLines = 0;
        rowsInPlace = 0;
        flushing = false;
        flushReady = false;
        events.clear();
        speakers.clear();
        speaker = 0;
        portraits.clear();
        expression = 0;
        if (locked != null) {
            for (Entity e : locked) {
                e.setUnableToMove(false);
            }
        }
    }

    private class Portrait {

        SpriteSheet image;
        boolean onRight;

        Portrait(SpriteSheet image, boolean onRight) {
            this.image = image;
            this.onRight = onRight;
        }
    }

    private class TextRow {

        private final int rowNum;
        private int end;
        private boolean ending;
        private final ArrayList<TextEvent> list;
        private final ArrayList<TextEvent> changers;

        public TextRow(int rowNum) {
            this.rowNum = rowNum;
            list = new ArrayList<>();
            changers = new ArrayList<>();
            end = -1;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        public void addEvent(TextEvent te) {
            if (te instanceof PropertyChanger) {
                changers.add(te);
            } else {
                list.add(te);
            }
        }

        public boolean isEnding(int i) {
            if (ending) {
                return false;
            } else {
                if (i >= end && end > 0) {
                    ending = true;
                    index = end;
                    return true;
                }
                return false;
            }
        }

        public void event(int start) {
            list.stream().forEach((te) -> {
                te.event(start, rowNum);
            });
        }

        public void changers(int start) {
            changers.stream().forEach((te) -> {
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
        private boolean done;

        public PropertyChanger(int start, int type, float quatity) {
            super(start, 0);
            this.type = type;
            this.quatity = Math.min(quatity, 10);
        }

        @Override
        void event(int i, int lineNum) {
            if (i >= start && !done) {
                switch (type) {
                    case PROP_SPEED:
                        speed = (float) quatity;
                        break;
                    case PROP_FLUSH:
                        flushReady = true;
                        rowsInPlace++;
                        break;
                    case PROP_SPEAKER:
                        speaker = (int) quatity;
                        break;
                    case PROP_PORTRAIT:
                        portrait = (int) quatity;
                        break;
                    case PROP_EXPRESSION:
                        expression = (int) quatity;
                        break;
                }
                done = true;
                index = start;
            }
        }

    }

    private class TextRenderer extends TextEvent {

        protected final int x, y, end;
        protected final String text;
        protected final float height;
        protected final FontHandler font;

        TextRenderer(String text, int start, int startX, int lineNum, FontHandler font) {
            super(start, lineNum);
            this.text = text;
            this.font = font;
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
            base.a = (lineNum == deltaLines && flushing ? Math.max(0f, 1f - 3 * change) : 1f);
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

        ShakyTextRenderer(String text, int start, int startX, int lineNum, FontHandler font) {
            super(text, start, startX, lineNum, font);
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

        MusicTextRenderer(String text, int start, int startX, int lineNum, FontHandler font) {
            super(text, start, startX, lineNum, font);
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
                    dt += 4;
                }
            }
        }
    }
}
