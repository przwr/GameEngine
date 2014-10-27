/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import engine.Point;
import game.gameobject.GameObject;
import java.awt.geom.Line2D;

/**
 *
 * @author Wojtek
 */
public class Line extends Figure {
    private final int xk;
    private final int yk;
    private final int d;
    private final int A;
    private final int B;
    
    public Line(int xs, int ys, int d, int A, int B, GameObject owner) {  // Środek prostokąta (xs,ys) dla (0,0) jest w lewym górnym rogu prostokąta
        super(xs, ys, owner);
        this.d = d;
        this.A = A;
        this.B = B;
        this.type = 1;
        xk = d * A;
        yk = d * B;
        this.type = 3;
    }
    
    public Line(int d, int A, int B, GameObject owner) {  
        super(0, 0, owner);
        this.d = d;
        this.A = A;
        this.B = B;
        this.type = 1;
        xk = d * A;
        yk = d * B;
        this.type = 3;
    }

    @Override
    public Point[] listPoints() {
        Point[] list = {new Point(super.getX(), super.getY()), 
                        new Point(super.getX() + xk, super.getY() + yk)};
        return list;
    }

    @Override
    public boolean ifCollideSngl(int x, int y, Figure f) {
                
        if (f.getType() == 1) {         // Z Prostokątem
            
            Point[] list = f.listPoints();
            int[] w = {super.getX(x), super.getY(y), super.getX(x) + xk, super.getY(y) + yk};
            return (Line2D.linesIntersect(w[0], w[1], w[2], w[3], list[0].getX(), list[0].getY(), list[1].getX(), list[1].getY()) || 
                    Line2D.linesIntersect(w[0], w[1], w[2], w[3], list[1].getX(), list[1].getY(), list[2].getX(), list[2].getY()) ||
                    Line2D.linesIntersect(w[0], w[1], w[2], w[3], list[2].getX(), list[2].getY(), list[3].getX(), list[3].getY()) ||
                    Line2D.linesIntersect(w[0], w[1], w[2], w[3], list[3].getX(), list[3].getY(), list[0].getX(), list[0].getY()));
            
        } else if (f.getType() == 2) {  // Z Okręgiem
            
            Circle l = (Circle) f;
            return (Line2D.ptSegDist(super.getX(x), super.getY(y), super.getX(x) + xk, super.getY(y) + yk, l.getX(), l.getY()) <= l.getRadius());
            
        } else if (f.getType() == 3) {  // Z Linią
            
            Line l = (Line) f;
            return (Line2D.linesIntersect(super.getX(x), super.getY(y), super.getX(x) + xk, super.getY(y) + yk, 
                                                    l.getX(), l.getY(), l.getX() + l.getXk(), l.getY() + l.getYk()));
        }
        return false;
    }
    
    public int getA() {
        return A;
    }
    
    public int getB() {
        return B;
    }
    
    public int getXk() {
        return xk;
    }
    
    public int getYk() {
        return yk;
    }
}
