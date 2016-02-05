package gamecontent.maps;

import collision.Rectangle;
import game.place.Place;
import game.place.map.Map;
import game.place.map.PuzzleObject;
import game.place.map.WarpPoint;
import gamecontent.SpawnPoint;
import gamecontent.mobs.Tongub;

import static collision.OpticProperties.IN_SHADE_NO_SHADOW;

/**
 * Created by przemek on 21.11.15.
 */
public class CaveTest extends Map {


    public CaveTest(short ID, Place place, int width, int height, int tileSize) {
        super(ID, "CaveTest", place, width, height, tileSize);
        PuzzleObject puzzle = new PuzzleObject("demo/cave", place);
        puzzle.placePuzzle(0, 0, this);

        WarpPoint warp = new WarpPoint("toTestLeft", 27 * tileSize, 17 * tileSize, "Test");
        warp.setCollision(Rectangle.create(0, 0, tileSize, 2 * tileSize, IN_SHADE_NO_SHADOW, warp));
        addObject(warp);
        addObject(new WarpPoint("toCaveLeft", 26 * tileSize + tileSize / 2, 18 * tileSize + tileSize / 2));
        generateNavigationMeshes();
    }

    @Override
    public void populate() {
        addObject(new Tongub(600, 2560, place, mobID++));
        addObject(new Tongub(640, 2720, place, mobID++));
        addObject(new Tongub(900, 2600, place, mobID++));
        addObject(SpawnPoint.createInVisible(768, 2600, 54, 38, "Tongub spawn", Tongub.class, 10, 10));
        addObject(SpawnPoint.createInVisible(1400, 3440, 54, 38, "Tongub spawn", Tongub.class, 10, 10));

    }
}
