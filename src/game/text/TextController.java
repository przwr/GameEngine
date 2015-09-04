/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.text;

import engine.Drawer;
import engine.Executive;
import engine.Main;
import game.gameobject.Entity;
import game.gameobject.GUIObject;
import game.place.Place;
import gamecontent.MyController;
import org.newdawn.slick.Color;
import sprites.SpriteSheet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Wojtek
 */
public class TextController extends GUIObject {

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_SHAKY = 1;
    private static final int TYPE_MUSIC = 2;

    private static final int STYLE_NORMAL = 0;
    private static final int STYLE_BOLD = 1;
    private static final int STYLE_ITALIC = 2;

    private final FontHandler[] fonts;
    private final FontHandler littleFont;
    private final ArrayList<Branch> branches;
    private final SpriteSheet frame;
    private final ArrayList<String> speakers;
    private final ArrayList<Portrait> portraits;
    private final ArrayList<String> jumpPlacements;
    private final ArrayList<Color> colors;
    private Branch events;
    private float index, speed, change, realSpeed;
    private int time, rows, deltaLines, rowsInPlace, speaker, portrait, expression, answer, jumpTo;
    private boolean started, flushing, flushReady, stop, question, firstStep;
    private Entity[] locked;
    private String[] answerText;
    private String[] answerJump;

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
        frame = place.getSpriteSheet("messageFrame", "");
        rows = 3;
        speakers = new ArrayList<>(1);
        portraits = new ArrayList<>(1);
        jumpPlacements = new ArrayList<>(1);
        colors = new ArrayList<>(1);
        firstStep = true;
    }

    public void startFromFile(String file) {
        if (!started) {
            try (BufferedReader read = new BufferedReader(new FileReader("res/text/" + file + ".txt"))) {
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
                            portraits.add(new Portrait(place.getSpriteSheet(tab[1], ""), tab[2].equals("1")));
                            break;
                        case "co":
                            colors.add(new Color(Integer.parseInt(tab[1], 16)));
                            break;
                    }
                }
                if (speakers.isEmpty()) {
                    speakers.add("???");
                }
                if (colors.isEmpty()) {
                    colors.add(Color.black);
                }
                int currIndex = 0, lineNum = 0, lineIndex, last;
                int type = TYPE_NORMAL;
                FontHandler font = fonts[STYLE_NORMAL];
                float defSpeed = speed;
                Color color = colors.get(0);
                TextRow tmp;
                branches.add(events);
                Branch currentBranch = events;
                jumpPlacements.add("0");
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
                                jumpPlacements.add(line.substring(2));
                                break;
                        }
                    } else {
                        last = 0;
                        tmp = new TextRow(lineNum);
                        if (line.length() != 0) {
                            for (lineIndex = 0; lineIndex < line.length(); ) {
                                if (line.charAt(lineIndex) == '$') {
                                    switch (line.substring(lineIndex + 1, lineIndex + 3).toLowerCase()) {
                                        case "ve":   //CHANGE SPEED
                                            for (int j = lineIndex + 3; j < line.length(); j++) {
                                                if (line.charAt(j) == '$') {
                                                    tmp.addEvent(new PropertyChanger(currIndex + lineIndex, PropertyChanger.PROP_SPEED,
                                                            Float.parseFloat(line.substring(lineIndex + 3, j)), this));
                                                    line = line.substring(0, lineIndex) + line.substring(j + 1);
                                                    break;
                                                }
                                            }
                                            break;
                                        case "vn":   //NORMALIZE SPEED
                                            tmp.addEvent(new PropertyChanger(currIndex + lineIndex, PropertyChanger.PROP_SPEED, defSpeed, this));
                                            line = line.substring(0, lineIndex) + line.substring(lineIndex + 3);
                                            break;
                                        case "au":   //SPEAKER'S NAME
                                            for (int j = lineIndex + 3; j < line.length(); j++) {
                                                if (line.charAt(j) == '$') {
                                                    tmp.addEvent(new PropertyChanger(currIndex + lineIndex, PropertyChanger.PROP_SPEAKER,
                                                            Integer.parseInt(line.substring(lineIndex + 3, j)), this));
                                                    line = line.substring(0, lineIndex) + line.substring(j + 1);
                                                    break;
                                                }
                                            }
                                            break;
                                        case "po":   //PORTRAIT
                                            for (int j = lineIndex + 3; j < line.length(); j++) {
                                                if (line.charAt(j) == '$') {
                                                    tmp.addEvent(new PropertyChanger(currIndex + lineIndex, PropertyChanger.PROP_PORTRAIT,
                                                            Integer.parseInt(line.substring(lineIndex + 3, j)), this));
                                                    line = line.substring(0, lineIndex) + line.substring(j + 1);
                                                    break;
                                                }
                                            }
                                            break;
                                        case "ex":   //EXPRESSION
                                            for (int j = lineIndex + 3; j < line.length(); j++) {
                                                if (line.charAt(j) == '$') {
                                                    tmp.addEvent(new PropertyChanger(currIndex + lineIndex, PropertyChanger.PROP_EXPRESSION,
                                                            Integer.parseInt(line.substring(lineIndex + 3, j)), this));
                                                    line = line.substring(0, lineIndex) + line.substring(j + 1);
                                                    break;
                                                }
                                            }
                                            break;
                                        case "ju":   //ATTACK
                                            for (int j = lineIndex + 3; j < line.length(); j++) {
                                                if (line.charAt(j) == '$') {
                                                    tmp.addEvent(new Jumper(currIndex + lineIndex - 1, line.substring(lineIndex + 3, j), this));
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
                                                    String[] jumps = new String[answers.length];
                                                    for (int i = 0; i < answers.length; i++) {
                                                        answers[i] = tab[2 * i];
                                                        jumps[i] = tab[2 * i + 1];
                                                    }
                                                    tmp.addEvent(new QuestionMaker(currIndex + lineIndex - 1, answers, jumps, this));
                                                    line = line.substring(0, lineIndex) + line.substring(j + 1);
                                                    break;
                                                }
                                            }
                                            break;
                                        case "fl":   //FLUSH LINES
                                            if (last != lineIndex) {
                                                tmp.addEvent(generateEvent(type, line.substring(last, lineIndex), currIndex + last,
                                                        font.getWidth(line.substring(0, last)), lineNum, color, font));
                                            }
                                            tmp.addEvent(new PropertyChanger(currIndex + lineIndex - 1, PropertyChanger.PROP_FLUSH, 0, this));
                                            line = line.substring(0, lineIndex) + line.substring(lineIndex + 3);
                                            last = lineIndex;
                                            break;
                                        case "co":   //CHANGE COLOR
                                            if (last != lineIndex) {
                                                tmp.addEvent(generateEvent(type, line.substring(last, lineIndex), currIndex + last,
                                                        font.getWidth(line.substring(0, last)), lineNum, color, font));
                                            }
                                            for (int j = lineIndex + 3; j < line.length(); j++) {
                                                if (line.charAt(j) == '$') {
                                                    color = colors.get(Integer.parseInt(line.substring(lineIndex + 3, j), 16));
                                                    line = line.substring(0, lineIndex) + line.substring(j + 1);
                                                    last = lineIndex;
                                                    break;
                                                }
                                            }
                                            break;
                                        case "cn":   //PLAIN TEXT
                                            if (last != lineIndex) {
                                                tmp.addEvent(generateEvent(type, line.substring(last, lineIndex), currIndex + last,
                                                        font.getWidth(line.substring(0, last)), lineNum, color, font));
                                            }
                                            color = colors.get(0);
                                            line = line.substring(0, lineIndex) + line.substring(lineIndex + 3);
                                            last = lineIndex;
                                            break;
                                        case "pl":   //PLAIN TEXT
                                            if (last != lineIndex) {
                                                tmp.addEvent(generateEvent(type, line.substring(last, lineIndex), currIndex + last,
                                                        font.getWidth(line.substring(0, last)), lineNum, color, font));
                                            }
                                            font = fonts[STYLE_NORMAL];
                                            line = line.substring(0, lineIndex) + line.substring(lineIndex + 3);
                                            last = lineIndex;
                                            break;
                                        case "bo":   //BOLD TEXT
                                            if (last != lineIndex) {
                                                tmp.addEvent(generateEvent(type, line.substring(last, lineIndex), currIndex + last,
                                                        font.getWidth(line.substring(0, last)), lineNum, color, font));
                                            }
                                            font = fonts[STYLE_BOLD];
                                            line = line.substring(0, lineIndex) + line.substring(lineIndex + 3);
                                            last = lineIndex;
                                            break;
                                        case "it":   //ITALIC TEXT
                                            if (last != lineIndex) {
                                                tmp.addEvent(generateEvent(type, line.substring(last, lineIndex),
                                                        currIndex + last, font.getWidth(line.substring(0, last)), lineNum, color, font));
                                            }
                                            font = fonts[STYLE_ITALIC];
                                            line = line.substring(0, lineIndex) + line.substring(lineIndex + 3);
                                            last = lineIndex;
                                            break;
                                        case "no":   //NORMAL TEXT
                                            if (last != lineIndex) {
                                                tmp.addEvent(generateEvent(type, line.substring(last, lineIndex), currIndex + last,
                                                        font.getWidth(line.substring(0, last)), lineNum, color, font));
                                            }
                                            type = TYPE_NORMAL;
                                            line = line.substring(0, lineIndex) + line.substring(lineIndex + 3);
                                            last = lineIndex;
                                            break;
                                        case "sh":   //SHAKY TEXT
                                            if (last != lineIndex) {
                                                tmp.addEvent(generateEvent(type, line.substring(last, lineIndex),
                                                        currIndex + last, font.getWidth(line.substring(0, last)), lineNum, color, font));
                                            }
                                            type = TYPE_SHAKY;
                                            line = line.substring(0, lineIndex) + line.substring(lineIndex + 3);
                                            last = lineIndex;
                                            break;
                                        case "me":   //MELODIC TEXT
                                            if (last != lineIndex) {
                                                tmp.addEvent(generateEvent(type, line.substring(last, lineIndex),
                                                        currIndex + last, font.getWidth(line.substring(0, last)), lineNum, color, font));
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
                                tmp.addEvent(generateEvent(type, line.substring(last, lineIndex),
                                        currIndex + last, font.getWidth(line.substring(0, last)), lineNum, color, font));
                            }
                        } else {
                            tmp.addEvent(new TextRenderer(" ", currIndex, 0, lineNum, color, font, this));
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
        firstStep = true;
        events.clear();
        branches.clear();
        jumpPlacements.clear();
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

    private int jumpLocation(String pointer) {
        for (int i = 0; i < jumpPlacements.size(); i++) {
            if (jumpPlacements.get(i).equals(pointer)) {
                return i;
            }
        }
        return 0;
    }

    public void addExternalEvent(Executive event, String branch, boolean onStart) {
        if (started) {
            Branch b = branches.get(jumpLocation(branch));
            if (onStart) {
                b.startEvent = event;
            } else {
                b.endEvent = event;
            }
        }
    }

    private TextEvent generateEvent(int type, String text, int start, int xStart, int lineNum, Color color, FontHandler font) {
        TextEvent ret = null;
        switch (type) {
            case TYPE_NORMAL:
                ret = new TextRenderer(text, start, xStart, lineNum, color, font, this);
                break;
            case TYPE_SHAKY:
                ret = new ShakyTextRenderer(text, start, xStart, lineNum, color, font, this);
                break;
            case TYPE_MUSIC:
                ret = new MusicTextRenderer(text, start, xStart, lineNum, color, font, this);
                break;
        }
        return ret;
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (started) {
            int tile = Place.tileSize;
            glPushMatrix();

            glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);


            glScaled(1 / Place.getCurrentScale(), 1 / Place.getCurrentScale(), 1);

            glTranslatef(xEffect, yEffect + getCamera().getHeight() - 3.5f * tile, 0);

            Drawer.setCentralPoint();

            drawGui();

            Drawer.returnToCentralPoint();

            if (!firstStep) {
                if (portraits.get(portrait).onRight) {
                    Drawer.translate(getCamera().getWidth() - 3 * tile, 0);
                    portraits.get(portrait).image.renderPieceMirrored(expression);
                } else {
                    Drawer.translate(3 * tile, 0);
                    portraits.get(portrait).image.renderPiece(expression);
                }
            } else {
                events.startingEvent();
                firstStep = false;
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
            realSpeed = speed * (playerController.isKeyPressed(MyController.RUN) ? 2f : 1f);

            if (flushing) {
                handleFlushing();
            } else {
                if (!flushReady) {
                    if (index < events.length) {
                        index += realSpeed;
                        change = 1;
                    } else if (jumpTo >= 0) {
                        flushReady = true;
                    } else if (playerController.isKeyClicked(MyController.ATTACK)) {
                        events.endingEvent();
                        stopTextViewing();
                    }
                } else {
                    if (!question) {
                        if (playerController.isKeyClicked(MyController.ATTACK)) {
                            flushing = true;
                            flushReady = false;
                            change = 0;
                        }
                    } else {
                        handleQuestion();
                    }
                }
            }
            events.stream().forEach(this::handleEvent);
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
                    events.endingEvent();
                    events = branches.get(jumpTo);
                    events.startingEvent();
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
            if (playerController.isKeyClicked(MyController.UP)) {
                answer--;
                if (answer < 0) {
                    answer = answerText.length - 1;
                }
            }
            if (playerController.isKeyClicked(MyController.DOWN)) {
                answer++;
                if (answer > answerText.length - 1) {
                    answer = 0;
                }
            }
            if (playerController.isKeyClicked(MyController.ATTACK)) {
                jumpTo = jumpLocation(answerJump[answer]);
                question = false;
                flushing = true;
                flushReady = false;
                change = 0;
            }
        } else {
            if (playerController.isKeyClicked(MyController.UP)) {
                answer = 0;
            }
            if (playerController.isKeyClicked(MyController.DOWN)) {
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

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    private void lockEntities(Entity[] locked) {
        this.locked = locked;
        for (Entity e : locked) {
            e.setUnableToMove(true);
        }
    }

    public void lockEntity(Entity locked) {
        lockEntities(new Entity[]{locked});
    }

    void flushText() {
        flushReady = true;
        rowsInPlace++;
    }

    int getCurrentRow() {
        return deltaLines;
    }

    float getChange() {
        return change;
    }

    int getTime() {
        return time;
    }

    boolean isFlushing() {
        return flushing;
    }

    void setIndex(int index) {
        this.index = index;
    }

    void setSpeed(float speed) {
        this.speed = speed;
    }

    void setSpeaker(int speaker) {
        this.speaker = speaker;
    }

    void setPortrait(int portrait) {
        this.portrait = portrait;
    }

    void setExpression(int expression) {
        this.expression = expression;
    }

    void setJumpLocation(String location) {
        jumpTo = jumpLocation(location);
    }

    void setQuestion(String[] answers, String[] jumpLocations) {
        question = true;
        answerText = answers;
        answerJump = jumpLocations;
        answer = -1;
        flushReady = true;
        rowsInPlace++;
    }

    private class Portrait {

        final SpriteSheet image;
        final boolean onRight;

        Portrait(SpriteSheet image, boolean onRight) {
            this.image = image;
            this.onRight = onRight;
        }
    }

    private class Branch extends ArrayList<TextRow> {

        int length;
        Executive startEvent, endEvent;

        public void setLength(int l) {
            length = l;
        }

        public void startingEvent() {
            if (startEvent != null) {
                startEvent.execute();
            }
        }

        public void endingEvent() {
            if (endEvent != null) {
                endEvent.execute();
            }
        }
    }

    private class TextRow {

        private final int rowNum;
        private final ArrayList<TextEvent> list;
        private final ArrayList<TextEvent> changers;
        private int end;
        private boolean ending;

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
            list.stream().forEach((te) -> te.event(start, rowNum));
        }

        public void changers(int start) {
            changers.stream().forEach((te) -> te.event(start, rowNum));
        }
    }
}
