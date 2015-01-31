/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;

/**
 *
 * @author przemek
 */
public class FontBase {

    protected final ArrayList<TrueTypeFont> fonts;
    private final char[] chars = {'ą', 'ę', 'ć', 'ł', 'ń', 'ó', 'ś', 'ż', 'ź', 'Ą', 'Ę', 'Ć', 'Ł', 'Ń', 'Ó', 'Ś', 'Ż', 'Ź'};

    public FontBase(int size) {
        fonts = new ArrayList<>(size);
    }

    public void add(String name, int type, int size) {
        fonts.add(new TrueTypeFont(new Font(name, type, size), true, chars));
    }

    public void add(String name, int size) {
        try {
            InputStream inputStream = ResourceLoader.getResourceAsStream("/res/fonts/" + name + ".ttf");
            Font awtFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtFont = awtFont.deriveFont((float) size);
            fonts.add(new TrueTypeFont(awtFont, true, chars));
        } catch (FontFormatException | IOException ex) {
            Logger.getLogger(FontBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public TrueTypeFont write(int i) {
        return fonts.get(i);
    }
}
