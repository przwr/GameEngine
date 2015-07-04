/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navmeshpathfinding;

import game.gameobject.Entity;

/**
 *
 * @author przemek
 */
public class PathRequestContener {

    private static final byte INITIAL_POINT_COUNT = 8;
    private PathRequest[] requests;
    private int requestCount;

    public PathRequestContener() {
        requests = new PathRequest[INITIAL_POINT_COUNT];
        for (int i = 0; i < INITIAL_POINT_COUNT; i++) {
            requests[i] = new PathRequest();
        }
    }

    public PathRequestContener(int pointCount) {
        requests = new PathRequest[pointCount];
        for (int i = 0; i < pointCount; i++) {
            requests[i] = new PathRequest();
        }
    }

    public void add(Entity requester, int xDest, int yDest) {
        ensureCapacity(1);
        requests[requestCount].xDest = xDest;
        requests[requestCount].yDest = yDest;
        requests[requestCount].requester = requester;
        requestCount++;
    }

    private static int caps, maxSize;

    private void ensureCapacity(int capacity) {
        if (requestCount + capacity > requests.length) {
            PathRequest[] tempRequests = new PathRequest[(int) (1.5 * requests.length)];
            System.arraycopy(requests, 0, tempRequests, 0, requests.length);
            requests = tempRequests;
            for (int i = requestCount; i < requests.length; i++) {
                requests[i] = new PathRequest();
            }
            caps++;
            if (requests.length > maxSize) {
                maxSize = requests.length;
            }
            System.out.println("Capacity of PathRequestContener enlarged " + caps + " times to maxSize: " + maxSize);
        }
    }

    public PathRequest get(int i) {
        return requests[i];
    }

    public void remove(int index) {
        if (index < requestCount && index >= 0) {
            requestCount--;
            requests[index].requester = requests[requestCount].requester;
            requests[index].xDest = requests[requestCount].xDest;
            requests[index].yDest = requests[requestCount].yDest;
        }
    }

    public boolean isEmpty() {
        return requestCount == 0;
    }

    public void clear() {
        requestCount = 0;
    }

    public int size() {
        return requestCount;
    }
}
