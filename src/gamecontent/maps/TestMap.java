package gamecontent.maps;

import collision.Rectangle;
import engine.utilities.RandomGenerator;
import game.place.Place;
import game.place.map.Map;
import game.place.map.PuzzleObject;
import game.place.map.Tile;
import game.place.map.WarpPoint;
import gamecontent.SpawnPoint;
import gamecontent.environment.Rock;
import gamecontent.mobs.Blazag;
import gamecontent.mobs.Dummy;
import gamecontent.mobs.Plurret;
import gamecontent.mobs.Shen;
import gamecontent.npcs.Melodia;
import gamecontent.npcs.Nutka;
import gamecontent.npcs.Tercja;
import sounds.Sound;

import static collision.OpticProperties.TRANSPARENT;

/**
 * Created by przemek on 21.11.15.
 */
public class TestMap extends Map {

    private Sound envSounds;

    public TestMap(short ID, Place place, int tileSize) {
        super(ID, "Test", place, tileSize);

        PuzzleObject puzzle = new PuzzleObject("demo/testMap", place);
        initializeAreas((puzzle.getWidth() + 20) * tileSize, (puzzle.getHeight() + 20) * tileSize);

        Tile GRASS = new Tile(place.getSpriteSheet("grassland", "backgrounds"), 1, 1);
        Tile[] flowers = new Tile[7];
        for (int i = 0; i < flowers.length; i++) {
            flowers[i] = new Tile(place.getSpriteSheet("grassland", "backgrounds"), i, 10);
        }
        RandomGenerator rand = RandomGenerator.create();

        for (int y = 0; y < height / tileSize; y++) {
            for (int x = 0; x < width / tileSize; x++) {
                if (!rand.chance(5)) {
                    setTile(x, y, GRASS);
                } else {
                    setTile(x, y, flowers[rand.random(flowers.length - 1)]);
                }
            }
        }
        puzzle.addTileChanger("grassland", 10, 1, 4,
                7, 10, 8, 10, 9, 10, 10, 10, 11, 10, 12, 10, 13, 10);
        placePuzzle(0, 0, puzzle);

        WarpPoint warp = new WarpPoint("toCaveLeft", 35 * tileSize, 67 * tileSize, "CaveTest");
        warp.setCollision(Rectangle.create(0, 0, tileSize, 2 * tileSize, TRANSPARENT, warp));
        addObject(warp);
        addObject(new WarpPoint("toTestLeft", 36 * tileSize + tileSize / 2, 68 * tileSize));

        warp = new WarpPoint("toCaveRight", 89 * tileSize, 64 * tileSize, "CaveTest");
        warp.setCollision(Rectangle.create(0, 0, tileSize, 2 * tileSize, TRANSPARENT, warp));
        addObject(warp);
        addObject(new WarpPoint("toTestRight", 90 * tileSize + tileSize / 2, 65 * tileSize));
        generateNavigationMeshes();
    }

    @Override
    public void populate() {
        Shen shen = new Shen(3010, 1226, place, mobID++);
        addObject(shen);

        /*addObject(GrassClump.createRectangle(2650, 1320, 1, 5, 8, 2, 8, 32));
        addObject(GrassClump.createCorner(2750, 1320, 2, 5, 7, 2, 8, 32, 0));
        addObject(GrassClump.createCorner(2850, 1320, 2, 5, 7, 2, 8, 32, 1));
        addObject(GrassClump.createCorner(2950, 1320, 2, 5, 7, 2, 8, 32, 2));
        addObject(GrassClump.createCorner(3050, 1320, 2, 5, 7, 2, 8, 32, 3));*/

        Rock rock;
        addObject(new Dummy(4643, 1793, place, mobID++));
        rock = new Rock(4060, 2976, place, mobID++);
        addObject(rock);
        addObject(new Melodia(4034, 1811, place, mobID++, shen, rock));
        addObject(new Blazag(5473, 3783, place, mobID++));
        addObject(new Blazag(5722, 3797, place, mobID++));
        addObject(new Blazag(5311, 3761, place, mobID++));
        addObject(new Nutka(3663, 3084, place, mobID++));
        //addObject(new Magician(2906, 763, place, mobID++));
        Plurret[] plurrets = new Plurret[]{
                new Plurret(7043, 4034, place, mobID++),
                new Plurret(8618, 3609, place, mobID++),
                new Plurret(8281, 4084, place, mobID++)
        };
        addObject(plurrets[0]);
        addObject(plurrets[1]);
        addObject(plurrets[2]);
        addObject(new Tercja(7104, 3667, place, mobID++, plurrets));

        addObject(SpawnPoint.createInVisible(8780, 3871, 54, 38, "Shen spawn", Shen.class, 7, 3));
    }

    //TYMCZASOWE
    @Override
    public void updateEntitesFromAreasToUpdate() {
        super.updateEntitesFromAreasToUpdate();
        if (envSounds != null) {
            if (place.players[0].getMap() == this) {
                envSounds.resume();
            } else {
                envSounds.fade(1000, false);
            }
        } else {
            envSounds = place.getSounds().getSound("env_forest");
        }
    }
}
