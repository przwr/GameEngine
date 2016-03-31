/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.text;

import engine.systemcommunication.Time;
import engine.utilities.Drawer;
import engine.utilities.Executive;
import game.Settings;
import game.gameobject.GUIObject;
import game.gameobject.entities.Entity;
import game.place.Place;
import gamecontent.MyController;
import org.newdawn.slick.Color;
import sprites.SpriteSheet;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Wojtek
 */
public class TextController extends GUIObject {

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_SHAKY = 1;
    private static final int TYPE_MUSIC = 2;
    private static final int TYPE_NERVOUS = 3;

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
    private final ArrayList<Statement> statements;
    private final ArrayList<Event> events;
    private final ArrayList<Writer> writers;
    private final int optimalWidth, optimalHeight;
    private Branch branch;
    private float index, speed, change, realSpeed, time;
    private int rows, deltaLines, rowsInPlace, speaker, portrait, expression, answer, jumpTo;
    private boolean started, flushing, flushReady, stop, question, firstStep, action, terminate;
    private Entity[] locked;
    private String[] answerText;
    private String[] answerJump;

    public TextController(Place place) {
        super("TextController", place);
        branches = new ArrayList<>();
        branch = new Branch();
        fonts = new FontHandler[3];
        fonts[0] = Settings.fonts.getFont("Amble-Regular", 35);//PLAIN
        fonts[1] = Settings.fonts.changeStyle(fonts[0], 1);//BOLD
        fonts[2] = Settings.fonts.changeStyle(fonts[0], 2);//ITALIC
        littleFont = Settings.fonts.getFont("Amble-Regular", 20);
        started = false;
        priority = 1;
        frame = place.getSpriteSheet("messageFrame", "");
        rows = 3;
        speakers = new ArrayList<>(1);
        portraits = new ArrayList<>(1);
        jumpPlacements = new ArrayList<>(1);
        colors = new ArrayList<>(1);
        statements = new ArrayList<>();
        events = new ArrayList<>();
        writers = new ArrayList<>();
        firstStep = true;

        optimalWidth = 1024;
        optimalHeight = 768;
    }

    public void startFromFile(String file) {
        if (!started) {
            try {
                FileInputStream stream = new FileInputStream("res/text/" + file + ".dia");
                InputStreamReader sr = new InputStreamReader(stream, StandardCharsets.UTF_8);
                BufferedReader read = new BufferedReader(sr);
                String line;
                ArrayList<String> tmplist = new ArrayList<>();
                while ((line = read.readLine()) != null) {
                    tmplist.add(line);
                }
                String[] list = new String[tmplist.size()];
                for (int i = 0; i < tmplist.size(); i++) {
                    list[i] = tmplist.get(i);
                }
                startFromText(list);
                read.close();
                sr.close();
                stream.close();
            } catch (IOException e) {
            }
        }
    }

    public void startFromFile(String file, String startingBranch) {
        if (!started) {
            startFromFile(file);
            branch = branches.get(getJumpLocation(startingBranch));
        }
    }

    public void startFromText(String[] text) {
        if (!started) {
            String line;
            String[] tab;
            speed = 1;
            int num = 0;
            if (colors.isEmpty()) {
                colors.add(new Color(Color.black));
            }
            if (text[0].substring(1).equals("{")) {
                while (!(line = text[num++]).equals("}")) {
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
            }
            if (speakers.isEmpty()) {
                speakers.add("");
            }
            int lineNum = 0, lineIndex, last;
            TextEvent lastEvent = null;
            int type = TYPE_NORMAL;
            FontHandler font = fonts[STYLE_NORMAL];
            float defSpeed = speed;
            Color color = colors.get(0);
            TextRow tmp;
            branch.length = 1000;
            branches.add(branch);
            Branch currentBranch = branch;
            jumpPlacements.add("0");
            for (; num < text.length; num++) {
                line = text[num];
                if (line.length() > 1 && line.charAt(0) == '#') {
                    switch (line.charAt(1)) {
                        case '#':   //COMMENT
                            break;
                        case 'B':   //NEW DIALOG BRANCH
                            lineNum = 0;
                            Branch b = new Branch();
                            currentBranch = b;
                            lastEvent = null;
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
                                String symbol = line.substring(lineIndex + 1, lineIndex + 3).toLowerCase();
                                switch (symbol) {
                                    case "ve":   //CHANGE SPEED
                                        for (int j = lineIndex + 3; j < line.length(); j++) {
                                            if (line.charAt(j) == '$') {
                                                if (last != lineIndex) {
                                                    lastEvent = generateEvent(type, line.substring(last, lineIndex), lastEvent,
                                                            lineNum, color, font);
                                                    tmp.addEvent(lastEvent);
                                                }
                                                lastEvent = new PropertyChanger(lastEvent, PropertyChanger.PROP_SPEED,
                                                        Float.parseFloat(line.substring(lineIndex + 3, j)), this);
                                                tmp.addEvent(lastEvent);
                                                line = line.substring(0, lineIndex) + line.substring(j + 1);
                                                last = lineIndex;
                                                break;
                                            }
                                        }
                                        break;
                                    case "vn":   //NORMALIZE SPEED
                                        if (last != lineIndex) {
                                            lastEvent = generateEvent(type, line.substring(last, lineIndex), lastEvent,
                                                    lineNum, color, font);
                                            tmp.addEvent(lastEvent);
                                        }
                                        lastEvent = new PropertyChanger(lastEvent, PropertyChanger.PROP_SPEED, defSpeed, this);
                                        tmp.addEvent(lastEvent);
                                        line = line.substring(0, lineIndex) + line.substring(lineIndex + 3);
                                        last = lineIndex;
                                        break;
                                    case "au":   //SPEAKER'S NAME
                                    case "po":   //PORTRAIT
                                        for (int j = lineIndex + 3; j < line.length(); j++) {
                                            if (line.charAt(j) == '$') {
                                                if (last != lineIndex) {
                                                    lastEvent = generateEvent(type, line.substring(last, lineIndex), lastEvent,
                                                            lineNum, color, font);
                                                    tmp.addEvent(lastEvent);
                                                }
                                                switch (symbol) {
                                                    case "au":
                                                        lastEvent = new PropertyChanger(lastEvent, PropertyChanger.PROP_SPEAKER,
                                                                Integer.parseInt(line.substring(lineIndex + 3, j)), this);
                                                        break;
                                                    case "po":
                                                        lastEvent = new PropertyChanger(lastEvent, PropertyChanger.PROP_PORTRAIT,
                                                                Integer.parseInt(line.substring(lineIndex + 3, j)), this);
                                                        break;
                                                }
                                                tmp.addEvent(lastEvent);
                                                line = line.substring(0, lineIndex) + line.substring(j + 1);
                                                last = lineIndex;
                                                break;
                                            }
                                        }
                                        break;
                                    case "ex":   //EXPRESSION
                                        for (int j = lineIndex + 3; j < line.length(); j++) {
                                            if (line.charAt(j) == '$') {
                                                /*if (last != lineIndex) {
                                                 lastEvent = generateEvent(type, line.substring(last, lineIndex), lastEvent,
                                                 lineNum, color, font);
                                                 tmp.addEvent(lastEvent);
                                                 }*/
                                                lastEvent = new PropertyChanger(lastEvent, PropertyChanger.PROP_EXPRESSION,
                                                        Integer.parseInt(line.substring(lineIndex + 3, j)), this);
                                                tmp.addEvent(lastEvent);
                                                line = line.substring(0, lineIndex) + line.substring(j + 1);
                                                last = lineIndex;
                                                break;
                                            }
                                        }
                                        break;
                                    case "ju":   //JUMP TO BRANCH
                                        for (int j = lineIndex + 3; j < line.length(); j++) {
                                            if (line.charAt(j) == '$') {
                                                if (last != lineIndex) {
                                                    lastEvent = generateEvent(type, line.substring(last, lineIndex), lastEvent,
                                                            lineNum, color, font);
                                                    tmp.addEvent(lastEvent);
                                                }
                                                lastEvent = new Jumper(lastEvent, line.substring(lineIndex + 3, j), this);
                                                tmp.addEvent(lastEvent);
                                                line = line.substring(0, lineIndex) + line.substring(j + 1);
                                                last = lineIndex;
                                                break;
                                            }
                                        }
                                        break;
                                    case "ev":   //TRIGGER EVENT
                                        for (int j = lineIndex + 3; j < line.length(); j++) {
                                            if (line.charAt(j) == '$') {
                                                if (last != lineIndex) {
                                                    lastEvent = generateEvent(type, line.substring(last, lineIndex), lastEvent,
                                                            lineNum, color, font);
                                                    tmp.addEvent(lastEvent);
                                                }
                                                lastEvent = new EventMaker(lastEvent, line.substring(lineIndex + 3, j), this);
                                                tmp.addEvent(lastEvent);
                                                line = line.substring(0, lineIndex) + line.substring(j + 1);
                                                last = lineIndex;
                                                break;
                                            }
                                        }
                                        break;
                                    case "wr":   //WRITE EXTERNAL DATA
                                        for (int j = lineIndex + 3; j < line.length(); j++) {
                                            if (line.charAt(j) == '$') {
                                                if (last != lineIndex) {
                                                    lastEvent = generateEvent(type, line.substring(last, lineIndex), lastEvent,
                                                            lineNum, color, font);
                                                    tmp.addEvent(lastEvent);
                                                }
                                                //Methods.print(color.r, color.g, color.b);
                                                lastEvent = generateEvent(type, "", lastEvent,
                                                        lineNum, color, font);
                                                ((TextRenderer) lastEvent).setAlterer(line.substring(lineIndex + 3, j));
                                                tmp.addEvent(lastEvent);
                                                line = line.substring(0, lineIndex) + line.substring(j + 1);
                                                last = lineIndex;
                                                break;
                                            }
                                        }
                                        last = lineIndex;
                                        break;
                                    case "qu":   //QUESTION
                                        for (int j = lineIndex + 3; j < line.length(); j++) {
                                            if (line.charAt(j) == '$') {
                                                if (last != lineIndex) {
                                                    lastEvent = generateEvent(type, line.substring(last, lineIndex), lastEvent,
                                                            lineNum, color, font);
                                                    tmp.addEvent(lastEvent);
                                                }
                                                tab = line.substring(lineIndex + 3, j).split(":");
                                                String[] answers = new String[tab.length / 2];
                                                String[] jumps = new String[answers.length];
                                                for (int i = 0; i < answers.length; i++) {
                                                    answers[i] = tab[2 * i];
                                                    jumps[i] = tab[2 * i + 1];
                                                }
                                                lastEvent = new QuestionMaker(lastEvent, answers, jumps, this);
                                                tmp.addEvent(lastEvent);
                                                line = line.substring(0, lineIndex) + line.substring(j + 1);
                                                break;
                                            }
                                        }
                                        break;
                                    case "if":   //CHECK EXTERNAL STATEMENT
                                        for (int j = lineIndex + 3; j < line.length(); j++) {
                                            if (line.charAt(j) == '$') {
                                                if (last != lineIndex) {
                                                    lastEvent = generateEvent(type, line.substring(last, lineIndex), lastEvent,
                                                            lineNum, color, font);
                                                    tmp.addEvent(lastEvent);
                                                }
                                                tab = line.substring(lineIndex + 3, j).split(":");
                                                String[] jumps = new String[tab.length - 1];
                                                for (int i = 0; i < jumps.length; i++) {
                                                    jumps[i] = tab[i + 1];
                                                }
                                                lastEvent = new CheckExpressiontMaker(lastEvent, tab[0], jumps, this);
                                                tmp.addEvent(lastEvent);
                                                line = line.substring(0, lineIndex) + line.substring(j + 1);
                                                last = lineIndex;
                                                break;
                                            }
                                        }
                                        break;
                                    case "fl":   //FLUSH LINES
                                        if (last != lineIndex) {
                                            lastEvent = generateEvent(type, line.substring(last, lineIndex), lastEvent,
                                                    lineNum, color, font);
                                            tmp.addEvent(lastEvent);
                                        }
                                        lastEvent = new PropertyChanger(lastEvent, PropertyChanger.PROP_FLUSH, 0, this);
                                        tmp.addEvent(lastEvent);
                                        line = line.substring(0, lineIndex) + line.substring(lineIndex + 3);
                                        last = lineIndex;
                                        break;
                                    case "en":   //TERMINATE DIALOG
                                        if (last != lineIndex) {
                                            lastEvent = generateEvent(type, line.substring(last, lineIndex), lastEvent,
                                                    lineNum, color, font);
                                            tmp.addEvent(lastEvent);
                                        }
                                        lastEvent = new PropertyChanger(lastEvent, PropertyChanger.PROP_END, 0, this);
                                        tmp.addEvent(lastEvent);
                                        line = line.substring(0, lineIndex) + line.substring(lineIndex + 3);
                                        last = lineIndex;
                                        break;
                                    case "co":   //CHANGE COLOR
                                    case "cf":
                                        if (last != lineIndex) {
                                            lastEvent = generateEvent(type, line.substring(last, lineIndex), lastEvent,
                                                    lineNum, color, font);
                                            tmp.addEvent(lastEvent);
                                        }
                                        for (int j = lineIndex + 3; j < line.length(); j++) {
                                            if (line.charAt(j) == '$') {
                                                switch (symbol) {
                                                    case "co":
                                                        color = colors.get(Integer.parseInt(line.substring(lineIndex + 3, j), 16));
                                                        break;
                                                    case "cf":
                                                        color = new Color(Integer.parseInt(line.substring(lineIndex + 3, j), 16));
                                                        break;
                                                }
                                                line = line.substring(0, lineIndex) + line.substring(j + 1);
                                                last = lineIndex;
                                                break;
                                            }
                                        }
                                        break;
                                    case "cn":   //BLACK
                                        if (last != lineIndex) {
                                            lastEvent = generateEvent(type, line.substring(last, lineIndex), lastEvent,
                                                    lineNum, color, font);
                                            tmp.addEvent(lastEvent);
                                        }
                                        color = colors.get(0);
                                        line = line.substring(0, lineIndex) + line.substring(lineIndex + 3);
                                        last = lineIndex;
                                        break;
                                    case "pl":   //PLAIN TEXT
                                    case "bo":   //BOLD TEXT
                                    case "it":   //ITALIC TEXT
                                        if (last != lineIndex) {
                                            lastEvent = generateEvent(type, line.substring(last, lineIndex),
                                                    lastEvent, lineNum, color, font);
                                            tmp.addEvent(lastEvent);
                                        }
                                        switch (symbol) {
                                            case "pl":
                                                font = fonts[STYLE_NORMAL];
                                                break;
                                            case "bo":
                                                font = fonts[STYLE_BOLD];
                                                break;
                                            case "it":
                                                font = fonts[STYLE_ITALIC];
                                                break;
                                        }
                                        line = line.substring(0, lineIndex) + line.substring(lineIndex + 3);
                                        last = lineIndex;
                                        break;
                                    case "no":   //NORMAL TEXT
                                    case "sh":   //SHAKY TEXT
                                    case "ne":   //NERVOUS TEXT
                                    case "me":   //MELODIC TEXT
                                        if (last != lineIndex) {
                                            lastEvent = generateEvent(type, line.substring(last, lineIndex),
                                                    lastEvent, lineNum, color, font);
                                            tmp.addEvent(lastEvent);
                                        }
                                        switch (symbol) {
                                            case "no":
                                                type = TYPE_NORMAL;
                                                break;
                                            case "sh":
                                                type = TYPE_SHAKY;
                                                break;
                                            case "ne":
                                                type = TYPE_NERVOUS;
                                                break;
                                            case "me":
                                                type = TYPE_MUSIC;
                                                break;
                                        }
                                        line = line.substring(0, lineIndex) + line.substring(lineIndex + 3);
                                        last = lineIndex;
                                        break;
                                    default:
                                        throw new RuntimeException("UNKNOWN SYMBOL \"" + line.charAt(lineIndex + 1) + "\"!\nLine : " + line + "");
                                }
                            } else {
                                lineIndex++;
                            }
                        }
                        if (last != lineIndex) {
                            lastEvent = generateEvent(type, line.substring(last, lineIndex),
                                    lastEvent, lineNum, color, font);
                            tmp.addEvent(lastEvent);
                        }
                    } else {
                        lastEvent = new TextRenderer(" ", lastEvent, lineNum, color, font, this);
                        tmp.addEvent(lastEvent);
                    }
                    lineNum++;
                    currentBranch.add(tmp);
                }
            }
            started = true;
            index = 0;
            change = 100;
            rowsInPlace = 0;
            jumpTo = -1;
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
        branch.clear();
        branches.clear();
        jumpPlacements.clear();
        statements.clear();
        events.clear();
        writers.clear();
        speakers.clear();
        speaker = 0;
        portraits.clear();
        expression = 0;
        terminate = false;
        if (locked != null) {
            for (Entity e : locked) {
                e.setAbleToMove(true);
            }
        }
    }

    private int getJumpLocation(String pointer) {
        for (int i = 0; i < jumpPlacements.size(); i++) {
            if (jumpPlacements.get(i).equals(pointer)) {
                return i;
            }
        }
        return 0;
    }

    private Statement getStatement(String pointer) {
        for (Statement statement : statements) {
            if (statement.getName().equals(pointer)) {
                return statement;
            }
        }
        return null;
    }

    private Event getEvent(String pointer) {
        for (Event event : events) {
            if (event.name.equals(pointer)) {
                return event;
            }
        }
        return null;
    }

    Writer getWriter(String pointer) {
        for (Writer writer : writers) {
            if (writer.getName().equals(pointer)) {
                return writer;
            }
        }
        return null;
    }

    public void addEventOnBranchStart(Executive event, String... branchList) {
        for (String br : branchList) {
            if (started) {
                Branch b = branches.get(getJumpLocation(br));
                if (b.startEvent == null) {
                    b.startEvent = new ArrayList<>();
                }
                b.startEvent.add(event);
            }
        }
    }

    public void addEventOnBranchEnd(Executive event, String... branchList) {
        for (String br : branchList) {
            if (started) {
                Branch b = branches.get(getJumpLocation(br));
                if (b.endEvent == null) {
                    b.endEvent = new ArrayList<>();
                }
                b.endEvent.add(event);
            }
        }
    }

    public void addExternalStatement(Statement statement) {
        if (started) {
            statements.add(statement);
        }
    }

    public void addExternalEvent(Executive event, String name) {
        if (started) {
            events.add(new Event(event, name));
        }
    }

    public void addExternalWriter(Writer event) {
        if (started) {
            writers.add(event);
        }
    }

    private TextEvent generateEvent(int type, String text, TextEvent previous, int lineNum, Color color, FontHandler font) {
        TextEvent ret = null;
        switch (type) {
            case TYPE_NORMAL:
                ret = new TextRenderer(text, previous, lineNum, color, font, this);
                break;
            case TYPE_SHAKY:
                ret = new ShakyTextRenderer(text, previous, lineNum, color, font, this);
                break;
            case TYPE_MUSIC:
                ret = new MusicTextRenderer(text, previous, lineNum, color, font, this);
                break;
            case TYPE_NERVOUS:
                ret = new NervousTextRenderer(text, previous, lineNum, color, font, this);
                break;
        }
        return ret;
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (started) {
            int tile = Place.tileSize;
            action = playerController.getAction(MyController.INPUT_ACTION).isKeyClicked();

            if (terminate) {
                branch.endingEvent();
                stopTextViewing();
                return;
            }

            glPushMatrix();

            glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);

            glScaled(1 / Place.getCurrentScale(), 1 / Place.getCurrentScale(), 1);

            glTranslatef((getCamera().getWidth() - optimalWidth) / 2, (getCamera().getHeight() - optimalHeight) / 2 + optimalHeight - 3.5f * tile, 0);

            Drawer.setCentralPoint();

            drawGui();

            Drawer.returnToCentralPoint();

            if (!firstStep) {
                if (portraits.size() > 0) {
                    if (portraits.get(portrait).onRight) {
                        Drawer.translate(optimalWidth - 3 * tile, 0);
                        portraits.get(portrait).image.renderPieceMirrored(expression);
                    } else {
                        Drawer.translate(3 * tile, 0);
                        portraits.get(portrait).image.renderPiece(expression);
                    }
                }
            } else {
                branch.startingEvent();
                firstStep = false;
            }

            Drawer.returnToCentralPoint();

            if (question) {
                drawQuestion();
            }

            Drawer.returnToCentralPoint();

            Drawer.bindFontTexture();

            if (!speakers.get(speaker).isEmpty()) {
                littleFont.drawLine(speakers.get(speaker) + ":", Place.tileHalf, tile / 5, Color.gray);
            }

            Drawer.translate(Place.tileHalf, 2 * tile / 3
                    - (int) (Math.max((deltaLines + (flushing ? change : 0)) * fonts[0].getHeight() * 1.2, 0)));

            time += Time.getDelta();
            if (time >= 60) {
                time = 0;
            }
            realSpeed = speed * (playerController.getAction(MyController.INPUT_RUN).isKeyPressed() ? 2f : 1f) * Time.getDelta();

            if (flushing) {
                handleFlushing();
            } else if (!flushReady) {
                if (index < branch.length) {
                    index += realSpeed;
                    change = 1;
                } else if (jumpTo >= 0) {
                    flushReady = true;
                } else if (action) {
                    branch.endingEvent();
                    stopTextViewing();
                }
            } else if (!question) {
                if (action) {
                    flushing = true;
                    flushReady = false;
                    change = 0;
                }
            } else {
                handleQuestion();
            }
            for (TextRow tr : branch) {
                handleEvent(tr);
            }
            branch.setLength();
            Drawer.refreshForRegularDrawing();
            glPopMatrix();
        }
    }

    private void handleFlushing() {
        change += (0.75 + realSpeed / 4) / 10;
        if (change >= 1) {
            deltaLines++;
            rowsInPlace--;
            if (rowsInPlace <= 0) {
                flushing = false;
                change = 1;
                if (jumpTo >= 0) {
                    branch.endingEvent();
                    branch = branches.get(jumpTo);
                    branch.initialize();
                    branch.startingEvent();
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
        frame.renderPieceResized(1, 0, optimalWidth - 2 * tile, tile);
        Drawer.translate(0, tile);
        frame.renderPieceResized(1, 1, optimalWidth - 2 * tile, tile * 1.5f);
        Drawer.translate(0, tile * 1.5f);
        frame.renderPieceResized(1, 2, optimalWidth - 2 * tile, tile);
        Drawer.translate(optimalWidth - 2 * tile, -2.5f * tile);
        frame.renderPiece(2, 0);
        Drawer.translate(0, tile);
        frame.renderPieceResized(2, 1, tile, tile * 1.5f);
        Drawer.translate(0, tile * 1.5f);
        frame.renderPiece(2, 2);

        if (!question) {
            if (flushReady) {
                Drawer.translate(-Place.tileHalf, (int) (5 * Math.sin(time / 30 * Math.PI)));
                frame.renderPiece(3, 0);
            }
            if (index >= branch.length && jumpTo < 0) {
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
        Drawer.translate(optimalWidth - maxL - Place.tileHalf, 0);
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
            Drawer.setColorStatic(Color.gray);
            frame.renderPiece(0, 3);
            Drawer.refreshColor();
            Drawer.translate(tile * 0.7f, -Place.tileHalf);
        }
    }

    private void handleQuestion() {
        if (answer != -1) {
            if (playerController.getAction(MyController.INPUT_UP).isKeyClicked()) {
                answer--;
                if (answer < 0) {
                    answer = answerText.length - 1;
                }
            }
            if (playerController.getAction(MyController.INPUT_DOWN).isKeyClicked()) {
                answer++;
                if (answer > answerText.length - 1) {
                    answer = 0;
                }
            }
            if (action) {
                jumpTo = getJumpLocation(answerJump[answer]);
                question = false;
                flushing = true;
                flushReady = false;
                change = 0;
            }
        } else {
            if (playerController.getAction(MyController.INPUT_UP).isKeyClicked()) {
                answer = 0;
            }
            if (playerController.getAction(MyController.INPUT_DOWN).isKeyClicked()) {
                answer = 1;
            }
        }
    }

    private void handleEvent(TextRow te) {
        if (te.rowNum >= deltaLines && te.rowNum <= deltaLines + rows) {
            te.event((int) index);
        }
        te.setEnd();
        if (te.isEnding((int) index) && !flushReady && !flushing) {
            rowsInPlace++;
            if (rowsInPlace == rows && !stop) {
                flushReady = true;
            }
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
            e.setAbleToMove(false);
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
        return (int) time;
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
        jumpTo = getJumpLocation(location);
    }

    void triggerEvent(String event) {
        getEvent(event).event.execute();
    }

    void setCheckingExpression(String expression, String[] jumpLocations) {
        Statement s = getStatement(expression);
        String name = jumpLocations[s.check()];
        if (!name.equals("-")) {
            jumpTo = getJumpLocation(name);
            flushReady = true;
            rowsInPlace++;
        }
    }

    void setQuestion(String[] answers, String[] jumpLocations) {
        question = true;
        answerText = answers;
        answerJump = jumpLocations;
        answer = -1;
        flushReady = true;
        rowsInPlace++;
    }

    void terminateDialog() {
        terminate = true;
    }

    private class Event {

        Executive event;
        String name;

        Event(Executive event, String name) {
            this.event = event;
            this.name = name;
        }
    }

    private class Portrait {

        final SpriteSheet image;
        final boolean onRight;

        Portrait(SpriteSheet image, boolean onRight) {
            this.image = image;
            this.onRight = onRight;
            image.canBeMirrored = true;
        }
    }

    class Branch extends ArrayList<TextRow> {

        int length = Integer.MAX_VALUE;
        ArrayList<Executive> startEvent, endEvent;

        public void setLength() {
            if (size() > 0) {
                length = get(size() - 1).end;
            }
        }

        public void initialize() {
            this.stream().forEach((tr) -> {
                tr.initialize();
            });
        }

        public void startingEvent() {
            if (startEvent != null) {
                for (Executive e : startEvent) {
                    e.execute();
                }
            }
        }

        public void endingEvent() {
            if (endEvent != null) {
                for (Executive e : endEvent) {
                    e.execute();
                }
            }
        }

        @Override
        public void clear() {
            super.clear();
            if (startEvent != null) {
                startEvent.clear();
            }
            if (endEvent != null) {
                endEvent.clear();
            }
        }
    }

    class TextRow {

        private final int rowNum;
        private final ArrayList<TextEvent> list;
        private int end;
        private boolean ending;

        public TextRow(int rowNum) {
            this.rowNum = rowNum;
            list = new ArrayList<>();
            end = -1;
        }

        public void initialize() {
            list.stream().forEach((te) -> {
                if (te instanceof PropertyChanger) {
                    ((PropertyChanger) te).done = false;
                }
            });
        }

        public void setEnd() {
            this.end = list.size() > 0 ? list.get(list.size() - 1).getEnd() : 0;
        }

        public void addEvent(TextEvent te) {
            list.add(te);
        }

        public boolean isEnding(int i) {
            if (ending) {
                return false;
            } else {
                if (i >= end - 1 && end - 1 > 0) {
                    ending = true;
                    index = end - 1;
                    return true;
                }
                return false;
            }
        }

        public void event(int start) {
            for (TextEvent te : list) {
                te.event(start, rowNum);
            }
        }
    }
}
