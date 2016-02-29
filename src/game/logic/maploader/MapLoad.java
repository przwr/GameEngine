package game.logic.maploader;

import game.place.map.Map;

import java.util.ArrayList;

/**
 * Created by przemek on 18.08.15.
 */
public class MapLoad {

    String name;
    Map map;
    ArrayList<Integer> areas = new ArrayList<>(9);

    public MapLoad() {
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

    public void set(String name, Iterable<Integer> areas, Map map) {
        this.name = name;
        this.map = map;
        this.areas.clear();
        for (int area : areas) {
            if (area >= 0) {
                this.areas.add(area);
            }
        }
    }

    public void clear() {
        map = null;
        name = null;
        areas.clear();
    }

}
