/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic.navmeshpathfinding.navigationmesh;

import engine.utilities.Point;

/**
 * @author WROBELP1
 */
public class EmptyPoint extends Point {

    public EmptyPoint() {
        super(-1, -1);
    }

    @Override
    public String toString() {
        return "Brak rozwiązania!";
    }
}
