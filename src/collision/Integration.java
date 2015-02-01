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
public interface Integration {

    public boolean isWhole();

    public void updateProperties(Area area, Figure figure);

    public boolean isCollide(Area area, int x, int y, Figure figure);

    public Figure whatCollide(Area area, int x, int y, Figure figure);
}
