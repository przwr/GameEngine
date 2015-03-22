/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import collision.Figure;
import engine.Point;

/**
 *
 * @author przemek
 */
public class Shadow {

    public static final int DARK = 0, BRIGHT = 1, BRIGHTEN = 2, DARKEN = 3, BRIGHTEN_OBJECT = 4, DARKEN_OBJECT = 5;
    public int type;
    public Point point;
    public Figure source;

    public Shadow(int type) {
        this.type = type;
        point = new Point();
    }

    public Shadow setDark() {
        type = DARK;
        return this;
    }

    public Shadow setBright() {
        type = BRIGHT;
        return this;
    }

    public Shadow setBrighten(int x, int y) {
        type = BRIGHTEN;
        point.set(x, y);
        return this;
    }

    public Shadow setBrighten(int x, int y, Figure source) {
        type = BRIGHTEN;
        point.set(x, y);
        this.source = source;
        return this;
    }

    public Shadow setDarken(int x, int y) {
        this.type = DARKEN;
        point.set(x, y);
        return this;
    }

    public Shadow setBrightenObject(int x, int y) {
        type = BRIGHTEN_OBJECT;
        point.set(x, y);
        return this;
    }

    public Shadow setDarkenObject(int x, int y) {
        this.type = DARKEN_OBJECT;
        point.set(x, y);
        return this;
    }

}
