/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

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
        FontHandler tmp = new FontHandler(name, type, size);
        fonts.add(tmp);
        return tmp;
    }

    public FontHandler add(String name, int size) {
        FontHandler tmp = new FontHandler(name, size);
        fonts.add(tmp);
        return tmp;
    }

    public FontHandler getFont(int i) {
        return fonts.get(i);
    }
    
    public FontHandler getFont(String name, int size) {
        FontHandler firstOcc = null;
        for (FontHandler fh : fonts) {
            if (fh.getName().equals(name)) {
                if (fh.getSize() == size) {
                    return fh;
                }
                if (firstOcc == null) {
                    firstOcc = fh;
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
