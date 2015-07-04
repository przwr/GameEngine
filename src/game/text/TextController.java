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
    private static final int PROP_QUESTION = 5;
    private static final int PROP_JUMP = 6;

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_SHAKY = 1;
    private static final int TYPE_MUSIC = 2;

    private static final int STYLE_NORMAL = 0;
    private static final int STYLE_BOLD = 1;
    private static final int STYLE_ITALIC = 2;

    private final FontHandler[] fonts;
    private final FontHandler littleFont;
    private Branch events;
    private final ArrayList<Branch> branches;
    private final RandomGenerator r;
    private final SpriteSheet frame;

    private float index, speed, change, realSpeed;
    private int time, rows, deltaLines, rowsInPlace, speaker, portrait, expression, answer, jumpTo;

    private boolean started, flushing, flushReady, stop, question;

    private Entity[] locked;
    private final ArrayList<String> speakers;
    private final ArrayList<Portrait> portraits;

    private String[] answerText;
    private int[] answerJump;

    public TextController(Place place) {
        super("TextController", place);
        branches = new ArrayList<>();
        events = new Branch();
        fonts = new FontHandler[3];
        fonts[0] = place.fonts.getFont("Amble-Regular", 0, 35);//PLAIN
        fonts[1] = place.fonts.changeStyle(fonts[0], 1);//BOLD
        fonts[2] = place.fonts.changeStyle(fonts[0], 2);//ITALIC
        littleFont = place.fonts.getFont("Amble-Regular", 0, 20);
        started = false;
        priority = 1;
        frame = place.getSpriteSheet("messageFrame");
        r = RandomGenerator.create();
        rows = 3;
        speakers = new ArrayList<>(1);
        portraits = new ArrayList<>(1);
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
        stop = false;
        events.clear();
        branches.clear();
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

    public void startFromFile(String file) {
        if (!started) {
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
                int currIndex = 0, lineNum = 0, lineIndex, last;
                int type = TYPE_NORMAL;
                FontHandler font = fonts[STYLE_NORMAL];
                float defSpeed = speed;
                TextRow tmp;
                branches.add(events);
                Branch currentBranch = events;
                while ((line = read.readLine()) != null) {
                    if (line.length() > 1 && line.charAt(0) == '#') {
                        switch (line.charAt(1)) {
                            case '#':   //COMMENT
                                break;
                            case 'B':   //NEW DIALOG BRANCH
                                currentBranch.setLength(currIndex);
                                currIndex = 0;
                                lineNum = 0;
                                Branch b = new Branch();
                                currentBranch = b;
                                branches.add(b);
                                break;
                        }
                    } else {
                        last = 0;
                        tmp = new TextRow(lineNum);
                        if (line.length() != 0) {
                            for (lineIndex = 0; lineIndex < line.length();) {
                                if (line.charAt(lineIndex) == '$') {
                                    switch (line.substring(lineIndex + 1, lineIndex + 3).toLowerCase()) {
                                        case "ve":   //CHANGE SPEED
                                            for (int j = lineIndex + 3; j < line.length(); j++) {
                                                if (line.charAt(j) == '$') {
                                                    tmp.addEvent(new PropertyChanger(currIndex + lineIndex, PROP_SPEED,
                                                            Float.parseFloat(line.substring(lineIndex + 3, j))));
                                                    line = line.substring(0, lineIndex) + line.substring(j + 1);
                                                    break;
                                                }
                                            }
                                            break;
                                        case "vn":   //CHANGE SPEED
                                            tmp.addEvent(new PropertyChanger(currIndex + lineIndex, PROP_SPEED, defSpeed));
                                            line = line.substring(0, lineIndex) + line.substring(lineIndex + 3);
                                            break;
                                        case "au":   //SPEAKER'S NAME
                                            for (int j = lineIndex + 3; j < line.length(); j++) {
                                                if (line.charAt(j) == '$') {
                                                    tmp.addEvent(new PropertyChanger(currIndex + lineIndex, PROP_SPEAKER,
                                                            Integer.parseInt(line.substring(lineIndex + 3, j))));
                                                    line = line.substring(0, lineIndex) + line.substring(j + 1);
                                                    break;
                                                }
                                            }
                                            break;
                                        case "po":   //PORTRAIT
                                            for (int j = lineIndex + 3; j < line.length(); j++) {
                                                if (line.charAt(j) == '$') {
                                                    tmp.addEvent(new PropertyChanger(currIndex + lineIndex, PROP_PORTRAIT,
                                                            Integer.parseInt(line.substring(lineIndex + 3, j))));
                                                    line = line.substring(0, lineIndex) + line.substring(j + 1);
                                                    break;
                                                }
                                            }
                                            break;
                                        case "ex":   //EXPRESSION
                                            for (int j = lineIndex + 3; j < line.length(); j++) {
                                                if (line.charAt(j) == '$') {
                                                    tmp.addEvent(new PropertyChanger(currIndex + lineIndex, PROP_EXPRESSION,
                                                            Integer.parseInt(line.substring(lineIndex + 3, j))));
                                                    line = line.substring(0, lineIndex) + line.substring(j + 1);
                                                    break;
                                                }
                                            }
                                            break;
                                        case "ju":   //JUMP
                                            for (int j = lineIndex + 3; j < line.length(); j++) {
                                                if (line.charAt(j) == '$') {
                                                    tmp.addEvent(new PropertyChanger(currIndex + lineIndex - 1, PROP_JUMP,
                                                            Integer.parseInt(line.substring(lineIndex + 3, j))));
                                                    line = line.substring(0, lineIndex) + line.substring(j + 1);
                                                    break;
                                                }
                                            }
                                            break;
                                        case "qu":   //QUESTION
                                            for (int j = lineIndex + 3; j < line.length(); j++) {
                                                if (line.charAt(j) == '$') {
                                                    tab = line.substring(lineIndex + 3, j).split(":");
                                                    String[] answers = new String[tab.length / 2];
                                                    int[] jumps = new int[answers.length];
                                                    for (int i = 0; i < answers.length; i++) {
                                                        answers[i] = tab[2 * i];
                                                        jumps[i] = Integer.parseInt(tab[2 * i + 1]);
                                                    }
                                                    tmp.addEvent(new PropertyChanger(currIndex + lineIndex - 1, answers, jumps));
                                                    line = line.substring(0, lineIndex) + line.substring(j + 1);
                                                    break;
                                                }
                                            }
                                            break;
                                        case "fl":   //FLUSH LINES
                                            if (last != lineIndex) {
                                                tmp.addEvent(generateEvent(type, line.substring(last, lineIndex), currIndex + last, font.getWidth(line.substring(0, last)), lineNum, font));
                                            }
                                            tmp.addEvent(new PropertyChanger(currIndex + lineIndex - 1, PROP_FLUSH, 0));
                                            line = line.substring(0, lineIndex) + line.substring(lineIndex + 3);
                                            last = lineIndex;
                                            break;
                                        case "pl":   //PLAIN TEXT
                                            if (last != lineIndex) {
                                                tmp.addEvent(generateEvent(type, line.substring(last, lineIndex), currIndex + last, font.getWidth(line.substring(0, last)), lineNum, font));
                                            }
                                            font = fonts[STYLE_NORMAL];
                                            line = line.substring(0, lineIndex) + line.substring(lineIndex + 3);
                                            last = lineIndex;
                                            break;
                                        case "bo":   //BOLD TEXT
                                            if (last != lineIndex) {
                                                tmp.addEvent(generateEvent(type, line.substring(last, lineIndex), currIndex + last, font.getWidth(line.substring(0, last)), lineNum, font));
                                            }
                                            font = fonts[STYLE_BOLD];
                                            line = line.substring(0, lineIndex) + line.substring(lineIndex + 3);
                                            last = lineIndex;
                                            break;
                                        case "it":   //ITALIC TEXT
                                            if (last != lineIndex) {
                                                tmp.addEvent(generateEvent(type, line.substring(last, lineIndex), currIndex + last, font.getWidth(line.substring(0, last)), lineNum, font));
                                            }
                                            font = fonts[STYLE_ITALIC];
                                            line = line.substring(0, lineIndex) + line.substring(lineIndex + 3);
                                            last = lineIndex;
                                            break;
                                        case "no":   //NORMAL TEXT
                                            if (last != lineIndex) {
                                                tmp.addEvent(generateEvent(type, line.substring(last, lineIndex), currIndex + last, font.getWidth(line.substring(0, last)), lineNum, font));
                                            }
                                            type = TYPE_NORMAL;
                                            line = line.substring(0, lineIndex) + line.substring(lineIndex + 3);
                                            last = lineIndex;
                                            break;
                                        case "sh":   //SHAKY TEXT
                                            if (last != lineIndex) {
                                                tmp.addEvent(generateEvent(type, line.substring(last, lineIndex), currIndex + last, font.getWidth(line.substring(0, last)), lineNum, font));
                                            }
                                            type = TYPE_SHAKY;
                                            line = line.substring(0, lineIndex) + line.substring(lineIndex + 3);
                                            last = lineIndex;
                                            break;
                                        case "me":   //MELODIC TEXT
                                            if (last != lineIndex) {
                                                tmp.addEvent(generateEvent(type, line.substring(last, lineIndex), currIndex + last, font.getWidth(line.substring(0, last)), lineNum, font));
                                            }
                                            type = TYPE_MUSIC;
                                            line = line.substring(0, lineIndex) + line.substring(lineIndex + 3);
                                            last = lineIndex;
                                            break;
                                        default:
                                            throw new IOException("UNKNOWN SYMBOL \"" + line.charAt(lineIndex + 1) + "\"!\nLine : " + line + "");
                                    }
                                } else {
                                    lineIndex++;
                                }
                            }
                            if (last != lineIndex) {
                                tmp.addEvent(generateEvent(type, line.substring(last, lineIndex), currIndex + last, font.getWidth(line.substring(0, last)), lineNum, font));
                            }
                        } else {
                            tmp.addEvent(new TextRenderer(" ", currIndex, 0, lineNum, font));
                            currIndex++;
                        }
                        currIndex += line.length();
                        lineNum++;
                        tmp.setEnd(currIndex - 1);
                        currentBranch.add(tmp);
                    }
                }
                started = true;
                currentBranch.setLength(currIndex);
                index = 0;
                change = 100;
                rowsInPlace = 0;
                jumpTo = -1;
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

            drawGui();

            Drawer.returnToCentralPoint();

            if (index != 0) {
                if (portraits.get(portrait).onRight) {
                    Drawer.translate(getCamera().getWidth() - 3 * tile, 0);
                    portraits.get(portrait).image.renderPieceMirrored(expression);
                } else {
                    Drawer.translate(3 * tile, 0);
                    portraits.get(portrait).image.renderPiece(expression);
                }
            }

            Drawer.returnToCentralPoint();

            if (question) {
                drawQuestion();
            }

            Drawer.returnToCentralPoint();

            Drawer.bindFontTexture();

            littleFont.drawLine(speakers.get(speaker) + ":", Place.tileHalf, tile / 5, Color.gray);

            Drawer.translate(Place.tileHalf, 2 * tile / 3
                    - (int) (Math.max((deltaLines + (flushing ? change : 0)) * fonts[0].getHeight() * 1.2, 0)));

            time++;
            if (time == 60) {
                time = 0;
            }
            realSpeed = speed * (controler.isKeyPressed(MyController.RUN) ? 2f : 1f);

            if (flushing) {
                handleFlushing();
            } else {
                if (!flushReady) {
                    if (index < events.length) {
                        index += realSpeed;
                        change = 1;
                    } else if (jumpTo >= 0) {
                        flushReady = true;
                    } else if (controler.isKeyClicked(MyController.JUMP)) {
                        stopTextViewing();
                    }
                } else {
                    if (!question) {
                        if (controler.isKeyClicked(MyController.JUMP)) {
                            flushing = true;
                            flushReady = false;
                            change = 0;
                        }
                    } else {
                        handleQuestion();
                    }
                }
            }
            events.stream().forEach((te) -> {
                handleEvent(te);
            });
            Drawer.refreshForRegularDrawing();
            glPopMatrix();
        }
    }

    private void handleFlushing() {
        change += (0.75 + realSpeed / 4) / 15;
        if (change >= 1) {
            deltaLines++;
            rowsInPlace--;
            if (rowsInPlace == 0) {
                flushing = false;
                change = 1;
                if (jumpTo >= 0) {
                    events = branches.get(jumpTo);
                    index = 0;
                    deltaLines = 0;
                    jumpTo = -1;
                }
            } else {
                change = 0;
            }
        }
    }

    private void drawGui() {
        int tile = Place.tileSize;
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

        if (!question) {
            if (flushReady) {
                Drawer.translate(-Place.tileHalf, (int) (5 * Math.sin((float) time / 30 * Math.PI)));
                frame.renderPiece(3, 0);
            }
            if (index >= events.length && jumpTo < 0) {
                Drawer.translate(-Place.tileHalf, 0);
                frame.renderPiece(3, 1);
            }
        }
    }

    private void drawQuestion() {
        int tile = Place.tileSize;
        int i, maxL = 0, len;
        for (i = 0; i < answerText.length; i++) {
            len = fonts[0].getWidth(answerText[i]);
            if (len > maxL) {
                maxL = len;
            }
        }
        maxL = Math.max(maxL + tile, 3 * tile);
        Drawer.translate(getCamera().getWidth() - maxL - Place.tileHalf, 0);
        for (i = answerText.length - 1; i >= 0; i--) {
            Drawer.translate(0, -tile - 2);
            if (answer == i) {
                Drawer.translate(-tile, 0);
                frame.renderPiece(0, 3);
                Drawer.translate(tile, 0);
            }
            frame.renderPiece(1, 3);
            Drawer.translate(tile, 0);
            frame.renderPieceResized(2, 3, maxL - 2 * tile, tile);
            Drawer.translate(maxL - 2 * tile, 0);
            frame.renderPiece(3, 3);
            Drawer.translate(-maxL + tile, 0);
            len = (maxL - fonts[0].getWidth(answerText[i])) / 2;
            Drawer.bindFontTexture();
            fonts[0].drawLine(answerText[i], len, tile / 4, Color.black);
            Drawer.refreshColor();
        }
        if (answer < 0) {
            Drawer.translate(-tile * 0.7f, Place.tileHalf);
            Drawer.setColor(Color.gray);
            frame.renderPiece(0, 3);
            Drawer.refreshColor();
            Drawer.translate(tile * 0.7f, -Place.tileHalf);
        }
    }

    private void handleQuestion() {
        if (answer != -1) {
            if (controler.isKeyClicked(MyController.UP)) {
                answer--;
                if (answer < 0) {
                    answer = answerText.length - 1;
                }
            }
            if (controler.isKeyClicked(MyController.DOWN)) {
                answer++;
                if (answer > answerText.length - 1) {
                    answer = 0;
                }
            }
            if (controler.isKeyClicked(MyController.JUMP)) {
                jumpTo = answerJump[answer];
                question = false;
                flushing = true;
                flushReady = false;
                change = 0;
            }
        } else {
            if (controler.isKeyClicked(MyController.UP)) {
                answer = 0;
            }
            if (controler.isKeyClicked(MyController.DOWN)) {
                answer = 1;
            }
        }
    }

    private void handleEvent(TextRow te) {
        te.changers((int) index);
        if (te.isEnding((int) index) && !flushReady && !flushing) {
            rowsInPlace++;
            if (rowsInPlace == rows && !stop) {
                flushReady = true;
            }
        }
        if (te.rowNum >= deltaLines && te.rowNum <= deltaLines + rows) {
            te.event((int) index);
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

    private class Portrait {

        SpriteSheet image;
        boolean onRight;

        Portrait(SpriteSheet image, boolean onRight) {
            this.image = image;
            this.onRight = onRight;
        }
    }

    private class Branch extends ArrayList<TextRow> {

        int length;

        public void setLength(int l) {
            length = l;
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
        private String[] answers;
        private int[] jumps;

        public PropertyChanger(int start, int type, float quatity) {
            super(start, 0);
            this.type = type;
            this.quatity = Math.min(quatity, 10);
        }

        public PropertyChanger(int start, String[] answers, int[] jumps) {
            super(start, 0);
            this.type = PROP_QUESTION;
            this.quatity = 0;
            this.answers = answers;
            this.jumps = jumps;
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
                    case PROP_JUMP:
                        jumpTo = (int) quatity;
                        break;
                    case PROP_QUESTION:
                        question = true;
                        answerText = answers;
                        answerJump = jumps;
                        answer = -1;
                        flushReady = true;
                        rowsInPlace++;
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
                    font.drawLine(tmp, x + xd, (int) (y + 5 * Math.sin((float) dt / 30 * Math.PI)),
                            changeColor(Color.black, lineNum));
                    xd += font.getWidth(tmp);
                    dt += 4;
                }
            }
        }
    }
}
