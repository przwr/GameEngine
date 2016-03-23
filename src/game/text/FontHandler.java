/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.text;

import engine.utilities.Drawer;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Wojtek
 */
public class FontHandler {

    private final char[] chars = {'ą', 'ę', 'ć', 'ł', 'ń', 'ó', 'ś', 'ż', 'ź', 'Ą', 'Ę', 'Ć', 'Ł', 'Ń', 'Ó', 'Ś', 'Ż', 'Ź'};
    private TrueTypeFont trueFont;
    private Font font;
    private String name = "";
    private int size = 0;

    public FontHandler(String name, int size) {
        try {
            InputStream inputStream = ResourceLoader.getResourceAsStream("/res/fonts/" + name + ".ttf");
            font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            font = font.deriveFont((float) size);
            trueFont = new TrueTypeFont(font, true, chars);
            this.name = name;
            this.size = size;
            inputStream.close();
        } catch (FontFormatException | IOException ex) {
            Logger.getLogger(FontBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private FontHandler(TrueTypeFont trueFont, Font font, String name, int size) {
        this.name = name;
        this.size = size;
        this.font = font;
        this.trueFont = trueFont;
    }

    public void drawLine(String text, float x, float y, Color color) {
        if (Drawer.regularShader != null) {
            Drawer.regularShader.stop();
        }
        trueFont.drawString(x, y, text, color);
        if (Drawer.regularShader != null) {
            Drawer.regularShader.start();
        }
    }

    public void drawManyLines(String text, int x, int y, Color color) {
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            trueFont.drawString(x, (float) (y + i * getHeight() * 1.2), lines[i], color);
        }
    }

    public FontHandler getFontWithSize(int size) {
        Font tmp = font.deriveFont(font.getStyle(), size);
        return new FontHandler(new TrueTypeFont(tmp, true, chars), tmp, name, size);
    }

    public FontHandler getFontWithStyle(int style) {
        Font tmp = font.deriveFont(style);
        return new FontHandler(new TrueTypeFont(tmp, true, chars), tmp, name, size);
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public int getStyle() {
        return font.getStyle();
    }

    public int getHeight() {
        return trueFont.getHeight();
    }

    public int getWidth(String text) {
        return trueFont.getWidth(text);
    }

    public int getHeight(String text) {
        int lines = 1;
        int index;
        int lastIndex = 0;
        while ((index = text.indexOf("\n", lastIndex)) != -1) {
            lines++;
            lastIndex = index + 1;
        }
        return lines * trueFont.getHeight(text);
    }
}
