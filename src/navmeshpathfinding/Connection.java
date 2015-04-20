/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navmeshpathfinding;

/**
 *
 * @author WROBELP1
 */
public class Connection {

    private final Triangle neightbour;
    private final int[] nodesIndexes = new int[2];

    public Connection(Triangle neightbour, int firstNodeIndex, int secondNodeIndex) {
        this.neightbour = neightbour;
        nodesIndexes[0] = firstNodeIndex;
        nodesIndexes[1] = secondNodeIndex;
    }

    public Triangle getNeightbour() {
        return neightbour;
    }

    public Node getNode(int i) {
        return neightbour.getNode(nodesIndexes[i]);
    }
}
