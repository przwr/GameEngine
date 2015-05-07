/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navmeshpathfinding;

/**
 *
 * @author przemek
 */
public class NeightbourTriangles {

    Triangle[] triangles = new Triangle[2];
    int size=0;

    public NeightbourTriangles(Triangle triangle) {
        triangles[size++] = triangle;
    }

    public void addTriangle(Triangle triangle) {
        if (size == 1) {
            if (!triangles[0].equals(triangle)) {
                triangles[size++] = triangle;
            }
        }
    }

    public Triangle getTriangle(int i) {
        return triangles[i];
    }

    public int size() {
        return size;
    }

}
