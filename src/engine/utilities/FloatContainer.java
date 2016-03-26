/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.utilities;

/**
 * @author Domi
 */
public class FloatContainer {

    private static final byte INITIAL_POINT_COUNT = 8;
    private static int caps, maxSize;
    private float[] floats;
    private int floatCount;

    public FloatContainer() {
        floats = new float[INITIAL_POINT_COUNT];
    }

    public FloatContainer(int size) {
        floats = new float[size];
    }

    public void clear() {
        floatCount = 0;
    }

    public void add(float... values) {
        ensureCapacity(values.length);
        for (int i = 0; i < values.length; i++) {
            floats[floatCount++] = values[i];
        }
    }

    public float[] toArray() {
        float[] array = new float[floatCount];
        System.arraycopy(floats, 0, array, 0, floatCount);
        return array;
    }

    private void ensureCapacity(int capacity) {
        if (floatCount + capacity > floats.length) {
            float[] tempPoints = new float[(int) (1.5 * floats.length)];
            System.arraycopy(floats, 0, tempPoints, 0, floats.length);
            floats = tempPoints;
            caps++;
            if (floats.length > maxSize) {
                maxSize = floats.length;
            }
            System.out.println("Capacity of FloatContainer enlarged " + caps + " times to maxSize: " + maxSize);
        }
    }

    @Override
    public String toString() {
        String string = "[";
        for (int i = 0; i < floatCount; i++) {
            if (i == 0) {
                string += floats[i];
            } else {
                string += ", " + floats[i];
            }
        }
        string += "]";
        return string;
    }

}
