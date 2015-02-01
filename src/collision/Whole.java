/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

/**
 *
 * @author przemek
 */
public class Whole implements Integration {

    @Override
    public void updateProperties(Area area, Figure figure) {
        area.updateCollision();
        area.updateBoundsForWhole();
        area.updateCenter();
    }

    @Override
    public boolean isWhole() {
        return true;
    }

    @Override
    public boolean isCollide(Area area, int x, int y, Figure figure) {
        return area.isCollideWhole(x, y, figure);
    }

    @Override
    public Figure whatCollide(Area area, int x, int y, Figure figure) {
        return area.whatCollideWhole(x, y, figure);
    }
}
