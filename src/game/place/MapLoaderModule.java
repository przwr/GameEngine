package game.place;

import engine.ErrorHandler;
import game.Game;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by przemek on 18.08.15.
 */
public class MapLoaderModule implements Runnable {

    private ArrayList<Map> maps = new ArrayList<>(); // TODO zamienić na wczytywanie z pliku

    private MapLoadContainer list1 = new MapLoadContainer();
    private MapLoadContainer list2 = new MapLoadContainer();

    private ArrayList<Map> toUnload1 = new ArrayList<>();
    private ArrayList<Map> toUnload2 = new ArrayList<>();

    private boolean run, firstActive, pause, firstUnloadActive;
    private Game game;

    public MapLoaderModule(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        maps.addAll(game.getPlace().maps.stream().collect(Collectors.toList()));
        run = true;
        while (run) {
            try {
                loadMapAreas();
            } catch (Exception exception) {
                ErrorHandler.swallowLogAndPrint(exception);
            }
        }
    }

    private void loadMapAreas() {
        if (this.game != null) {
            Place place = game.getPlace();
            if (place != null && !pause) {
                MapLoadContainer workingList = firstActive ? list1 : list2;
                for (Map loadedMap : maps) {
                    if (workingList != null && workingList.containsMap(loadedMap.name)) {
                        if (place.maps.contains(loadedMap)) {
                            // todo
                        } else {
                            (place.firstMapsToAddActive ? place.mapsToAdd2 : place.mapsToAdd1).add(loadedMap);
                        }
                    }
                }
                firstActive = !firstActive;
            }
        }
    }

    public synchronized void requestMap(String name, WarpPoint warp) {
        pause = true;
        Iterable<Integer> areas = new ArrayList<>(1);
        MapLoadContainer workingList = firstActive ? list2 : list1;
        workingList.add(name, areas);
        pause = false;
    }

    public synchronized void updateList(Set<Map> tempMaps) {
        pause = true;
        MapLoadContainer workingList = firstActive ? list2 : list1;
        workingList.clear();
        for (Map map : tempMaps) {
            workingList.add(map.name, map.areasToUpdate);
        }
        pause = false;
    }


    public void stop() {
        list1.clear();
        list2.clear();
        maps.clear();
        run = false;
    }

    public boolean isRunning() {
        return run;
    }
}
