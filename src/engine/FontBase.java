/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import game.text.FontHandler;
import java.util.ArrayList;

/**
 *
 * @author przemek
 */
public class FontBase {

    protected final ArrayList<FontHandler> fonts;

    public FontBase(int size) {
        fonts = new ArrayList<>(size);
    }

    public FontHandler add(String name, int type, int size) {
        FontHandler temp = new FontHandler(name, type, size);
        fonts.add(temp);
        return temp;
    }

    public FontHandler add(String name, int size) {
        FontHandler temp = new FontHandler(name, size);
        fonts.add(temp);
        return temp;
    }

    public FontHandler getFont(int i) {
        return fonts.get(i);
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
        }
        return firstOcc;
    }
}
