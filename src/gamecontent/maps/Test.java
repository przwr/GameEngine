package gamecontent.maps;

import game.place.Place;
import game.place.map.Map;
import game.place.map.PuzzleObject;
import game.place.map.Tile;

/**
 * Created by przemek on 21.11.15.
 */
public class Test extends Map {


    public Test(short ID, Place place, int width, int height, int tileSize) {
        super(ID, "Test", place, width, height, tileSize);
        Tile GRASS = new Tile(place.getSpriteSheet("tlo", "backgrounds"), 1, 8);

        for (int y = 0; y < height / tileSize; y++) {
            for (int x = 0; x < width / tileSize; x++) {
                setTile(x, y, GRASS);
            }
        }
        PuzzleObject puzzle = new PuzzleObject("domek2", place);
        puzzle.placePuzzle(10, 10, this);
        generateNavigationMeshes();
    }

    @Override
    public void populate() {
    }
}
