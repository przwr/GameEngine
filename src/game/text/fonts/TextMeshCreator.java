package game.text.fonts;

import engine.utilities.Drawer;
import engine.utilities.Methods;
import game.Settings;
import org.lwjgl.opengl.Display;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TextMeshCreator {

    protected static final int SPACE_ASCII = 32;

    private MetaFile metaData;

    protected TextMeshCreator(File metaFile, File OFile) {
        metaData = new MetaFile(metaFile, OFile);
    }

    public static double getFontHeightFactor() {
        return (Settings.nativeScale / 0.75 / (double) Display.getHeight());
    }

    private static void addVertices(double x, double y, double maxX, double maxY) {
        Drawer.streamVertexData.add((float) x, (float) y,
                (float) x, (float) maxY,
                (float) maxX, (float) maxY,
                (float) maxX, (float) maxY,
                (float) maxX, (float) y,
                (float) x, (float) y);
    }

    private static void addTexCoords(double x, double y, double maxX, double maxY) {
        Drawer.streamColorData.add((float) x, (float) y,
                (float) x, (float) maxY,
                (float) maxX, (float) maxY,
                (float) maxX, (float) maxY,
                (float) maxX, (float) y,
                (float) x, (float) y);
    }

    protected TextMeshData createTextMesh(TextPiece text) {
        List<Line> lines = createStructure(text);
        TextMeshData data = createQuadVertices(text, lines);
        return data;
    }

    protected int getTextWidth(String string, double fontSize) {
        Word w = new Word(fontSize);
        double size = 0;
        for (char c : string.toCharArray()) {
            int ascii = (int) c;
            if (ascii == SPACE_ASCII) {
                size += metaData.getSpaceWidth() * fontSize;
            } else {
                Character character = metaData.getCharacter(ascii);
                w.addCharacter(character);
                size += character.getXAdvance() * fontSize;
            }
        }
        w.getWordWidth();
        if (size > 1) {
            return Display.getWidth();
        } else {
            return Methods.roundDouble(size * Display.getWidth());
        }
    }

    private List<Line> createStructure(TextPiece text) {
        char[] chars = text.getTextString().toCharArray();
        List<Line> lines = new ArrayList<>();
        Line currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
        Word currentWord = new Word(text.getFontSize());
        for (char c : chars) {
            int ascii = (int) c;
            if (ascii == SPACE_ASCII) {
                boolean added = currentLine.attemptToAddWord(currentWord);
                if (!added) {
                    lines.add(currentLine);
                    currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
                    currentLine.attemptToAddWord(currentWord);
                }
                currentWord = new Word(text.getFontSize());
                continue;
            }
            Character character = metaData.getCharacter(ascii);
            if (character != null) {
                currentWord.addCharacter(character);
            } else {
                System.out.println("Missing character: " + c);
            }
        }
        completeStructure(lines, currentLine, currentWord, text);
        return lines;
    }

    private void completeStructure(List<Line> lines, Line currentLine, Word currentWord, TextPiece text) {
        boolean added = currentLine.attemptToAddWord(currentWord);
        if (!added) {
            lines.add(currentLine);
            currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
            currentLine.attemptToAddWord(currentWord);
        }
        lines.add(currentLine);
    }

    private TextMeshData createQuadVertices(TextPiece text, List<Line> lines) {
        text.setNumberOfLines(lines.size());
        double textWidth = 0;
        double cursorX = 0;
        double cursorY = 0;
        double kerning;
        Character last;
        Drawer.streamVertexData.clear();
        Drawer.streamColorData.clear();
        for (Line line : lines) {
            if (text.isCentered()) {
                cursorX = (line.getMaxLength() - line.getLineLength()) / 2d;
            }
            for (Word word : line.getWords()) {
                last = null;
                for (Character letter : word.getCharacters()) {
                    kerning = 0;
                    if (last != null) {
                        kerning = metaData.getKerning(last.getId() * 1000 + letter.getId());
                    }
                    addVerticesForCharacter(cursorX, cursorY, letter, text.getFontSize(), kerning);
                    addTexCoords(letter.getXTextureCoord(), letter.getYTextureCoord(),
                            letter.getXMaxTextureCoord(), letter.getYMaxTextureCoord());
                    cursorX += (letter.getXAdvance() + kerning) * text.getFontSize();
                    last = letter;
                }
                cursorX += metaData.getSpaceWidth() * text.getFontSize();
            }
            if (textWidth < line.getLineLength()) {
                textWidth = line.getLineLength();
            }
            cursorX = 0;
            cursorY += getFontHeightFactor() * text.getFontSize();
        }
        text.setWidth(Methods.roundDouble(textWidth * Display.getWidth()));
        return new TextMeshData(Drawer.streamVertexData.toArray(), (Drawer.streamColorData.toArray()));
    }

    private void addVerticesForCharacter(double cursorX, double cursorY, Character character, double fontSize, double kerning) {
        double x = cursorX + ((kerning + character.getXOffset()) * fontSize);
        double y = cursorY + (character.getYOffset() * fontSize);
        double maxX = x + (character.getXSize() * fontSize);
        double maxY = y + (character.getYSize() * fontSize);
        double properX = (2 * x) - 1;
        double properY = (-2 * y) + 1;
        double properMaxX = (2 * maxX) - 1;
        double properMaxY = (-2 * maxY) + 1;
        addVertices(properX, properY, properMaxX, properMaxY);
    }

}
