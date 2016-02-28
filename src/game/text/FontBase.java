/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.text;

import engine.utilities.Methods;
import java.util.ArrayList;

/**
 * @author przemek
 */
public class FontBase {

    private final ArrayList<FontHandler> fonts;

    public FontBase(int size) {
        fonts = new ArrayList<>(size);
    }

    public FontHandler add(String name, int size) {
        System.out.println("Adding font '" + name + "' " + size);
        FontHandler temp = new FontHandler(name, size);
        fonts.add(temp);
        return temp;
    }

    public FontHandler getFont(int i) {
        return fonts.get(i);
    }

    public int size() {
        return fonts.size();
    }

    public FontHandler getFont(String name, int size) {
        FontHandler firstOcc = null;
        for (FontHandler fontHandler : fonts) {
            if (fontHandler.getName().equals(name)) {
                if (fontHandler.getSize() == size) {
                    return fontHandler;
                }
                if (firstOcc == null) {
                    firstOcc = fontHandler;
                }
            }
        }
        if (firstOcc != null) {
            firstOcc = firstOcc.getFontWithSize(size);
            fonts.add(firstOcc);
        } else {
            firstOcc = add(name, size);
        }
        return firstOcc;
    }

    public FontHandler getFont(String name, int style, int size) {
        FontHandler firstOcc = null;
        for (FontHandler fontHandler : fonts) {
            if (fontHandler.getName().equals(name)) {
                if (fontHandler.getSize() == size) {
                    return fontHandler;
                }
                if (firstOcc == null) {
                    firstOcc = fontHandler;
                }
            }
        }
        if (firstOcc != null) {
            firstOcc = firstOcc.getFontWithSize(size);
            fonts.add(firstOcc);
        } else {
            firstOcc = add(name, size);
            firstOcc = firstOcc.getFontWithStyle(style);
        }
        return firstOcc;
    }

    public FontHandler changeStyle(FontHandler font, int style) {
        FontHandler firstOcc = null;
        for (FontHandler fontHandler : fonts) {
            if (fontHandler.getName().equals(font.getName()) && fontHandler.getSize() == font.getSize()) {
                if (fontHandler.getStyle() == style) {
                    return fontHandler;
                }
                if (firstOcc == null) {
                    firstOcc = fontHandler;
                }
            }
        }
        if (firstOcc != null) {
            firstOcc = firstOcc.getFontWithStyle(style);
            fonts.add(firstOcc);
        } else {
            firstOcc = font.getFontWithStyle(style);
            fonts.add(firstOcc);
        }
        return firstOcc;
    }
}
