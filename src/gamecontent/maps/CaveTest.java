package gamecontent.maps;

import collision.Rectangle;
import game.place.Place;
import game.place.map.Map;
import game.place.map.PuzzleObject;
import game.place.map.WarpPoint;
import gamecontent.SpawnPoint;
import gamecontent.mobs.Blazag;
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
        long start = System.nanoTime();
        generateNavigationMeshes();
        long end = System.nanoTime();
        System.out.println("Navigation mesh for " + GladeMap.class.getSimpleName() + " generated in " + (((end - start)) / 1000000) + " ms");
    }

    @Override
    public void populate() {
        addObject(SpawnPoint.createInVisible(2176, 5120, 54, 38, "Tongub spawn", Tongub.class, 2, 10));
        addObject(SpawnPoint.createInVisible(2880, 4096, 54, 38, "Tongub spawn", Tongub.class, 2, 10));
        addObject(SpawnPoint.createInVisible(4352, 4864, 54, 38, "Tongub spawn", Tongub.class, 2, 10));
        addObject(SpawnPoint.createInVisible(3072, 5568, 54, 38, "Tongub spawn", Tongub.class, 2, 10));
        addObject(SpawnPoint.createInVisible(4288, 5568, 54, 38, "Tongub spawn", Tongub.class, 2, 10));
        addObject(SpawnPoint.createInVisible(3584, 6272, 54, 38, "Tongub spawn", Tongub.class, 2, 10));
        addObject(SpawnPoint.createInVisible(4544, 7168, 54, 38, "Tongub spawn", Tongub.class, 2, 10));
        addObject(SpawnPoint.createInVisible(5056, 7040, 54, 38, "Tongub spawn", Tongub.class, 2, 10));
        addObject(SpawnPoint.createInVisible(3648, 6912, 54, 38, "Tongub spawn", Tongub.class, 2, 10));
        addObject(SpawnPoint.createInVisible(4096, 8128, 54, 38, "Tongub spawn", Tongub.class, 2, 10));
        addObject(SpawnPoint.createInVisible(5504, 7936, 54, 38, "Tongub spawn", Tongub.class, 2, 10));
        addObject(SpawnPoint.createInVisible(5568, 6336, 54, 38, "Tongub spawn", Tongub.class, 2, 10));
        addObject(SpawnPoint.createInVisible(6818, 2662, 54, 38, "Blazag spawn", Blazag.class, 5, 10));
        addObject(SpawnPoint.createInVisible(7271, 2771, 54, 38, "Blazag spawn", Blazag.class, 5, 10));
        addObject(SpawnPoint.createInVisible(7126, 2445, 54, 38, "Blazag spawn", Blazag.class, 5, 10));
    }
}
