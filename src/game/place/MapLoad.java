package game.place;

import engine.BlueArray;

/**
 * Created by przemek on 18.08.15.
 */
public class MapLoad {

    String name;
    BlueArray<Integer> areas = new BlueArray<>(9);

    public MapLoad() {
    }

    public MapLoad(String name, int[] areas) {
        this.name = name;
        this.areas.clear();
        for (int area : areas) {
            this.areas.add(area);
        }
    }


    public void set(String name, Iterable<Integer> areas) {
        this.name = name;
        this.areas.clear();
        for (int area : areas) {
            if (area >= 0) {
                this.areas.add(area);
            }
        }
    }

}
