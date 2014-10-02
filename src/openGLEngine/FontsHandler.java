/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package openGLEngine;

import java.awt.Font;
import org.newdawn.slick.TrueTypeFont;

/**
 *
 * @author przemek
 */
public class FontsHandler {

    protected final TrueTypeFont[] fonts;
    protected final int size;
    protected int n;
    private char[] chars = {'ą', 'ę', 'ć', 'ł', 'ń', 'ó', 'ś', 'ż', 'ź', 'Ą', 'Ę', 'Ć', 'Ł', 'Ń', 'Ó', 'Ś', 'Ż', 'Ź',};

    public FontsHandler(int size) {
        n = 0;
        this.size = size;
        fonts = new TrueTypeFont[size];
    }

    public void add(String name, int type, int size) {
        if (n < size) {
            fonts[n] = new TrueTypeFont(new Font(name, type, size), true, chars);
            n++;
        } else {
            System.out.println("Za mało miejsca!");
        }
    }

    public void remove(int i) {
        for (; i < size - 1; i++) {
            fonts[i] = fonts[i + 1];
        }
        fonts[i] = null;
    }

    public TrueTypeFont write(int i) {
        if (i < n) {
            return fonts[i];
        }
        return null;
    }
}
