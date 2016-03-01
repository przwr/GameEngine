package game.logic.maploader;

import engine.utilities.ErrorHandler;
import game.Game;
import game.place.Place;
import game.place.map.Map;
import game.place.map.WarpPoint;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by przemek on 18.08.15.
 */
public class MapLoaderModule implements Runnable {

    private ArrayList<Map> maps = new ArrayList<>(); // TODO zamienić na wczytywanie z pliku
    private MapLoadContainer list1 = new MapLoadContainer(10);
    private MapLoadContainer list2 = new MapLoadContainer(10);
    private boolean run, firstActive, pause;
    private Game game;

    public MapLoaderModule(Game game) {
        this.game = game;
    }

    private Map loadMap(String name) {
        for (Map map : maps) {
            if (map.getName() == name) {
                return map;
            }
        }
        return null;
    }

    @Override
    public void run() {
        maps.addAll(game.getPlace().maps.stream().collect(Collectors.toList()));
        run = true;
        while (run) {
            try {
                loadMaps();
            } catch (Exception exception) {
                ErrorHandler.swallowLogAndPrint(exception);
            }
        }
    }

    private void loadMaps() {
        if (this.game != null) {
            Place place = game.getPlace();
            if (place != null && !pause) {
                MapLoadContainer workingList = firstActive ? list1 : list2;
                if (!workingList.isEmpty()) {
                    for (int i = 0; i < workingList.size(); i++) {
                        MapLoad mapLoad = workingList.get(i);
                        Map placeMap = mapLoad.getMap();
                        if (placeMap == null) {
                            placeMap = loadMap(mapLoad.getName());   //TODO Informacja które areas wczytać z pliku!
                            loadAreas(mapLoad, placeMap);
                            ArrayList<Map> workingMap = (place.firstMapsToAddActive ? place.mapsToAdd2 : place.mapsToAdd1);
                            workingMap.add(placeMap);
                        } else {
                            loadAreas(mapLoad, placeMap);
                        }
                    }
                    workingList.clear();
                } else if (list1.isEmpty() && list2.isEmpty()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                }
                firstActive = !firstActive;
            }
        }
    }

    private void loadAreas(MapLoad mapLoad, Map placeMap) {
        for (int area : mapLoad.getAreas()) {            // TODO przerobić na wczytywanie z pliku
            if (placeMap != null && placeMap.areas[area] == null) {
                placeMap.areas[area] = placeMap.areasCopies[area];
            }
        }
    }

    public synchronized void requestMap(String name, WarpPoint warp) {
        pause = true;
        Iterable<Integer> areas = null;
        MapLoadContainer workingList = firstActive ? list2 : list1;
        if (!workingList.contains(name, areas)) {
            workingList.add(name, new ArrayList<>(1));
        }
        pause = false;
    }

    public synchronized void updateList(Set<Map> tempMaps) {
        pause = true;
        MapLoadContainer workingList = firstActive ? list2 : list1;
        for (Map map : tempMaps) {
            Iterable<Integer> areas = map.getAreasToUpdate();
            if (!workingList.contains(map.getName(), areas)) {
                workingList.add(map.getName(), areas, map);
            }
        }
        pause = false;
    }

    public void stop() {
        list1.clearReally();
        list2.clearReally();
        maps.clear();
        run = false;
    }

    public boolean isRunning() {
        return run;
    }
}
