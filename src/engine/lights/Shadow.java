/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.lights;

import collision.Figure;

/**
 * @author przemek
 */
public class Shadow {

    public static final short DARK = 0, BRIGHT = 1, BRIGHTEN = 2, DARKEN = 3, BRIGHTEN_OBJECT = 4, DARKEN_OBJECT = 5;
    public static final int shadowLength = 32768;
    public int type;
    public int xS, xE;
    public int xC, yC;

    public Shadow(int type) {
        this.type = type;
    }

    public void setDark() {
        type = DARK;
    }

    public void setBright() {
        type = BRIGHT;
    }

    public void setBrighten(int xS, int xE) {
        type = BRIGHTEN;
        this.xS = xS;
        this.xE = xE;
    }

    public void setBrighten(int xS, int xE, Figure caster) {
        type = BRIGHTEN;
        this.xS = xS;
        this.xE = xE;
        this.xC = caster.getX();
        this.yC = caster.getYEnd();
    }

    public void setDarken(int xS, int xE) {
        this.type = DARKEN;
        this.xS = xS;
        this.xE = xE;
    }

    public void setBrightenObject(int xS, int xE) {
        type = BRIGHTEN_OBJECT;
        this.xS = xS;
        this.xE = xE;
    }

    public void setDarkenObject(int xS, int xE) {
        this.type = DARKEN_OBJECT;
        this.xS = xS;
        this.xE = xE;
    }
}
