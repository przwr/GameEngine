package gamecontent.maps;

import collision.Rectangle;
import game.place.Place;
import game.place.map.Map;
import game.place.map.PuzzleObject;
import game.place.map.WarpPoint;

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
//        generateNavigationMeshes();
    }

    @Override
    public void populate() {
    }
}
