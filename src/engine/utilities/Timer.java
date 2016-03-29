package engine.utilities;

/**
 * Created by przemek on 24.03.16.
 */
public class Timer {

    long sum = 0, start;
    int count = 0;
    float sample = 200;
    String name;

    public Timer(String name, int sample) {
        this.name = name;
        this.sample = sample;
    }

    public void start() {
        start = System.nanoTime();
    }

    public void stop() {
        long end = System.nanoTime();
        sum += (end - start);
        count++;
        if (count == sample) {
            System.out.println(name + " Time: " + (sum / (sample * 1000f)) + " µs");
            count = 0;
            sum = 0;
        }
    }
}
