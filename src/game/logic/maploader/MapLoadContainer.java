package game.logic.maploader;

import game.place.map.Map;

/**
 * Created by przemek on 19.08.15.
 */
public class MapLoadContainer {

    private static final byte INITIAL_POINT_COUNT = 8;
    private int caps, maxSize;
    private MapLoad[] requests;
    private int requestCount;

    public MapLoadContainer() {
        requests = new MapLoad[INITIAL_POINT_COUNT];
        for (int i = 0; i < INITIAL_POINT_COUNT; i++) {
            requests[i] = new MapLoad();
        }
    }

    public MapLoadContainer(int pointCount) {
        requests = new MapLoad[pointCount];
        for (int i = 0; i < pointCount; i++) {
            requests[i] = new MapLoad();
        }
    }

    public void add(String name, Iterable<Integer> areas, Map map) {
        ensureCapacity(1);
        requests[requestCount].set(name, areas);
        requestCount++;
    }

    public void add(String name, Iterable<Integer> areas) {
        ensureCapacity(1);
        requests[requestCount].set(name, areas);
        requestCount++;
    }

    private void ensureCapacity(int capacity) {
        if (requestCount + capacity > requests.length) {
            MapLoad[] tempRequests = new MapLoad[(int) (1.5 * requests.length)];
            System.arraycopy(requests, 0, tempRequests, 0, requests.length);
            requests = tempRequests;
            for (int i = requestCount; i < requests.length; i++) {
                requests[i] = new MapLoad();
            }
            caps++;
            if (requests.length > maxSize) {
                maxSize = requests.length;
            }
            System.out.println("Capacity of MapLoadContainer enlarged " + caps + " times to maxSize: " + maxSize);
        }
    }

    public MapLoad get(int i) {
        return requests[i];
    }

    public void remove(int index) {
        if (index < requestCount && index >= 0) {
            requestCount--;
            requests[index].set(requests[requestCount].name, requests[requestCount].areas);
        }
    }

    public boolean contains(String name, Iterable<Integer> areas) {
        boolean found;
        if (areas != null) {
            for (MapLoad load : requests) {
                if (load.name == name) {
                    found = true;
                    for (int area : areas) {
                        if (!load.areas.contains(area)) {
                            found = false;
                        }
                    }
                    if (found) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return requestCount == 0;
    }

    public void clear() {
        requestCount = 0;
    }


    public void clearReally() {
        for (int i = 0; i < requests.length; i++) {
            requests[i].clear();
        }
    }

    public void setSize(int size) {
        requestCount = size;
    }

    public int size() {
        return requestCount;
    }

    public boolean isSufficientCapacity() {
        return requestCount < requests.length;
    }

    public MapLoad getMapByName(String name) {
        for (int i = 0; i < requestCount; i++) {
            if (requests[i].name == name) {
                return requests[i];
            }
        }
        return null;
    }

    @Override
    public String toString() {
        String string = requestCount + ": ";
        for (int i = 0; i < requestCount; i++) {
            string += " " + requests[i].name + " - " + requests[i].areas.toString() + ";";
        }
        return string;
    }
}
