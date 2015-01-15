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
public class Chunks implements Integration {

    @Override
    public void upProperties(Area area, Figure figure) {
        area.upBoundsForChunks(figure);
        area.upCenter();
    }

    @Override
    public boolean isWhole() {
        return false;
    }

    @Override
    public boolean isCollide(Area area, int x, int y, Figure figure) {
        return area.isCollideChunks(x, y, figure);
    }

    @Override
    public Figure whatCollide(Area area, int x, int y, Figure figure) {
        return area.whatCollideChunks(x, y, figure);
    }
}
