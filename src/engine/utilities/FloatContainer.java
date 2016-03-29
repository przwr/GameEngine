package engine.utilities;

/**
 * @author przemek
 */
public class FloatContainer {

    private static final byte INITIAL_POINT_COUNT = 8;
    private static int caps, maxSize;
    private float[] floats;
    private int pointCount;

    public FloatContainer() {
        floats = new float[INITIAL_POINT_COUNT];
    }

    public FloatContainer(int pointCount) {
        floats = new float[pointCount];
    }

    public void add(float value) {
        ensureCapacity(1);
        floats[pointCount++] = value;
    }


    public void add(float... values) {
        ensureCapacity(values.length);
        for (int i = 0; i < values.length; i++) {
            floats[pointCount++] = values[i];
        }
    }

    private void ensureCapacity(int capacity) {
        if (pointCount + capacity > floats.length) {
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

    public float get(int i) {
        return floats[i];
    }

    public void remove(int index) {
        if (index < pointCount && index >= 0) {
            pointCount--;
            float temp = floats[index];
            System.arraycopy(floats, index + 1, floats, index, pointCount - index);
            floats[pointCount] = temp;
        }
    }

    public boolean isEmpty() {
        return pointCount == 0;
    }

    public void clear() {
        pointCount = 0;
    }

    public int size() {
        return pointCount;
    }

    public float[] toArray() {
        float[] array = new float[pointCount];
        System.arraycopy(floats, 0, array, 0, pointCount);
        return array;
    }


    @Override
    public String toString() {
        String string = "[";
        for (int i = 0; i < pointCount; i++) {
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
