package game.logic.betweenareapathfinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by przemek on 14.02.16.
 */
public class AreaConnector {

    int areaIndex;
    ArrayList<AreaConnection> connections = new ArrayList<>(4);

    public AreaConnector(int areaIndex) {
        this.areaIndex = areaIndex;
    }

    public void addConnection(AreaConnection connection) {
        connections.add(connection);
    }

    public void trim() {
        connections.forEach(AreaConnection::trim);
        connections.trimToSize();
    }

    public List<AreaConnection> getConnections() {
        return connections;
    }

    public boolean containsConnection(int areaIndex, int nearAreaIndex) {
        for (AreaConnection connection : connections) {
            if (connection.getFirstAreaIndex() == areaIndex && connection.getSecondAreaIndex() == nearAreaIndex
                    || connection.getFirstAreaIndex() == nearAreaIndex && connection.getSecondAreaIndex() == areaIndex) {
                return true;
            }
        }
        return false;
    }

    public AreaConnection getConnection(int destinationArea) {
        for (AreaConnection connection : connections) {
            if (connection.getFirstAreaIndex() == areaIndex && connection.getSecondAreaIndex() == destinationArea ||
                    connection.getFirstAreaIndex() == destinationArea && connection.getSecondAreaIndex() == areaIndex) {
                return connection;
            }
        }
        return null;
    }
}
