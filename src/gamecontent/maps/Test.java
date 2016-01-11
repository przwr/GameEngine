package gamecontent.maps;

import engine.utilities.RandomGenerator;
import game.place.Place;
import game.place.map.Map;
import game.place.map.PuzzleObject;
import game.place.map.Tile;
import gamecontent.Bush;
import gamecontent.GrassClump;
import gamecontent.Tree;
import gamecontent.mobs.Shen;
import gamecontent.npcs.MyNPC;

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
        PuzzleObject puzzle = new PuzzleObject("start", place);
        puzzle.placePuzzle(32, 32, this);
        puzzle = new PuzzleObject("domekstart", place);
        puzzle.placePuzzle(82, 32, this);
        generateNavigationMeshes();
    }

    @Override
    public void populate() {
        RandomGenerator random = RandomGenerator.create();
        addObject(Tree.create(1580, 1650, 32, 200, 0.8f));
        addObject(Tree.create(1650, 1800, 32, 200, 0.8f));
        addObject(Tree.create(1600, 1950, 32, 200, 0.8f));
        addObject(Tree.create(1690, 2100, 32, 200, 0.8f));
        addObject(Tree.create(1700, 2300, 32, 200, 0.8f));
        addObject(Tree.create(1600, 2190, 32, 200, 0.8f));
        addObject(Tree.create(1590, 2450, 32, 200, 0.8f));
        addObject(Tree.create(1800, 2420, 32, 200, 0.8f));
        addObject(new Bush(1700, 2422, 12, 70, 0.8f));
        addObject(Tree.create(1780, 1710, 32, 200, 0.8f));
        addObject(new Bush(1900, 2400, 12, 70, 0.8f));
        addObject(new Bush(2050, 2430, 14, 80, 0.8f));

        for (int i = 0; i < 29; i++) {
            addObject(Tree.create(1490 + i * 90 + random.next(5), 2610 + random.next(7), 32, 200, 0.8f));
//            addObject(Tree.createBranchless(1490 + i * 90 + random.next(5), 2790 + random.next(7), 32, 200, 0.8f));
//            addObject(Tree.createBranchless(1490 + i * 90 + random.next(5), 2970 + random.next(7), 32, 200, 0.8f));
//            addObject(Tree.createBranchless(1490 + i * 90 + random.next(5), 3150 + random.next(7), 32, 200, 0.8f));
//            addObject(Tree.createBranchless(1490 + i * 90 + random.next(5), 3330 + random.next(7), 32, 200, 0.8f));
//            addObject(Tree.createBranchless(1490 + i * 90 + random.next(5), 3510 + random.next(7), 32, 200, 0.8f));
            addObject(Tree.create(1890 + i * 90 + random.next(5), 1510 + random.next(7), 32, 200, 0.8f));
            addObject(new Bush(1930 + i * 90 + random.next(4), 1600 + random.next(6), 14, 80, 0.8f));
        }
        for (int i = 0; i < 18; i++) {
            addObject(Tree.create(4530 + i * 90 + random.next(5), 1510 + random.next(7), 32, 200, 0.8f));
            addObject(new Bush(4570 + i * 90 + random.next(4), 1600 + random.next(6), 14, 80, 0.8f));
        }

//        Background Trees

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 14; y++) {
//                addObject(Tree.createBranchless(300 + x * 120 + random.next(5), 1150 + 180 * y + random.next(6), 32, 200, 0.8f));
            }
        }
        for (int x = 0; x < 54; x++) {
            for (int y = 0; y < 5; y++) {
//                addObject(Tree.createBranchless(700 + x * 120 + random.next(5), 700 + 180 * y + random.next(6), 32, 200, 0.8f));
            }
        }

        addObject(new Shen(5584, 2560, place, mobID++));

        addObject(GrassClump.createCorner(2560, 1864, 2, 8, 8, 2, 8, 32, 0));
        addObject(GrassClump.createCorner(2560, 1736, 2, 8, 8, 2, 8, 32, 1));
        addObject(GrassClump.createCorner(2752, 1736, 2, 8, 8, 2, 8, 32, 2));
        addObject(GrassClump.createCorner(2752, 1864, 2, 8, 8, 2, 8, 32, 3));
        addObject(GrassClump.createRectangle(2560, 1800, 2, 8, 8, 2, 8, 32));
        addObject(GrassClump.createRectangle(2656, 1864, 2, 8, 8, 2, 8, 32));
        addObject(GrassClump.createRectangle(2656, 1736, 2, 8, 8, 2, 8, 32));
        addObject(GrassClump.createRectangle(2656, 1800, 2, 8, 8, 2, 8, 32));
        addObject(GrassClump.createRectangle(2752, 1800, 2, 8, 8, 2, 8, 32));

        addObject(new Bush(3200, 1872, 12, 70, 0.8f));
        addObject(new Bush(3900, 2072, 12, 70, 0.8f));
        addObject(new Bush(4500, 1972, 12, 70, 0.8f));

        addObject(new MyNPC(5000, 2000, place, mobID++));

    }
}
