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

    public FontsHandler(int size) {
        n = 0;
        this.size = size;
        fonts = new TrueTypeFont[size];
    }

    public void add(String name, int type, int size) {
        if (n < size) {
            fonts[n] = new TrueTypeFont(new Font(name, type, size), true);
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

    public String PL(String znaki) {
        char[] znak = znaki.toCharArray();
        for (int i = 0; i < znak.length; i++) {
            if (znak[i] == 'ą') {
                znak[i] = 'a';
            } else if (znak[i] == 'ć') {
                znak[i] = 'c';
            } else if (znak[i] == 'ę') {
                znak[i] = 'e';
            } else if (znak[i] == 'ł') {
                znak[i] = 'l';
            } else if (znak[i] == 'ó') {
                znak[i] = 'ó';
            } else if (znak[i] == 'ś') {
                znak[i] = 's';
            } else if (znak[i] == 'ź') {
                znak[i] = 'z';
            } else if (znak[i] == 'ż') {
                znak[i] = 'z';
            } 
            else if (znak[i] == 'Ą') {
                znak[i] = 'A';
            } else if (znak[i] == 'Ć') {
                znak[i] = 'C';
            } else if (znak[i] == 'Ę') {
                znak[i] = 'E';
            } else if (znak[i] == 'Ł') {
                znak[i] = 'L';
            } else if (znak[i] == 'Ó') {
                znak[i] = 'Ó';
            } else if (znak[i] == 'Ś') {
                znak[i] = 'S';
            } else if (znak[i] == 'Ź') {
                znak[i] = 'Z';
            } else if (znak[i] == 'Ż') {
                znak[i] = 'Z';
            }
        }
        return String.copyValueOf(znak);
    }
}
