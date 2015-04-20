/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navmeshpathfinding;

import engine.Point;

/**
 *
 * @author WROBELP1
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        NavigationMesh mesh = new NavigationMesh(new Point(0, 0), new Point(3, 1), new Point(0, 4));
        mesh.addTriangle(Triangle.create(new Point(4, 4), new Point(3, 1), new Point(0, 4)));
        mesh.addTriangle(Triangle.create(new Point(4, 4), new Point(4, 6), new Point(0, 4)));
        mesh.addTriangle(Triangle.create(new Point(4, 4), new Point(4, 6), new Point(7, 6)));
        mesh.addTriangle(Triangle.create(new Point(4, 9), new Point(4, 6), new Point(7, 6)));
        mesh.addTriangle(Triangle.create(new Point(4, 9), new Point(4, 6), new Point(0, 9)));
        mesh.addTriangle(Triangle.create(new Point(4, 9), new Point(2, 12), new Point(0, 9)));
        mesh.addTriangle(Triangle.create(new Point(4, 9), new Point(4, 11), new Point(7, 6)));
        mesh.addTriangle(Triangle.create(new Point(10, 8), new Point(4, 11), new Point(7, 6)));
        mesh.addTriangle(Triangle.create(new Point(10, 8), new Point(15, 6), new Point(7, 6)));
        mesh.addTriangle(Triangle.create(new Point(10, 8), new Point(4, 11), new Point(13, 11)));
        mesh.addTriangle(Triangle.create(new Point(13, 11), new Point(4, 11), new Point(15, 16)));
        mesh.addTriangle(Triangle.create(new Point(4, 11), new Point(4, 15), new Point(15, 16)));
        mesh.addTriangle(Triangle.create(new Point(4, 11), new Point(2, 12), new Point(4, 15)));
        mesh.addTriangle(Triangle.create(new Point(0, 9), new Point(2, 12), new Point(0, 17)));
        mesh.addTriangle(Triangle.create(new Point(4, 15), new Point(2, 12), new Point(0, 17)));
        mesh.addTriangle(Triangle.create(new Point(13, 11), new Point(20, 11), new Point(15, 16)));
        mesh.addTriangle(Triangle.create(new Point(13, 11), new Point(20, 11), new Point(15, 6)));
        mesh.addTriangle(Triangle.create(new Point(20, 4), new Point(20, 11), new Point(15, 6)));

        Point start = new Point(3, 12);
        Point end = new Point(2, 4);
        Node path = PathFinder.findPath(mesh, start, end);

        Window win = new Window();
        win.addVariables(mesh, start, end, path);
        win.setVisible(true);
    }
}
