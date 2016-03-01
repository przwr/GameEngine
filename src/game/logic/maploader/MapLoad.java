package game.logic.maploader;

import game.place.map.Map;

import java.util.ArrayList;

/**
 * Created by przemek on 18.08.15.
 */
public class MapLoad {

    private String name;
    private Map map;
    private ArrayList<Integer> areas = new ArrayList<>(9);

    public MapLoad() {
    }

    public void set(String name, Iterable<Integer> areas) {
        this.name = name;
        this.getAreas().clear();
        for (int area : areas) {
            if (area >= 0) {
                this.getAreas().add(area);
            }
        }
    }

    public void set(String name, Iterable<Integer> areas, Map map) {
        this.name = name;
        this.map = map;
        this.getAreas().clear();
        for (int area : areas) {
            if (area >= 0) {
                this.getAreas().add(area);
            }
        }
    }

    public void clear() {
        map = null;
        name = null;
        getAreas().clear();
    }
    
    public String getName() {
        return name;
    }
    
    public Map getMap() {
        return map;
    }

    public ArrayList<Integer> getAreas() {
        return areas;
    }

}
