/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.lights;

import collision.Figure;
import engine.utilities.Point;

/**
 * @author przemek
 */
public class Shadow {

    public static final int DARK = 0, BRIGHT = 1, BRIGHTEN = 2, DARKEN = 3,
            BRIGHTEN_OBJECT = 4, DARKEN_OBJECT = 5;
    public static final int shadowLength = 32768;
    public final Point point;
    public int type;
    public Figure caster;

    public Shadow(int type) {
        this.type = type;
        point = new Point();
    }

    public void setDark() {
        type = DARK;
    }

    public void setBright() {
        type = BRIGHT;
    }

    public void setBrighten(int x, int y) {
        type = BRIGHTEN;
        point.set(x, y);
    }

    public void setBrighten(int x, int y, Figure caster) {
        type = BRIGHTEN;
        point.set(x, y);
        this.caster = caster;
    }

    public void setDarken(int x, int y) {
        this.type = DARKEN;
        point.set(x, y);
    }

    public void setBrightenObject(int x, int y) {
        type = BRIGHTEN_OBJECT;
        point.set(x, y);
    }

    public void setDarkenObject(int x, int y) {
        this.type = DARKEN_OBJECT;
        point.set(x, y);
    }
}
