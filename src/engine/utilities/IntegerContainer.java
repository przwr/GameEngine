package engine.utilities;

import java.util.Arrays;

/**
 * @author przemek
 */
public class IntegerContainer {

    private static final byte INITIAL_POINT_COUNT = 8;
    private static int caps, maxSize;
    private int[] ints;
    private int count;

    public IntegerContainer() {
        ints = new int[INITIAL_POINT_COUNT];
    }

    public IntegerContainer(int count) {
        ints = new int[count];
    }

    public void add(int value) {
        ensureCapacity(1);
        ints[count++] = value;
    }

    public void add(int value, int times) {
        ensureCapacity(times);
        Arrays.fill(ints, count, count + times, value);
        count += times;
    }


    public void add(int... values) {
        ensureCapacity(values.length);
        System.arraycopy(values, 0, ints, count, values.length);
        count += values.length;
    }

    private void ensureCapacity(int capacity) {
        if (count + capacity > ints.length) {
            int[] tempPoints = new int[(int) (1.5 * ints.length)];
            System.arraycopy(ints, 0, tempPoints, 0, ints.length);
            ints = tempPoints;
            caps++;
            if (ints.length > maxSize) {
                maxSize = ints.length;
            }
            System.out.println("Capacity of intContainer enlarged " + caps + " times to maxSize: " + maxSize);
        }
    }

    public int get(int i) {
        return ints[i];
    }

    public void remove(int index) {
        if (index < count && index >= 0) {
            count--;
            int temp = ints[index];
            System.arraycopy(ints, index + 1, ints, index, count - index);
            ints[count] = temp;
        }
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public void clear() {
        count = 0;
    }

    public int size() {
        return count;
    }

    public int[] toArray() {
        int[] array = new int[count];
        System.arraycopy(ints, 0, array, 0, count);
        return array;
    }


    @Override
    public String toString() {
        String string = "[";
        for (int i = 0; i < count; i++) {
            if (i == 0) {
                string += ints[i];
            } else {
                string += ", " + ints[i];
            }
        }
        string += "]";
        return string;
    }

}
